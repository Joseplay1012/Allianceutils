package net.joseplay.allianceutils.Statics;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

public class PotStackManager {
    private static final Map<Player, Boolean> potStackingPlayers = new HashMap<>();

    public static void setPotStacking(Player player, boolean stacking) {
        potStackingPlayers.put(player, stacking);
    }

    public static boolean isPotStacking(Player player) {
        return potStackingPlayers.getOrDefault(player, false);
    }
}
