package net.joseplay.allianceutils.api.economy;

import org.bukkit.entity.Player;
import java.util.UUID;

public interface PointsProvider {

    void add(Player player, int amount);

    int get(Player player);

    int get(UUID uuid);

    UUID getTop();
}
