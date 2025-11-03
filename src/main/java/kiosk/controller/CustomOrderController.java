package kiosk.controller;

import kiosk.database.dao.CustomOrderDAO;
import kiosk.database.dao.CustomProductDAO;
import kiosk.model.CustomOrder;
import kiosk.model.CustomOrder.*;
import kiosk.model.CustomProduct;
import kiosk.model.OrderAddOn;
import kiosk.model.AddonCategory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller for Custom Order operations
 */
public class CustomOrderController {
    private CustomOrderDAO orderDAO;
    private CustomProductDAO productDAO;
    private CustomOrder currentOrder;
    
    private static final double TAX_RATE = 0.12; // 12% VAT
    private static final double DEPOSIT_PERCENTAGE = 0.50; // 50% deposit
    private static final double DELIVERY_FEE = 200.00; // Fixed delivery fee
    
    public CustomOrderController() {
        this.orderDAO = new CustomOrderDAO();
        this.productDAO = new CustomProductDAO();
    }
    
    /**
     * Start a new custom order
     */
    public boolean startNewOrder(String customerId, String productCode, int servings) {
        CustomProduct product = productDAO.findByProductCode(productCode);
        
        if (product == null) {
            System.err.println("Product not found: " + productCode);
            return false;
        }
        
        // Validate servings
        if (servings < product.getMinServings() || servings > product.getMaxServings()) {
            System.err.println(String.format("Servings must be between %d and %d", 
                              product.getMinServings(), product.getMaxServings()));
            return false;
        }
        
        // Create new order
        currentOrder = new CustomOrder(customerId, productCode, servings);
        currentOrder.setProductName(product.getProductName());
        currentOrder.setOrderType(product.getCategoryCode());
        
        // Calculate base price
        double basePrice = product.calculatePrice(servings);
        currentOrder.setBasePrice(basePrice);
        
        return true;
    }
    
    /**
     * Add addon to current order
     */
    public boolean addAddon(String addonCode) {
        if (currentOrder == null) {
            System.err.println("No active order");
            return false;
        }
        
        OrderAddOn addon = productDAO.findAddonByCode(addonCode);
        if (addon == null) {
            System.err.println("Addon not found: " + addonCode);
            return false;
        }
        
        // Calculate addon price
        addon.calculatePrice(currentOrder.getBasePrice(), currentOrder.getServings());
        
        // Add to order
        currentOrder.addAddon(addon);
        
        // Recalculate total
        calculateOrderTotal();
        
        return true;
    }
    
    /**
     * Remove addon from current order
     */
    public boolean removeAddon(String addonCode) {
        if (currentOrder == null) {
            return false;
        }
        
        currentOrder.getAddons().removeIf(addon -> addon.getAddonCode().equals(addonCode));
        calculateOrderTotal();
        
        return true;
    }
    
    /**
     * Set order message
     */
    public void setOrderMessage(String message) {
        if (currentOrder != null) {
            currentOrder.setMessageOnItem(message);
        }
    }
    
    /**
     * Set special instructions
     */
    public void setSpecialInstructions(String instructions) {
        if (currentOrder != null) {
            currentOrder.setSpecialInstructions(instructions);
        }
    }
    
    /**
     * Set pickup/delivery details
     */
    public void setFulfillmentDetails(FulfillmentType type, LocalDateTime pickupDateTime, 
                                      Integer deliveryAddressId) {
        if (currentOrder != null) {
            currentOrder.setFulfillmentType(type);
            currentOrder.setPickupDatetime(pickupDateTime);
            
            if (type == FulfillmentType.DELIVERY) {
                // Only set delivery address if provided (not null)
                if (deliveryAddressId != null) {
                    currentOrder.setDeliveryAddressId(deliveryAddressId);
                }
                currentOrder.setDeliveryDatetime(pickupDateTime);
                currentOrder.setDeliveryFee(DELIVERY_FEE);
            } else {
                // For PICKUP, ensure delivery fields are null
                currentOrder.setDeliveryAddressId(null);
                currentOrder.setDeliveryDatetime(null);
                currentOrder.setDeliveryFee(0.0);
            }
            
            calculateOrderTotal();
        }
    }
    
    /**
     * Calculate order total
     */
    private void calculateOrderTotal() {
        if (currentOrder == null) {
            return;
        }
        
        // Calculate addons total
        double addonsTotal = 0;
        for (OrderAddOn addon : currentOrder.getAddons()) {
            addonsTotal += addon.getTotalAddonPrice();
        }
        currentOrder.setAddonsTotal(addonsTotal);
        
        // Calculate subtotal
        double subtotal = currentOrder.getBasePrice() + addonsTotal;
        currentOrder.setSubtotal(subtotal);
        
        // Calculate tax
        double taxAmount = (subtotal - currentOrder.getDiscountAmount()) * TAX_RATE;
        currentOrder.setTaxAmount(taxAmount);
        
        // Calculate deposit required
        double depositRequired = (subtotal + taxAmount + currentOrder.getDeliveryFee()) * DEPOSIT_PERCENTAGE;
        currentOrder.setDepositRequired(depositRequired);
        
        // Calculate final total
        currentOrder.calculateTotal();
    }
    
