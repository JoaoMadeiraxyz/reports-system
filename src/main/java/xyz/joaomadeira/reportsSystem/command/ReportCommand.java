package xyz.joaomadeira.reportsSystem.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.joaomadeira.reportsSystem.ReportsSystem;
import xyz.joaomadeira.reportsSystem.model.Report;
import xyz.joaomadeira.reportsSystem.storage.ReportStorage;

import java.util.UUID;

public class ReportCommand implements CommandExecutor {

    private final ReportsSystem plugin;
    private final ReportStorage reportStorage;

    public ReportCommand(ReportsSystem plugin, ReportStorage reportStorage) {
        this.plugin = plugin;
        this.reportStorage = reportStorage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            String msg = plugin.getConfigManager().getString("messages.console-only");
            if (msg != null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            String msg = plugin.getConfigManager().getString("messages.usage-report");
            if (msg != null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
            return true;
        }

        String targetName = args[0];
        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reasonBuilder.append(args[i]);
            if (i < args.length - 1) {
                reasonBuilder.append(" ");
            }
        }
        String reason = reasonBuilder.toString();

        if (player.getName().equalsIgnoreCase(targetName)) {
            String msg = plugin.getConfigManager().getString("messages.self-report");
            if (msg != null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
            return true;
        }

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            String msg = plugin.getConfigManager().getString("messages.player-not-found");
            if (msg != null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
            return true;
        }

        for (Report report : reportStorage.getAllReports()) {
            if (report.getReportedName().equalsIgnoreCase(targetName)
                && report.getReporterName().equalsIgnoreCase(player.getName())
                && report.getStatus() == Report.Status.PENDING) {
                String msg = plugin.getConfigManager().getString("messages.already-reported");
                if (msg != null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                }
                return true;
            }
        }

        String id = UUID.randomUUID().toString();
        Report report = new Report(id, player.getName(), target.getName(), reason, System.currentTimeMillis());
        reportStorage.addReport(report);

        String msg = plugin.getConfigManager().getString("messages.report-submitted");
        if (msg != null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        }

        return true;
    }
}
