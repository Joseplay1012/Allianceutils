package net.joseplay.allianceutils.api.internalListener.events;

import org.bukkit.entity.Player;

public class PluginMessageEvent {
    private final String channel;
    private final String bytes;
    private final Player player;
    private final boolean isRedis;

    public PluginMessageEvent(String channel, String bytes, Player player, boolean isRedis) {
        this.channel = channel;
        this.bytes = bytes;
        this.player = player;
        this.isRedis = isRedis;
    }

    /**
     * Pega o jogador do evento, apenas se ele for uma messagem do sistema padrao do Bukkit
     * @return player
     */
    public Player getPlayer() {
        return player;
    }

    public String getChannel() {
        return channel;
    }

    public String getMessage() {
        return bytes;
    }

    /**
     * Retorna sempre se está usando o redis ou o PluginMessage padrão
     * @return true or false
     */
    public boolean isRedis() {
        return isRedis;
    }
}
