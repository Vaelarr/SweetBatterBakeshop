package kiosk.database;

/**
 * Quick test to check database connection status
 * Run this to see what database you're connected to
 */
public class DatabaseConnectionTest {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Database Connection Status Check");
        System.out.println("========================================\n");
        
        try {
            // Get database connection
            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            java.sql.Connection conn = dbConn.getConnection();
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Database Connected Successfully!\n");
                
                // Show which database type
                if (dbConn.isMysql()) {
                    System.out.println("Database Type: MySQL");
                    System.out.println("Database URL: " + conn.getMetaData().getURL());
                    System.out.println("Database Name: " + conn.getCatalog());
                } else if (dbConn.isSqlite()) {
                    System.out.println("Database Type: SQLite (FALLBACK)");
                    System.out.println("Database URL: " + conn.getMetaData().getURL());
                    System.out.println("\n⚠️  WARNING: Using SQLite fallback!");
                    System.out.println("This means MySQL connection failed.");
                    System.out.println("Check your database.properties file.");
                }
                
                // Check inventory count
                System.out.println("\nChecking inventory...");
                java.sql.Statement stmt = conn.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery(
                    "SELECT COUNT(*) as count FROM inventory");
                if (rs.next()) {
                    int count = rs.getInt("count");
                    System.out.println("✓ Inventory items found: " + count);
                    if (count == 0) {
                        System.out.println("\n⚠️  WARNING: No inventory items!");
                        System.out.println("You need to import test_data.sql");
                    }
                } else {
                    System.out.println("✗ Could not count inventory items");
                }
                rs.close();
                stmt.close();
                
            } else {
                System.out.println("✗ Failed to connect to database!");
            }
            
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n========================================");
        System.out.println("Test Complete");
        System.out.println("========================================");
    }
}
