# LocaleAPI

API utilitária para internacionalização (i18n) de nomes de **biomas**, **materiais**, **encantamentos** e **valores customizados**, baseada em arquivos de locale e cache em memória.

---

## Visão geral

A `LocaleAPI` fornece métodos estáticos para obter textos localizados a partir de uma `Locale`, utilizando um cache interno (`ConcurrentHashMap`) de `LocaleReader`.

Ela é pensada para uso em plugins Spigot/Paper modernos, evitando leituras repetidas de arquivos e padronizando chaves no formato do Minecraft (`minecraft:*`).

---

## Cache interno

```java
protected static Map<Locale, LocaleReader> CACHE = new ConcurrentHashMap<>();
```

- Um `LocaleReader` é criado **uma única vez por locale**.
- O cache é thread-safe.
- O carregamento é lazy (sob demanda).

---

## Valores customizados

### `getCustomValue`

```java
@Nullable
public static String getCustomValue(@NotNull String key, @NotNull Locale locale)
```

Obtém um valor diretamente pela chave informada.

**Exemplo:**
```java
String title = LocaleAPI.getCustomValue("menu.title", Locale.pt_br);
```

---

## Listagem de chaves

### `getAllKey`

```java
@NotNull
public static List<String> getAllKey(@NotNull Locale locale)
```

Retorna todas as chaves disponíveis para o locale informado.

- A lista retornada é **imutável**.
- Útil para debug, validações ou sistemas dinâmicos.

---

## Biomas

### `getBiome`

```java
@NotNull
public static String getBiome(@NotNull Locale locale, @NotNull Biome biome)
```

Resolve o nome localizado de um bioma.

- Usa a chave: `biome.minecraft.<nome>`
- Caso o bioma seja `custom`, retorna `"Custom"` diretamente.

**Exemplo:**
```java
String biomeName = LocaleAPI.getBiome(Locale.pt_br, player.getWorld().getBiome(loc));
```

---

## Encantamentos

### `getEnchantment`

```java
@NotNull
public static String getEnchantment(Enchantment enchantment)
```

Usa o locale padrão `pt_br`.

```java
@NotNull
public static String getEnchantment(Enchantment enchantment, Locale locale)
```

- Usa a chave: `enchantment.minecraft.<nome>`

**Exemplo:**
```java
String ench = LocaleAPI.getEnchantment(Enchantment.DAMAGE_ALL, Locale.en_us);
```

---

## Materiais

### `getMaterial`

```java
@NotNull
public static String getMaterial(@NotNull Material mat)
```

Usa o locale padrão `pt_br`.

```java
@NotNull
public static String getMaterial(@NotNull Locale locale, @NotNull Material mat)
```

### Regras aplicadas

- Remove automaticamente o prefixo `wall_` quando existir.
- Detecta se o material é bloco ou item:
  - Bloco → `block.minecraft.<nome>`
  - Item → `item.minecraft.<nome>`

**Exemplo:**
```java
String matName = LocaleAPI.getMaterial(Locale.pt_br, Material.DIAMOND_SWORD);
```

---

## LocaleReader

### `getLocaleReader`

```java
public static LocaleReader getLocaleReader(Locale locale)
```

- Retorna o `LocaleReader` do cache.
- Cria automaticamente caso não exista.

---

## Padrão de chaves esperado

```text
block.minecraft.stone
iitem.minecraft.diamond_sword
enchantment.minecraft.sharpness
biome.minecraft.plains
menu.title
```

---

## Boas práticas

- Evite chamar diretamente o `LocaleReader` fora da API.
- Centralize traduções customizadas em chaves próprias.
- Não misture lógica de formatação com a API de locale.

---

## Observações

- A API **não faz fallback automático** entre locales.
- Se a chave não existir, o retorno depende da implementação do `LocaleReader`.

---

## Exemplo completo

```java
Material mat = Material.OAK_PLANKS;
Biome biome = Biome.PLAINS;

String matName = LocaleAPI.getMaterial(Locale.pt_br, mat);
String biomeName = LocaleAPI.getBiome(Locale.pt_br, biome);
```

