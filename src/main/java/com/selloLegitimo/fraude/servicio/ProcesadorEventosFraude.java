package com.selloLegitimo.fraude.servicio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.selloLegitimo.fraude.dto.AlertaFraudeResponse;
import com.selloLegitimo.fraude.dto.ReportarEventoRequest;
import com.selloLegitimo.fraude.evento.AlertaCriticaEvent;
import com.selloLegitimo.fraude.excepcion.ExcepcionHashNoCoincide;
import com.selloLegitimo.fraude.modelo.AlertaFraude;
import com.selloLegitimo.fraude.modelo.EstadoAlerta;
import com.selloLegitimo.fraude.modelo.NivelSeveridad;
import com.selloLegitimo.fraude.modelo.Tipologia;
import com.selloLegitimo.fraude.repositorio.RepositorioAlertaFraude;
import com.selloLegitimo.fraude.scoring.RiskEvaluator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProcesadorEventosFraude {

	private static final Logger logger = LoggerFactory.getLogger(ProcesadorEventosFraude.class);
	private static final ObjectMapper MAPPER = new ObjectMapper();

	private final RepositorioAlertaFraude repositorioAlerta;
	private final ClasificadorTipologia clasificador;
	private final RiskEvaluator riskEvaluator;

	private final ApplicationEventPublisher eventPublisher;

	public ProcesadorEventosFraude(RepositorioAlertaFraude repositorioAlerta,
		ClasificadorTipologia clasificador,
		RiskEvaluator riskEvaluator,
		ApplicationEventPublisher eventPublisher) {
		this.repositorioAlerta = repositorioAlerta;
		this.clasificador = clasificador;
		this.riskEvaluator = riskEvaluator;
		this.eventPublisher = eventPublisher;
	}

	@Transactional
	public AlertaFraudeResponse procesar(ReportarEventoRequest request) {
		verificarHash(request);

		Optional<AlertaFraude> existente = repositorioAlerta
			.findByOriginModuleAndOriginEventId(request.getSource(), request.getOriginEventId());
		if (existente.isPresent()) {
			logger.info("Evento duplicado origen={}/{} -> alerta existente {}",
				request.getSource(), request.getOriginEventId(), existente.get().getAlertUuid());
			return convertirARespuesta(existente.get());
		}

		Tipologia tipologia = clasificador.classify(request.getSource(), request.getEventType(), request.getMetadata());
		RiskEvaluator.RiskResult riesgo = riskEvaluator.evaluate(
			tipologia.getId(), tipologia.getDefaultSeverity(), request.getMetadata());

		AlertaFraude alerta = new AlertaFraude();
		alerta.setTypologyId(tipologia.getId());
		alerta.setSeverityLevel(tipologia.getDefaultSeverity());
		alerta.setRiskScore(riesgo.score());
		alerta.setRiskScoreSource(riesgo.source());
		alerta.setStatus(EstadoAlerta.DETECTADO);
		alerta.setOriginModule(request.getSource());
		alerta.setOriginEventId(request.getOriginEventId());
		alerta.setVerificationHash(request.getVerificationHash());
		alerta.setCertifiedTimestamp(request.getCertifiedTimestamp());

		if (request.getLogicalLocation() != null) {
			alerta.setTableId(request.getLogicalLocation().getTableId());
			alerta.setPollingStation(request.getLogicalLocation().getPollingStation());
			alerta.setConstituency(request.getLogicalLocation().getConstituency());
			alerta.setChannel(request.getLogicalLocation().getChannel() != null
				? request.getLogicalLocation().getChannel() : "UNKNOWN");
		}

		if (request.getMetadata() != null && !request.getMetadata().isEmpty()) {
			try {
				alerta.setContextMetadata(MAPPER.writeValueAsString(request.getMetadata()));
			} catch (JsonProcessingException e) {
				logger.warn("No se pudo serializar context_metadata para evento {}", request.getOriginEventId());
			}
		}

		AlertaFraude guardada = repositorioAlerta.save(alerta);

		if (NivelSeveridad.CRITICAL.name().equals(tipologia.getDefaultSeverity())) {
			String constituency = request.getLogicalLocation() != null
				? request.getLogicalLocation().getConstituency() : null;
			eventPublisher.publishEvent(new AlertaCriticaEvent(
				guardada.getAlertUuid(), tipologia.getId(), constituency));
			logger.info("[RNF-SEG-007] Evento CRITICO publicado alerta={} tipologia={}",
				guardada.getAlertUuid(), tipologia.getId());
		}

		logger.info("Alerta generada uuid={} tipologia={} severidad={} score={} origen={}/{}",
			guardada.getAlertUuid(), tipologia.getId(), tipologia.getDefaultSeverity(),
			riesgo.score(), request.getSource(), request.getOriginEventId());

		return convertirARespuesta(guardada);
	}

	private void verificarHash(ReportarEventoRequest request) {
		String canonical = request.getOriginEventId() + "|"
			+ request.getCertifiedTimestamp().toString() + "|"
			+ request.getSource() + "|"
			+ request.getEventType();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] digest = md.digest(canonical.getBytes());
			String computed = HexFormat.of().formatHex(digest);
			if (!computed.equals(request.getVerificationHash())) {
				throw new ExcepcionHashNoCoincide(
					"Hash de verificacion no coincide. Recibido=" + request.getVerificationHash()
						+ " calculado=" + computed);
			}
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-256 no disponible", e);
		}
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

		if (alerta.getContextMetadata() != null) {
			try {
				@SuppressWarnings("unchecked")
				java.util.Map<String, Object> map = MAPPER.readValue(alerta.getContextMetadata(), java.util.Map.class);
				response.setContextMetadata(map);
			} catch (Exception e) {
				logger.warn("No se pudo deserializar context_metadata para alerta {}", alerta.getAlertUuid());
			}
		}

		return response;
	}
}
