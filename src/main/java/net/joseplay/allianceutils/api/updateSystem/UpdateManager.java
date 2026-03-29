package net.joseplay.allianceutils.api.updateSystem;

import net.joseplay.allianceutils.Allianceutils;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UpdateManager {
    private static final String API_URL = "unknow";
    public static int PROJECT_VERSION = 80900;
    public static String PROJECT_ID = "AllianceUtils";
    protected static String prefix = "[UpdateManager] ";
    public static List<Integer> checkVersions = new ArrayList<>();

    // --- Verificação de update ---
    public static void checkUpdate(){
        Allianceutils.getInstance().getLogger().warning(prefix + "Iniciando verificação de update...");
        try{

            String response = sendUpdateRequest();

            if (response == null){
                Allianceutils.getInstance().getLogger().warning(prefix + "Houve um erro ao tentar entrar em contato com o servidor de atualizações");
                return;
            }

            int currentVersion = getCurrentVersion(response);

            if (currentVersion < 0){
                return;
            }

            if (currentVersion < PROJECT_VERSION) {
                Allianceutils.getInstance().getLogger().info(prefix + "Versão do servidor (" + currentVersion + ") é mais antiga que a do plugin (" + PROJECT_VERSION + ").");
                return;
            }

            if (currentVersion == PROJECT_VERSION){
                Allianceutils.getInstance().getLogger().warning(prefix + "Plugin atualizado.");
                return;
            }

            if (currentVersion > PROJECT_VERSION){
                Allianceutils.getInstance().getLogger().warning(prefix + "Nova versão encontrada, baixando...");
                Allianceutils.getInstance().getLogger().warning(prefix + "Novidades: " + getNews(response));
                if (downloadJar(API_URL + "api/download")) {
                    Allianceutils.getInstance().getLogger().warning(prefix + "Atualização baixada! Ela será aplicada no próximo restart/desligamento.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void checkUpdateAsync() {
        Allianceutils.getInstance().getLogger().info(prefix + "Iniciando verificação de update...");

        sendUpdateRequestAsync(response -> {
            if (response == null) return;

            try {
                int currentVersion = getCurrentVersion(response);

                if (checkVersions.contains(currentVersion)) {
                    Allianceutils.getInstance().getLogger().info("Nova versão ja instalada, esperando reinicio para aplicar.");
                    return;
                }

                if (currentVersion < 0) return;

                if (currentVersion < PROJECT_VERSION) {
                    Allianceutils.getInstance().getLogger().info(prefix + "Versão do servidor de atualizações (" + currentVersion + ") é mais antiga que a do plugin (" + PROJECT_VERSION + ").");
                    return;
                }

                if (currentVersion == PROJECT_VERSION){
                    Allianceutils.getInstance().getLogger().info(prefix + "Plugin atualizado.");
                    return;
                }

                if (currentVersion > PROJECT_VERSION){
                    checkVersions.add(currentVersion);
                    Allianceutils.getInstance().getLogger().warning(prefix + "Nova versão encontrada, baixando...");
                    Allianceutils.getInstance().getLogger().warning(prefix + "Novidades: " + getNews(response));

                    // download continua síncrono, mas pode ser movido pra async se quiser
                    Bukkit.getScheduler().runTaskAsynchronously(Allianceutils.getInstance(), () -> {
                        if (downloadJar(API_URL + "api/download")) {
                            Allianceutils.getInstance().getLogger().warning(prefix + "Atualização baixada! Será aplicada no próximo restart/desligamento.");
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static int getCurrentVersion(String response) throws Exception {
        JSONObject object = new JSONObject(response);
        return object.optInt("version", -0);
    }

    private static String sendUpdateRequest() throws Exception {
        URL urlReq = new URL(API_URL + "api/check");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(urlReq.toURI())
                .setHeader("pluginId", PROJECT_ID)
                .timeout(Duration.ofSeconds(5))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            return null;
        }
        return response.body();
    }

    private static void sendUpdateRequestAsync(Consumer<String> callback) {
        try {
            URL urlReq = new URL(API_URL + "api/check");

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(urlReq.toURI())
                    .setHeader("pluginId", PROJECT_ID)
                    .timeout(Duration.ofSeconds(5))
                    .build();

            // versão async
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(callback)
                    .exceptionally(ex -> {
                        Allianceutils.getInstance().getLogger().warning(prefix + "Erro ao contatar servidor de updates: " + ex.getMessage());
                        return null;
                    });

        } catch (Exception e) {
            Bukkit.getPluginManager().disablePlugin(Allianceutils.getPlugin());
            e.printStackTrace();
        }
    }

    private static String getNews(String json){
        JSONObject jsonObject = new JSONObject(json);
        return jsonObject.optString("news", "Nenhuma novidade encontrada.");
    }

    // --- Download sempre para _update.jar ---
    private static boolean downloadJar(String jarUrl) {
        try {
            URL url = new URL(jarUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("pluginId", PROJECT_ID);

            int contentLength = con.getContentLength();
            if (contentLength <= 0) {
                Allianceutils.getInstance().getLogger().warning(prefix + "Tamanho do arquivo não informado pelo servidor.");
            }

            File pluginsFolder = Allianceutils.getInstance().getDataFolder().getParentFile();
            String pluginName = Allianceutils.getInstance().getDescription().getName();
            File updateFile = new File(pluginsFolder, pluginName + "_update.jar");

            try (InputStream in = con.getInputStream();
                 FileOutputStream out = new FileOutputStream(updateFile)) {

                byte[] buffer = new byte[8192];
                long totalRead = 0;
                int read;
                long lastLoggedTime = System.currentTimeMillis();

                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                    totalRead += read;

                    if (contentLength > 0) {
                        int percent = (int) ((totalRead * 100) / contentLength);
                        long now = System.currentTimeMillis();
                        if (now - lastLoggedTime >= 500) {
                            Allianceutils.getInstance().getLogger().info(prefix + "Baixando atualização: " + percent + "%");
                            lastLoggedTime = now;
                        }
                    }
                }
            }
            Allianceutils.getInstance().getLogger().info(prefix + "Download concluído!");
            return true;
        } catch (IOException e) {
            Allianceutils.getInstance().getLogger().warning(prefix + "Erro ao baixar: " + e.getMessage());
            return false;
        }
    }

    // --- Aplicar atualização no disable ---
    public static void applyUpdateOnDisable() {
        File pluginsFolder = Allianceutils.getInstance().getDataFolder().getParentFile();
        String pluginName = Allianceutils.getInstance().getDescription().getName();

        File updateFile = new File(pluginsFolder, pluginName + "_update.jar");
        File mainFile = new File(pluginsFolder, pluginName + ".jar");

        if (updateFile.exists()) {
            Allianceutils.getInstance().getLogger().info(prefix + "Atualização encontrada, aplicando...");

            if (mainFile.exists() && !mainFile.delete()) {
                Allianceutils.getInstance().getLogger().warning(prefix + "Não foi possível deletar o arquivo antigo (" + mainFile.getName() + ").");
                return;
            }

            if (updateFile.renameTo(mainFile)) {
                Allianceutils.getInstance().getLogger().info(prefix + "Atualização aplicada com sucesso! Reinicie o servidor para carregar a nova versão.");
            } else {
                Allianceutils.getInstance().getLogger().warning(prefix + "Falha ao mover o arquivo de atualização. Faça manualmente.");
            }
        }
    }
}