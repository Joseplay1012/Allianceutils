package net.joseplay.allianceutils.api.pluginComunicate.internalPackets.packets;

import net.joseplay.allianceutils.api.playerProfile.entity.ServerProfile;
import net.joseplay.allianceutils.api.pluginComunicate.packets.UniPacket;
import org.json.JSONObject;

public class ServerProfileAsyncPacket implements UniPacket {
    private final ServerProfile profile;
    private boolean remote;

    public ServerProfileAsyncPacket(JSONObject json){
        this.profile = ServerProfile.fromJSON(json.getString("profile"));
        this.remote = json.getBoolean("remote");
    }

    public ServerProfileAsyncPacket(ServerProfile profile, boolean remote){
        this.profile = profile;
        this.remote = remote;
    }

    @Override
    public String getType() {
        return "server_profile_async";
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("type", getType());
        jsonObject.put("profile", profile.toJSON());
        jsonObject.put("remote", remote);


        return jsonObject;
    }

    public ServerProfile getProfile() {
        return profile;
    }

    public boolean isRemote() {
        return remote;
    }

    public void setRemote(boolean remote) {
        this.remote = remote;
    }
}
