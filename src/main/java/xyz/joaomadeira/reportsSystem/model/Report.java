package xyz.joaomadeira.reportsSystem.model;

public class Report {

    public enum Status {
        PENDING,
        RESOLVED
    }

    private final String id;
    private final String reporterName;
    private final String reportedName;
    private final String reason;
    private final long timestamp;
    private Status status;
    private String resolvedBy;

    public Report(String id, String reporterName, String reportedName, String reason, long timestamp) {
        this.id = id;
        this.reporterName = reporterName;
        this.reportedName = reportedName;
        this.reason = reason;
        this.timestamp = timestamp;
        this.status = Status.PENDING;
        this.resolvedBy = null;
    }

    public String getId() {
        return id;
    }

    public String getReporterName() {
        return reporterName;
    }

    public String getReportedName() {
        return reportedName;
    }

    public String getReason() {
        return reason;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getResolvedBy() {
        return resolvedBy;
    }

    public void setResolvedBy(String resolvedBy) {
        this.resolvedBy = resolvedBy;
    }
}
