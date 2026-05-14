package com.selloLegitimo.fraude.dto;

import com.selloLegitimo.fraude.modelo.EntidadCompetente;
import com.selloLegitimo.fraude.modelo.NivelPrioridad;
import com.selloLegitimo.fraude.modelo.TipologiaFraude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

/**
 * Payload de creacion de un caso de investigacion a partir de una o mas alertas.
 */
public class CrearCasoRequest {

	@NotEmpty(message = "alertasOrigen no puede estar vacio")
	private List<UUID> alertasOrigen;

	@NotNull(message = "tipologiaFraude es obligatorio")
	private TipologiaFraude tipologiaFraude;

	@NotNull(message = "nivelPrioridad es obligatorio")
	private NivelPrioridad nivelPrioridad;

	@NotNull(message = "responsableInstitucional es obligatorio")
	@Size(min = 1, max = 100, message = "responsableInstitucional debe tener entre 1 y 100 caracteres")
	private String responsableInstitucional;

	@NotNull(message = "entidadCompetente es obligatorio")
	private EntidadCompetente entidadCompetente;

	private UUID casoPrecedente;

	public List<UUID> getAlertasOrigen() { return alertasOrigen; }
	public void setAlertasOrigen(List<UUID> alertasOrigen) { this.alertasOrigen = alertasOrigen; }

	public TipologiaFraude getTipologiaFraude() { return tipologiaFraude; }
	public void setTipologiaFraude(TipologiaFraude tipologiaFraude) { this.tipologiaFraude = tipologiaFraude; }

	public NivelPrioridad getNivelPrioridad() { return nivelPrioridad; }
	public void setNivelPrioridad(NivelPrioridad nivelPrioridad) { this.nivelPrioridad = nivelPrioridad; }

	public String getResponsableInstitucional() { return responsableInstitucional; }
	public void setResponsableInstitucional(String responsableInstitucional) {
		this.responsableInstitucional = responsableInstitucional;
	}

	public EntidadCompetente getEntidadCompetente() { return entidadCompetente; }
	public void setEntidadCompetente(EntidadCompetente entidadCompetente) {
		this.entidadCompetente = entidadCompetente;
	}

	public UUID getCasoPrecedente() { return casoPrecedente; }
	public void setCasoPrecedente(UUID casoPrecedente) { this.casoPrecedente = casoPrecedente; }
}
