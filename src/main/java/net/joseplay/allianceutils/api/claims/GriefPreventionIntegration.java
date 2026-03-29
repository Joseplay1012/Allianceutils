package net.joseplay.allianceutils.api.claims;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GriefPreventionIntegration {

    /**
     * Checks whether a given location is inside a GriefPrevention claim.
     *
     * @param location the location to check
     * @return true if the location is within a claim, false otherwise or if the plugin is unavailable
     */
    public boolean isClaimRegion(Location location) {
        try {
            if (Bukkit.getPluginManager().getPlugin("GriefPrevention") != null) {
                return GriefPrevention.instance.dataStore.getClaimAt(location, false, null) != null;
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§cFailed to access GriefPrevention: " + e.getMessage());
        }

        return false;
    }

    /**
     * Checks whether the specified player is the owner of the claim at a given location.
     *
     * @param player   the player to check
     * @param location the location of the claim
     * @return true if the player owns the claim, false otherwise or if no claim exists
     */
    public boolean isPlayerClaimOwner(Player player, Location location) {
        try {
            if (Bukkit.getPluginManager().getPlugin("GriefPrevention") != null) {
                Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, false, null);
                return claim != null && claim.getOwnerName().equalsIgnoreCase(player.getName());
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§cFailed to access GriefPrevention: " + e.getMessage());
        }

        return false;
    }

    /**
     * Checks whether the player has build permission in the claim at the given location.
     * This includes:
     * - Being the claim owner
     * - Having explicit build trust granted by the claim owner
     *
     * @param player   the player to check
     * @param location the location inside the claim
     * @return true if the player can build, false otherwise or if no claim exists
     */
    public boolean playerCanClaimBuild(Player player, Location location) {

        try {
            if (Bukkit.getPluginManager().getPlugin("GriefPrevention") != null) {
                Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, false, null);
                if (claim != null) {
                    return claim.getOwnerName().equalsIgnoreCase(player.getName()) ||
                            claim.hasExplicitPermission(player.getUniqueId(), ClaimPermission.Build);
                }
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§cFailed to access GriefPrevention: " + e.getMessage());
        }

        return false;
    }
}