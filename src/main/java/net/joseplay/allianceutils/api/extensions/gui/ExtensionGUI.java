package net.joseplay.allianceutils.api.extensions.gui;

import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.Utils.UnicodeFontReplace;
import net.joseplay.allianceutils.api.extensions.AlliancePlugin;
import net.joseplay.allianceutils.api.menu.CreateItem;
import net.joseplay.allianceutils.api.menu.SimpleMenu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static net.joseplay.allianceutils.Utils.UnicodeFontReplace.allianceFontReplace;

public class ExtensionGUI extends SimpleMenu {
    public ExtensionGUI() {
        super(Rows.THREE_LINE, "Extensions");
        setUsePlaceholders(true);
    }



    @Override
    public void createPlaceholdeItem() {
        PLACEHOLDER_ITEM = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
        ItemMeta meta = PLACEHOLDER_ITEM.getItemMeta();
        meta.setDisplayName(" ");
        meta.setHideTooltip(true);
        meta.getPersistentDataContainer().set(PLACEHOLDER_KEY, PersistentDataType.BOOLEAN, true);
        PLACEHOLDER_ITEM.setItemMeta(meta);
    }

    @Override
    public void onSetItems() {
        var loader = Allianceutils.getInstance().extensionLoader;
        
        int loaddeds = loader.getActiveExtensions().size();

        int noloaddeds = (int) Arrays
                .stream(loader.folder.listFiles((file, name) -> name.endsWith(".jar")))
                .filter(file -> !loader.getRegistry().containsKey(file.getName())).count();

        ItemStack loads = CreateItem.createItemStack(
                allianceFontReplace("§aLoaded extensions."),
                Stream.of(
                        "§eCurrent §a" + loaddeds + " §e Loaded extensions."
                ).map(UnicodeFontReplace::allianceFontReplace).toList(),
                Material.LIME_WOOL
        );

        ItemStack unLoads = CreateItem.createItemStack(
                allianceFontReplace("§a Unloaded extensions."),
                Stream.of(
                        "§eCurrent §c" + noloaddeds + " §eExtensões descarregadas."
                ).map(UnicodeFontReplace::allianceFontReplace).toList(),
                Material.RED_WOOL
        );

        setItem(12, loads, e -> {
            List<AlliancePlugin> exts = Allianceutils.getInstance()
                    .extensionLoader
                    .getRegistry().values().stream()
                    .filter(container -> container.extension instanceof AlliancePlugin)
                    .map(ext -> (AlliancePlugin) ext.extension)
                    .toList();

            new ExtensionsLoadedsGUI(RowsStyle.COMPACT, exts).open(player);
        });

        setItem(14, unLoads, e -> {
            if (noloaddeds == 0) return;

            new ExtensionsUnLoadedsGUI(RowsStyle.COMPACT).open(player);
        });

    }
}
