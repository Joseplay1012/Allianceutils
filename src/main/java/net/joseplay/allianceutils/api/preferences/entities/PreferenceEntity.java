package net.joseplay.allianceutils.api.preferences.entities;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class PreferenceEntity {
    private final ItemStack icon;
    private final Consumer<InventoryClickEvent> action;


    public PreferenceEntity(ItemStack icon, Consumer<InventoryClickEvent> action) {
        this.icon = icon;
        this.action = action;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public Consumer<InventoryClickEvent> getAction() {
        return action;
    }
}