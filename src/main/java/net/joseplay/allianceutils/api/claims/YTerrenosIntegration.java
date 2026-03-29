package net.joseplay.allianceutils.api.claims;

import br.com.ystoreplugins.product.yterrenos.TerrenoAPIHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class YTerrenosIntegration {

    /**
     * Checks whether a given location is inside a yTerrenos claim.
     *
     * @param location the location to check
     * @return true if the location belongs to a terrain, false otherwise or if the API is unavailable
     */
    public boolean isClaimRegion(Location location) {
        try {
            RegisteredServiceProvider<TerrenoAPIHolder> rps =
                    Bukkit.getServer().getServicesManager().getRegistration(TerrenoAPIHolder.class);

            if (rps != null) {
                return rps.getProvider().getTerrenoAt(location) != null;
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§cFailed to access yTerrenos: " + e.getMessage());
        }

        return false;
    }

    /**
     * Checks whether the specified player is the owner of the terrain at a given location.
     *
     * Note: If the location is not inside a claim, this method returns true by design.
     *
     * @param player   the player to check
     * @param location the location of the terrain
     * @return true if the player owns the terrain or if no claim exists, false otherwise
     */
    public boolean isPlayerClaimOwner(Player player, Location location) {
        if (!isClaimRegion(location)) return true;

        try {
            RegisteredServiceProvider<TerrenoAPIHolder> rps =
                    Bukkit.getServer().getServicesManager().getRegistration(TerrenoAPIHolder.class);

            if (rps != null) {
                return rps.getProvider().getTerrenoAt(location).isOwner(player);
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§cFailed to access yTerrenos: " + e.getMessage());
        }

        return false;
    }

    /**
     * Checks whether the player has build permission within the terrain at the given location.
     *
     * Note: If the location is not inside a claim, this method returns true by design.
     *
     * @param player   the player to check
     * @param location the location inside the terrain
     * @return true if the player can build, false otherwise
     */
    public boolean playerCanClaimBuild(Player player, Location location) {
        if (!isClaimRegion(location)) return true;

        try {
            RegisteredServiceProvider<TerrenoAPIHolder> rps =
                    Bukkit.getServer().getServicesManager().getRegistration(TerrenoAPIHolder.class);

            if (rps != null) {
                return rps.getProvider().canPlayerBuild(
                        player,
                        rps.getProvider().getTerrenoAt(location)
                );
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§cFailed to access yTerrenos: " + e.getMessage());
        }

        return false;
    }
}