package net.joseplay.allianceutils.api.menu;

import net.joseplay.allianceutils.Utils.UnicodeFontReplace;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.Consumer;

public abstract class PagedCustomMenu extends SimpleMenu {

    protected int currentPage = 0;
    protected int maxPage = 0;
    protected final List<Integer> contentSlots;
    private final int nextSlot;
    private final int previousSlot;

    // Itens e ações paginados (índice global)
    protected final Map<Integer, ItemStack> pagedItems = new HashMap<>();
    protected final Map<Integer, Consumer<InventoryClickEvent>> pagedActions = new HashMap<>();

    // Itens e ações fixos (por slot)
    protected final Map<Integer, ItemStack> fixedItems = new HashMap<>();
    protected final Map<Integer, Consumer<InventoryClickEvent>> fixedActions = new HashMap<>();

    public PagedCustomMenu(Rows rows, String title, List<Integer> contentSlots, int nextSlot, int previousSlot) {
        super(rows, UnicodeFontReplace.allianceFontReplace(title));

        if (contentSlots == null){
            contentSlots = RowsStyle.COMPACT.getSlots();
        }

        this.contentSlots = List.copyOf(contentSlots);
        this.nextSlot = nextSlot;
        this.previousSlot = previousSlot;

        // Validação
        if (contentSlots.contains(nextSlot) || contentSlots.contains(previousSlot)) {
            throw new IllegalArgumentException("Navigation slots (" + nextSlot + ", " + previousSlot + ") cannot be in contentSlots!");
        }
    }

    // === SET ITEM PAGINADO ===
    public void setItem(int page, int slot, ItemStack item, Consumer<InventoryClickEvent> action) {
        if (!contentSlots.contains(slot)) return;
        int globalIndex = page * contentSlots.size() + contentSlots.indexOf(slot);
        pagedItems.put(globalIndex, item);
        pagedActions.put(globalIndex, action != null ? action : e -> {});
        if (page > maxPage) maxPage = page;
    }

    public void setItem(int page, int slot, ItemStack item) {
        setItem(page, slot, item, null);
    }

    public void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> action) {
        int slotIndex = contentSlots.indexOf(slot);

        if (slotIndex == -1) {
            throw new IllegalArgumentException("Slot " + slot + " não faz parte dos contentSlots");
        }

        int index = pagedItems.size();
        int page = index / contentSlots.size();

        setItem(page, slotIndex, item, action);
    }

    public void setItem(int slot, ItemStack item){
        setItem(slot, item, e -> {});
    }

    // === SET ITEM FIXO (FICA EM TODAS AS PÁGINAS) ===
    public void setFixedItem(int slot, ItemStack item, Consumer<InventoryClickEvent> action) {
        fixedItems.put(slot, item);
        fixedActions.put(slot, action != null ? action : e -> {});
        getInventory().setItem(slot, item != null ? item.clone() : null);
    }

    public void setFixedItem(int slot, ItemStack item) {
        setFixedItem(slot, item, null);
    }

    // === ADICIONA ITEM NA PRÓXIMA POSIÇÃO ===
    @Override
    public void addItem(ItemStack item, Consumer<InventoryClickEvent> action) {
        int index = pagedItems.size();
        int page = index / contentSlots.size();
        int slotIndex = index % contentSlots.size();
        setItem(page, contentSlots.get(slotIndex), item, action);
    }

    @Override
    public void addItem(ItemStack itemStack) {
        int index = pagedItems.size();
        int page = index / contentSlots.size();
        int slotIndex = index % contentSlots.size();
        setItem(page, contentSlots.get(slotIndex), itemStack, e -> {});
    }

    public void addAll(List<ItemAction> itemActions){
        for (ItemAction itemAction : itemActions){
            addItem(itemAction.getItemStack(), itemAction.getAction());
        }
    }

    // === LIMPA APENAS ITENS PAGINADOS ===
    public void clearContent() {
        pagedItems.clear();
        pagedActions.clear();
        maxPage = 0;
    }

    // === LIMPA ITENS FIXOS ===
    public void clearFixedItems() {
        for (int slot : fixedItems.keySet()) {
            getInventory().setItem(slot, null);
        }
        fixedItems.clear();
        fixedActions.clear();
    }

    @Override
    public void update() {
        for (int slot : contentSlots) {
            getInventory().setItem(slot, null);
        }

        int start = currentPage * contentSlots.size();
        int end = Math.min(start + contentSlots.size(), pagedItems.size());
        for (int i = start; i < end; i++) {
            ItemStack item = pagedItems.get(i);
            if (item != null) {
                int slot = contentSlots.get(i % contentSlots.size());
                getInventory().setItem(slot, item.clone());
            }
        }

        for (Map.Entry<Integer, ItemStack> entry : fixedItems.entrySet()) {
            getInventory().setItem(entry.getKey(), entry.getValue().clone());
        }

        setNavigator();
        if (usePlaceholders()) setPlaceholders();
    }

    public void setNavigator(){
        getInventory().setItem(previousSlot, currentPage > 0 ? getPreviousItem() : null);
        getInventory().setItem(nextSlot, currentPage < maxPage ? getNextItem() : null);
    }

    @Override
    public void click(InventoryClickEvent e, int slot) {
        unCheckedClick(e);
        Player p = (Player) e.getWhoClicked();

        // Navegação
        if (slot == previousSlot && currentPage > 0) {
            currentPage--;
            p.playSound(p.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_HAT, 1f, 1.5f);
            update();
            return;
        }
        if (slot == nextSlot && currentPage < maxPage) {
            currentPage++;
            p.playSound(p.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_HAT, 1f, 1.8f);
            update();
            return;
        }

        // Item fixo
        if (fixedActions.containsKey(slot)) {
            Consumer<InventoryClickEvent> action = fixedActions.get(slot);
            action.accept(e);
            return;
        }

        // Item paginado
        if (contentSlots.contains(slot)) {
            int localIndex = contentSlots.indexOf(slot);
            int globalIndex = currentPage * contentSlots.size() + localIndex;
            Consumer<InventoryClickEvent> action = pagedActions.get(globalIndex);
            if (action != null) {
                action.accept(e);
            }
        }
    }

    public ItemStack getNextItem() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(UnicodeFontReplace.allianceFontReplace("§aPróxima Página §7→"));
        meta.setLore(List.of(UnicodeFontReplace.allianceFontReplace("§7Página §e" + (currentPage + 2) + " §7de §e" + (maxPage + 1))));
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getPreviousItem() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(UnicodeFontReplace.allianceFontReplace("§f← §aPágina Anterior"));
        meta.setLore(List.of(UnicodeFontReplace.allianceFontReplace("§7Página §e" + currentPage + " §7de §e" + (maxPage + 1))));
        item.setItemMeta(meta);
        return item;
    }

    public int getCurrentPage() { return currentPage; }
    public int getMaxPage() { return maxPage; }
    public void setPage(int page) {
        this.currentPage = Math.max(0, Math.min(page, maxPage));
        update();
    }

    public int getNextSlot() {
        return nextSlot;
    }

    public List<Integer> getContentSlots() {
        return contentSlots;
    }

    public int getPreviousSlot() {
        return previousSlot;
    }

    public Map<Integer, ItemStack> getPagedItems() {
        return pagedItems;
    }

    public Map<Integer, Consumer<InventoryClickEvent>> getPagedActions() {
        return pagedActions;
    }

    public Map<Integer, ItemStack> getFixedItems() {
        return fixedItems;
    }

    public Map<Integer, Consumer<InventoryClickEvent>> getFixedActions() {
        return fixedActions;
    }
}