package com.selloLegitimo.fraude.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.selloLegitimo.fraude.modelo.EstadoCaso;
import com.selloLegitimo.fraude.modelo.RolActorControl;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegistroAuditoriaResponse {

	private UUID id;
	private UUID radicadoCaso;
	private String actorId;
	private RolActorControl rolActor;
	private String accion;
	private EstadoCaso estadoAnterior;
	private EstadoCaso estadoNuevo;
	private LocalDateTime timestamp;
	private String metadatos;

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
