package com.selloLegitimo.fraude.dto;

import java.util.Map;

public class MetricasAlertasResponse {

	private long totalAlerts;
	private Map<String, Long> bySeverity;
	private Map<String, Long> byStatus;
	private Map<String, Long> byTypology;

	public long getTotalAlerts() { return totalAlerts; }
	public void setTotalAlerts(long totalAlerts) { this.totalAlerts = totalAlerts; }

	public Map<String, Long> getBySeverity() { return bySeverity; }
	public void setBySeverity(Map<String, Long> bySeverity) { this.bySeverity = bySeverity; }

	public Map<String, Long> getByStatus() { return byStatus; }
	public void setByStatus(Map<String, Long> byStatus) { this.byStatus = byStatus; }

	public Map<String, Long> getByTypology() { return byTypology; }
	public void setByTypology(Map<String, Long> byTypology) { this.byTypology = byTypology; }
}
