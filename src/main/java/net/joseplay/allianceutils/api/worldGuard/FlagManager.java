package net.joseplay.allianceutils.api.worldGuard;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import net.joseplay.allianceutils.Utils.messages.Logger;

public class FlagManager {

    public static StateFlag NO_ELYTRA_FLAG;

    public static void registerElytraFlags() {
        try {
            FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

            try {
                NO_ELYTRA_FLAG = new StateFlag("no-elytra", false); // false = permitido por padrão
                registry.register(NO_ELYTRA_FLAG);
            } catch (FlagConflictException e) {

                Flag<?> existing = registry.get("no-elytra");
                if (existing instanceof StateFlag) {
                    NO_ELYTRA_FLAG = (StateFlag) existing;
                }
            }
        } catch (Exception e) {
            Logger.warning("WorldGuard não encontrado:" + e.getMessage());
        }
    }
}