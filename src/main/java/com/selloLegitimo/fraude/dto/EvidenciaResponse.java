package com.selloLegitimo.fraude.dto;

import com.selloLegitimo.fraude.modelo.EvidenciaReferencia;
import java.time.LocalDateTime;
import java.util.UUID;

public class EvidenciaResponse {

    private UUID id;
    private UUID alertUuid;
    private String referenceId;
    private String hashSignature;
    private LocalDateTime originalTimestamp;
    private Boolean verified;
    private LocalDateTime verifiedAt;
    private LocalDateTime createdAt;

    public static EvidenciaResponse fromEntity(EvidenciaReferencia e) {
        EvidenciaResponse r = new EvidenciaResponse();
        r.id = e.getId();
        r.alertUuid = e.getAlertUuid();
        r.referenceId = e.getReferenceId();
        r.hashSignature = e.getHashSignature();
        r.originalTimestamp = e.getOriginalTimestamp();
        r.verified = e.getVerified();
        r.verifiedAt = e.getVerifiedAt();
        r.createdAt = e.getCreatedAt();
        return r;
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
