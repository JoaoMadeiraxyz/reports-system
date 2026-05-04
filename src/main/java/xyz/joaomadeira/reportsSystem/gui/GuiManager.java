package xyz.joaomadeira.reportsSystem.gui;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import xyz.joaomadeira.reportsSystem.ReportsSystem;

public class GuiManager implements Listener {

    private final Map<UUID, Gui> openGuis;
    private final Map<Inventory, Gui> inventories;
    private final ReportsSystem plugin;

    public GuiManager(ReportsSystem plugin) {
        this.plugin = plugin;
        this.openGuis = new HashMap<>();
        this.inventories = new HashMap<>();
    }

    public boolean canAccess(Player player, Gui gui) {
        if (gui.getAccessLevel() == Gui.AccessLevel.PUBLIC) {
            return true;
        }
        return player.isOp();
    }

    public void openGui(Player player, Gui gui) {
        if (!canAccess(player, gui)) {
            String msg = plugin.getConfigManager().getString("messages.no-permission");
            if (msg != null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
            return;
        }
        gui.open(player);
        openGuis.put(player.getUniqueId(), gui);
        inventories.put(gui.getInventory(), gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) {
            return;
        }

        Gui gui = inventories.get(clickedInventory);
        if (gui == null) {
            return;
        }

        event.setCancelled(true);

        if (event.getWhoClicked() instanceof Player) {
            int slot = event.getSlot();

            if (slot < 0 || slot >= gui.getSize()) {
                return;
            }

            GuiItem item = gui.getItem(slot);
            if (item != null && item.getAction() != null) {
                item.getAction().accept(event);

                if (gui instanceof PaginatedGui) {
                    ((PaginatedGui) gui).renderPage();
                }
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Gui gui = inventories.get(event.getInventory());
        if (gui != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            Gui gui = openGuis.remove(player.getUniqueId());
            if (gui != null) {
                inventories.remove(gui.getInventory());
            }
        }
    }

    public void closeGui(Player player) {
        Gui gui = openGuis.remove(player.getUniqueId());
        if (gui != null) {
            inventories.remove(gui.getInventory());
            player.closeInventory();
        }
    }

    public Gui getOpenGui(Player player) {
        return openGuis.get(player.getUniqueId());
    }
}
