package kiosk.database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Utility to flush test transactions from the database
 * Use this to clear all sales data while keeping inventory intact
 */
public class FlushTransactions {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Flush Test Transactions");
        System.out.println("========================================\n");
        
        System.out.println("⚠️  WARNING: This will delete ALL sales transactions!");
        System.out.println("Inventory items will NOT be affected.\n");
        
        // Confirm with user
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        System.out.print("Type 'YES' to confirm: ");
        String confirm = scanner.nextLine();
        
        if (!confirm.equalsIgnoreCase("YES")) {
            System.out.println("Operation cancelled.");
            scanner.close();
            return;
        }
        
        try {
            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            Connection conn = dbConn.getConnection();
            
            if (conn == null) {
                System.err.println("✗ Failed to connect to database!");
                scanner.close();
                return;
            }
            
            // Display current counts
            System.out.println("\nBefore flush:");
            displayCounts(conn);
            
            // Flush transactions
            System.out.println("\nFlushing transactions...");
            flushAllTransactions(conn);
            
            // Display results
            System.out.println("\nAfter flush:");
            displayCounts(conn);
            
            System.out.println("\n========================================");
            System.out.println("✓ Transactions flushed successfully!");
            System.out.println("========================================");
            
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
    
    private static void displayCounts(Connection conn) throws Exception {
        Statement stmt = conn.createStatement();
        
        // Count sales transactions
        ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) as count FROM sales_transactions");
        if (rs1.next()) {
            System.out.println("  Sales Transactions: " + rs1.getInt("count"));
        }
        rs1.close();
        
        // Count sales items
        ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) as count FROM sales_items");
        if (rs2.next()) {
            System.out.println("  Sales Items: " + rs2.getInt("count"));
        }
        rs2.close();
        
        // Count inventory items (should remain unchanged)
        ResultSet rs3 = stmt.executeQuery("SELECT COUNT(*) as count FROM inventory");
        if (rs3.next()) {
            System.out.println("  Inventory Items: " + rs3.getInt("count") + " (will not be deleted)");
        }
        rs3.close();
        
        stmt.close();
    }
    
    private static void flushAllTransactions(Connection conn) throws Exception {
        Statement stmt = conn.createStatement();
        
        // Disable foreign key checks
        stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
        
        // Clear sales items (child table first)
        stmt.executeUpdate("TRUNCATE TABLE sales_items");
        System.out.println("  ✓ Sales items cleared");
        
        // Clear sales transactions
        stmt.executeUpdate("TRUNCATE TABLE sales_transactions");
        System.out.println("  ✓ Sales transactions cleared");
        
        // Clear inventory stock history if exists
        try {
            stmt.executeUpdate("TRUNCATE TABLE inventory_stock_history");
            System.out.println("  ✓ Inventory stock history cleared");
        } catch (Exception e) {
            // Table might not exist, ignore
        }
        
        // Clear daily sales summary if exists
        try {
            stmt.executeUpdate("TRUNCATE TABLE daily_sales_summary");
            System.out.println("  ✓ Daily sales summary cleared");
        } catch (Exception e) {
            // Table might not exist, ignore
        }
        
        // Clear product sales analytics if exists
        try {
            stmt.executeUpdate("TRUNCATE TABLE product_sales_analytics");
            System.out.println("  ✓ Product sales analytics cleared");
        } catch (Exception e) {
            // Table might not exist, ignore
        }
        
        // Clear admin activity log if exists
        try {
            stmt.executeUpdate("TRUNCATE TABLE admin_activity_log");
            System.out.println("  ✓ Admin activity log cleared");
        } catch (Exception e) {
            // Table might not exist, ignore
        }
        
        // Re-enable foreign key checks
        stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        
        stmt.close();
    }
}
