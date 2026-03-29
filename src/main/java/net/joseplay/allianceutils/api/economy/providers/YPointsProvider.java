package net.joseplay.allianceutils.api.economy.providers;

import net.joseplay.allianceutils.api.economy.PointsProvider;
import net.joseplay.allianceutils.api.ystore.yPointsApi;
import org.bukkit.entity.Player;

import java.util.UUID;

public class YPointsProvider implements PointsProvider {

    private final yPointsApi api = new yPointsApi();

    @Override
    public void add(Player player, int amount) {
        api.addPoints(player, amount);
    }

    @Override
    public int get(Player player) {
        return api.getPoints(player);
    }

    @Override
    public int get(UUID uuid) {
        return api.getPoints(uuid);
    }

    @Override
    public UUID getTop() {
        return api.getTop();
    }
}