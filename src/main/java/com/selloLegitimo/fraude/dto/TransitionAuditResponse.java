package com.selloLegitimo.fraude.dto;

import java.time.LocalDateTime;

public class TransitionAuditResponse {

    private String mockAuditLogId;
    private LocalDateTime timestamp;
    private String fromStatus;
    private String toStatus;
    private String actorId;

    public String getMockAuditLogId() { return mockAuditLogId; }
    public void setMockAuditLogId(String mockAuditLogId) { this.mockAuditLogId = mockAuditLogId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getFromStatus() { return fromStatus; }
    public void setFromStatus(String fromStatus) { this.fromStatus = fromStatus; }

    public String getToStatus() { return toStatus; }
    public void setToStatus(String toStatus) { this.toStatus = toStatus; }

    public String getActorId() { return actorId; }
    public void setActorId(String actorId) { this.actorId = actorId; }
}
