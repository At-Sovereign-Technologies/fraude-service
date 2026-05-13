package com.selloLegitimo.fraude.scoring;

import java.util.Map;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class MockRiskEvaluator implements RiskEvaluator {

	@Override
	public RiskResult evaluate(String typologyId, String severity, Map<String, Object> metadata) {
		int base = switch (severity) {
			case "CRITICAL" -> 85;
			case "SUSPICIOUS" -> 50;
			case "INFORMATIONAL" -> 20;
			default -> 0;
		};

		int escalation = 0;
		if (metadata != null) {
			if (metadata.containsKey("failedAttempts") && metadata.containsKey("threshold")) {
				int attempts = ((Number) metadata.get("failedAttempts")).intValue();
				int threshold = ((Number) metadata.get("threshold")).intValue();
				if (threshold > 0 && attempts >= threshold * 2) {
					escalation = 10;
				}
			}
			if (metadata.containsKey("votosPreviosRegistrados")) {
				int previos = ((Number) metadata.get("votosPreviosRegistrados")).intValue();
				if (previos > 0) {
					escalation = 15;
				}
			}
		}

		return new RiskResult(Math.min(100, base + escalation), "MOCK");
	}
}
