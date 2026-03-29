package net.joseplay.allianceutils.api.preferences.data;

import net.joseplay.allianceutils.api.preferences.categories.UnCategory;
import net.joseplay.allianceutils.api.preferences.interfaces.Preference;
import net.joseplay.allianceutils.api.preferences.interfaces.PreferencePermission;
import net.joseplay.allianceutils.api.preferences.interfaces.PreferencesCategory;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central registry for managing preferences and their categories.
 *
 * <p>This class stores preferences grouped by {@link PreferencesCategory} and
 * provides static access methods for registration and retrieval.</p>
 *
 * <p>Notes:</p>
 * <ul>
 *     <li>All data is stored statically (global state)</li>
 *     <li>Thread-safety is partially handled via {@link ConcurrentHashMap}</li>
 *     <li>Lists themselves are NOT thread-safe</li>
 * </ul>
 */
public class PreferencesManager {

    /**
     * Default category used for deprecated APIs.
     */
    private static final UnCategory unCategory = new UnCategory();

    /**
     * Maps categories to their registered preferences.
     */
    private static final Map<PreferencesCategory, List<Preference>> preferences = new ConcurrentHashMap<>();

    /**
     * Maps category IDs to category instances.
     */
    private static final Map<String, PreferencesCategory> categorys = new ConcurrentHashMap<>();

    /**
     * Registers a category.
     *
     * @param category category to register
     */
    public static void addCategory(@NotNull PreferencesCategory category){
        categorys.put(category.getID(), category);
    }

    /**
     * Removes a category and its preferences.
     *
     * @param category category to remove
     */
    public static void removeCategory(PreferencesCategory category){
        categorys.remove(category.getID());
        preferences.remove(category);
    }

    /**
     * Retrieves a category by ID.
     *
     * @param id category identifier
     * @return category or null if not found
     */
    public static PreferencesCategory getCategory(@NotNull String id){
        return categorys.get(id);
    }

    /**
     * Registers a preference under a category.
     *
     * @param preference preference instance
     * @param category category to associate
     */
    public static void addPreference(@NotNull Preference preference, @NotNull PreferencesCategory category){
        preferences.computeIfAbsent(category, k -> new ArrayList<>()).add(preference);
    }

    /**
     * Registers a permission-based preference under a category.
     */
    public static void addPreference(@NotNull PreferencePermission preference, @NotNull PreferencesCategory category){
        preferences.computeIfAbsent(category, k -> new ArrayList<>()).add(preference);
    }

    /**
     * Removes a preference from a specific category.
     */
    public static void removePreference(PreferencesCategory category, Preference preference){
        preferences.computeIfAbsent(category, k -> new ArrayList<>()).remove(preference);
    }

    /**
     * Removes a preference from all categories.
     */
    public static void removePreference(Preference preference){
        for (Map.Entry<PreferencesCategory, List<Preference>> entry : preferences.entrySet()) {
            if (entry.getValue().contains(preference)) {
                removePreference(entry.getKey(), preference);
            }
        }
    }

    /**
     * Returns the preferences of a category.
     *
     * <p>WARNING: returned list is mutable and not thread-safe.</p>
     */
    public static List<Preference> getPreferences(PreferencesCategory category){
        return preferences.computeIfAbsent(category, k -> new ArrayList<>());
    }

    /**
     * Returns all preferences grouped by category.
     *
     * @return unmodifiable map view
     */
    public static Map<PreferencesCategory, List<Preference>> getPreferences() {
        return Collections.unmodifiableMap(preferences);
    }
}