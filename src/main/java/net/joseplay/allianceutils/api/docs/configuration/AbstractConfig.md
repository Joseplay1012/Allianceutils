# AbstractConfig

**Pacote:** `net.craft.allianceutils.api.configuration`

## Descrição

`AbstractConfig` é a classe abstrata base para gerenciamento centralizado de arquivos de configuração YAML no Utils.

Ela fornece uma arquitetura tipada, segura e flexível para configurações, baseada em **enums fortemente tipados** (`ConfigKey`) que representam cada chave do arquivo.  
O sistema elimina erros comuns como paths inválidos, tipos incorretos e inconsistências entre código e YAML.

O carregamento suporta **keys tanto dentro de seções quanto diretamente na raiz do arquivo**, sem exigir um padrão fixo do desenvolvedor.

---

## Principais vantagens

- Tipagem forte via enum (`ConfigKey`)
- Cache interno para acesso rápido
- Valores padrão automáticos
- Processamento automático de mensagens (cores e gradientes)
- Validação rigorosa de tipos
- Salvamento automático ao alterar valores
- Suporte a listas tipadas
- Suporte a keys em section (`section.key`) ou na raiz (`key`)
- Compatível com configs legadas

---

## Suporte a Paths Flexíveis

O `AbstractConfig` resolve automaticamente o caminho de cada chave seguindo esta prioridade:

1. `configPath.key` (dentro da section definida)
2. `key` (direto na raiz do YAML)
3. Valor padrão (fallback)

Isso permite que o desenvolvedor use ou não seções sem quebrar o carregamento.

### Exemplo com section

```yaml
messages:
  prefix: "&7[&bServer&7]"
````

### Exemplo sem section

```yaml
prefix: "&7[&bServer&7]"
```

Ambos funcionam sem alterar o enum ou o código.

---

## Estrutura Obrigatória para Uso

Para usar `AbstractConfig`, você deve criar:

1. **Um enum que implementa `ConfigKey`**
   Define path, tipo e valor padrão.

2. **Uma classe concreta que estende `AbstractConfig`**
   Representa um arquivo YAML específico.

---

## Exemplo Completo de Uso

### Enum de Chaves (`MessagesKeys.java`)

```java
public enum MessagesKeys implements AbstractConfig.ConfigKey {

    PREFIX("prefix", AbstractConfig.ValueType.STRING, "§8[§b§8] §7"),
    PLAYER_JOIN("player-join", AbstractConfig.ValueType.STRING, "&a{player} entrou no servidor!"),
    PLAYER_QUIT("player-quit", AbstractConfig.ValueType.STRING, "&c{player} saiu do servidor."),
    WELCOME_TITLE("welcome-title", AbstractConfig.ValueType.STRING_LIST,
            List.of("&b&lBem-vindo!", "&f ao  Server"));

    private final String path;
    private final AbstractConfig.ValueType type;
    private final Object defaultValue;

    MessagesKeys(String path, AbstractConfig.ValueType type, Object defaultValue) {
        this.path = path;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public AbstractConfig.ValueType getType() {
        return type;
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }
}
```

---

### Classe de Configuração (`MessagesConfig.java`)

```java
public class MessagesConfig extends AbstractConfig {

    public MessagesConfig(File file) {
        super(file, "messages", MessagesKeys.class);
    }

    @Override
    protected Object getDefaultValue(Enum<?> key) {
        return ((MessagesKeys) key).getDefaultValue();
    }

    @Override
    protected ConfigKey getConfigKey(Enum<?> key) {
        return (MessagesKeys) key;
    }
}
```

> O parâmetro `"messages"` define apenas a section preferencial, não obrigatória.

---

### Uso no Plugin

```java
MessagesConfig messages = new MessagesConfig(
        new File(getDataFolder(), "messages.yml")
);

String prefix = messages.getString(MessagesKeys.PREFIX);
List<String> title = messages.getStringList(MessagesKeys.WELCOME_TITLE);

messages.setValue(MessagesKeys.PREFIX, "&8[&aNovoPrefix&8]");
```

---

## Recursos Automáticos

* Copia o arquivo do `resources/` se existir
* Cria valores ausentes usando defaults
* Substitui valores inválidos automaticamente
* Processa mensagens com `GradientMessage`
* Cache interno para leitura rápida
* `setValue()` atualiza cache e arquivo imediatamente
* Suporte transparente a paths com ou sem section

---

## Métodos Disponíveis

| Método               | Retorno       | Descrição                        |
| -------------------- | ------------- | -------------------------------- |
| `getString(key)`     | String        | Mensagem formatada               |
| `getStringList(key)` | List<String>  | Lista de mensagens               |
| `getInt(key)`        | int           | Inteiro                          |
| `getLong(key)`       | long          | Long                             |
| `getBoolean(key)`    | boolean       | Boolean                          |
| `getDouble(key)`     | double        | Double                           |
| `getIntList(key)`    | List<Integer> | Lista de inteiros                |
| `setValue(key,val)`  | boolean       | Atualiza cache e salva o arquivo |

---

## Recomendações

* Uma classe de config por arquivo YAML
* Sempre use enums, nunca strings manuais
* Seções são opcionais
* Compatível com configs antigas sem refactor forçado

---

**AbstractConfig** centraliza e simplifica o gerenciamento de configurações no Utils, mantendo flexibilidade sem sacrificar segurança ou performance.
