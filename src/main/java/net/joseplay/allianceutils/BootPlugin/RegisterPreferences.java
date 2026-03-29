package net.joseplay.allianceutils.BootPlugin;

import net.joseplay.allianceutils.api.preferences.categories.EconomyCategory;
import net.joseplay.allianceutils.api.preferences.categories.VipCategory;
import net.joseplay.allianceutils.api.preferences.categories.VisualCategory;
import net.joseplay.allianceutils.api.preferences.data.PreferencesManager;
import net.joseplay.allianceutils.api.preferences.categories.GamePlayCategory;

public class RegisterPreferences {
    public static void registerCategories(){
        GamePlayCategory gamePlayCategory = new GamePlayCategory();
        EconomyCategory economyCategory = new EconomyCategory();
        VipCategory vipCategory = new VipCategory();
        VisualCategory visualCategory = new VisualCategory();

        PreferencesManager.addCategory(gamePlayCategory);
        PreferencesManager.addCategory(economyCategory);
        PreferencesManager.addCategory(vipCategory);
        PreferencesManager.addCategory(visualCategory);
    }
}
