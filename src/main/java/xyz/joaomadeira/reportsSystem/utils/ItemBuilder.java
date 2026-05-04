package xyz.joaomadeira.reportsSystem.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    private final Material material;
    private int amount;
    private short data;
    private String name;
    private List<String> lore;

    public ItemBuilder(Material material) {
        this.material = material;
        this.amount = 1;
        this.data = 0;
        this.lore = new ArrayList<>();
    }

    public ItemBuilder data(short data) {
        this.data = data;
        return this;
    }

    public ItemBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder name(String name) {
        this.name = ChatColor.translateAlternateColorCodes('&', name);
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        this.lore = new ArrayList<>();
        for (String line : lore) {
            this.lore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        return this;
    }

    public ItemBuilder lore(String... lore) {
        return lore(Arrays.asList(lore));
    }

    public ItemStack build() {
        ItemStack item = new ItemStack(material, amount, data);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        if (name != null) {
            meta.setDisplayName(name);
        }
        if (!lore.isEmpty()) {
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }
}
