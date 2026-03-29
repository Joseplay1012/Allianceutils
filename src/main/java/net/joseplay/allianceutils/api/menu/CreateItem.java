package net.joseplay.allianceutils.api.menu;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/**
 * Utility class for creating {@link ItemStack} instances with predefined metadata.
 *
 * <p>This class centralizes item creation logic, including name, lore,
 * custom model data, and persistent data container usage.</p>
 *
 * <b>Warning:</b>
 * <ul>
 *     <li>No null-safety checks are performed</li>
 *     <li>ItemMeta is assumed to always be non-null</li>
 *     <li>Incorrect material types may cause ClassCastException</li>
 * </ul>
 */
public class CreateItem {

    /**
     * Creates an ItemStack with name, lore and a persistent data key.
     *
     * @param name display name
     * @param lore lore lines
     * @param material item material
     * @param namespacedKey key to store in PersistentDataContainer
     * @return configured ItemStack
     */
    public static ItemStack createItemStack(String name, List<String> lore, Material material, NamespacedKey namespacedKey){
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.BOOLEAN, false);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * Creates an ItemStack with name, lore, custom model data and persistent data.
     *
     * @param name display name
     * @param lore lore lines
     * @param material item material
     * @param namespacedKey key to store in PersistentDataContainer
     * @param modelData custom model data value
     * @return configured ItemStack
     */
    public static ItemStack createItemStack(String name, List<String> lore, Material material, NamespacedKey namespacedKey, int modelData){
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        itemMeta.setCustomModelData(modelData);
        itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.BOOLEAN, false);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * Creates a basic ItemStack with name and lore.
     *
     * @param name display name
     * @param lore lore lines
     * @param material item material
     * @return configured ItemStack
     */
    public static ItemStack createItemStack(String name, List<String> lore, Material material){
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * Creates an ItemStack with name, lore and custom model data.
     *
     * @param name display name
     * @param lore lore lines
     * @param material item material
     * @param modelData custom model data value
     * @return configured ItemStack
     */
    public static ItemStack createItemStack(String name, List<String> lore, Material material, int modelData){
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        itemMeta.setCustomModelData(modelData);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * Creates a leather armor ItemStack with custom color.
     *
     * <b>Warning:</b>
     * - The material must be a leather armor piece
     * - Otherwise a ClassCastException will be thrown
     *
     * @param name display name
     * @param lore lore lines
     * @param material leather armor material
     * @param color armor color
     * @return configured ItemStack
     */
    public static ItemStack createLeatherItemStack(String name, List<String> lore, Material material, Color color){
        ItemStack itemStack = new ItemStack(material);
        LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();

        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        itemMeta.setColor(color);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}