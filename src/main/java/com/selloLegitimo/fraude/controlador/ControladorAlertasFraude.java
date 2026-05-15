package com.selloLegitimo.fraude.controlador;

import com.selloLegitimo.fraude.dto.ActualizarEstadoRequest;
import com.selloLegitimo.fraude.dto.AlertaFraudeResponse;
import com.selloLegitimo.fraude.dto.TransitionAuditResponse;
import com.selloLegitimo.fraude.excepcion.ExcepcionEstadoInmutable;
import com.selloLegitimo.fraude.excepcion.ExcepcionFraude;
import com.selloLegitimo.fraude.excepcion.ExcepcionRecursoNoEncontrado;
import com.selloLegitimo.fraude.modelo.AlertaFraude;
import com.selloLegitimo.fraude.modelo.EstadoAlerta;
import com.selloLegitimo.fraude.modelo.RegistroAuditoriaCaso;
import com.selloLegitimo.fraude.repositorio.RepositorioAlertaFraude;
import com.selloLegitimo.fraude.repositorio.RepositorioAuditoria;
import com.selloLegitimo.fraude.servicio.ServicioAuditoriaMock;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fraude/alertas")
public class ControladorAlertasFraude {

	private static final Logger logger = LoggerFactory.getLogger(ControladorAlertasFraude.class);

	private static final Map<EstadoAlerta, Set<EstadoAlerta>> TRANSICIONES_VALIDAS = Map.of(
		EstadoAlerta.DETECTADO, Set.of(EstadoAlerta.EN_EVALUACION),
		EstadoAlerta.EN_EVALUACION, Set.of(EstadoAlerta.EN_INVESTIGACION, EstadoAlerta.DESCARTADO),
		EstadoAlerta.EN_INVESTIGACION, Set.of(EstadoAlerta.ESCALADO, EstadoAlerta.CONFIRMADO, EstadoAlerta.DESCARTADO),
		EstadoAlerta.ESCALADO, Set.of(EstadoAlerta.CONFIRMADO, EstadoAlerta.DESCARTADO),
		EstadoAlerta.CONFIRMADO, Set.of(EstadoAlerta.CERRADO),
		EstadoAlerta.DESCARTADO, Set.of(EstadoAlerta.CERRADO),
		EstadoAlerta.CERRADO, Set.of()
	);

	private final RepositorioAlertaFraude repositorio;
	private final ServicioAuditoriaMock servicioAuditoria;
	private final RepositorioAuditoria repositorioAuditoria;

	public ControladorAlertasFraude(RepositorioAlertaFraude repositorio,
		ServicioAuditoriaMock servicioAuditoria,
		RepositorioAuditoria repositorioAuditoria) {
		this.repositorio = repositorio;
		this.servicioAuditoria = servicioAuditoria;
		this.repositorioAuditoria = repositorioAuditoria;
	}

	@GetMapping
	public Page<AlertaFraudeResponse> listar(
			@RequestParam(required = false) String status,
			@RequestParam(required = false) String severity,
			@RequestParam(required = false) String typologyId,
			@RequestParam(required = false) String originModule,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<AlertaFraude> alertas;
		if (status != null) {
			alertas = repositorio.findByStatus(EstadoAlerta.valueOf(status.toUpperCase()), pageable);
		} else if (severity != null) {
			alertas = repositorio.findBySeverityLevel(severity.toUpperCase(), pageable);
		} else if (typologyId != null) {
			alertas = repositorio.findByTypologyId(typologyId, pageable);
		} else if (originModule != null) {
			alertas = repositorio.findByOriginModule(originModule, pageable);
		} else {
			alertas = repositorio.findAll(pageable);
		}
		return alertas.map(this::convertirARespuesta);
	}

	@GetMapping("/{alertUuid}")
	public ResponseEntity<AlertaFraudeResponse> obtener(@PathVariable UUID alertUuid) {
		AlertaFraude alerta = repositorio.findByAlertUuid(alertUuid)
			.orElseThrow(() -> new ExcepcionRecursoNoEncontrado(
				"No existe alerta con uuid " + alertUuid));
		return ResponseEntity.ok(convertirARespuesta(alerta));
	}

