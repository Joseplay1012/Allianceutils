# AllianceUtils PlayerProfile API - Guia para Desenvolvedores

Esta documentação explica como utilizar a API de **PlayerProfile** do plugin **AllianceUtils**, uma sistema leve e eficiente para armazenar dados persistentes por jogador de forma tipada e organizada, utilizando namespaces para evitar conflitos entre plugins/extensions.

## Visão Geral

O sistema permite que plugins ou extensões (AlliancePlugin) salvem dados personalizados para cada jogador de forma segura e persistente no banco de dados MySQL.

- Os dados são armazenados em cache em memória (ConcurrentHashMap).
- Alterações são marcadas como "dirty" e salvas automaticamente a cada 30 segundos.
- Os dados são serializados em JSON usando GSON.
- Cada chave é namespacada (`pluginname:chave`) para evitar colisões.

## Classes Principais

| Classe                  | Função                                                                 |
|-------------------------|------------------------------------------------------------------------|
| `PlayerProfile`         | Representa o perfil de um jogador (UUID + FeatureManager)              |
| `FeatureManager`        | Gerencia os dados (features) do perfil                                  |
| `PlayerProfileManager`  | Gerencia cache, carregamento e salvamento dos perfis                   |
| `NameSpace`             | Cria chaves namespacadas de forma segura                               |

## Como Obter o PlayerProfileManager

O manager é um singleton acessível através da instância principal do AllianceUtils:

```java
PlayerProfileManager profileManager = Alianceutils.getInstance().getPlayerProfileManager();
// ou, se preferir via API estática
PlayerProfileManager profileManager = AllianceUtilsApi.getPlayerProfileManager();
```

## Obtendo o Perfil de um Jogador apenas leitura

```java
import org.bukkit.entity.Player;
import java.util.UUID;

// Para jogador online
Player player = ...;
PlayerProfile profile = profileManager.getSnapshot(player.getUniqueId());

// Para qualquer UUID (carrega do banco se necessário)
UUID uuid = ...;
PlayerProfile profile = profileManager.getSnapshot(uuid);
```

> O método getSnapshot(UUID) retorna apenas um perfil de leitura, qualquer modificação é descartada.

## Criando Chaves Namespacadas (NameSpace)

**Recomendado**: Sempre use `NameSpace` para evitar conflitos.

```java
// Se seu plugin for uma AlliancePlugin (extensão)
NameSpace minhaChave = new NameSpace(this, "auto_pickup_enabled");

// Se for um plugin Bukkit normal
NameSpace minhaChave = new NameSpace(getPlugin(), "moedas");

// Ou manualmente (menos recomendado)
NameSpace minhaChave = new NameSpace("meuplugin", "nivel_vip");
```

> A chave final será algo como `meuplugin:auto_pickup_enabled` (tudo em minúsculas).

## Salvando e Lendo Dados

### Exemplos Básicos

```java
import net.joseplay.allianceutils.Statics.AllianceUtilsApi;
import net.joseplay.allianceutils.api.playerProfile.entity.FeatureManager;
import net.joseplay.allianceutils.api.playerProfile.entity.NameSpace;
import net.joseplay.allianceutils.api.playerProfile.entity.PlayerProfile;

import java.util.UUID;

public void test() {
    // Definindo valores
    AllianceUtilsApi
            .getPlayerProfileManager()
            .modify(player.getUniqueId(), profile -> {
                FeatureManager features = profile.getFeatureManager();
                features.setBoolean(new NameSpace(this, "auto_pickup"), true);
                features.setInt(new NameSpace(this, "moedas"), 1500);
                features.setString(new NameSpace(this, "rank"), "VIP");
                features.setDouble(new NameSpace(this, "xp"), 2450.5);
                features.setUUID(new NameSpace(this, "ultimo_amigo"), amigoUUID);
            });

    // Lendo valores (com valor padrão recomendado)
    PlayerProfile profile = profileManager.getSnapshot(player.getUniqueId());
    FeatureManager features = profile.getFeatureManager();

    boolean autoPickup = features.getBoolean(new NameSpace(this, "auto_pickup"), false);
    int moedas = features.getInt(new NameSpace(this, "moedas"), 0);
    String rank = features.getString(new NameSpace(this, "rank"), "Jogador");
    double xp = features.getDouble(new NameSpace(this, "xp"), 0.0);
    UUID ultimoAmigo = features.getUUID(new NameSpace(this, "ultimo_amigo")); // pode ser null
}
```

