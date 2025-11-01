package main.java.kiosk.util;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import main.java.kiosk.controller.CartController;
import main.java.kiosk.controller.InventoryController;
import main.java.kiosk.controller.SalesController;
import main.java.kiosk.model.CartItem;
import main.java.kiosk.model.InventoryItem;
import main.java.kiosk.model.SaleTransaction;

/**
 * Demonstration class showing MVC architecture, Generics, and Serialization features
 */
public class MVCDemo {
    
    public static void main(String[] args) {
        System.out.println("=== MVC Architecture Demo ===\n");
        
        demonstrateGenerics();
        demonstrateInventoryController();
        demonstrateCartController();
        demonstrateSalesController();
        demonstrateSerialization();
    }
    
    /**
     * Demonstrates the use of Generics in Repository pattern
     */
    private static void demonstrateGenerics() {
        System.out.println("1. GENERICS DEMONSTRATION");
        System.out.println("--------------------------");
        
        // Generic Repository can store any Serializable type
        Repository<InventoryItem> inventoryRepo = new Repository<>("demo_inventory.dat");
        Repository<CartItem> cartRepo = new Repository<>("demo_cart.dat");
        Repository<SaleTransaction> salesRepo = new Repository<>("demo_sales.dat");
        
        // Type-safe operations - no casting needed!
        InventoryItem item = new InventoryItem(
            "Demo Product", 
            "Demo Category", 
            99.99, 
            100, 
            LocalDate.now().plusMonths(6),
            "DEMO001",
            "Demo Supplier"
        );
        
        inventoryRepo.add(item);
        List<InventoryItem> items = inventoryRepo.getAll(); // Type-safe!
        
        System.out.println("✓ Generic Repository created for InventoryItem");
        System.out.println("✓ Generic Repository created for CartItem");
        System.out.println("✓ Generic Repository created for SaleTransaction");
        System.out.println("✓ Type-safe operations without casting\n");
    }
    
    /**
     * Demonstrates InventoryController (Model-Controller separation)
     */
    private static void demonstrateInventoryController() {
        System.out.println("2. INVENTORY CONTROLLER (MVC)");
        System.out.println("------------------------------");
        
        InventoryController controller = InventoryController.getInstance();
        
        // Get all items
        List<InventoryItem> allItems = controller.getAllItems();
        System.out.println("Total inventory items: " + allItems.size());
        
        // Filter by category (using Stream API with generics)
        List<InventoryItem> snacks = controller.getItemsByCategory("Snacks");
        System.out.println("Snacks in inventory: " + snacks.size());
        
        // Get low stock items
        List<InventoryItem> lowStock = controller.getLowStockItems(30);
        System.out.println("Low stock items (< 30): " + lowStock.size());
        
        // Get expiring items
        List<InventoryItem> expiringSoon = controller.getExpiringItems(7);
        System.out.println("Items expiring in 7 days: " + expiringSoon.size());
        
        System.out.println("✓ Separation of concerns: Controller handles business logic\n");
    }
    
    /**
     * Demonstrates CartController with generic CartItem model
     */
    private static void demonstrateCartController() {
        System.out.println("3. CART CONTROLLER (MVC)");
        System.out.println("-------------------------");
        
        CartController controller = CartController.getInstance();
        
        // Clear cart for demo
        controller.clearCart();
        
        // Add items
        controller.addItem("Piattos", 35.1);
        controller.addItem("Coca Cola", 30.0);
        controller.addItem("Piattos", 35.1); // Increases quantity
        
        // Get all items as CartItem objects (generic type)
        List<CartItem> cartItems = controller.getAllItems();
        System.out.println("Cart items: " + cartItems.size());
        
        for (CartItem item : cartItems) {
            System.out.println("  - " + item.getItemName() + 
                             " (Qty: " + item.getQuantity() + 
                             ", Subtotal: ₱" + item.getSubtotal() + ")");
        }
        
        // Apply discount
        controller.applyDiscount(true);
        System.out.println("Subtotal: ₱" + controller.getTotalPrice());
        System.out.println("Discount: ₱" + controller.getDiscountAmount());
        System.out.println("Total: ₱" + controller.getTotal());
        
        System.out.println("✓ Generic CartItem model with type safety\n");
    }
    
    /**
     * Demonstrates SalesController with transaction management
     */
    private static void demonstrateSalesController() {
        System.out.println("4. SALES CONTROLLER (MVC)");
        System.out.println("--------------------------");
        
        SalesController salesController = SalesController.getInstance();
        CartController cartController = CartController.getInstance();
        
        // Save current cart as transaction
        String receiptId = cartController.saveReceipt();
        System.out.println("Transaction saved: " + receiptId);
        
        // Get all transactions
        List<SaleTransaction> transactions = salesController.getAllTransactions();
        System.out.println("Total transactions: " + transactions.size());
        
        // Get today's sales
        double todaySales = salesController.getTodayTotalSales();
        System.out.println("Today's sales: ₱" + todaySales);
        
        // Get top selling items
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();
        List<Map.Entry<String, Integer>> topItems = 
            salesController.getTopSellingItems(5, startDate, endDate);
        
        System.out.println("Top 5 selling items (last 30 days):");
        for (Map.Entry<String, Integer> entry : topItems) {
            System.out.println("  - " + entry.getKey() + ": " + entry.getValue() + " sold");
        }
        
        System.out.println("✓ Generic SaleTransaction model with analytics\n");
    }
    
    /**
     * Demonstrates Serialization for data persistence
     */
    private static void demonstrateSerialization() {
        System.out.println("5. SERIALIZATION (Data Persistence)");
        System.out.println("------------------------------------");
        
        try {
            // Save data
            System.out.println("Saving data to disk...");
            InventoryController.getInstance().save();
            CartController.getInstance().save();
            SalesController.getInstance().save();
            System.out.println("✓ All data saved successfully");
            
            // Simulate loading data
            System.out.println("\nSimulating data load...");
            InventoryController.getInstance().load();
            CartController.getInstance().load();
            SalesController.getInstance().load();
            System.out.println("✓ All data loaded successfully");
            
            System.out.println("\n✓ Data persists across application restarts");
            System.out.println("✓ All models implement Serializable interface");
            System.out.println("✓ Generic Repository handles serialization transparently");
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Serialization error: " + e.getMessage());
        }
        
        System.out.println("\n=== Demo Complete ===");
    }
}
