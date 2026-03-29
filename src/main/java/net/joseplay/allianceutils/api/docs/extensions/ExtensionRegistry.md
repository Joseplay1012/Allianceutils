# ExtensionRegistry

**Pacote:** `net.joseplay.allianceutils.api.extensions`

**Descrição:**  
Registro interno que armazena todas as extensões atualmente carregadas, mapeadas pelo nome do arquivo JAR.

**Estrutura:**
- Usa `ConcurrentHashMap<String, ExtensionContainer>`
- Chave: nome do arquivo JAR
- Valor: `ExtensionContainer` com extensão, classloader e metadados

**Métodos principais:**
- `put()`, `remove()`, `clear()`
- `values()`, `entrySet()`, `size()`
- Logs de debug ao adicionar/remover extensões

**Uso:**  
Gerenciado exclusivamente pelo `ExtensionLoader`. Não deve ser manipulado diretamente por extensões.