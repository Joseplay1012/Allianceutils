package net.joseplay.allianceutils.api.scoreboard.data;

import net.joseplay.allianceutils.api.scoreboard.entities.Sidebar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages {@link Sidebar} instances per player.
 *
 * <p>This class provides a simple cache system to ensure each player
 * has at most one active sidebar instance.</p>
 *
 * <p>All methods are static and operate on a global in-memory cache.</p>
 */
public class SidebarManager {

    /**
     * Stores active sidebars mapped by player UUID.
     */
    private static final Map<UUID, Sidebar> cache = new HashMap<>();

    /**
     * Creates or retrieves an existing sidebar for a player.
     *
     * <p>If a sidebar already exists, its title will be updated.</p>
     *
     * @param player the player
     * @param title the sidebar title
     * @return the existing or newly created sidebar
     */
    public static Sidebar create(Player player, String title) {

        Sidebar sidebar = cache.get(player.getUniqueId());

        if (sidebar == null) {
            sidebar = Sidebar.create(player, title);
            cache.put(player.getUniqueId(), sidebar);
        }

        // Update title regardless of creation
        sidebar.setTitle(title);

        return sidebar;
    }

    /**
     * Retrieves the sidebar of a player.
     *
     * @param player the player
     * @return the sidebar or null if not present
     */
    public static Sidebar get(Player player) {
        return cache.get(player.getUniqueId());
    }

    /**
     * Removes and destroys the sidebar of a player.
     *
     * @param player the player
     */
    public static void remove(Player player) {

        Sidebar sidebar = cache.remove(player.getUniqueId());

        if (sidebar != null) {
            sidebar.remove();
        }
    }

    /**
     * Removes all sidebars and clears the cache.
     *
     * <p>Should be called on plugin shutdown to prevent memory leaks.</p>
     */
    public static void shutdown() {

        for (Sidebar sidebar : cache.values()) {
            sidebar.remove();
        }

        cache.clear();
    }
}