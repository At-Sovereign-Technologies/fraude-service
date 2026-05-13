package com.selloLegitimo.fraude.scoring;

import java.util.Map;

public interface RiskEvaluator {

	RiskResult evaluate(String typologyId, String severity, Map<String, Object> metadata);

	record RiskResult(int score, String source) {}
}
