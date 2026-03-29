# Como Criar uma Extensão para AllianceUtils

**Guia completo passo a passo**  
Este documento explica como criar uma extensão funcional para o plugin **AllianceUtils** utilizando o sistema de extensões integrado.

### 1. Pré-requisitos

- Java 17 ou superior (recomendado compatível com a versão do Spigot/Paper do servidor)
- Um IDE (IntelliJ IDEA, Eclipse, etc.)
- Maven ou Gradle para gerenciamento de dependências
- O JAR do **AllianceUtils** como dependência (provided/shaded)

### 2. Estrutura do Projeto

Crie um projeto Maven/Gradle comum com a seguinte estrutura básica:

```
MeuPluginExtension/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── seuplugin/
│       │           └── MinhaExtensao.java
│       └── resources/
│           ├── extension.yml
│           └── config.yml (opcional, será copiado automaticamente)
├── pom.xml (ou build.gradle)
```

### 3. Configuração do Build (pom.xml exemplo com Maven)

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.seuplugin</groupId>
    <artifactId>MinhaExtensao</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>

    <dependencies>
        <!-- AllianceUtils como provided (não será incluído no JAR final) -->
        <dependency>
            <groupId>net.alliancecraft</groupId>
            <artifactId>AllianceUtils</artifactId>
            <version>ULTIMA_VERSAO</version>
            <scope>provided</scope>
        </dependency>

        <!-- Bukkit/Spigot API -->
        <dependency>
            <groupId>org.spigotmc</groupId>
            <artifactId>spigot-api</artifactId>
            <version>1.20.4-R0.1-SNAPSHOT</version> <!-- ajuste para sua versão -->
            <scope>provided</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Plugin para shade se necessário (geralmente não precisa) -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.0</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals><goal>shade</goal></goals>
                        <configuration>
                            <relocations>
                                <!-- Não relocar AllianceUtils, pois é provided -->
                            </relocations>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

### 4. Arquivo extension.yml (OBRIGATÓRIO)

Coloque na pasta `src/main/resources/extension.yml`:

```yaml
name: MinhaExtensao
main: com.seuplugin.MinhaExtensao
version: 1.0.0
description: Uma extensão de exemplo para AllianceUtils
authors:
  - SeuNome
```

- `name`: Nome único da extensão (usado na pasta de dados)
- `main`: Caminho completo da classe principal
- `version`, `description`, `authors`: Opcionais mas recomendados

### 5. Classe Principal da Extensão

Crie a classe indicada no `main` do `extension.yml`:

```java
package com.seuplugin;

import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.api.extensions.AlliancePlugin;
import net.joseplay.allianceutils.api.extensions.Alliance;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventHandler;

public class MinhaExtensao extends AlliancePlugin implements Listener {

   @Override
   public void onEnable(Allianceutils plugin) {
      // Configuração padrão será criada automaticamente se existir config.yml no resources

      // Registrar listener de eventos
      Alliance.getAllianceListenerManager().registerListener(this, this);

      // Registrar um comando de exemplo
      Alliance.getAllianceCommandManager().registerCommand(this, new ComandoExemplo());

      getLogger().info("MinhaExtensao foi habilitada com sucesso!");
   }

   @Override
   public void onDisable() {
      getLogger().info("MinhaExtensao foi desabilitada.");
   }

   // Exemplo de listener
   @EventHandler
   public void onJoin(PlayerJoinEvent e) {
      e.getPlayer().sendMessage("§aBem-vindo! Esta mensagem vem da extensão MinhaExtensao!");
   }
}
```

### 6. Criando um Comando Personalizado

```java
package com.seuplugin;

import net.joseplay.allianceutils.api.extensions.interfaces.AllianceCommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ComandoExemplo implements AllianceCommandExecutor {

   @Override
   public String getName() {
      return "testeext"; // /testeext
   }

   @Override
   public List<String> alliances() {
      return List.of("te", "extensao"); // /te ou /extensao também funcionam
   }

   @Override
   public void execute(CommandSender sender, String[] args) {
      sender.sendMessage("§aComando da extensão executado com sucesso!");
   }

   @Override
   public List<String> tabComplete(Player player, String[] args) {
      return Collections.emptyList(); // ou sugestões personalizadas
   }
}
```

### 7. Config.yml Opcional (padrão)

Se quiser um config.yml padrão, coloque em `src/main/resources/config.yml`:

```yaml
mensagem-boas-vindas: "Bem-vindo ao servidor com AllianceUtils!"
ativar-recursos: true
```

Ele será copiado automaticamente para a pasta da extensão na primeira carga.

Acesse com `getConfig().getString("mensagem-boas-vindas")`.

### 8. Compilar e Instalar

1. Compile o projeto: `mvn clean package`
2. Copie o JAR gerado (em `target/`) para a pasta:
   ```
   plugins/AllianceUtils/extensions/
   ```
3. Reinicie ou recarregue o servidor (ou use `/extensions (load, reload, unload) <extensionname>` se disponível)

### 9. Comandos Úteis no Servidor

- `/extensions` → Lista todas as extensões carregadas (com hover de detalhes)
- Recarregar uma extensão específica: depende do comando principal do AllianceUtils

### Dicas Finais

- Sempre use os gerenciadores do `Alliance` para registrar listeners e comandos
- Use `getLogger()` para logs com prefixo automático
- Use os métodos `runTask*` para agendar tarefas com segurança
- Todas as tarefas registradas via `runTaskTimer` são canceladas automaticamente no disable

Pronto! Sua extensão agora está totalmente integrada ao sistema AllianceUtils.