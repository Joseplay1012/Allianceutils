package net.joseplay.allianceutils.api.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * Represents an item associated with a click action in a menu system.
 *
 * <p>This class binds an {@link ItemStack} to a {@link Consumer} that will be
 * executed when the item is interacted with in an inventory.</p>
 *
 * <b>Usage example:</b>
 * <pre>
 * ItemAction action = new ItemAction(item, event -> {
 *     event.setCancelled(true);
 *     // handle click
 * });
 * </pre>
 *
 * <b>Notes:</b>
 * <ul>
 *     <li>The action is executed with the {@link InventoryClickEvent}</li>
 *     <li>No null validation is performed</li>
 *     <li>The ItemStack is stored by reference (not cloned)</li>
 * </ul>
 */
public class ItemAction {

    /**
     * The item associated with this action.
     */
    private final ItemStack itemStack;

    /**
     * The action executed when the item is clicked.
     */
    private final Consumer<InventoryClickEvent> action;

    /**
     * Creates a new ItemAction.
     *
     * @param itemStack the item to associate with the action
     * @param action the logic to execute on click
     */
    public ItemAction(ItemStack itemStack, Consumer<InventoryClickEvent> action) {
        this.itemStack = itemStack;
        this.action = action;
    }

    /**
     * Returns the associated item.
     *
     * @return item stack
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Returns the click action.
     *
     * @return consumer handling the click event
     */
    public Consumer<InventoryClickEvent> getAction() {
        return action;
    }
}