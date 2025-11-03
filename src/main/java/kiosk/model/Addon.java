package kiosk.model;

/**
 * Represents an add-on item that can be added to custom orders
 */
public class Addon {
    private int addonId;
    private String addonName;
    private int categoryId;
    private double addonPrice;
    private String description;
    private boolean isAvailable;
    
    // Constructors
    public Addon() {
    }
    
    public Addon(int addonId, String addonName, int categoryId, double addonPrice, boolean isAvailable) {
        this.addonId = addonId;
        this.addonName = addonName;
        this.categoryId = categoryId;
        this.addonPrice = addonPrice;
        this.isAvailable = isAvailable;
    }
    
    // Getters and Setters
    public int getAddonId() {
        return addonId;
    }
    
    public void setAddonId(int addonId) {
        this.addonId = addonId;
    }
    
    public String getAddonName() {
        return addonName;
    }
    
    public void setAddonName(String addonName) {
        this.addonName = addonName;
    }
    
    public int getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
    public double getAddonPrice() {
        return addonPrice;
    }
    
    public void setAddonPrice(double addonPrice) {
        this.addonPrice = addonPrice;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isAvailable() {
        return isAvailable;
    }
    
    public void setAvailable(boolean available) {
        isAvailable = available;
    }
    
    @Override
    public String toString() {
        return addonName + " (₱" + String.format("%.2f", addonPrice) + ")";
    }
}


