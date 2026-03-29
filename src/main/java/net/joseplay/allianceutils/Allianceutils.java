package net.joseplay.allianceutils;


import com.google.gson.Gson;
import io.lettuce.core.*;
import net.joseplay.allianceutils.BootPlugin.*;
import net.joseplay.allianceutils.api.claims.ClaimApi;
import net.joseplay.allianceutils.api.combat.CombatVerifier;
import net.joseplay.allianceutils.api.database.DataBaseManager;
import net.joseplay.allianceutils.api.database.DatabaseExecutor;
import net.joseplay.allianceutils.api.discord.webhook.DiscordWebhookSender;
import net.joseplay.allianceutils.api.extensions.Alliance;
import net.joseplay.allianceutils.api.extensions.ExtensionCommandUtils;
import net.joseplay.allianceutils.api.extensions.ExtensionLoader;
import net.joseplay.allianceutils.api.extensions.Listener;
import net.joseplay.allianceutils.api.internalListener.EventManager;
import net.joseplay.allianceutils.api.internalListener.events.PluginBootEvent;
import net.joseplay.allianceutils.api.internalListener.events.PluginShutdownEvent;
import net.joseplay.allianceutils.api.playerProfile.data.PlayerProfileManager;
import net.joseplay.allianceutils.api.playerProfile.data.ServerProfileManager;
import net.joseplay.allianceutils.api.pluginComunicate.PluginChannelDispatcher;
import net.joseplay.allianceutils.api.timecommands.TimerCommands;
import net.joseplay.allianceutils.api.updateSystem.UpdateManager;
import net.joseplay.allianceutils.Utils.GradientMessage;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.ServerLinks;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.floodgate.api.FloodgateApi;

import java.io.File;
import java.net.URI;


public final class Allianceutils extends JavaPlugin {
    private static Economy econ = null;
    DevMode devMode = new DevMode(this);
    String devat;
    private static DataBaseManager dataBaseManager;
    private static JavaPlugin plugin;
    private static Allianceutils instance;
    public PluginChannelDispatcher dispatcher;
    public ExtensionLoader extensionLoader;
    private PlayerProfileManager playerProfileManager;
    private ServerProfileManager serverProfileManager;
    public TimerCommands timerCommands;
    public static File pluginFile;
    public static final Gson GSON = new Gson();
    public FloodgateApi floodgateApi;
    public ClaimApi claimApi;
    public CombatVerifier combatVerifier;
    public String serverName = "server";
    public DiscordWebhookSender discordWebhookSender;

    public Allianceutils() {
        this.devat = this.devMode.DevActive(this);
    }

    /**@Override
    public void onLoad() {
        FlagManager.registerElytraFlags();
    }*/

