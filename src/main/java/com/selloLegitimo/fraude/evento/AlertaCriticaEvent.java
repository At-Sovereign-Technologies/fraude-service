package com.selloLegitimo.fraude.evento;

import java.time.LocalDateTime;
import java.util.UUID;

public class AlertaCriticaEvent {

    private final UUID alertUuid;
    private final String typologyId;
    private final String constituency;
    private final LocalDateTime generatedAt;

    public AlertaCriticaEvent(UUID alertUuid, String typologyId, String constituency) {
        this.alertUuid = alertUuid;
        this.typologyId = typologyId;
        this.constituency = constituency;
        this.generatedAt = LocalDateTime.now();
    }

    public UUID getAlertUuid() { return alertUuid; }
    public String getTypologyId() { return typologyId; }
    public String getConstituency() { return constituency; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
}
