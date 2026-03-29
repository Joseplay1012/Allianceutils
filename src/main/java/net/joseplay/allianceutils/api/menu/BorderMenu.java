package net.joseplay.allianceutils.api.menu;

import net.joseplay.allianceutils.Allianceutils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.function.Consumer;

import static net.joseplay.allianceutils.Utils.UnicodeFontReplace.allianceFontReplace;

public abstract class BorderMenu extends SimpleMenu {

    private int currentPage = 0;
    private int maxPage = 0;
    private final List<Integer> borderSlots;
    private final int nextSlot;
    private final int previousSlot;

    public BorderMenu(List<Integer> borderSlots, Rows rows, String title, int nextSlot, int previousSlot) {
        super(rows, title);
        this.borderSlots = borderSlots;
        this.nextSlot = nextSlot;
        this.previousSlot = previousSlot;
    }

    protected void setNavigation(int nextSlot, int previousSlot) {
        setItem(nextSlot, getItemNextPage(), event -> {
            currentPage = Math.min(maxPage, currentPage + 1);
            Player p = (Player) event.getWhoClicked();
            if (currentPage == maxPage) {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
            } else {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);
            }
            update();
        });

        setItem(previousSlot, getItemPreviousPage(), event -> {
            currentPage = Math.max(0, currentPage - 1);
            Player p = (Player) event.getWhoClicked();
            if (currentPage == 0) {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
            } else {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);
            }
            update();
        });
    }

    public void addAll(List<ItemStack> items) {
        int borderSize = borderSlots.size();
        for (int i = 0; i < items.size(); i++) {
            int page = i / borderSize;
            int slotIndex = i % borderSize;
            setItem(page, borderSlots.get(slotIndex), items.get(i));
        }
    }

    public void addAllAction(List<ItemAction> items) {
        int borderSize = borderSlots.size();
        for (int i = 0; i < items.size(); i++) {
            int page = i / borderSize;
            int slotIndex = i % borderSize;
            setItem(page, borderSlots.get(slotIndex), items.get(i).getItemStack(), items.get(i).getAction());
        }
    }

    @Override
    public void update() {
        getInventory().clear();

        int borderSize = borderSlots.size();
        for (int i = 0; i < borderSize; i++) {
            int index = currentPage * borderSize + i;
            ItemStack item = getItemsMap().get(index);
            if (item != null) {
                getInventory().setItem(borderSlots.get(i), item);
            }
        }

        if (usePlaceholders()) setPlaceholders();
        setNavigation(nextSlot, previousSlot);
    }

    @Override
    public void click(InventoryClickEvent event, int slot) {
        try {
            Consumer<InventoryClickEvent> action = null;
            if (slot == nextSlot || slot == previousSlot) {
                action = getActionsMap().get(slot);
            } else {
                int index = currentPage * borderSlots.size() + borderSlots.indexOf(slot);
                action = getActionsMap().get(index);
            }
            if (action != null) {
                action.accept(event);
            }
        } catch (Exception e) {
            event.getWhoClicked().sendMessage(allianceFontReplace("§cOcorreu um erro na interação, espere e tente novamente."));
            Allianceutils.getInstance().getLogger().warning("Erro ao interagir com slot " + slot + " na página " + currentPage + " em " + event.getView().getTitle());
            e.printStackTrace();
        }
    }

    public void setItem(int page, int slot, ItemStack item) {
        setItem(page, slot, item, event -> {});
    }

    public void setItem(int page, int slot, ItemStack item, Consumer<InventoryClickEvent> action) {
        int index = page * borderSlots.size() + borderSlots.indexOf(slot);
        getItemsMap().put(index, item);
        getActionsMap().put(index, action);

        if (page == 0) getInventory().setItem(slot, item);
        if (page > maxPage) maxPage = page;
    }

    @Override
    public void setPlaceholders() {
        for (int i : borderSlots) {
            if (getInventory().getItem(i) == null) getInventory().setItem(i, PLACEHOLDER_ITEM);
        }
    }

    public ItemStack getItemPreviousPage() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(allianceFontReplace("§ePágina anterior"));
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getItemNextPage() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(allianceFontReplace("§ePróxima Página"));
        item.setItemMeta(meta);
        return item;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public int getNextSlot() {
        return nextSlot;
    }

    public int getPreviousSlot() {
        return previousSlot;
    }
}