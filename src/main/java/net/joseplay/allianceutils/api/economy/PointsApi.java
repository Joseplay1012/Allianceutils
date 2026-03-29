package net.joseplay.allianceutils.api.economy;

import org.bukkit.entity.Player;

import java.util.UUID;

public class PointsApi {

    private final PointsProvider provider;

    public PointsApi() {
        this.provider = PointsProviderResolver.resolve();

        if (this.provider == null) {
            throw new IllegalStateException("No points provider found (yPoints or PlayerPoints).");
        }
    }

    public void addPlayerPoints(Player player, int amount){
        provider.add(player, amount);
    }

    public int getPlayerPoints(Player player){
        return provider.get(player);
    }

    public int getPlayerPoints(UUID uuid){
        return provider.get(uuid);
    }

    public UUID getTopPoints(){
        return provider.getTop();
    }
}