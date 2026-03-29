package net.joseplay.allianceutils.api.playerProfile.entity;

import net.joseplay.allianceutils.Allianceutils;

import java.util.UUID;

public class PlayerProfile implements Cloneable {
    private final UUID uuid;
    private FeatureManager featureManager = new FeatureManager();

    public PlayerProfile(UUID uuid){
        this.uuid = uuid;
        featureManager.ensureMaps();
    }

    public FeatureManager getFeatureManager(){
        return this.featureManager;
    }

    public void setFeatureManager(FeatureManager featureManager) {
        this.featureManager = featureManager;
    }

    public void ensureFeatures(){
        if (featureManager == null) featureManager = new FeatureManager();
        featureManager.ensureMaps();
    }

    @Deprecated(forRemoval = true, since = "new compiler")
    public boolean hasFeature(NameSpace ns){
        return featureManager.hasFeature(ns);
    }

    @Deprecated(forRemoval = true, since = "new compiler")
    public void removeFeature(NameSpace ns){
        featureManager.hasFeature(ns);
    }


    @Deprecated(forRemoval = true, since = "new compiler")
    public Object feature(NameSpace ns){
        return featureManager.feature(ns);
    }
    @Deprecated(forRemoval = true, since = "new compiler")
    public void setFeature(NameSpace ns, Object o){
        featureManager.setFeature(ns, o);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String toJSON(){
        return Allianceutils.GSON.toJson(this);
    }

    @Override
    public PlayerProfile clone() {
        try {
            return (PlayerProfile) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public static PlayerProfile fromJSON(String json){
        return Allianceutils.GSON.fromJson(json, PlayerProfile.class);
    }
}
