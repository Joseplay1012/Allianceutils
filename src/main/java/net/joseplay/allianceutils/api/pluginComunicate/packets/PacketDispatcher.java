package net.joseplay.allianceutils.api.pluginComunicate.packets;

import net.joseplay.allianceutils.api.internalListener.EventManager;
import net.joseplay.allianceutils.api.internalListener.events.PacketRecivedEvent;
import net.joseplay.allianceutils.api.internalListener.events.PacketSendEvent;
import org.json.JSONObject;

/**
 * Responsible for dispatching {@link UniPacket} instances across the system.
 *
 * <p>This class handles:</p>
 * <ul>
 *     <li>Local packet execution</li>
 *     <li>Redis-based cross-server communication</li>
 *     <li>Event triggering for send/receive lifecycle</li>
 * </ul>
 *
 * <p>Behavior depends on configuration:</p>
 * <ul>
 *     <li>If Redis is enabled → packets are sent through Redis</li>
 *     <li>If disabled → packets execute locally</li>
 * </ul>
 */
public class PacketDispatcher {

    private final PacketRegistry registry;
    private final PacketExecutorRegistry executorRegistry;

    /**
     * Creates a new dispatcher.
     *
     * @param registry packet registry for serialization/deserialization
     * @param executorRegistry executor registry for handling packets
     */
    public PacketDispatcher(PacketRegistry registry, PacketExecutorRegistry executorRegistry) {
        this.registry = registry;
        this.executorRegistry = executorRegistry;
    }

    /**
     * Sends a packet.
     *
     * @param packet the packet to send
     * @param executeLocal whether to execute locally before/without sending to Redis
     */
    public void send(UniPacket packet, boolean executeLocal) {
        EventManager.callEvent(new PacketSendEvent(packet));

        PacketExecutable executor = executorRegistry.getExecutor(packet.getClass());

        if (!executeLocal && registry.useRedis()){
            registry.getRedis().sendMessage("alc:async", packet.toJson().toString());
            return;
        }

        if (executor != null && !registry.useRedis()) {
            executor.execute(packet);
        }

        if (registry.useRedis()) {
            registry.getRedis().sendMessage("alc:async", packet.toJson().toString());
        }
    }

    /**
     * Sends a packet and executes it locally by default.
     */
    public void send(UniPacket packet) {
        send(packet, true);
    }

    /**
     * Handles packets received from Redis.
     *
     * @param jsonMessage serialized packet data
     */
    public void receiveFromRedis(String jsonMessage) {
        UniPacket packet = registry.deserialize(new JSONObject(jsonMessage));

        EventManager.callEvent(new PacketRecivedEvent(packet));

        PacketExecutable executor = executorRegistry.getExecutor(packet.getClass());

        if (executor != null) {
            executor.execute(packet);
        }
    }
}