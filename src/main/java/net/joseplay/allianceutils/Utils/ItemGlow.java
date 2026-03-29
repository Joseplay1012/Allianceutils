package net.joseplay.allianceutils.Utils;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemGlow {
    public static void setItemGlown(ItemMeta itemGlown){
        itemGlown.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemGlown.addEnchant(Enchantment.LURE, 1, false);
    }
}
