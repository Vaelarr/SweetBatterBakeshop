package main.java.kiosk.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Database configuration manager
 */
public class DatabaseConfig {
    private static final String CONFIG_FILE = "config/database.properties";
    private static Properties properties;
    
    static {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
        } catch (IOException e) {
            // Use default values if config file not found
            setDefaultProperties();
        }
    }
    
    private static void setDefaultProperties() {
        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/kiosk_db");
        properties.setProperty("db.username", "root");
        properties.setProperty("db.password", "");
        properties.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
    }
    
    public static String getUrl() {
        return properties.getProperty("db.url");
    }
    
    public static String getUsername() {
        return properties.getProperty("db.username");
    }
    
    public static String getPassword() {
        return properties.getProperty("db.password");
    }
    
    public static String getDriver() {
        return properties.getProperty("db.driver");
    }
}
