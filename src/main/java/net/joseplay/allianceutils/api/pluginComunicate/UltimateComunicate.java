package net.joseplay.allianceutils.api.pluginComunicate;

import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.api.pluginComunicate.internalPackets.packets.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class UltimateComunicate {
    private final PluginChannelDispatcher dispatcher;

    public UltimateComunicate(PluginChannelDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void sendSound(String soundName, UUID uuid, List<UUID> ignore) {
        Allianceutils.getInstance()
                .getDispatcher()
                .getPacketDispatcher()
                .send(new SendSoundPacket(soundName, uuid, ignore));
    }

    public void sendSound(String soundName, List<UUID> ignore) {
        sendSound(soundName, null, ignore);
    }

    public void sendSound(UUID uuid, String soundName) {
        sendSound(soundName, uuid, null);
    }

    public void sendSound(UUID uuid, Sound sound) {
        sendSound(uuid, sound.name());
    }

    public void sendSound(String soundName) {
        sendSound(soundName, null, null);
    }


    public void sendServerSound(Sound sound){
        sendSound(sound.name());
    }

    public void sendMessagePlayer(UUID uuid, String message){
        Allianceutils.getInstance().getDispatcher().getPacketDispatcher().send(new SendMessagePacket(message, uuid, null));
    }

    public void sendMessagePlayer(UUID uuid, Component message){
        Allianceutils.getInstance().getDispatcher().getPacketDispatcher().send(new SendMessageComponentPacket(message, uuid, null));
    }

    public void sendMessagePlayer(String playerName, String message){
        Allianceutils.getInstance().getDispatcher().getPacketDispatcher().send(new SendMessagePacket(message, null, playerName));
    }

    public void sendMessagePlayer(String playerName, Component message){
        Allianceutils.getInstance().getDispatcher().getPacketDispatcher().send(new SendMessageComponentPacket(message, null, playerName));
    }

    public void sendTitle(String title, String subtitle, List<UUID> ignore, int fadeId, int time, int fadeOut){
        Allianceutils.getInstance()
                .getDispatcher()
                .getPacketDispatcher()
                .send(new SendTitlePacket(title, subtitle, ignore, fadeId, time, fadeOut));
    }

    public void sendTitle(String title, String subtitle, List<UUID> ignore) {
        sendTitle(title, subtitle, ignore, 10, 70, 20);
    }

    public void sendTitle(String title, String subtitle,
                          int fadeIn, int time, int fadeOut) {
        sendTitle(title, subtitle, null, fadeIn, time, fadeOut);
    }

    public void sendTitle(String title, String subtitle) {
        sendTitle(title, subtitle, null);
    }

    public void sendTitle(String title) {
        sendTitle(title, null);
    }

    public void sendBroadcast(String message, String permission){
        Allianceutils.getInstance().getDispatcher().getPacketDispatcher().send(new SendBroadCastPacket(message, permission, null));
    }

    public void sendBroadcast(String message, String permission, List<UUID> list){
        Allianceutils.getInstance().getDispatcher().getPacketDispatcher().send(new SendBroadCastPacket(message, permission, list));
    }

    public void sendBroadcast(String message){
        sendBroadcast(message, null);
    }

    public void sendBroadcast(Component component, String permission){
        Allianceutils.getInstance().getDispatcher().getPacketDispatcher().send(new SendBroadCastComponentPacket(component, permission, null));
    }
    public void sendBroadcast(Component component, String permission, List<UUID> list){
        Allianceutils.getInstance().getDispatcher().getPacketDispatcher().send(new SendBroadCastComponentPacket(component, permission, list));
    }

    public void sendBroadcast(Component component){
        sendBroadcast(component, null);
    }

    public void sendActionBar(String message, UUID uuid, List<UUID> ignore){
        Allianceutils.getInstance().getDispatcher().getPacketDispatcher().send(new SendActionBarPacket(message, uuid, ignore));
    }

    public void sendActionBar(String message, UUID uuid){
        sendActionBar(message, uuid, null);
    }

    public void sendActionBar(String message, Player player){
        sendActionBar(message, player.getUniqueId());
    }

    public void sendActionBar(String message, List<UUID> ignore){
        Allianceutils.getInstance().getDispatcher().getPacketDispatcher().send(new SendActionBarPacket(message, ignore));
    }

    public void sendActionBar(String message){
        Allianceutils.getInstance().getDispatcher().getPacketDispatcher().send(new SendActionBarPacket(message));
    }
}
