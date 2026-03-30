# AllianceUtils

[![License](https://img.shields.io/badge/License-AU--PCL-blue.svg)](LICENSE.txt)

AllianceUtils is a core library for Minecraft servers (Spigot/Paper) providing essential functionality for plugins and custom systems. It serves as a foundation for creating extensions, commands, point systems, and more while maintaining high compatibility and modularity.

---

## Credits

- Menu interface by [SimpleMineCode](https://github.com/SimpleMineCode/menu-api)  
- LocaleAPI by [Unp1xelt](https://github.com/Unp1xelt/Locale-API/)

## Features

- Modular system for managing points and rewards
- Simplified API for commands and events
- Easy integration with additional extensions and plugins
- Data storage and configuration support via YAML
- Ready structure for future implementations such as NPCs, chests, quests, etc

Note: The AllianceUtils core **cannot be sold**. Extensions may be distributed or sold only with authorization, keeping credits and the official repository link visible.

---

## Installation

### On a Spigot/Paper server

1. Download the latest `.jar` from the releases page:  
   [Releases](https://github.com/Joseplay1012/Allianceutils/releases)

2. Place the `.jar` file in the server's `plugins` folder

3. Restart the server to load the plugin

### Using the JAR locally in Maven

Add the local JAR to your project:

```xml
<dependency>
    <groupId>net.alliancecraft</groupId>
    <artifactId>allianceutils</artifactId>
    <version>1.0.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/libs/AllianceUtils.jar</systemPath>
</dependency>

```

### Using the JAR locally in Gradle

```gradle
dependencies {
    implementation files('libs/AllianceUtils.jar')
}

```

----------

## Documentation

Full JavaDoc is available at:  
[https://joseplay1012.github.io/Allianceutils/](https://joseplay1012.github.io/Allianceutils/)

----------


## Create extensions

visit: [how to create extensions](src/main/java/net/joseplay/allianceutils/api/docs/extensions/HowToCreateExtension.md)

## License

AllianceUtils is licensed under the **AllianceUtils Public Credit License (AU-PCL)**.

-   [Read the full license](https://github.com/Joseplay1012/Allianceutils/blob/master/LICENSE.txt)
    
-   Credits and the official repository link must always be visible: [https://github.com/Joseplay1012/Allianceutils](https://github.com/Joseplay1012/Allianceutils)
    

----------

## Useful Links

-   Official repository: [https://github.com/Joseplay1012/Allianceutils](https://github.com/Joseplay1012/Allianceutils)
    
-   Support / Contact: open an issue on GitHub
