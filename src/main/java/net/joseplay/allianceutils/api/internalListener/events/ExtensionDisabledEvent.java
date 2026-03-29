package net.joseplay.allianceutils.api.internalListener.events;

import net.joseplay.allianceutils.api.extensions.AlliancePlugin;

public class ExtensionDisabledEvent {
    private final AlliancePlugin extension;

    public ExtensionDisabledEvent(AlliancePlugin extension) {
        this.extension = extension;
    }

    public AlliancePlugin getExtension() {
        return extension;
    }
}
