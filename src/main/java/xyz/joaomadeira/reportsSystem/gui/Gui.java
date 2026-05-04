package xyz.joaomadeira.reportsSystem.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class Gui {

    public enum AccessLevel {
        PUBLIC,
        STAFF
    }

    protected final Map<Integer, GuiItem> items;
    protected final String title;
    protected final int size;
    protected Inventory inventory;
    protected AccessLevel accessLevel;

    public Gui(String title, int size) {
        this.title = title;
        this.size = size;
        this.items = new HashMap<>();
        this.accessLevel = AccessLevel.PUBLIC;
    }

    public Gui(String title, int size, AccessLevel accessLevel) {
        this.title = title;
        this.size = size;
        this.items = new HashMap<>();
        this.accessLevel = accessLevel;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setItem(int slot, GuiItem item) {
        items.put(slot, item);
        if (inventory != null) {
            inventory.setItem(slot, item.getItemStack());
        }
    }

    public void removeItem(int slot) {
        items.remove(slot);
        if (inventory != null) {
            inventory.setItem(slot, null);
        }
    }

    public GuiItem getItem(int slot) {
        return items.get(slot);
    }

    public void open(Player player) {
        inventory = Bukkit.createInventory(null, size, title);
        for (Map.Entry<Integer, GuiItem> entry : items.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getItemStack());
        }
        player.openInventory(inventory);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String getTitle() {
        return title;
    }

    public int getSize() {
        return size;
    }
}
