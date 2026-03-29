package net.joseplay.allianceutils.api.internalListener.events;

import net.joseplay.allianceutils.api.pluginComunicate.packets.UniPacket;

public class PacketSendEvent {
    private final UniPacket packet;

    public PacketSendEvent(UniPacket packet) {
        this.packet = packet;
    }

    public UniPacket getPacket() {
        return packet;
    }
}
