package net.joseplay.allianceutils.api.internalListener.events;

import org.bukkit.plugin.Plugin;

public class PluginShutdownEvent {
    private final Plugin plugin;

    public PluginShutdownEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
