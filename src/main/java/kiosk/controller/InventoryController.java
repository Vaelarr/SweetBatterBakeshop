package kiosk.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import kiosk.database.dao.InventoryDAO;
import kiosk.model.InventoryItem;
import kiosk.util.DataPersistence;

/**
 * Controller for managing inventory operations with MySQL database
 */
public class InventoryController implements DataPersistence<InventoryItem> {
    private static final InventoryController instance = new InventoryController();
    private InventoryDAO inventoryDAO;
    
    private InventoryController() {
        inventoryDAO = new InventoryDAO();
        inventoryDAO.createTable();
        
        // Initialize sample inventory if database is empty
        if (inventoryDAO.getAll().isEmpty()) {
            initializeSampleInventory();
        }
    }
    
    public static InventoryController getInstance() {
        return instance;
    }
    
    private void initializeSampleInventory() {
        LocalDate today = LocalDate.now();
        
        // Breads & Rolls
        addItem(new InventoryItem("French Baguette", "Breads & Rolls", 85.0, 30, today.plusDays(2), "BR001", "Bakery Chef"));
        addItem(new InventoryItem("Sourdough Bread", "Breads & Rolls", 120.0, 25, today.plusDays(3), "BR002", "Artisan Bakers"));
        addItem(new InventoryItem("Whole Wheat Bread", "Breads & Rolls", 95.0, 35, today.plusDays(3), "BR003", "Health Bakery"));
        addItem(new InventoryItem("Ciabatta Rolls", "Breads & Rolls", 110.0, 20, today.plusDays(2), "BR004", "Italian Bakery"));
        addItem(new InventoryItem("Multigrain Bread", "Breads & Rolls", 105.0, 28, today.plusDays(3), "BR005", "Grain Masters"));
        addItem(new InventoryItem("Garlic Bread", "Breads & Rolls", 130.0, 22, today.plusDays(2), "BR006", "Bakery Chef"));
        addItem(new InventoryItem("Brioche Buns", "Breads & Rolls", 145.0, 18, today.plusDays(2), "BR007", "French Bakery"));
        addItem(new InventoryItem("Dinner Rolls (6 pack)", "Breads & Rolls", 90.0, 40, today.plusDays(2), "BR008", "Bakery Chef"));
        addItem(new InventoryItem("Focaccia Bread", "Breads & Rolls", 155.0, 15, today.plusDays(2), "BR009", "Italian Bakery"));
        addItem(new InventoryItem("Rye Bread", "Breads & Rolls", 125.0, 20, today.plusDays(4), "BR010", "European Bakers"));
        
        // Pastries & Desserts
        addItem(new InventoryItem("Chocolate Croissant", "Pastries & Desserts", 75.0, 35, today.plusDays(1), "PD001", "French Bakery"));
        addItem(new InventoryItem("Butter Croissant", "Pastries & Desserts", 65.0, 40, today.plusDays(1), "PD002", "French Bakery"));
        addItem(new InventoryItem("Almond Croissant", "Pastries & Desserts", 85.0, 30, today.plusDays(1), "PD003", "French Bakery"));
        addItem(new InventoryItem("Danish Pastry", "Pastries & Desserts", 70.0, 35, today.plusDays(1), "PD004", "Nordic Bakers"));
        addItem(new InventoryItem("Cinnamon Roll", "Pastries & Desserts", 80.0, 40, today.plusDays(2), "PD005", "Sweet Bakery"));
        addItem(new InventoryItem("Apple Turnover", "Pastries & Desserts", 75.0, 28, today.plusDays(1), "PD006", "Fruit Pastry Co"));
        addItem(new InventoryItem("Éclair", "Pastries & Desserts", 95.0, 25, today.plusDays(1), "PD007", "French Bakery"));
        addItem(new InventoryItem("Cream Puff", "Pastries & Desserts", 65.0, 30, today.plusDays(1), "PD008", "Choux Masters"));
        addItem(new InventoryItem("Macaron (6 pack)", "Pastries & Desserts", 250.0, 20, today.plusDays(3), "PD009", "French Patisserie"));
        addItem(new InventoryItem("Fruit Tart", "Pastries & Desserts", 150.0, 18, today.plusDays(1), "PD010", "Tart Artisan"));
        addItem(new InventoryItem("Chocolate Muffin", "Pastries & Desserts", 60.0, 45, today.plusDays(2), "PD011", "Muffin Masters"));
        addItem(new InventoryItem("Blueberry Muffin", "Pastries & Desserts", 65.0, 42, today.plusDays(2), "PD012", "Muffin Masters"));
        addItem(new InventoryItem("Brownie", "Pastries & Desserts", 55.0, 50, today.plusDays(3), "PD013", "Chocolate Heaven"));
        addItem(new InventoryItem("Cookie Assortment", "Pastries & Desserts", 120.0, 35, today.plusDays(4), "PD014", "Cookie Co"));
        addItem(new InventoryItem("Tiramisu Cup", "Pastries & Desserts", 145.0, 20, today.plusDays(2), "PD015", "Italian Desserts"));
        
        // Cakes & Special Occasions
        addItem(new InventoryItem("Chocolate Cake Slice", "Cakes & Special Occasions", 95.0, 25, today.plusDays(2), "CS001", "Cake Studio"));
        addItem(new InventoryItem("Vanilla Cake Slice", "Cakes & Special Occasions", 85.0, 30, today.plusDays(2), "CS002", "Cake Studio"));
        addItem(new InventoryItem("Red Velvet Cake Slice", "Cakes & Special Occasions", 110.0, 22, today.plusDays(2), "CS003", "Velvet Bakers"));
        addItem(new InventoryItem("Carrot Cake Slice", "Cakes & Special Occasions", 100.0, 20, today.plusDays(2), "CS004", "Healthy Sweets"));
        addItem(new InventoryItem("Cheesecake Slice", "Cakes & Special Occasions", 135.0, 18, today.plusDays(2), "CS005", "Cheese Masters"));
        addItem(new InventoryItem("Black Forest Slice", "Cakes & Special Occasions", 120.0, 20, today.plusDays(2), "CS006", "German Bakery"));
        addItem(new InventoryItem("Tiramisu Cake Slice", "Cakes & Special Occasions", 140.0, 15, today.plusDays(2), "CS007", "Italian Bakery"));
        addItem(new InventoryItem("Strawberry Shortcake", "Cakes & Special Occasions", 125.0, 18, today.plusDays(1), "CS008", "Berry Delights"));
        addItem(new InventoryItem("Cupcake Assorted (6)", "Cakes & Special Occasions", 280.0, 25, today.plusDays(2), "CS009", "Cupcake Heaven"));
        addItem(new InventoryItem("Mini Cake (6 inch)", "Cakes & Special Occasions", 450.0, 12, today.plusDays(3), "CS010", "Cake Studio"));
        addItem(new InventoryItem("Birthday Cake (8 inch)", "Cakes & Special Occasions", 850.0, 8, today.plusDays(2), "CS011", "Celebration Cakes"));
        addItem(new InventoryItem("Wedding Cupcake Tower", "Cakes & Special Occasions", 2500.0, 3, today.plusDays(1), "CS012", "Wedding Sweets"));
        
        // Beverages & Extras
        addItem(new InventoryItem("Fresh Brewed Coffee", "Beverages & Extras", 55.0, 100, today.plusDays(1), "BE001", "Coffee Bar"));
        addItem(new InventoryItem("Espresso", "Beverages & Extras", 65.0, 80, today.plusDays(1), "BE002", "Coffee Bar"));
        addItem(new InventoryItem("Cappuccino", "Beverages & Extras", 75.0, 75, today.plusDays(1), "BE003", "Coffee Bar"));
        addItem(new InventoryItem("Latte", "Beverages & Extras", 85.0, 70, today.plusDays(1), "BE004", "Coffee Bar"));
        addItem(new InventoryItem("Hot Chocolate", "Beverages & Extras", 70.0, 60, today.plusDays(1), "BE005", "Chocolate Bar"));
        addItem(new InventoryItem("Fresh Orange Juice", "Beverages & Extras", 65.0, 50, today, "BE006", "Fresh Juice Co"));
        addItem(new InventoryItem("Bottled Water", "Beverages & Extras", 25.0, 150, today.plusYears(1), "BE007", "Pure Water"));
        addItem(new InventoryItem("Iced Tea", "Beverages & Extras", 45.0, 80, today.plusDays(3), "BE008", "Tea House"));
        addItem(new InventoryItem("Milk (1L)", "Beverages & Extras", 95.0, 40, today.plusDays(5), "BE009", "Dairy Fresh"));
        addItem(new InventoryItem("Cream Cheese Spread", "Beverages & Extras", 125.0, 30, today.plusDays(14), "BE010", "Dairy Delights"));
        addItem(new InventoryItem("Butter (250g)", "Beverages & Extras", 145.0, 35, today.plusMonths(1), "BE011", "Butter Co"));
        addItem(new InventoryItem("Jam Assortment", "Beverages & Extras", 180.0, 25, today.plusMonths(3), "BE012", "Fruit Preserves"));
        addItem(new InventoryItem("Honey (500g)", "Beverages & Extras", 250.0, 20, today.plusYears(1), "BE013", "Pure Honey"));
        addItem(new InventoryItem("Nutella Spread", "Beverages & Extras", 220.0, 28, today.plusMonths(6), "BE014", "Ferrero"));
    }
    
