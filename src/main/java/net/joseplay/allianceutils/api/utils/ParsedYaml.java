package net.joseplay.allianceutils.api.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ParsedYaml {
    private final Map<String, String> values = new HashMap<>();

    public void put(String key, String value) {
        values.put(key, value);
    }

    public String get(String key, String def) {
        return values.getOrDefault(key, def);
    }

    public double getDouble(String key, double def) {
        try {
            return Double.parseDouble(values.get(key));
        } catch (Exception e) {
            return def;
        }
    }

    public int getInt(String key, int def){
        try{
            return Integer.parseInt(values.get(key));
        } catch (Exception e){
            return def;
        }
    }

    public boolean has(String key) {
        return values.containsKey(key);
    }

    public Map<String, String> asMap() {
        return Collections.unmodifiableMap(values);
    }
}
