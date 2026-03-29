# ExtensionLoader

**Pacote:** `net.joseplay.allianceutils.api.extensions`

**Descrição:**  
Classe responsável por carregar, descarregar e recarregar extensões (.jar) da pasta `extensions/`.

**Funcionalidades principais:**
- Carregamento automático de todas as extensões na pasta
- Leitura de `extension.yml` para metadados
- Uso de `URLClassLoader` isolado por extensão
- Suporte a hot-reload (reloadExtension)
- Descarregamento seguro com limpeza de tarefas, listeners e comandos
- Tratamento robusto de erros com retries

**Fluxo de carregamento:**
1. Lê `extension.yml`
2. Carrega classe principal
3. Instancia e configura `AlliancePlugin`
4. Chama `onEnable()`
5. Registra no `ExtensionRegistry`

**Métodos úteis:**
- `loadExtensions()`
- `reloadExtension(name)`
- `unloadExtension(name)`
- `getActiveExtensions()`
- `getActiveExtensionsAsSingleLine()` → componente de chat com hover

**Segurança:**  
Cancela tarefas ativas, remove listeners e comandos ao descarregar.