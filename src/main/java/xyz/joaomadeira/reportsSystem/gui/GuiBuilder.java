package xyz.joaomadeira.reportsSystem.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.joaomadeira.reportsSystem.ReportsSystem;
import xyz.joaomadeira.reportsSystem.model.Report;
import xyz.joaomadeira.reportsSystem.storage.ReportStorage;
import xyz.joaomadeira.reportsSystem.utils.ItemBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GuiBuilder {

    private final ReportsSystem plugin;
    private final ReportStorage reportStorage;

    public GuiBuilder(ReportsSystem plugin, ReportStorage reportStorage) {
        this.plugin = plugin;
        this.reportStorage = reportStorage;
    }

    public PaginatedGui buildStaffPanel(Player staffPlayer) {
        ConfigurationSection section = plugin.getConfigManager().getConfigurationSection("gui.staff-panel");
        if (section == null) {
            return null;
        }

        String title = ChatColor.translateAlternateColorCodes('&', section.getString("title", "&6Staff Panel"));
        int rows = section.getInt("rows", 5);
        int itemsPerPage = section.getInt("items-per-page", 36);
        String accessStr = section.getString("access", "staff");
        Gui.AccessLevel access = "public".equalsIgnoreCase(accessStr) ? Gui.AccessLevel.PUBLIC : Gui.AccessLevel.STAFF;

        PaginatedGui gui = new PaginatedGui(title, rows, itemsPerPage);
        gui.setAccessLevel(access);

        ConfigurationSection itemsSection = section.getConfigurationSection("items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
                if (itemSection != null) {
                    GuiItem item = buildItemFromConfig(itemSection, staffPlayer);
                    if (item != null) {
                        gui.addItem(item);
                    }
                }
            }
        }

        List<Report> pendingReports = reportStorage.getPendingReports();
        ConfigurationSection reportItemSection = section.getConfigurationSection("report-item");

        if (pendingReports.isEmpty()) {
            ConfigurationSection noReportsSection = section.getConfigurationSection("no-reports-item");
            ItemBuilder noReportsBuilder;
            if (noReportsSection != null) {
                String materialStr = noReportsSection.getString("material", "BARRIER");
                Material material;
                try {
                    material = Material.valueOf(materialStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    material = Material.BARRIER;
                }
                noReportsBuilder = new ItemBuilder(material);
                String name = noReportsSection.getString("name");
                if (name != null) {
                    noReportsBuilder.name(name);
                }
            } else {
                noReportsBuilder = new ItemBuilder(Material.BARRIER).name("&cNo Reports");
            }
            String noReportsMsg = plugin.getConfigManager().getString("messages.no-reports");
            if (noReportsMsg != null) {
                noReportsBuilder.lore(noReportsMsg);
            }
            gui.addItem(new GuiItem(noReportsBuilder.build()));
        } else {
            for (Report report : pendingReports) {
                GuiItem reportItem = buildReportItem(report, staffPlayer, reportItemSection, gui);
                if (reportItem != null) {
                    gui.addItem(reportItem);
                }
            }
        }

        return gui;
    }

    private GuiItem buildItemFromConfig(ConfigurationSection section, Player player) {
        String materialStr = section.getString("material", "STONE");
        Material material;
        try {
            material = Material.valueOf(materialStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            material = Material.STONE;
        }

        ItemBuilder builder = new ItemBuilder(material);

        short data = (short) section.getInt("data", 0);
        if (data != 0) {
            builder.data(data);
        }

        int amount = section.getInt("amount", 1);
        builder.amount(amount);

        String name = section.getString("name");
        if (name != null) {
            builder.name(name);
        }

        List<String> lore = section.getStringList("lore");
        if (!lore.isEmpty()) {
            builder.lore(lore);
        }

        return new GuiItem(builder.build());
    }

    private GuiItem buildReportItem(Report report, Player staffPlayer, ConfigurationSection templateSection, PaginatedGui gui) {
        ItemBuilder builder;

        if (templateSection != null) {
            String materialStr = templateSection.getString("material", "PAPER");
            Material material;
            try {
                material = Material.valueOf(materialStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                material = Material.PAPER;
            }

            builder = new ItemBuilder(material);

            short data = (short) templateSection.getInt("data", 0);
            if (data != 0) {
                builder.data(data);
            }

            String name = templateSection.getString("name", "&e{reported}");
            name = name
                .replace("{reported}", report.getReportedName())
                .replace("{reporter}", report.getReporterName())
                .replace("{reason}", report.getReason());
            builder.name(name);

            List<String> rawLore = templateSection.getStringList("lore");
            if (!rawLore.isEmpty()) {
                List<String> lore = new ArrayList<>();
                for (String line : rawLore) {
                    lore.add(line
                        .replace("{reported}", report.getReportedName())
                        .replace("{reporter}", report.getReporterName())
                        .replace("{reason}", report.getReason())
                        .replace("{date}", formatDate(report.getTimestamp())));
                }
                builder.lore(lore);
            }
        } else {
            return null;
        }

        ItemStack itemStack = builder.build();
        GuiItem[] itemRef = new GuiItem[1];
        itemRef[0] = new GuiItem(itemStack, event -> {
            reportStorage.resolveReport(report.getId(), staffPlayer.getName());
            String msg = plugin.getConfigManager().getString("messages.report-resolved");
            if (msg != null) {
                staffPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
            gui.getPageItems().remove(itemRef[0]);
        });
        return itemRef[0];
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(new Date(timestamp));
    }
}
