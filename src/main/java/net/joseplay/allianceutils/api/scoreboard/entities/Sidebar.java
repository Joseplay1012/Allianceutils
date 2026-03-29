package net.joseplay.allianceutils.api.scoreboard.entities;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.scoreboard.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Abstract representation of a player sidebar (scoreboard).
 *
 * <p>This class provides a unified abstraction layer for different
 * scoreboard implementations (packet-based or Bukkit-based).</p>
 *
 * <p>It also integrates with the TAB plugin to disable its scoreboard
 * when a custom sidebar is applied.</p>
 */
public abstract class Sidebar {

    /**
     * Target player for this sidebar instance.
     */
    protected final Player player;

    /**
     * Constructs a new sidebar instance for the given player.
     *
     * <p>If the TAB plugin is present, its scoreboard is disabled
     * to prevent conflicts with this custom implementation.</p>
     *
     * @param player the player associated with this sidebar
     */
    protected Sidebar(Player player) {
        this.player = player;

        if (Bukkit.getPluginManager().isPluginEnabled("TAB")) {
            TabAPI api = TabAPI.getInstance();
            ScoreboardManager sbm = api.getScoreboardManager();
            if (sbm != null) {
                TabPlayer tabPlayer = api.getPlayer(player.getUniqueId());
                if (tabPlayer != null){
                    sbm.setScoreboardVisible(tabPlayer, false, false);
                }
            }
        }
    }

    /**
     * Updates the sidebar title.
     *
     * @param title new title value
     */
    public abstract void setTitle(String title);

    /**
     * Sets or updates a line at a specific position.
     *
     * @param line line index (position)
     * @param text content to display
     */
    public abstract void setLine(int line, String text);

    /**
     * Removes a specific line from the sidebar.
     *
     * @param line line index to remove
     */
    public abstract void removeLine(int line);

    /**
     * Fully removes the sidebar from the player.
     *
     * <p>This should clean all resources and restore previous state if necessary.</p>
     */
    public abstract void remove();

    /**
     * Restores the default scoreboard behavior for the player.
     *
     * <p>If TAB is installed, its scoreboard will be re-enabled.
     * Otherwise, the main Bukkit scoreboard will be applied.</p>
     */
    public void setDefaultSB() {
        if (!Bukkit.getPluginManager().isPluginEnabled("TAB")) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            return;
        }

        TabAPI api = TabAPI.getInstance();
        ScoreboardManager sbm = api.getScoreboardManager();
        if (sbm == null) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            return;
        }

        TabPlayer tabPlayer = api.getPlayer(player.getUniqueId());
        if (tabPlayer == null) return;

        sbm.resetScoreboard(tabPlayer);
        sbm.setScoreboardVisible(tabPlayer, true, false);
    }

    /**
     * Clears all lines from the sidebar without removing it.
     */
    public abstract void clear();

    /**
     * Factory method for creating a sidebar implementation.
     *
     * <p>If PacketEvents is available, a packet-based implementation is used.
     * Otherwise, a Bukkit-based implementation is returned.</p>
     *
     * @param player target player
     * @param title initial title
     * @return concrete sidebar implementation
     */
    public static Sidebar create(Player player, String title) {

        if (Bukkit.getPluginManager().getPlugin("packetevents") != null) {
            return new PacketSidebar(player, title);
        }

        return new BukkitSidebar(player, title);
    }
}