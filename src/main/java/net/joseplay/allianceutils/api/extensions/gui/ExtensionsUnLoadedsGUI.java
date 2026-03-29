package net.joseplay.allianceutils.api.extensions.gui;

import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.Utils.UnicodeFontReplace;
import net.joseplay.allianceutils.api.extensions.AlliancePlugin;
import net.joseplay.allianceutils.api.extensions.ExtensionContainer;
import net.joseplay.allianceutils.api.menu.CreateItem;
import net.joseplay.allianceutils.api.menu.PagedCustomMenu;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static net.joseplay.allianceutils.Utils.UnicodeFontReplace.allianceFontReplace;

public class ExtensionsUnLoadedsGUI extends PagedCustomMenu {
    private final RowsStyle style;

    public ExtensionsUnLoadedsGUI(RowsStyle style) {
        super(style.getRows(), "Extensions", style.getSlots(), style.getNextPage(), style.getPreviusPage());
        this.style = style;
        setUsePlaceholders(true);
    }

    @Override
    public ItemStack getNextItem() {
        return CreateItem.createItemStack("§aNext page §7→", List.of("§eclick to next page"), Material.LIME_DYE);
    }

    @Override
    public ItemStack getPreviousItem() {
        return CreateItem.createItemStack("§7← §aPrevious page", List.of("§eclick to previous page"), Material.RED_DYE);
    }

    @Override
    public void setNavigator() {
        getInventory().setItem(getPreviousSlot(), currentPage >= 0 ? getPreviousItem() : null);
        getInventory().setItem(getNextSlot(), currentPage <= maxPage ? getNextItem() : null);
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
        List<File> files = new ArrayList<>();

        for (File file : Allianceutils.getInstance().extensionLoader.folder.listFiles((file, name) -> name.endsWith(".jar"))){
            if (Allianceutils.getInstance().extensionLoader.getRegistry().containsKey(file.getName())) continue;
            files.add(file);
        }

        files.forEach(extFile -> {
            ExtensionContainer container = Allianceutils.getInstance().
                    extensionLoader.getExtensionContainer(extFile);

            if (container == null) return;

            if (container.extension instanceof AlliancePlugin ext) {


                boolean enabled = Allianceutils.getInstance()
                        .extensionLoader.getActiveExtensions().contains(ext.getExtensionName());

                ItemStack itemStack = CreateItem.createItemStack(
                        allianceFontReplace(ext.getExtensionName()),
                        Stream.of(
                                "§eDescription: §a" + ext.getExtensionDescription(),
                                "§eAuthor: §a" + ext.getExtensionAuthors(),
                                "§eVersion: §a" + ext.getExtensionVersion(),
                                "§eStatus: §a" + (enabled ? "§aActived" : "§cInative"),
                                "",
                                "§eClick to load."
                        ).map(UnicodeFontReplace::allianceFontReplace).toList(),
                        Material.PAPER
                );

                addItem(itemStack, e -> handlerClick(container, e));
            }
        });

        update();
    }

    public void updateGUI(){clearContent();
        clearFixedItems();
        getInventory().clear();
        onSetItems();
    }

    private void handlerClick(ExtensionContainer container, InventoryClickEvent clickEvent){
        if (container == null) return;
        if (!clickEvent.getWhoClicked().hasPermission("alc.admin")) return;

        Allianceutils.getInstance().extensionLoader.loadExtension(container, e -> {
            clickEvent.getWhoClicked().sendMessage("§aExtension §e" + e.extensionName + " §areloaded.");
            updateGUI();
        });
    }

    @Override
    public void setPlaceholders() {
        super.setPlaceholders();
        for (int i : contentSlots) {
            if (getInventory().getItem(i) != null) {
                ItemStack itemStack = getInventory().getItem(i);

                if (itemStack.hasItemMeta() && itemStack.getItemMeta().getPersistentDataContainer().has(PLACEHOLDER_KEY)){
                    getInventory().setItem(i, null);
                }
            }
        }
    }

    @Override
    public void click(InventoryClickEvent e, int slot) {
        unCheckedClick(e);
        Player p = (Player) e.getWhoClicked();

        // Navegação
        if (slot == getPreviousSlot()) {
            if (currentPage <= 0) {
                new ExtensionGUI().open(player);
                return;
            }

            currentPage--;
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1f, 1.5f);
            update();
            return;
        }
        if (slot == getNextSlot() && currentPage < maxPage) {
            currentPage++;
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1f, 1.8f);
            update();
            return;
        }

        // Item fixo
        if (fixedActions.containsKey(slot)) {
            Consumer<InventoryClickEvent> action = fixedActions.get(slot);
            action.accept(e);
            return;
        }

        // Item paginado
        if (contentSlots.contains(slot)) {
            int localIndex = contentSlots.indexOf(slot);
            int globalIndex = currentPage * contentSlots.size() + localIndex;
            Consumer<InventoryClickEvent> action = pagedActions.get(globalIndex);
            if (action != null) {
                action.accept(e);
            }
        }
    }
}
