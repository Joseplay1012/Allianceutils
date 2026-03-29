package net.joseplay.allianceutils.api.menu;

import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.api.internalListener.AuListener;
import net.joseplay.allianceutils.api.internalListener.EventManager;
import net.joseplay.allianceutils.api.internalListener.annotations.AuEventHandler;
import net.joseplay.allianceutils.api.internalListener.events.PluginShutdownEvent;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class MenuListener implements Listener, AuListener {

   public MenuListener(){
        EventManager.registerListener(this, Allianceutils.getPlugin());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGuiClick(InventoryClickEvent event){
        final Inventory clickInventory = event.getClickedInventory();

        if (clickInventory == null) return;

        if (clickInventory.getHolder() instanceof final Menu menu) {
            event.setCancelled(true);
            menu.click(event, event.getRawSlot());
        }

        if (event.getWhoClicked().getOpenInventory().getTopInventory().getHolder() instanceof Menu menu){
            if (menu instanceof SimpleMenu sm){
                if (sm.cancelClickPlayerInventory){
                    event.setCancelled(true);
                }
            }

            menu.unCheckedClick(event);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGuiClose(InventoryCloseEvent event){
        if (event.getInventory().getHolder() instanceof Menu menu){
            menu.onClose();
            Menu.menuList.remove(menu);
            MenuUpdater.unregister(menu);
        }
    }


    @AuEventHandler
    public void onPluginShowdown(PluginShutdownEvent event) {
        if (event.getPlugin().equals(Menu.plugin)) {
            for (Menu menu : Menu.menuList) {
                if (menu.getInventory().getSize() > 0) {
                    menu.getInventory().getViewers().forEach(HumanEntity::closeInventory);
                }
            }
            Menu.menuList.clear();
        }
    }
}
