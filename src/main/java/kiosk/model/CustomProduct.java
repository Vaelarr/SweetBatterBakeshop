package kiosk.model;

/**
 * Base product for custom orders
 */
public class CustomProduct {
    private int id;
    private String productCode;
    private String categoryCode;
    private String categoryName;
    private String productName;
    private String description;
    private double basePrice;
    private double pricePerServing;
    private int minServings;
    private int maxServings;
    private int preparationTimeHours;
    private String imagePath;
    private boolean isActive;
    
    // Constructor
    public CustomProduct() {
        this.isActive = true;
    }
    
    public CustomProduct(String productCode, String productName, double basePrice, double pricePerServing) {
        this();
        this.productCode = productCode;
        this.productName = productName;
        this.basePrice = basePrice;
        this.pricePerServing = pricePerServing;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getProductCode() {
        return productCode;
    }
    
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
    
    public String getCategoryCode() {
        return categoryCode;
    }
    
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public double getBasePrice() {
        return basePrice;
    }
    
    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }
    
    public double getPricePerServing() {
        return pricePerServing;
    }
    
    public void setPricePerServing(double pricePerServing) {
        this.pricePerServing = pricePerServing;
    }
    
    public int getMinServings() {
        return minServings;
    }
    
    public void setMinServings(int minServings) {
        this.minServings = minServings;
    }
    
    public int getMaxServings() {
        return maxServings;
    }
    
    public void setMaxServings(int maxServings) {
        this.maxServings = maxServings;
    }
    
    public int getPreparationTimeHours() {
        return preparationTimeHours;
    }
    
    public void setPreparationTimeHours(int preparationTimeHours) {
        this.preparationTimeHours = preparationTimeHours;
    }
    
    public String getImagePath() {
        return imagePath;
    }
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    /**
     * Calculate price for given number of servings
     */
    public double calculatePrice(int servings) {
        if (servings < minServings || servings > maxServings) {
            throw new IllegalArgumentException(
                String.format("Servings must be between %d and %d", minServings, maxServings)
            );
        }
        return basePrice + (pricePerServing * servings);
    }
    
    /**
     * Get minimum days notice required
     */
    public int getMinDaysNotice() {
        return (int) Math.ceil(preparationTimeHours / 24.0);
    }
    
    /**
     * Get lead time in days (alias for getMinDaysNotice)
     */
    public int getLeadTimeDays() {
        return getMinDaysNotice();
    }
    
    @Override
    public String toString() {
        return "CustomProduct{" +
                "productCode='" + productCode + '\'' +
                ", productName='" + productName + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", basePrice=" + basePrice +
                ", pricePerServing=" + pricePerServing +
                ", minServings=" + minServings +
                ", maxServings=" + maxServings +
                '}';
    }
}


