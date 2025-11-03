package kiosk.database;

import kiosk.database.dao.InventoryDAO;
import kiosk.database.dao.SalesDAO;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database initializer to create tables and setup the database
 * Supports both MySQL and SQLite with full schema compatibility
 */
public class DatabaseInitializer {
    
    /**
     * Initialize database with full schema
     */
    public static void initialize() {
        System.out.println("Initializing database...");
        
        // Test database connection
        DatabaseConnection dbConn = DatabaseConnection.getInstance();
        if (dbConn.getConnection() == null) {
            System.err.println("Failed to establish database connection!");
            return;
        }
        
        // Check which database type is being used
        if (dbConn.isSqlite()) {
            System.out.println("Using SQLite database - loading full schema...");
            initializeSqliteSchema();
        } else {
            System.out.println("Using MySQL database - basic table creation...");
            // For MySQL, assume schema is already created via setup.sql
            // But we can still create basic tables if needed
            createBasicTables();
        }
        
        System.out.println("Database initialization completed!");
    }
    
    /**
     * Initialize SQLite database with full schema from SQL file
     */
    private static void initializeSqliteSchema() {
        String sqlFile = "database/sql/setup_sqlite.sql";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        
        try {
            // Read and execute SQL file
            String sql = readSqlFile(sqlFile);
            if (sql != null && !sql.trim().isEmpty()) {
                executeSqlScript(conn, sql);
                System.out.println("✓ SQLite schema loaded successfully from " + sqlFile);
            } else {
                System.out.println("⚠ SQL file not found or empty. Creating basic tables...");
                createBasicTables();
            }
        } catch (Exception e) {
            System.err.println("Error loading SQLite schema: " + e.getMessage());
            System.out.println("Falling back to basic table creation...");
            createBasicTables();
        }
    }
    
    /**
     * Read SQL file content
     */
    private static String readSqlFile(String filePath) {
        StringBuilder sql = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip comments and empty lines
                line = line.trim();
                if (!line.startsWith("--") && !line.isEmpty()) {
                    sql.append(line).append(" ");
                }
            }
            return sql.toString();
        } catch (IOException e) {
            System.err.println("Could not read SQL file: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Execute SQL script (split by semicolon for SQLite)
     */
    private static void executeSqlScript(Connection conn, String sql) throws SQLException {
        // Split SQL into individual statements
        String[] statements = sql.split(";");
        
        try (Statement stmt = conn.createStatement()) {
            int executedCount = 0;
            for (String statement : statements) {
                statement = statement.trim();
                if (!statement.isEmpty() && !statement.startsWith("--")) {
                    try {
                        stmt.execute(statement);
                        executedCount++;
                    } catch (SQLException e) {
                        // Continue on errors for idempotent statements
                        if (!e.getMessage().contains("already exists") && 
                            !e.getMessage().contains("duplicate")) {
                            System.err.println("Warning executing SQL: " + e.getMessage());
                        }
                    }
                }
            }
            System.out.println("✓ Executed " + executedCount + " SQL statements");
        }
    }
    
    /**
     * Create basic tables using DAO classes (fallback method)
     */
    private static void createBasicTables() {
        InventoryDAO inventoryDAO = new InventoryDAO();
        inventoryDAO.createTable();
        
        SalesDAO salesDAO = new SalesDAO();
        salesDAO.createTables();
        
        System.out.println("✓ Basic tables created");
    }
    
    public static void main(String[] args) {
        initialize();
    }
}


