package net.joseplay.allianceutils.api.extensions;

import net.joseplay.allianceutils.Allianceutils;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the registration and lifecycle of Bukkit event listeners
 * associated with Alliance extensions.
 *
 * <p>This class provides centralized control over listener registration,
 * allowing listeners to be grouped and managed per {@link AlliancePlugin}.</p>
 */
public class AllianceListenerManager {

    /**
     * Reference to the main plugin instance used for event registration.
     */
    public final JavaPlugin plugin = Allianceutils.getPlugin();

    /**
     * Stores listeners grouped by their owning extension.
     *
     * <p>Uses {@link ConcurrentHashMap} to ensure thread-safe access
     * in dynamic registration scenarios.</p>
     */
    public Map<AlliancePlugin, List<Listener>> listeners = new ConcurrentHashMap<>();

    /**
     * Registers a listener for a given extension.
     *
     * <p>The listener is registered in Bukkit's event system and
     * stored internally for future management.</p>
     *
     * @param extension the owning extension
     * @param listener  the listener instance to register
     */
    public void registerListener(AlliancePlugin extension, Listener listener) {
        System.out.println("[Alliance] Registering listener: "
                + listener.getClass().getSimpleName()
                + " for extension: " + extension.getExtensionName());

        Bukkit.getPluginManager().registerEvents(listener, plugin);
        listeners.computeIfAbsent(extension, k -> new ArrayList<>()).add(listener);
    }

    /**
     * Unregisters all listeners associated with a given extension.
     *
     * <p>All listeners are removed from Bukkit's handler lists and
     * cleared from internal storage.</p>
     *
     * @param extension the extension whose listeners should be removed
     */
    public void unregisterListeners(AlliancePlugin extension) {
        if (listeners.containsKey(extension)) {
            System.out.println("[Alliance] Unregistering all listeners for extension: "
                    + extension.getExtensionName());

            for (Listener listener : listeners.get(extension)) {
                HandlerList.unregisterAll(listener);
            }

            listeners.remove(extension);
        }
    }

    /**
     * Retrieves all listeners registered for a specific extension.
     *
     * @param extension the extension
     * @return list of listeners, or null if none exist
     */
    public List<Listener> getListeners(AlliancePlugin extension) {
        return listeners.get(extension);
    }

    /**
     * Unregisters a specific listener from an extension.
     *
     * <p>The listener is removed from Bukkit's handler list and
     * from internal tracking.</p>
     *
     * @param extension the owning extension
     * @param listener  the listener to unregister
     */
    public void unregisterListener(AlliancePlugin extension, Listener listener) {
        if (listeners.containsKey(extension)) {
            System.out.println("[Alliance] Unregistering listener: "
                    + listener.getClass().getSimpleName()
                    + " from extension: " + extension.getExtensionName());

            HandlerList.unregisterAll(listener);
            listeners.get(extension).remove(listener);
        }
    }

    /**
     * Unregisters all listeners globally for the given plugin.
     *
     * <p>This will remove every listener associated with the plugin
     * from Bukkit and clear the internal registry completely.</p>
     *
     * @param plugin the plugin whose listeners should be removed
     */
    public void unregisterAllListeners(JavaPlugin plugin) {
        System.out.println("[Alliance] Unregistering ALL listeners for plugin: "
                + plugin.getName());

        HandlerList.unregisterAll(plugin);
        listeners.clear();
    }
}