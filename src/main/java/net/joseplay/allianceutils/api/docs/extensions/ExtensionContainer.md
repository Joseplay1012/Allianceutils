# ExtensionContainer

**Pacote:** `net.joseplay.allianceutils.api.extensions`

**Descrição:**  
Classe que encapsula todos os dados de uma extensão carregada.

**Campos:**
- `extension`: Instância da extensão (`AllianceUtilsExtension`)
- `classLoader`: `URLClassLoader` isolado da extensão
- `extensionName`: Nome definido no extension.yml
- `extensionFileName`: Nome do arquivo JAR

**Uso:**  
Armazenado no `ExtensionRegistry`. Usado pelo `ExtensionLoader` para gerenciar ciclo de vida.