package com.selloLegitimo.fraude.dto;

import java.util.List;

public class MetricasMapaRiesgoResponse {

    private List<CeldaRiesgo> celdas;

    public List<CeldaRiesgo> getCeldas() { return celdas; }
    public void setCeldas(List<CeldaRiesgo> celdas) { this.celdas = celdas; }

    public static class CeldaRiesgo {
        private String zona;
        private double averageRiskScore;
        private long alertCount;
        private boolean suppressed;

        public String getZona() { return zona; }
        public void setZona(String zona) { this.zona = zona; }

        public double getAverageRiskScore() { return averageRiskScore; }
        public void setAverageRiskScore(double averageRiskScore) { this.averageRiskScore = averageRiskScore; }

        public long getAlertCount() { return alertCount; }
        public void setAlertCount(long alertCount) { this.alertCount = alertCount; }

        public boolean isSuppressed() { return suppressed; }
        public void setSuppressed(boolean suppressed) { this.suppressed = suppressed; }
    }
}
