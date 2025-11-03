package kiosk.database;

import kiosk.database.dao.InventoryDAO;
import kiosk.database.dao.SalesDAO;

/**
 * Database initializer to create tables and setup the database
 */
public class DatabaseInitializer {
    
    public static void initialize() {
        System.out.println("Initializing database...");
        
        // Test database connection
        DatabaseConnection dbConn = DatabaseConnection.getInstance();
        if (dbConn.getConnection() == null) {
            System.err.println("Failed to establish database connection!");
            return;
        }
        
        // Create tables
        InventoryDAO inventoryDAO = new InventoryDAO();
        inventoryDAO.createTable();
        
        SalesDAO salesDAO = new SalesDAO();
        salesDAO.createTables();
        
        System.out.println("Database initialization completed!");
    }
    
    public static void main(String[] args) {
        initialize();
    }
}


