package net.joseplay.allianceutils.api.playerProfile.entity;

import net.joseplay.allianceutils.Allianceutils;

public class ServerProfile implements Cloneable {
    private final FeatureManager featureManager = new FeatureManager();

    public FeatureManager getFeatureManager() {
        return featureManager;
    }

    public String toJSON(){
        return Allianceutils.GSON.toJson(this);
    }

    public static ServerProfile fromJSON(String json){
        return Allianceutils.GSON.fromJson(json, ServerProfile.class);
    }

    @Override
    public ServerProfile clone() {
        try {
            return (ServerProfile) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e); // nunca deve acontecer
        }
    }
}
