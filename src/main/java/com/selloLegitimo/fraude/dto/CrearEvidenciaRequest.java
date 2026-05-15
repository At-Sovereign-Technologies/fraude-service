package com.selloLegitimo.fraude.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.UUID;

public class CrearEvidenciaRequest {

    @NotNull(message = "alertUuid es obligatorio")
    private UUID alertUuid;

    @NotBlank(message = "referenceId es obligatorio")
    private String referenceId;

    @NotBlank(message = "hashSignature es obligatorio")
    @Pattern(regexp = "^[a-f0-9]{64}$", message = "hashSignature debe ser un SHA-256 hex de 64 caracteres")
    private String hashSignature;

    @NotNull(message = "originalTimestamp es obligatorio")
    private LocalDateTime originalTimestamp;

    public UUID getAlertUuid() { return alertUuid; }
    public void setAlertUuid(UUID alertUuid) { this.alertUuid = alertUuid; }

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    public String getHashSignature() { return hashSignature; }
    public void setHashSignature(String hashSignature) { this.hashSignature = hashSignature; }

    public LocalDateTime getOriginalTimestamp() { return originalTimestamp; }
    public void setOriginalTimestamp(LocalDateTime originalTimestamp) { this.originalTimestamp = originalTimestamp; }
}
