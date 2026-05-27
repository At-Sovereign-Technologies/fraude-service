package com.selloLegitimo.fraude.controlador;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.selloLegitimo.fraude.dto.MetricasAlertasPorZonaResponse;
import com.selloLegitimo.fraude.dto.MetricasCasosPorEstadoResponse;
import com.selloLegitimo.fraude.dto.MetricasMapaRiesgoResponse;
import com.selloLegitimo.fraude.dto.MetricasTipologiasPorDistritoResponse;
import com.selloLegitimo.fraude.servicio.ServicioMetricasDashboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ControladorMetricasTest {

    @Mock
    private ServicioMetricasDashboard servicioMetricas;

    private ControladorMetricas controlador;

    @BeforeEach
    void setUp() {
        controlador = new ControladorMetricas(servicioMetricas);
    }

    @Test
    void debeRetornarAlertasPorZona() {
        MetricasAlertasPorZonaResponse responseMock = new MetricasAlertasPorZonaResponse();
        when(servicioMetricas.activeAlertsByZone()).thenReturn(responseMock);

        ResponseEntity<MetricasAlertasPorZonaResponse> response = controlador.alertasPorZona();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(responseMock, response.getBody());
        verify(servicioMetricas).activeAlertsByZone();
    }

    @Test
    void debeRetornarCasosPorEstado() {
        MetricasCasosPorEstadoResponse responseMock = new MetricasCasosPorEstadoResponse();
        when(servicioMetricas.casesInvestigationStatus()).thenReturn(responseMock);

        ResponseEntity<MetricasCasosPorEstadoResponse> response = controlador.casosPorEstado();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(responseMock, response.getBody());
        verify(servicioMetricas).casesInvestigationStatus();
    }

    @Test
    void debeRetornarMapaDeRiesgo() {
        MetricasMapaRiesgoResponse responseMock = new MetricasMapaRiesgoResponse();
        when(servicioMetricas.riskHeatmap()).thenReturn(responseMock);

        ResponseEntity<MetricasMapaRiesgoResponse> response = controlador.mapaRiesgo();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(responseMock, response.getBody());
        verify(servicioMetricas).riskHeatmap();
    }

    @Test
    void debeRetornarTipologiasPorDistrito() {
        MetricasTipologiasPorDistritoResponse responseMock = new MetricasTipologiasPorDistritoResponse();
        when(servicioMetricas.typologyMetricsByDistrict()).thenReturn(responseMock);

        ResponseEntity<MetricasTipologiasPorDistritoResponse> response = controlador.tipologiasPorDistrito();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(responseMock, response.getBody());
        verify(servicioMetricas).typologyMetricsByDistrict();
    }
}
