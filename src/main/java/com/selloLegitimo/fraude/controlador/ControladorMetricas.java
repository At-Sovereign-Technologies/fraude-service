package com.selloLegitimo.fraude.controlador;

import com.selloLegitimo.fraude.dto.MetricasAlertasPorZonaResponse;
import com.selloLegitimo.fraude.dto.MetricasCasosPorEstadoResponse;
import com.selloLegitimo.fraude.dto.MetricasMapaRiesgoResponse;
import com.selloLegitimo.fraude.dto.MetricasTipologiasPorDistritoResponse;
import com.selloLegitimo.fraude.seguridad.RequierePermiso;
import com.selloLegitimo.fraude.servicio.ServicioMetricasDashboard;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fraude/metricas")
public class ControladorMetricas {

    private final ServicioMetricasDashboard servicioMetricas;

    public ControladorMetricas(ServicioMetricasDashboard servicioMetricas) {
        this.servicioMetricas = servicioMetricas;
    }

    @GetMapping("/alertas-por-zona")
    @RequierePermiso(recurso = "METRICAS", operacion = "READ")
    public ResponseEntity<MetricasAlertasPorZonaResponse> alertasPorZona() {
        return ResponseEntity.ok(servicioMetricas.activeAlertsByZone());
    }

    @GetMapping("/casos-por-estado")
    @RequierePermiso(recurso = "METRICAS", operacion = "READ")
    public ResponseEntity<MetricasCasosPorEstadoResponse> casosPorEstado() {
        return ResponseEntity.ok(servicioMetricas.casesInvestigationStatus());
    }

    @GetMapping("/mapa-riesgo")
    @RequierePermiso(recurso = "METRICAS", operacion = "READ")
    public ResponseEntity<MetricasMapaRiesgoResponse> mapaRiesgo() {
        return ResponseEntity.ok(servicioMetricas.riskHeatmap());
    }

    @GetMapping("/tipologias-por-distrito")
    @RequierePermiso(recurso = "METRICAS", operacion = "READ")
    public ResponseEntity<MetricasTipologiasPorDistritoResponse> tipologiasPorDistrito() {
        return ResponseEntity.ok(servicioMetricas.typologyMetricsByDistrict());
    }
}
