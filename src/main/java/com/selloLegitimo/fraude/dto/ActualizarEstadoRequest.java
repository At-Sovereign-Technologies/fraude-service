package com.selloLegitimo.fraude.dto;

import com.selloLegitimo.fraude.modelo.EstadoAlerta;
import jakarta.validation.constraints.NotNull;

public class ActualizarEstadoRequest {

    @NotNull(message = "status es obligatorio")
    private EstadoAlerta status;

    private String assignedTo;

    private String resolutionNotes;

    private String actorId;

    public EstadoAlerta getStatus() { return status; }
    public void setStatus(EstadoAlerta status) { this.status = status; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }

    public String getActorId() { return actorId; }
    public void setActorId(String actorId) { this.actorId = actorId; }
}
