package net.joseplay.allianceutils.api.pluginComunicate.internalPackets.packets;

import net.joseplay.allianceutils.api.pluginComunicate.packets.UniPacket;
import org.json.JSONObject;

import java.util.UUID;

public class PlayerProfileAsyncPacket implements UniPacket {

    private final UUID uuid;
    private final String profile;

    public PlayerProfileAsyncPacket(UUID uuid, String profile) {
        this.uuid = uuid;
        this.profile = profile;
    }

    public PlayerProfileAsyncPacket(JSONObject jsonObject){
        this.uuid = UUID.fromString(jsonObject.getString("uuid"));
        this.profile = jsonObject.getString("profile");
    }



    public UUID getUuid() {
        return uuid;
    }

    public String getProfile() {
        return profile;
    }

    @Override
    public String getType() {
        return "player_profile_async";
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", getType());
        jsonObject.put("uuid", uuid.toString());
        jsonObject.put("profile", profile);
        return jsonObject;
    }
}

