package com.selloLegitimo.fraude.servicio;

import com.selloLegitimo.fraude.dto.MetricasAlertasPorZonaResponse;
import com.selloLegitimo.fraude.dto.MetricasAlertasPorZonaResponse.ZonaMetrica;
import com.selloLegitimo.fraude.dto.MetricasCasosPorEstadoResponse;
import com.selloLegitimo.fraude.dto.MetricasCasosPorEstadoResponse.EstadoMetrica;
import com.selloLegitimo.fraude.dto.MetricasMapaRiesgoResponse;
import com.selloLegitimo.fraude.dto.MetricasMapaRiesgoResponse.CeldaRiesgo;
import com.selloLegitimo.fraude.dto.MetricasTipologiasPorDistritoResponse;
import com.selloLegitimo.fraude.dto.MetricasTipologiasPorDistritoResponse.DistritoTipologiaMetrica;
import com.selloLegitimo.fraude.modelo.AlertaFraude;
import com.selloLegitimo.fraude.modelo.EstadoAlerta;
import com.selloLegitimo.fraude.repositorio.RepositorioAlertaFraude;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ServicioMetricasDashboard {

    private static final Logger log = LoggerFactory.getLogger(ServicioMetricasDashboard.class);
    private static final long K_THRESHOLD = 5;

    private final RepositorioAlertaFraude repositorioAlerta;

    public ServicioMetricasDashboard(RepositorioAlertaFraude repositorioAlerta) {
        this.repositorioAlerta = repositorioAlerta;
    }

    public MetricasAlertasPorZonaResponse activeAlertsByZone() {
        List<AlertaFraude> alertas = repositorioAlerta.findAll();
        Map<String, Long> grouped = alertas.stream()
            .filter(a -> a.getStatus() != EstadoAlerta.CERRADO
                && a.getStatus() != EstadoAlerta.DESCARTADO)
            .collect(Collectors.groupingBy(
                a -> a.getConstituency() != null ? a.getConstituency() : "UNKNOWN",
                Collectors.counting()));

        MetricasAlertasPorZonaResponse response = new MetricasAlertasPorZonaResponse();
        response.setZonas(anonymizeZonas(grouped));
        return response;
    }

    public MetricasCasosPorEstadoResponse casesInvestigationStatus() {
        List<AlertaFraude> alertas = repositorioAlerta.findAll();
        Map<String, Long> grouped = alertas.stream()
            .collect(Collectors.groupingBy(
                a -> a.getStatus() != null ? a.getStatus().name() : "UNKNOWN",
                Collectors.counting()));

        MetricasCasosPorEstadoResponse response = new MetricasCasosPorEstadoResponse();
        response.setEstados(anonymizeEstados(grouped));
        return response;
    }

    public MetricasMapaRiesgoResponse riskHeatmap() {
        List<AlertaFraude> alertas = repositorioAlerta.findAll();
        Map<String, List<AlertaFraude>> grouped = alertas.stream()
            .filter(a -> a.getConstituency() != null)
            .collect(Collectors.groupingBy(AlertaFraude::getConstituency));

        MetricasMapaRiesgoResponse response = new MetricasMapaRiesgoResponse();
        response.setCeldas(new ArrayList<>());
        for (Map.Entry<String, List<AlertaFraude>> entry : grouped.entrySet()) {
            double avgScore = entry.getValue().stream()
                .mapToInt(a -> a.getRiskScore() != null ? a.getRiskScore() : 0)
                .average().orElse(0.0);
            long count = entry.getValue().size();
            CeldaRiesgo celda = new CeldaRiesgo();
            celda.setZona(entry.getKey());
            celda.setAlertCount(count);
            if (count < K_THRESHOLD) {
                celda.setSuppressed(true);
                celda.setAverageRiskScore(0);
            } else {
                celda.setSuppressed(false);
                celda.setAverageRiskScore(Math.round(avgScore * 100.0) / 100.0);
            }
            response.getCeldas().add(celda);
        }
        response.getCeldas().sort(Comparator.comparing(CeldaRiesgo::getZona));
        return response;
    }

    public MetricasTipologiasPorDistritoResponse typologyMetricsByDistrict() {
        List<AlertaFraude> alertas = repositorioAlerta.findAll();
        Map<String, Map<String, Long>> grouped = alertas.stream()
            .filter(a -> a.getConstituency() != null && a.getTypologyId() != null)
            .collect(Collectors.groupingBy(
                AlertaFraude::getConstituency,
                Collectors.groupingBy(AlertaFraude::getTypologyId, Collectors.counting())));

        MetricasTipologiasPorDistritoResponse response = new MetricasTipologiasPorDistritoResponse();
        response.setDistritos(new ArrayList<>());
        for (Map.Entry<String, Map<String, Long>> distrito : grouped.entrySet()) {
            for (Map.Entry<String, Long> tipologia : distrito.getValue().entrySet()) {
                DistritoTipologiaMetrica metrica = new DistritoTipologiaMetrica();
                metrica.setDistrito(distrito.getKey());
                metrica.setTypologyId(tipologia.getKey());
                metrica.setCount(tipologia.getValue());
                if (tipologia.getValue() < K_THRESHOLD) {
                    metrica.setSuppressed(true);
                } else {
                    metrica.setSuppressed(false);
                }
                response.getDistritos().add(metrica);
            }
        }
        response.getDistritos().sort(
            Comparator.comparing(DistritoTipologiaMetrica::getDistrito)
                .thenComparing(DistritoTipologiaMetrica::getTypologyId));
        return response;
    }

    private List<ZonaMetrica> anonymizeZonas(Map<String, Long> raw) {
        List<ZonaMetrica> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : raw.entrySet()) {
            ZonaMetrica zm = new ZonaMetrica();
            zm.setZona(entry.getKey());
            zm.setActiveAlerts(entry.getValue());
            if (entry.getValue() < K_THRESHOLD) {
                zm.setSuppressed(true);
            } else {
                zm.setSuppressed(false);
            }
            result.add(zm);
        }
        result.sort(Comparator.comparing(ZonaMetrica::getZona));
        log.info("[ANONYMIZE] zonas={} suprimidas={}",
            result.size(), result.stream().filter(ZonaMetrica::isSuppressed).count());
        return result;
    }

    private List<EstadoMetrica> anonymizeEstados(Map<String, Long> raw) {
        List<EstadoMetrica> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : raw.entrySet()) {
            EstadoMetrica em = new EstadoMetrica();
            em.setStatus(entry.getKey());
            em.setCount(entry.getValue());
            if (entry.getValue() < K_THRESHOLD) {
                em.setSuppressed(true);
            } else {
                em.setSuppressed(false);
            }
            result.add(em);
        }
        result.sort(Comparator.comparing(EstadoMetrica::getStatus));
        log.info("[ANONYMIZE] estados={} suprimidas={}",
            result.size(), result.stream().filter(EstadoMetrica::isSuppressed).count());
        return result;
    }
}
