package xyz.joaomadeira.reportsSystem.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PaginatedGui extends Gui {

    private final List<GuiItem> pageItems;
    private final int itemsPerPage;
    private final int navRow;
    private int currentPage;

    private GuiItem prevArrow;
    private GuiItem nextArrow;
    private GuiItem pageIndicator;

    private final int prevSlot;
    private final int nextSlot;
    private final int indicatorSlot;

    public PaginatedGui(String title, int rows, int itemsPerPage) {
        super(title, rows * 9);
        this.pageItems = new ArrayList<>();
        this.itemsPerPage = itemsPerPage;
        this.navRow = rows - 1;
        this.currentPage = 0;

        this.prevSlot = navRow * 9;
        this.nextSlot = navRow * 9 + 8;
        this.indicatorSlot = navRow * 9 + 4;

        setupDefaultNavigation();
    }

    private void setupDefaultNavigation() {
        prevArrow = new GuiItem(
            new ItemStack(Material.ARROW),
            event -> previousPage()
        );

        nextArrow = new GuiItem(
            new ItemStack(Material.ARROW),
            event -> nextPage()
        );

        pageIndicator = new GuiItem(
            new ItemStack(Material.PAPER)
        );
    }

    public void setPrevArrow(GuiItem prevArrow) {
        this.prevArrow = prevArrow;
    }

    public void setNextArrow(GuiItem nextArrow) {
        this.nextArrow = nextArrow;
    }

    public void setPageIndicator(GuiItem pageIndicator) {
        this.pageIndicator = pageIndicator;
    }

    public void addItem(GuiItem item) {
        pageItems.add(item);
    }

    public void removeItem(GuiItem item) {
        pageItems.remove(item);
    }

    public int getTotalPages() {
        if (pageItems.isEmpty()) {
            return 1;
        }
        return (int) Math.ceil((double) pageItems.size() / itemsPerPage);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void nextPage() {
        if (currentPage < getTotalPages() - 1) {
            currentPage++;
            renderPage();
        }
    }

    public void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            renderPage();
        }
    }

    public void renderPage() {
        if (inventory == null) {
            return;
        }

        int totalPages = getTotalPages();
        if (currentPage >= totalPages) {
            currentPage = Math.max(0, totalPages - 1);
        }

        int contentSlots = itemsPerPage;
        int startIdx = currentPage * itemsPerPage;
        int endIdx = Math.min(startIdx + itemsPerPage, pageItems.size());

        for (int i = 0; i < contentSlots; i++) {
            inventory.clear(i);
        }

        for (int i = 0; i < contentSlots; i++) {
            int itemIdx = startIdx + i;
            if (itemIdx < endIdx) {
                inventory.setItem(i, pageItems.get(itemIdx).getItemStack());
                items.put(i, pageItems.get(itemIdx));
            } else {
                items.remove(i);
            }
        }

        renderNavigation();
    }

    private void renderNavigation() {
        int totalPages = getTotalPages();

        if (currentPage > 0) {
            inventory.setItem(prevSlot, prevArrow.getItemStack());
            items.put(prevSlot, prevArrow);
        } else {
            inventory.clear(prevSlot);
            items.remove(prevSlot);
        }

        if (currentPage < totalPages - 1) {
            inventory.setItem(nextSlot, nextArrow.getItemStack());
            items.put(nextSlot, nextArrow);
        } else {
            inventory.clear(nextSlot);
            items.remove(nextSlot);
        }

        if (totalPages > 1) {
            ItemStack indicator = pageIndicator.getItemStack().clone();
            ItemMeta meta = indicator.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                meta.setDisplayName(meta.getDisplayName()
                    .replace("{current}", String.valueOf(currentPage + 1))
                    .replace("{total}", String.valueOf(totalPages)));
                indicator.setItemMeta(meta);
            }
            inventory.setItem(indicatorSlot, indicator);
            items.put(indicatorSlot, new GuiItem(indicator));
        } else {
            inventory.clear(indicatorSlot);
            items.remove(indicatorSlot);
        }
    }

    @Override
    public void open(Player player) {
        inventory = Bukkit.createInventory(null, getSize(), getTitle());
        renderPage();
        player.openInventory(inventory);
    }

    public List<GuiItem> getPageItems() {
        return pageItems;
    }
}
