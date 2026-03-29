package net.joseplay.allianceutils.api.extensions.gui;

import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.Utils.UnicodeFontReplace;
import net.joseplay.allianceutils.api.extensions.Alliance;
import net.joseplay.allianceutils.api.extensions.AlliancePlugin;
import net.joseplay.allianceutils.api.menu.CreateItem;
import net.joseplay.allianceutils.api.menu.PagedCustomMenu;
import net.joseplay.allianceutils.api.utils.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static net.joseplay.allianceutils.Utils.UnicodeFontReplace.allianceFontReplace;

public class ExtensionsLoadedsGUI extends PagedCustomMenu {
    private final List<AlliancePlugin> exts;
    private final RowsStyle style;
    private final NamespacedKey EXTENSIONS_KEY = new NamespacedKey(Allianceutils.getPlugin(), "extension_name");

    public ExtensionsLoadedsGUI(RowsStyle style, List<AlliancePlugin> exts) {
        super(style.getRows(), "Extensions", style.getSlots(), style.getNextPage(), style.getPreviusPage());
        this.exts = new ArrayList<>(exts);
        this.style = style;
        setUsePlaceholders(true);
        enableAutoUpdate(20);
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
    public void update() {
        for (int slot : contentSlots) {
            getInventory().setItem(slot, null);
        }

        int start = currentPage * contentSlots.size();
        int end = Math.min(start + contentSlots.size(), pagedItems.size());
        for (int i = start; i < end; i++) {
            ItemStack item = pagedItems.get(i);
            if (item != null) {

                ItemStackUtils.editMeta(item, m -> {
                    if (m.getPersistentDataContainer().has(EXTENSIONS_KEY)){
                        String extensionName = m.getPersistentDataContainer().get(EXTENSIONS_KEY, PersistentDataType.STRING);

                        if (extensionName != null &&
                                exts.stream().anyMatch(ext -> ext.getExtensionName().equalsIgnoreCase(extensionName))){

                            m.setLore(getLore(exts.stream().filter(ext -> ext.getExtensionName().equalsIgnoreCase(extensionName)).findFirst().orElse(null)));
                        }
                    }
                });

                int slot = contentSlots.get(i % contentSlots.size());
                getInventory().setItem(slot, item.clone());
            }
        }

        for (Map.Entry<Integer, ItemStack> entry : fixedItems.entrySet()) {
            getInventory().setItem(entry.getKey(), entry.getValue().clone());
        }

        setNavigator();
        if (usePlaceholders()) setPlaceholders();
    }

    @Override
    public void onSetItems() {
        exts.forEach(ext -> {
            ItemStack itemStack = CreateItem.createItemStack(
                    allianceFontReplace(ext.getExtensionName()),
                    getLore(ext),
                    Material.PAPER
            );

            ItemStackUtils.editMeta(itemStack, m -> {
                m.getPersistentDataContainer().set(EXTENSIONS_KEY, PersistentDataType.STRING, ext.getExtensionName());
            });

            addItem(itemStack, e -> handlerClick(ext, e));
        });

        update();
    }

    public String getRemainingTime(AlliancePlugin ext) {
        ZoneId zoneId = ZoneId.of("America/Sao_Paulo");

        LocalDateTime startTime =
                LocalDateTime.ofInstant(ext.getStartTime(),zoneId)
                        .atZone(zoneId)
                        .toLocalDateTime();

        long totalSeconds = Duration.between(startTime, LocalDateTime.now(zoneId)).getSeconds();
        long days = totalSeconds / 86400L;
        long hours = totalSeconds % 86400L / 3600L;
        long minutes = totalSeconds % 3600L / 60L;
        long seconds = totalSeconds % 60L;

        if (days > 0) {
            return days + "d " + hours + "h " + minutes + "m " + seconds + "s";
        } else if (hours > 0) {
            return hours + "h " + minutes + "m " + seconds + "s";
        } else if (minutes > 0) {
            return minutes + "m " + seconds + "s";
        } else {
            return seconds + "s";
        }
    }

    public void updateGUI(){clearContent();
        clearContent();
        clearFixedItems();
        getInventory().clear();
        onSetItems();
    }

    private void handlerClick(AlliancePlugin ext, InventoryClickEvent clickEvent){
        if (ext == null) return;
        if (!clickEvent.getWhoClicked().hasPermission("alc.admin")) return;

        if (clickEvent.isLeftClick()){
            Allianceutils.getInstance().extensionLoader.reloadExtension(ext.getExtensionName(), e -> {
                clickEvent.getWhoClicked().sendMessage("§aExtension §e" + e.extensionName + " §a reloaded.");
                updateGUI();
            });
        } else if (clickEvent.isRightClick()){
            Allianceutils.getInstance().extensionLoader.unloadExtension(ext.getExtensionName(), e -> {
                exts.remove(ext);
                clickEvent.getWhoClicked().sendMessage("§aExtension §e" + e.extensionName + " §a unloaded.");
                updateGUI();
            });
        }
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


    public List<String> getLore(AlliancePlugin ext){

        if (ext == null) return List.of("§cinvalid extension.");

        boolean enabled = Allianceutils.getInstance()
                .extensionLoader.getActiveExtensions().contains(ext.getExtensionName());

        int commands = Alliance.getAllianceCommandManager()
                .getAllCommandNames(ext).size();

        int listeners = 0;

        List<Listener> listenerList = Alliance.getAllianceListenerManager()
                .getListeners(ext);
        if (listenerList != null) listeners = listenerList.size();

        int tasks = ext.activeTasks.size();

        return Stream.of(
                "§eDescription: §a" + ext.getExtensionDescription(),
                "§eAuthor: §a" + ext.getExtensionAuthors(),
                "§eVersion: §a" + ext.getExtensionVersion(),
                "§eRegistred commands: §a" + commands,
                "§eRegistred events: §a" + listeners,
                "§eActive tasks: §a" + tasks,
                "§eUpTime: §a" + getRemainingTime(ext),
                "§eStatus: §a" + (enabled ? "§aActived" : "§cInative"),
                "",
                "§eLeftClick to §areload.",
                "§eRightClick to §aunload."
        ).map(UnicodeFontReplace::allianceFontReplace).toList();
    }
}
