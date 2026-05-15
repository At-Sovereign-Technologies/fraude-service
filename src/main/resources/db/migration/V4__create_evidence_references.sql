CREATE TABLE fraude.evidence_references (
    id                 UUID PRIMARY KEY,
    alert_uuid         UUID NOT NULL
        REFERENCES fraude.fraud_alerts(alert_uuid),
    reference_id       VARCHAR(255) NOT NULL,
    hash_signature     VARCHAR(64) NOT NULL,
    original_timestamp TIMESTAMP NOT NULL,
    verified           BOOLEAN NOT NULL DEFAULT FALSE,
    verified_at        TIMESTAMP,
    created_at         TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_evidence_alert
    ON fraude.evidence_references(alert_uuid);
