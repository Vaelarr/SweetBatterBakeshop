package main.java.kiosk.util;

import java.util.List;
import java.util.Map;

import main.java.kiosk.controller.InventoryController;
import main.java.kiosk.model.InventoryItem;

/**
 * Adapter class for backward compatibility with existing UI code
 * Delegates to InventoryController (MVC Pattern)
 */
public class InventoryManager {
    private static final InventoryManager instance = new InventoryManager();
    
    private InventoryManager() {
    }
    
    public static InventoryManager getInstance() {
        return instance;
    }
    
    public List<InventoryItem> getAllItems() {
        return InventoryController.getInstance().getAllItems();
    }
    
    public void addItem(InventoryItem item) {
        InventoryController.getInstance().addItem(item);
    }
    
    public boolean removeItem(String itemName) {
        return InventoryController.getInstance().removeItem(itemName);
    }
    
    public InventoryItem getItem(String itemName) {
        return InventoryController.getInstance().getItem(itemName);
    }
    
    public void updateItem(InventoryItem updatedItem) {
        InventoryController.getInstance().updateItem(updatedItem);
    }
    
    public List<InventoryItem> getExpiredItems() {
        return InventoryController.getInstance().getExpiredItems();
    }
    
    public List<InventoryItem> getExpiringItems(int daysWarning) {
        return InventoryController.getInstance().getExpiringItems(daysWarning);
    }
    
    public List<InventoryItem> getLowStockItems(int threshold) {
        return InventoryController.getInstance().getLowStockItems(threshold);
    }
    
    public List<InventoryItem> getItemsByCategory(String category) {
        return InventoryController.getInstance().getItemsByCategory(category);
    }
    
    public Map<String, Integer> getStockByCategory() {
        return InventoryController.getInstance().getStockByCategory();
    }
    
    public InventoryItem getItemByName(String itemName) {
        return InventoryController.getInstance().getItem(itemName);
    }
}
