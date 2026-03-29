package net.joseplay.allianceutils.api.pluginComunicate.internalPackets.executors;

import net.joseplay.allianceutils.api.pluginComunicate.internalPackets.packets.SendSoundPacket;
import net.joseplay.allianceutils.api.pluginComunicate.packets.PacketExecutable;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SendSoundExecutor implements PacketExecutable<SendSoundPacket> {
    @Override
    public void execute(SendSoundPacket packet) {
        if (packet.getUuid() != null){
            Player player = Bukkit.getPlayer(packet.getUuid());
            if (player != null && player.isOnline()){
                player.playSound(player.getLocation(), Sound.valueOf(packet.getSoundName()), 1.0f, 1.0f);
            }

            return;
        }


        Bukkit.getOnlinePlayers().forEach(p -> {
            if (packet.getIgnore() != null && packet.getIgnore().contains(p.getUniqueId())) return;
            p.playSound(p.getLocation(), Sound.valueOf(packet.getSoundName()), 1.0f, 1.0f);
        });
    }
}
