package net.joseplay.allianceutils.api.ystore;

import br.com.ystoreplugins.product.ytops.TopAPIHolder;
import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.api.internalListener.AuListener;
import net.joseplay.allianceutils.api.playerPoints.PlayerPointsApi;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class yTopsApi implements AuListener {
    private String vaultTopID = "ALC_UTILS_VAULT";
    private String pointsTopID = "ALC_UTILS_POINTS";

    private void registerTop(JavaPlugin plugin){
        RegisteredServiceProvider<TopAPIHolder> rsp = Bukkit.getServer().getServicesManager().getRegistration(TopAPIHolder.class);
        if (rsp == null) return;
        TopAPIHolder provider = rsp.getProvider();

        provider.setup(vaultTopID);
        getTopPlayersAsync().thenAccept(topPlayer -> {
            provider.update(vaultTopID, topPlayer);
        });

        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")){
            PlayerPointsApi pointsApi = new PlayerPointsApi();
            provider.setup(pointsTopID);
            provider.update(pointsTopID, pointsApi.getTop());
        }

        Bukkit.getScheduler().runTaskTimer(plugin, this::updateTop, 0, 60 * 20);
    }

    public void updateTop(){
        RegisteredServiceProvider<TopAPIHolder> rsp = Bukkit.getServer().getServicesManager().getRegistration(TopAPIHolder.class);
        if (rsp == null || rsp.getProvider() == null) return;
        TopAPIHolder provider = rsp.getProvider();

        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")){
            PlayerPointsApi pointsApi = new PlayerPointsApi();
            provider.update(pointsTopID, pointsApi.getTop());
        }

        getTopPlayersAsync().thenAccept(topPlayer -> {
            provider.update(vaultTopID, topPlayer);
        });
    }

    public LinkedHashMap<String, Double> getTopPlayers() {
        List<OfflinePlayer> players = Arrays.asList(Bukkit.getOfflinePlayers());
        Economy economy = Allianceutils.getEconomy();

        return players.stream()
                .sorted((p1, p2) -> Double.compare(economy.getBalance(p2), economy.getBalance(p1)))
                .limit(5)
                .collect(Collectors.toMap(
                        OfflinePlayer::getName,
                        economy::getBalance,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    public CompletableFuture<HashMap<String, Double>> getTopPlayersAsync() {
        return CompletableFuture.supplyAsync(() -> {
            List<OfflinePlayer> players = Arrays.asList(Bukkit.getOfflinePlayers());
            Economy economy = Allianceutils.getEconomy();

            HashMap<String, Double> topPlayers = players.stream()
                    .sorted((p1, p2) -> Double.compare(economy.getBalance(p2), economy.getBalance(p1)))
                    .limit(20)
                    .collect(Collectors.toMap(
                            OfflinePlayer::getName,
                            economy::getBalance,
                            (a, b) -> a,
                            HashMap::new
                    ));

            return topPlayers;
        });
    }

    /**@AuEventHandler
    public void onPluginBootEvent(PluginBootEvent event){
        if (event.getPlugin() == Allianceutils.getInstance()){
            registerTop(event.getPlugin());
        }
    }*/
}
