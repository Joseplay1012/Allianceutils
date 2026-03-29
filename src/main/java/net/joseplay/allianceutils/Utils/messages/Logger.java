package net.joseplay.allianceutils.Utils.messages;

import org.bukkit.Bukkit;

public class Logger {
    private static final String PREFIX = "[AllianceUtils]";

    public static void info(String string){
        Bukkit.getConsoleSender().sendMessage(PREFIX + " [INFO] " + string.replace("&", "§"));
    }

    public static void warning(String string){
        Bukkit.getConsoleSender().sendMessage(PREFIX + " §e[WARNING] " + string.replace("&", "§"));
    }

    public static void severe(String string){
        Bukkit.getConsoleSender().sendMessage(PREFIX + " §c[ERROR] " + string.replace("&", "§"));
    }
}
