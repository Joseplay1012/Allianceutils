package net.joseplay.allianceutils.api.extensions;

import net.joseplay.allianceutils.api.extensions.interfaces.AllianceUtilsExtension;

import java.net.URLClassLoader;

public class ExtensionContainer {
    public final AllianceUtilsExtension extension;
    public final URLClassLoader classLoader;
    public final String extensionName;
    public final String extensionFileName;

    public ExtensionContainer(AllianceUtilsExtension extension, URLClassLoader classLoader, String extensionName, String extensionFileName) {
        this.extension = extension;
        this.classLoader = classLoader;
        this.extensionName = extensionName;
        this.extensionFileName = extensionFileName;
    }
}
