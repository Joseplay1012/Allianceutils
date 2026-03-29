package net.joseplay.allianceutils.api.locale;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class LocaleReader {

    private final JsonObject json;
    private final Locale locale;

    /**
     * Constructs an LocaleReader for the given locale.
     *
     * @param locale The locale used
     */
    LocaleReader(@NotNull Locale locale) {
        this.locale = locale;
        String fileName = locale.name() + ".json";
        InputStream inputStream = getFileFromResourceAsStream( "lang/" + fileName);
        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);
        Gson gson = new Gson();

        this.json = gson.fromJson(reader, JsonObject.class);
    }

    /**
     * Returns the value with the specific key.
     *
     * If the key does not exist, first it will check if an English translation
     * is available if not it returns null.
     *
     * @param key Name of the key that is requested.
     * @return Value as {@code String}. If this key does not exist {@code null}
     *         is returned.
     */
    String getValue(@NotNull String key) {
        JsonElement element = json.get(key);
        if (element == null) {
            if (locale == Locale.en_us) {
                return null;
            }

            return LocaleAPI.getCustomValue(key, Locale.en_us);
        }
        return element.getAsString();
    }

    /**
     * Returns all keys for the locale.
     *
     * @return An unmodifiable {@code List} containing all keys for this locale
     * @see Collections#unmodifiableList(List)
     */
    List<String> getKeys() {
        List<String> keys = new ArrayList<>();

        for(Map.Entry<String, JsonElement> entry : json.entrySet()) {
            keys.add(entry.getKey());
        }
        return Collections.unmodifiableList(keys);
    }

    /**
     * Returns an {@code InputStream} from the resource.
     *
     * @param path The resource filepath
     * @return {@link InputStream} from the resource file
     * @throws IllegalArgumentException If this path to the resource does not
     *         exist
     */
    @NotNull
    private InputStream getFileFromResourceAsStream(String path) {
        // The class loader that loaded the class
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(path);

        // The stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("Filepath not found! " + path);
        } else {
            return inputStream;
        }
    }
}
