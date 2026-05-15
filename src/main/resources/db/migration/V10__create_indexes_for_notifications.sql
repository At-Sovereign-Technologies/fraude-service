-- Additional indexes to support notification SLA checks and 60-second escalation queries
CREATE INDEX IF NOT EXISTS idx_notif_sla_check
    ON fraude.notification_delivery(generated_at, delivered_at, escalated_at)
    WHERE delivered_at IS NULL;
