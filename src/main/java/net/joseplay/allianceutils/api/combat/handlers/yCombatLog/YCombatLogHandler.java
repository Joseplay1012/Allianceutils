package net.joseplay.allianceutils.api.combat.handlers.yCombatLog;

import br.com.ystoreplugins.product.ycombatlog.CombatAPIHolder;
import net.joseplay.allianceutils.api.combat.handlers.CombatHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class YCombatLogHandler implements CombatHandler {
    private CombatAPIHolder apiHolder;

    public YCombatLogHandler(){
        apiHolder = getAPI();
    }

    @Override
    public String getName() {
        return "yCombatLog";
    }

    public CombatAPIHolder getAPI(){
        try {
            RegisteredServiceProvider<CombatAPIHolder> rsp = Bukkit.getServer().getServicesManager().getRegistration(CombatAPIHolder.class);
            return rsp != null ? rsp.getProvider() : null;
        } catch (Throwable t) {
            return null;
        }
    }

    @Override
    public boolean isValidAPI() {
        if (apiHolder == null){
            apiHolder = getAPI();
        }

        return apiHolder != null;
    }

    @Override
    public boolean isCombat(Player player) {
        if (!isValidAPI()) return false;

        return apiHolder.isInCombat(player.getName());
    }

    @Override
    public boolean addCombat(Player damaged, Player damager) {
        try{
            apiHolder.addCombat(damaged.getName());
            apiHolder.addCombat(damager.getName());
            return true;
        } catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean removeCombat(Player player) {
        try{
            apiHolder.removeCombat(player.getName());
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
