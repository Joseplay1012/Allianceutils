package net.joseplay.allianceutils.api.playerProfile.listeners;

import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.api.internalListener.AuListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerProfileListener implements Listener, AuListener {

    public PlayerProfileListener(){
        Allianceutils.getPlugin().getServer().getPluginManager().registerEvents(this, Allianceutils.getPlugin());
        //EventManager.registerListener(this, Allianceutils.getPlugin());
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event){
        Allianceutils.getInstance()
                .getPlayerProfileManager()
                .loadOnJoin(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event){
        Allianceutils.getInstance()
                .getPlayerProfileManager()
                .unloadOnQuit(event.getPlayer().getUniqueId());
    }
}
