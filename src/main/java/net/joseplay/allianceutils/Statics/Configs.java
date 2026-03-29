package net.joseplay.allianceutils.Statics;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Configs {
    public static String ConfigString(JavaPlugin plugin, String key){
        FileConfiguration config = plugin.getConfig();
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(key, "&cMensagem não encontrada no arquivo config.yml"));
    }
}
