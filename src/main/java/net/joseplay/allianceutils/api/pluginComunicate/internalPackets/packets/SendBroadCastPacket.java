package net.joseplay.allianceutils.api.pluginComunicate.internalPackets.packets;

import net.joseplay.allianceutils.api.pluginComunicate.packets.UniPacket;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SendBroadCastPacket implements UniPacket {
    private final String message;
    private final String permission;
    private final List<UUID> ignoreUUID;

    public SendBroadCastPacket(String message, String permission, List<UUID> ignoreUUID) {
        this.message = message;
        this.permission = permission;
        this.ignoreUUID = ignoreUUID;
    }

    public SendBroadCastPacket(String message){
        this.message = message;
        this.permission = null;
        this.ignoreUUID = null;
    }

    public SendBroadCastPacket(JSONObject json) {
        this.message = json.getString("message");
        this.permission = json.has("permission") ? json.getString("permission") : null;
        this.ignoreUUID = json.has("ignoreUUID") && !json.isNull("ignoreUUID")
                ? json.getJSONArray("ignoreUUID").toList().stream()
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
        return "send_brodcast";
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("type", getType());
        jsonObject.put("message", message);
        if (permission != null) {
            jsonObject.put("permission", permission);
        }
        if (ignoreUUID != null){
            jsonObject.put("ignoreUUID", new ArrayList<>(ignoreUUID).stream().map(UUID::toString).toList());
        }

        return jsonObject;
    }

    public String getMessage() {
        return message;
    }

    public String getPermission() {
        return permission;
    }

    public List<UUID> getIgnoreUUID() {
        return ignoreUUID;
    }
}
