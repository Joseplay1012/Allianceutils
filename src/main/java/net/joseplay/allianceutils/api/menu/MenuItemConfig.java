package net.joseplay.allianceutils.api.menu;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

import static net.joseplay.allianceutils.Utils.UnicodeFontReplace.allianceFontReplace;

/**
 * Represents a configurable menu item for use in custom inventory menus.
 * This class encapsulates the properties of a menu item, such as its slot, display name,
 * lore, material, and click action, allowing for reusable and dynamic menu creation.
 */
public class MenuItemConfig {
    private final int slot;
    private final String displayName;
    private final List<String> lore;
    private final Material material;
    private final Consumer<InventoryClickEvent> clickAction;

    /**
     * Constructs a new MenuItemConfig with the specified properties.
     *
     * @param slot        The inventory slot where the item will be placed.
     * @param displayName The display name of the item, with color codes.
     * @param lore        The lore (description) lines of the item, with color codes.
     * @param material    The Material type of the item.
     * @param clickAction The action to perform when the item is clicked, or null if no action.
     */
    public MenuItemConfig(int slot, String displayName, List<String> lore, Material material, Consumer<InventoryClickEvent> clickAction) {
        this.slot = slot;
        this.displayName = displayName;
        this.lore = lore;
        this.material = material;
        this.clickAction = clickAction;
    }

    /**
     * Gets the inventory slot where this item should be placed.
     *
     * @return The slot number.
     */
    public int getSlot() {
        return slot;
    }

    /**
     * Creates an ItemStack for this menu item, applying the configured display name, lore, and material.
     * The display name and lore are processed with allianceFontReplace for proper formatting.
     *
     * @return The configured ItemStack ready to be placed in an inventory.
     */
    public ItemStack createItem() {
        return CreateItem.createItemStack(
                allianceFontReplace(displayName),
                lore,
                material
        );
    }

    /**
     * Handles a click event for this menu item by executing the configured click action.
     * If no click action is defined, this method does nothing.
     *
     * @param event The InventoryClickEvent triggered by clicking the item.
     */
    public void handleClick(InventoryClickEvent event) {
        if (clickAction != null) {
            clickAction.accept(event);
        }
    }

    /**
     * A builder class for creating MenuItemConfig instances in a fluent and readable way.
     */
    public static class Builder {
        private int slot;
        private String displayName;
        private List<String> lore;
        private Material material;
        private Consumer<InventoryClickEvent> clickAction;

        /**
         * Sets the inventory slot for the menu item.
         *
         * @param slot The slot number.
         * @return This Builder instance for chaining.
         */
        public Builder slot(int slot) {
            this.slot = slot;
            return this;
        }

        /**
         * Sets the display name of the menu item.
         *
         * @param displayName The display name, with color codes.
         * @return This Builder instance for chaining.
         */
        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        /**
         * Sets the lore (description) lines of the menu item.
         *
         * @param lore The list of lore lines, with color codes.
         * @return This Builder instance for chaining.
         */
        public Builder lore(List<String> lore) {
            this.lore = lore;
            return this;
        }

        /**
         * Sets the Material type of the menu item.
         *
         * @param material The Material to use for the item.
         * @return This Builder instance for chaining.
         */
        public Builder material(Material material) {
            this.material = material;
            return this;
        }

        /**
         * Sets the action to perform when the menu item is clicked.
         *
         * @param clickAction The Consumer to handle the InventoryClickEvent, or null if no action.
         * @return This Builder instance for chaining.
         */
        public Builder clickAction(Consumer<InventoryClickEvent> clickAction) {
            this.clickAction = clickAction;
            return this;
        }

        /**
         * Builds a new MenuItemConfig instance with the configured properties.
         *
         * @return A new MenuItemConfig instance.
         */
        public MenuItemConfig build() {
            return new MenuItemConfig(slot, displayName, lore, material, clickAction);
        }
    }
}