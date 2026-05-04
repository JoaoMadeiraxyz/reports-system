package xyz.joaomadeira.reportsSystem;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.joaomadeira.reportsSystem.command.ReportCommand;
import xyz.joaomadeira.reportsSystem.command.ReportsCommand;
import xyz.joaomadeira.reportsSystem.config.ConfigManager;
import xyz.joaomadeira.reportsSystem.gui.GuiBuilder;
import xyz.joaomadeira.reportsSystem.gui.GuiManager;
import xyz.joaomadeira.reportsSystem.storage.ReportStorage;

public final class ReportsSystem extends JavaPlugin {

    private ConfigManager configManager;
    private GuiManager guiManager;
    private GuiBuilder guiBuilder;
    private ReportStorage reportStorage;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.load();

        reportStorage = new ReportStorage(this);
        reportStorage.load();

        guiManager = new GuiManager(this);
        getServer().getPluginManager().registerEvents(guiManager, this);

        guiBuilder = new GuiBuilder(this, reportStorage);

        getCommand("report").setExecutor(new ReportCommand(this, reportStorage));
        getCommand("reports").setExecutor(new ReportsCommand(this, guiManager, guiBuilder));

        getLogger().info("ReportsSystem has been enabled!");
    }

    @Override
    public void onDisable() {
        reportStorage.save();
        getLogger().info("ReportsSystem has been disabled!");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public GuiBuilder getGuiBuilder() {
        return guiBuilder;
    }

    public ReportStorage getReportStorage() {
        return reportStorage;
    }
}
