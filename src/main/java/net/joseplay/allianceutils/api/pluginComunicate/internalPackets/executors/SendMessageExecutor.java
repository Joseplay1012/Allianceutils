package net.joseplay.allianceutils.api.pluginComunicate.internalPackets.executors;

import net.joseplay.allianceutils.api.pluginComunicate.internalPackets.packets.SendMessagePacket;
import net.joseplay.allianceutils.api.pluginComunicate.packets.PacketExecutable;
import org.bukkit.Bukkit;

public class SendMessageExecutor implements PacketExecutable<SendMessagePacket> {

    @Override
    public void execute(SendMessagePacket packet) {
        if (packet.getUuid() != null && Bukkit.getPlayer(packet.getUuid()) != null){
            Bukkit.getPlayer(packet.getUuid()).sendMessage(packet.getMessage());
        } else if (packet.getPlayerName() != null && Bukkit.getPlayer(packet.getPlayerName()) != null){
            Bukkit.getPlayer(packet.getPlayerName()).sendMessage(packet.getMessage());
        }
    }
}
