package kiosk.model;

/**
 * Add-on item for custom orders
 */
public class OrderAddOn {
    private int id;
    private String addonCode;
    private String addonName;
    private String categoryCode;
    private String categoryName;
    private int quantity;
    private double priceModifier;
    private PriceType priceType;
    private double totalAddonPrice;
    private String description;
    private boolean isPremium;
    
    public enum PriceType {
        FLAT, PERCENTAGE, PER_SERVING
    }
    
    // Constructors
    public OrderAddOn() {
        this.quantity = 1;
        this.priceType = PriceType.FLAT;
    }
    
    public OrderAddOn(String addonCode, String addonName, String categoryCode, double priceModifier) {
        this();
        this.addonCode = addonCode;
        this.addonName = addonName;
        this.categoryCode = categoryCode;
        this.priceModifier = priceModifier;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getAddonCode() {
        return addonCode;
    }
    
    public void setAddonCode(String addonCode) {
        this.addonCode = addonCode;
    }
    
    public String getAddonName() {
        return addonName;
    }
    
    public void setAddonName(String addonName) {
        this.addonName = addonName;
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
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public double getPriceModifier() {
        return priceModifier;
    }
    
    public void setPriceModifier(double priceModifier) {
        this.priceModifier = priceModifier;
    }
    
    public PriceType getPriceType() {
        return priceType;
    }
    
    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }
    
    public double getTotalAddonPrice() {
        return totalAddonPrice;
    }
    
    public void setTotalAddonPrice(double totalAddonPrice) {
        this.totalAddonPrice = totalAddonPrice;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isPremium() {
        return isPremium;
    }
    
    public void setPremium(boolean premium) {
        isPremium = premium;
    }
    
    /**
     * Calculate total addon price based on base amount and servings
     */
    public void calculatePrice(double baseAmount, int servings) {
        switch (priceType) {
            case FLAT:
                this.totalAddonPrice = priceModifier * quantity;
                break;
            case PERCENTAGE:
                this.totalAddonPrice = (baseAmount * priceModifier / 100) * quantity;
                break;
            case PER_SERVING:
                this.totalAddonPrice = priceModifier * servings * quantity;
                break;
            default:
                this.totalAddonPrice = 0;
        }
    }
    
    @Override
    public String toString() {
        return "OrderAddOn{" +
                "addonName='" + addonName + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", quantity=" + quantity +
                ", priceModifier=" + priceModifier +
                ", totalAddonPrice=" + totalAddonPrice +
                '}';
    }
}


