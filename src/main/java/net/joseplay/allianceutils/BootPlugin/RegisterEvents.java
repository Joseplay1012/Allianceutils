package net.joseplay.allianceutils.BootPlugin;

import net.joseplay.allianceutils.api.chat.ChatRegisters;
import net.joseplay.allianceutils.api.internalListener.EventManager;
import net.joseplay.allianceutils.api.menu.MenuListener;
import net.joseplay.allianceutils.api.pluginComunicate.PluginChannelListener;
import net.joseplay.allianceutils.api.scoreboard.ScoreboardRegisters;
import net.joseplay.allianceutils.api.worldGuard.RegisterWorldGuard;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class RegisterEvents {
    public static void registreEvents(JavaPlugin plugin) {
        try {
            // Lista de métodos que você quer executar sequencialmente
            Runnable[] events = {
                    () -> plugin.getServer().getPluginManager().registerEvents(new MenuListener(), plugin),
                    () -> EventManager.registerListener(new PluginChannelListener(), plugin),
                    () -> RegisterWorldGuard.registerEvents(),
                    () -> ScoreboardRegisters.registerEvents(),
                    () -> ChatRegisters.registerEvents()
            };

            // Execução sequencial
            for (Runnable event : events) {
                event.run();
            }

            Bukkit.getLogger().info("\u001B[36mevents registering!");
        } catch (Exception e) {
            Bukkit.getLogger().severe("error registering event: " + e.getMessage());
        }
    }

}
