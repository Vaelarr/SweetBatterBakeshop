package kiosk.database;

/**
 * SQL Dialect helper to translate MySQL-specific syntax to SQLite when needed
 */
public class SqlDialect {
    
    /**
     * Converts MySQL AUTO_INCREMENT to SQLite AUTOINCREMENT
     */
    public static String autoIncrement() {
        return DatabaseConfig.isSqlite() ? "AUTOINCREMENT" : "AUTO_INCREMENT";
    }
    
    /**
     * Returns the appropriate primary key definition
     */
    public static String primaryKeyAutoIncrement(String columnName) {
        if (DatabaseConfig.isSqlite()) {
            return columnName + " INTEGER PRIMARY KEY AUTOINCREMENT";
        } else {
            return columnName + " INT AUTO_INCREMENT PRIMARY KEY";
        }
    }
    
    /**
     * Returns the appropriate timestamp type
     */
    public static String timestamp() {
        return DatabaseConfig.isSqlite() ? "DATETIME" : "TIMESTAMP";
    }
    
    /**
     * Returns the appropriate default timestamp value
     */
    public static String currentTimestamp() {
        return DatabaseConfig.isSqlite() ? "CURRENT_TIMESTAMP" : "CURRENT_TIMESTAMP";
    }
    
    /**
     * Returns the appropriate decimal type
     */
    public static String decimal(int precision, int scale) {
        return "DECIMAL(" + precision + ", " + scale + ")";
    }
    
    /**
     * Returns the appropriate text type
     */
    public static String text() {
        return "TEXT";
    }
    
    /**
     * Returns the appropriate varchar type
     */
    public static String varchar(int length) {
        return "VARCHAR(" + length + ")";
    }
    
    /**
     * Returns the appropriate date type
     */
    public static String date() {
        return "DATE";
    }
    
    /**
     * Returns the appropriate integer type
     */
    public static String integer() {
        return DatabaseConfig.isSqlite() ? "INTEGER" : "INT";
    }
    
    /**
     * Handles ON UPDATE CURRENT_TIMESTAMP (MySQL only)
     * SQLite doesn't support this, so we return empty string for SQLite
     */
    public static String onUpdateCurrentTimestamp() {
        return DatabaseConfig.isSqlite() ? "" : "ON UPDATE CURRENT_TIMESTAMP";
    }
    
    /**
     * Creates a complete table creation SQL with dialect-specific syntax
     */
    public static String createTableIfNotExists(String tableName, String columns) {
        return "CREATE TABLE IF NOT EXISTS " + tableName + " (" + columns + ")";
    }
    
    /**
     * Handles the updated_at column definition
     */
    public static String updatedAtColumn() {
        if (DatabaseConfig.isSqlite()) {
            return "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP";
        } else {
            return "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP";
        }
    }
    
    /**
     * Handles the created_at column definition
     */
    public static String createdAtColumn() {
        return "created_at " + timestamp() + " DEFAULT CURRENT_TIMESTAMP";
    }
    
    /**
     * Returns IF NOT EXISTS clause for indexes (if supported)
     */
    public static String createIndexIfNotExists() {
        return "CREATE INDEX IF NOT EXISTS";
    }
    
    /**
     * Handles ENUM types (MySQL) vs CHECK constraints (SQLite)
     */
    public static String enumType(String columnName, String... values) {
        if (DatabaseConfig.isSqlite()) {
            StringBuilder sb = new StringBuilder(columnName + " TEXT CHECK(" + columnName + " IN (");
            for (int i = 0; i < values.length; i++) {
                sb.append("'").append(values[i]).append("'");
                if (i < values.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("))");
            return sb.toString();
        } else {
            StringBuilder sb = new StringBuilder(columnName + " ENUM(");
            for (int i = 0; i < values.length; i++) {
                sb.append("'").append(values[i]).append("'");
                if (i < values.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append(")");
            return sb.toString();
        }
    }
}
