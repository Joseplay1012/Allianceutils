package net.joseplay.allianceutils.api.extensions;

import net.joseplay.allianceutils.Allianceutils;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Central access point for Alliance API components.
 *
 * <p>This class acts as a static façade providing global access to
 * core managers such as listeners and commands. It abstracts the
 * internal initialization and ensures a single shared instance
 * (singleton-like behavior) of each manager.</p>
 */
public class Alliance {

    /**
     * Reference to the owning plugin instance.
     *
     * <p>Resolved via {@link JavaPlugin#getProvidingPlugin(Class)} to avoid
     * manual dependency injection and ensure correct plugin context.</p>
     */
    private JavaPlugin plugin = JavaPlugin.getProvidingPlugin(Allianceutils.class);

    /**
     * Global listener manager responsible for dynamic registration
     * and lifecycle control of Bukkit event listeners.
     *
     * <p>Declared as static to guarantee a single shared instance
     * across the entire runtime.</p>
     */
    private final static AllianceListenerManager allianceListenerManager = new AllianceListenerManager();

    /**
     * Global command manager responsible for handling custom command
     * execution logic and dispatching.
     *
     * <p>Provides an abstraction layer over Bukkit's command system,
     * allowing more flexible command registration and handling.</p>
     */
    private final static AllianceCommandManager allianceCommandManager = new AllianceCommandManager();

    /**
     * Retrieves the global {@link AllianceListenerManager} instance.
     *
     * @return the shared listener manager
     */
    public static AllianceListenerManager getAllianceListenerManager() {
        return allianceListenerManager;
    }

    /**
     * Retrieves the global {@link AllianceCommandManager} instance.
     *
     * @return the shared command manager
     */
    public static AllianceCommandManager getAllianceCommandManager() {
        return allianceCommandManager;
    }
}