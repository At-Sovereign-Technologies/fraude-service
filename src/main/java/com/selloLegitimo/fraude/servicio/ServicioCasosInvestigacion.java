package com.selloLegitimo.fraude.servicio;

import com.selloLegitimo.fraude.dto.CrearCasoRequest;
import com.selloLegitimo.fraude.excepcion.ExcepcionCasoCerrado;
import com.selloLegitimo.fraude.excepcion.ExcepcionFraude;
import com.selloLegitimo.fraude.excepcion.ExcepcionRecursoNoEncontrado;
import com.selloLegitimo.fraude.excepcion.ExcepcionTransicionInvalida;
import com.selloLegitimo.fraude.modelo.CasoInvestigacion;
import com.selloLegitimo.fraude.modelo.EstadoCaso;
import com.selloLegitimo.fraude.modelo.RegistroAuditoria;
import com.selloLegitimo.fraude.repositorio.RepositorioCasoInvestigacion;
import com.selloLegitimo.fraude.repositorio.RepositorioRegistroAuditoria;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de aplicacion para SR-M6.
 *
 * <p>Concentra las invariantes de negocio del modulo: maquina de estados lineal,
 * irreversibilidad del cierre, idempotencia en creacion, validacion de
 * {@code casoPrecedente} y emision atomica de {@link RegistroAuditoria} en cada
 * operacion sensible. Todas las escrituras viven dentro de transacciones
 * declarativas; el {@code RegistroAuditoria} se persiste en la misma transaccion
 * que el cambio que documenta.</p>
 */
@Service
public class ServicioCasosInvestigacion {

	private static final Logger logger = LoggerFactory.getLogger(ServicioCasosInvestigacion.class);

	private final RepositorioCasoInvestigacion repositorioCasos;
	private final RepositorioRegistroAuditoria repositorioAuditoria;

	public ServicioCasosInvestigacion(
			RepositorioCasoInvestigacion repositorioCasos,
			RepositorioRegistroAuditoria repositorioAuditoria) {
		this.repositorioCasos = repositorioCasos;
		this.repositorioAuditoria = repositorioAuditoria;
	}

	/**
	 * Resultado de {@link #crearCaso}: encapsula el caso resultante y si fue
	 * creado o si se reuso uno existente por idempotencia (200 vs 201).
	 */
	public record ResultadoCreacion(CasoInvestigacion caso, boolean creado) { }

	/**
	 * Crea un nuevo caso a partir de una o mas alertas.
	 *
	 * <p>Reglas:
	 * <ul>
	 *   <li>{@code alertasOrigen} no puede estar vacio.</li>
	 *   <li>Si {@code casoPrecedente} no es nulo, debe existir y estar CERRADO.</li>
	 *   <li>Idempotencia: si ya existe un caso con el mismo conjunto exacto de
	 *       alertas y el mismo {@code casoPrecedente}, se devuelve ese caso con
	 *       {@code creado=false}.</li>
	 * </ul>
	 *
	 * <p>Estado inicial: {@link EstadoCaso#DETECTADO}. Se persiste atomicamente
	 * un {@link RegistroAuditoria} con {@code accion="CREACION_CASO"} y
	 * {@code estadoAnterior=null}.</p>
	 *
	 * @throws ExcepcionFraude si la validacion de negocio falla.
	 * @throws ExcepcionRecursoNoEncontrado si {@code casoPrecedente} no existe.
	 */
	@Transactional
	public ResultadoCreacion crearCaso(CrearCasoRequest req, ContextoActor actor) {
		List<UUID> alertas = dedupPreservandoOrden(req.getAlertasOrigen());
		if (alertas.isEmpty()) {
			throw new ExcepcionFraude("alertasOrigen no puede estar vacio");
		}

		if (req.getCasoPrecedente() != null) {
			CasoInvestigacion precedente = repositorioCasos.findByRadicado(req.getCasoPrecedente())
				.orElseThrow(() -> new ExcepcionRecursoNoEncontrado(
					"casoPrecedente no existe: " + req.getCasoPrecedente()));
			if (precedente.getEstado() != EstadoCaso.CERRADO) {
				throw new ExcepcionFraude(
					"casoPrecedente debe estar CERRADO; estado actual: " + precedente.getEstado());
			}
		}

		Optional<CasoInvestigacion> existente = buscarCasoIdempotente(alertas, req.getCasoPrecedente());
		if (existente.isPresent()) {
			logger.info("Idempotencia: caso existente {} reutilizado para alertas {}",
				existente.get().getRadicado(), alertas);
			return new ResultadoCreacion(existente.get(), false);
		}

		CasoInvestigacion caso = new CasoInvestigacion();
		caso.setAlertasOrigen(new ArrayList<>(alertas));
		caso.setTipologiaFraude(req.getTipologiaFraude());
		caso.setNivelPrioridad(req.getNivelPrioridad());
		caso.setEstado(EstadoCaso.DETECTADO);
		caso.setResponsableInstitucional(req.getResponsableInstitucional());
		caso.setEntidadCompetente(req.getEntidadCompetente());
		caso.setCasoPrecedente(req.getCasoPrecedente());
		caso.setCreadoPor(actor.actorId());
		CasoInvestigacion persistido = repositorioCasos.save(caso);

		registrarAuditoria(
			persistido.getRadicado(),
			actor,
			"CREACION_CASO",
			null,
			EstadoCaso.DETECTADO,
			req.getCasoPrecedente() != null
				? "{\"casoPrecedente\":\"" + req.getCasoPrecedente() + "\"}"
				: null);

		logger.info("Caso {} creado por {} (rol {}) con {} alertas",
			persistido.getRadicado(), actor.actorId(), actor.rol(), alertas.size());
		return new ResultadoCreacion(persistido, true);
	}

