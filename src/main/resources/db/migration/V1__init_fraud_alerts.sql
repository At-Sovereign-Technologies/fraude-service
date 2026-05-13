CREATE SCHEMA IF NOT EXISTS fraude;

CREATE TABLE fraude.typology_catalog (
    id               VARCHAR(40) PRIMARY KEY,
    name             VARCHAR(150) NOT NULL,
    description      VARCHAR(500),
    default_severity VARCHAR(20) NOT NULL DEFAULT 'SUSPICIOUS'
        CHECK (default_severity IN ('INFORMATIONAL','SUSPICIOUS','CRITICAL')),
    requires_review  BOOLEAN NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE fraude.fraud_alerts (
    id                BIGSERIAL PRIMARY KEY,
    alert_uuid        UUID NOT NULL UNIQUE,
    typology_id       VARCHAR(40) NOT NULL
        REFERENCES fraude.typology_catalog(id),
    severity_level    VARCHAR(20) NOT NULL
        CHECK (severity_level IN ('INFORMATIONAL','SUSPICIOUS','CRITICAL')),
    risk_score        INTEGER NOT NULL DEFAULT 0
        CHECK (risk_score BETWEEN 0 AND 100),
    risk_score_source VARCHAR(10) NOT NULL DEFAULT 'DEFAULT'
        CHECK (risk_score_source IN ('DEFAULT','M8-06','MOCK')),
    status            VARCHAR(20) NOT NULL DEFAULT 'PENDING_REVIEW'
        CHECK (status IN ('PENDING_REVIEW','UNDER_INVESTIGATION','CONFIRMED','DISMISSED')),
    origin_module     VARCHAR(20) NOT NULL,
    origin_event_id   VARCHAR(100) NOT NULL,
    verification_hash VARCHAR(64) NOT NULL,
    certified_timestamp TIMESTAMP NOT NULL,
    table_id          VARCHAR(100),
    polling_station   VARCHAR(100),
    constituency      VARCHAR(100),
    channel           VARCHAR(20) DEFAULT 'UNKNOWN'
        CHECK (channel IN ('REMOTE','PRESENCIAL','UNKNOWN')),
    context_metadata  JSONB,
    assigned_to       VARCHAR(100),
    resolution_notes  TEXT,
    resolved_at       TIMESTAMP,
    resolved_by       VARCHAR(100),
    created_at        TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_fraud_alerts_origin
    ON fraude.fraud_alerts(origin_module, origin_event_id);
CREATE INDEX idx_fraud_alerts_status
    ON fraude.fraud_alerts(status);
CREATE INDEX idx_fraud_alerts_severity
    ON fraude.fraud_alerts(severity_level);
CREATE INDEX idx_fraud_alerts_typology
    ON fraude.fraud_alerts(typology_id);
CREATE INDEX idx_fraud_alerts_created
    ON fraude.fraud_alerts(created_at DESC);
