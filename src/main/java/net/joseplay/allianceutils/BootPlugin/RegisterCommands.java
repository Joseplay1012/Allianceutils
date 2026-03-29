package net.joseplay.allianceutils.BootPlugin;

import net.joseplay.allianceutils.api.updateSystem.commands.UpdateCommand;
import net.joseplay.allianceutils.api.preferences.command.PreferencesCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class RegisterCommands {
    public static void registreCommands(JavaPlugin plugin) {
        try {
            // Lista de comandos e seus respectivos executores
            Runnable[] commands = {
                    () -> plugin.getCommand("updatecheck").setExecutor(new UpdateCommand()),
                    () -> plugin.getCommand("preferences").setExecutor(new PreferencesCommand())
            };

            // Execução sequencial dos comandos
            for (Runnable command : commands) {
                command.run();
            }

            Bukkit.getLogger().info("\u001B[36mCommands registering!");
        } catch (Exception e) {
            Bukkit.getLogger().severe("error registering event " + e.getMessage() + " cause " + e.getCause());
        }
    }
}
