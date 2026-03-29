package net.joseplay.allianceutils.api.combat.handlers.CombatLogX;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;
import com.github.sirblobman.combatlogx.api.object.UntagReason;
import net.joseplay.allianceutils.api.combat.handlers.CombatHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class CombatLogXHandler implements CombatHandler {
    private ICombatLogX apiHolder;

    public CombatLogXHandler(){
        apiHolder = getAPI();
    }

    @Override
    public String getName() {
        return "CombatLogX";
    }


    public ICombatLogX getAPI() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        Plugin plugin = pluginManager.getPlugin("CombatLogX");
        return (ICombatLogX) plugin;
    }

    @Override
    public boolean isValidAPI(){
        if (apiHolder == null) {
            apiHolder = getAPI();

            return apiHolder == null;
        }

        return true;
    }

    @Override
    public boolean isCombat(Player player) {
        if (isValidAPI()){
            return false;
        }

        return apiHolder.getCombatManager().isInCombat(player);
    }

    @Override
    public boolean addCombat(Player damaged, Player damager) {
        if (isValidAPI()){
            return false;
        }
        return apiHolder.getCombatManager().tag(damaged, damager, TagType.PLAYER, TagReason.ATTACKER);
    }

    @Override
    public boolean removeCombat(Player player) {
        if (isValidAPI()){
            return false;
        }

        try{
            apiHolder.getCombatManager().untag(player, UntagReason.EXPIRE);
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
