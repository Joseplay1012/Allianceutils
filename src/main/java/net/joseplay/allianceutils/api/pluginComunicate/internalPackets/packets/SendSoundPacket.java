package net.joseplay.allianceutils.api.pluginComunicate.internalPackets.packets;

import net.joseplay.allianceutils.api.pluginComunicate.packets.UniPacket;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SendSoundPacket implements UniPacket {
    private final String soundName;
    private final UUID uuid;
    private final List<UUID> ignore;

    public SendSoundPacket(String soundName, UUID uuid, List<UUID> ignore){
        this.soundName = soundName;
        this.uuid = uuid;
        this.ignore = ignore;
    }

    public SendSoundPacket(String soundName, List<UUID> ignore) {
        this(soundName, null, ignore);
    }

    public SendSoundPacket(String soundName, UUID uuid) {
        this(soundName, uuid, null);
    }

    public SendSoundPacket(String soundName) {
        this(soundName, null, null);
    }

    public SendSoundPacket(JSONObject json){
        this.soundName = json.getString("soundName");
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
        return "send_sound";
    }



    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("type", getType());
        jsonObject.put("soundName", soundName);
        if (uuid != null){
            jsonObject.put("uuid", uuid.toString());
        }
        if (ignore != null){
            jsonObject.put("ignore", ignore.stream().map(UUID::toString).toList());
        }

        return jsonObject;
    }

    public String getSoundName() {
        return soundName;
    }

    public UUID getUuid() {
        return uuid;
    }

    public List<UUID> getIgnore() {
        return ignore;
    }
}
