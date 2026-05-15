INSERT INTO fraude.fraud_alerts (
    alert_uuid, typology_id, severity_level, risk_score, risk_score_source,
    status, origin_module, origin_event_id, verification_hash, certified_timestamp,
    table_id, polling_station, constituency, channel, context_metadata,
    assigned_to, resolution_notes, resolved_at, resolved_by,
    closed_at, closed_by, last_actor_id, last_transition_at,
    created_at, updated_at
) VALUES
-- 1. DETECTADO — Intento de voto duplicado (crítico)
('a1000001-0000-4000-8000-000000000001','DUPLICATE_VOTE','CRITICAL',85,'M8-06',
 'DETECTADO','M8-05','evt-seed-001',
 '0c2d043b50e1f5ce6d2f1f9b29c36984a5e2240589eea9a44e4143acc8123f44','2026-05-10T08:30:00',
 'MESA-101','PUESTO-ANT-015','ANTIOQUIA','PRESENCIAL',
 '{"failedAttempts":3,"threshold":1,"windowMinutes":5}',
 NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,
 '2026-05-10T08:30:00','2026-05-10T08:30:00'),

-- 2. DETECTADO — Anomalía de autenticación
('a1000002-0000-4000-8000-000000000002','AUTH_ANOMALY','SUSPICIOUS',45,'DEFAULT',
 'DETECTADO','SE-M1','evt-seed-002',
 'ce3801b47235909cf347dce31ce3c602690f3dae48a21d77bb360ea6584d2a4d','2026-05-10T09:15:00',
 NULL,'PUESTO-BOG-003','BOGOTA','REMOTE',
 '{"failedAttempts":12,"threshold":5,"windowMinutes":30,"ip":"192.168.1.50"}',
 NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,
 '2026-05-10T09:15:00','2026-05-10T09:15:00'),

-- 3. EN_EVALUACION — Discrepancia biométrica
('a1000003-0000-4000-8000-000000000003','BIOMETRIC_MISMATCH','SUSPICIOUS',62,'MOCK',
 'EN_EVALUACION','M8-05','evt-seed-003',
 'd0c71ecdf54b56382e460d29aa6c604df6464ca086876ae522f6b84542e9772b','2026-05-10T10:00:00',
 'MESA-203','PUESTO-VAL-007','VALLE','PRESENCIAL',
 '{"confidence":0.32,"threshold":0.75,"biometricProvider":"MOCK-FACE"}',
 'investigador1',NULL,NULL,NULL,NULL,NULL,'investigador1','2026-05-11T09:00:00',
 '2026-05-10T10:00:00','2026-05-11T09:00:00'),

-- 4. EN_INVESTIGACION — Anomalía en acta E14 (crítica)
('a1000004-0000-4000-8000-000000000004','E14_ANOMALY','CRITICAL',78,'M8-06',
 'EN_INVESTIGACION','SR-M5','evt-seed-004',
 '94b2ca650c3926cefa4f1d472df18e2643f905c196801477a026219306224d25','2026-05-10T11:45:00',
 'MESA-305','PUESTO-BOL-002','BOLIVAR','PRESENCIAL',
 '{"expectedVotes":300,"reportedVotes":450,"difference":150}',
 'investigador2',NULL,NULL,NULL,NULL,NULL,'investigador2','2026-05-12T08:00:00',
 '2026-05-10T11:45:00','2026-05-12T08:00:00'),

-- 5. ESCALADO — Rotura de cadena de auditoría (crítica)
('a1000005-0000-4000-8000-000000000005','AUTH_CHAIN_BREAK','CRITICAL',92,'MOCK',
 'ESCALADO','SR-M6','evt-seed-005',
 'd758d7f993728a16f837f32820a68a710dde3cfec91fa4abb25240eede00f655','2026-05-10T12:30:00',
 NULL,NULL,'NACIONAL','REMOTE',
 '{"chainLength":1500,"brokenAt":847,"expectedHash":"abc...","receivedHash":"def..."}',
 'supervisor1',NULL,NULL,NULL,NULL,NULL,'supervisor1','2026-05-12T14:00:00',
 '2026-05-10T12:30:00','2026-05-12T14:00:00'),

-- 6. CONFIRMADO — Mesa con comportamiento atípico
('a1000006-0000-4000-8000-000000000006','TABLE_OUTLIER','SUSPICIOUS',70,'M8-06',
 'CONFIRMADO','M8-05','evt-seed-006',
 '31d393d044d0d06b72300dbbb97125d4faf87204713d5b8422501ec2480530e3','2026-05-09T14:00:00',
 'MESA-402','PUESTO-SAN-011','SANTANDER','PRESENCIAL',
 '{"expectedAvg":180,"actualVotes":340,"stdDev":4.2,"zScore":8.1}',
 'admin','Fraude confirmado: se comprobo suplantacion de identidad en 23 votos.','2026-05-12T16:00:00','admin',
 NULL,NULL,'admin','2026-05-12T16:00:00',
 '2026-05-09T14:00:00','2026-05-12T16:00:00'),

-- 7. CERRADO — Violación de acceso (descartado)
('a1000007-0000-4000-8000-000000000007','ACCESS_VIOLATION','INFORMATIONAL',15,'DEFAULT',
 'CERRADO','SE-M3','evt-seed-007',
 'a5d8032a98473c45f0bb6e3b91cebb1ba28ffa123aa2243ff8bbfc466c39333c','2026-05-08T07:00:00',
 NULL,NULL,'CUNDINAMARCA','REMOTE',
 '{"resource":"/api/admin/users","method":"GET","ip":"10.0.0.23"}',
 'admin','Falso positivo: el usuario tenia permisos temporales validos.','2026-05-09T10:00:00','admin',
 '2026-05-09T10:30:00','admin','admin','2026-05-09T10:30:00',
 '2026-05-08T07:00:00','2026-05-09T10:30:00'),

-- 8. CERRADO — Ráfaga de eventos (confirmado)
('a1000008-0000-4000-8000-000000000008','BURST_DETECTED','SUSPICIOUS',55,'MOCK',
 'CERRADO','SR-M6','evt-seed-008',
 '9b6f8d622f4c4e2967cf01c9bad39dd63265b824554ff5df3330e9592be7690c','2026-05-07T16:30:00',
 'MESA-501','PUESTO-ATL-009','ATLANTICO','PRESENCIAL',
 '{"eventCount":85,"windowMinutes":15,"threshold":30}',
 'admin','Se confirmo un ataque de suplantacion coordinado en 3 mesas.','2026-05-08T11:00:00','admin',
 '2026-05-08T11:30:00','admin','admin','2026-05-08T11:30:00',
 '2026-05-07T16:30:00','2026-05-08T11:30:00');
