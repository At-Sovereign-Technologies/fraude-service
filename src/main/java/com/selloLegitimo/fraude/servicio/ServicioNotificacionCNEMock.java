package com.selloLegitimo.fraude.servicio;

import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ServicioNotificacionCNEMock {

    private static final Logger log = LoggerFactory.getLogger(ServicioNotificacionCNEMock.class);

    public String notificarModificacionTipologia(String tipologiaId, String accion, String justification) {
        String notificationId = "CNE-NOTIF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("[CNE NOTIFICATION] tipologia={} accion={} justificacion={} notificationId={}",
            tipologiaId, accion, justification, notificationId);
        return notificationId;
    }
}
