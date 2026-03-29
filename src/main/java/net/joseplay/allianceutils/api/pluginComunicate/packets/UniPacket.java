package net.joseplay.allianceutils.api.pluginComunicate.packets;

import org.json.JSONObject;

public interface UniPacket {
    String getType();
    JSONObject toJson();
}

