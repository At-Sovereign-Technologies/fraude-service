package com.selloLegitimo.fraude.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;

@Entity
@Table(name = "typology_catalog", schema = "fraude")
public class Tipologia {

	@Id
	@Column(name = "id", length = 40)
	private String id;

	@Column(name = "name", nullable = false, length = 150)
	private String name;

	@Column(name = "description", length = 500)
	private String description;

	@Column(name = "default_severity", nullable = false, length = 20)
	private String defaultSeverity;

	@Column(name = "requires_review", nullable = false)
	private Boolean requiresReview;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Version
	@Column(name = "version", nullable = false)
	private Integer version;

	public Tipologia() {}

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
