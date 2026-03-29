package net.joseplay.allianceutils.api.preferences.categories;

import net.joseplay.allianceutils.Utils.UnicodeFontReplace;
import net.joseplay.allianceutils.api.menu.CreateItem;
import net.joseplay.allianceutils.api.preferences.interfaces.PreferencesCategory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class UnCategory implements PreferencesCategory {
    @Override
    public String getName() {
        return UnicodeFontReplace.allianceFontReplace("§aNão categorizados");
    }

    @Override
    public List<String> getDescription() {
        return List.of(UnicodeFontReplace.allianceFontReplace("§7▪ essas preferencias ainda não estão categorizadas."));
    }

    @Override
    public String getID() {
        return "uncategory";
    }

    @Override
    public ItemStack getIcon() {
        return CreateItem.createItemStack(
                getName(),
                getDescription(),
                Material.COBWEB
        );
    }
}
