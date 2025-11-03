package kiosk.database;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import kiosk.database.dao.*;
import kiosk.model.*;
import kiosk.model.Customer.CustomerType;

/**
 * Comprehensive CRUD Validation Test
 * Tests all Create, Read, Update, Delete operations for all DAOs
 */
public class CRUDValidationTest {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("CRUD OPERATIONS VALIDATION TEST");
        System.out.println("========================================\n");
        
        // Initialize database connection
        DatabaseConnection.getInstance();
        
        // Run all CRUD tests
        testCustomerDAO();
        testInventoryDAO();
        testCustomProductDAO();
        testSalesDAO();
        testCustomOrderDAO();
        
        System.out.println("\n========================================");
        System.out.println("ALL CRUD TESTS COMPLETED");
        System.out.println("========================================");
    }
    
    /**
     * Test CustomerDAO CRUD operations
     */
    private static void testCustomerDAO() {
        System.out.println("\n--- Testing CustomerDAO CRUD Operations ---");
        CustomerDAO dao = new CustomerDAO();
        
        try {
            // CREATE - Register a new customer
            System.out.println("Testing CREATE (register)...");
            Customer customer = new Customer();
            customer.setEmail("test.crud@example.com");
            customer.setPasswordHash("hashedpassword123");
            customer.setFirstName("CRUD");
            customer.setLastName("Test");
            customer.setPhone("555-1234");
            customer.setDateOfBirth(LocalDate.of(1990, 1, 1));
            customer.setAddressLine1("123 Test St");
            customer.setCity("Test City");
            customer.setStateProvince("TS");
            customer.setPostalCode("12345");
            customer.setCountry("USA");
            customer.setCustomerType(CustomerType.REGULAR);
            
            boolean created = dao.register(customer);
            System.out.println("  ✓ CREATE: " + (created ? "SUCCESS" : "FAILED"));
            
            if (created) {
                String customerId = customer.getCustomerId();
                
                // READ - Find by customer ID
                System.out.println("Testing READ (findByCustomerId)...");
                Customer found = dao.findByCustomerId(customerId);
                System.out.println("  ✓ READ by ID: " + (found != null ? "SUCCESS" : "FAILED"));
                
                // READ - Find by email
                System.out.println("Testing READ (findByEmail)...");
                Customer foundByEmail = dao.findByEmail("test.crud@example.com");
                System.out.println("  ✓ READ by Email: " + (foundByEmail != null ? "SUCCESS" : "FAILED"));
                
                // READ - Get all customers
                System.out.println("Testing READ (getAllCustomers)...");
                List<Customer> allCustomers = dao.getAllCustomers();
                System.out.println("  ✓ READ All: " + (allCustomers.size() > 0 ? "SUCCESS" : "FAILED") 
                                 + " (" + allCustomers.size() + " customers found)");
                
                // UPDATE - Update customer information
                System.out.println("Testing UPDATE...");
                customer.setPhone("555-9999");
                customer.setCity("Updated City");
                boolean updated = dao.update(customer);
                System.out.println("  ✓ UPDATE: " + (updated ? "SUCCESS" : "FAILED"));
                
                // UPDATE - Update password
                System.out.println("Testing UPDATE (password)...");
                boolean passwordUpdated = dao.updatePassword(customerId, "newhash456");
                System.out.println("  ✓ UPDATE Password: " + (passwordUpdated ? "SUCCESS" : "FAILED"));
                
                // UPDATE - Deactivate (soft delete)
                System.out.println("Testing SOFT DELETE (deactivate)...");
                boolean deactivated = dao.deactivate(customerId);
                System.out.println("  ✓ SOFT DELETE: " + (deactivated ? "SUCCESS" : "FAILED"));
                
                // UPDATE - Reactivate
                System.out.println("Testing REACTIVATE...");
                boolean activated = dao.activate(customerId);
                System.out.println("  ✓ REACTIVATE: " + (activated ? "SUCCESS" : "FAILED"));
                
                // DELETE - Hard delete
                System.out.println("Testing DELETE (hard delete)...");
                boolean deleted = dao.delete(customerId);
                System.out.println("  ✓ DELETE: " + (deleted ? "SUCCESS" : "FAILED"));
                
                // Verify deletion
                Customer shouldBeNull = dao.findByCustomerId(customerId);
                System.out.println("  ✓ DELETE Verification: " + (shouldBeNull == null ? "SUCCESS" : "FAILED"));
            }
            
        } catch (Exception e) {
            System.err.println("  ✗ ERROR in CustomerDAO test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test InventoryDAO CRUD operations
     */
    private static void testInventoryDAO() {
        System.out.println("\n--- Testing InventoryDAO CRUD Operations ---");
        InventoryDAO dao = new InventoryDAO();
        
        try {
            // CREATE table
            dao.createTable();
            
            // CREATE - Insert item
            System.out.println("Testing CREATE (insert)...");
            InventoryItem item = new InventoryItem(
                "CRUD Test Bread",
                "Breads",
                3.99,
                50,
                LocalDate.now().plusDays(7),
                "CRUD-001",
                "Test Supplier"
            );
            boolean created = dao.insert(item);
            System.out.println("  ✓ CREATE: " + (created ? "SUCCESS" : "FAILED"));
            
            if (created) {
                // READ - Get by name
                System.out.println("Testing READ (getByName)...");
                InventoryItem found = dao.getByName("CRUD Test Bread");
                System.out.println("  ✓ READ by Name: " + (found != null ? "SUCCESS" : "FAILED"));
                
                // READ - Get all
                System.out.println("Testing READ (getAll)...");
                List<InventoryItem> allItems = dao.getAll();
                System.out.println("  ✓ READ All: " + (allItems.size() > 0 ? "SUCCESS" : "FAILED") 
                                 + " (" + allItems.size() + " items found)");
                
                // READ - Get by category
                System.out.println("Testing READ (getByCategory)...");
                List<InventoryItem> categoryItems = dao.getByCategory("Breads");
                System.out.println("  ✓ READ by Category: " + (categoryItems.size() > 0 ? "SUCCESS" : "FAILED"));
                
                // UPDATE - Update item
                System.out.println("Testing UPDATE...");
                item.setPrice(4.99);
                item.setStockQuantity(75);
                boolean updated = dao.update(item);
                System.out.println("  ✓ UPDATE: " + (updated ? "SUCCESS" : "FAILED"));
                
                // UPDATE - Update stock
                System.out.println("Testing UPDATE (updateStock)...");
                boolean stockUpdated = dao.updateStock("CRUD Test Bread", 100);
                System.out.println("  ✓ UPDATE Stock: " + (stockUpdated ? "SUCCESS" : "FAILED"));
                
                // DELETE - Delete item
                System.out.println("Testing DELETE...");
                boolean deleted = dao.delete("CRUD Test Bread");
                System.out.println("  ✓ DELETE: " + (deleted ? "SUCCESS" : "FAILED"));
                
                // Verify deletion
                InventoryItem shouldBeNull = dao.getByName("CRUD Test Bread");
                System.out.println("  ✓ DELETE Verification: " + (shouldBeNull == null ? "SUCCESS" : "FAILED"));
            }
            
        } catch (Exception e) {
            System.err.println("  ✗ ERROR in InventoryDAO test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test CustomProductDAO CRUD operations
     */
    private static void testCustomProductDAO() {
        System.out.println("\n--- Testing CustomProductDAO CRUD Operations ---");
        CustomProductDAO dao = new CustomProductDAO();
        
        try {
            // CREATE - Insert product
            System.out.println("Testing CREATE (insertProduct)...");
            CustomProduct product = new CustomProduct();
            product.setProductCode("CRUD-CAKE-001");
            product.setCategoryCode("CAKE");
            product.setProductName("CRUD Test Cake");
            product.setDescription("Test cake for CRUD validation");
            product.setBasePrice(25.00);
            product.setPricePerServing(2.50);
            product.setMinServings(8);
            product.setMaxServings(24);
            product.setPreparationTimeHours(24);
            product.setActive(true);
            
            boolean created = dao.insertProduct(product);
            System.out.println("  ✓ CREATE: " + (created ? "SUCCESS" : "FAILED"));
            
            if (created) {
                // READ - Find by product code
                System.out.println("Testing READ (findByProductCode)...");
                CustomProduct found = dao.findByProductCode("CRUD-CAKE-001");
                System.out.println("  ✓ READ by Code: " + (found != null ? "SUCCESS" : "FAILED"));
                
                // READ - Get all products
                System.out.println("Testing READ (getAllProducts)...");
                List<CustomProduct> allProducts = dao.getAllProducts();
                System.out.println("  ✓ READ All: " + (allProducts.size() > 0 ? "SUCCESS" : "FAILED"));
                
                // UPDATE - Update product
                System.out.println("Testing UPDATE (updateProduct)...");
                product.setBasePrice(30.00);
                product.setDescription("Updated test cake");
                boolean updated = dao.updateProduct(product);
                System.out.println("  ✓ UPDATE: " + (updated ? "SUCCESS" : "FAILED"));
                
                // SOFT DELETE - Deactivate product
                System.out.println("Testing SOFT DELETE (deactivateProduct)...");
                boolean deactivated = dao.deactivateProduct("CRUD-CAKE-001");
                System.out.println("  ✓ SOFT DELETE: " + (deactivated ? "SUCCESS" : "FAILED"));
                
                // DELETE - Hard delete product
                System.out.println("Testing DELETE (deleteProduct)...");
                boolean deleted = dao.deleteProduct("CRUD-CAKE-001");
                System.out.println("  ✓ DELETE: " + (deleted ? "SUCCESS" : "FAILED"));
                
                // Verify deletion
                CustomProduct shouldBeNull = dao.findByProductCode("CRUD-CAKE-001");
                System.out.println("  ✓ DELETE Verification: " + (shouldBeNull == null ? "SUCCESS" : "FAILED"));
            }
            
        } catch (Exception e) {
            System.err.println("  ✗ ERROR in CustomProductDAO test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test SalesDAO CRUD operations
     */
    private static void testSalesDAO() {
        System.out.println("\n--- Testing SalesDAO CRUD Operations ---");
        SalesDAO dao = new SalesDAO();
        
        try {
            // CREATE tables
            dao.createTables();
            
            // CREATE - Insert sale transaction
            System.out.println("Testing CREATE (insert)...");
            SaleTransaction transaction = new SaleTransaction(
                "CRUD-TEST-001",
                LocalDateTime.now(),
                List.of(
                    new CartItem("Test Bread", 3.99, 2),
                    new CartItem("Test Cake", 15.99, 1)
                ),
                23.97,
                0.00,
                23.97,
                false
            );
            
            boolean created = dao.insert(transaction);
            System.out.println("  ✓ CREATE: " + (created ? "SUCCESS" : "FAILED"));
            
            if (created) {
                // READ - Get by ID
                System.out.println("Testing READ (getById)...");
                SaleTransaction found = dao.getById("CRUD-TEST-001");
                System.out.println("  ✓ READ by ID: " + (found != null ? "SUCCESS" : "FAILED"));
                
                // READ - Get all
                System.out.println("Testing READ (getAll)...");
                List<SaleTransaction> allTransactions = dao.getAll();
                System.out.println("  ✓ READ All: " + (allTransactions.size() > 0 ? "SUCCESS" : "FAILED"));
                
                // UPDATE - Update transaction
                System.out.println("Testing UPDATE...");
                transaction.setDiscountAmount(2.40);
                transaction.setTotal(21.57);
                transaction.setDiscountApplied(true);
                boolean updated = dao.update(transaction);
                System.out.println("  ✓ UPDATE: " + (updated ? "SUCCESS" : "FAILED"));
                
                // DELETE - Delete transaction
                System.out.println("Testing DELETE...");
                boolean deleted = dao.delete("CRUD-TEST-001");
                System.out.println("  ✓ DELETE: " + (deleted ? "SUCCESS" : "FAILED"));
                
                // Verify deletion
                SaleTransaction shouldBeNull = dao.getById("CRUD-TEST-001");
                System.out.println("  ✓ DELETE Verification: " + (shouldBeNull == null ? "SUCCESS" : "FAILED"));
            }
            
        } catch (Exception e) {
            System.err.println("  ✗ ERROR in SalesDAO test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test CustomOrderDAO CRUD operations
     */
    private static void testCustomOrderDAO() {
        System.out.println("\n--- Testing CustomOrderDAO CRUD Operations ---");
        CustomOrderDAO dao = new CustomOrderDAO();
        
        try {
            // Note: This test assumes a customer exists in the database
            // For a complete test, you would need to create a test customer first
            
            System.out.println("Testing READ operations only (CREATE/UPDATE/DELETE require valid customer)...");
            
            // READ - Get all orders
            System.out.println("Testing READ (getAllOrders)...");
            List<CustomOrder> allOrders = dao.getAllOrders();
            System.out.println("  ✓ READ All Orders: SUCCESS (" + allOrders.size() + " orders found)");
            
            // READ - Get order statistics
            System.out.println("Testing READ (getStatistics)...");
            CustomOrderDAO.OrderStatistics stats = dao.getStatistics();
            System.out.println("  ✓ READ Statistics: SUCCESS");
            System.out.println("    Total Orders: " + stats.totalOrders);
            System.out.println("    Pending: " + stats.pendingOrders);
            System.out.println("    Confirmed: " + stats.confirmedOrders);
            System.out.println("    Total Revenue: $" + stats.totalRevenue);
            
            System.out.println("\n  NOTE: Full CRUD test for CustomOrderDAO requires:");
            System.out.println("    - Valid customer_id in database");
            System.out.println("    - Valid product_code in database");
            System.out.println("    - Complete custom orders schema");
            
        } catch (Exception e) {
            System.err.println("  ✗ ERROR in CustomOrderDAO test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
