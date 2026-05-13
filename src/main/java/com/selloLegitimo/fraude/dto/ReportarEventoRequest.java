package com.selloLegitimo.fraude.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.Map;

public class ReportarEventoRequest {

	@NotBlank(message = "source es obligatorio")
	private String source;

	@NotBlank(message = "eventType es obligatorio")
	private String eventType;

	@NotBlank(message = "originEventId es obligatorio")
	private String originEventId;

	@NotBlank(message = "verificationHash es obligatorio")
	@Pattern(regexp = "^[a-f0-9]{64}$", message = "verificationHash debe ser un SHA-256 hex de 64 caracteres")
	private String verificationHash;

	@NotNull(message = "certifiedTimestamp es obligatorio")
	private LocalDateTime certifiedTimestamp;

	@Valid
	private LogicalLocation logicalLocation;

	private Map<String, Object> metadata;

	public String getSource() { return source; }
	public void setSource(String source) { this.source = source; }

	public String getEventType() { return eventType; }
	public void setEventType(String eventType) { this.eventType = eventType; }

	public String getOriginEventId() { return originEventId; }
	public void setOriginEventId(String originEventId) { this.originEventId = originEventId; }

	public String getVerificationHash() { return verificationHash; }
	public void setVerificationHash(String verificationHash) { this.verificationHash = verificationHash; }

	public LocalDateTime getCertifiedTimestamp() { return certifiedTimestamp; }
	public void setCertifiedTimestamp(LocalDateTime certifiedTimestamp) { this.certifiedTimestamp = certifiedTimestamp; }

	public LogicalLocation getLogicalLocation() { return logicalLocation; }
	public void setLogicalLocation(LogicalLocation logicalLocation) { this.logicalLocation = logicalLocation; }

	public Map<String, Object> getMetadata() { return metadata; }
	public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

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
