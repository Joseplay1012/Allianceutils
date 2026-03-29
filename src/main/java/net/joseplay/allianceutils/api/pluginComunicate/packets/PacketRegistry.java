package net.joseplay.allianceutils.api.pluginComunicate.packets;

import net.joseplay.allianceutils.api.pluginComunicate.PluginChannelDispatcher;
import net.joseplay.allianceutils.api.pluginComunicate.RedisManager;
import net.joseplay.allianceutils.api.pluginComunicate.internalPackets.packets.*;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry responsible for mapping packet types to their corresponding classes
 * and handling deserialization.
 *
 * <p>Each packet must:</p>
 * <ul>
 *     <li>Be registered with a unique string identifier</li>
 *     <li>Provide a constructor accepting a {@link JSONObject}</li>
 * </ul>
 *
 * <p>This class also acts as a bridge to Redis configuration via
 * {@link PluginChannelDispatcher}.</p>
 */
public class PacketRegistry {

    /**
     * Maps packet type identifiers to packet classes.
     */
    private final Map<String, Class<? extends UniPacket>> packets = new ConcurrentHashMap<>();

    /**
     * Dispatcher providing access to Redis and configuration.
     */
    private final PluginChannelDispatcher pluginChannelDispatcher;

    /**
     * Creates a new registry and registers default packets.
     *
     * @param pluginChannelDispatcher dispatcher instance
     */
    public PacketRegistry(PluginChannelDispatcher pluginChannelDispatcher) {
        this.pluginChannelDispatcher = pluginChannelDispatcher;
        registerDefaults();
    }

    /**
     * Registers built-in packet types.
     */
    private void registerDefaults() {
        register("send_component_message", SendMessageComponentPacket.class);
        register("send_message", SendMessagePacket.class);
        register("send_brodcast", SendBroadCastPacket.class);
        register("send_title", SendTitlePacket.class);
        register("send_brodcast_component", SendBroadCastComponentPacket.class);
        register("send_sound", SendSoundPacket.class);
        register("send_actionbar", SendActionBarPacket.class);
        register("server_profile_async", ServerProfileAsyncPacket.class);
        register("player_profile_async", PlayerProfileAsyncPacket.class);
    }

    /**
     * Registers a packet type.
     *
     * @param type unique identifier
     * @param clazz packet class
     */
    public void register(String type, Class<? extends UniPacket> clazz) {
        packets.put(type, clazz);
    }

    /**
     * Deserializes a JSON object into a {@link UniPacket}.
     *
     * @param json packet data
     * @return reconstructed packet instance
     * @throws IllegalArgumentException if type is unknown
     * @throws RuntimeException if instantiation fails
     */
    public UniPacket deserialize(JSONObject json) {

        String type = json.getString("type");

        Class<? extends UniPacket> clazz = packets.get(type);
        if (clazz == null) {
            throw new IllegalArgumentException("Unknown packet type: " + type);
        }

        try {
            return clazz.getDeclaredConstructor(JSONObject.class).newInstance(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to reconstruct packet: " + type, e);
        }
    }

    /**
     * Returns the Redis manager.
     */
    public RedisManager getRedis() {
        return pluginChannelDispatcher.getRedisManager();
    }

    /**
     * Indicates whether Redis is enabled.
     */
    public boolean useRedis() {
        return pluginChannelDispatcher.isUseRedis();
    }
}