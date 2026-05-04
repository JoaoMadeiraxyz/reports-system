package xyz.joaomadeira.reportsSystem.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.joaomadeira.reportsSystem.ReportsSystem;
import xyz.joaomadeira.reportsSystem.gui.GuiBuilder;
import xyz.joaomadeira.reportsSystem.gui.GuiManager;
import xyz.joaomadeira.reportsSystem.gui.PaginatedGui;

public class ReportsCommand implements CommandExecutor {

    private final ReportsSystem plugin;
    private final GuiManager guiManager;
    private final GuiBuilder guiBuilder;

    public ReportsCommand(ReportsSystem plugin, GuiManager guiManager, GuiBuilder guiBuilder) {
        this.plugin = plugin;
        this.guiManager = guiManager;
        this.guiBuilder = guiBuilder;
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

        PaginatedGui gui = guiBuilder.buildStaffPanel(player);
        if (gui == null) {
            String msg = plugin.getConfigManager().getString("messages.panel-not-configured");
            if (msg != null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
            return true;
        }

        guiManager.openGui(player, gui);
        return true;
    }
}
