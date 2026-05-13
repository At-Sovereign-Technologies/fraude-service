package com.selloLegitimo.fraude.servicio;

import com.selloLegitimo.fraude.modelo.Tipologia;
import com.selloLegitimo.fraude.repositorio.RepositorioTipologia;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ClasificadorTipologia {

	private static final Map<String, String> EVENT_TYPE_TO_TYPOLOGY = Map.ofEntries(
		Map.entry("SE-M1:LOGIN_FAILED", "AUTH_ANOMALY"),
		Map.entry("SE-M1:MFA_VERIFY_FAIL", "AUTH_ANOMALY"),
		Map.entry("SE-M3:ACCESS_DENIED", "ACCESS_VIOLATION"),
		Map.entry("SE-M3:ROLE_ESCALATION", "ACCESS_VIOLATION"),
		Map.entry("SR-M5:E14_INCONSISTENCY", "E14_ANOMALY"),
		Map.entry("SR-M6:HASH_CHAIN_BREAK", "AUTH_CHAIN_BREAK"),
		Map.entry("SR-M6:TEMPORAL_ANOMALY", "BURST_DETECTED"),
		Map.entry("M8-05:FAILED_AUTH_ATTEMPTS", "AUTH_ANOMALY"),
		Map.entry("M8-05:BIOMETRIC_INCONSISTENCY", "BIOMETRIC_MISMATCH"),
		Map.entry("M8-05:DUPLICATE_VOTE_ATTEMPT", "DUPLICATE_VOTE"),
		Map.entry("M8-05:ANOMALOUS_TIME_PATTERN", "BURST_DETECTED"),
		Map.entry("M8-05:IRREGULAR_TABLE_BEHAVIOR", "TABLE_OUTLIER")
	);

	private final RepositorioTipologia repositorioTipologia;

	public ClasificadorTipologia(RepositorioTipologia repositorioTipologia) {
		this.repositorioTipologia = repositorioTipologia;
	}

	public Tipologia classify(String source, String eventType, Map<String, Object> metadata) {
		String typologyId = EVENT_TYPE_TO_TYPOLOGY.getOrDefault(source + ":" + eventType, "AUTH_ANOMALY");
		return repositorioTipologia.findById(typologyId)
			.orElseThrow(() -> new IllegalStateException("Tipologia " + typologyId + " no encontrada en catalogo"));
	}
}
