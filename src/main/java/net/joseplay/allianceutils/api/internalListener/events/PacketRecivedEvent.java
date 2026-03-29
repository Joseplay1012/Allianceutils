package net.joseplay.allianceutils.api.internalListener.events;

import net.joseplay.allianceutils.api.pluginComunicate.packets.UniPacket;

public class PacketRecivedEvent {
    private final UniPacket packet;

    public PacketRecivedEvent(UniPacket packet) {
        this.packet = packet;
    }

    public UniPacket getPacket() {
        return packet;
    }

}
