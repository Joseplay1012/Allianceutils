package net.joseplay.allianceutils.api.menu;

import net.joseplay.allianceutils.Allianceutils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static net.joseplay.allianceutils.Utils.UnicodeFontReplace.allianceFontReplace;

public abstract class PagedMenu extends SimpleMenu {
    public int currentPage = 0;
    public int maxPage = 0;
    private List<Integer> slotsUsage;
    private final int nextSlot;
    private final int previousSlot;

    public PagedMenu(Rows rows, String title, List<Integer> slotsUsage, int nextSlot, int previousSlot) {
        super(rows, title);
        if (slotsUsage == null){
            List<Integer> slots = new ArrayList<>();

            int i = 0;
            while (i < rows.getSize()){
                slots.add(i);
                i++;
            }

            this.slotsUsage = slots;
        } else {
            this.slotsUsage = slotsUsage;
        }

        this.nextSlot = nextSlot;
        this.previousSlot = previousSlot;
    }

    public PagedMenu(Rows rows, String title, List<Integer> slotsUsage, int nextSlot, int previousSlot, int currentPage) {
        super(rows, title);

        if (this.currentPage != currentPage){
            this.currentPage = currentPage;
        }

        if (slotsUsage == null){
            List<Integer> slots = new ArrayList<>();

            int i = 0;
            while (i < rows.getSize()){
                slots.add(i);
                i++;
            }

            this.slotsUsage = slots;
        } else {
            this.slotsUsage = slotsUsage;
        }

        this.nextSlot = nextSlot;
        this.previousSlot = previousSlot;
    }

    protected void setNavigation(int nextSlot, int previousSlot) {
        setItem(nextSlot, getItemNextPage(), event -> {
            currentPage = Math.min(maxPage, currentPage + 1);

            Player p = (Player) event.getWhoClicked();

            if (currentPage == maxPage){
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
            } else {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);
            }

            update();
        });
        setItem(previousSlot, getItemPreviousPage(), event -> {
            currentPage = Math.max(0, currentPage - 1);

            Player p = (Player) event.getWhoClicked();

            if (currentPage == 0){
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
            } else {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);
            }

            update();
        });
    }

    public void addAll(List<ItemStack> items) {
        final int safeArea = getInventory().getSize() - 9;

        for (int i = 0; i < items.size(); i++) {
            int page = i / safeArea;
            int slot = i % safeArea;

            if (slot < slotsUsage.size()) {
                setItem(page, slotsUsage.get(slot), items.get(i));
            }
        }
    }

    public void addAllAction(List<ItemAction> items) {
        final int safeArea = getInventory().getSize() - 9;

        for (int i = 0; i < items.size(); i++) {
            int page = i / safeArea;
            int slot = i % safeArea;

            if (slot < slotsUsage.size()) {
                setItem(page, slotsUsage.get(slot), items.get(i).getItemStack(), items.get(i).getAction());
            }
        }
    }

    @Override
    public void update() {
        getInventory().clear();

        for (int i = 0; i < getInventory().getSize(); i++) {
            final int index = currentPage * getInventory().getSize() + i;
            final ItemStack item = this.getItemsMap().get(index);

            if (item != null)
                getInventory().setItem(i, item);
        }

        if (usePlaceholders()) setPlaceholders();
        setNavigation(nextSlot, previousSlot);
    }

    @Override
    public void click(InventoryClickEvent event, int slot) {
        unCheckedClick(event);

        try {
            Consumer<InventoryClickEvent> action = null;

            if (slot == nextSlot || slot == previousSlot) {
                action = getActionsMap().get(slot);
            } else {
                int index = currentPage * getInventory().getSize() + slot;
                action = getActionsMap().get(index);
            }

            if (action != null) {
                action.accept(event);
            }
        } catch (Exception e) {
            event.getWhoClicked().sendMessage(allianceFontReplace("§cOcorreu um erro na interação, espere e tente novamente."));
            Allianceutils.getInstance().getLogger().warning("Ocorreu um erro ao interagir com slot " + slot + " na página " + currentPage + " em " + event.getView().getTitle());
            e.printStackTrace();
        }
    }

    public void setItem(int page, int slot, ItemStack item) {
        setItem(page, slot, item, event -> {});
    }

    public void setItem(int page, int slot, ItemStack item, Consumer<InventoryClickEvent> action) {
        final int index = page * getInventory().getSize() + slot;
        getActionsMap().put(index, action);
        getItemsMap().put(index, item);

        if (page == 0)
            getInventory().setItem(index, item);

        if (page > maxPage)
            maxPage = page;
    }

    @Override
    public void setPlaceholders() {
        for (int i = 0; i < getInventory().getSize(); i++) {
            if (getInventory().getItem(i) == null)
                getInventory().setItem(i, PLACEHOLDER_ITEM);
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
        meta.setDisplayName(allianceFontReplace("§eProxima Página"));
        item.setItemMeta(meta);

        return item;
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

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
