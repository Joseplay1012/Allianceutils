package net.joseplay.allianceutils.api.pluginComunicate.internalPackets.executors;

import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.api.internalListener.EventManager;
import net.joseplay.allianceutils.api.internalListener.events.PacketRecivedEvent;
import net.joseplay.allianceutils.api.pluginComunicate.internalPackets.packets.ServerProfileAsyncPacket;
import net.joseplay.allianceutils.api.pluginComunicate.packets.PacketExecutable;
import org.bukkit.Bukkit;

public class ServerProfileAsyncExecutor
        implements PacketExecutable<ServerProfileAsyncPacket> {

    @Override
    public void execute(ServerProfileAsyncPacket packet) {

        Bukkit.getScheduler().runTask(
                Allianceutils.getPlugin(),
                () -> {

                    Allianceutils.getInstance()
                            .getServerProfileManager()
                            .applyRemoteProfile(packet.getProfile());


                    packet.setRemote(false);
                    EventManager.callEvent(
                            new PacketRecivedEvent(packet)
                    );

                    Allianceutils.getInstance().getLogger()
                            .info("[ServerProfile] Atualização remota aplicada.");
                }
        );
    }
}

