package net.joseplay.allianceutils.api.pluginComunicate.internalPackets.executors;

import net.joseplay.allianceutils.api.pluginComunicate.internalPackets.packets.SendMessageComponentPacket;
import net.joseplay.allianceutils.api.pluginComunicate.packets.PacketExecutable;
import org.bukkit.Bukkit;

public class SendMessageCompomentExecutor implements PacketExecutable<SendMessageComponentPacket> {
    @Override
    public void execute(SendMessageComponentPacket packet) {
        if (packet.getUuid() != null && Bukkit.getPlayer(packet.getUuid()) != null){
            Bukkit.getPlayer(packet.getUuid()).sendMessage(packet.getMessage());
        } else if (packet.getPlayerName() != null && Bukkit.getPlayer(packet.getPlayerName()) != null){
            Bukkit.getPlayer(packet.getPlayerName()).sendMessage(packet.getMessage());
        }
    }
}
