# AllianceUtils ServerProfile API – Guia para Desenvolvedores

O `ServerProfile` é um sistema global (1 por servidor) para armazenar configurações e dados persistentes que afetam o servidor inteiro, não jogadores individuais. Funciona de forma semelhante ao `PlayerProfile`, utilizando o mesmo `FeatureManager` para gerenciamento de dados.

---

## Visão Geral

* Existe **apenas 1** `ServerProfile` por servidor (id fixo = 1 na tabela `alcserverprofile`)
* Dados armazenados no MySQL e sincronizados entre servidores via pacotes assíncronos
* Mantido em cache na memória para acesso rápido
* Alterações são marcadas como "dirty" e salvas automaticamente de forma assíncrona
* Dados serializados em JSON para persistência e transmissão

---

## Obtendo o ServerProfile para Leitura

```java
ServerProfile snapshot = Allianceutils.getInstance()
    .getServerProfileManager()
    .getSnapshot();

// snapshot é uma cópia do perfil atual, apenas para leitura
```

> **Importante:** Nunca modifique o objeto retornado diretamente, pois ele é uma cópia. Para modificar, use o método dedicado.

---

## Modificando o ServerProfile

Para alterar dados, utilize o método `modifyProfile` que garante acesso seguro e marca o perfil para salvamento:

```java
Allianceutils.getInstance()
    .getServerProfileManager()
    .modifyProfile(profile -> {
        // Modifique os dados dentro deste bloco
        profile.getFeatureManager().setBoolean(NS_MAINTENANCE, true);
        profile.getFeatureManager().setString(NS_ANNOUNCEMENT, "Servidor em manutenção");
    });
```

---

## Salvando o Perfil

O sistema salva automaticamente as alterações marcadas como "dirty", porém você pode forçar um salvamento assíncrono manual:

```java
Allianceutils.getInstance()
    .getServerProfileManager()
    .saveIfDirtyAsync();
```

---

## Aplicando um Perfil Remoto
Quando salvo com `saveIfDirtyAsync();` o Packet `ServerProfileAsyncPacket` é enviado.
para receber essas informações antes do propio servidor use o exemplo abaixo. (isso não é necessario, o AllianceUtils ja
atualiza isso internamente, use apenas se precisar atualizar algo fora do ServerProfileManager)

# Exemplo com codigo de tags

```java
import net.joseplay.allianceutils.api.internalListener.AuListener;
import net.joseplay.allianceutils.api.internalListener.annotations.AuEventHandler;
import net.joseplay.allianceutils.api.internalListener.events.PacketRecivedEvent;
import net.joseplay.allianceutils.api.playerProfile.entity.ServerProfile;
import net.joseplay.allianceutils.api.pluginComunicate.internalPackets.packets.ServerProfileAsyncPacket;
import net.joseplay.allianceutils.features.tags.data.TagsData;

public class TagListeners implements AuListener {

    @AuEventHandler
    public void onPacketRecivedEvent(PacketRecivedEvent event) {
        if (event.getPacket() instanceof ServerProfileAsyncPacket packet) {
            if (packet.isRemote()) return;

            ServerProfile profile = packet.getProfile();
            TagsData.onRemoteProfileApplied(profile);
        }
    }
}
```

Note que ele retorna em `if (packet.isRemote()) return;`
você não precisa usar isso aqui, so foi usado porque o ServerProfileAsyncPacket
envia um interno além do dispatcher do au.

```java
public class ServerProfileAsyncExecutor
        implements PacketExecutable<ServerProfileAsyncPacket> {

    @Override
    public void execute(ServerProfileAsyncPacket packet) {

        Bukkit.getScheduler().runTask(
                Allianceutils.getPlugin(),
                () -> {

                    Allianceutils.getInstance()
                            .getServerProfileManager()
                            .applyRemoteProfile(packet.getProfile());


                    packet.setRemote(false);
                    EventManager.callEvent(
                            new PacketRecivedEvent(packet)
                    );

                    Allianceutils.getInstance().getLogger()
                            .info("[ServerProfile] Atualização remota aplicada.");
                }
        );
    }
}
```
Note que em `packet.setRemote(false);` foi setado como false, para passar pelo sistema do `TagListener`
isso é só encheção de saco, você não vai usar isso a menos que realmente saiba 
oque esta fazendo e com oque esta mexendo.

---

## Exemplo Completo – Alternar Modo Manutenção

```java
public static final NameSpace NS_MAINTENANCE = new NameSpace("meuplugin", "maintenance_mode");

public void toggleMaintenance(boolean enable) {
    var manager = Allianceutils.getInstance().getServerProfileManager();

    manager.modifyProfile(profile -> {
        profile.getFeatureManager().setBoolean(NS_MAINTENANCE, enable);
    });

    // Salva e sincroniza com outros servidores
    manager.saveIfDirtyAsync();
}
```

---

## Boas Práticas

* Use sempre `NameSpace` para evitar conflitos entre plugins
* Nunca modifique diretamente o objeto retornado por `getSnapshot()`
* Faça modificações dentro do `modifyProfile` para garantir concorrência segura
* Utilize `saveIfDirtyAsync()` para garantir persistência quando necessário
* Evite armazenar dados que mudam com muita frequência (prefira cache ou outras soluções)

---

## API Resumida

| Método                              | Descrição                                       |
| ----------------------------------- | ----------------------------------------------- |
| `getSnapshot()`                     | Retorna uma cópia para leitura do ServerProfile |
| `modifyProfile(ProfileModifier)`    | Modifica o ServerProfile de forma segura        |
| `saveIfDirtyAsync()`                | Salva o perfil se houver alterações pendentes   |
| `applyRemoteProfile(ServerProfile)` | Aplica perfil recebido remotamente              |

---