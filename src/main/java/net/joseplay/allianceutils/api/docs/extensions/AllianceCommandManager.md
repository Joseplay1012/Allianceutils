# AllianceCommandManager

**Pacote:** `net.joseplay.allianceutils.api.extensions`

**Descrição:**  
Gerenciador de comandos personalizado para extensões. Permite registrar comandos dinâmicos sem necessidade de entrada no `plugin.yml`.

**Características:**
- Comandos são registrados via `AllianceCommandExecutor`
- Suporta aliases
- Usa um sistema de "fake commands" (`FakeCommandRegister`) para interceptar execução
- Permite tab-complete personalizado

**Métodos principais:**
- `registerCommand(AlliancePlugin, AllianceCommandExecutor)`
- `getCommand(String name)`
- `getAllCommandNames()`
- `unregisterCommands(AlliancePlugin)`

**Observação:**  
Os comandos são executados interceptando `PlayerCommandPreprocessEvent` ou via comando map refletido.