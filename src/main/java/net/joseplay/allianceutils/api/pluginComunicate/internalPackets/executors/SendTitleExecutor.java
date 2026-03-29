package net.joseplay.allianceutils.api.pluginComunicate.internalPackets.executors;

import net.joseplay.allianceutils.api.pluginComunicate.internalPackets.packets.SendTitlePacket;
import net.joseplay.allianceutils.api.pluginComunicate.packets.PacketExecutable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SendTitleExecutor implements PacketExecutable<SendTitlePacket> {

    @Override
    public void execute(SendTitlePacket packet) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (packet.getIgnored() != null && packet.getIgnored().contains(p.getUniqueId())) {
                continue;
            }

            p.resetTitle();
            p.sendTitle(
                    packet.getTitle() != null ? packet.getTitle() : "",
                    packet.getSubtitle() != null ? packet.getSubtitle() : "",
                    packet.getFadeIn(),
                    packet.getTime(),
                    packet.getFadeOut()
            );
        }
    }
}
