# CountDown API – Documentação

Este documento explica como criar, manipular e consultar CountDowns no sistema `dayCountDownData` da **AllianceUtils**.

---

## 🚀 Criar um CountDown

```java
Allianceutils.getInstance().dayCountDownData.createCountDown(
    "00/00/0000 00:00:00",  // Data inicial
    "01/00/0000 00:00:00",  // Data final
    "example",              // ID do CountDown
    BukkitPlayer            // Dono/opcional
);
```

---

## 🔍 Obter um CountDown já criado

```java
CountDown countDown = Allianceutils.getInstance().dayCountDownData.getCountDown("example");
```

---

## 📅 Verificações

### ✔ Verificar se a data final já passou
```java
if (countDown.hasPassed()) {
    // lógica quando o CountDown termina
}
```

### 🔁 Verificar se o CountDown está em loop
```java
if (countDown.isLoop()) {
    // comportamento de loop
}
```

---

## ⏩ Manipular datas

### ➕ Avançar automaticamente para o próximo ciclo (apenas se loop = true)
```java
countDown.advanceLoop();
```

### ➕ Adicionar dias à data final
```java
countDown.addDays(1);
```

### ➖ Remover dias da data final
```java
countDown.addDays(-1);
```

---

## ⏱️ Informações do CountDown

### ⏳ Tempo restante
```java
countDown.getRemainingTime();
```

### 📆 Quantidade total de dias entre início e fim
```java
countDown.getDaysBetween();
```

---

## 🛠️ Atualizar datas

### Atualizar a data inicial
```java
countDown.setFirstDate("00/00/0000 00:00:00");
```

### Atualizar a data final
```java
countDown.setLastDate("00/00/0000 00:00:00");
```

### Ativar ou desativar loop
```java
countDown.setLoop(true);
```

---

## 🗑️ Remover CountDown

```java
Allianceutils.getInstance().dayCountDownData.deleteCountDown("example");
```

---

## 💾 Salvar e carregar (automático)

Esses métodos existem, mas NÃO precisam ser chamados manualmente:

```java
Allianceutils.getInstance().dayCountDownData.saveAll();
Allianceutils.getInstance().dayCountDownData.loadAll();
```

---

## 📋 Ver se foi criado

- Verifique no cache interno
- Ou use o comando:

```
/dcd list
```

