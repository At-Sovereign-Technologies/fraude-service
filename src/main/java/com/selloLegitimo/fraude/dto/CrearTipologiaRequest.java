package com.selloLegitimo.fraude.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CrearTipologiaRequest {

    @NotBlank(message = "El id de tipologia es obligatorio")
    @Size(max = 40, message = "El id no debe exceder 40 caracteres")
    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150)
    private String name;

    @Size(max = 500)
    private String description;

    @NotBlank
    @Pattern(regexp = "^(INFORMATIONAL|SUSPICIOUS|CRITICAL)$",
        message = "Severidad debe ser INFORMATIONAL, SUSPICIOUS o CRITICAL")
    private String defaultSeverity;

    private Boolean requiresReview;

    private String justification;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDefaultSeverity() { return defaultSeverity; }
    public void setDefaultSeverity(String defaultSeverity) { this.defaultSeverity = defaultSeverity; }

    public Boolean getRequiresReview() { return requiresReview; }
    public void setRequiresReview(Boolean requiresReview) { this.requiresReview = requiresReview; }

    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }
}
