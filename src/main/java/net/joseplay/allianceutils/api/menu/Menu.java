package net.joseplay.allianceutils.api.menu;

import net.joseplay.allianceutils.Allianceutils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public interface Menu extends InventoryHolder {
    JavaPlugin plugin = Allianceutils.getPlugin();
    List<Menu> menuList = new CopyOnWriteArrayList<>();


    void click(InventoryClickEvent event, int slot);

    void unCheckedClick(InventoryClickEvent event);

    void setItem(int slot, ItemStack itemStack);

    void addItem(ItemStack itemStack);

    void addItem(ItemStack itemStack, Consumer<InventoryClickEvent> action);

    void setItem(int slot, ItemStack itemStack, Consumer<InventoryClickEvent> action);

    void onSetItems();

    void onClose();

    boolean usePlaceholders();

    void setPlaceholders();

    void update();

    Map<Integer, ItemStack> getItemsMap();
    Map<Integer, Consumer<InventoryClickEvent>> getActionsMap();

    default void open(Player player) {
        if (usePlaceholders()) setPlaceholders();

        onSetItems();
        player.openInventory(getInventory());
        update();
    }
}
