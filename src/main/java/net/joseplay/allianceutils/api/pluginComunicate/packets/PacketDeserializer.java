package net.joseplay.allianceutils.api.pluginComunicate.packets;

import org.json.JSONObject;

public interface PacketDeserializer<T extends UniPacket> {
    T deserialize(JSONObject json);
}
