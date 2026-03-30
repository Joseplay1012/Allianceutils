package net.joseplay.allianceutils.api.chat;

import net.joseplay.allianceutils.Allianceutils;
import org.bukkit.Bukkit;

public class ChatRegisters {


    public static void registerEvents(){
        Bukkit.getPluginManager().registerEvents(new ChatEditor(), Allianceutils.getPlugin());
    }
}
