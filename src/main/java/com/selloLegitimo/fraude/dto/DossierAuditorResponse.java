package com.selloLegitimo.fraude.dto;

import java.util.List;

public class DossierAuditorResponse {

    private String electionStatus;
    private int totalClosedCases;
    private int confirmedFraudCount;
    private int dismissedCount;
    private List<CierreCasoResponse> closures;
    private List<AuditEntryResponse> auditChain;
    private boolean chainIntegrityVerified;

    public String getElectionStatus() { return electionStatus; }
    public void setElectionStatus(String electionStatus) { this.electionStatus = electionStatus; }

    public int getTotalClosedCases() { return totalClosedCases; }
    public void setTotalClosedCases(int totalClosedCases) { this.totalClosedCases = totalClosedCases; }

    public int getConfirmedFraudCount() { return confirmedFraudCount; }
    public void setConfirmedFraudCount(int confirmedFraudCount) { this.confirmedFraudCount = confirmedFraudCount; }

    public int getDismissedCount() { return dismissedCount; }
    public void setDismissedCount(int dismissedCount) { this.dismissedCount = dismissedCount; }

    public List<CierreCasoResponse> getClosures() { return closures; }
    public void setClosures(List<CierreCasoResponse> closures) { this.closures = closures; }

    public List<AuditEntryResponse> getAuditChain() { return auditChain; }
    public void setAuditChain(List<AuditEntryResponse> auditChain) { this.auditChain = auditChain; }

    public boolean isChainIntegrityVerified() { return chainIntegrityVerified; }
    public void setChainIntegrityVerified(boolean chainIntegrityVerified) { this.chainIntegrityVerified = chainIntegrityVerified; }
}
