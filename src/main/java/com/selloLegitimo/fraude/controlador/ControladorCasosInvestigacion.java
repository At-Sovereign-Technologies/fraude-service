package com.selloLegitimo.fraude.controlador;

import com.selloLegitimo.fraude.dto.CasoInvestigacionResponse;
import com.selloLegitimo.fraude.dto.CrearCasoRequest;
import com.selloLegitimo.fraude.dto.RegistroAuditoriaResponse;
import com.selloLegitimo.fraude.dto.TransicionarEstadoRequest;
import com.selloLegitimo.fraude.modelo.CasoInvestigacion;
import com.selloLegitimo.fraude.modelo.EntidadCompetente;
import com.selloLegitimo.fraude.modelo.EstadoCaso;
import com.selloLegitimo.fraude.modelo.NivelPrioridad;
import com.selloLegitimo.fraude.modelo.RegistroAuditoria;
import com.selloLegitimo.fraude.modelo.TipologiaFraude;
import com.selloLegitimo.fraude.servicio.ContextoActor;
import com.selloLegitimo.fraude.servicio.ServicioCasosInvestigacion;
import com.selloLegitimo.fraude.servicio.ServicioCasosInvestigacion.ResultadoCreacion;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * API REST de Casos de Investigacion Formal (SR-M6).
 *
 * <p>Autenticacion: cada llamada presenta los headers {@code X-Actor-Id} y
 * {@code X-Actor-Rol}. Solo los cuatro roles de Actores de Control listados
 * en {@link com.selloLegitimo.fraude.modelo.RolActorControl} pueden operar el
 * modulo; otros valores retornan HTTP 403.</p>
 */
@RestController
@RequestMapping("/api/v1/fraude/casos")
public class ControladorCasosInvestigacion {

	private final ServicioCasosInvestigacion servicio;
	private final RepositorioCasoAdapter adapter;

	public ControladorCasosInvestigacion(ServicioCasosInvestigacion servicio,
			com.selloLegitimo.fraude.repositorio.RepositorioCasoInvestigacion repositorioCasos) {
		this.servicio = servicio;
		this.adapter = new RepositorioCasoAdapter(repositorioCasos);
	}

	/**
	 * Crea un caso (201) o devuelve el caso idempotente existente (200).
	 */
	@PostMapping
	public ResponseEntity<CasoInvestigacionResponse> crear(
			@Valid @RequestBody CrearCasoRequest request,
			@RequestHeader(value = "X-Actor-Id", required = false) String actorId,
			@RequestHeader(value = "X-Actor-Rol", required = false) String rolCrudo) {
		ContextoActor actor = ContextoActor.desdeHeaders(actorId, rolCrudo);
		ResultadoCreacion resultado = servicio.crearCaso(request, actor);
		HttpStatus status = resultado.creado() ? HttpStatus.CREATED : HttpStatus.OK;
		return ResponseEntity.status(status).body(aRespuesta(resultado.caso(), null));
	}

	/**
	 * Transiciona el estado del caso (200) si la transicion es legal.
	 */
	@PatchMapping("/{radicado}/estado")
	public ResponseEntity<CasoInvestigacionResponse> transicionar(
			@PathVariable UUID radicado,
			@Valid @RequestBody TransicionarEstadoRequest request,
			@RequestHeader(value = "X-Actor-Id", required = false) String actorId,
			@RequestHeader(value = "X-Actor-Rol", required = false) String rolCrudo) {
		ContextoActor actor = ContextoActor.desdeHeaders(actorId, rolCrudo);
		CasoInvestigacion caso = servicio.transicionarEstado(
			radicado, request.getNuevoEstado(), request.getMotivo(), actor);
		return ResponseEntity.ok(aRespuesta(caso, null));
	}

	/**
	 * Detalle de un caso, incluyendo su historial de auditoria.
	 */
	@GetMapping("/{radicado}")
	public ResponseEntity<CasoInvestigacionResponse> obtener(
			@PathVariable UUID radicado,
			@RequestHeader(value = "X-Actor-Id", required = false) String actorId,
			@RequestHeader(value = "X-Actor-Rol", required = false) String rolCrudo) {
		ContextoActor.desdeHeaders(actorId, rolCrudo);
		CasoInvestigacion caso = servicio.obtenerCaso(radicado);
		List<RegistroAuditoria> historial = servicio.obtenerAuditoria(radicado);
		return ResponseEntity.ok(aRespuesta(caso, historial));
	}

