package net.joseplay.allianceutils.api.pluginComunicate.internalPackets.executors;

import net.joseplay.allianceutils.api.pluginComunicate.internalPackets.packets.SendBroadCastComponentPacket;
import net.joseplay.allianceutils.api.pluginComunicate.packets.PacketExecutable;
import org.bukkit.Bukkit;

public class SendBroadCastComponentExecutor implements PacketExecutable<SendBroadCastComponentPacket> {
    @Override
    public void execute(SendBroadCastComponentPacket packet) {
        if (packet.getIgnoreUUID() == null){
            if (packet.getPermission() != null) {
                Bukkit.broadcast(packet.getMessage(), packet.getPermission());
                return;
            }

            Bukkit.broadcast(packet.getMessage());
            return;
        }

        Bukkit.getOnlinePlayers().stream()
                .filter(p -> !packet.getIgnoreUUID().contains(p.getUniqueId()))
                .filter(p -> {
                    if (packet.getPermission() == null) return true;
                    if (packet.getPermission() != null) return p.hasPermission(packet.getPermission());
                    return false;
                })
                .forEach(p -> p.sendMessage(packet.getMessage()));
    }
}
