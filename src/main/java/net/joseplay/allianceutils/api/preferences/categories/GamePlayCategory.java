package net.joseplay.allianceutils.api.preferences.categories;

import net.joseplay.allianceutils.Utils.UnicodeFontReplace;
import net.joseplay.allianceutils.api.menu.CreateItem;
import net.joseplay.allianceutils.api.preferences.interfaces.PreferencesCategory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GamePlayCategory implements PreferencesCategory {
    private static final String id = "gameplay_category";

    @Override
    public String getName() {
        return UnicodeFontReplace.allianceFontReplace("§aJogabilidade");
    }

    @Override
    public List<String> getDescription() {
        return List.of(
                UnicodeFontReplace.allianceFontReplace("§7• Preferencias de jogabilidade.")
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
                Material.COMPASS
        );
    }
}
