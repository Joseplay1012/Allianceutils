# How to Create an Extension for AllianceUtils

**Complete step-by-step guide**
This document explains how to create a functional extension for the **AllianceUtils** plugin using its built-in extension system.

---

## 1. Requirements

* Java 21 or higher (must match your server's Spigot/Paper version)
* An IDE (IntelliJ IDEA, Eclipse, etc.)
* Maven or Gradle for dependency management
* The **AllianceUtils** JAR as a dependency (`provided` scope)

---

## 2. Project Structure

Create a standard Maven/Gradle project with the following structure:

```
MyPluginExtension/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── yourplugin/
│       │           └── MyExtension.java
│       └── resources/
│           ├── extension.yml
│           └── config.yml (optional, auto-copied on first load)
├── pom.xml (or build.gradle)
```

---

## 3. Build Configuration (Maven example)

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.yourplugin</groupId>
    <artifactId>MyExtension</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>

    <dependencies>
        <!-- AllianceUtils (provided) -->
        <dependency>
            <groupId>net.joseplay</groupId>
            <artifactId>AllianceUtils</artifactId>
            <version>LATEST_VERSION</version>
            <scope>provided</scope>
        </dependency>

        <!-- Spigot API -->
        <dependency>
            <groupId>org.spigotmc</groupId>
            <artifactId>spigot-api</artifactId>
            <version>1.20.4-R0.1-SNAPSHOT</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Optional shading (usually not required) -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.0</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals><goal>shade</goal></goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## 4. extension.yml (REQUIRED)

Create `src/main/resources/extension.yml`:

```yaml
name: MyExtension
main: com.yourplugin.MyExtension
version: 1.0.0
description: Example extension for AllianceUtils
authors:
  - YourName
```

**Fields:**

* `name`: Unique extension identifier (used as data folder name)
* `main`: Fully qualified main class
* `version`, `description`, `authors`: Optional but recommended

---

## 5. Main Extension Class

```java
package com.yourplugin;

import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.api.extensions.AlliancePlugin;
import net.joseplay.allianceutils.api.extensions.Alliance;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventHandler;

public class MyExtension extends AlliancePlugin implements Listener {

   @Override
   public void onEnable(Allianceutils plugin) {

      // Register listeners
      Alliance.getAllianceListenerManager().registerListener(this, this);

      // Register commands
      Alliance.getAllianceCommandManager().registerCommand(this, new ExampleCommand());

      getLogger().info("MyExtension enabled successfully.");
   }

   @Override
   public void onDisable() {
      getLogger().info("MyExtension disabled.");
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent e) {
      e.getPlayer().sendMessage("§aWelcome! This message is from MyExtension.");
   }
}
```

---

## 6. Creating a Custom Command

```java
package com.yourplugin;

import net.joseplay.allianceutils.api.extensions.interfaces.AllianceCommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ExampleCommand implements AllianceCommandExecutor {

   @Override
   public String getName() {
      return "testext";
   }

   @Override
   public List<String> alliances() {
      return List.of("te", "extension");
   }

   @Override
   public void execute(CommandSender sender, String[] args) {
      sender.sendMessage("§aExtension command executed successfully!");
   }

   @Override
   public List<String> tabComplete(Player player, String[] args) {
      return Collections.emptyList();
   }
}
```

---

## 7. Optional config.yml

```yaml
welcome-message: "Welcome to the server with AllianceUtils!"
enable-features: true
```

Access:

```java
getConfig().getString("welcome-message");
```

---

## 8. Build and Install

1. Build the project:

   ```
   mvn clean package
   ```

2. Move the generated JAR to:

   ```
   plugins/AllianceUtils/extensions/
   ```

3. Reload or restart the server:

   ```
   /extensions load <name>
   /extensions reload <name>
   /extensions unload <name>
   ```

---

## 9. Useful Commands

* `/extensions` → Lists all loaded extensions (with hover details)

---

## Final Notes

* Always register from `Alliance` managers (never use Bukkit)
* Avoid static state unless necessarily → extensions can be reloaded
* Tasks created from your extension instance are **auto-cancelled on disable**
* `config.yml` is copied automatically only on first load
