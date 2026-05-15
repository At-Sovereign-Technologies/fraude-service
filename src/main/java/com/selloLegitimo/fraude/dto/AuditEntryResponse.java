package com.selloLegitimo.fraude.dto;

import java.time.LocalDateTime;

public class AuditEntryResponse {

    private Long id;
    private String alertUuid;
    private String actorId;
    private String role;
    private String fromStatus;
    private String toStatus;
    private LocalDateTime transitionTimestamp;
    private String recordHash;
    private String previousHash;
    private String closureUuid;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAlertUuid() { return alertUuid; }
    public void setAlertUuid(String alertUuid) { this.alertUuid = alertUuid; }

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

    public String getClosureUuid() { return closureUuid; }
    public void setClosureUuid(String closureUuid) { this.closureUuid = closureUuid; }
}
