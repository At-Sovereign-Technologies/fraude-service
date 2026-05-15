package com.selloLegitimo.fraude.dto;

import java.util.List;

public class MetricasAlertasPorZonaResponse {

    private List<ZonaMetrica> zonas;

    public List<ZonaMetrica> getZonas() { return zonas; }
    public void setZonas(List<ZonaMetrica> zonas) { this.zonas = zonas; }

    public static class ZonaMetrica {
        private String zona;
        private long activeAlerts;
        private boolean suppressed;

        public String getZona() { return zona; }
        public void setZona(String zona) { this.zona = zona; }

        public long getActiveAlerts() { return activeAlerts; }
        public void setActiveAlerts(long activeAlerts) { this.activeAlerts = activeAlerts; }

        public boolean isSuppressed() { return suppressed; }
        public void setSuppressed(boolean suppressed) { this.suppressed = suppressed; }
    }
}