    public List<InventoryItem> getAllItems() {
        return inventoryDAO.getAll();
    }
    
    public void addItem(InventoryItem item) {
        InventoryItem existing = inventoryDAO.getByName(item.getName());
        if (existing != null) {
            inventoryDAO.update(item);
        } else {
            inventoryDAO.insert(item);
        }
    }
    
    public boolean removeItem(String itemName) {
        return inventoryDAO.delete(itemName);
    }
    
    public InventoryItem getItem(String itemName) {
        return inventoryDAO.getByName(itemName);
    }
    
    public void updateItem(InventoryItem updatedItem) {
        inventoryDAO.update(updatedItem);
    }
    
    public List<InventoryItem> getExpiredItems() {
        return inventoryDAO.getExpired();
    }
    
    public List<InventoryItem> getExpiringItems(int daysWarning) {
        return inventoryDAO.getExpiringSoon(daysWarning);
    }
    
    public List<InventoryItem> getLowStockItems(int threshold) {
        return inventoryDAO.getLowStock(threshold);
    }
    
    public List<InventoryItem> getItemsByCategory(String category) {
        return inventoryDAO.getByCategory(category);
    }
    
    public Map<String, Integer> getStockByCategory() {
        Map<String, Integer> stockByCategory = new HashMap<>();
        
        for (InventoryItem item : inventoryDAO.getAll()) {
            String category = item.getCategory();
            int currentStock = stockByCategory.getOrDefault(category, 0);
            stockByCategory.put(category, currentStock + item.getStockQuantity());
        }
        
        return stockByCategory;
    }
    
    @Override
    public void save() throws IOException {
        // Data is automatically saved to database with each operation
        // This method is kept for interface compatibility
    }
    
    @Override
    public void load() throws IOException, ClassNotFoundException {
        // Data is automatically loaded from database
        // This method is kept for interface compatibility
    }
}


