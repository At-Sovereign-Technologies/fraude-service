package com.selloLegitimo.fraude.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServicioMetricasDashboardTest {

    @Mock
    private RepositorioAlertaFraude repositorioAlerta;

    private ServicioMetricasDashboard servicio;

    @BeforeEach
    void setUp() {
        servicio = new ServicioMetricasDashboard(repositorioAlerta);
    }

    @Test
    void debeCalcularAlertasActivasPorZonaYAnonimizarMenoresA5() {
        List<AlertaFraude> alertas = new ArrayList<>();
        // Zona BOGOTA con 6 alertas activas (no CERRADO ni DESCARTADO)
        for (int i = 0; i < 6; i++) {
            AlertaFraude a = new AlertaFraude();
            a.setConstituency("BOGOTA");
            a.setStatus(EstadoAlerta.DETECTADO);
            alertas.add(a);
        }
        // Zona MEDELLIN con 3 alertas activas
        for (int i = 0; i < 3; i++) {
            AlertaFraude a = new AlertaFraude();
            a.setConstituency("MEDELLIN");
            a.setStatus(EstadoAlerta.EN_EVALUACION);
            alertas.add(a);
        }
        // Alerta cerrada/descartada (no debe sumarse)
        AlertaFraude cerrada = new AlertaFraude();
        cerrada.setConstituency("BOGOTA");
        cerrada.setStatus(EstadoAlerta.CERRADO);
        alertas.add(cerrada);

        AlertaFraude descartada = new AlertaFraude();
        descartada.setConstituency("MEDELLIN");
        descartada.setStatus(EstadoAlerta.DESCARTADO);
        alertas.add(descartada);

        when(repositorioAlerta.findAll()).thenReturn(alertas);

        MetricasAlertasPorZonaResponse result = servicio.activeAlertsByZone();

        assertNotNull(result);
        assertEquals(2, result.getZonas().size());

        ZonaMetrica bogota = result.getZonas().get(0);
        assertEquals("BOGOTA", bogota.getZona());
        assertEquals(6, bogota.getActiveAlerts());
        assertFalse(bogota.isSuppressed());

        ZonaMetrica medellin = result.getZonas().get(1);
        assertEquals("MEDELLIN", medellin.getZona());
        assertEquals(3, medellin.getActiveAlerts());
        assertTrue(medellin.isSuppressed());
    }

    @Test
    void debeCalcularCasosPorEstadoYAnonimizarMenoresA5() {
        List<AlertaFraude> alertas = new ArrayList<>();
        // 5 en DETECTADO
        for (int i = 0; i < 5; i++) {
            AlertaFraude a = new AlertaFraude();
            a.setStatus(EstadoAlerta.DETECTADO);
            alertas.add(a);
        }
        // 2 en EN_EVALUACION
        for (int i = 0; i < 2; i++) {
            AlertaFraude a = new AlertaFraude();
            a.setStatus(EstadoAlerta.EN_EVALUACION);
            alertas.add(a);
        }

        when(repositorioAlerta.findAll()).thenReturn(alertas);

        MetricasCasosPorEstadoResponse result = servicio.casesInvestigationStatus();

        assertNotNull(result);
        assertEquals(2, result.getEstados().size());

        EstadoMetrica detectado = result.getEstados().get(0);
        assertEquals("DETECTADO", detectado.getStatus());
        assertEquals(5, detectado.getCount());
        assertFalse(detectado.isSuppressed());

        EstadoMetrica evaluacion = result.getEstados().get(1);
        assertEquals("EN_EVALUACION", evaluacion.getStatus());
        assertEquals(2, evaluacion.getCount());
        assertTrue(evaluacion.isSuppressed());
    }

    @Test
    void debeCalcularMapaDeRiesgoYAnonimizarMenoresA5() {
        List<AlertaFraude> alertas = new ArrayList<>();
        // Zona BOGOTA con 5 alertas y promedio de riesgo 80.0
        for (int i = 0; i < 5; i++) {
            AlertaFraude a = new AlertaFraude();
            a.setConstituency("BOGOTA");
            a.setRiskScore(80);
            alertas.add(a);
        }
        // Zona CALI con 2 alertas y promedio de riesgo 50.0
        for (int i = 0; i < 2; i++) {
            AlertaFraude a = new AlertaFraude();
            a.setConstituency("CALI");
            a.setRiskScore(50);
            alertas.add(a);
        }

        when(repositorioAlerta.findAll()).thenReturn(alertas);

        MetricasMapaRiesgoResponse result = servicio.riskHeatmap();

        assertNotNull(result);
        assertEquals(2, result.getCeldas().size());

        CeldaRiesgo bogota = result.getCeldas().get(0);
        assertEquals("BOGOTA", bogota.getZona());
        assertEquals(5, bogota.getAlertCount());
        assertEquals(80.0, bogota.getAverageRiskScore());
        assertFalse(bogota.isSuppressed());

        CeldaRiesgo cali = result.getCeldas().get(1);
        assertEquals("CALI", cali.getZona());
        assertEquals(2, cali.getAlertCount());
        assertEquals(0.0, cali.getAverageRiskScore()); // Suppressed averages are set to 0
        assertTrue(cali.isSuppressed());
    }

    @Test
    void debeCalcularTipologiasPorDistritoYAnonimizarMenoresA5() {
        List<AlertaFraude> alertas = new ArrayList<>();
        // BOGOTA + DUPLICATE_VOTE = 6
        for (int i = 0; i < 6; i++) {
            AlertaFraude a = new AlertaFraude();
            a.setConstituency("BOGOTA");
            a.setTypologyId("DUPLICATE_VOTE");
            alertas.add(a);
        }
        // BOGOTA + E14_ANOMALY = 2
        for (int i = 0; i < 2; i++) {
            AlertaFraude a = new AlertaFraude();
            a.setConstituency("BOGOTA");
            a.setTypologyId("E14_ANOMALY");
            alertas.add(a);
        }

        when(repositorioAlerta.findAll()).thenReturn(alertas);

        MetricasTipologiasPorDistritoResponse result = servicio.typologyMetricsByDistrict();

        assertNotNull(result);
        assertEquals(2, result.getDistritos().size());

        DistritoTipologiaMetrica duplicateVote = result.getDistritos().get(0);
        assertEquals("BOGOTA", duplicateVote.getDistrito());
        assertEquals("DUPLICATE_VOTE", duplicateVote.getTypologyId());
        assertEquals(6, duplicateVote.getCount());
        assertFalse(duplicateVote.isSuppressed());

        DistritoTipologiaMetrica e14Anomaly = result.getDistritos().get(1);
        assertEquals("BOGOTA", e14Anomaly.getDistrito());
        assertEquals("E14_ANOMALY", e14Anomaly.getTypologyId());
        assertEquals(2, e14Anomaly.getCount());
        assertTrue(e14Anomaly.isSuppressed());
    }
}
