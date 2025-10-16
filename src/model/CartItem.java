package model;

public class CartItem {
    private Product product;
    private int quantity;
    private String toppings;
    private String specialNote;

    public CartItem(Product product, int quantity) {
        this(product, quantity, "", "");
    }

    public CartItem(Product product, int quantity, String toppings, String specialNote) {
        this.product = product;
        this.quantity = quantity;
        this.toppings = toppings;
        this.specialNote = specialNote;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getToppings() {
        return toppings;
    }

    public void setToppings(String toppings) {
        this.toppings = toppings;
    }

    public String getSpecialNote() {
        return specialNote;
    }

    public void setSpecialNote(String specialNote) {
        this.specialNote = specialNote;
    }

    public double getSubtotal() {
        return product.getPrice() * quantity;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(product.getName()).append(" x").append(quantity);
        if (toppings != null && !toppings.trim().isEmpty()) {
            sb.append(" + ").append(toppings);
        }
        sb.append(" - ₱").append(String.format("%.2f", getSubtotal()));
        return sb.toString();
    }

    public String getFullDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(product.getName()).append(" x").append(quantity);
        if (toppings != null && !toppings.trim().isEmpty()) {
            sb.append("\n  Toppings: ").append(toppings);
        }
        if (specialNote != null && !specialNote.trim().isEmpty()) {
            sb.append("\n  Note: ").append(specialNote);
        }
        return sb.toString();
    }
}
