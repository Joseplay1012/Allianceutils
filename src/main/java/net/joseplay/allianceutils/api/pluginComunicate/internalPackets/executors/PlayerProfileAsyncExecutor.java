package net.joseplay.allianceutils.api.pluginComunicate.internalPackets.executors;

import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.api.pluginComunicate.internalPackets.packets.PlayerProfileAsyncPacket;
import net.joseplay.allianceutils.api.pluginComunicate.packets.PacketExecutable;
import org.bukkit.Bukkit;

public class PlayerProfileAsyncExecutor implements PacketExecutable<PlayerProfileAsyncPacket> {

    @Override
    public void execute(PlayerProfileAsyncPacket packet) {

        Bukkit.getScheduler().runTask(
                Allianceutils.getPlugin(),
                () -> Allianceutils.getInstance()
                        .getPlayerProfileManager()
                        .applyRemoteUpdate(packet.getUuid(), packet.getProfile())
        );
    }
}