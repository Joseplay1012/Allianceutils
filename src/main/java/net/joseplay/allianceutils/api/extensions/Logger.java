package net.joseplay.allianceutils.api.extensions;

import org.bukkit.Bukkit;

public class Logger {

    private final String prefix;

    public Logger(String prefix) {
        this.prefix = prefix;
    }

    public void info(String s){
        Bukkit.getLogger().info("[" + prefix + "] " + s);
    }

    public void warning(String s){
        Bukkit.getLogger().warning("[" + prefix + "] " + s);
    }

    public void error(String s){
        Bukkit.getLogger().severe("[" + prefix + "] " + s);
    }

}
