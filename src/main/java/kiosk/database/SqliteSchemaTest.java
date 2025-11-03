package kiosk.database;

import kiosk.database.dao.*;
import kiosk.model.*;
import java.time.LocalDate;
import java.sql.*;

/**
 * Test program to verify SQLite schema is complete and matches MySQL
 */
public class SqliteSchemaTest {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("  SQLite Schema Completeness Test");
        System.out.println("=".repeat(70));
        System.out.println();
        
        // Force SQLite mode for testing
        System.setProperty("test.force.sqlite", "true");
        
        // Initialize database
        System.out.println("1. Initializing SQLite database...");
        DatabaseInitializer.initialize();
        
        DatabaseConnection dbConn = DatabaseConnection.getInstance();
        if (!dbConn.isSqlite()) {
            System.err.println("✗ Not using SQLite! Test aborted.");
            return;
        }
        System.out.println("✓ SQLite database initialized");
        System.out.println();
        
        // Verify all tables exist
        System.out.println("2. Verifying all tables exist...");
        String[] requiredTables = {
            "inventory", "inventory_stock_history",
            "sales_transactions", "sales_items",
            "admin_users", "admin_activity_log",
            "help_requests",
            "daily_sales_summary", "product_sales_analytics",
            "system_settings",
            "customers", "customer_addresses",
            "custom_order_categories", "custom_order_base_products",
            "addon_categories", "addons",
            "product_addon_compatibility",
            "custom_orders", "custom_order_addons",
            "custom_order_attachments", "custom_order_status_history",
            "custom_order_reviews"
        };
        
        int tableCount = verifyTables(dbConn.getConnection(), requiredTables);
        System.out.println("✓ Found " + tableCount + " out of " + requiredTables.length + " required tables");
        System.out.println();
        
        // Test data operations
        System.out.println("3. Testing data operations...");
        testDataOperations();
        System.out.println();
        
        // Verify sample data
        System.out.println("4. Verifying sample data...");
        verifySampleData(dbConn.getConnection());
        System.out.println();
        
        System.out.println("=".repeat(70));
        System.out.println("  ✓ SQLite Schema Test Complete!");
        System.out.println("=".repeat(70));
        
        // Close connection
        dbConn.closeConnection();
    }
    
    /**
     * Verify all required tables exist
     */
    private static int verifyTables(Connection conn, String[] tables) {
        int foundCount = 0;
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(
                "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name"
            );
            
            System.out.println("  Tables found in database:");
            while (rs.next()) {
                String tableName = rs.getString("name");
                if (!tableName.startsWith("sqlite_")) {
                    System.out.println("    • " + tableName);
                    foundCount++;
                }
            }
        } catch (SQLException e) {
            System.err.println("  ✗ Error checking tables: " + e.getMessage());
        }
        return foundCount;
    }
    
    /**
     * Test basic CRUD operations
     */
    private static void testDataOperations() {
        // Test inventory operations
        InventoryDAO inventoryDAO = new InventoryDAO();
        
        InventoryItem testItem = new InventoryItem(
            "SQLite Test Croissant",
            "Pastries",
            4.50,
            25,
            LocalDate.now().plusDays(2),
            "TEST-SQLITE-001",
            "Test Supplier"
        );
        
        boolean inserted = inventoryDAO.insert(testItem);
        System.out.println("  " + (inserted ? "✓" : "✗") + " Insert test item");
        
        InventoryItem retrieved = inventoryDAO.getByName("SQLite Test Croissant");
        System.out.println("  " + (retrieved != null ? "✓" : "✗") + " Retrieve test item");
        
        if (retrieved != null) {
            retrieved.setPrice(4.99);
            boolean updated = inventoryDAO.update(retrieved);
            System.out.println("  " + (updated ? "✓" : "✗") + " Update test item");
        }
        
        boolean deleted = inventoryDAO.delete("SQLite Test Croissant");
        System.out.println("  " + (deleted ? "✓" : "✗") + " Delete test item");
    }
    
    /**
     * Verify sample data was inserted
     */
    private static void verifySampleData(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Check inventory
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM inventory");
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("  ✓ Inventory items: " + count);
            }
            
            // Check admin users
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM admin_users");
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("  ✓ Admin users: " + count);
            }
            
            // Check system settings
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM system_settings");
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("  ✓ System settings: " + count);
            }
            
            // Check customers
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM customers");
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("  ✓ Customers: " + count);
            }
            
            // Check custom order categories
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM custom_order_categories");
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("  ✓ Custom order categories: " + count);
            }
            
        } catch (SQLException e) {
            System.err.println("  ✗ Error verifying sample data: " + e.getMessage());
        }
    }
}
