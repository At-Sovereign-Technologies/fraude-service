package com.selloLegitimo.fraude.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.selloLegitimo.fraude.modelo.EntidadCompetente;
import com.selloLegitimo.fraude.modelo.EstadoCaso;
import com.selloLegitimo.fraude.modelo.NivelPrioridad;
import com.selloLegitimo.fraude.modelo.TipologiaFraude;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CasoInvestigacionResponse {

	private UUID radicado;
	private List<UUID> alertasOrigen;
	private TipologiaFraude tipologiaFraude;
	private NivelPrioridad nivelPrioridad;
	private EstadoCaso estado;
	private String responsableInstitucional;
	private EntidadCompetente entidadCompetente;
	private LocalDateTime creadoEn;
	private String creadoPor;
	private LocalDateTime cerradoEn;
	private UUID casoPrecedente;
	private List<RegistroAuditoriaResponse> auditoria;

	public UUID getRadicado() { return radicado; }
	public void setRadicado(UUID radicado) { this.radicado = radicado; }

	public List<UUID> getAlertasOrigen() { return alertasOrigen; }
	public void setAlertasOrigen(List<UUID> alertasOrigen) { this.alertasOrigen = alertasOrigen; }

	public TipologiaFraude getTipologiaFraude() { return tipologiaFraude; }
	public void setTipologiaFraude(TipologiaFraude tipologiaFraude) { this.tipologiaFraude = tipologiaFraude; }

	public NivelPrioridad getNivelPrioridad() { return nivelPrioridad; }
	public void setNivelPrioridad(NivelPrioridad nivelPrioridad) { this.nivelPrioridad = nivelPrioridad; }

	public EstadoCaso getEstado() { return estado; }
	public void setEstado(EstadoCaso estado) { this.estado = estado; }

	public String getResponsableInstitucional() { return responsableInstitucional; }
	public void setResponsableInstitucional(String responsableInstitucional) {
		this.responsableInstitucional = responsableInstitucional;
	}

	public EntidadCompetente getEntidadCompetente() { return entidadCompetente; }
	public void setEntidadCompetente(EntidadCompetente entidadCompetente) {
		this.entidadCompetente = entidadCompetente;
	}

	public LocalDateTime getCreadoEn() { return creadoEn; }
	public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

	public String getCreadoPor() { return creadoPor; }
	public void setCreadoPor(String creadoPor) { this.creadoPor = creadoPor; }

	public LocalDateTime getCerradoEn() { return cerradoEn; }
	public void setCerradoEn(LocalDateTime cerradoEn) { this.cerradoEn = cerradoEn; }

	public UUID getCasoPrecedente() { return casoPrecedente; }
	public void setCasoPrecedente(UUID casoPrecedente) { this.casoPrecedente = casoPrecedente; }

	public List<RegistroAuditoriaResponse> getAuditoria() { return auditoria; }
	public void setAuditoria(List<RegistroAuditoriaResponse> auditoria) { this.auditoria = auditoria; }
}
