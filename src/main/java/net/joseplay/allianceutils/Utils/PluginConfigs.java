package net.joseplay.allianceutils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.bukkit.Bukkit.getServer;

public class PluginConfigs {
    public static void reloadPluginConfig(FileConfiguration config, JavaPlugin plugin, Player player, File configFile){
        try {
            plugin.saveConfig();
            config.save(configFile);
            config.load(configFile);
            plugin.reloadConfig();

            //new Allianceutils().onDisable();
            //new Allianceutils().onEnable();

            if(player == null){
                Bukkit.getConsoleSender().sendMessage("§b --> §7"+configFile.getName()+" §bRecaregado!");
            } else {
                player.sendMessage("§b --> §7"+configFile.getName()+" §bRecaregado!");
            }
        } catch (Exception e){
            if(player == null) {
                Bukkit.getConsoleSender().sendMessage("§cErro ao regarregar plugin §f"+e.getMessage());
            } else {
                player.sendMessage("§cErro ao regarregar plugin §f"+e.getMessage());
            }
        }
    }
    public static int getServerPort() {
        return getServer().getPort();
    }
    public static String getServerIP() {
        try {
            URL url = new URL("https://api.ipify.org/?format=text");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                Bukkit.getLogger().severe("Falha ao obter o IP público do servidor. Código de resposta: " + conn.getResponseCode());
                return null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String publicIP = br.readLine();
            br.close();

            return publicIP;
        } catch (Exception e) {
            Bukkit.getLogger().severe("Erro ao obter o IP público do servidor" + e);
            return null;
        }
    }
}
