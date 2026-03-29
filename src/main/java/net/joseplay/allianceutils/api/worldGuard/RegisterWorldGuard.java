package net.joseplay.allianceutils.api.worldGuard;

import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.api.worldGuard.listeners.NoExitRegion;
import org.bukkit.plugin.java.JavaPlugin;

public class RegisterWorldGuard {
    private static JavaPlugin plugin = JavaPlugin.getProvidingPlugin(Allianceutils.class);

    public static void registerEvents(){
        //plugin.getServer().getPluginManager().registerEvents(new NoGlidingListener(), plugin);
        if (plugin.getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            plugin.getServer().getPluginManager().registerEvents(new NoExitRegion(), plugin);
        }
    }
}
