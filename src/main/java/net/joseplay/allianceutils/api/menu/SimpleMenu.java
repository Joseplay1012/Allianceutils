package net.joseplay.allianceutils.api.menu;

import net.joseplay.allianceutils.Allianceutils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

import static net.joseplay.allianceutils.Utils.UnicodeFontReplace.allianceFontReplace;

public abstract class SimpleMenu implements Menu {
    public ItemStack PLACEHOLDER_ITEM;
    public long updateIntervalTicks = -1;
    public Player player;
    /**
     * cancela o evento de click no inventario do jogador.
     */
    public boolean cancelClickPlayerInventory = true;
    public NamespacedKey PLACEHOLDER_KEY = new NamespacedKey(Allianceutils.getPlugin(), "menuapi_placeholder");

    private final Map<Integer, Consumer<InventoryClickEvent>> actions = new HashMap<>();
    private final Map<Integer, ItemStack> items = new HashMap<>();
    private final Inventory inventory;
    private boolean usePlaceholders;

    public SimpleMenu(Rows rows, String title) {
        createPlaceholdeItem();
        menuList.add(this);
        this.usePlaceholders = false;
        this.inventory = Bukkit.createInventory(this, rows.getSize(), title);
    }

    public SimpleMenu(InventoryType type){
        createPlaceholdeItem();
        menuList.add(this);
        this.usePlaceholders = false;
        this.inventory = Bukkit.createInventory(this, type);
    }

    public void createPlaceholdeItem(){
        PLACEHOLDER_ITEM = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        ItemMeta meta = PLACEHOLDER_ITEM.getItemMeta();
        meta.setDisplayName(" ");
        meta.setHideTooltip(true);
        meta.getPersistentDataContainer().set(PLACEHOLDER_KEY, PersistentDataType.BOOLEAN, true);
        PLACEHOLDER_ITEM.setItemMeta(meta);
    }

    @Override
    public void click(InventoryClickEvent event, int slot) {
        unCheckedClick(event);
        try {
            final Consumer<InventoryClickEvent> action = this.actions.get(slot);

            if (action != null) action.accept(event);
        } catch (Exception e) {
            event.getWhoClicked().sendMessage(allianceFontReplace("§cOcorreu um erro na interação, espere e tente novamente."));
            Allianceutils.getInstance().getLogger().warning("Occoreu um erro ao interagir com slot " + slot + event.getView().getTitle());
            e.printStackTrace();
        }
    }

    @Override
    public void unCheckedClick(InventoryClickEvent event) {}

    public void enableAutoUpdate(long intervalTicks) {
        this.updateIntervalTicks = intervalTicks;
    }

    @Override
    public void addItem(ItemStack itemStack) {
        addItem(itemStack, event -> {});
    }

    @Override
    public void addItem(ItemStack itemStack, Consumer<InventoryClickEvent> action) {
        // Encontre o primeiro slot vazio
        for (int i = 0; i < getInventory().getSize(); i++) {
            if (getInventory().getItem(i) == null || getInventory().getItem(i).getType() == Material.AIR) {
                // Adiciona o item no slot encontrado
                getInventory().setItem(i, itemStack);
                // Registra a ação associada a esse slot
                this.actions.put(i, action);
                break;
            }
        }
    }

    @Override
    public void setItem(int slot, ItemStack item) {
        setItem(slot, item, event -> {
        });
    }


    public void setItemNull(int slot) {
        setItem(slot, new ItemStack(Material.AIR), event -> {
        });
    }

