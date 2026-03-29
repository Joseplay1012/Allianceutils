package net.joseplay.allianceutils;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class DevMode {
    private final JavaPlugin plugin;

    public DevMode(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public String DevActive(Plugin plugin){
        if(!plugin.getConfig().getBoolean("dev-mode")){
            return "\u001B[36mModo Desenvolvedor ativado. Pulando Verificação!";
        }
        return null;
    }
}