package net.joseplay.allianceutils.DeBug;

import static net.joseplay.allianceutils.Utils.UnicodeFontReplace.allianceFontReplace;

public class SendDebugMessage {
    public static String sendDebugMessage(String message){
        return allianceFontReplace("[§cDEBUG§f]: "+message);
    }
}
