CREATE TABLE fraude.case_audit_log (
    id          BIGSERIAL PRIMARY KEY,
    alert_uuid  UUID NOT NULL
        REFERENCES fraude.fraud_alerts(alert_uuid),
    actor_id    VARCHAR(100) NOT NULL,
    from_status VARCHAR(30),
    to_status   VARCHAR(30) NOT NULL,
    transition_timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
    metadata    JSONB
);

CREATE INDEX idx_audit_alert
    ON fraude.case_audit_log(alert_uuid);
CREATE INDEX idx_audit_timestamp
    ON fraude.case_audit_log(transition_timestamp DESC);
