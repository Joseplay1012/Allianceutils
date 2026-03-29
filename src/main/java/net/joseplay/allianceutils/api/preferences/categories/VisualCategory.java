package net.joseplay.allianceutils.api.preferences.categories;

import net.joseplay.allianceutils.api.menu.CreateItem;
import net.joseplay.allianceutils.api.preferences.interfaces.PreferencesCategory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static net.joseplay.allianceutils.Utils.UnicodeFontReplace.allianceFontReplace;

public class VisualCategory implements PreferencesCategory {
    private static String id = "visual_category";

    @Override
    public String getName() {
        return allianceFontReplace("§eVisual");
    }

    @Override
    public List<String> getDescription() {
        return List.of(
                allianceFontReplace("§7▪ categoria para funções de visualização.")
        );
    }

    @Override
    public String getID() {
        return id;
    }

    public static String getId() {
        return id;
    }

    @Override
    public ItemStack getIcon() {
        return CreateItem.createItemStack(
                getName(),
                getDescription(),
                Material.ENDER_EYE
        );
    }
}
