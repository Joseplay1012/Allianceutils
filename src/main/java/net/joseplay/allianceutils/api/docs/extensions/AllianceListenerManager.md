# AllianceListenerManager

**Pacote:** `net.joseplay.allianceutils.api.extensions`

**Descrição:**  
Gerenciador responsável por registrar e desregistrar listeners de eventos Bukkit pertencentes a extensões específicas.

**Funcionalidades:**
- Associa listeners a uma instância de `AlliancePlugin`
- Permite desregistrar todos os listeners de uma extensão de uma vez
- Suporte a desregistro individual ou total

**Métodos principais:**
- `registerListener(AlliancePlugin, Listener)`
- `unregisterListeners(AlliancePlugin)`
- `unregisterListener(AlliancePlugin, Listener)`
- `unregisterAllListeners(JavaPlugin)`

**Importante:**  
Todos os listeners de uma extensão devem ser registrados por este gerenciador para que sejam corretamente limpos ao descarregar a extensão.