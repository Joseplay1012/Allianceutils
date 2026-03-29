package net.joseplay.allianceutils.DeBug;

import net.joseplay.allianceutils.Allianceutils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import static net.joseplay.allianceutils.Utils.UnicodeFontReplace.allianceFontReplace;

public class ActiveDebugCommand {
    protected static JavaPlugin plugin = JavaPlugin.getProvidingPlugin(Allianceutils.class);
    public static void activeDebugCommand(Player player){
        if(player == null){
            if (plugin.getConfig().getBoolean("dbug-mode")) {
                plugin.getConfig().set("dbug-mode", false);
                plugin.saveConfig();
                Bukkit.getConsoleSender().sendMessage(allianceFontReplace("§aDeBug ativado!"));
            } else {
                plugin.getConfig().set("dbug-mode", true);
                plugin.saveConfig();
                Bukkit.getConsoleSender().sendMessage(allianceFontReplace("§cDeBug desativado!"));
            }
        } else {
            if (plugin.getConfig().getBoolean("dbug-mode")) {
                plugin.getConfig().set("dbug-mode", false);
                plugin.saveConfig();
                player.sendMessage(allianceFontReplace("§aDeBug ativado!"));
            } else {
                plugin.getConfig().set("dbug-mode", true);
                plugin.saveConfig();
                player.sendMessage(allianceFontReplace("§cDeBug desativado!"));
            }
        }
    }
}
