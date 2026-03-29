package net.joseplay.allianceutils.Statics;

import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.api.claims.ClaimApi;
import net.joseplay.allianceutils.api.combat.CombatVerifier;
import net.joseplay.allianceutils.api.playerProfile.data.PlayerProfileManager;
import net.joseplay.allianceutils.api.playerProfile.data.ServerProfileManager;

public class AllianceUtilsApi {
    public static PlayerProfileManager getPlayerProfileManager() {
        return Allianceutils.getInstance().getPlayerProfileManager();
    }

    public static ServerProfileManager getServerProfileManager(){
        return Allianceutils.getInstance().getServerProfileManager();
    }

    public static ClaimApi getClaimAPI(){
        return Allianceutils.getInstance().getClaimApi();
    }

    public static CombatVerifier getCombatVerify(){return Allianceutils.getInstance().getCombatVerify();}

}
