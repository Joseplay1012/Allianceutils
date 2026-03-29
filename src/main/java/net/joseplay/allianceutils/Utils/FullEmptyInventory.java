package net.joseplay.allianceutils.Utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FullEmptyInventory {
    public static void fillEmptySlotsWith(Inventory gui, Material glassMaterial, Integer model) {
        ItemStack[] contents = gui.getContents();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] == null || contents[i].getType().equals(Material.AIR)) {
                ItemStack glassPane = new ItemStack(glassMaterial);
                ItemMeta itemMeta = glassPane.getItemMeta();
                itemMeta.setDisplayName(" ");
                //itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                itemMeta.setHideTooltip(true);

                if (model != null){
                    itemMeta.setCustomModelData(model);
                }

                glassPane.setItemMeta(itemMeta);

                contents[i] = glassPane;
            }
        }

        gui.setContents(contents);
    }
}