    @Override
    public void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> action) {
        this.actions.put(slot, action);
        this.items.put(slot, item);
        getInventory().setItem(slot, item);
    }

    public int findNextFree(int... slots) {
        for (int slot : slots) {
            ItemStack item = getInventory().getItem(slot);
            if (item == null || item.getType() == Material.AIR) {
                return slot;
            }
        }
        return slots[0]; // fallback
    }

    public void setUsePlaceholders(boolean usePlaceholders) {
        this.usePlaceholders = usePlaceholders;
    }

    @Override
    public void setPlaceholders() {
        for (int i = 0; i < getInventory().getSize(); i++) {
            if (getInventory().getItem(i) == null) {
                getInventory().setItem(i, PLACEHOLDER_ITEM);
            }
        }
    }

    @Override
    public boolean usePlaceholders() {
        return usePlaceholders;
    }

    @Override
    public void update() {
        getInventory().clear();

        for (int i = 0; i < getInventory().getSize(); i++) {
            final ItemStack item = getItemsMap().get(i);

            if (item != null)
                getInventory().setItem(i, item);
        }
    }

    @Override
    public void open(Player player) {
        this.player = player;

        onSetItems();

        if (usePlaceholders()) setPlaceholders();
        player.openInventory(getInventory());


        if (updateIntervalTicks > 0 && plugin != null) {
            MenuUpdater.register(plugin, this, updateIntervalTicks);
        }
    }

    public abstract void onSetItems();

    public void setCancelClickPlayerInventory(boolean b){
        this.cancelClickPlayerInventory = b;
    }

    @Override
    public void onClose() {}

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public Map<Integer, ItemStack> getItemsMap() {
        return items;
    }

    @Override
    public Map<Integer, Consumer<InventoryClickEvent>> getActionsMap() {
        return actions;
    }

    public enum Rows {
        ONE_LINE(1),
        TWO_LINE(2),
        THREE_LINE(3),
        FOUR_LINE(4),
        FIVE_LINE(5),
        SIX_LINE(6);

        private final int size;

        Rows(int rows) {
            this.size = rows * 9;
        }

        public static Rows getSizeByInteger(int integer){
            int rows = integer * 9;
            if (rows == 9) return ONE_LINE;
            if (rows == 2 * 9) return TWO_LINE;
            if (rows == 3 * 9) return THREE_LINE;
            if (rows == 4 * 9) return FOUR_LINE;
            if (rows == 5 * 9) return FIVE_LINE;
            if (rows == 6 * 9) return SIX_LINE;

            return ONE_LINE;
        }

        public int getLines(){
            return size / 9;
        }

        public int getSize() {
            return size;
        }
    }

    public enum RowsStyle {
        /**Compacto, itens bem agrupados no meio*/
        COMPACT(
                List.of(
                        10, 11, 12, 13, 14, 15, 16,
                                  19, 20, 21, 22, 23, 24, 25,
                                  28, 29, 30, 31, 32, 33, 34
                ),
                Rows.FIVE_LINE, 39, 41
        ),
        COMPACT_XADREZ(
                List.of(
                        10, 12, 14, 16,
                                  20, 22, 24,
                                  28, 30, 32,34
                ),
                Rows.SIX_LINE, 48, 50
        ),

        /**Grade cheia (aproveita todos os slots do inventário, exceto os botões)*/
        FULL(
                List.of(
                        0, 1, 2, 3, 4, 5, 6, 7, 8,
                        9, 10, 11, 12, 13, 14, 15, 16, 17,
                        18, 19, 20, 21, 22, 23, 24, 25, 26,
                        27, 28, 29, 30, 31, 32, 33, 34, 35,
                        36, 37, 38, 39, 40, 41, 42, 43, 44,
                        45, 46, 47, 49, 51, 52, 53
                ),
                Rows.SIX_LINE, 48, 50
        ),
        XADREZ_THREE_LINES(
                List.of(10, 12, 14, 16),
                Rows.THREE_LINE, 18, 26
        ),

        HORIZONTAL_BAR_THREE_LINES(
                List.of(10, 11, 12, 13, 14, 15, 16),
                Rows.THREE_LINE, 21, 23
        ),

        /**Linha única (para menus simples, tipo barra horizontal)*/
        HORIZONTAL_BAR(
                List.of(1, 2, 3, 4, 5, 6, 7),
                Rows.ONE_LINE, 0, 8
        ),

        /**Grade em "anel" (borda, deixando o centro vazio)*/
        BORDER(
                List.of(
                        0, 1, 2, 3, 4, 5, 6, 7, 8,
                        9,                               17,
                        18,                              26,
                        27,                              35,
                        36, 37, 38, 39, 40, 41, 42, 43,  44
                ),
                Rows.SIX_LINE, 48, 50
        ),

        /**Layout estilo "livro" (2 páginas centrais, botões laterais)*/
        BOOK(
                List.of(
                        20, 21, 22, 23, 24,
                        29, 30, 31, 32, 33
                ),
                Rows.SIX_LINE, 48, 50
        );

        final List<Integer> slots;
        final Rows rows;
        final int previusPage;
        final int nextPage;

        RowsStyle(List<Integer> slots, Rows rows, int previusPage, int nextPage) {
            this.slots = slots;
            this.rows = rows;
            this.previusPage = previusPage;
            this.nextPage = nextPage;
        }

        public List<Integer> getSlots() {
            return slots;
        }

        public Rows getRows() {
            return rows;
        }

        public int getPreviusPage() {
            return previusPage;
        }

        public int getNextPage() {
            return nextPage;
        }
    }
}
