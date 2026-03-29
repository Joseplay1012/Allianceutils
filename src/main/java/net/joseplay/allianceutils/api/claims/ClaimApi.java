package net.joseplay.allianceutils.api.claims;

import br.com.ystoreplugins.product.yterrenos.TerrenoAPIHolder;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.joseplay.allianceutils.Allianceutils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * API that provides information about land protection.
 */
public class ClaimApi {
    /**
     * Check if the location is in a region protected by WorldGuard.
     *
     * @param location The location to be verified.
     * @return True if in a protected region, false otherwise.
     */
    public static boolean isRegion(Location location) {
        try {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regionManager = container.get(BukkitAdapter.adapt(location.getWorld()));
            if (regionManager != null) {
                ApplicableRegionSet regionSet = regionManager.getApplicableRegions(
                        BlockVector3.at(location.getX(), location.getY(), location.getZ())
                );
                return !regionSet.getRegions().isEmpty();
            }
        } catch (NoClassDefFoundError e) {
            Bukkit.getConsoleSender().sendMessage("§cError accessing WorldGuard!");
            return false;
        }
        return false;
    }

    /**
     * Check if PvP is allowed in the region where the location is situated.
     *
     * @param location The location to be verified.
     * @return True if PvP is allowed, false otherwise.
     */
    public static boolean allowPvPRegion(Location location) {
        //Se não for uma rg ele retorna true, para permitir o pvp
        if (!isRegion(location)) return true;

        try {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();

            com.sk89q.worldedit.util.Location weLocation = BukkitAdapter.adapt(location);

            //Se for uma rg, e estiver com o pvp ativo ele retorna true
            if (query.testState(weLocation, null, Flags.PVP)) return true;
        } catch (NoClassDefFoundError e) {
            Bukkit.getConsoleSender().sendMessage("§cError accessing WorldGuard!");
            return false;
        }

        //Se for uma rg e não estiver com o pvp ativo, retorna false
        return false;
    }

    /**
     * Checks if a {@link StateFlag} is allowed in the region where the location is situated.
     *
     * @param location The location to be verified.
     * @param flags The flag that will be tested
     * @return True if the flag is allowed or is not a region, false otherwise.
     */
    public static boolean allowFlagRegion(Location location, StateFlag flags) {
        if (!isRegion(location)) return true;

        try {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();

            com.sk89q.worldedit.util.Location weLocation = BukkitAdapter.adapt(location);

            if (query.testState(weLocation, null, flags)) return true;
        } catch (NoClassDefFoundError e) {
            Bukkit.getConsoleSender().sendMessage("§cError accessing WorldGuard!");
            return false;
        }
        return false;
    }

    /**
     * Check if the location is included in a claim (GriefPrevention or HuskClaims).
     *
     * @param location The location to be verified.
     * @return True if it's in a claim, false otherwise.
     */
    public boolean isClaimRegion(Location location) {
        try {
            if (Bukkit.getPluginManager().getPlugin("GriefPrevention") != null) {
                return new GriefPreventionIntegration().isClaimRegion(location);
            } else if (Bukkit.getPluginManager().getPlugin("HuskClaims") != null) {
                return new HuskClaimsIntegration().isClaimRegion(location);
            } else if (Bukkit.getServer().getServicesManager().getRegistration(TerrenoAPIHolder.class) != null){
                return new YTerrenosIntegration().isClaimRegion(location);
            }
        } catch (NoClassDefFoundError e) {
            Allianceutils.getInstance().getLogger()
                    .warning("claim plugin not found!");
            return false;
        }

        return false;
    }

    /**
     * Checks if the player owns a piece of land.
     *
     * @param player   The player to be verified.
     * @param location The location of the land.
     * @return True if you are the owner, false otherwise.
     */
    public boolean isPlayerClaimOwner(Player player, Location location) {
        try {
            if (!isClaimRegion(location)) return false;
            if (Bukkit.getPluginManager().getPlugin("GriefPrevention") != null) {
                return new GriefPreventionIntegration().isPlayerClaimOwner(player, location);
            } else if (Bukkit.getPluginManager().getPlugin("HuskClaims") != null) {
                return new HuskClaimsIntegration().isPlayerClaimOwner(player, location);
            } else if (Bukkit.getServer().getServicesManager().getRegistration(TerrenoAPIHolder.class) != null){
                return new YTerrenosIntegration().isPlayerClaimOwner(player, location);
            }
        } catch (NoClassDefFoundError e) {
            Allianceutils.getInstance().getLogger()
                    .warning("Sem plugin de claims!");
            return false;
        }

        return false;
    }

    /**
     * Checks if the player has permission to build on the land.
     *
     * @param player   The player to be verified.
     * @param location The location of the land.
     * @return True if permitted, false otherwise.
     */
    public boolean playerCanClaimBuild(Player player, Location location) {
        try {
            if (!isClaimRegion(location)) return false;

            if (Bukkit.getPluginManager().getPlugin("GriefPrevention") != null) {
                return new GriefPreventionIntegration().playerCanClaimBuild(player, location);
            } else if (Bukkit.getPluginManager().getPlugin("HuskClaims") != null) {
                return new HuskClaimsIntegration().playerCanClaimBuild(player, location);
            } else if (Bukkit.getServer().getServicesManager().getRegistration(TerrenoAPIHolder.class) != null){
                return new YTerrenosIntegration().playerCanClaimBuild(player, location);
            }
        } catch (NoClassDefFoundError e) {
            Allianceutils.getInstance().getLogger()
                    .warning("claim plugin not found!");
            return false;
        }

        return false;
    }
}