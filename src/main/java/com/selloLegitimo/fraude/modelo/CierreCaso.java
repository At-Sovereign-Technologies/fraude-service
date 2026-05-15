package com.selloLegitimo.fraude.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "case_closure", schema = "fraude")
public class CierreCaso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alert_uuid", nullable = false, unique = true)
    private UUID alertUuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "final_result", nullable = false, length = 20)
    private ResultadoFinal finalResult;

    @Column(name = "justification", nullable = false, columnDefinition = "TEXT")
    private String justification;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "institutional_actions", nullable = false, columnDefinition = "JSONB")
    private String institutionalActions;

    @Column(name = "responsible_entity", nullable = false, length = 200)
    private String responsibleEntity;

    @Column(name = "actor_id", nullable = false, length = 100)
    private String actorId;

    @Column(name = "actor_role", nullable = false, length = 50)
    private String actorRole;

    @Column(name = "closure_timestamp", nullable = false)
    private LocalDateTime closureTimestamp;

    @Column(name = "signature", nullable = false, length = 128)
    private String signature;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (closureTimestamp == null) {
            closureTimestamp = LocalDateTime.now();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UUID getAlertUuid() { return alertUuid; }
    public void setAlertUuid(UUID alertUuid) { this.alertUuid = alertUuid; }

    public ResultadoFinal getFinalResult() { return finalResult; }
    public void setFinalResult(ResultadoFinal finalResult) { this.finalResult = finalResult; }

    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }

    public String getInstitutionalActions() { return institutionalActions; }
    public void setInstitutionalActions(String institutionalActions) { this.institutionalActions = institutionalActions; }

    public String getResponsibleEntity() { return responsibleEntity; }
    public void setResponsibleEntity(String responsibleEntity) { this.responsibleEntity = responsibleEntity; }

    public String getActorId() { return actorId; }
    public void setActorId(String actorId) { this.actorId = actorId; }

    public String getActorRole() { return actorRole; }
    public void setActorRole(String actorRole) { this.actorRole = actorRole; }

    public LocalDateTime getClosureTimestamp() { return closureTimestamp; }
    public void setClosureTimestamp(LocalDateTime closureTimestamp) { this.closureTimestamp = closureTimestamp; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
