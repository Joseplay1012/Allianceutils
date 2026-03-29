package net.joseplay.allianceutils.api.pluginComunicate.internalPackets.packets;

import net.joseplay.allianceutils.api.pluginComunicate.packets.UniPacket;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SendActionBarPacket implements UniPacket {
    private final String message;
    private final UUID uuid;
    private final List<UUID> ignore;

    public SendActionBarPacket(String message, UUID uuid, List<UUID> ignore) {
        this.message = message;
        this.uuid = uuid;
        this.ignore = ignore;
    }

    public SendActionBarPacket(String message, UUID uuid) {
        this(message, uuid, null);
    }

    public SendActionBarPacket(String message, List<UUID> ignore) {
        this(message, null, ignore);
    }

    public SendActionBarPacket(String message) {
        this(message, null, null);
    }

    public SendActionBarPacket(JSONObject json) {
        this.message = json.getString("message");
        this.uuid = json.has("uuid") ? UUID.fromString(json.getString("uuid")) : null;
        this.ignore = json.has("ignore") && !json.isNull("ignore")
                ? json.getJSONArray("ignore").toList().stream()
                .map(obj -> {
                    if (obj instanceof UUID uuid) return uuid;
                    if (obj instanceof String str) {
                        try {
                            return UUID.fromString(str);
                        } catch (IllegalArgumentException e) {
                            return null;
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList()
                : null;
    }

    @Override
    public String getType() {
        return "send_actionbar";
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("type", getType());
        jsonObject.put("message", message);
        if (uuid != null) {
            jsonObject.put("uuid", uuid.toString());
        }
        if (ignore != null){
            jsonObject.put("ignore", ignore.stream().map(UUID::toString).toList());
        }

        return jsonObject;
    }

    public String getMessage() {
        return message;
    }

    public UUID getUuid() {
        return uuid;
    }

    public List<UUID> getIgnore() {
        return ignore;
    }
}
