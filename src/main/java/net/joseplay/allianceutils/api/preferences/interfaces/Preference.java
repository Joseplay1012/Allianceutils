package net.joseplay.allianceutils.api.preferences.interfaces;

import net.joseplay.allianceutils.api.menu.PagedCustomMenu;
import net.joseplay.allianceutils.api.preferences.entities.PreferenceEntity;

import java.util.UUID;

public interface Preference {
    PreferenceEntity get(UUID uuid);
    void menu(PagedCustomMenu menu);
}
