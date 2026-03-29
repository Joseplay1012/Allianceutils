package net.joseplay.allianceutils.api.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.Consumer;

public class ItemStackUtils {

    public static void editMeta(ItemStack item, Consumer<ItemMeta> metaConsumer) {
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();

        metaConsumer.accept(meta);
        item.setItemMeta(meta);
    }
}
