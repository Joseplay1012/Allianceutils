package net.joseplay.allianceutils.api.playerPoints;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.black_ixx.playerpoints.libs.rosegarden.hook.PlaceholderAPIHook;
import org.black_ixx.playerpoints.models.SortedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.UUID;

public class PlayerPointsApi {
    public PlayerPoints getPlayerPoints(){
        return PlayerPoints.getInstance();
    }

    public void addPoints(Player player, int amount){
        getPlayerPoints().getAPI().give(player.getUniqueId(), amount);
    }

    public int getPoints(Player player){
        return getPlayerPoints().getAPI().look(player.getUniqueId());
    }

    public int getPoints(UUID uuid){
        return getPlayerPoints().getAPI().look(uuid);
    }

    public void test(Player player){
        PlayerPointsAPI api = PlayerPoints.getInstance().getAPI();
        UUID uuid = player.getUniqueId();

        int amount = api.look(uuid);
        String text = PlaceholderAPIHook.applyPlaceholders(player, "%playerpoints_points_shorthand%");
        Bukkit.broadcastMessage("Before: " + amount + " | " + text);

        api.give(uuid, 1000);

        amount = api.look(uuid);
        text = PlaceholderAPIHook.applyPlaceholders(player, "%playerpoints_points_shorthand%");
        Bukkit.broadcastMessage("After: " + amount + " | " + text);
    }

    public LinkedHashMap<String, Double> getTop(){
        LinkedHashMap<String, Double> linkedHashMap = new LinkedHashMap<>();

        for (SortedPlayer sortedPlayer : getPlayerPoints().getAPI().getTopSortedPoints(5)){
            linkedHashMap.put(sortedPlayer.getUsername(), (double) sortedPlayer.getPoints());
        }

        return linkedHashMap;
    }

    public UUID getTopUUID(){
        SortedPlayer player = getPlayerPoints().getAPI().getTopSortedPoints(1).get(0);

        return player.getUniqueId();
    }
}
