package com.selloLegitimo.fraude.controlador;

import com.selloLegitimo.fraude.dto.ActualizarEstadoRequest;
import com.selloLegitimo.fraude.dto.AlertaFraudeResponse;
import com.selloLegitimo.fraude.excepcion.ExcepcionFraude;
import com.selloLegitimo.fraude.excepcion.ExcepcionRecursoNoEncontrado;
import com.selloLegitimo.fraude.modelo.AlertaFraude;
import com.selloLegitimo.fraude.repositorio.RepositorioAlertaFraude;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;
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

	private static final Set<String> STATUS_VALIDOS = Set.of(
		"PENDING_REVIEW", "UNDER_INVESTIGATION", "CONFIRMED", "DISMISSED");

	private static final java.util.Map<String, Set<String>> TRANSICIONES_VALIDAS = java.util.Map.of(
		"PENDING_REVIEW", Set.of("UNDER_INVESTIGATION", "DISMISSED"),
		"UNDER_INVESTIGATION", Set.of("CONFIRMED", "DISMISSED"),
		"CONFIRMED", Set.of(),
		"DISMISSED", Set.of()
	);

	private final RepositorioAlertaFraude repositorio;

	public ControladorAlertasFraude(RepositorioAlertaFraude repositorio) {
		this.repositorio = repositorio;
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
			alertas = repositorio.findByStatus(status.toUpperCase(), pageable);
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

		String nuevoEstado = request.getStatus().toUpperCase();
		if (!STATUS_VALIDOS.contains(nuevoEstado)) {
			throw new ExcepcionFraude("Estado invalido: " + request.getStatus());
		}

		Set<String> permitidas = TRANSICIONES_VALIDAS.get(alerta.getStatus());
		if (permitidas == null || !permitidas.contains(nuevoEstado)) {
			throw new ExcepcionFraude(
				"Transicion invalida: " + alerta.getStatus() + " -> " + nuevoEstado);
		}

		alerta.setStatus(nuevoEstado);
		alerta.setAssignedTo(request.getAssignedTo());

		if ("CONFIRMED".equals(nuevoEstado) || "DISMISSED".equals(nuevoEstado)) {
			alerta.setResolvedAt(LocalDateTime.now());
			alerta.setResolvedBy(request.getAssignedTo());
			alerta.setResolutionNotes(request.getResolutionNotes());
		}

		AlertaFraude guardada = repositorio.save(alerta);
		logger.info("Alerta {} estado actualizado: {} -> {} por {}",
			alertUuid, alerta.getStatus(), nuevoEstado, request.getAssignedTo());

		return ResponseEntity.ok(convertirARespuesta(guardada));
	}

	private AlertaFraudeResponse convertirARespuesta(AlertaFraude alerta) {
		AlertaFraudeResponse response = new AlertaFraudeResponse();
		response.setAlertUuid(alerta.getAlertUuid().toString());
		response.setTypologyId(alerta.getTypologyId());
		response.setSeverityLevel(alerta.getSeverityLevel());
		response.setRiskScore(alerta.getRiskScore());
		response.setRiskScoreSource(alerta.getRiskScoreSource());
		response.setStatus(alerta.getStatus());
		response.setCreatedAt(alerta.getCreatedAt());

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
