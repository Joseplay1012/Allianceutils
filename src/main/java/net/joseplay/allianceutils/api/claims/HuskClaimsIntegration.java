package net.joseplay.allianceutils.api.claims;

import net.william278.huskclaims.api.BukkitHuskClaimsAPI;
import net.william278.huskclaims.claim.Claim;
import net.william278.huskclaims.libraries.cloplib.operation.OperationType;
import net.william278.huskclaims.position.Position;
import net.william278.huskclaims.user.OnlineUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;

public class HuskClaimsIntegration {

    /**
     * Checks whether a given location is inside a HuskClaims claim.
     *
     * @param location the location to check
     * @return true if the location is within a claim, false otherwise or if the plugin is unavailable
     */
    public boolean isClaimRegion(Location location) {
        try {
            if (Bukkit.getPluginManager().getPlugin("HuskClaims") != null) {
                return BukkitHuskClaimsAPI.getInstance()
                        .getClaimAt(BukkitHuskClaimsAPI.getInstance().getPosition(location))
                        .isPresent();
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§cFailed to access HuskClaims: " + e.getMessage());
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
            if (Bukkit.getPluginManager().getPlugin("HuskClaims") != null) {
                Optional<Claim> claim = BukkitHuskClaimsAPI.getInstance()
                        .getClaimAt(BukkitHuskClaimsAPI.getInstance().getPosition(location));

                return claim.isPresent()
                        && claim.get().getOwner().isPresent()
                        && claim.get().getOwner().get().equals(player.getUniqueId());
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§cFailed to access HuskClaims: " + e.getMessage());
        }

        return false;
    }

    /**
     * Checks whether the player has build permission within the claim at the given location.
     * This includes:
     * - Being the claim owner
     * - Being explicitly trusted in the claim
     *
     * @param player   the player to check
     * @param location the location inside the claim
     * @return true if the player can build, false otherwise or if no claim exists
     */
    public boolean playerCanClaimBuild(Player player, Location location) {
        try {
            if (Bukkit.getPluginManager().getPlugin("HuskClaims") != null) {
                Position position = BukkitHuskClaimsAPI.getInstance().getPosition(location);
                Optional<Claim> claim = BukkitHuskClaimsAPI.getInstance().getClaimAt(position);

                if (isPlayerClaimOwner(player, location)) return true;

                return claim.isPresent()
                        && claim.get().getTrustedUsers().containsKey(player.getUniqueId());
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§cFailed to access HuskClaims: " + e.getMessage());
        }

        return false;
    }
}