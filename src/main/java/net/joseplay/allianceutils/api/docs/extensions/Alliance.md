# Alliance

**Pacote:** `net.joseplay.allianceutils.api.extensions`

**Descrição:**  
Classe utilitária central que fornece acesso aos gerenciadores globais do sistema de extensões.

**Componentes acessíveis:**
- `AllianceListenerManager`: Gerencia registro/desregistro de listeners por extensão
- `AllianceCommandManager`: Gerencia registro e execução de comandos de extensões

**Uso:**
```java
Alliance.getAllianceListenerManager().registerListener(extension, listener);
Alliance.getAllianceCommandManager().registerCommand(extension, commandExecutor);
```
## Nota:
É uma classe estática que serve como ponto de entrada para os sistemas de listener e comando do AllianceUtils.