    /**
     * Submit order
     */
    public SubmitResult submitOrder() {
        if (currentOrder == null) {
            return new SubmitResult(false, "No active order");
        }
        
        // Validate order
        if (currentOrder.getPickupDatetime() == null) {
            return new SubmitResult(false, "Pickup date/time is required");
        }
        
        // Check if pickup time is valid (minimum lead time)
        CustomProduct product = productDAO.findByProductCode(currentOrder.getProductCode());
        if (product != null) {
            LocalDateTime minPickupTime = LocalDateTime.now().plusDays(product.getMinDaysNotice());
            if (currentOrder.getPickupDatetime().isBefore(minPickupTime)) {
                return new SubmitResult(false, 
                    String.format("Orders require at least %d days notice", product.getMinDaysNotice()));
            }
        }
        
        // Save order
        boolean success = orderDAO.insert(currentOrder);
        
        if (success) {
            String orderNumber = currentOrder.getOrderNumber();
            currentOrder = null; // Clear current order
            return new SubmitResult(true, "Order submitted successfully!", orderNumber);
        } else {
            return new SubmitResult(false, "Failed to submit order");
        }
    }
    
    /**
     * Get current order
     */
    public CustomOrder getCurrentOrder() {
        return currentOrder;
    }
    
    /**
     * Cancel current order draft
     */
    public void cancelCurrentOrder() {
        currentOrder = null;
    }
    
    /**
     * Get customer orders
     */
    public List<CustomOrder> getCustomerOrders(String customerId) {
        return orderDAO.findByCustomerId(customerId);
    }
    
    /**
     * Get order by order number
     */
    public CustomOrder getOrder(String orderNumber) {
        return orderDAO.findByOrderNumber(orderNumber);
    }
    
    /**
     * Get all orders (admin)
     */
    public List<CustomOrder> getAllOrders() {
        return orderDAO.getAllOrders();
    }
    
    /**
     * Get orders by status (admin)
     */
    public List<CustomOrder> getOrdersByStatus(OrderStatus status) {
        return orderDAO.findByStatus(status);
    }
    
    /**
     * Get upcoming orders (admin)
     */
    public List<CustomOrder> getUpcomingOrders() {
        return orderDAO.getUpcomingOrders();
    }
    
    /**
     * Update order status (admin)
     */
    public boolean updateOrderStatus(String orderNumber, OrderStatus newStatus) {
        return orderDAO.updateStatus(orderNumber, newStatus);
    }
    
    /**
     * Process deposit payment
     */
    public boolean processDeposit(String orderNumber, double amount, String paymentMethod) {
        CustomOrder order = orderDAO.findByOrderNumber(orderNumber);
        
        if (order == null) {
            return false;
        }
        
        if (amount < order.getDepositRequired()) {
            System.err.println("Deposit amount insufficient");
            return false;
        }
        
        PaymentStatus newStatus = (amount >= order.getTotalAmount()) ? 
                                 PaymentStatus.FULLY_PAID : PaymentStatus.DEPOSIT_PAID;
        
        return orderDAO.updatePaymentStatus(orderNumber, newStatus, amount);
    }
    
    /**
     * Update admin notes
     */
    public boolean updateAdminNotes(String orderNumber, String notes) {
        return orderDAO.updateAdminNotes(orderNumber, notes);
    }
    
    /**
     * Assign baker to order
     */
    public boolean assignBaker(String orderNumber, String bakerName) {
        return orderDAO.assignBaker(orderNumber, bakerName);
    }
    
    /**
     * Cancel order
     */
    public boolean cancelOrder(String orderNumber, String reason, String cancelledBy) {
        return orderDAO.cancelOrder(orderNumber, reason, cancelledBy);
    }
    
    /**
     * Get available products
     */
    public List<CustomProduct> getAvailableProducts() {
        return productDAO.getAllProducts();
    }
    
    /**
     * Get products by category
     */
    public List<CustomProduct> getProductsByCategory(String categoryCode) {
        return productDAO.getProductsByCategory(categoryCode);
    }
    
    /**
     * Get product details
     */
    public CustomProduct getProduct(String productCode) {
        return productDAO.findByProductCode(productCode);
    }
    
    /**
     * Get compatible addon categories for a product
     */
    public List<AddonCategory> getAddonCategories(String productCode) {
        return productDAO.getCompatibleAddonCategories(productCode);
    }
    
    /**
     * Get addons for a category
     */
    public List<OrderAddOn> getAddonsByCategory(String categoryCode) {
        return productDAO.getAddonsByCategory(categoryCode);
    }
    
    /**
     * Get all product categories
     */
    public List<String[]> getAllCategories() {
        return productDAO.getAllCategories();
    }
    
    /**
     * Get order statistics
     */
    public CustomOrderDAO.OrderStatistics getOrderStatistics() {
        return orderDAO.getStatistics();
    }
    
    /**
     * Result class for order submission
     */
    public static class SubmitResult {
        public final boolean success;
        public final String message;
        public final String orderNumber;
        
        public SubmitResult(boolean success, String message) {
            this(success, message, null);
        }
        
        public SubmitResult(boolean success, String message, String orderNumber) {
            this.success = success;
            this.message = message;
            this.orderNumber = orderNumber;
        }
    }
}


