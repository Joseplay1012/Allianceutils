package net.joseplay.allianceutils.api.internalListener.events;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerItemAddEvent {
    private final Player player;
    private final ItemStack item;
    private boolean cancelled;

    public PlayerItemAddEvent(Player player, ItemStack item) {
        this.player = player;
        this.item = item;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItem() {
        return item;
    }
}
