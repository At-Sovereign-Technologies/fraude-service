package com.selloLegitimo.fraude.evento;

import java.time.LocalDateTime;
import java.util.UUID;

public class EscalacionNotificacionEvent {

    private final Long deliveryId;
    private final UUID alertUuid;
    private final String targetRole;
    private final LocalDateTime escalatedAt;

    public EscalacionNotificacionEvent(Long deliveryId, UUID alertUuid, String targetRole) {
        this.deliveryId = deliveryId;
        this.alertUuid = alertUuid;
        this.targetRole = targetRole;
        this.escalatedAt = LocalDateTime.now();
    }

    public Long getDeliveryId() { return deliveryId; }
    public UUID getAlertUuid() { return alertUuid; }
    public String getTargetRole() { return targetRole; }
    public LocalDateTime getEscalatedAt() { return escalatedAt; }
}
