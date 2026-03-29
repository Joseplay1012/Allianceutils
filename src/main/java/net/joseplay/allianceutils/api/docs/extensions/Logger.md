# Logger

**Pacote:** `net.joseplay.allianceutils.api.extensions`

**Descrição:**  
Wrapper simples em torno do logger do Bukkit, permitindo prefixo personalizado por extensão.

**Construtor:**
```java
new Logger("[AllianceUtils] MeuPlugin")
```
## Métodos:

- info(String)
- warning(String)
- error(String)

## Uso recomendado:
```Java
getLogger().info("Extensão iniciada com sucesso!");
```
## Alternativa: 
Extensões que herdam AlliancePlugin podem usar getLogger() que já retorna uma instância com prefixo correto.