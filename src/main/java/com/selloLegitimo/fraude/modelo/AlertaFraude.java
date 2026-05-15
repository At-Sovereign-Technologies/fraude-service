package com.selloLegitimo.fraude.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "fraud_alerts", schema = "fraude")
public class AlertaFraude {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "alert_uuid", nullable = false, unique = true)
	private UUID alertUuid;

	@Column(name = "typology_id", nullable = false, length = 40)
	private String typologyId;

	@Column(name = "severity_level", nullable = false, length = 20)
	private String severityLevel;

	@Column(name = "risk_score", nullable = false)
	private Integer riskScore;

	@Column(name = "risk_score_source", nullable = false, length = 10)
	private String riskScoreSource;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 30)
	private EstadoAlerta status;

	@Column(name = "origin_module", nullable = false, length = 20)
	private String originModule;

	@Column(name = "origin_event_id", nullable = false, length = 100)
	private String originEventId;

	@Column(name = "verification_hash", nullable = false, length = 64)
	private String verificationHash;

	@Column(name = "certified_timestamp", nullable = false)
	private LocalDateTime certifiedTimestamp;

	@Column(name = "table_id", length = 100)
	private String tableId;

	@Column(name = "polling_station", length = 100)
	private String pollingStation;

	@Column(name = "constituency", length = 100)
	private String constituency;

	@Column(name = "channel", length = 20)
	private String channel;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "context_metadata", columnDefinition = "JSONB")
	private String contextMetadata;

	@Column(name = "assigned_to", length = 100)
	private String assignedTo;

	@Column(name = "resolution_notes", columnDefinition = "TEXT")
	private String resolutionNotes;

	@Column(name = "resolved_at")
	private LocalDateTime resolvedAt;

	@Column(name = "resolved_by", length = 100)
	private String resolvedBy;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Column(name = "closed_at")
	private LocalDateTime closedAt;

	@Column(name = "closed_by", length = 100)
	private String closedBy;

	@Column(name = "last_actor_id", length = 100)
	private String lastActorId;

	@Column(name = "last_transition_at")
	private LocalDateTime lastTransitionAt;

	@PrePersist
	public void prePersist() {
		if (alertUuid == null) {
			alertUuid = UUID.randomUUID();
		}
		if (status == null) {
			status = EstadoAlerta.DETECTADO;
		}
		if (riskScore == null) {
			riskScore = 0;
		}
		if (riskScoreSource == null) {
			riskScoreSource = "DEFAULT";
		}
		if (channel == null) {
			channel = "UNKNOWN";
		}
		LocalDateTime ahora = LocalDateTime.now();
		if (createdAt == null) {
			createdAt = ahora;
		}
		if (updatedAt == null) {
			updatedAt = ahora;
		}
	}

	@PreUpdate
	public void preUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public UUID getAlertUuid() { return alertUuid; }
	public void setAlertUuid(UUID alertUuid) { this.alertUuid = alertUuid; }

	public String getTypologyId() { return typologyId; }
	public void setTypologyId(String typologyId) { this.typologyId = typologyId; }

	public String getSeverityLevel() { return severityLevel; }
	public void setSeverityLevel(String severityLevel) { this.severityLevel = severityLevel; }

	public Integer getRiskScore() { return riskScore; }
	public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }

	public String getRiskScoreSource() { return riskScoreSource; }
	public void setRiskScoreSource(String riskScoreSource) { this.riskScoreSource = riskScoreSource; }

	public EstadoAlerta getStatus() { return status; }
	public void setStatus(EstadoAlerta status) { this.status = status; }

	public String getOriginModule() { return originModule; }
	public void setOriginModule(String originModule) { this.originModule = originModule; }

	public String getOriginEventId() { return originEventId; }
	public void setOriginEventId(String originEventId) { this.originEventId = originEventId; }

	public String getVerificationHash() { return verificationHash; }
	public void setVerificationHash(String verificationHash) { this.verificationHash = verificationHash; }

	public LocalDateTime getCertifiedTimestamp() { return certifiedTimestamp; }
	public void setCertifiedTimestamp(LocalDateTime certifiedTimestamp) { this.certifiedTimestamp = certifiedTimestamp; }

	public String getTableId() { return tableId; }
	public void setTableId(String tableId) { this.tableId = tableId; }

	public String getPollingStation() { return pollingStation; }
	public void setPollingStation(String pollingStation) { this.pollingStation = pollingStation; }

	public String getConstituency() { return constituency; }
	public void setConstituency(String constituency) { this.constituency = constituency; }

	public String getChannel() { return channel; }
	public void setChannel(String channel) { this.channel = channel; }

	public String getContextMetadata() { return contextMetadata; }
	public void setContextMetadata(String contextMetadata) { this.contextMetadata = contextMetadata; }

	public String getAssignedTo() { return assignedTo; }
	public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

	public String getResolutionNotes() { return resolutionNotes; }
	public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }

	public LocalDateTime getResolvedAt() { return resolvedAt; }
	public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

	public String getResolvedBy() { return resolvedBy; }
	public void setResolvedBy(String resolvedBy) { this.resolvedBy = resolvedBy; }

	public LocalDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

	public LocalDateTime getUpdatedAt() { return updatedAt; }
	public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

	public LocalDateTime getClosedAt() { return closedAt; }
	public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }

	public String getClosedBy() { return closedBy; }
	public void setClosedBy(String closedBy) { this.closedBy = closedBy; }

	public String getLastActorId() { return lastActorId; }
	public void setLastActorId(String lastActorId) { this.lastActorId = lastActorId; }

	public LocalDateTime getLastTransitionAt() { return lastTransitionAt; }
	public void setLastTransitionAt(LocalDateTime lastTransitionAt) { this.lastTransitionAt = lastTransitionAt; }
}
