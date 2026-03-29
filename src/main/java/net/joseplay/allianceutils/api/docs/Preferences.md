# Preferências

A API permite que criem categorias de preferências, implementem preferências individuais e as registrem no gerenciador central. Todas as operações são thread-safe, utilizando estruturas como `ConcurrentHashMap`.

A API é composta por interfaces e classes principais:
- `PreferencesCategory`: Interface para definir categorias de preferências.
- `Preference`: Interface para definir preferências individuais.
- `PreferencePermission`: Uma classe abstrata que implementa `Preference` adicionndo permissão. 
- `PreferenceEntity`: Classe que representa uma entidade de preferência, contendo um ícone e uma ação para eventos de clique no inventário.
- `PreferencesManager`: Classe estática responsável pelo registro e gerenciamento de categorias e preferências.

## Criando uma Categoria de Preferências

Para criar uma categoria, implemente a interface `net.joseplay.allianceutils.api.preferences.interfaces.PreferencesCategory`. Essa interface requer os seguintes métodos:

- `String getName()`: Retorna o nome da categoria (usado para exibição).
- `List<String> getDescription()`: Retorna uma lista de strings com a descrição da categoria.
- `String getID()`: Retorna um identificador único para a categoria (deve ser uma string única, como "minha-categoria").
- `ItemStack getIcon()`: Retorna um `ItemStack` representando o ícone da categoria no menu.

### Exemplo de Implementação de Categoria

```java
package meu.plugin.exemplo;

import net.joseplay.allianceutils.api.menu.CreateItem;
import net.joseplay.allianceutils.api.preferences.interfaces.PreferencesCategory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class MinhaCategoria implements PreferencesCategory {
    @Override
    public String getName() {
        return "Minha Categoria Personalizada";
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList("Descrição linha 1", "Descrição linha 2");
    }

    @Override
    public String getID() {
        return "minha-categoria";
    }

    @Override
    public ItemStack getIcon() {
        return CreateItem.createItemStack(
                getName(),
                getDescription(),
                Material.COMPASS
        );
    }
}
```

## Registrando uma Categoria

Após criar a categoria, registre-a no `PreferencesManager` usando o método `addCategory(PreferencesCategory category)`. Isso adiciona a categoria ao mapa de categorias disponíveis.

### Exemplo de Registro de Categoria

No método `onEnable()` do seu plugin/extensão principal:

```java
import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.api.extensions.AlliancePlugin;
import net.joseplay.allianceutils.api.preferences.data.PreferencesManager;
import meu.plugin.exemplo.MinhaCategoria;

public class MeuPlugin extends AlliancePlugin {
    @Override
    public void onEnable(Allianceutils plugin) {
        PreferencesCategory minhaCategoria = new MinhaCategoria();
        PreferencesManager.addCategory(minhaCategoria);
    }
}
```

Para remover uma categoria, use `PreferencesManager.removeCategory(PreferencesCategory category)`.

Para obter uma categoria registrada, use `PreferencesManager.getCategory(String id)`.

## Exemplo de remoção e como obter uma categoria

```java
import net.joseplay.allianceutils.Allianceutils;
import net.joseplay.allianceutils.api.extensions.AlliancePlugin;
import net.joseplay.allianceutils.api.preferences.data.PreferencesManager;
import meu.plugin.exemplo.MinhaCategoria;

public class MeuPlugin extends AlliancePlugin {
    @Override
    public void onEnable(Allianceutils plugin) {
        //Pega a categoria
        MinhaCategoria minhaCategoria = PreferencesManager.getCategory("categoryID");
        //Remove-a
        PreferencesManager.removeCategory(minhaCategoria);

    }
}
```

## Criando uma Preferência

Para criar uma preferência, implemente a interface `net.joseplay.allianceutils.api.preferences.interfaces.Preference`. Essa interface requer os seguintes métodos:

- `PreferenceEntity get(UUID uuid)`: Retorna uma `PreferenceEntity` para um jogador específico (identificado pelo UUID). A `PreferenceEntity` contém um `ItemStack` como ícone e um `Consumer<InventoryClickEvent>` como ação executada ao clicar no item no menu.
- `void menu(PagedCustomMenu menu)`: Configura o menu paginado personalizado para exibir as opções da preferência.

### Exemplo de Implementação de Preferência

```java
package meu.plugin.exemplo;

import net.joseplay.allianceutils.api.menu.PagedCustomMenu;
import net.joseplay.allianceutils.api.preferences.entities.PreferenceEntity;
import net.joseplay.allianceutils.api.preferences.interfaces.Preference;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.Consumer;

public class MinhaPreferencia implements Preference {
    @Override
    public PreferenceEntity get(UUID uuid) {
        // Lógica para obter o estado da preferência para o jogador (ex: de um banco de dados)
        ItemStack icon = new ItemStack(Material.APPLE);
        Consumer<InventoryClickEvent> action = event -> {
            // Ação ao clicar: exibir uma mensagem ou alterar configuração
            event.getWhoClicked().sendMessage("Preferência alterada!");
            event.setCancelled(true);
        };
        return new PreferenceEntity(icon, action);
    }

    @Override
    public void menu(PagedCustomMenu menu) {
    }
}
```

