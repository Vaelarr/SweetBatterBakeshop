package kiosk.database;

/**
 * Test program to verify MySQL JDBC driver is properly configured
 * Run this after adding mysql-connector-java-8.0.33.jar to lib folder
 */
public class JDBCTest {
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("MySQL JDBC Driver Configuration Test");
        System.out.println("===========================================\n");
        
        // Test 1: Check if JDBC driver can be loaded
        System.out.print("Test 1: Loading JDBC driver... ");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✓ SUCCESS");
        } catch (ClassNotFoundException e) {
            System.out.println("✗ FAILED");
            System.err.println("\nERROR: MySQL JDBC Driver not found!");
            System.err.println("Please add mysql-connector-java-8.0.33.jar to the lib folder");
            System.err.println("Download from: https://dev.mysql.com/downloads/connector/j/\n");
            return;
        }
        
        // Test 2: Check database configuration
        System.out.print("Test 2: Reading database config... ");
        try {
            String url = DatabaseConfig.getUrl();
            String username = DatabaseConfig.getUsername();
            String driver = DatabaseConfig.getDriver();
            
            System.out.println("✓ SUCCESS");
            System.out.println("   URL: " + url);
            System.out.println("   Username: " + username);
            System.out.println("   Driver: " + driver);
        } catch (Exception e) {
            System.out.println("✗ FAILED");
            System.err.println("Error reading config: " + e.getMessage());
            return;
        }
        
        // Test 3: Try to establish database connection
        System.out.print("\nTest 3: Connecting to database... ");
        try {
            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            if (dbConn.getConnection() != null && !dbConn.getConnection().isClosed()) {
                System.out.println("✓ SUCCESS");
                System.out.println("\n===========================================");
                System.out.println("All tests passed! Database is ready to use.");
                System.out.println("===========================================\n");
            } else {
                System.out.println("✗ FAILED - Connection is null or closed");
            }
        } catch (Exception e) {
            System.out.println("✗ FAILED");
            System.err.println("\nERROR: Could not connect to database!");
            System.err.println("Possible issues:");
            System.err.println("1. MySQL server is not running");
            System.err.println("2. Wrong username/password in config/database.properties");
            System.err.println("3. Database 'kiosk_db' doesn't exist - run setup_database.bat first");
            System.err.println("4. MySQL is running on a different port (default is 3306)");
            System.err.println("\nError details: " + e.getMessage());
            System.err.println("\n===========================================\n");
            return;
        }
        
        // Test 4: Initialize database tables
        System.out.print("Test 4: Initializing database tables... ");
        try {
            DatabaseInitializer.initialize();
            System.out.println("✓ SUCCESS");
        } catch (Exception e) {
            System.out.println("✗ FAILED");
            System.err.println("Error: " + e.getMessage());
        }
        
        System.out.println("\n===========================================");
        System.out.println("Setup Complete! You can now run the kiosk application.");
        System.out.println("===========================================");
    }
}


