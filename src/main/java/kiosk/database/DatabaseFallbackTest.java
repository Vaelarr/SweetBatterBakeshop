package kiosk.database;

import kiosk.database.dao.InventoryDAO;
import kiosk.database.dao.SalesDAO;
import kiosk.model.InventoryItem;
import java.time.LocalDate;

/**
 * Test program to verify database fallback functionality
 */
public class DatabaseFallbackTest {
    
    public static void main(String[] args) {
        System.out.println("=== Database Fallback Test ===\n");
        
        // Test database connection
        System.out.println("1. Testing database connection...");
        DatabaseConnection dbConn = DatabaseConnection.getInstance();
        
        if (dbConn.getConnection() == null) {
            System.err.println("✗ Failed to establish database connection!");
            return;
        }
        
        // Display which database is being used
        DatabaseConfig.DatabaseType dbType = dbConn.getDatabaseType();
        System.out.println("✓ Connected to: " + dbType);
        System.out.println("  Is MySQL: " + dbConn.isMysql());
        System.out.println("  Is SQLite: " + dbConn.isSqlite());
        System.out.println();
        
        // Test table creation
        System.out.println("2. Creating database tables...");
        InventoryDAO inventoryDAO = new InventoryDAO();
        SalesDAO salesDAO = new SalesDAO();
        
        inventoryDAO.createTable();
        salesDAO.createTables();
        System.out.println();
        
        // Test data insertion
        System.out.println("3. Testing data insertion...");
        InventoryItem testItem = new InventoryItem(
            "Test Croissant",
            "Pastries",
            3.50,
            10,
            LocalDate.now().plusDays(3),
            "TEST001",
            "Test Supplier"
        );
        
        boolean inserted = inventoryDAO.insert(testItem);
        if (inserted) {
            System.out.println("✓ Test item inserted successfully");
        } else {
            System.out.println("✗ Failed to insert test item");
        }
        System.out.println();
        
        // Test data retrieval
        System.out.println("4. Testing data retrieval...");
        InventoryItem retrieved = inventoryDAO.getByName("Test Croissant");
        if (retrieved != null) {
            System.out.println("✓ Test item retrieved successfully");
            System.out.println("  Name: " + retrieved.getName());
            System.out.println("  Category: " + retrieved.getCategory());
            System.out.println("  Price: $" + retrieved.getPrice());
            System.out.println("  Stock: " + retrieved.getStockQuantity());
        } else {
            System.out.println("✗ Failed to retrieve test item");
        }
        System.out.println();
        
        // Test data update
        System.out.println("5. Testing data update...");
        if (retrieved != null) {
            retrieved.setStockQuantity(15);
            retrieved.setPrice(3.75);
            boolean updated = inventoryDAO.update(retrieved);
            if (updated) {
                System.out.println("✓ Test item updated successfully");
            } else {
                System.out.println("✗ Failed to update test item");
            }
        }
        System.out.println();
        
        // Test data deletion
        System.out.println("6. Testing data deletion...");
        boolean deleted = inventoryDAO.delete("Test Croissant");
        if (deleted) {
            System.out.println("✓ Test item deleted successfully");
        } else {
            System.out.println("✗ Failed to delete test item");
        }
        System.out.println();
        
        // Display all inventory items
        System.out.println("7. Displaying all inventory items...");
        var allItems = inventoryDAO.getAll();
        System.out.println("Total items in inventory: " + allItems.size());
        for (InventoryItem item : allItems) {
            System.out.println("  - " + item.getName() + " ($" + item.getPrice() + ")");
        }
        System.out.println();
        
        System.out.println("=== Test Complete ===");
        System.out.println("Database type used: " + dbType);
        
        // Close connection
        dbConn.closeConnection();
    }
}