	/**
	 * Transiciona el estado del caso a {@code nuevoEstado}.
	 *
	 * <p>Garantias:
	 * <ul>
	 *   <li>Si el caso esta CERRADO, lanza {@link ExcepcionCasoCerrado} (409).</li>
	 *   <li>Si la transicion no esta permitida por la maquina de estados, lanza
	 *       {@link ExcepcionTransicionInvalida} (400).</li>
	 *   <li>Al transicionar a CERRADO, escribe {@code cerradoEn} en la misma
	 *       transaccion (lo cual el trigger de BD permite porque
	 *       {@code OLD.estado != 'CERRADO'} en ese momento).</li>
	 *   <li>Persiste {@link RegistroAuditoria} atomicamente.</li>
	 * </ul>
	 *
	 * @throws ExcepcionRecursoNoEncontrado si el radicado no existe.
	 * @throws ExcepcionCasoCerrado si el caso ya esta CERRADO.
	 * @throws ExcepcionTransicionInvalida si la transicion no es legal.
	 */
	@Transactional
	public CasoInvestigacion transicionarEstado(
			UUID radicado, EstadoCaso nuevoEstado, String motivo, ContextoActor actor) {
		CasoInvestigacion caso = repositorioCasos.findByRadicado(radicado)
			.orElseThrow(() -> new ExcepcionRecursoNoEncontrado(
				"No existe caso con radicado " + radicado));

		EstadoCaso actual = caso.getEstado();

		if (actual.esTerminal()) {
			throw new ExcepcionCasoCerrado(radicado,
				"No se puede transicionar de CERRADO a " + nuevoEstado
				+ ". El caso esta cerrado de forma irreversible.");
		}

		if (!actual.puedeTransicionarA(nuevoEstado)) {
			throw new ExcepcionTransicionInvalida(radicado,
				"No se puede transicionar de " + actual + " a " + nuevoEstado
				+ ". Transiciones permitidas: " + actual.transicionesPermitidas());
		}

		caso.setEstado(nuevoEstado);
		if (nuevoEstado == EstadoCaso.CERRADO) {
			caso.setCerradoEn(LocalDateTime.now(ZoneOffset.UTC));
		}
		CasoInvestigacion guardado = repositorioCasos.save(caso);

		registrarAuditoria(
			radicado,
			actor,
			"TRANSICION_ESTADO: " + actual + " -> " + nuevoEstado,
			actual,
			nuevoEstado,
			motivo != null && !motivo.isBlank()
				? "{\"motivo\":" + escaparJson(motivo) + "}"
				: null);

		logger.info("Caso {}: transicion {} -> {} por {} (rol {})",
			radicado, actual, nuevoEstado, actor.actorId(), actor.rol());
		return guardado;
	}

	/**
	 * Recupera un caso por radicado o lanza {@link ExcepcionRecursoNoEncontrado}.
	 */
	@Transactional(readOnly = true)
	public CasoInvestigacion obtenerCaso(UUID radicado) {
		return repositorioCasos.findByRadicado(radicado)
			.orElseThrow(() -> new ExcepcionRecursoNoEncontrado(
				"No existe caso con radicado " + radicado));
	}

	/**
	 * Devuelve el historial completo de auditoria de un caso ordenado por
	 * timestamp ascendente.
	 */
	@Transactional(readOnly = true)
	public List<RegistroAuditoria> obtenerAuditoria(UUID radicado) {
		if (!repositorioCasos.existsById(radicado)) {
			throw new ExcepcionRecursoNoEncontrado(
				"No existe caso con radicado " + radicado);
		}
		return repositorioAuditoria.findByRadicadoCasoOrderByTimestampAsc(radicado);
	}

	// ------------------------------------------------------------------
	// Helpers
	// ------------------------------------------------------------------

	private void registrarAuditoria(
			UUID radicado, ContextoActor actor, String accion,
			EstadoCaso estadoAnterior, EstadoCaso estadoNuevo, String metadatos) {
		RegistroAuditoria reg = new RegistroAuditoria();
		reg.setRadicadoCaso(radicado);
		reg.setActorId(actor.actorId());
		reg.setRolActor(actor.rol());
		reg.setAccion(accion);
		reg.setEstadoAnterior(estadoAnterior);
		reg.setEstadoNuevo(estadoNuevo);
		reg.setMetadatos(metadatos);
		repositorioAuditoria.save(reg);
	}

	private Optional<CasoInvestigacion> buscarCasoIdempotente(List<UUID> alertas, UUID casoPrecedente) {
		List<UUID> candidatos = repositorioCasos.buscarRadicadosPorConjuntoExacto(alertas, alertas.size());
		return candidatos.stream()
			.map(repositorioCasos::findByRadicado)
			.flatMap(Optional::stream)
			.filter(c -> Objects.equals(c.getCasoPrecedente(), casoPrecedente))
			.max(Comparator.comparing(CasoInvestigacion::getCreadoEn));
	}

	/**
	 * Quita duplicados preservando el orden de aparicion. Util porque el cliente
	 * puede repetir UUIDs sin que ello deba multiplicar la huella del caso.
	 */
	private static List<UUID> dedupPreservandoOrden(List<UUID> entrada) {
		if (entrada == null) {
			return List.of();
		}
		return new ArrayList<>(new LinkedHashSet<>(entrada));
	}

	private static String escaparJson(String texto) {
		String escapado = texto
			.replace("\\", "\\\\")
			.replace("\"", "\\\"")
			.replace("\n", "\\n")
			.replace("\r", "\\r")
			.replace("\t", "\\t");
		return "\"" + escapado + "\"";
	}
}
