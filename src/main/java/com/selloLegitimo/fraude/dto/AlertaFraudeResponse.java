package com.selloLegitimo.fraude.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class AlertaFraudeResponse {

	private String alertUuid;
	private String typologyId;
	private String severityLevel;
	private Integer riskScore;
	private String riskScoreSource;
	private String status;
	private SourceReference sourceReference;
	private LogicalLocation logicalLocation;
	private Map<String, Object> contextMetadata;
	private LocalDateTime createdAt;

	public String getAlertUuid() { return alertUuid; }
	public void setAlertUuid(String alertUuid) { this.alertUuid = alertUuid; }

	public String getTypologyId() { return typologyId; }
	public void setTypologyId(String typologyId) { this.typologyId = typologyId; }

	public String getSeverityLevel() { return severityLevel; }
	public void setSeverityLevel(String severityLevel) { this.severityLevel = severityLevel; }

	public Integer getRiskScore() { return riskScore; }
	public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }

	public String getRiskScoreSource() { return riskScoreSource; }
	public void setRiskScoreSource(String riskScoreSource) { this.riskScoreSource = riskScoreSource; }

	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }

	public SourceReference getSourceReference() { return sourceReference; }
	public void setSourceReference(SourceReference sourceReference) { this.sourceReference = sourceReference; }

	public LogicalLocation getLogicalLocation() { return logicalLocation; }
	public void setLogicalLocation(LogicalLocation logicalLocation) { this.logicalLocation = logicalLocation; }

	public Map<String, Object> getContextMetadata() { return contextMetadata; }
	public void setContextMetadata(Map<String, Object> contextMetadata) { this.contextMetadata = contextMetadata; }

	public LocalDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

	public static class SourceReference {

		private String originEventId;
		private String verificationHash;
		private LocalDateTime certifiedTimestamp;
		private String originModule;

		public String getOriginEventId() { return originEventId; }
		public void setOriginEventId(String originEventId) { this.originEventId = originEventId; }

		public String getVerificationHash() { return verificationHash; }
		public void setVerificationHash(String verificationHash) { this.verificationHash = verificationHash; }

		public LocalDateTime getCertifiedTimestamp() { return certifiedTimestamp; }
		public void setCertifiedTimestamp(LocalDateTime certifiedTimestamp) { this.certifiedTimestamp = certifiedTimestamp; }

		public String getOriginModule() { return originModule; }
		public void setOriginModule(String originModule) { this.originModule = originModule; }
	}

	public static class LogicalLocation {

		private String tableId;
		private String pollingStation;
		private String constituency;
		private String channel;

		public String getTableId() { return tableId; }
		public void setTableId(String tableId) { this.tableId = tableId; }

		public String getPollingStation() { return pollingStation; }
		public void setPollingStation(String pollingStation) { this.pollingStation = pollingStation; }

		public String getConstituency() { return constituency; }
		public void setConstituency(String constituency) { this.constituency = constituency; }

		public String getChannel() { return channel; }
		public void setChannel(String channel) { this.channel = channel; }
	}
}
