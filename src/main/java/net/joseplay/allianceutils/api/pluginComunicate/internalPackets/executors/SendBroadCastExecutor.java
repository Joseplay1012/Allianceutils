package net.joseplay.allianceutils.api.pluginComunicate.internalPackets.executors;

import net.joseplay.allianceutils.api.pluginComunicate.internalPackets.packets.SendBroadCastPacket;
import net.joseplay.allianceutils.api.pluginComunicate.packets.PacketExecutable;
import org.bukkit.Bukkit;

public class SendBroadCastExecutor implements PacketExecutable<SendBroadCastPacket> {
    @Override
    public void execute(SendBroadCastPacket packet) {

        if (packet.getIgnoreUUID() == null) {

            if (packet.getPermission() != null) {
                Bukkit.broadcast(packet.getMessage(), packet.getPermission());
                return;
            }

            Bukkit.broadcastMessage(packet.getMessage());
            return;
        }

        Bukkit.getOnlinePlayers().stream()
                .filter(p -> !packet.getIgnoreUUID().contains(p.getUniqueId()))
                .filter(p -> {
                    if (packet.getPermission() == null) return true;
                    if (packet.getPermission() != null) return p.hasPermission(packet.getPermission());
                    return false;
                })
                .forEach(p -> {
                            p.sendMessage(packet.getMessage());
                        }
                );
    }
}