    public void onEnable() {
        plugin = this;
        instance = this;
        pluginFile = this.getFile();
        //UpdateCheck

//        UpdateManager.checkUpdate();
//        Bukkit.getScheduler().runTaskTimer(this, UpdateManager::checkUpdateAsync, 30 * 60 * 20, 30 * 60 * 20);

        dispatcher = new PluginChannelDispatcher(registerRedis(), this, getConfig().getBoolean("redis.enabled"));

        BStats.registerBStats(this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        if (!this.setupEconomy()) {
            Bukkit.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", this.getDescription().getName()));
            this.getServer().getPluginManager().disablePlugin(this);
        } else {
            this.saveDefaultConfig();
            serverName = getConfig().getString("servername", serverName);

            this.getLogger().info(this.devat);
            claimApi = new ClaimApi();
            combatVerifier = new CombatVerifier();
            dataBaseManager = connectDataBase();
            serverProfileManager = new ServerProfileManager();
            playerProfileManager = new PlayerProfileManager();
            setServerLinks();

            if (Bukkit.getPluginManager().getPlugin("FloodGate") != null){
                floodgateApi = FloodgateApi.getInstance();
            }


            RegisterPreferences.registerCategories();
            RegisterCommands.registreCommands(this);
            RegisterEvents.registreEvents(this);
            ConsoleCommandSender var10000 = Bukkit.getConsoleSender();
            ChatColor var10001 = ChatColor.GREEN;
            var10000.sendMessage("§aInitialized plugin with §e" + HandlerList.getRegisteredListeners(this).size() + " §a events registered.");
            this.getLogger().info("\u001b[36mPlugin initialized! build version: " + UpdateManager.PROJECT_VERSION + " - \u001b[37mALLIANCE\u001b[33mUTILS\u001b[0m");
            EventManager.callEvent(new PluginBootEvent(this));

            //Carregar as extensões se estiver alguma
            this.getLogger().info("\u001b[36m loading extensions - \u001b[37mALLIANCE\u001b[33mUTILS\u001b[0m");

            //Extensions
            if (getConfig().getBoolean("useextensions", true)) {
                this.extensionLoader = new ExtensionLoader(this);
                Listener listener = new Listener();
                Bukkit.getPluginManager().registerEvents(listener, this);
                EventManager.registerListener(listener, this);

                ExtensionCommandUtils extensionCommand = new ExtensionCommandUtils();
                plugin.getCommand("extensions").setExecutor(extensionCommand);
                plugin.getCommand("extensions").setTabCompleter(extensionCommand);

                extensionLoader.loadExtensions();
            }
            //-------------------------------------------------------------

            //Inicia o timerCommands
            timerCommands = new TimerCommands();

            //Inicia o antiEntityCrash
        }
    }

    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        } else {
            RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                return false;
            } else {
                econ = rsp.getProvider();
                return econ != null;
            }
        }
    }

    public static Economy getEconomy() {
        return econ;
    }

    public void setServerLinks() {
        ServerLinks links = Bukkit.getServerLinks();


        for (ServerLinks.ServerLink link : links.getLinks()) {
            links.removeLink(link);
        }

        if (getConfig().getConfigurationSection("serverlinks") == null) {
            Bukkit.getConsoleSender().sendMessage("§cnot found server links.");
            return;
        }

        for (String linkKey : getConfig().getConfigurationSection("serverlinks").getKeys(false)) {
            String name = GradientMessage.createGradientMessageAsString(getConfig().getString("serverlinks." + linkKey + ".name"));
            String stringUrl = getConfig().getString("serverlinks." + linkKey + ".url");
            String type = getConfig().getString("serverlinks." + linkKey + ".type");

            URI url = null;

            try {
                url = new URI(stringUrl);
            } catch (Exception e) {
            }

            if (url == null) return;

            links.addLink(name, url);
            if (type != null) {
                links.addLink(ServerLinks.Type.valueOf(type), url);
            }

            Bukkit.getConsoleSender().sendMessage("§a Registering Url: " + name + ": " + stringUrl);
        }
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static File getPluginFile() {
        return pluginFile;
    }

    public static Allianceutils getInstance() {
        return instance;
    }

    public PluginChannelDispatcher getDispatcher() {
        return dispatcher;
    }

    public static DataBaseManager getDataBaseManager() {
        return dataBaseManager;
    }

    public String getServerName() {
        return serverName;
    }

    public ServerProfileManager getServerProfileManager() {
        return serverProfileManager;
    }

    public PlayerProfileManager getPlayerProfileManager() {
        return playerProfileManager;
    }

    public ClaimApi getClaimApi() {
        return claimApi;
    }

    public CombatVerifier getCombatVerify() {
        return combatVerifier;
    }

    public DiscordWebhookSender getDiscordWebhookSender() {
        return discordWebhookSender;
    }

    public FloodgateApi getFloodgateApi() {
        return floodgateApi;
    }

    public RedisClient registerRedis() {

        ConfigurationSection redisSection = getConfig().getConfigurationSection("redis");
        if (redisSection == null || !redisSection.getBoolean("enabled")) {
            return null;
        }

        System.out.println("Connecting to Redis (Lettuce)");

        String address = redisSection.getString("address", "127.0.0.1");
        int port = redisSection.getInt("port", 6379);
        String username = redisSection.getString("username");
        String password = redisSection.getString("password");

        RedisURI.Builder uriBuilder = RedisURI.builder()
                .withHost(address)
                .withPort(port);

        if (password != null && !password.isEmpty()) {
            if (username != null && !username.isEmpty()) {
                uriBuilder.withAuthentication(username, password);
            } else {
                uriBuilder.withPassword(password.toCharArray());
            }
        }

        RedisURI uri = uriBuilder.build();

        RedisClient redisClient = RedisClient.create(uri);

        redisClient.setOptions(
                ClientOptions.builder()
                        .autoReconnect(true)
                        .socketOptions(
                                SocketOptions.builder()
                                        .keepAlive(true)
                                        .build()
                        )
                        .timeoutOptions(TimeoutOptions.enabled())
                        .build()
        );

        return redisClient;
    }

    public DataBaseManager connectDataBase(){
        if (!plugin.getConfig().contains("mysql")) return null;
        if (!plugin.getConfig().getBoolean("mysql.enabled")) return null;

        String host = plugin.getConfig().getString("mysql.host");
        String port = plugin.getConfig().getString("mysql.port");
        String database = plugin.getConfig().getString("mysql.database");
        String user = plugin.getConfig().getString("mysql.user");
        String password = plugin.getConfig().getString("mysql.password");

        if (host == null || port == null || database == null || user == null || password == null) return null;

        return new DataBaseManager(host, port, database, user, password);
    }

    public void onDisable() {
        this.getLogger().info("Initialized plugin shutdown - \u001b[37mALLIANCE\u001b[33mUTILS\u001b[0m");
        try {
            if (serverProfileManager != null){
                serverProfileManager.shutdown();
            }

            if (playerProfileManager != null){
                playerProfileManager.shutdown();
            }

            if (extensionLoader != null) {
                extensionLoader.disableExtensions();
            }

            if (timerCommands != null){
                timerCommands.stop();
                timerCommands = null;
            }

            EventManager.callEvent(new PluginShutdownEvent(this));
            EventManager.unregisterAll();

            DatabaseExecutor.shutdownAndAwait();

            if (dataBaseManager != null){
                dataBaseManager.disconnect();
            }

            if (dispatcher != null) {
                dispatcher.shutdown();
            }

            Alliance.getAllianceListenerManager().unregisterAllListeners(this);
            Alliance.getAllianceCommandManager().commands.clear();
            HandlerList.unregisterAll(this);
            getServer().getMessenger().unregisterOutgoingPluginChannel(this, "BungeeCord");
            UpdateManager.applyUpdateOnDisable();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.getLogger().info("\u001b[24mPlugin shutdown complete! - \u001b[37mALLIANCE\u001b[33mUTILS\u001b[0m");
    }
}