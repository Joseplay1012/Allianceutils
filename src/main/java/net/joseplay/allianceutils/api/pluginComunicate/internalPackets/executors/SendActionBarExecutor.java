package net.joseplay.allianceutils.api.pluginComunicate.internalPackets.executors;

import net.joseplay.allianceutils.api.pluginComunicate.internalPackets.packets.SendActionBarPacket;
import net.joseplay.allianceutils.api.pluginComunicate.packets.PacketExecutable;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SendActionBarExecutor implements PacketExecutable<SendActionBarPacket> {
    @Override
    public void execute(SendActionBarPacket packet) {
        if (packet.getUuid() != null){
            Player player = Bukkit.getPlayer(packet.getUuid());

            if (player != null && player.isOnline()){
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(packet.getMessage()));
            }

            return;
        }

        Bukkit.getOnlinePlayers().forEach(p -> {
            if (packet.getIgnore() != null && packet.getIgnore().contains(p.getUniqueId())) return;
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(packet.getMessage()));
        });
    }
}
