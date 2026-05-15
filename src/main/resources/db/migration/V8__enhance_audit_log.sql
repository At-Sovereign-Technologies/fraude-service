ALTER TABLE fraude.case_audit_log
    ADD COLUMN IF NOT EXISTS role VARCHAR(50),
    ADD COLUMN IF NOT EXISTS record_hash VARCHAR(64),
    ADD COLUMN IF NOT EXISTS previous_hash VARCHAR(64),
    ADD COLUMN IF NOT EXISTS closure_uuid UUID
        REFERENCES fraude.case_closure(alert_uuid)
        ON DELETE RESTRICT;

CREATE INDEX idx_audit_record_hash ON fraude.case_audit_log(record_hash);
CREATE INDEX idx_audit_previous_hash ON fraude.case_audit_log(previous_hash);

CREATE OR REPLACE FUNCTION fraude.immutable_audit_log()
RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION 'El log de auditoria es append-only: no se permite %', TG_OP
        USING HINT = 'Las entradas del log de auditoria no pueden ser modificadas ni eliminadas';
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_audit_log_immutable
    BEFORE UPDATE OR DELETE ON fraude.case_audit_log
    FOR EACH ROW EXECUTE FUNCTION fraude.immutable_audit_log();
