package com.selloLegitimo.fraude.dto;

import jakarta.validation.constraints.Size;

public class ActualizarEstadoRequest {

	private String status;

	@Size(max = 100, message = "assignedTo no puede superar 100 caracteres")
	private String assignedTo;

	@Size(max = 2000, message = "resolutionNotes no puede superar 2000 caracteres")
	private String resolutionNotes;

	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }

	public String getAssignedTo() { return assignedTo; }
	public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

	public String getResolutionNotes() { return resolutionNotes; }
	public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
}
