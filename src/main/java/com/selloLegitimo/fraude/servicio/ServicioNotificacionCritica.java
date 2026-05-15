package com.selloLegitimo.fraude.servicio;

import com.selloLegitimo.fraude.evento.AlertaCriticaEvent;
import com.selloLegitimo.fraude.modelo.EntregaNotificacion;
import com.selloLegitimo.fraude.repositorio.RepositorioEntregaNotificacion;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioNotificacionCritica {

    private static final Logger log = LoggerFactory.getLogger(ServicioNotificacionCritica.class);
    private static final List<String> ROLES_COMPETENTES = List.of(
        "FRAUD_ANALYST", "SUPERVISOR", "SYSTEM_ADMIN");

    private final RepositorioEntregaNotificacion repositorioEntrega;

    public ServicioNotificacionCritica(RepositorioEntregaNotificacion repositorioEntrega) {
        this.repositorioEntrega = repositorioEntrega;
    }

    @Async
    @EventListener
    @Transactional
    public void handleAlertaCritica(AlertaCriticaEvent event) {
        for (String role : ROLES_COMPETENTES) {
            EntregaNotificacion entrega = new EntregaNotificacion();
            entrega.setAlertUuid(event.getAlertUuid());
            entrega.setTargetRole(role);
            entrega.setGeneratedAt(LocalDateTime.now());
            entrega.setDeliveryChannel("IN_APP");
            repositorioEntrega.save(entrega);

            log.info("[RNF-SEG-007] NOTIFICACION ENVIADA alerta={} rol={} entregaId={}",
                event.getAlertUuid(), role, entrega.getId());
        }
    }

    @Transactional
    public void confirmarEntrega(Long deliveryId, String targetRole) {
        EntregaNotificacion entrega = repositorioEntrega.findById(deliveryId)
            .orElseThrow(() -> new IllegalArgumentException(
                "No existe registro de entrega con id " + deliveryId));

        if (!entrega.getTargetRole().equals(targetRole)) {
            throw new IllegalArgumentException(
                "El rol " + targetRole + " no coincide con el destinatario " + entrega.getTargetRole());
        }

        entrega.setDeliveredAt(LocalDateTime.now());
        repositorioEntrega.save(entrega);

        log.info("[RNF-SEG-007] ENTREGA CONFIRMADA deliveryId={} alerta={} rol={}",
            deliveryId, entrega.getAlertUuid(), targetRole);
    }

    public List<EntregaNotificacion> obtenerPendientes() {
        LocalDateTime threshold = LocalDateTime.now().minusSeconds(60);
        return repositorioEntrega.findPendingBefore(threshold);
    }
}
