package com.selloLegitimo.fraude.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "evidence_references", schema = "fraude")
public class EvidenciaReferencia {

    @Id
    private UUID id;

    @Column(name = "alert_uuid", nullable = false)
    private UUID alertUuid;

    @Column(name = "reference_id", nullable = false, length = 255)
    private String referenceId;

    @Column(name = "hash_signature", nullable = false, length = 64)
    private String hashSignature;

    @Column(name = "original_timestamp", nullable = false)
    private LocalDateTime originalTimestamp;

    @Column(name = "verified", nullable = false)
    private Boolean verified;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (verified == null) {
            verified = false;
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getAlertUuid() { return alertUuid; }
    public void setAlertUuid(UUID alertUuid) { this.alertUuid = alertUuid; }

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    public String getHashSignature() { return hashSignature; }
    public void setHashSignature(String hashSignature) { this.hashSignature = hashSignature; }

    public LocalDateTime getOriginalTimestamp() { return originalTimestamp; }
    public void setOriginalTimestamp(LocalDateTime originalTimestamp) { this.originalTimestamp = originalTimestamp; }

    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }

    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
