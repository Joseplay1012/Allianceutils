package net.joseplay.allianceutils.api.preferences.interfaces;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface PreferencesCategory {
    String getName();
    List<String> getDescription();
    String getID();
    ItemStack getIcon();
}
