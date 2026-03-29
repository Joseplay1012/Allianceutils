package net.joseplay.allianceutils.api.webSocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketManager extends WebSocketClient {

    private final Map<String, CompletableFuture<JSONObject>> pendingRequests = new ConcurrentHashMap<>();

    public WebSocketManager(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Conectado ao servidor WebSocket");
    }

    @Override
    public void onMessage(String message) {
        try {
            JSONObject json = new JSONObject(message);
            String requestId = json.getString("requestId"); // Pega o ID da requisição

            CompletableFuture<JSONObject> future = pendingRequests.remove(requestId);
            if (future != null) {
                future.complete(json); // Completa a requisição pendente
            }
        } catch (Exception e) {
            System.out.println("Erro ao processar resposta: " + e.getMessage());
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Desconectado do servidor");
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public CompletableFuture<JSONObject> getInfo(String command) {
        String requestId = UUID.randomUUID().toString(); // Gera um ID único
        CompletableFuture<JSONObject> future = new CompletableFuture<>();
        pendingRequests.put(requestId, future); // Salva a requisição pendente

        JSONObject requestJson = new JSONObject();
        requestJson.put("requestId", requestId);
        requestJson.put("command", command);

        send(requestJson.toString()); // Envia a requisição para o servidor

        return future; // Retorna o Future para quem chamou
    }

    /**public static void main(String[] args) throws Exception {
        WebSocketManager client = new WebSocketManager(URI.create("ws://localhost:8080"));
        client.connectBlocking(); // Espera a conexão ser estabelecida

        // Envia múltiplas requisições
        client.getInfo("getstats player1").thenAccept(response -> {
            System.out.println("Resposta 1: " + response);
        });

        client.getInfo("getstats player2").thenAccept(response -> {
            System.out.println("Resposta 2: " + response);
        });

        client.getInfo("getstats player3").thenAccept(response -> {
            System.out.println("Resposta 3: " + response);
        });

        Thread.sleep(5000); // Espera um tempo antes de fechar a aplicação
    }*/
}
