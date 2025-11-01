package main.java.kiosk.util;

import java.util.Map;

import main.java.kiosk.controller.CartController;
import main.java.kiosk.model.CartItem;

/**
 * Adapter class for backward compatibility with existing UI code
 * Delegates to CartController (MVC Pattern)
 */
public class CartManager {
    
    /**
     * Adds an item to the cart or increases its quantity if already present
     */
    public static void addItem(String itemName, double price) {
        CartController.getInstance().addItem(itemName, price);
    }
    
    /**
     * Removes one instance of an item from the cart
     */
    public static void removeItem(String itemName) {
        CartController.getInstance().removeItem(itemName);
    }
    
    /**
     * Gets the current quantity of an item in the cart
     */
    public static int getItemQuantity(String itemName) {
        return CartController.getInstance().getItemQuantity(itemName);
    }
    
    /**
     * Gets the price of an item
     */
    public static double getItemPrice(String itemName) {
        return CartController.getInstance().getItemPrice(itemName);
    }
    
    /**
     * Gets the total number of items in the cart
     */
    public static int getTotalItems() {
        return CartController.getInstance().getTotalItems();
    }
    
    /**
     * Calculates the total price of all items in the cart
     */
    public static double getTotalPrice() {
        return CartController.getInstance().getTotalPrice();
    }
    
    /**
     * Gets all items in the cart with their quantities
     */
    public static Map<String, Integer> getAllItems() {
        return CartController.getInstance().getCartItemsAsMap();
    }
    
    /**
     * Checks if the cart is empty
     */
    public static boolean isCartEmpty() {
        return CartController.getInstance().isCartEmpty();
    }
    
    /**
     * Gets all items in the cart with their quantities, preserving order of insertion
     */
    public static Map<String, Integer> getOrderedItems() {
        return CartController.getInstance().getCartItemsAsMap();
    }
    
    /**
     * Gets all items in the cart with their quantities (alias for getAllItems)
     */
    public static Map<String, Integer> getCartItems() {
        return CartController.getInstance().getCartItemsAsMap();
    }
    
    /**
     * Gets all items in the cart with their prices
     */
    public static Map<String, Double> getCartPrices() {
        return CartController.getInstance().getCartPricesAsMap();
    }
    
    /**
     * Gets the subtotal for a specific item
     */
    public static double getItemSubtotal(String itemName) {
        return CartController.getInstance().getItemSubtotal(itemName);
    }
    
    /**
     * Saves the current cart as a receipt
     */
    public static String saveReceipt() {
        return CartController.getInstance().saveReceipt();
    }
    
    /**
     * Updates the quantity of an item directly
     */
    public static void updateItemQuantity(String itemName, int quantity) {
        CartController.getInstance().updateItemQuantity(itemName, quantity);
    }
    
    /**
     * Applies or removes discount from the cart
     */
    public static void applyDiscount(boolean apply) {
        CartController.getInstance().applyDiscount(apply);
    }
    
    /**
     * Checks if discount is currently applied
     */
    public static boolean isDiscountApplied() {
        return CartController.getInstance().isDiscountApplied();
    }
    
    /**
     * Gets the discount amount based on cart total
     */
    public static double getDiscountAmount() {
        return CartController.getInstance().getDiscountAmount();
    }
    
    /**
     * Gets the total price after discount
     */
    public static double getTotal() {
        return CartController.getInstance().getTotal();
    }
    
    /**
     * Generates a formatted receipt as a string
     */
    public static String getFormattedReceipt() {
        return CartController.getInstance().getFormattedReceipt();
    }
    
    /**
     * Clears all items from the cart
     */
    public static void clearCart() {
        CartController.getInstance().clearCart();
    }
}
