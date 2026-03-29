package net.joseplay.allianceutils.api.preferences.gui;

import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.api.menu.CreateItem;
import net.joseplay.allianceutils.api.menu.PagedCustomMenu;
import net.joseplay.allianceutils.api.preferences.data.PreferencesManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

import static net.joseplay.allianceutils.Utils.UnicodeFontReplace.allianceFontReplace;

public class PreferencesCategoriesGUI extends PagedCustomMenu {
    public PreferencesCategoriesGUI() {
        super(RowsStyle.XADREZ_THREE_LINES.getRows(), "§aPreferences", RowsStyle.XADREZ_THREE_LINES.getSlots(), RowsStyle.XADREZ_THREE_LINES.getNextPage(), RowsStyle.XADREZ_THREE_LINES.getPreviusPage());
        setUsePlaceholders(true);
    }

    @Override
    public void onSetItems() {

        if (Allianceutils.getInstance().getFloodgateApi() != null){
            if (Allianceutils.getInstance().getFloodgateApi().isFloodgatePlayer(player.getUniqueId())){
                setUsePlaceholders(false);
            }
        }


        PreferencesManager.getPreferences().keySet().forEach(category -> {
            addItem(category.getIcon(), event -> {
                new PreferencesGUI(
                        category,
                        RowsStyle.BOOK.getRows(),
                        allianceFontReplace("§aPreferences " + category.getName()),
                        RowsStyle.BOOK.getSlots(),
                        RowsStyle.BOOK.getNextPage(),
                        RowsStyle.BOOK.getPreviusPage())
                        .open(player);
            });
        });

        ItemStack allPrefs = CreateItem.createItemStack(
                allianceFontReplace("§eAll preferences."),
                List.of(),
                Material.CHEST
        );

        setFixedItem(22, allPrefs, event -> {
            new PreferencesAllGUI(
                    RowsStyle.COMPACT_XADREZ.getRows(),
                    allianceFontReplace("§aall preferences"),
                    RowsStyle.COMPACT_XADREZ.getSlots(),
                    RowsStyle.COMPACT_XADREZ.getNextPage(),
                    RowsStyle.COMPACT_XADREZ.getPreviusPage())
                    .open(player);
        });

        update();
    }

    @Override
    public ItemStack getNextItem() {
        return CreateItem.createItemStack("§aNext page §7→", List.of("§eClick to next page"), Material.LIME_DYE);
    }

    @Override
    public ItemStack getPreviousItem() {
        return CreateItem.createItemStack("§7← §aPrevious page", List.of("§eClick to previous page"), Material.RED_DYE);
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
}
