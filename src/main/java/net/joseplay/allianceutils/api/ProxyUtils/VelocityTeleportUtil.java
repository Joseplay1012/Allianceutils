package net.joseplay.allianceutils.api.ProxyUtils;

import net.joseplay.allianceutils.api.pluginComunicate.PluginChannelDispatcher;
import net.joseplay.allianceutils.api.pluginComunicate.RedisManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class VelocityTeleportUtil {

    private final PluginChannelDispatcher channelDispatcher;
    private final String currentServerName;
    private final String velocityChannel = "BungeeCord";

    public VelocityTeleportUtil(PluginChannelDispatcher channelDispatcher, String currentServerName) {
        this.channelDispatcher = channelDispatcher;
        this.currentServerName = currentServerName;
    }

    /**
     * Teleporta um player para uma Location em outro servidor, se necessário.
     *
     * @param player       Jogador que será teleportado
     * @param targetServer Nome do servidor onde a Location está
     */
    public void teleportCrossServer(Player player, double x, double y, double z, float yaw, float pitch, String world, String targetServer) {
        if (currentServerName.equalsIgnoreCase(targetServer)) {
            teleportLocally(player, world, x, y, z, yaw, pitch);
        } else {
            sendVelocityConnect(player, targetServer);
            savePendingTeleport(player, x, y, z, yaw, pitch, world);
        }
    }

    private void teleportLocally(Player player, String world, double x, double y, double z, float yaw, float pitch) {
        World bukkitWorld = Bukkit.getWorld(world);
        if (bukkitWorld == null) {
            player.sendMessage("§cMundo não encontrado: " + world);
            return;
        }
        Location loc = new Location(bukkitWorld, x, y, z, yaw, pitch);
        player.teleport(loc);
    }

    private void sendVelocityConnect(Player player, String targetServer) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(byteStream);

            out.writeUTF("Connect");
            out.writeUTF(targetServer);

            player.sendPluginMessage(
                    channelDispatcher.getPlugin(),
                    velocityChannel,
                    byteStream.toByteArray()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void savePendingTeleport(
            Player player,
            double x, double y, double z,
            float yaw, float pitch,
            String world
    ) {
        RedisManager redis = channelDispatcher.getRedisManager();
        if (redis == null) {
            System.out.println("[VelocityTeleportUtil] Redis não configurado.");
            return;
        }

        String key = "pending_teleport:" + player.getUniqueId();

        redis.setAsync(key + ":world", world);
        redis.setAsync(key + ":x", String.valueOf(x));
        redis.setAsync(key + ":y", String.valueOf(y));
        redis.setAsync(key + ":z", String.valueOf(z));
        redis.setAsync(key + ":yaw", String.valueOf(yaw));
        redis.setAsync(key + ":pitch", String.valueOf(pitch));
    }

    public void checkPendingTeleport(Player player) {
        RedisManager redis = channelDispatcher.getRedisManager();
        if (redis == null) return;

        String base = "pending_teleport:" + player.getUniqueId();

        String world = redis.get(base + ":world");
        if (world == null) return;

        double x = Double.parseDouble(redis.get(base + ":x"));
        double y = Double.parseDouble(redis.get(base + ":y"));
        double z = Double.parseDouble(redis.get(base + ":z"));
        float yaw = Float.parseFloat(redis.get(base + ":yaw"));
        float pitch = Float.parseFloat(redis.get(base + ":pitch"));

        World bukkitWorld = Bukkit.getWorld(world);
        if (bukkitWorld != null) {
            player.teleport(new Location(bukkitWorld, x, y, z, yaw, pitch));
        }
    }
}
