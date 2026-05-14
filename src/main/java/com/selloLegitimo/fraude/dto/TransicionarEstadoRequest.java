package com.selloLegitimo.fraude.dto;

import com.selloLegitimo.fraude.modelo.EstadoCaso;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Payload para PATCH /casos/{radicado}/estado.
 */
public class TransicionarEstadoRequest {

	@NotNull(message = "nuevoEstado es obligatorio")
	private EstadoCaso nuevoEstado;

	@Size(max = 2000, message = "motivo no puede superar 2000 caracteres")
	private String motivo;

	public EstadoCaso getNuevoEstado() { return nuevoEstado; }
	public void setNuevoEstado(EstadoCaso nuevoEstado) { this.nuevoEstado = nuevoEstado; }

	public String getMotivo() { return motivo; }
	public void setMotivo(String motivo) { this.motivo = motivo; }
}
