package net.joseplay.allianceutils.api.preferences.categories;

import net.joseplay.allianceutils.Utils.UnicodeFontReplace;
import net.joseplay.allianceutils.api.menu.CreateItem;
import net.joseplay.allianceutils.api.preferences.interfaces.PreferencesCategory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EconomyCategory implements PreferencesCategory {
    private static String id = "economy_category";

    @Override
    public String getName() {
        return UnicodeFontReplace.allianceFontReplace("§aEconomia");
    }

    @Override
    public List<String> getDescription() {
        return List.of(
                UnicodeFontReplace.allianceFontReplace("§7• Preferencias de economia.")
        );
    }

    @Override
    public String getID() {
        return id;
    }

    public static String getId(){
        return id;
    }

    @Override
    public ItemStack getIcon() {
        return CreateItem.createItemStack(
                getName(),
                getDescription(),
                Material.EMERALD
        );
    }
}
