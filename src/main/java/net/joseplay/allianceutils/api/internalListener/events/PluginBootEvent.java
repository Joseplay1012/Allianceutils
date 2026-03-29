package net.joseplay.allianceutils.api.internalListener.events;

import org.bukkit.plugin.java.JavaPlugin;

public class PluginBootEvent {
    private final JavaPlugin plugin;

    public PluginBootEvent(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}
