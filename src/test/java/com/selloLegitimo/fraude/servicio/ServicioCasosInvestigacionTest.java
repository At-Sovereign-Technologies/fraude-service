package com.selloLegitimo.fraude.servicio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.selloLegitimo.fraude.dto.CrearCasoRequest;
import com.selloLegitimo.fraude.excepcion.ExcepcionCasoCerrado;
import com.selloLegitimo.fraude.excepcion.ExcepcionFraude;
import com.selloLegitimo.fraude.excepcion.ExcepcionRecursoNoEncontrado;
import com.selloLegitimo.fraude.excepcion.ExcepcionTransicionInvalida;
import com.selloLegitimo.fraude.modelo.CasoInvestigacion;
import com.selloLegitimo.fraude.modelo.EntidadCompetente;
import com.selloLegitimo.fraude.modelo.EstadoCaso;
import com.selloLegitimo.fraude.modelo.NivelPrioridad;
import com.selloLegitimo.fraude.modelo.RegistroAuditoria;
import com.selloLegitimo.fraude.modelo.RolActorControl;
import com.selloLegitimo.fraude.modelo.TipologiaFraude;
import com.selloLegitimo.fraude.repositorio.RepositorioCasoInvestigacion;
import com.selloLegitimo.fraude.repositorio.RepositorioRegistroAuditoria;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ServicioCasosInvestigacionTest {

	@Mock RepositorioCasoInvestigacion repositorioCasos;
	@Mock RepositorioRegistroAuditoria repositorioAuditoria;
	@InjectMocks ServicioCasosInvestigacion servicio;

	private ContextoActor actor;

	@BeforeEach
	void setUp() {
		actor = new ContextoActor("actor-123", RolActorControl.DELEGADO_FISCALIA);
		// Por defecto, el guardado retorna la misma entidad pasada (con prePersist).
		when(repositorioCasos.save(any(CasoInvestigacion.class)))
			.thenAnswer(inv -> {
				CasoInvestigacion c = inv.getArgument(0);
				if (c.getRadicado() == null) {
					c.setRadicado(UUID.randomUUID());
				}
				if (c.getCreadoEn() == null) {
					c.setCreadoEn(LocalDateTime.now(ZoneOffset.UTC));
				}
				return c;
			});
		when(repositorioCasos.buscarRadicadosPorConjuntoExacto(anyList(), anyLong()))
			.thenReturn(List.of());
	}

	// ------------------------------------------------------------------
	// Maquina de estados — transiciones validas e invalidas
	// ------------------------------------------------------------------

	@Test
	@DisplayName("Transicion DETECTADO -> EN_EVALUACION actualiza estado y persiste auditoria")
	void transicionValidaPersisteEstadoYAuditoria() {
		CasoInvestigacion caso = casoEnEstado(EstadoCaso.DETECTADO);
		when(repositorioCasos.findByRadicado(caso.getRadicado())).thenReturn(Optional.of(caso));

		CasoInvestigacion res = servicio.transicionarEstado(
			caso.getRadicado(), EstadoCaso.EN_EVALUACION, "motivo", actor);

		assertThat(res.getEstado()).isEqualTo(EstadoCaso.EN_EVALUACION);
		ArgumentCaptor<RegistroAuditoria> capt = ArgumentCaptor.forClass(RegistroAuditoria.class);
		verify(repositorioAuditoria).save(capt.capture());
		RegistroAuditoria reg = capt.getValue();
		assertThat(reg.getEstadoAnterior()).isEqualTo(EstadoCaso.DETECTADO);
		assertThat(reg.getEstadoNuevo()).isEqualTo(EstadoCaso.EN_EVALUACION);
		assertThat(reg.getActorId()).isEqualTo("actor-123");
		assertThat(reg.getRolActor()).isEqualTo(RolActorControl.DELEGADO_FISCALIA);
		assertThat(reg.getAccion()).contains("DETECTADO", "EN_EVALUACION");
	}

	@Test
	@DisplayName("Saltar etapas dispara ExcepcionTransicionInvalida (400)")
	void saltarEtapasEsRechazado() {
		CasoInvestigacion caso = casoEnEstado(EstadoCaso.DETECTADO);
		when(repositorioCasos.findByRadicado(caso.getRadicado())).thenReturn(Optional.of(caso));

		assertThatThrownBy(() -> servicio.transicionarEstado(
				caso.getRadicado(), EstadoCaso.ESCALADO, null, actor))
			.isInstanceOf(ExcepcionTransicionInvalida.class)
			.hasMessageContaining("DETECTADO")
			.hasMessageContaining("ESCALADO");

		verify(repositorioAuditoria, never()).save(any());
	}

	@Test
	@DisplayName("Retroceder estados es ilegal")
	void retrocederEstadosEsRechazado() {
		CasoInvestigacion caso = casoEnEstado(EstadoCaso.EN_INVESTIGACION);
		when(repositorioCasos.findByRadicado(caso.getRadicado())).thenReturn(Optional.of(caso));

		assertThatThrownBy(() -> servicio.transicionarEstado(
				caso.getRadicado(), EstadoCaso.EN_EVALUACION, null, actor))
			.isInstanceOf(ExcepcionTransicionInvalida.class);
	}

	@Test
	@DisplayName("ESCALADO se bifurca a CONFIRMADO o DESCARTADO")
	void escaladoBifurcaACONFIRMADOoDESCARTADO() {
		CasoInvestigacion caso = casoEnEstado(EstadoCaso.ESCALADO);
		when(repositorioCasos.findByRadicado(caso.getRadicado())).thenReturn(Optional.of(caso));

		CasoInvestigacion res = servicio.transicionarEstado(
			caso.getRadicado(), EstadoCaso.CONFIRMADO, null, actor);
		assertThat(res.getEstado()).isEqualTo(EstadoCaso.CONFIRMADO);
	}

	@Test
	@DisplayName("Transicion a CERRADO escribe cerradoEn en la misma operacion")
	void transicionACerradoEscribeCerradoEn() {
		CasoInvestigacion caso = casoEnEstado(EstadoCaso.CONFIRMADO);
		when(repositorioCasos.findByRadicado(caso.getRadicado())).thenReturn(Optional.of(caso));

		CasoInvestigacion res = servicio.transicionarEstado(
			caso.getRadicado(), EstadoCaso.CERRADO, null, actor);
		assertThat(res.getEstado()).isEqualTo(EstadoCaso.CERRADO);
		assertThat(res.getCerradoEn()).isNotNull();
	}

	// ------------------------------------------------------------------
	// Inmutabilidad tras CERRADO
	// ------------------------------------------------------------------

	@Test
	@DisplayName("Caso CERRADO no admite ninguna transicion (409)")
	void casoCerradoNoAdmiteTransiciones() {
		CasoInvestigacion caso = casoEnEstado(EstadoCaso.CERRADO);
		caso.setCerradoEn(LocalDateTime.now(ZoneOffset.UTC));
		when(repositorioCasos.findByRadicado(caso.getRadicado())).thenReturn(Optional.of(caso));

		for (EstadoCaso destino : EstadoCaso.values()) {
			assertThatThrownBy(() -> servicio.transicionarEstado(
					caso.getRadicado(), destino, null, actor))
				.as("CERRADO -> %s debe ser rechazado", destino)
				.isInstanceOf(ExcepcionCasoCerrado.class)
				.hasMessageContaining("irreversible");
		}

		verify(repositorioCasos, never()).save(any());
		verify(repositorioAuditoria, never()).save(any());
	}

	// ------------------------------------------------------------------
	// Auditoria
	// ------------------------------------------------------------------

	@Test
	@DisplayName("Creacion de caso genera registro de auditoria con estadoAnterior=null")
	void creacionGeneraAuditoria() {
		CrearCasoRequest req = baseRequest();
		ServicioCasosInvestigacion.ResultadoCreacion res = servicio.crearCaso(req, actor);

		assertThat(res.creado()).isTrue();
		assertThat(res.caso().getEstado()).isEqualTo(EstadoCaso.DETECTADO);

		ArgumentCaptor<RegistroAuditoria> capt = ArgumentCaptor.forClass(RegistroAuditoria.class);
		verify(repositorioAuditoria).save(capt.capture());
		RegistroAuditoria reg = capt.getValue();
		assertThat(reg.getEstadoAnterior()).isNull();
		assertThat(reg.getEstadoNuevo()).isEqualTo(EstadoCaso.DETECTADO);
		assertThat(reg.getAccion()).isEqualTo("CREACION_CASO");
	}

	@Test
	@DisplayName("Cada transicion genera exactamente un registro de auditoria")
	void cadaTransicionGeneraUnRegistroDeAuditoria() {
		CasoInvestigacion caso = casoEnEstado(EstadoCaso.DETECTADO);
		when(repositorioCasos.findByRadicado(caso.getRadicado())).thenReturn(Optional.of(caso));

		servicio.transicionarEstado(caso.getRadicado(), EstadoCaso.EN_EVALUACION, null, actor);
		servicio.transicionarEstado(caso.getRadicado(), EstadoCaso.EN_INVESTIGACION, null, actor);
		servicio.transicionarEstado(caso.getRadicado(), EstadoCaso.ESCALADO, null, actor);

		verify(repositorioAuditoria, times(3)).save(any(RegistroAuditoria.class));
	}

	// ------------------------------------------------------------------
	// casoPrecedente
	// ------------------------------------------------------------------

	@Test
	@DisplayName("casoPrecedente debe existir; si no, 404")
	void casoPrecedenteInexistenteDispara404() {
		UUID precedenteId = UUID.randomUUID();
		when(repositorioCasos.findByRadicado(precedenteId)).thenReturn(Optional.empty());

		CrearCasoRequest req = baseRequest();
		req.setCasoPrecedente(precedenteId);

		assertThatThrownBy(() -> servicio.crearCaso(req, actor))
			.isInstanceOf(ExcepcionRecursoNoEncontrado.class)
			.hasMessageContaining(precedenteId.toString());
	}

	@Test
	@DisplayName("casoPrecedente debe estar CERRADO; estado distinto -> 400")
	void casoPrecedenteNoCerradoEsRechazado() {
		CasoInvestigacion precedente = casoEnEstado(EstadoCaso.EN_INVESTIGACION);
		when(repositorioCasos.findByRadicado(precedente.getRadicado()))
			.thenReturn(Optional.of(precedente));

		CrearCasoRequest req = baseRequest();
		req.setCasoPrecedente(precedente.getRadicado());

		assertThatThrownBy(() -> servicio.crearCaso(req, actor))
			.isInstanceOf(ExcepcionFraude.class)
			.hasMessageContaining("CERRADO");
	}

	@Test
	@DisplayName("casoPrecedente CERRADO es aceptado y propagado al caso derivado")
	void casoPrecedenteCerradoEsAceptado() {
		CasoInvestigacion precedente = casoEnEstado(EstadoCaso.CERRADO);
		precedente.setCerradoEn(LocalDateTime.now(ZoneOffset.UTC));
		when(repositorioCasos.findByRadicado(precedente.getRadicado()))
			.thenReturn(Optional.of(precedente));

		CrearCasoRequest req = baseRequest();
		req.setCasoPrecedente(precedente.getRadicado());

		ServicioCasosInvestigacion.ResultadoCreacion res = servicio.crearCaso(req, actor);
		assertThat(res.creado()).isTrue();
		assertThat(res.caso().getCasoPrecedente()).isEqualTo(precedente.getRadicado());
	}

	@Test
	@DisplayName("alertasOrigen vacio dispara ExcepcionFraude")
	void alertasOrigenVacioEsRechazado() {
		CrearCasoRequest req = baseRequest();
		req.setAlertasOrigen(new ArrayList<>());

		assertThatThrownBy(() -> servicio.crearCaso(req, actor))
			.isInstanceOf(ExcepcionFraude.class);
	}

	// ------------------------------------------------------------------
	// Idempotencia
	// ------------------------------------------------------------------

	@Test
	@DisplayName("Mismas alertasOrigen reutilizan el caso existente (creado=false)")
	void idempotenciaReutilizaCasoExistente() {
		CrearCasoRequest req = baseRequest();
		CasoInvestigacion existente = new CasoInvestigacion();
		existente.setRadicado(UUID.randomUUID());
		existente.setAlertasOrigen(new ArrayList<>(req.getAlertasOrigen()));
		existente.setTipologiaFraude(req.getTipologiaFraude());
		existente.setNivelPrioridad(req.getNivelPrioridad());
		existente.setEstado(EstadoCaso.EN_INVESTIGACION);
		existente.setResponsableInstitucional(req.getResponsableInstitucional());
		existente.setEntidadCompetente(req.getEntidadCompetente());
		existente.setCreadoEn(LocalDateTime.now(ZoneOffset.UTC));
		existente.setCreadoPor("alguien");

		when(repositorioCasos.buscarRadicadosPorConjuntoExacto(anyList(), anyLong()))
			.thenReturn(List.of(existente.getRadicado()));
		when(repositorioCasos.findByRadicado(existente.getRadicado()))
			.thenReturn(Optional.of(existente));

		ServicioCasosInvestigacion.ResultadoCreacion res = servicio.crearCaso(req, actor);

		assertThat(res.creado()).isFalse();
		assertThat(res.caso().getRadicado()).isEqualTo(existente.getRadicado());
		verify(repositorioCasos, never()).save(any(CasoInvestigacion.class));
		verify(repositorioAuditoria, never()).save(any(RegistroAuditoria.class));
	}

	// ------------------------------------------------------------------
	// Helpers
	// ------------------------------------------------------------------

	private CasoInvestigacion casoEnEstado(EstadoCaso estado) {
		CasoInvestigacion c = new CasoInvestigacion();
		c.setRadicado(UUID.randomUUID());
		c.setEstado(estado);
		c.setAlertasOrigen(new ArrayList<>(List.of(UUID.randomUUID())));
		c.setTipologiaFraude(TipologiaFraude.SUPLANTACION);
		c.setNivelPrioridad(NivelPrioridad.ALTO);
		c.setResponsableInstitucional("resp-1");
		c.setEntidadCompetente(EntidadCompetente.FISCALIA);
		c.setCreadoEn(LocalDateTime.now(ZoneOffset.UTC));
		c.setCreadoPor("creador-1");
		return c;
	}

	private CrearCasoRequest baseRequest() {
		CrearCasoRequest req = new CrearCasoRequest();
		req.setAlertasOrigen(new ArrayList<>(List.of(UUID.randomUUID(), UUID.randomUUID())));
		req.setTipologiaFraude(TipologiaFraude.SUPLANTACION);
		req.setNivelPrioridad(NivelPrioridad.ALTO);
		req.setResponsableInstitucional("resp-1");
		req.setEntidadCompetente(EntidadCompetente.FISCALIA);
		return req;
	}
}
