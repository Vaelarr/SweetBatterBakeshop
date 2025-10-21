package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseConnector class manages MySQL database connections for XAMPP.
 * Provides connection management and database initialization.
 */
public class DatabaseConnector {
    // Default XAMPP configuration
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DATABASE = "sweetbatterbakeshop";
    private static final String DEFAULT_USERNAME = "root";
    private static final String DEFAULT_PASSWORD = "";
    
    private final String host;
    private final String port;
    private final String database;
    private final String username;
    private final String password;
    
    /**
     * Constructor using default XAMPP configuration.
     */
    public DatabaseConnector() {
        this(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_DATABASE, DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }
    
    /**
     * Constructor with custom database configuration.
     * 
     * @param host Database host
     * @param port Database port
     * @param database Database name
     * @param username Database username
     * @param password Database password
     */
    public DatabaseConnector(String host, String port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }
    
    /**
     * Gets a connection to the database.
     * 
     * @return A Connection object
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + 
                        "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            
            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Please add mysql-connector-java to classpath.", e);
        }
    }
    
    /**
     * Tests the database connection.
     * 
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Initializes the database by creating the sweetbatterbakeshop database
     * and reviews table if they don't exist.
     * 
     * @throws SQLException if database initialization fails
     */
    public void initializeDatabase() throws SQLException {
        // First, connect without specifying database to create it if needed
        String urlWithoutDb = "jdbc:mysql://" + host + ":" + port + 
                              "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
        
        try (Connection conn = DriverManager.getConnection(urlWithoutDb, username, password);
             Statement stmt = conn.createStatement()) {
            
            // Create database if not exists
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + database);
            System.out.println("Database '" + database + "' ready.");
        }
        
        // Now connect to the database and create table
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create reviews table if not exists
            String createTableSQL = "CREATE TABLE IF NOT EXISTS reviews (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "restaurant VARCHAR(255) NOT NULL, " +
                    "reviewer VARCHAR(255) NOT NULL, " +
                    "rating INT NOT NULL, " +
                    "review TEXT NOT NULL" +
                    ")";
            
            stmt.executeUpdate(createTableSQL);
            System.out.println("Table 'reviews' ready.");
        }
    }
    
    /**
     * Gets the database name.
     * 
     * @return The database name
     */
    public String getDatabase() {
        return database;
    }
    
    /**
     * Gets the connection URL.
     * 
     * @return The JDBC connection URL
     */
    public String getConnectionUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/" + database;
    }
}
