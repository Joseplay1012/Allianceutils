package net.joseplay.allianceutils.api.combat;

import br.com.ystoreplugins.bootstrap.BukkitBootstrap;
import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.api.combat.handlers.CombatHandler;
import net.joseplay.allianceutils.api.combat.handlers.CombatLogX.CombatLogXHandler;
import net.joseplay.allianceutils.api.combat.handlers.yCombatLog.YCombatLogHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CombatVerifier {

    private CombatHandler handler;
    private boolean attemptedLoad = false;

    /**
     * Attempts to resolve a compatible combat log provider.
     * This method is safe to call multiple times (lazy + retry).
     */
    private void resolveHandler() {
        if (handler != null) return;

        Plugin combatLogX = Bukkit.getPluginManager().getPlugin("CombatLogX");
        if (combatLogX != null && combatLogX.isEnabled()) {
            handler = new CombatLogXHandler();
            logInfo("Using combat log provider: " + handler.getName());
            return;
        }

        Plugin yPlugins = Bukkit.getPluginManager().getPlugin("yPlugins");
        if (yPlugins != null && yPlugins.isEnabled()) {
            if (BukkitBootstrap.isActivated("yCombatLog")) {
                handler = new YCombatLogHandler();
                logInfo("Using combat log provider: " + handler.getName());
                return;
            }
        }

        // Only log once to avoid spam
        if (!attemptedLoad) {
            attemptedLoad = true;
            logWarning("No supported combat log plugin found. Features will be disabled.");
        }
    }

    /**
     * Checks whether the given player is currently in combat.
     *
     * @param player the player to check
     * @return true if in combat, false otherwise
     */
    public boolean isCombat(Player player) {
        resolveHandler();
        if (handler == null) return false;

        return handler.isCombat(player);
    }

    /**
     * Adds two players to combat state.
     *
     * @param damaged the player who received damage
     * @param damager the player who caused the damage
     * @return true if applied successfully, false otherwise
     */
    public boolean addCombat(Player damaged, Player damager) {
        resolveHandler();
        if (handler == null) return false;

        return handler.addCombat(damaged, damager);
    }

    /**
     * Removes a player from combat state.
     *
     * @param player the player to remove
     * @return true if removed successfully, false otherwise
     */
    public boolean removeCombat(Player player) {
        resolveHandler();
        if (handler == null) return false;

        return handler.removeCombat(player);
    }

    /**
     * Logs an info message using the plugin logger.
     */
    private void logInfo(String msg) {
        Allianceutils.getInstance().getLogger().info("[CombatVerifier] " + msg);
    }

    /**
     * Logs a warning message using the plugin logger.
     */
    private void logWarning(String msg) {
        Allianceutils.getInstance().getLogger().warning("[CombatVerifier] " + msg);
    }
}