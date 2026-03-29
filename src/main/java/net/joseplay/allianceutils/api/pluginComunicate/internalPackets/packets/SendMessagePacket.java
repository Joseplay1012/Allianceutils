package net.joseplay.allianceutils.api.pluginComunicate.internalPackets.packets;

import net.joseplay.allianceutils.api.pluginComunicate.packets.UniPacket;
import org.json.JSONObject;

import java.util.UUID;

public class SendMessagePacket implements UniPacket {
    private final String message;
    private UUID uuid;
    private String playerName;

    public SendMessagePacket(String message, UUID uuid, String playerName) {
        this.message = message;
        this.uuid = uuid;
        this.playerName = playerName;
    }

    public SendMessagePacket(JSONObject json) {
        this.message = json.getString("message");
        this.uuid = json.has("uuid") ? UUID.fromString(json.getString("uuid")) : null;
        this.playerName = json.has("playerName") ? json.getString("playerName") : null;
    }

    @Override
    public String getType() {
        return "send_message";
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("type", getType());
        jsonObject.put("message", message);
        if (uuid != null) {
            jsonObject.put("uuid", uuid.toString());
        }
        if (playerName != null) {
            jsonObject.put("playerName", playerName);
        }

        return jsonObject;
    }

    public String getMessage() {
        return message;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getPlayerName() {
        return playerName;
    }
}
