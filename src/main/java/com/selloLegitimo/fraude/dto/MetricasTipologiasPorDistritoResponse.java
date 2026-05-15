package com.selloLegitimo.fraude.dto;

import java.util.List;

public class MetricasTipologiasPorDistritoResponse {

    private List<DistritoTipologiaMetrica> distritos;

    public List<DistritoTipologiaMetrica> getDistritos() { return distritos; }
    public void setDistritos(List<DistritoTipologiaMetrica> distritos) { this.distritos = distritos; }

    public static class DistritoTipologiaMetrica {
        private String distrito;
        private String typologyId;
        private long count;
        private boolean suppressed;

        public String getDistrito() { return distrito; }
        public void setDistrito(String distrito) { this.distrito = distrito; }

        public String getTypologyId() { return typologyId; }
        public void setTypologyId(String typologyId) { this.typologyId = typologyId; }

        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }

        public boolean isSuppressed() { return suppressed; }
        public void setSuppressed(boolean suppressed) { this.suppressed = suppressed; }
    }
}
