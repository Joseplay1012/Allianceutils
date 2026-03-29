package net.joseplay.allianceutils.api.pluginComunicate.packets;

public interface PacketExecutable<T extends UniPacket> {
    void execute(T packet);
}