## Criando uma PreferencePermission

```java
package meu.plugin.exemplo;

import net.joseplay.allianceutils.api.menu.PagedCustomMenu;
import net.joseplay.allianceutils.api.preferences.entities.PreferenceEntity;
import net.joseplay.allianceutils.api.preferences.interfaces.PreferencePermission;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.Consumer;

public class MinhaPreferenciaComPermissão implements PreferencePermission {
    public MinhaPreferencia() {
        super("my.permission");
    }

    @Override
    public PreferenceEntity get(UUID uuid) {
        // Lógica para obter o estado da preferência para o jogador (ex: de um banco de dados)
        ItemStack icon = new ItemStack(Material.APPLE);
        Consumer<InventoryClickEvent> action = event -> {
            // Ação ao clicar: exibir uma mensagem ou alterar configuração
            event.getWhoClicked().sendMessage("Preferência alterada!");
            event.setCancelled(true);
        };
        return new PreferenceEntity(icon, action);
    }

    @Override
    public void menu(PagedCustomMenu menu) {
    }
}
```

## Registrando uma Preferência

Após criar a preferência, registre-a no `PreferencesManager` associando-a a uma categoria existente usando `addPreference(Preference preference, PreferencesCategory category)`. Isso adiciona a preferência à lista de preferências da categoria.

Nota: Os métodos depreciados `addPreference(Preference preference, String key)` e `addPreference(Preference preference, NameSpace key)` devem ser evitados, pois serão removidos em versões futuras. Use sempre a versão com `PreferencesCategory`.

### Exemplo de Registro de Preferência

Assumindo que a categoria já foi registrada:

```java
import net.joseplay.allianceutils.api.preferences.data.PreferencesManager;
import net.joseplay.allianceutils.api.preferences.interfaces.PreferencePermission;
import net.joseplay.allianceutils.api.preferences.interfaces.PreferencesCategory;
import meu.plugin.exemplo.MinhaCategoria;
import meu.plugin.exemplo.MinhaPreferencia;

public class MeuPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        PreferencesCategory minhaCategoria = new MinhaCategoria();
        PreferencesManager.addCategory(minhaCategoria);

        Preference minhaPreferencia = new MinhaPreferencia();
        PreferencePermission minhaPreferenciaComPermissão = new MinhaPreferenciaComPermissão();
        PreferencesManager.addPreference(minhaPreferencia, minhaCategoria);
        PreferencesManager.addPreference(minhaPreferenciaComPermissão, minhaCategoria);
    }
}
```

Para remover uma preferência, use `PreferencesManager.removePreference(PreferencesCategory category, Preference preference)`.

Para obter as preferências de uma categoria, use `PreferencesManager.getPreferences(PreferencesCategory category)`, que retorna uma lista de `Preference`.

Para obter todas as preferências organizadas por categoria, use `PreferencesManager.getPreferences()`, que retorna um mapa imutável de `PreferencesCategory` para listas de `Preference`.


## Quer abrir a gui de preferencias diretamente por comamndos?

```java
import net.joseplay.allianceutils.api.menu.SimpleMenu;
import net.joseplay.allianceutils.api.preferences.gui.PreferencesGUI;
import org.bukkit.entity.Player;

public void openMarriagePrefs(Player player) {
    SimpleMenu.RowsStyle style = SimpleMenu.RowsStyle.BOOK;
    new PreferencesGUI(
            Marriage.instance.category,
            style.getRows(),
            "§aPreferencias " + Marriage.instance.category.getName(),
            style.getSlots(),
            style.getNextPage(),
            style.getPreviusPage())
            .open(player);
}
```
A classe PreferencesGUI pede `PreferencesGUI(category, rows, title, contentSlots, nextSlot, previousSlot)`
passando todos os metodo você abre qualquer GUI de qualquer categoria registrada
no `Map<String, PreferencesCategory> categorys = new ConcurrentHashMap<>()`.

## Considerações Adicionais

- **Segurança de Threads**: O `PreferencesManager` usa `ConcurrentHashMap` para garantir operações seguras em ambientes multithread.
- **Categoria Padrão (UnCategory)**: Existe uma categoria interna não categorizada (`UnCategory`), mas seu uso é depreciado. Prefira categorias personalizadas.
- **Gerenciamento de Estados**: No método `get(UUID uuid)` da preferência, implemente lógica para persistir estados por jogador (ex: usando um banco de dados ou mapa interno).
