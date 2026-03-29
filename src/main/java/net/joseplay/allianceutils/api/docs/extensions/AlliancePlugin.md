# AlliancePlugin

**Pacote:** `net.joseplay.allianceutils.api.extensions`

**Descrição:**  
Classe abstrata base para todas as extensões do AllianceUtils. Implementa a interface `AllianceUtilsExtension` e fornece funcionalidades comuns como gerenciamento de configuração, tarefas agendadas, logging personalizado, acesso a recursos dentro do JAR da extensão e tratamento de erros em tarefas.

**Principais funcionalidades:**
- Criação automática de `config.yml` padrão a partir do JAR da extensão
- Acesso a recursos embutidos no JAR (`getResource`)
- Logger personalizado com prefixo da extensão
- Gerenciamento de tarefas Bukkit com proteção contra exceções (`activeTasks`)
- Métodos utilitários para agendamento de tarefas síncronas e assíncronas
- Tratamento centralizado de erros em tarefas

**Campos importantes:**
- `dataFolder`: Pasta de dados da extensão
- `config`: Configuração carregada
- `extensionName`, `extensionVersion`, `extensionDescription`, `extensionAuthors`: Metadados da extensão
- `activeTasks`: Conjunto de tarefas ativas (para cancelamento no disable)

**Métodos principais:**
- `createDefaultConfig()`: Copia config.yml do JAR se não existir
- `getConfig()`: Carrega ou cria a configuração
- `runTask*` variantes: Executam tarefas com tratamento de erro automático
- `getLogger()`: Retorna logger com prefixo da extensão

**Uso recomendado:**  
Todas as extensões devem estender esta classe para ter acesso completo às facilidades do framework.