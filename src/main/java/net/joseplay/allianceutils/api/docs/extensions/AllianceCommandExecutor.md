# AllianceCommandExecutor

**Pacote:** `net.joseplay.allianceutils.api.extensions.interfaces`

**Descrição:**  
Interface que define o comportamento de um comando registrado por uma extensão no sistema AllianceUtils.

**Métodos obrigatórios:**

- `String getName()`  
  Retorna o nome principal do comando (sem "/").

- `List<String> alliances()`  
  Retorna uma lista de aliases (apelidos) do comando.

- `void execute(CommandSender sender, String[] args)`  
  Lógica de execução do comando.

- `List<String> tabComplete(Player player, String[] args)`  
  Sugestões para autocompletar com tab.

**Observação:**  
Os comandos não são registrados diretamente no `plugin.yml`. Em vez disso, são registrados via `AllianceCommandManager` usando instâncias que implementam esta interface. Isso permite comandos dinâmicos e "fakes" gerenciados pelo framework.