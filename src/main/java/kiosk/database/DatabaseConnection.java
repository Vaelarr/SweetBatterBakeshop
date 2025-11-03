package kiosk.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection manager using Singleton pattern with SQLite fallback
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final int RETRY_DELAY_MS = 1000;
    
    private DatabaseConnection() {
        establishConnection();
    }
    
    /**
     * Establishes database connection, attempting MySQL first, then falling back to SQLite
     */
    private void establishConnection() {
        // Try MySQL first
        if (tryMySqlConnection()) {
            DatabaseConfig.setDatabaseType(DatabaseConfig.DatabaseType.MYSQL);
            System.out.println("✓ MySQL database connection established successfully!");
            return;
        }
        
        // Fall back to SQLite
        System.out.println("MySQL connection failed. Falling back to SQLite...");
        if (trySqliteConnection()) {
            DatabaseConfig.setDatabaseType(DatabaseConfig.DatabaseType.SQLITE);
            System.out.println("✓ SQLite database connection established successfully!");
            System.out.println("  Database file: bakery_kiosk.db");
        } else {
            System.err.println("✗ Failed to establish any database connection!");
        }
    }
    
    /**
     * Attempts to connect to MySQL database
     */
    private boolean tryMySqlConnection() {
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url = DatabaseConfig.getUrl();
                String username = DatabaseConfig.getUsername();
                String password = DatabaseConfig.getPassword();
                
                this.connection = DriverManager.getConnection(url, username, password);
                return true;
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
                return false; // No point in retrying if driver is missing
            } catch (SQLException e) {
                if (attempt < MAX_RETRY_ATTEMPTS) {
                    System.err.println("MySQL connection attempt " + attempt + " failed. Retrying...");
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    System.err.println("MySQL connection failed after " + MAX_RETRY_ATTEMPTS + " attempts: " + e.getMessage());
                }
            }
        }
        return false;
    }
    
    /**
     * Attempts to connect to SQLite database
     */
    private boolean trySqliteConnection() {
        try {
            Class.forName(DatabaseConfig.getSqliteDriver());
            this.connection = DriverManager.getConnection(DatabaseConfig.getSqliteUrl());
            return true;
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("Failed to connect to SQLite: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static DatabaseConnection getInstance() {
        if (instance == null || instance.connection == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null || instance.connection == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }
    
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                establishConnection();
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection status: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }
    
    /**
     * Returns the current database type
     */
    public DatabaseConfig.DatabaseType getDatabaseType() {
        return DatabaseConfig.getCurrentDatabaseType();
    }
    
    /**
     * Checks if currently using SQLite
     */
    public boolean isSqlite() {
        return DatabaseConfig.isSqlite();
    }
    
    /**
     * Checks if currently using MySQL
     */
    public boolean isMysql() {
        return DatabaseConfig.isMysql();
    }
    
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}


