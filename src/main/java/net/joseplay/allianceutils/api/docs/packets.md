# Sistema de Pacotes - AllianceUtils Docs

🚀 **Bem-vindo à documentação do Sistema de Pacotes do AllianceUtils!** Este módulo fornece uma estrutura flexível para
comunicação entre plugins/estensões, permitindo o envio e recebimento de pacotes de dados de forma assíncrona ou local.
Ele suporta integração com Redis para ambientes multi-servidores e execução local para simplicidade.

## O que é o Sistema de Pacotes?

O Sistema de Pacotes é parte a API de AllianceUtils para criar, registrar, enviar e executar pacotes de dados
customizados. Ele usa JSON para serialização e suporta dois modos principais:

- **Execução Local**: Para comunicação dentro do mesmo servidor.
- **Execução via Redis**: Para comunicação assíncrona entre servidores.

Principais componentes:

- **UniPacket**: Interface base para todos os pacotes.
- **PacketRegistry**: Gerencia o registro e desserialização de pacotes.
- **PacketExecutorRegistry**: Gerencia os executores que processam os pacotes recebidos.
- **PacketDispatcher**: Responsável por enviar e receber pacotes.

## Principais Recursos

Aqui estão os destaques do sistema:

* **Serialização JSON**: Todos os pacotes são convertidos para JSON, facilitando a transmissão via canais como Redis.
* **Registro Dinâmico**: Registre novos tipos de pacotes e seus executores em runtime.
* **Eventos Integrados**: Eventos como `PacketSendEvent` e `PacketRecivedEvent` permitem interceptar envios e
  recebimentos.
* **Suporte a Redis**: Integração nativa com Redis para envio assíncrono (canal: "alc:async").
* **Execução Condicional**: Opção para executar localmente ou apenas enviar via Redis.
* **Pacotes Padrão**: Inclui suporte para mensagens, títulos, sons, broadcasts e atualizações específicas (ex: glow de
  jogadores, perfis async).

> 💡 **Dica:** Use Redis em ambientes de produção para escalabilidade, mas desative para testes locais via
`registry.useRedis()`.

## Como Funciona?

O fluxo do sistema é simples e modular:

1. **Registro de Pacotes**: No `PacketRegistry`, associe um "type" (string) a uma classe que implementa `UniPacket`.
   Isso permite desserializar JSON para objetos de pacote.
2. **Registro de Executores**: No `PacketExecutorRegistry`, associe uma classe de pacote a um `PacketExecutable` que
   define como processá-lo.
3. **Envio de Pacotes**: Use `PacketDispatcher.send(UniPacket packet, boolean executeLocal)` para enviar. Se
   `useRedis()` for true e `executeLocal` false, envia apenas via Redis. Caso contrário, executa localmente e/ou envia.
4. **Recebimento**: Ao receber uma mensagem JSON via Redis (`receiveFromRedis(String jsonMessage)`), o sistema
   desserializa, chama eventos e executa o executor correspondente.

**Diagrama Simplificado do Fluxo:**

- Envio: Packet -> Event (Send) -> Executor (local) -> Redis (opcional).
- Recebimento: Redis -> Desserialização -> Event (Received) -> Executor.

Exemplo de ciclo completo:

- Um plugin cria um `SendMessagePacket`.
- Envia via dispatcher.
- Se Redis ativado, publica no canal "alc:async".
- Outro servidor recebe, desserializa e executa (ex: envia mensagem para jogador).

## Como Criar um Pacote Customizado?

Para criar um pacote novo, implemente a interface `UniPacket` ou estenda `UniPacketAbstract` (recomendado para
abstração).

### Passos:

1. **Defina a Classe do Pacote:**
   Crie uma classe que implemente `UniPacket` com:
    - `String getType()`: Retorna o identificador único (ex: "meu_pacote").
    - `JSONObject toJson()`: Converte o pacote para JSON.
    - Construtor de JSON: Para desserialização.

   Exemplo de código:

```java
   package meu.plugin.pacotes;

import net.joseplay.allianceutils.api.pluginComunicate.packets.UniPacket;
import org.json.JSONObject;
import org.bukkit.entity.Player;

public class MeuPacote implements UniPacket {
   private String mensagem;
   private String jogadorNome;

   // Construtor para criação
   public MeuPacote(String mensagem, Player jogador) {
      this.mensagem = mensagem;
      this.jogadorNome = jogador.getName();
   }

   // Construtor para desserialização
   public MeuPacote(JSONObject json) {
      this.mensagem = json.getString("mensagem");
      this.jogadorNome = json.getString("jogador");
   }

   @Override
   public String getType() {
      return "meu_pacote";
   }

   @Override
   public JSONObject toJson() {
      JSONObject json = new JSONObject();
      json.put("type", getType());
      json.put("mensagem", mensagem);
      json.put("jogador", jogadorNome);
      return json;
   }

   // Getters opcionais
   public String getMensagem() {
      return mensagem;
   }

   public String getJogadorNome() {
      return jogadorNome;
   }
}
   ```

> 💡 **Dica:** Sempre inclua o "type" no JSON para desserialização correta. Use campos simples para dados primitivos ou
> serializáveis.

2. **Registre o Pacote:**
   No seu plugin, acesse o `PacketRegistry` e registre:
   ```java
   PacketRegistry registry = // Obtenha via API ou injeção;
   registry.register("meu_pacote", MeuPacote.class);
   ```

## Como Criar um Executor?

O executor define a lógica de processamento ao receber o pacote.

1. **Implemente PacketExecutable:**
   ```java
   package meu.plugin.executores;

   import net.joseplay.allianceutils.api.pluginComunicate.packets.PacketExecutable;
   import org.bukkit.Bukkit;

   public class MeuExecutor implements PacketExecutable<MeuPacote> {
       @Override
       public void execute(MeuPacote packet) {
           // Lógica de execução
           Player jogador = Bukkit.getPlayer(packet.getJogadorNome());
           if (jogador != null) {
               jogador.sendMessage(packet.getMensagem());
           }
       }
   }
   ```

2. **Registre o Executor:**
   ```java
   PacketExecutorRegistry.registerExecutor(MeuPacote.class, new MeuExecutor());
   ```

> 💡 **Dica:** Registre executores no carregamento do plugin para garantir disponibilidade.

## Como Enviar um Pacote?

Use o `PacketDispatcher` para enviar.

Exemplo:

```java
UniPacket packet = new MeuPacote("Olá, mundo!", player);
PacketDispatcher dispatcher = // Obtenha via API;
        dispatcher.send(packet); // Executa local e envia via Redis se ativado
// Ou: dispatcher.send(packet, false); // Apenas envia via Redis
```

Se Redis estiver configurado (`useRedis() == true`), o pacote é publicado no canal "alc:async".

## Como Receber Pacotes?

- Para Redis: Integre um listener no Redis que chame `dispatcher.receiveFromRedis(jsonString)`.
- O sistema cuida da desserialização, eventos e execução automaticamente.

## Integração com Redis

- Acesse via `registry.getRedis()` para configurações avançadas.
- Certifique-se de que `PluginChannelDispatcher` esteja inicializado com Redis ativado.

> 💡 **Dica:** Em ambientes sem Redis, o sistema cai para execução local, ideal para desenvolvimento.

## Próximos Passos

📚 Explore os pacotes padrão como `SendMessagePacket` para inspiração. Teste em um servidor local e expanda para Redis em
produção.

🔗 **Referências Relacionadas:**

- [EventManager](/wiki/allianceutils-docs/api/eventmanager) (para eventos de pacotes).
- [RedisManager](/wiki/allianceutils-docs/api/redismanager).