	@PatchMapping("/{alertUuid}/status")
	public ResponseEntity<AlertaFraudeResponse> actualizarEstado(
			@PathVariable UUID alertUuid,
			@Valid @RequestBody ActualizarEstadoRequest request) {
		AlertaFraude alerta = repositorio.findByAlertUuid(alertUuid)
			.orElseThrow(() -> new ExcepcionRecursoNoEncontrado(
				"No existe alerta con uuid " + alertUuid));

		if (alerta.getStatus() == EstadoAlerta.CERRADO) {
			throw new ExcepcionEstadoInmutable(
				"La alerta " + alertUuid + " esta en estado CERRADO y no permite modificaciones");
		}

		EstadoAlerta nuevoEstado = request.getStatus();
		Set<EstadoAlerta> permitidas = TRANSICIONES_VALIDAS.get(alerta.getStatus());
		if (permitidas == null || !permitidas.contains(nuevoEstado)) {
			throw new ExcepcionFraude(
				"Transicion invalida: " + alerta.getStatus() + " -> " + nuevoEstado);
		}

		String actorId = request.getActorId() != null ? request.getActorId() : "unknown";

		EstadoAlerta estadoAnterior = alerta.getStatus();
		alerta.setStatus(nuevoEstado);
		alerta.setLastActorId(actorId);
		alerta.setLastTransitionAt(LocalDateTime.now());

		if (request.getAssignedTo() != null) {
			alerta.setAssignedTo(request.getAssignedTo());
		}

		if (nuevoEstado == EstadoAlerta.CONFIRMADO || nuevoEstado == EstadoAlerta.DESCARTADO) {
			alerta.setResolvedAt(LocalDateTime.now());
			alerta.setResolvedBy(actorId);
			alerta.setResolutionNotes(request.getResolutionNotes());
		}

		if (nuevoEstado == EstadoAlerta.CERRADO) {
			alerta.setClosedAt(LocalDateTime.now());
			alerta.setClosedBy(actorId);
		}

		AlertaFraude guardada = repositorio.save(alerta);

		TransitionAuditResponse audit = servicioAuditoria.registrarTransicion(
			alertUuid, estadoAnterior, nuevoEstado, actorId);

		RegistroAuditoriaCaso registro = new RegistroAuditoriaCaso();
		registro.setAlertUuid(alertUuid);
		registro.setActorId(actorId);
		registro.setFromStatus(estadoAnterior != null ? estadoAnterior.name() : null);
		registro.setToStatus(nuevoEstado.name());
		registro.setTransitionTimestamp(LocalDateTime.now());
		repositorioAuditoria.save(registro);

		logger.info("Alerta {} transicion: {} -> {} por {} (auditId={})",
			alertUuid, estadoAnterior, nuevoEstado, actorId, audit.getMockAuditLogId());

		return ResponseEntity.ok(convertirARespuesta(guardada));
	}

	private AlertaFraudeResponse convertirARespuesta(AlertaFraude alerta) {
		AlertaFraudeResponse response = new AlertaFraudeResponse();
		response.setAlertUuid(alerta.getAlertUuid().toString());
		response.setTypologyId(alerta.getTypologyId());
		response.setSeverityLevel(alerta.getSeverityLevel());
		response.setRiskScore(alerta.getRiskScore());
		response.setRiskScoreSource(alerta.getRiskScoreSource());
		response.setStatus(alerta.getStatus() != null ? alerta.getStatus().name() : null);
		response.setCreatedAt(alerta.getCreatedAt());
		response.setClosedAt(alerta.getClosedAt());
		response.setClosedBy(alerta.getClosedBy());
		response.setLastActorId(alerta.getLastActorId());
		response.setLastTransitionAt(alerta.getLastTransitionAt());

		AlertaFraudeResponse.SourceReference ref = new AlertaFraudeResponse.SourceReference();
		ref.setOriginEventId(alerta.getOriginEventId());
		ref.setVerificationHash(alerta.getVerificationHash());
		ref.setCertifiedTimestamp(alerta.getCertifiedTimestamp());
		ref.setOriginModule(alerta.getOriginModule());
		response.setSourceReference(ref);

		AlertaFraudeResponse.LogicalLocation loc = new AlertaFraudeResponse.LogicalLocation();
		loc.setTableId(alerta.getTableId());
		loc.setPollingStation(alerta.getPollingStation());
		loc.setConstituency(alerta.getConstituency());
		loc.setChannel(alerta.getChannel());
		response.setLogicalLocation(loc);

		return response;
	}
}