### Métodos de Conveniência

Para cada tipo primitivo há métodos específicos:

| Tipo         | Setter                          | Getter (com default)                          | Getter (sem default)               |
|--------------|---------------------------------|-----------------------------------------------|------------------------------------|
| Boolean      | `setBoolean(ns, boolean)`       | `getBoolean(ns, false)`                       | `getBoolean(ns)` → default false   |
| String       | `setString(ns, String)`         | `getString(ns, "default")`                     | `getString(ns)` → default null     |
| Int          | `setInt(ns, int)`               | `getInt(ns, 0)`                               | `getInt(ns)` → default 0           |
| Long         | `setLong(ns, long)`             | `getLong(ns, 0L)`                             | `getLong(ns)` → default 0L         |
| Double       | `setDouble(ns, double)`         | `getDouble(ns, 0.0)`                          | `getDouble(ns)` → default 0.0      |
| Float        | `setFloat(ns, float)`           | `getFloat(ns, 0.0f)`                          | `getFloat(ns)` → default 0.0f      |
| Short/Byte   | `setShort/setByte`              | `getShort/getByte`                            |                                    |
| Char         | `setChar(ns, char)`             | `getChar(ns, '')`                       |                                    |
| UUID         | `setUUID(ns, UUID)`             | —                                             | `getUUID(ns)` → pode retornar null |

## Tipos Permitidos

A API só aceita tipos primitivos wrappers e alguns tipos básicos:

- `String`
- `Integer`, `Long`, `Short`, `Byte`
- `Float`, `Double`
- `Boolean`
- `Character`
- `UUID`

**Não é permitido** salvar objetos complexos, listas, mapas, etc.

> Se tentar salvar um tipo inválido, será lançada uma `RuntimeException`.

## Salvamento Automático

- Toda vez que você usa `setFeature`, `setBoolean`, `setInt`, etc., o perfil é marcado como "dirty".
- A cada **30 segundos**, todos os perfis modificados são salvos em lote no banco de dados (tabela `alcprofiles`).
- Não é necessário chamar salvamento manualmente na maioria dos casos.

### Salvamento Manual (opcional)

```java
profileManager.saveToDataBase(uuid); // salva um único
```

## Boas Práticas

1. **Sempre use NameSpace** com o nome do seu plugin para evitar conflitos.
2. **Sempre forneça valores padrão** nos getters (ex: `getInt(ns, 0)`).
3. **Não use os métodos depreciados** que recebem String diretamente (eles serão removidos).
4. Se você descarregar seu plugin, considere salvar os perfis relevantes manualmente.

## Exemplo Completo

```java
import net.joseplay.allianceutils.Statics.AllianceUtilsApi;

public class MeuPlugin extends JavaPlugin {

    private final NameSpace NS_AUTO_PICKUP = new NameSpace(this, "auto_pickup");
    private final NameSpace NS_MOEDAS = new NameSpace(this, "moedas");

    public void toggleAutoPickup(Player player) {
        boolean atual = AllianceUtilsApi.
                getPlayerProfileManager()
                .getSnapshot(player.getUniqueId())
                .getFeatureManager()
                .getBoolean(NS_AUTO_PICKUP, false);

        AllianceUtilsApi
                .getPlayerProfileManager()
                .modify(player.getUniqueId(), profile -> {
                    FeatureManager fm = profile.getFeatureManager();

                    /**Aqui eu uso `atual` mas recomendo usar
                     * profile.getFeatureManager().getBoolean(NS_AUTO_PICKUP, false);
                     * assim ja pega da entitidade em tempo de salvamento. (Atualizado da propia entidade)
                     **/
                    fm.setBoolean(NS_AUTO_PICKUP, !atual);

                    player.sendMessage("Auto pickup: " + (!atual ? "ativado" : "desativado"));
                });
    }

    public void adicionarMoedas(Player player, int quantidade) {
        AllianceUtilsApi
                .getPlayerProfileManager()
                .modify(uuid, profile -> {
                    FeatureManager fm = profile.getFeatureManager();
                    int atual = fm.getInt(NS_MOEDAS, 0);
                    fm.setInt(NS_MOEDAS, atual + quantidade);
                    player.sendMessage("Você recebeu " + quantidade + " moedas! Total: " + (atual + quantidade));
                });

    }
}
```

Pronto! Agora você pode usar o sistema de perfis do AllianceUtils de forma segura e organizada.

Qualquer dúvida, consulte o código-fonte ou abra uma issue no repositório do AllianceUtils.