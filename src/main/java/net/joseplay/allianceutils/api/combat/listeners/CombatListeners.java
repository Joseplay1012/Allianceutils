package net.joseplay.allianceutils.api.combat.listeners;

import net.joseplay.allianceutils.Statics.AllianceUtilsApi;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class CombatListeners implements Listener {
    public void onPlayerMoveEvent(PlayerMoveEvent event){
        if (event.getPlayer().hasPermission("alc.admin")) return;

        if (AllianceUtilsApi.getCombatVerify().isCombat(event.getPlayer())){
            if (AllianceUtilsApi.getClaimAPI().isClaimRegion(event.getTo())){
                event.setCancelled(true);
                event.getPlayer().sendMessage("§6[Combate] §cVocê não pode entrar em claims em PVP.");
            }
        }
    }
}
