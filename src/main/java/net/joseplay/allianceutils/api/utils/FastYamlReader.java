package net.joseplay.allianceutils.api.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class FastYamlReader {
    public static ParsedYaml read(File file) {
        ParsedYaml parsed = new ParsedYaml();
        Deque<Section> stack = new ArrayDeque<>();

        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            String raw;
            while ((raw = reader.readLine()) != null) {
                if (raw.isEmpty()) continue;

                int indent = leadingSpaces(raw);
                String line = stripLeading(raw);
                if (line.isEmpty() || line.startsWith("#")) continue;

                int idx = line.indexOf(':');
                if (idx < 0) continue;

                String key = line.substring(0, idx).trim();
                String after = line.substring(idx + 1).trim();

                while (!stack.isEmpty() && stack.peek().indent >= indent) {
                    stack.pop();
                }

                if (after.isEmpty()) {
                    stack.push(new Section(indent, key));
                    continue;
                }

                String fullKey = buildPath(stack, key);
                String val = stripQuotes(after);

                parsed.put(fullKey, val);
            }
        } catch (IOException ignored) {}

        return parsed;
    }

    private static int leadingSpaces(String s) {
        int i = 0, count = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == ' ') { count++; i++; }
            else if (c == '\t') { count += 4; i++; }
            else break;
        }
        return count;
    }

    private static String stripLeading(String s) {
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == ' ' || c == '\t') i++;
            else break;
        }
        return s.substring(i);
    }

    private static String buildPath(Deque<Section> stack, String key) {
        if (stack.isEmpty()) return key;
        StringBuilder sb = new StringBuilder();
        Iterator<Section> it = stack.descendingIterator();
        while (it.hasNext()) {
            sb.append(it.next().name).append('.');
        }
        sb.append(key);
        return sb.toString();
    }

    private static String stripQuotes(String v) {
        if (v.length() >= 2) {
            char a = v.charAt(0), b = v.charAt(v.length() - 1);
            if ((a == '\'' && b == '\'') || (a == '"' && b == '"')) {
                return v.substring(1, v.length() - 1);
            }
        }
        return v;
    }

    private static double parseDouble(String v, double def) {
        try { return Double.parseDouble(v); } catch (Exception e) { return def; }
    }

    private static final class Section {
        final int indent;
        final String name;
        Section(int indent, String name) { this.indent = indent; this.name = name; }
    }
}
