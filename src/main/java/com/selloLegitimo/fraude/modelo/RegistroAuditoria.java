package com.selloLegitimo.fraude.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Immutable;
import org.hibernate.type.SqlTypes;

/**
 * Registro inmutable de una accion sensible (tipicamente transicion de estado)
 * ejecutada sobre un {@link CasoInvestigacion}.
 *
 * <p>La inmutabilidad esta garantizada a nivel de base de datos por triggers
 * que rechazan UPDATE y DELETE sobre la tabla {@code fraude.registros_auditoria}.
 * La anotacion {@link Immutable} alinea el cache de JPA con esa garantia.</p>
 */
@Entity
@Immutable
@Table(name = "registros_auditoria", schema = "fraude")
public class RegistroAuditoria {

	@Id
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@Column(name = "radicado_caso", nullable = false, updatable = false)
	private UUID radicadoCaso;

	@Column(name = "actor_id", nullable = false, updatable = false, length = 100)
	private String actorId;

	@Enumerated(EnumType.STRING)
	@Column(name = "rol_actor", nullable = false, updatable = false, length = 40)
	private RolActorControl rolActor;

	@Column(name = "accion", nullable = false, updatable = false, length = 255)
	private String accion;

	@Enumerated(EnumType.STRING)
	@Column(name = "estado_anterior", updatable = false, length = 40)
	private EstadoCaso estadoAnterior;

	@Enumerated(EnumType.STRING)
	@Column(name = "estado_nuevo", nullable = false, updatable = false, length = 40)
	private EstadoCaso estadoNuevo;

	@Column(name = "timestamp", nullable = false, updatable = false)
	private LocalDateTime timestamp;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "metadatos", columnDefinition = "JSONB", updatable = false)
	private String metadatos;

	@PrePersist
	public void prePersist() {
		if (id == null) {
			id = UUID.randomUUID();
		}
		if (timestamp == null) {
			timestamp = LocalDateTime.now(ZoneOffset.UTC);
		}
	}

	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }

	public UUID getRadicadoCaso() { return radicadoCaso; }
	public void setRadicadoCaso(UUID radicadoCaso) { this.radicadoCaso = radicadoCaso; }

	public String getActorId() { return actorId; }
	public void setActorId(String actorId) { this.actorId = actorId; }

	public RolActorControl getRolActor() { return rolActor; }
	public void setRolActor(RolActorControl rolActor) { this.rolActor = rolActor; }

	public String getAccion() { return accion; }
	public void setAccion(String accion) { this.accion = accion; }

	public EstadoCaso getEstadoAnterior() { return estadoAnterior; }
	public void setEstadoAnterior(EstadoCaso estadoAnterior) { this.estadoAnterior = estadoAnterior; }

	public EstadoCaso getEstadoNuevo() { return estadoNuevo; }
	public void setEstadoNuevo(EstadoCaso estadoNuevo) { this.estadoNuevo = estadoNuevo; }

	public LocalDateTime getTimestamp() { return timestamp; }
	public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

	public String getMetadatos() { return metadatos; }
	public void setMetadatos(String metadatos) { this.metadatos = metadatos; }
}
