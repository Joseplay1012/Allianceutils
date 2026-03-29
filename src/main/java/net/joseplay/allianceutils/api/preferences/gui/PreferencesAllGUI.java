package net.joseplay.allianceutils.api.preferences.gui;

import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.api.menu.CreateItem;
import net.joseplay.allianceutils.api.menu.PagedCustomMenu;
import net.joseplay.allianceutils.api.preferences.data.PreferencesManager;
import net.joseplay.allianceutils.api.preferences.entities.PreferenceEntity;
import net.joseplay.allianceutils.api.preferences.interfaces.Preference;
import net.joseplay.allianceutils.api.preferences.interfaces.PreferencePermission;
import net.joseplay.allianceutils.api.preferences.interfaces.PreferencesCategory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import static net.joseplay.allianceutils.Utils.UnicodeFontReplace.allianceFontReplace;

public class PreferencesAllGUI extends PagedCustomMenu {
    private final String regex = "([§\u00A7][xX][§\u00A70-9a-fA-F]{6}|[§\u00A7][0-9a-fk-orA-FK-OR])";
    private int giveaway = 0;
    private Rows rows;

    public PreferencesAllGUI(Rows rows, String title, List<Integer> contentSlots, int nextSlot, int previousSlot) {
        super(rows, title, contentSlots, nextSlot, previousSlot);
        this.rows = rows;
        setUsePlaceholders(true);
        giveawaySlot();
    }

    public void giveawaySlot(){
        List<Integer> slotsToGiveAway = new ArrayList<>();

        for (int i = 0; i < rows.getSize(); i++) {
            if (contentSlots.contains(i)) continue;
            slotsToGiveAway.add(i);
        }


        giveaway = slotsToGiveAway.get(ThreadLocalRandom.current().nextInt(slotsToGiveAway.size()));
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
    public void setNavigator() {
        getInventory().setItem(getPreviousSlot(), currentPage >= 0 ? getPreviousItem() : null);
        getInventory().setItem(getNextSlot(), currentPage <= maxPage ? getNextItem() : null);
    }

    @Override
    public void onSetItems() {
        if (Allianceutils.getInstance().getFloodgateApi() != null){
            if (Allianceutils.getInstance().getFloodgateApi().isFloodgatePlayer(player.getUniqueId())){
                setUsePlaceholders(false);
            }
        }

        if (PreferencesManager.getPreferences().isEmpty()) {
            setItem(0, CreateItem.createItemStack(
                    allianceFontReplace("§cempty"),
                    List.of(),
                    Material.BARRIER
            ), event -> {
                new PreferencesCategoriesGUI().open(player);
            });
            update();
            return;
        }

        List<String> addedItems = new ArrayList<>();

        try {
            for(Map.Entry<PreferencesCategory, List<Preference>> entry : PreferencesManager.getPreferences().entrySet()) {
                PreferencesCategory category = entry.getKey();

                for (Preference p : entry.getValue()) {
                    PreferenceEntity preferenceEntity = p.get(player.getUniqueId());

                    ItemStack icon = preferenceEntity.getIcon();

                    if (icon.hasItemMeta() && icon.getItemMeta().hasDisplayName()){
                        if (addedItems.contains(icon.getItemMeta().getDisplayName())) continue;
                        addedItems.add(icon.getItemMeta().getDisplayName());
                    }

                    PreferencePermission preferencePermission;

                    if (p instanceof PreferencePermission pfp) {
                        preferencePermission = pfp;
                    } else {
                        preferencePermission = null;
                    }

                    icon.editMeta(m -> {
                        parseLore(m, preferencePermission, category);
                    });

                    if (preferencePermission != null && preferencePermission.getPermission() != null) {
                        addItem(icon, event -> {
                            if (!player.hasPermission(preferencePermission.getPermission())) {
                                event.getWhoClicked().sendMessage(allianceFontReplace("§cyou don't have access."));
                                return;
                            }
                            preferenceEntity.getAction().accept(event);
                            update();
                        });

                        continue;
                    }

                    addItem(icon, event -> {
                        preferenceEntity.getAction().accept(event);
                        update();
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            addItem(CreateItem.createItemStack("§cInvalid preference!", List.of(), Material.BARRIER));
        }

        fixView();
    }

    public void fixView() {
        for (int slot : contentSlots) {
            getInventory().setItem(slot, null);
        }

        int start = currentPage * contentSlots.size();
        int end = Math.min(start + contentSlots.size(), pagedItems.size());
        for (int i = start; i < end; i++) {
            ItemStack item = pagedItems.get(i);
            if (item != null) {
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
    public void update() {
        clearContent();
        clearFixedItems();
        getInventory().clear();
        onSetItems();
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

    public void parseLore(ItemMeta meta, PreferencePermission preferencePermission, PreferencesCategory category){
        List<String> lore = new ArrayList<>();

        lore.add("§l§8|§r §8" + category.getName().replaceAll(regex, ""));
        lore.add("");

        if (meta.getLore() != null) {
            lore.addAll(meta.getLore());
        }

        if (preferencePermission != null){
            if (preferencePermission.getPermission() != null) {
                if (!player.hasPermission(preferencePermission.getPermission())) {
                    lore.add(allianceFontReplace("§c▪ you don't have access."));
                }
            }
        }

        meta.setLore(lore);
    }

    public void giveAwayRun(){
        if (player == null) return;
        if (!player.isOnline()) return;

        player.playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 5.0f, 1.0f);
        player.getWorld().createExplosion(player.getLocation(), 1, false, false);
        Bukkit.getScheduler().runTaskLater(Allianceutils.getPlugin(), () -> {
            player.closeInventory();
        }, 10);
        player.sendMessage(allianceFontReplace("§cOps..."));
    }

    @Override
    public void click(InventoryClickEvent e, int slot) {
        unCheckedClick(e);
        Player p = (Player) e.getWhoClicked();

        ItemStack clickedItem = getInventory().getItem(slot);

//        if (clickedItem != null && !clickedItem.getType().equals(Material.AIR)){
//            if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().getPersistentDataContainer().has(PLACEHOLDER_KEY)){
//
//                if (slot == giveaway){
//                    clickedItem.setType(Material.TNT);
//                    getInventory().setItem(slot, clickedItem);
//                    giveAwayRun();
//                    return;
//                }
//
//                MaterialVariant.Variant variant = MaterialVariant.getVariant(PLACEHOLDER_ITEM.getType());
//
//                if (variant != null){
//                    Material newMaterial = variant.materials().get(ThreadLocalRandom.current().nextInt(variant.materials().size()));
//
//                    if (newMaterial == null) return;
//                    clickedItem.setType(newMaterial);
//                    getInventory().setItem(slot, clickedItem);
//                    return;
//                }
//            }
//        }

        // Navegação
        if (slot == getPreviousSlot()) {
            if (currentPage <= 0) {
                new PreferencesCategoriesGUI().open(player);
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
}
