package kiosk.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Model class representing an inventory item with serialization support
 */
public class InventoryItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String category;
    private double price;
    private int stockQuantity;
    private LocalDate expirationDate;
    private String barcode;
    private String supplier;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public InventoryItem(String name, String category, double price, int stockQuantity, 
                         LocalDate expirationDate, String barcode, String supplier) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.expirationDate = expirationDate;
        this.barcode = barcode;
        this.supplier = supplier;
    }
    
    // Copy constructor
    public InventoryItem(InventoryItem other) {
        this.name = other.name;
        this.category = other.category;
        this.price = other.price;
        this.stockQuantity = other.stockQuantity;
        this.expirationDate = other.expirationDate;
        this.barcode = other.barcode;
        this.supplier = other.supplier;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public int getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public LocalDate getExpirationDate() {
        return expirationDate;
    }
    
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
    
    public String getBarcode() {
        return barcode;
    }
    
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    
    public String getSupplier() {
        return supplier;
    }
    
    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }
    
    public String getFormattedExpirationDate() {
        return expirationDate != null ? expirationDate.format(DATE_FORMATTER) : "N/A";
    }
    
    public boolean isExpired() {
        return expirationDate != null && LocalDate.now().isAfter(expirationDate);
    }
    
    public boolean isExpiringSoon(int daysWarning) {
        if (expirationDate == null) {
            return false;
        }
        LocalDate warningDate = LocalDate.now().plusDays(daysWarning);
        return !expirationDate.isBefore(LocalDate.now()) && !expirationDate.isAfter(warningDate);
    }
    
    public boolean isLowStock(int threshold) {
        return stockQuantity <= threshold;
    }
    
    public long getDaysUntilExpiration() {
        if (expirationDate == null) {
            return -1;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), expirationDate);
    }
    
    @Override
    public String toString() {
        return String.format("InventoryItem{name='%s', category='%s', price=%.2f, stock=%d, expiration=%s}",
                name, category, price, stockQuantity, getFormattedExpirationDate());
    }
}


