package com.selloLegitimo.fraude.dto;

import java.util.List;

public class MetricasCasosPorEstadoResponse {

    private List<EstadoMetrica> estados;

    public List<EstadoMetrica> getEstados() { return estados; }
    public void setEstados(List<EstadoMetrica> estados) { this.estados = estados; }

    public static class EstadoMetrica {
        private String status;
        private long count;
        private boolean suppressed;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }

        public boolean isSuppressed() { return suppressed; }
        public void setSuppressed(boolean suppressed) { this.suppressed = suppressed; }
    }
}
