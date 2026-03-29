package net.joseplay.allianceutils.api.scoreboard.listeners;

import net.joseplay.allianceutils.api.internalListener.AuListener;
import net.joseplay.allianceutils.api.internalListener.annotations.AuEventHandler;
import net.joseplay.allianceutils.api.internalListener.events.PluginShutdownEvent;
import net.joseplay.allianceutils.api.scoreboard.data.SidebarManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ScoreboardListeners implements Listener, AuListener {

    @AuEventHandler
    public void onPluginShutDown(PluginShutdownEvent event){
        SidebarManager.shutdown();
    }

    @EventHandler
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent event){
        SidebarManager.remove(event.getPlayer());
    }
}
