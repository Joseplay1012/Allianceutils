package net.joseplay.allianceutils.api.playerProfile.entity;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages dynamic key-value features for a profile.
 *
 * <p>This class provides a flexible storage system where values are associated
 * with {@link NameSpace} keys. Only a restricted set of primitive wrapper types
 * and common objects are allowed.</p>
 *
 * <p>Supported types include:</p>
 * <ul>
 *     <li>String</li>
 *     <li>Numeric wrappers (Integer, Long, etc.)</li>
 *     <li>Boolean</li>
 *     <li>Character</li>
 *     <li>UUID</li>
 * </ul>
 *
 * <p>Thread-safety is partially guaranteed via {@link ConcurrentHashMap}, but
 * compound operations are not atomic.</p>
 *
 * <p>The manager tracks a "dirty" state indicating if data has changed and needs persistence.</p>
 */
public class FeatureManager {

    /**
     * Internal feature storage.
     */
    private Map<String, Object> features = new ConcurrentHashMap<>();

    /**
     * Indicates if the data has been modified.
     * Marked as transient for serialization purposes.
     */
    private transient boolean dirty = false;

    /**
     * Allowed value types.
     */
    private static final Set<Class<?>> VALID_TYPES = Set.of(
            String.class,
            Integer.class, Long.class, Short.class, Byte.class,
            Float.class, Double.class,
            Boolean.class,
            Character.class,
            UUID.class
    );

    /**
     * Checks if the given object is a valid feature value.
     *
     * @param o object to validate
     * @return true if valid
     */
    public boolean isValidObj(Object o) {
        return o != null && VALID_TYPES.contains(o.getClass());
    }

    /**
     * Ensures the internal map is initialized.
     * Useful after deserialization.
     */
    public void ensureMaps(){
        if (features == null) features = new ConcurrentHashMap<>();
    }

    /**
     * Checks if a feature exists.
     *
     * @param nameSpace feature key
     * @return true if present
     */
    public boolean hasFeature(NameSpace nameSpace){
        return features.containsKey(nameSpace.getKey());
    }

    /**
     * Removes a feature.
     *
     * @param nameSpace feature key
     */
    public void removeFeature(NameSpace nameSpace){
        features.remove(nameSpace.getKey().toLowerCase());
    }

    /**
     * Returns the raw feature value.
     *
     * @param nameSpace feature key
     * @return stored value or null
     */
    public Object feature(NameSpace nameSpace){
        return features.get(nameSpace.getKey());
    }

    /**
     * Sets a feature value.
     *
     * @param nameSpace feature key
     * @param o value to store
     * @throws RuntimeException if type is invalid
     */
    public void setFeature(NameSpace nameSpace, Object o){
        if (!isValidObj(o)){
            throw new RuntimeException("Invalid object type: " + o.getClass().getName());
        }
        features.put(nameSpace.getKey().toLowerCase(), o);
        setDirty(true);
    }

    /**
     * Retrieves a boolean value.
     */
    public boolean getBoolean(NameSpace nameSpace, boolean defaultValue) {
        Object value = feature(nameSpace);
        return value instanceof Boolean ? (Boolean) value : defaultValue;
    }

    public boolean getBoolean(NameSpace nameSpace){
        return getBoolean(nameSpace, false);
    }

    /**
     * Retrieves a string value.
     */
    public String getString(NameSpace nameSpace, String defaultValue){
        Object value = feature(nameSpace);
        return value instanceof String ? (String) value : defaultValue;
    }

    public String getString(NameSpace nameSpace){
        return getString(nameSpace, null);
    }

    /**
     * Retrieves an integer value.
     */
    public int getInt(NameSpace nameSpace, int defaultValue){
        Object value = feature(nameSpace);
        return value instanceof Integer ? (int) value : defaultValue;
    }

    public int getInt(NameSpace nameSpace){
        return getInt(nameSpace, 0);
    }

    /**
     * Retrieves a long value.
     */
    public long getLong(NameSpace nameSpace, long defaultValue){
        Object value = feature(nameSpace);
        return value instanceof Long ? (long) value : defaultValue;
    }

    public long getLong(NameSpace nameSpace){
        return getLong(nameSpace, 0L);
    }

    /**
     * Retrieves a UUID value.
     *
     * @return UUID or null (no safety check)
     */
    public UUID getUUID(NameSpace nameSpace){
        return (UUID) feature(nameSpace);
    }

    /**
     * Marks the data as modified.
     */
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    /**
     * Returns whether the data was modified.
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * Returns the raw feature map.
     *
     * <p>WARNING: this exposes internal mutable state.</p>
     */
    public Map<String, Object> getFeatures() {
        return features;
    }
}