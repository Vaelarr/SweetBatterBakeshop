package model;

public class Product {
    private String name;
    private String category;
    private double price;
    private String description;

    public Product(String name, String category, double price, String description) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name + " - ₱" + String.format("%.2f", price);
    }
}
