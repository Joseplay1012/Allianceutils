package net.joseplay.allianceutils.api.pluginComunicate;

import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PluginChannelManager {
    private final String channel;
    private final JavaPlugin plugin;

    public PluginChannelManager(String channel, JavaPlugin plugin) {
        this.channel = channel;
        this.plugin = plugin;

        if (!plugin.getServer().getMessenger().isOutgoingChannelRegistered(plugin, channel)) {
            plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, channel);
        }

    }


    // Enviar a mensagem
    public void sendMessage(Player player, String[] contents) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteStream);

        try {

            for (int i = 0; i < contents.length; i++) {
                out.writeUTF(contents[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(plugin, channel, byteStream.toByteArray());
    }

    public void sendActionBar(Player player, String message){
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteStream);

        String[] contents = {
                "command=actionbar",
                "playerUUID=" + player.getUniqueId(),
                "playerName=" + player.getName(),
                "content=" + message
        };


        try{
            for (int i = 0; i < contents.length; i++) {
                out.writeUTF(contents[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(plugin, channel, byteStream.toByteArray());
    }

    public void sendMessage(Player player, String message){
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteStream);

        String[] contents = {
                "command=message",
                "playerUUID=" + player.getUniqueId(),
                "playerName=" + player.getName(),
                "content=" + message
        };


        try{
            for (int i = 0; i < contents.length; i++) {
                out.writeUTF(contents[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(plugin, channel, byteStream.toByteArray());
    }

    public void sendActionBar(OfflinePlayer offlinePlayer, String message){
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteStream);

        String[] contents = {
                "command=actionbar",
                "playerUUID=" + offlinePlayer.getUniqueId(),
                "playerName=" + offlinePlayer.getName(),
                "content=" + message
        };


        try{
            for (int i = 0; i < contents.length; i++) {
                out.writeUTF(contents[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        offlinePlayer.getPlayer().sendPluginMessage(plugin, channel, byteStream.toByteArray());
    }

    public void sendSound(Player player, Sound sound){
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteStream);

        String[] contents = {
                "command=sound",
                "playerUUID=" + player.getUniqueId(),
                "playerName=" + player.getName(),
                "content=" + sound.name().toUpperCase()
        };


        try{
            for (int i = 0; i < contents.length; i++) {
                out.writeUTF(contents[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(plugin, channel, byteStream.toByteArray());
    }

    public void sendSound(OfflinePlayer offlinePlayer, Sound sound){
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteStream);

        String[] contents = {
                "command=sound",
                "playerUUID=" + offlinePlayer.getUniqueId(),
                "playerName=" + offlinePlayer.getName(),
                "content=" + sound.name().toUpperCase()
        };


        try{
            for (int i = 0; i < contents.length; i++) {
                out.writeUTF(contents[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        offlinePlayer.getPlayer().sendPluginMessage(plugin, channel, byteStream.toByteArray());
    }

    public String getChannel() {
        return channel;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}
