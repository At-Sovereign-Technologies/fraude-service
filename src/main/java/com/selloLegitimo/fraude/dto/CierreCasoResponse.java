package com.selloLegitimo.fraude.dto;

import java.time.LocalDateTime;

public class CierreCasoResponse {

    private String alertUuid;
    private String finalResult;
    private String justification;
    private String[] institutionalActions;
    private String responsibleEntity;
    private String actorId;
    private String actorRole;
    private LocalDateTime closureTimestamp;
    private String signature;

    public String getAlertUuid() { return alertUuid; }
    public void setAlertUuid(String alertUuid) { this.alertUuid = alertUuid; }

    public String getFinalResult() { return finalResult; }
    public void setFinalResult(String finalResult) { this.finalResult = finalResult; }

    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }

    public String[] getInstitutionalActions() { return institutionalActions; }
    public void setInstitutionalActions(String[] institutionalActions) { this.institutionalActions = institutionalActions; }

    public String getResponsibleEntity() { return responsibleEntity; }
    public void setResponsibleEntity(String responsibleEntity) { this.responsibleEntity = responsibleEntity; }

    public String getActorId() { return actorId; }
    public void setActorId(String actorId) { this.actorId = actorId; }

    public String getActorRole() { return actorRole; }
    public void setActorRole(String actorRole) { this.actorRole = actorRole; }

    public LocalDateTime getClosureTimestamp() { return closureTimestamp; }
    public void setClosureTimestamp(LocalDateTime closureTimestamp) { this.closureTimestamp = closureTimestamp; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
}