	/**
	 * Listado paginado con filtros opcionales.
	 */
	@GetMapping
	public Page<CasoInvestigacionResponse> listar(
			@RequestParam(required = false) EstadoCaso estado,
			@RequestParam(required = false) NivelPrioridad nivelPrioridad,
			@RequestParam(required = false) TipologiaFraude tipologiaFraude,
			@RequestParam(required = false) EntidadCompetente entidadCompetente,
			@RequestParam(required = false) String responsableInstitucional,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestHeader(value = "X-Actor-Id", required = false) String actorId,
			@RequestHeader(value = "X-Actor-Rol", required = false) String rolCrudo) {
		ContextoActor.desdeHeaders(actorId, rolCrudo);
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "creadoEn"));
		return adapter.buscar(estado, nivelPrioridad, tipologiaFraude,
				entidadCompetente, responsableInstitucional, pageable)
			.map(c -> aRespuesta(c, null));
	}

	/**
	 * Historial completo de auditoria del caso, ordenado cronologicamente.
	 */
	@GetMapping("/{radicado}/auditoria")
	public ResponseEntity<List<RegistroAuditoriaResponse>> auditoria(
			@PathVariable UUID radicado,
			@RequestHeader(value = "X-Actor-Id", required = false) String actorId,
			@RequestHeader(value = "X-Actor-Rol", required = false) String rolCrudo) {
		ContextoActor.desdeHeaders(actorId, rolCrudo);
		List<RegistroAuditoria> registros = servicio.obtenerAuditoria(radicado);
		return ResponseEntity.ok(registros.stream().map(this::aRespuestaAuditoria).toList());
	}

	// ------------------------------------------------------------------
	// Mapeo entidad -> DTO
	// ------------------------------------------------------------------

	private CasoInvestigacionResponse aRespuesta(CasoInvestigacion caso,
			List<RegistroAuditoria> auditoria) {
		CasoInvestigacionResponse r = new CasoInvestigacionResponse();
		r.setRadicado(caso.getRadicado());
		r.setAlertasOrigen(List.copyOf(caso.getAlertasOrigen()));
		r.setTipologiaFraude(caso.getTipologiaFraude());
		r.setNivelPrioridad(caso.getNivelPrioridad());
		r.setEstado(caso.getEstado());
		r.setResponsableInstitucional(caso.getResponsableInstitucional());
		r.setEntidadCompetente(caso.getEntidadCompetente());
		r.setCreadoEn(caso.getCreadoEn());
		r.setCreadoPor(caso.getCreadoPor());
		r.setCerradoEn(caso.getCerradoEn());
		r.setCasoPrecedente(caso.getCasoPrecedente());
		if (auditoria != null) {
			r.setAuditoria(auditoria.stream().map(this::aRespuestaAuditoria).toList());
		}
		return r;
	}

	private RegistroAuditoriaResponse aRespuestaAuditoria(RegistroAuditoria reg) {
		RegistroAuditoriaResponse r = new RegistroAuditoriaResponse();
		r.setId(reg.getId());
		r.setRadicadoCaso(reg.getRadicadoCaso());
		r.setActorId(reg.getActorId());
		r.setRolActor(reg.getRolActor());
		r.setAccion(reg.getAccion());
		r.setEstadoAnterior(reg.getEstadoAnterior());
		r.setEstadoNuevo(reg.getEstadoNuevo());
		r.setTimestamp(reg.getTimestamp());
		r.setMetadatos(reg.getMetadatos());
		return r;
	}

	/**
	 * Adaptador delgado que aisla la query con filtros opcionales del controlador.
	 */
	private record RepositorioCasoAdapter(
			com.selloLegitimo.fraude.repositorio.RepositorioCasoInvestigacion repo) {

		Page<CasoInvestigacion> buscar(EstadoCaso estado, NivelPrioridad prioridad,
				TipologiaFraude tipologia, EntidadCompetente entidad,
				String responsable, Pageable pageable) {
			return repo.buscarConFiltros(estado, prioridad, tipologia, entidad, responsable, pageable);
		}
	}
}
