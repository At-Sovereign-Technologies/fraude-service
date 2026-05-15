ALTER TABLE fraude.fraud_alerts
  DROP CONSTRAINT IF EXISTS fraud_alerts_status_check;

ALTER TABLE fraude.fraud_alerts
  ADD CONSTRAINT fraud_alerts_status_check
  CHECK (status IN (
    'DETECTADO',
    'EN_EVALUACION',
    'EN_INVESTIGACION',
    'ESCALADO',
    'CONFIRMADO',
    'DESCARTADO',
    'CERRADO'
  ));

ALTER TABLE fraude.fraud_alerts
  ADD COLUMN IF NOT EXISTS closed_at       TIMESTAMP,
  ADD COLUMN IF NOT EXISTS closed_by       VARCHAR(100),
  ADD COLUMN IF NOT EXISTS last_actor_id   VARCHAR(100),
  ADD COLUMN IF NOT EXISTS last_transition_at TIMESTAMP;

UPDATE fraude.fraud_alerts
  SET status = CASE
    WHEN status = 'PENDING_REVIEW'      THEN 'DETECTADO'
    WHEN status = 'UNDER_INVESTIGATION' THEN 'EN_INVESTIGACION'
    WHEN status = 'CONFIRMED'           THEN 'CONFIRMADO'
    WHEN status = 'DISMISSED'           THEN 'DESCARTADO'
    ELSE status
  END;

ALTER TABLE fraude.typology_catalog
  ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP,
  ADD COLUMN IF NOT EXISTS version    INTEGER NOT NULL DEFAULT 0;
