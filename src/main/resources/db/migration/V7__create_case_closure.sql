CREATE TABLE fraude.case_closure (
    id                    BIGSERIAL PRIMARY KEY,
    alert_uuid            UUID NOT NULL
        REFERENCES fraude.fraud_alerts(alert_uuid)
        ON DELETE RESTRICT,
    final_result          VARCHAR(20) NOT NULL
        CONSTRAINT chk_closure_final_result
            CHECK (final_result IN ('CONFIRMED_FRAUD', 'DISMISSED')),
    justification         TEXT NOT NULL
        CONSTRAINT chk_closure_justification
            CHECK (length(trim(justification)) > 0),
    institutional_actions JSONB NOT NULL
        CONSTRAINT chk_closure_actions
            CHECK (jsonb_typeof(institutional_actions) = 'array' AND jsonb_array_length(institutional_actions) > 0),
    responsible_entity    VARCHAR(200) NOT NULL
        CONSTRAINT chk_closure_entity
            CHECK (length(trim(responsible_entity)) > 0),
    actor_id              VARCHAR(100) NOT NULL,
    actor_role            VARCHAR(50) NOT NULL,
    closure_timestamp     TIMESTAMP NOT NULL DEFAULT NOW(),
    signature             VARCHAR(128) NOT NULL,
    created_at            TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_closure_alert UNIQUE (alert_uuid)
);

CREATE INDEX idx_closure_alert_uuid ON fraude.case_closure(alert_uuid);
CREATE INDEX idx_closure_timestamp ON fraude.case_closure(closure_timestamp DESC);
CREATE INDEX idx_closure_result ON fraude.case_closure(final_result);

CREATE OR REPLACE FUNCTION fraude.immutable_case_closure()
RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION 'Las filas de case_closure son inmutables: no se permite %', TG_OP
        USING HINT = 'Una vez creado, un cierre de caso no puede ser modificado ni eliminado';
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_case_closure_immutable
    BEFORE UPDATE OR DELETE ON fraude.case_closure
    FOR EACH ROW EXECUTE FUNCTION fraude.immutable_case_closure();
