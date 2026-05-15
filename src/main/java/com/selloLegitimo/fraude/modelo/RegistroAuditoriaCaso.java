package com.selloLegitimo.fraude.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "case_audit_log", schema = "fraude")
public class RegistroAuditoriaCaso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alert_uuid", nullable = false)
    private UUID alertUuid;

    @Column(name = "actor_id", nullable = false, length = 100)
    private String actorId;

    @Column(name = "role", length = 50)
    private String role;

    @Column(name = "from_status", length = 30)
    private String fromStatus;

    @Column(name = "to_status", nullable = false, length = 30)
    private String toStatus;

    @Column(name = "transition_timestamp", nullable = false)
    private LocalDateTime transitionTimestamp;

    @Column(name = "record_hash", length = 64)
    private String recordHash;

    @Column(name = "previous_hash", length = 64)
    private String previousHash;

    @Column(name = "closure_uuid")
    private UUID closureUuid;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "JSONB")
    private String metadata;

    @PrePersist
    public void prePersist() {
        if (transitionTimestamp == null) {
            transitionTimestamp = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UUID getAlertUuid() { return alertUuid; }
    public void setAlertUuid(UUID alertUuid) { this.alertUuid = alertUuid; }

    public String getActorId() { return actorId; }
    public void setActorId(String actorId) { this.actorId = actorId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getFromStatus() { return fromStatus; }
    public void setFromStatus(String fromStatus) { this.fromStatus = fromStatus; }

    public String getToStatus() { return toStatus; }
    public void setToStatus(String toStatus) { this.toStatus = toStatus; }

    public LocalDateTime getTransitionTimestamp() { return transitionTimestamp; }
    public void setTransitionTimestamp(LocalDateTime transitionTimestamp) { this.transitionTimestamp = transitionTimestamp; }

    public String getRecordHash() { return recordHash; }
    public void setRecordHash(String recordHash) { this.recordHash = recordHash; }

    public String getPreviousHash() { return previousHash; }
    public void setPreviousHash(String previousHash) { this.previousHash = previousHash; }

    public UUID getClosureUuid() { return closureUuid; }
    public void setClosureUuid(UUID closureUuid) { this.closureUuid = closureUuid; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
}
