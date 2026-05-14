package com.selloLegitimo.fraude.modelo;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Caso de Investigacion Formal (SR-M6).
 *
 * <p>Consolida una o mas alertas bajo un expediente unico con ciclo de vida
 * controlado por {@link EstadoCaso}. Una vez en estado {@link EstadoCaso#CERRADO}
 * el caso es inmutable a nivel de aplicacion y de base de datos (trigger).</p>
 */
@Entity
@Table(name = "casos_investigacion", schema = "fraude")
public class CasoInvestigacion {

	@Id
	@Column(name = "radicado", nullable = false, updatable = false)
	private UUID radicado;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(
		name = "caso_alertas_origen",
		schema = "fraude",
		joinColumns = @JoinColumn(name = "radicado"))
	@Column(name = "alerta_uuid", nullable = false)
	private List<UUID> alertasOrigen = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	@Column(name = "tipologia_fraude", nullable = false, length = 40)
	private TipologiaFraude tipologiaFraude;

	@Enumerated(EnumType.STRING)
	@Column(name = "nivel_prioridad", nullable = false, length = 20)
	private NivelPrioridad nivelPrioridad;

	@Enumerated(EnumType.STRING)
	@Column(name = "estado", nullable = false, length = 40)
	private EstadoCaso estado;

	@Column(name = "responsable_institucional", nullable = false, length = 100)
	private String responsableInstitucional;

	@Enumerated(EnumType.STRING)
	@Column(name = "entidad_competente", nullable = false, length = 40)
	private EntidadCompetente entidadCompetente;

	@Column(name = "creado_en", nullable = false, updatable = false)
	private LocalDateTime creadoEn;

	@Column(name = "creado_por", nullable = false, updatable = false, length = 100)
	private String creadoPor;

	@Column(name = "cerrado_en")
	private LocalDateTime cerradoEn;

	@Column(name = "caso_precedente")
	private UUID casoPrecedente;

	@PrePersist
	public void prePersist() {
		if (radicado == null) {
			radicado = UUID.randomUUID();
		}
		if (estado == null) {
			estado = EstadoCaso.DETECTADO;
		}
		if (creadoEn == null) {
			creadoEn = LocalDateTime.now(ZoneOffset.UTC);
		}
	}

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
}
