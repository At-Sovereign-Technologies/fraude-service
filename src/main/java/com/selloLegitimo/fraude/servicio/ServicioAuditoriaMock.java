package com.selloLegitimo.fraude.servicio;

import com.selloLegitimo.fraude.dto.TransitionAuditResponse;
import com.selloLegitimo.fraude.modelo.EstadoAlerta;
import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ServicioAuditoriaMock {

    private static final Logger log = LoggerFactory.getLogger(ServicioAuditoriaMock.class);

    public TransitionAuditResponse registrarTransicion(
            UUID alertUuid,
            EstadoAlerta fromStatus,
            EstadoAlerta toStatus,
            String actorId) {
        TransitionAuditResponse response = new TransitionAuditResponse();
        response.setMockAuditLogId("AUD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        response.setTimestamp(LocalDateTime.now());
        response.setFromStatus(fromStatus != null ? fromStatus.name() : null);
        response.setToStatus(toStatus.name());
        response.setActorId(actorId);
        log.info("[SR-M6 AUDIT] alerta={} from={} to={} actor={} logId={}",
            alertUuid, fromStatus, toStatus, actorId, response.getMockAuditLogId());
        return response;
    }
}
