package net.joseplay.allianceutils.api.economy;

import net.joseplay.allianceutils.api.economy.providers.PlayerPointsProvider;
import net.joseplay.allianceutils.api.economy.providers.YPointsProvider;
import org.bukkit.Bukkit;

public class PointsProviderResolver {

    public static PointsProvider resolve() {

        if (Bukkit.getPluginManager().isPluginEnabled("yPoints")) {
            return new YPointsProvider();
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            return new PlayerPointsProvider();
        }

        return null;
    }
}