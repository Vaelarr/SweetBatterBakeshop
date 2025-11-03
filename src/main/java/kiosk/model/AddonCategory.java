package kiosk.model;

/**
 * Add-on category for organizing custom order options
 */
public class AddonCategory {
    private int id;
    private String categoryCode;
    private String categoryName;
    private String description;
    private SelectionType selectionType;
    private int maxSelections;
    private boolean isRequired;
    private int displayOrder;
    
    public enum SelectionType {
        SINGLE,    // Can select only one (radio button)
        MULTIPLE,  // Can select multiple (checkbox)
        OPTIONAL   // Optional selection
    }
    
    // Constructor
    public AddonCategory() {
        this.selectionType = SelectionType.SINGLE;
        this.maxSelections = 1;
        this.isRequired = false;
    }
    
    public AddonCategory(String categoryCode, String categoryName, SelectionType selectionType) {
        this();
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.selectionType = selectionType;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public SelectionType getSelectionType() {
        return selectionType;
    }
    
    public void setSelectionType(SelectionType selectionType) {
        this.selectionType = selectionType;
    }
    
    public int getMaxSelections() {
        return maxSelections;
    }
    
    public void setMaxSelections(int maxSelections) {
        this.maxSelections = maxSelections;
    }
    
    public boolean isRequired() {
        return isRequired;
    }
    
    public void setRequired(boolean required) {
        isRequired = required;
    }
    
    public int getDisplayOrder() {
        return displayOrder;
    }
    
    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
    
    @Override
    public String toString() {
        return "AddonCategory{" +
                "categoryCode='" + categoryCode + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", selectionType=" + selectionType +
                ", isRequired=" + isRequired +
                '}';
    }
}


