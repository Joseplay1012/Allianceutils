package net.joseplay.allianceutils.BootPlugin;

import net.joseplay.allianceutils.api.pluginComunicate.PluginChannelManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class RegisterChannels {
    /**public static PluginChannelManager resgirerChannelManeger(String channel, JavaPlugin plugin) {
        PluginChannelDispatcher dispatcher = new PluginChannelDispatcher();
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, channel, dispatcher);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, channel);

        return new PluginChannelManager(channel, plugin, dispatcher);
    }*/

    public static PluginChannelManager registerChannelManeger(String channel, JavaPlugin plugin){
        return new PluginChannelManager(channel, plugin);
    }

    public static void registerCommandsChannel(String channel, JavaPlugin plugin, PluginMessageListener e){
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, channel, e);
    }

}
