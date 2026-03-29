package net.joseplay.allianceutils.api.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.joseplay.allianceutils.Utils.messages.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Handles MySQL connection pooling using HikariCP.
 * Responsible for initializing the datasource and managing connections.
 */
public class DataBaseManager {

    private final String HOST;
    private final String PORT;
    private final String DATABASE;
    private final String USER;
    private final String PASSWORD;

    private HikariDataSource dataSource;

    /**
     * Initializes database manager and starts connection pool.
     *
     * @param host     Database host
     * @param port     Database port
     * @param database Database name
     * @param user     Database user
     * @param password Database password
     */
    public DataBaseManager(String host, String port, String database, String user, String password) {
        this.HOST = host;
        this.PORT = port;
        this.DATABASE = database;
        this.USER = user;
        this.PASSWORD = password;

        setupDataSource();
    }

    /**
     * Configures and initializes HikariCP datasource.
     */
    private void setupDataSource() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?useSSL=false&allowPublicKeyRetrieval=true");
        config.setUsername(USER);
        config.setPassword(PASSWORD);

        // Connection pool settings
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);

        // Timeout configurations
        config.setConnectionTimeout(10000); // 10 seconds
        config.setIdleTimeout(60000);       // 1 minute
        config.setMaxLifetime(600000);      // 10 minutes

        config.setPoolName("AllianceUtils-Pool");

        dataSource = new HikariDataSource(config);

        Logger.info("[MySQL] HikariCP connection pool initialized.");
    }

    /**
     * Retrieves a connection from the pool.
     *
     * @return Active SQL connection
     * @throws SQLException if connection cannot be obtained
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Closes the connection pool and releases resources.
     */
    public void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            Logger.info("[MySQL] Connection pool closed.");
        }
    }
}