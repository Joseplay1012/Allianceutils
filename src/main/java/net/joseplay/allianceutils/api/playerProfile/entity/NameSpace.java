package net.joseplay.allianceutils.api.playerProfile.entity;

import net.joseplay.allianceutils.api.extensions.AlliancePlugin;
import org.bukkit.plugin.Plugin;

public class NameSpace {
    private final String key;
    private final String name;


    public NameSpace(AlliancePlugin extension, String key){
        this.key = key;
        this.name = extension.getExtensionName().toLowerCase();
    }

    public NameSpace(Plugin plugin, String key){
        this.key = key;
        this.name = plugin.getName();
    }

    public NameSpace(String name, String key){
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return (name + ":" + key).toLowerCase();
    }
}
