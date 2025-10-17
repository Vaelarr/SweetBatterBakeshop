package model;

import java.io.Serializable;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String category;
    private double price;
    private String description;
    private int stock;
    private boolean available;

    public Product(String name, String category, double price, String description) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.stock = 100; // Default stock
        this.available = true;
    }

    public Product(String name, String category, double price, String description, int stock) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.stock = stock;
        this.available = stock > 0;
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

    public int getStock() {
        return stock;
    }

    public boolean isAvailable() {
        return available && stock > 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStock(int stock) {
        this.stock = stock;
        this.available = stock > 0;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void addStock(int quantity) {
        this.stock += quantity;
        if (this.stock > 0) {
            this.available = true;
        }
    }

    public boolean reduceStock(int quantity) {
        if (this.stock >= quantity) {
            this.stock -= quantity;
            if (this.stock == 0) {
                this.available = false;
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return name + " - ₱" + String.format("%.2f", price);
    }
}
