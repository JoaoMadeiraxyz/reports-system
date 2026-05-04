package xyz.joaomadeira.reportsSystem.storage;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import xyz.joaomadeira.reportsSystem.ReportsSystem;
import xyz.joaomadeira.reportsSystem.model.Report;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReportStorage {

    private final ReportsSystem plugin;
    private final File dataFile;
    private final List<Report> reports;

    public ReportStorage(ReportsSystem plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "reports.json");
        this.reports = new ArrayList<>();
    }

    public void load() {
        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
                FileWriter writer = new FileWriter(dataFile);
                writer.write("[]");
                writer.close();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create reports.json: " + e.getMessage());
            }
            return;
        }

        if (dataFile.length() == 0) {
            return;
        }

        try {
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(dataFile));
            if (jsonArray == null) return;

            for (Object obj : jsonArray) {
                JSONObject json = (JSONObject) obj;
                String id = (String) json.get("id");
                String reporterName = (String) json.get("reporter");
                String reportedName = (String) json.get("reported");
                String reason = (String) json.get("reason");
                Object tsObj = json.get("timestamp");

                if (id == null || reporterName == null || reportedName == null || reason == null || tsObj == null || !(tsObj instanceof Number)) {
                    plugin.getLogger().warning("Skipping malformed report entry: " + json.toJSONString());
                    continue;
                }

                long timestamp = ((Number) tsObj).longValue();

                Report report = new Report(id, reporterName, reportedName, reason, timestamp);

                String statusStr = (String) json.get("status");
                if ("RESOLVED".equals(statusStr)) {
                    report.setStatus(Report.Status.RESOLVED);
                }

                String resolvedBy = (String) json.get("resolvedBy");
                if (resolvedBy != null) {
                    report.setResolvedBy(resolvedBy);
                }

                reports.add(report);
            }

            plugin.getLogger().info("Loaded " + reports.size() + " reports from file.");
        } catch (Exception e) {
            plugin.getLogger().severe("Could not load reports.json: " + e.getMessage());
        }
    }

    public void save() {
        try {
            JSONArray jsonArray = new JSONArray();

            for (Report report : reports) {
                JSONObject json = new JSONObject();
                json.put("id", report.getId());
                json.put("reporter", report.getReporterName());
                json.put("reported", report.getReportedName());
                json.put("reason", report.getReason());
                json.put("timestamp", report.getTimestamp());
                json.put("status", report.getStatus().name());
                if (report.getResolvedBy() != null) {
                    json.put("resolvedBy", report.getResolvedBy());
                }
                jsonArray.add(json);
            }

            FileWriter writer = new FileWriter(dataFile);
            writer.write(jsonArray.toJSONString());
            writer.close();
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save reports.json: " + e.getMessage());
        }
    }

    public void addReport(Report report) {
        reports.add(report);
        save();
    }

    public List<Report> getPendingReports() {
        List<Report> pending = new ArrayList<>();
        for (Report report : reports) {
            if (report.getStatus() == Report.Status.PENDING) {
                pending.add(report);
            }
        }
        return pending;
    }

    public List<Report> getAllReports() {
        return new ArrayList<>(reports);
    }

    public Report getReport(String id) {
        for (Report report : reports) {
            if (report.getId().equals(id)) {
                return report;
            }
        }
        return null;
    }

    public void resolveReport(String id, String resolvedBy) {
        Report report = getReport(id);
        if (report != null) {
            report.setStatus(Report.Status.RESOLVED);
            report.setResolvedBy(resolvedBy);
            save();
        }
    }
}
