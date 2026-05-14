-- =====================================================================
-- SR-M6: Conversion de Alertas en Casos de Investigacion Formal
-- =====================================================================
-- Atributos de calidad no negociables: transparencia, seguridad e
-- integridad referencial. Cada operacion sensible queda auditada con
-- identidad del actor, accion y timestamp inmutable.
-- =====================================================================

CREATE TABLE fraude.casos_investigacion (
    radicado                  UUID PRIMARY KEY,
    tipologia_fraude          VARCHAR(40) NOT NULL
        CHECK (tipologia_fraude IN (
            'SUPLANTACION','DOBLE_VOTO','ALTERACION_ACTA','COMPRA_VOTO','OTRO')),
    nivel_prioridad           VARCHAR(20) NOT NULL
        CHECK (nivel_prioridad IN ('BAJO','MEDIO','ALTO','CRITICO')),
    estado                    VARCHAR(40) NOT NULL DEFAULT 'DETECTADO'
        CHECK (estado IN (
            'DETECTADO','EN_EVALUACION','EN_INVESTIGACION','ESCALADO',
            'CONFIRMADO','DESCARTADO','CERRADO')),
    responsable_institucional VARCHAR(100) NOT NULL,
    entidad_competente        VARCHAR(40)  NOT NULL
        CHECK (entidad_competente IN ('RNEC','CNE','MESA_JUSTICIA','FISCALIA')),
    creado_en                 TIMESTAMP   NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
    creado_por                VARCHAR(100) NOT NULL,
    cerrado_en                TIMESTAMP,
    caso_precedente           UUID
        REFERENCES fraude.casos_investigacion(radicado)
);

CREATE INDEX idx_casos_estado            ON fraude.casos_investigacion(estado);
CREATE INDEX idx_casos_prioridad         ON fraude.casos_investigacion(nivel_prioridad);
CREATE INDEX idx_casos_tipologia         ON fraude.casos_investigacion(tipologia_fraude);
CREATE INDEX idx_casos_entidad           ON fraude.casos_investigacion(entidad_competente);
CREATE INDEX idx_casos_responsable       ON fraude.casos_investigacion(responsable_institucional);
CREATE INDEX idx_casos_creado_en         ON fraude.casos_investigacion(creado_en DESC);

-- Tabla puente alertas <-> caso
CREATE TABLE fraude.caso_alertas_origen (
    radicado     UUID NOT NULL
        REFERENCES fraude.casos_investigacion(radicado) ON DELETE CASCADE,
    alerta_uuid  UUID NOT NULL
        REFERENCES fraude.fraud_alerts(alert_uuid),
    PRIMARY KEY (radicado, alerta_uuid)
);

CREATE INDEX idx_caso_alertas_alerta ON fraude.caso_alertas_origen(alerta_uuid);

-- =====================================================================
-- Registro de auditoria: bitacora inmutable de transiciones de estado.
-- =====================================================================
CREATE TABLE fraude.registros_auditoria (
    id               UUID PRIMARY KEY,
    radicado_caso    UUID NOT NULL
        REFERENCES fraude.casos_investigacion(radicado),
    actor_id         VARCHAR(100) NOT NULL,
    rol_actor        VARCHAR(40)  NOT NULL
        CHECK (rol_actor IN (
            'ADMIN_RNEC','DELEGADO_CNE','DELEGADO_MESA_JUSTICIA','DELEGADO_FISCALIA')),
    accion           VARCHAR(255) NOT NULL,
    estado_anterior  VARCHAR(40),
    estado_nuevo     VARCHAR(40)  NOT NULL,
    "timestamp"      TIMESTAMP    NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
    metadatos        JSONB
);

CREATE INDEX idx_auditoria_radicado  ON fraude.registros_auditoria(radicado_caso);
CREATE INDEX idx_auditoria_timestamp ON fraude.registros_auditoria("timestamp");

-- =====================================================================
-- INVARIANTE DE SEGURIDAD 1 — Inmutabilidad del cierre.
-- Un caso en estado CERRADO no puede modificarse en ningun campo.
-- El cierre se materializa en una unica transaccion que pasa de
-- CONFIRMADO/DESCARTADO a CERRADO y escribe cerrado_en simultaneamente:
-- en ese UPDATE OLD.estado todavia no es CERRADO, asi que el trigger lo
-- permite. Cualquier UPDATE posterior con OLD.estado='CERRADO' aborta.
-- =====================================================================
CREATE OR REPLACE FUNCTION fraude.fn_bloquear_modificacion_cerrado()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.estado = 'CERRADO' THEN
        RAISE EXCEPTION
            'Caso % esta CERRADO de forma irreversible y no admite modificaciones',
            OLD.radicado
            USING ERRCODE = 'P0001';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_casos_bloquear_cerrado
BEFORE UPDATE ON fraude.casos_investigacion
FOR EACH ROW
EXECUTE FUNCTION fraude.fn_bloquear_modificacion_cerrado();

-- Tambien protegemos contra DELETE: un caso no se borra jamas.
CREATE OR REPLACE FUNCTION fraude.fn_bloquear_delete_caso()
RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION
        'Los casos de investigacion no pueden ser eliminados (radicado %)',
        OLD.radicado
        USING ERRCODE = 'P0001';
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_casos_bloquear_delete
BEFORE DELETE ON fraude.casos_investigacion
FOR EACH ROW
EXECUTE FUNCTION fraude.fn_bloquear_delete_caso();

-- =====================================================================
-- INVARIANTE DE SEGURIDAD 2 — Inmutabilidad de la auditoria.
-- Los registros de auditoria no admiten UPDATE ni DELETE bajo ninguna
-- circunstancia. Solo INSERT esta permitido.
-- =====================================================================
CREATE OR REPLACE FUNCTION fraude.fn_bloquear_modificacion_auditoria()
RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION
        'Los registros de auditoria son inmutables (operacion % rechazada)',
        TG_OP
        USING ERRCODE = 'P0001';
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_auditoria_no_update
BEFORE UPDATE ON fraude.registros_auditoria
FOR EACH ROW
EXECUTE FUNCTION fraude.fn_bloquear_modificacion_auditoria();

CREATE TRIGGER trg_auditoria_no_delete
BEFORE DELETE ON fraude.registros_auditoria
FOR EACH ROW
EXECUTE FUNCTION fraude.fn_bloquear_modificacion_auditoria();
