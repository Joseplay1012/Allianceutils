package net.joseplay.allianceutils.api.pluginComunicate.packets;

import net.joseplay.allianceutils.api.pluginComunicate.internalPackets.executors.*;
import net.joseplay.allianceutils.api.pluginComunicate.internalPackets.packets.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class PacketExecutorRegistry {
    private static final Map<Class<? extends UniPacket>, PacketExecutable<?>> executors = new ConcurrentHashMap<>();


    static {
        registerDefaults();
    }

    private static void registerDefaults() {
        PacketExecutorRegistry.registerExecutor(SendBroadCastPacket.class, new SendBroadCastExecutor());
        PacketExecutorRegistry.registerExecutor(SendBroadCastComponentPacket.class, new SendBroadCastComponentExecutor());
        PacketExecutorRegistry.registerExecutor(SendSoundPacket.class, new SendSoundExecutor());
        PacketExecutorRegistry.registerExecutor(SendMessagePacket.class, new SendMessageExecutor());
        PacketExecutorRegistry.registerExecutor(SendTitlePacket.class, new SendTitleExecutor());
        PacketExecutorRegistry.registerExecutor(SendActionBarPacket.class, new SendActionBarExecutor());
        PacketExecutorRegistry.registerExecutor(SendMessageComponentPacket.class, new SendMessageCompomentExecutor());
        PacketExecutorRegistry.registerExecutor(ServerProfileAsyncPacket.class, new ServerProfileAsyncExecutor());
        PacketExecutorRegistry.registerExecutor(PlayerProfileAsyncPacket.class, new PlayerProfileAsyncExecutor());
    }

    public static <T extends UniPacket> void registerExecutor(Class<T> packetClass, PacketExecutable<T> executor) {
        executors.put(packetClass, executor);
    }

    public <T extends UniPacket> PacketExecutable<T> getExecutor(Class<T> packetClass) {
        return (PacketExecutable<T>) executors.get(packetClass);
    }
}
