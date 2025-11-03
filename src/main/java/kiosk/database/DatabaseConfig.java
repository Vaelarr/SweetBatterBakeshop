package kiosk.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Database configuration manager with SQLite fallback support
 */
public class DatabaseConfig {
    private static final String CONFIG_FILE = "config/database.properties";
    private static Properties properties;
    
    // SQLite fallback configuration
    private static final String SQLITE_URL = "jdbc:sqlite:bakery_kiosk.db";
    private static final String SQLITE_DRIVER = "org.sqlite.JDBC";
    
    private static DatabaseType currentDatabaseType = DatabaseType.MYSQL;
    
    public enum DatabaseType {
        MYSQL,
        SQLITE
    }
    
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
        properties.setProperty("db.sqlite.url", SQLITE_URL);
        properties.setProperty("db.sqlite.driver", SQLITE_DRIVER);
    }
    
    public static String getUrl() {
        if (currentDatabaseType == DatabaseType.SQLITE) {
            return getSqliteUrl();
        }
        return properties.getProperty("db.url");
    }
    
    public static String getUsername() {
        if (currentDatabaseType == DatabaseType.SQLITE) {
            return ""; // SQLite doesn't use username
        }
        return properties.getProperty("db.username");
    }
    
    public static String getPassword() {
        if (currentDatabaseType == DatabaseType.SQLITE) {
            return ""; // SQLite doesn't use password
        }
        return properties.getProperty("db.password");
    }
    
    public static String getDriver() {
        if (currentDatabaseType == DatabaseType.SQLITE) {
            return getSqliteDriver();
        }
        return properties.getProperty("db.driver");
    }
    
    public static String getSqliteUrl() {
        return properties.getProperty("db.sqlite.url", SQLITE_URL);
    }
    
    public static String getSqliteDriver() {
        return properties.getProperty("db.sqlite.driver", SQLITE_DRIVER);
    }
    
    public static DatabaseType getCurrentDatabaseType() {
        return currentDatabaseType;
    }
    
    public static void setDatabaseType(DatabaseType type) {
        currentDatabaseType = type;
        System.out.println("Database type switched to: " + type);
    }
    
    public static boolean isSqlite() {
        return currentDatabaseType == DatabaseType.SQLITE;
    }
    
    public static boolean isMysql() {
        return currentDatabaseType == DatabaseType.MYSQL;
    }
}


