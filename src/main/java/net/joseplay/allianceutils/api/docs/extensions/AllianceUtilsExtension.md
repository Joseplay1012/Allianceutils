# AllianceUtilsExtension

**Pacote:** `net.joseplay.allianceutils.api.extensions.interfaces`

**Descrição:**  
Interface básica que toda extensão deve implementar para ser carregada pelo `ExtensionLoader`.

**Métodos obrigatórios:**
- `void onEnable(Allianceutils mainPlugin)`: Chamado ao carregar a extensão
- `void onDisable()`: Chamado ao descarregar a extensão

**Recomendação:**  
A maioria das extensões deve estender `AlliancePlugin` (que já implementa esta interface) em vez de implementar diretamente.