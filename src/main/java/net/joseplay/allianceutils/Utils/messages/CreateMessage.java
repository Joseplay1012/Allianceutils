package net.joseplay.allianceutils.Utils.messages;

public class CreateMessage {
    public static String createColoredMessage(String message){
        return message.replaceAll("&", "§");
    }
}
