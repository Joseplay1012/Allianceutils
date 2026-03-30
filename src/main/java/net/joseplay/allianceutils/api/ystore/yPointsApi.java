package net.joseplay.allianceutils.api.ystore;

import br.com.ystoreplugins.product.ypoints.PointAPIHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.UUID;

public class yPointsApi {
    public PointAPIHolder yPointsAPI;


    public yPointsApi(){
        this.yPointsAPI = getYpointsApi();
    }


    public PointAPIHolder getYpointsApi(){
        try {
            RegisteredServiceProvider<PointAPIHolder> rsp = Bukkit.getServer().getServicesManager()
                    .getRegistration(PointAPIHolder.class);
            return rsp == null ? null : rsp.getProvider();
        } catch (Throwable var1) {
            return null;
        }
    }

    public void addPoints(Player player, double amount){
        if (player != null){
            yPointsAPI.deposit(player.getName(), amount, true);
        }
    }

    public UUID getTop(){
        return null;
    }

    public int getPoints(Player player){
        return (int) yPointsAPI.getBalance(player.getName());
    }

    public int getPoints(UUID uuid){
        return (int) yPointsAPI.getBalance(Bukkit.getOfflinePlayer(uuid).getName());
    }
}
