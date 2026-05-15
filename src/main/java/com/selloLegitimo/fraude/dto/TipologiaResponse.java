package com.selloLegitimo.fraude.dto;

import com.selloLegitimo.fraude.modelo.Tipologia;
import java.time.LocalDateTime;

public class TipologiaResponse {

    private String id;
    private String name;
    private String description;
    private String defaultSeverity;
    private Boolean requiresReview;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer version;

    public static TipologiaResponse fromEntity(Tipologia t) {
        TipologiaResponse r = new TipologiaResponse();
        r.id = t.getId();
        r.name = t.getName();
        r.description = t.getDescription();
        r.defaultSeverity = t.getDefaultSeverity();
        r.requiresReview = t.getRequiresReview();
        r.createdAt = t.getCreatedAt();
        r.updatedAt = t.getUpdatedAt();
        r.version = t.getVersion();
        return r;
    }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
}
