package net.joseplay.allianceutils.api.preferences.interfaces;

public abstract class PreferencePermission implements Preference {
    private final String permission;

    protected PreferencePermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
