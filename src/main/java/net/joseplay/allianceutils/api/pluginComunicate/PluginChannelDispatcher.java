package net.joseplay.allianceutils.api.pluginComunicate;

import io.lettuce.core.RedisChannelHandler;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisConnectionStateListener;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.api.internalListener.EventManager;
import net.joseplay.allianceutils.api.internalListener.events.PluginMessageEvent;
import net.joseplay.allianceutils.api.pluginComunicate.packets.PacketDispatcher;
import net.joseplay.allianceutils.api.pluginComunicate.packets.PacketExecutorRegistry;
import net.joseplay.allianceutils.api.pluginComunicate.packets.PacketRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

public class PluginChannelDispatcher {

    public final String channel = "alc:async";

    private final JavaPlugin plugin;
    private final boolean useRedis;

    private final RedisClient redisClient;
    private StatefulRedisConnection<String, String> pubConnection;
    private StatefulRedisPubSubConnection<String, String> subConnection;
    private RedisAsyncCommands<String, String> async;

    private final RedisManager redisManager;
    private final PluginChannelManager pluginChannelManager;

    private UltimateComunicate ultimateComunicate;
    private PacketDispatcher packetDispatcher;
    private PacketRegistry packetRegistry;
    private PacketExecutorRegistry packetExecutorRegistry;
    private volatile boolean subscribed = false;

    public PluginChannelDispatcher(RedisClient redisClient, JavaPlugin plugin, boolean useRedis) {
        this.redisClient = redisClient;
        this.plugin = plugin;
        this.useRedis = useRedis;

        this.packetRegistry = new PacketRegistry(this);
        this.packetExecutorRegistry = new PacketExecutorRegistry();
        this.packetDispatcher = new PacketDispatcher(packetRegistry, packetExecutorRegistry);

        this.pluginChannelManager = new PluginChannelManager(channel, plugin);

        if (redisClient != null && useRedis) {
            startRedis();
            this.redisManager = new RedisManager(async, pubConnection.sync());
        } else {
            Bukkit.getMessenger().registerIncomingPluginChannel(plugin, channel, new PluginMessageSpigot());
            this.redisManager = null;
        }

        this.ultimateComunicate = new UltimateComunicate(this);
    }

    private void startRedis() {
        this.pubConnection = redisClient.connect();
        this.async = pubConnection.async();

        this.subConnection = redisClient.connectPubSub();

        this.subConnection.addListener(new RedisPubSubListener<>() {
            @Override
            public void message(String channel, String message) {
                EventManager.callEvent(
                        new PluginMessageEvent(channel, message, null, true)
                );
            }

            @Override
            public void message(String s, String k1, String s2) {}
            @Override
            public void subscribed(String s, long l){}
            @Override
            public void psubscribed(String s, long l) {}
            @Override
            public void unsubscribed(String s, long l) {}
            @Override
            public void punsubscribed(String s, long l) {}
        });

        this.subConnection.addListener(new RedisConnectionStateListener() {

            @Override
            public void onRedisConnected(
                    RedisChannelHandler<?, ?> connection,
                    SocketAddress socketAddress
            ) {
                Allianceutils.getInstance().getLogger()
                        .info("[Redis] Connected/Reconnected.");

                subscribeIfNeeded();
            }

            @Override
            public void onRedisDisconnected(RedisChannelHandler<?, ?> connection) {
                subscribed = false; // importante
                Allianceutils.getInstance().getLogger()
                        .severe("[Redis] Disconnected from Redis.");
            }
        });

        subscribeIfNeeded();
    }

    private synchronized void subscribeIfNeeded() {
        if (subscribed) return;

        subConnection.async().subscribe(channel);
        subscribed = true;

        Allianceutils.getInstance().getLogger()
                .info("[Redis] Subscribed to the channel: " + channel);
    }

    static class PluginMessageSpigot implements PluginMessageListener {

        @Override
        public void onPluginMessageReceived(
                @NotNull String channel,
                @NotNull Player player,
                @NotNull byte[] bytes
        ) {
            EventManager.callEvent(
                    new PluginMessageEvent(
                            channel,
                            new String(bytes, StandardCharsets.UTF_8),
                            player,
                            false
                    )
            );
        }
    }

    /* ================= GETTERS ================= */

    public RedisAsyncCommands<String, String> getAsync() {
        return async;
    }

    public RedisManager getRedisManager() {
        return redisManager;
    }

    public PluginChannelManager getPluginChannelManager() {
        return pluginChannelManager;
    }

    public PacketExecutorRegistry getPacketExecutorRegistry() {
        return packetExecutorRegistry;
    }

    public PacketRegistry getPacketRegistry() {
        return packetRegistry;
    }

    public PacketDispatcher getPacketDispatcher() {
        return packetDispatcher;
    }

    public UltimateComunicate getUltimateComunicate() {
        return ultimateComunicate;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public boolean isUseRedis() {
        return useRedis;
    }

    /* ================= SHUTDOWN ================= */

    public void shutdown() {

        if (subConnection != null) {
            try {
                subConnection.async().unsubscribe(channel);
            } catch (Exception ignored) {}

            subConnection.close();
            subConnection = null;
        }

        if (pubConnection != null) {
            pubConnection.close();
            pubConnection = null;
        }

        if (redisClient != null) {
            redisClient.shutdown();
        }
    }
}