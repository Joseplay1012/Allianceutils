package net.joseplay.allianceutils.api.extensions;

import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ExtensionRegistry {
    private final Map<String, ExtensionContainer> internalMap = new ConcurrentHashMap<>();
    private final String prefix = "[Extensions] [DEBUG] ";

    public ExtensionContainer put(String key, ExtensionContainer value) {
        Bukkit.getLogger().info(prefix + "Registering extension: " + key);
        return internalMap.put(key, value);
    }

    public ExtensionContainer remove(String key) {
        Bukkit.getLogger().warning(prefix + "Unregistering extension: " + key);
        return internalMap.remove(key);
    }

    public ExtensionContainer remove(ExtensionContainer container){
        Bukkit.getLogger().warning(prefix + "Unregistering extension: " + container.extensionFileName);
        return internalMap.remove(container.extensionFileName);
    }

    public void clear() {
        Bukkit.getLogger().warning(prefix + "Clearing all extensions");
        internalMap.clear();
    }

    public boolean containsKey(String key) {
        return internalMap.containsKey(key);
    }

    @Deprecated
    public boolean containsValue(Object object){
        return internalMap.containsValue(object);
    }

    public Set<Map.Entry<String, ExtensionContainer>> entrySet() {
        return internalMap.entrySet();
    }

    public Collection<ExtensionContainer> values(){
        return internalMap.values();
    }

    public int size() {
        return internalMap.size();
    }
}