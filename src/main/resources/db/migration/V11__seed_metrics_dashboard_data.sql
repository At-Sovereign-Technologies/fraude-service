DO $$
DECLARE
    i INT;
    alert_ts TIMESTAMP := '2026-05-10 08:00:00';
    event_id TEXT;
    vhash TEXT;
BEGIN

    -- ============================================================================
    -- BOGOTÁ + DUPLICATE_VOTE × 25  (all DETECTADO, risk 75-95, PRESENCIAL)
    -- ============================================================================
    FOR i IN 1..25 LOOP
        event_id := 'evt-seed-bog-' || lpad(i::TEXT, 3, '0');
        vhash    := encode(sha256((event_id || i || random()::TEXT)::BYTEA), 'hex');
        INSERT INTO fraude.fraud_alerts (
            alert_uuid, typology_id, severity_level, risk_score, risk_score_source,
            status, origin_module, origin_event_id, verification_hash, certified_timestamp,
            table_id, polling_station, constituency, channel, context_metadata,
            created_at, updated_at
        ) VALUES (
            gen_random_uuid(),
            'DUPLICATE_VOTE',
            'CRITICAL',
            75 + (i % 21),                          -- 75..95
            'M8-06',
            'DETECTADO',
            'M8-05',
            event_id,
            vhash,
            alert_ts + (i * INTERVAL '15 minutes'),
            'MESA-BOG-' || lpad(i::TEXT, 3, '0'),
            'PUESTO-BOG-' || lpad(i::TEXT, 3, '0'),
            'BOGOTA',
            'PRESENCIAL',
            ('{"failedAttempts":' || (2 + (i % 4)) || ',"threshold":1,"windowMinutes":5}')::JSONB,
            alert_ts + (i * INTERVAL '15 minutes'),
            alert_ts + (i * INTERVAL '15 minutes')
        );
    END LOOP;

    -- ============================================================================
    -- ANTIOQUIA + AUTH_ANOMALY × 15  (10 DETECTADO + 5 EN_EVALUACION, risk 35-55, REMOTE)
    -- ============================================================================
    FOR i IN 1..15 LOOP
        event_id := 'evt-seed-ant-' || lpad(i::TEXT, 3, '0');
        vhash    := encode(sha256((event_id || i || random()::TEXT)::BYTEA), 'hex');
        INSERT INTO fraude.fraud_alerts (
            alert_uuid, typology_id, severity_level, risk_score, risk_score_source,
            status, origin_module, origin_event_id, verification_hash, certified_timestamp,
            table_id, polling_station, constituency, channel, context_metadata,
            created_at, updated_at
        ) VALUES (
            gen_random_uuid(),
            'AUTH_ANOMALY',
            'SUSPICIOUS',
            35 + (i % 21),                          -- 35..55
            'DEFAULT',
            CASE WHEN i <= 10 THEN 'DETECTADO' ELSE 'EN_EVALUACION' END,
            'SE-M1',
            event_id,
            vhash,
            alert_ts + (i * INTERVAL '12 minutes'),
            NULL,
            'PUESTO-ANT-' || lpad(i::TEXT, 3, '0'),
            'ANTIOQUIA',
            'REMOTE',
            ('{"failedAttempts":' || (8 + (i % 10)) || ',"threshold":5,"windowMinutes":30,"ip":"192.168.1.' || (50 + i) || '"}')::JSONB,
            alert_ts + (i * INTERVAL '12 minutes'),
            alert_ts + (i * INTERVAL '12 minutes')
        );
    END LOOP;

    -- ============================================================================
    -- VALLE + BIOMETRIC_MISMATCH × 12  (5 DETECTADO + 4 EN_EVALUACION + 3 EN_INVESTIGACION, risk 55-75, PRESENCIAL)
    -- ============================================================================
    FOR i IN 1..12 LOOP
        event_id := 'evt-seed-val-' || lpad(i::TEXT, 3, '0');
        vhash    := encode(sha256((event_id || i || random()::TEXT)::BYTEA), 'hex');
        INSERT INTO fraude.fraud_alerts (
            alert_uuid, typology_id, severity_level, risk_score, risk_score_source,
            status, origin_module, origin_event_id, verification_hash, certified_timestamp,
            table_id, polling_station, constituency, channel, context_metadata,
            created_at, updated_at
        ) VALUES (
            gen_random_uuid(),
            'BIOMETRIC_MISMATCH',
            'SUSPICIOUS',
            55 + (i % 21),                          -- 55..75
            'MOCK',
            CASE
                WHEN i <= 5  THEN 'DETECTADO'
                WHEN i <= 9  THEN 'EN_EVALUACION'
                ELSE              'EN_INVESTIGACION'
            END,
            'M8-05',
            event_id,
            vhash,
            alert_ts + (i * INTERVAL '20 minutes'),
            'MESA-VAL-' || lpad(i::TEXT, 3, '0'),
            'PUESTO-VAL-' || lpad(i::TEXT, 3, '0'),
            'VALLE',
            'PRESENCIAL',
            ('{"confidence":0.' || (25 + (i % 20)) || ',"threshold":0.75,"biometricProvider":"MOCK-FACE"}')::JSONB,
            alert_ts + (i * INTERVAL '20 minutes'),
            alert_ts + (i * INTERVAL '20 minutes')
        );
    END LOOP;

    -- ============================================================================
    -- BOLÍVAR + E14_ANOMALY × 8  (3 DETECTADO + 2 EN_EVALUACION + 2 EN_INVESTIGACION + 1 CONFIRMADO, risk 65-85, PRESENCIAL)
    -- ============================================================================
    FOR i IN 1..8 LOOP
        event_id := 'evt-seed-bol-' || lpad(i::TEXT, 3, '0');
        vhash    := encode(sha256((event_id || i || random()::TEXT)::BYTEA), 'hex');
        INSERT INTO fraude.fraud_alerts (
            alert_uuid, typology_id, severity_level, risk_score, risk_score_source,
            status, origin_module, origin_event_id, verification_hash, certified_timestamp,
            table_id, polling_station, constituency, channel, context_metadata,
            created_at, updated_at
        ) VALUES (
            gen_random_uuid(),
            'E14_ANOMALY',
            'CRITICAL',
            65 + (i % 21),                          -- 65..85
            'M8-06',
            CASE
                WHEN i <= 3  THEN 'DETECTADO'
                WHEN i <= 5  THEN 'EN_EVALUACION'
                WHEN i <= 7  THEN 'EN_INVESTIGACION'
                ELSE              'CONFIRMADO'
            END,
            'SR-M5',
            event_id,
            vhash,
            alert_ts + (i * INTERVAL '25 minutes'),
            'MESA-BOL-' || lpad(i::TEXT, 3, '0'),
            'PUESTO-BOL-' || lpad(i::TEXT, 3, '0'),
            'BOLIVAR',
            'PRESENCIAL',
            ('{"expectedVotes":300,"reportedVotes":' || (350 + (i * 30)) || ',"difference":' || (50 + (i * 30)) || '}')::JSONB,
            alert_ts + (i * INTERVAL '25 minutes'),
            alert_ts + (i * INTERVAL '25 minutes')
        );
    END LOOP;

END $$;
