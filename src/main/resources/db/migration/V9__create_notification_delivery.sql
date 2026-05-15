CREATE TABLE fraude.notification_delivery (
    id               BIGSERIAL PRIMARY KEY,
    alert_uuid       UUID NOT NULL
        REFERENCES fraude.fraud_alerts(alert_uuid)
        ON DELETE RESTRICT,
    target_role      VARCHAR(50) NOT NULL,
    generated_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    delivered_at     TIMESTAMP,
    escalated_at     TIMESTAMP,
    delivery_channel VARCHAR(20) NOT NULL DEFAULT 'IN_APP'
);

CREATE INDEX idx_notif_alert_uuid ON fraude.notification_delivery(alert_uuid);
CREATE INDEX idx_notif_pending ON fraude.notification_delivery(generated_at)
    WHERE delivered_at IS NULL AND escalated_at IS NULL;
CREATE INDEX idx_notif_escalated ON fraude.notification_delivery(escalated_at)
    WHERE escalated_at IS NOT NULL;
