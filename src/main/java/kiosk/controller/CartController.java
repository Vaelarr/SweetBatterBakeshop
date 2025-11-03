package kiosk.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import kiosk.model.CartItem;
import kiosk.model.SaleTransaction;
import kiosk.util.DataPersistence;
import kiosk.util.Repository;

/**
 * Controller for managing shopping cart operations with generics and serialization
 */
public class CartController implements DataPersistence<CartItem> {
    private static final CartController instance = new CartController();
    private Repository<CartItem> cartRepository;
    private static final String CART_DATA_FILE = "data/cart.dat";
    
    private boolean discountApplied = false;
    private static final double DISCOUNT_RATE = 0.20; // 20% discount
    
    private CartController() {
        cartRepository = new Repository<>(CART_DATA_FILE);
    }
    
    public static CartController getInstance() {
        return instance;
    }
    
    /**
     * Add an item to cart or increase quantity if exists
     */
    public void addItem(String itemName, double price) {
        List<CartItem> items = cartRepository.getAll();
        
        for (CartItem item : items) {
            if (item.getItemName().equals(itemName)) {
                item.incrementQuantity();
                cartRepository.setAll(items);
                return;
            }
        }
        
        // Item not in cart, add new
        cartRepository.add(new CartItem(itemName, price, 1));
    }
    
    /**
     * Remove one quantity of an item from cart
     */
    public void removeItem(String itemName) {
        List<CartItem> items = cartRepository.getAll();
        
        for (CartItem item : items) {
            if (item.getItemName().equals(itemName)) {
                item.decrementQuantity();
                if (item.getQuantity() <= 0) {
                    items.remove(item);
                }
                cartRepository.setAll(items);
                return;
            }
        }
    }
    
    /**
     * Get quantity of a specific item
     */
    public int getItemQuantity(String itemName) {
        return cartRepository.getAll().stream()
                .filter(item -> item.getItemName().equals(itemName))
                .findFirst()
                .map(CartItem::getQuantity)
                .orElse(0);
    }
    
    /**
     * Get price of a specific item
     */
    public double getItemPrice(String itemName) {
        return cartRepository.getAll().stream()
                .filter(item -> item.getItemName().equals(itemName))
                .findFirst()
                .map(CartItem::getPrice)
                .orElse(0.0);
    }
    
    /**
     * Get total number of items in cart
     */
    public int getTotalItems() {
        return cartRepository.getAll().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
    
    /**
     * Calculate total price before discount
     */
    public double getTotalPrice() {
        return cartRepository.getAll().stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
    }
    
    /**
     * Get all cart items
     */
    public List<CartItem> getAllItems() {
        return cartRepository.getAll();
    }
    
    /**
     * Get all items as a map (for backward compatibility)
     */
    public Map<String, Integer> getCartItemsAsMap() {
        return cartRepository.getAll().stream()
                .collect(Collectors.toMap(
                        CartItem::getItemName,
                        CartItem::getQuantity,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }
    
    /**
     * Get all prices as a map (for backward compatibility)
     */
    public Map<String, Double> getCartPricesAsMap() {
        return cartRepository.getAll().stream()
                .collect(Collectors.toMap(
                        CartItem::getItemName,
                        CartItem::getPrice,
                        (existing, replacement) -> existing,
                        HashMap::new
                ));
    }
    
    /**
     * Check if cart is empty
     */
    public boolean isCartEmpty() {
        return cartRepository.isEmpty();
    }
    
    /**
     * Get subtotal for a specific item
     */
    public double getItemSubtotal(String itemName) {
        return cartRepository.getAll().stream()
                .filter(item -> item.getItemName().equals(itemName))
                .findFirst()
                .map(CartItem::getSubtotal)
                .orElse(0.0);
    }
    
    /**
     * Update item quantity directly
     */
    public void updateItemQuantity(String itemName, int quantity) {
        List<CartItem> items = cartRepository.getAll();
        
        if (quantity <= 0) {
            items.removeIf(item -> item.getItemName().equals(itemName));
        } else {
            for (CartItem item : items) {
                if (item.getItemName().equals(itemName)) {
                    item.setQuantity(quantity);
                    break;
                }
            }
        }
        
        cartRepository.setAll(items);
    }
    
    /**
     * Apply or remove discount
     */
    public void applyDiscount(boolean apply) {
        this.discountApplied = apply;
    }
    
    /**
     * Check if discount is applied
     */
    public boolean isDiscountApplied() {
        return discountApplied;
    }
    
    /**
     * Get discount amount
     */
    public double getDiscountAmount() {
        if (!discountApplied) {
            return 0.0;
        }
        return getTotalPrice() * DISCOUNT_RATE;
    }
    
    /**
     * Get total after discount
     */
    public double getTotal() {
        return getTotalPrice() - getDiscountAmount();
    }
    
    /**
     * Generate formatted receipt
     */
    public String getFormattedReceipt() {
        StringBuilder receipt = new StringBuilder();
        
        receipt.append("==================================\n");
        receipt.append("           RECEIPT\n");
        receipt.append("==================================\n\n");
        
        LocalDateTime now = LocalDateTime.now();
        receipt.append("Date: ").append(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");
        
        receipt.append("Items:\n");
        receipt.append("----------------------------------\n");
        
        for (CartItem item : cartRepository.getAll()) {
            receipt.append(String.format("%-25s %2d x ₱%6.2f = ₱%7.2f\n",
                    item.getItemName(), item.getQuantity(), item.getPrice(), item.getSubtotal()));
        }
        
        receipt.append("----------------------------------\n\n");
        
        double subtotal = getTotalPrice();
        receipt.append(String.format("Subtotal:                      ₱%7.2f\n", subtotal));
        
        if (discountApplied) {
            double discount = getDiscountAmount();
            receipt.append(String.format("Discount (20%%):                ₱%7.2f\n", discount));
        }
        
        double total = getTotal();
        receipt.append(String.format("TOTAL:                         ₱%7.2f\n\n", total));
        
        receipt.append("==================================\n");
        receipt.append("        Thank you for shopping!\n");
        receipt.append("==================================\n");
        
        return receipt.toString();
    }
    
    /**
     * Save receipt and record transaction
     */
    public String saveReceipt() {
        if (isCartEmpty()) {
            return null;
        }
        
        LocalDateTime now = LocalDateTime.now();
        String receiptId = "RCP" + now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        
        // Create transaction
        SaleTransaction transaction = new SaleTransaction(
                receiptId,
                now,
                getAllItems(),
                getTotalPrice(),
                getDiscountAmount(),
                getTotal(),
                discountApplied
        );
        
        // Record in sales controller
        SalesController.getInstance().recordTransaction(transaction);
        
        return receiptId;
    }
    
    /**
     * Clear the cart
     */
    public void clearCart() {
        cartRepository.clear();
        discountApplied = false;
    }
    
    @Override
    public void save() throws IOException {
        cartRepository.saveToFile();
    }
    
    @Override
    public void load() throws IOException, ClassNotFoundException {
        cartRepository.loadFromFile();
    }
}


