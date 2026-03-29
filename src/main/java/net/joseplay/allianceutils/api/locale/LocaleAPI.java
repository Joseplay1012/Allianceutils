package net.joseplay.allianceutils.api.locale;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocaleAPI {

    /**
     * Cache of loaded locale readers.
     *
     * <b>Key:</b> Locale
     * <b>Value:</b> LocaleReader instance
     *
     * <b>Side effects:</b>
     * - Grows over time (no eviction strategy)
     */
    protected static Map<Locale, LocaleReader> CACHE = new ConcurrentHashMap<>();

    /**
     * Retrieves a custom value from the locale file.
     *
     * <b>Use when:</b>
     * - Accessing arbitrary translation keys
     *
     * <b>Avoid when:</b>
     * - You need strict guarantees (can return null)
     *
     * @param key translation key
     * @param locale target locale
     * @return translated value or null if not found
     */
    @Nullable
    public static String getCustomValue(@NotNull String key, @NotNull Locale locale) {
        return getLocaleReader(locale).getValue(key);
    }

    /**
     * Gets the localized name of a potion effect.
     *
     * <b>Side effects:</b>
     * - Depends on Minecraft translation key format
     *
     * @param effect potion effect
     * @param locale locale
     * @return localized name
     */
    @NotNull
    public static String getEffect(PotionEffectType effect, Locale locale){
        LocaleReader reader = getLocaleReader(locale);
        String key = effect.getKey().getKey();

        return reader.getValue("effect.minecraft." + key);
    }

    /**
     * Shortcut using default locale (pt_br).
     */
    @NotNull
    public static String getEffect(PotionEffectType effect){
        return getEffect(effect, Locale.pt_br);
    }

    /**
     * Gets localized entity name.
     *
     * @param type entity type
     * @param locale locale
     * @return localized name
     */
    @NotNull
    public static String getEntity(EntityType type, Locale locale){
        LocaleReader reader = getLocaleReader(locale);
        String key = type.getKey().getKey();

        return reader.getValue("entity.minecraft." + key);
    }

    /**
     * Shortcut using default locale (pt_br).
     */
    @NotNull
    public static String getEntity(EntityType type){
        return getEntity(type, Locale.pt_br);
    }

    /**
     * Returns all translation keys available in a locale.
     *
     * <b>Use when:</b>
     * - Debugging or listing available translations
     *
     * <b>Side effects:</b>
     * - Returns internal data (should be immutable from reader)
     *
     * @param locale target locale
     * @return list of keys
     */
    @NotNull
    public static List<String> getAllKey(@NotNull Locale locale) {
        return getLocaleReader(locale).getKeys();
    }

    /**
     * Gets localized biome name.
     *
     * <b>Special case:</b>
     * - "custom" biome returns hardcoded value
     *
     * @param locale locale
     * @param biome biome
     * @return localized name
     */
    @NotNull
    public static String getBiome(@NotNull Locale locale, @NotNull Biome biome) {
        String name = biome.getKey().getKey();

        if (name.equals("custom")) return "Custom";

        return getLocaleReader(locale).getValue("biome.minecraft." + name);
    }

    /**
     * Shortcut using default locale (pt_br).
     */
    @NotNull
    public static String getEnchantment(Enchantment enchantment){
        return getEnchantment(enchantment, Locale.pt_br);
    }

    /**
     * Gets localized enchantment name.
     *
     * @param enchantment enchantment
     * @param locale locale
     * @return localized name
     */
    @NotNull
    public static String getEnchantment(Enchantment enchantment, Locale locale){
        LocaleReader reader = getLocaleReader(locale);
        String name = enchantment.getKey().getKey();

        return reader.getValue("enchantment.minecraft." + name);
    }

    /**
     * Shortcut using default locale (pt_br).
     */
    @NotNull
    public static String getMaterial(@NotNull Material mat) {
        return getMaterial(Locale.pt_br, mat);
    }

    /**
     * Gets localized material name.
     *
     * <b>Behavior:</b>
     * - Automatically detects block vs item
     * - Removes "wall_" prefix (Minecraft inconsistency workaround)
     *
     * <b>Side effects:</b>
     * - Relies on naming conventions from Mojang
     *
     * @param locale locale
     * @param mat material
     * @return localized name
     */
    @NotNull
    public static String getMaterial(@NotNull Locale locale, @NotNull Material mat) {
        LocaleReader reader = getLocaleReader(locale);
        String name = mat.getKey().getKey();

        // Fix for wall blocks (e.g. wall_torch → torch)
        if (name.contains("wall_")) {
            name = name.replace("wall_", "");
        }

        if (mat.isBlock()) {
            return reader.getValue("block.minecraft." + name);
        } else {
            return reader.getValue("item.minecraft." + name);
        }
    }

    /**
     * Retrieves or creates a cached LocaleReader.
     *
     * <b>Use when:</b>
     * - Accessing locale data repeatedly
     *
     * <b>Side effects:</b>
     * - Caches reader permanently (no cleanup)
     *
     * @param locale locale
     * @return cached reader
     */
    public static LocaleReader getLocaleReader(Locale locale){
        return CACHE.computeIfAbsent(locale, LocaleReader::new);
    }
}