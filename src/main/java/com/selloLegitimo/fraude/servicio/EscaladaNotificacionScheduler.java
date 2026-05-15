package com.selloLegitimo.fraude.servicio;

import com.selloLegitimo.fraude.evento.EscalacionNotificacionEvent;
import com.selloLegitimo.fraude.modelo.EntregaNotificacion;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@EnableScheduling
public class EscaladaNotificacionScheduler {

    private static final Logger log = LoggerFactory.getLogger(EscaladaNotificacionScheduler.class);

    private final ServicioNotificacionCritica servicioNotificacion;
    private final ApplicationEventPublisher eventPublisher;

    public EscaladaNotificacionScheduler(
            ServicioNotificacionCritica servicioNotificacion,
            ApplicationEventPublisher eventPublisher) {
        this.servicioNotificacion = servicioNotificacion;
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void verificarNotificacionesPendientes() {
        List<EntregaNotificacion> pendientes = servicioNotificacion.obtenerPendientes();

        for (EntregaNotificacion pendiente : pendientes) {
            pendiente.setEscalatedAt(LocalDateTime.now());
            log.warn("[RNF-SEG-007] ESCALACION deliveryId={} alerta={} rol={} "
                    + "generado={} sin_confirmar_por_ms={}",
                pendiente.getId(), pendiente.getAlertUuid(), pendiente.getTargetRole(),
                pendiente.getGeneratedAt(),
                java.time.Duration.between(pendiente.getGeneratedAt(), LocalDateTime.now()).toMillis());

            eventPublisher.publishEvent(new EscalacionNotificacionEvent(
                pendiente.getId(),
                pendiente.getAlertUuid(),
                pendiente.getTargetRole()));
        }

        if (!pendientes.isEmpty()) {
            log.warn("[RNF-SEG-007] {} notificaciones escaladas por exceder SLA de 60s",
                pendientes.size());
        }
    }
}
