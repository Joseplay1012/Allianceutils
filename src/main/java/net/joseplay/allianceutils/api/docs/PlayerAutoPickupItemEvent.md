quando quiser usar para enviar um item para o inventario diretamente para o jogador use ele, o item é adicionado para o player mesmo se ele nao estiver com o autopickup ativo ou sem perm.

uso:


```java
PlayerAutoPickupItemEvent event = new PlayerAutoPickupItemEvent(player, List<ItemStack>, dropLocation);

EventManager.callEvent(event);


//caso queira intercepitar o evento para usar em algum sistema que precisa pegar o item que foi para o inv do player
@AuEventHandler
public void onPlayerAutoPickupItem(PlayerAutoPickupItemEvent event){
   Player player = event.getPlayer();
   List<ItemStack> items = event.getItems();
   
  //caso queira remover os items
  event.setItems(List.of() or null);
}

//para registrar o evento do allianceutils
EventManager.registerEvent(new YouListener());
```