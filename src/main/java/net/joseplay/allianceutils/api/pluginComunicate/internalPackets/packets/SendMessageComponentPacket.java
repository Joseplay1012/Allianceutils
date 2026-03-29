package net.joseplay.allianceutils.api.pluginComunicate.internalPackets.packets;

import net.joseplay.allianceutils.api.pluginComunicate.packets.UniPacket;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.json.JSONObject;

import java.util.UUID;

public class SendMessageComponentPacket implements UniPacket {
    private final Component message;
    private UUID uuid;
    private String playerName;

    public SendMessageComponentPacket(Component message, UUID uuid, String playerName) {
        this.message = message;
        this.uuid = uuid;
        this.playerName = playerName;
    }

    public SendMessageComponentPacket(JSONObject json) {
        this.message = GsonComponentSerializer.gson().deserialize(json.getString("message"));
        this.uuid = json.has("uuid") ? UUID.fromString(json.getString("uuid")) : null;
        this.playerName = json.has("playerName") ? json.getString("playerName") : null;
    }

    @Override
    public String getType() {
        return "send_component_message";
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("type", getType());
        jsonObject.put("message", GsonComponentSerializer.gson().serialize(message));
        if (uuid != null) {
            jsonObject.put("uuid", uuid.toString());
        }
        if (playerName != null) {
            jsonObject.put("playerName", playerName);
        }

        return jsonObject;
    }

    public Component getMessage() {
        return message;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getPlayerName() {
        return playerName;
    }
}
