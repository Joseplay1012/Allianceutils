package net.joseplay.allianceutils.api.pluginComunicate.internalPackets.packets;

import net.joseplay.allianceutils.api.pluginComunicate.packets.UniPacket;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

public class SendTitlePacket implements UniPacket {

    private final String title;
    private final String subtitle;
    private final List<UUID> ignored;
    private final int fadeIn;
    private final int time;
    private final int fadeOut;

    public SendTitlePacket(String title, String subtitle, List<UUID> ignore, int fadeIn, int time, int fadeOut) {
        this.title = title;
        this.subtitle = subtitle;
        this.ignored = ignore;
        this.fadeIn = fadeIn;
        this.time = time;
        this.fadeOut = fadeOut;
    }

    public SendTitlePacket(String title, String subtitle, List<UUID> ignored) {
        this(title, subtitle, ignored, 10, 70, 20);
    }

    public SendTitlePacket(String title, String subtitle, int fadeIn, int time, int fadeOut) {
        this(title, subtitle, null, fadeIn, time, fadeOut);
    }

    public SendTitlePacket(String title, String subtitle) {
        this(title, subtitle, null);
    }

    public SendTitlePacket(String title) {
        this(title, null);
    }

    public SendTitlePacket(JSONObject json) {
        this.title = json.getString("title");
        this.subtitle = json.getString("subtitle");
        this.fadeIn = json.getInt("fadeIn");
        this.time = json.getInt("time");
        this.fadeOut = json.getInt("fadeOut");

        if (json.has("ignored")) {
            JSONArray array = json.getJSONArray("ignored");
            this.ignored = array.toList().stream()
                    .map(o -> UUID.fromString(o.toString()))
                    .toList();
        } else {
            this.ignored = null;
        }
    }

    @Override
    public String getType() {
        return "send_title";
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("type", getType());
        json.put("title", title);
        json.put("subtitle", subtitle);
        json.put("fadeIn", fadeIn);
        json.put("time", time);
        json.put("fadeOut", fadeOut);

        if (ignored != null && !ignored.isEmpty()) {
            JSONArray array = new JSONArray();
            ignored.forEach(uuid -> array.put(uuid.toString()));
            json.put("ignored", array);
        }

        return json;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public List<UUID> getIgnored() {
        return ignored;
    }

    public int getFadeIn() {
        return fadeIn;
    }

    public int getTime() {
        return time;
    }

    public int getFadeOut() {
        return fadeOut;
    }
}
