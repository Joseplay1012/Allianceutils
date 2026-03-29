package net.joseplay.allianceutils.api.pluginComunicate;

import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.api.internalListener.AuListener;
import net.joseplay.allianceutils.api.internalListener.annotations.AuEventHandler;
import net.joseplay.allianceutils.api.internalListener.events.PluginMessageEvent;

public class PluginChannelListener implements AuListener {

    @AuEventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getMessage().startsWith("{") && event.getMessage().endsWith("}")) {
            Allianceutils.getInstance().getDispatcher()
                    .getPacketDispatcher()
                    .receiveFromRedis(event.getMessage());
        }
    }
}
