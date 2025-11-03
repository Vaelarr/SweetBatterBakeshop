package kiosk.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom Order model for customer special orders
 */
public class CustomOrder {
    private int id;
    private String orderNumber;
    private String customerId;
    private String productCode;
    private String productName;
    private String orderType;
    
    // Order specifications
    private int servings;
    private String messageOnItem;
    private String specialInstructions;
    
    // Pricing
    private double basePrice;
    private double addonsTotal;
    private double subtotal;
    private double discountAmount;
    private double taxAmount;
    private double deliveryFee;
    private double totalAmount;
    
    // Deposit and payment
    private double depositRequired;
    private double depositPaid;
    private String depositPaymentMethod;
    private LocalDateTime depositPaidAt;
    private double balanceDue;
    private PaymentStatus paymentStatus;
    
    // Delivery/Pickup
    private FulfillmentType fulfillmentType;
    private LocalDateTime pickupDatetime;
    private Integer deliveryAddressId;
    private LocalDateTime deliveryDatetime;
    
    // Order status
    private OrderStatus orderStatus;
    private String adminNotes;
    private String cancellationReason;
    private LocalDateTime cancelledAt;
    private String cancelledBy;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime completedAt;
    private LocalDateTime updatedAt;
    
    // Assigned staff
    private String assignedBaker;
    private String assignedDecorator;
    
    // Related data
    private List<OrderAddOn> addons;
    private List<String> attachments;
    
    public enum PaymentStatus {
        PENDING, DEPOSIT_PAID, FULLY_PAID, REFUNDED
    }
    
    public enum FulfillmentType {
        PICKUP, DELIVERY
    }
    
    public enum OrderStatus {
        PENDING, CONFIRMED, IN_PRODUCTION, READY, COMPLETED, CANCELLED
    }
    
    // Constructors
    public CustomOrder() {
        this.paymentStatus = PaymentStatus.PENDING;
        this.fulfillmentType = FulfillmentType.PICKUP;
        this.orderStatus = OrderStatus.PENDING;
        this.addons = new ArrayList<>();
        this.attachments = new ArrayList<>();
    }
    
    public CustomOrder(String customerId, String productCode, int servings) {
        this();
        this.customerId = customerId;
        this.productCode = productCode;
        this.servings = servings;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getOrderNumber() {
        return orderNumber;
    }
    
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public String getProductCode() {
        return productCode;
    }
    
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getOrderType() {
        return orderType;
    }
    
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
    
    public int getServings() {
        return servings;
    }
    
    public void setServings(int servings) {
        this.servings = servings;
    }
    
    public String getMessageOnItem() {
        return messageOnItem;
    }
    
    public void setMessageOnItem(String messageOnItem) {
        this.messageOnItem = messageOnItem;
    }
    
    public String getSpecialInstructions() {
        return specialInstructions;
    }
    
    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
    
    public double getBasePrice() {
        return basePrice;
    }
    
    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }
    
    public double getAddonsTotal() {
        return addonsTotal;
    }
    
    public void setAddonsTotal(double addonsTotal) {
        this.addonsTotal = addonsTotal;
    }
    
    public double getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
    
    public double getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public double getTaxAmount() {
        return taxAmount;
    }
    
    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }
    
    public double getDeliveryFee() {
        return deliveryFee;
    }
    
    public void setDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public double getDepositRequired() {
        return depositRequired;
    }
    
    public void setDepositRequired(double depositRequired) {
        this.depositRequired = depositRequired;
    }
    
    public double getDepositPaid() {
        return depositPaid;
    }
    
    public void setDepositPaid(double depositPaid) {
        this.depositPaid = depositPaid;
    }
    
    public String getDepositPaymentMethod() {
        return depositPaymentMethod;
    }
    
    public void setDepositPaymentMethod(String depositPaymentMethod) {
        this.depositPaymentMethod = depositPaymentMethod;
    }
    
    public LocalDateTime getDepositPaidAt() {
        return depositPaidAt;
    }
    
    public void setDepositPaidAt(LocalDateTime depositPaidAt) {
        this.depositPaidAt = depositPaidAt;
    }
    
    public double getBalanceDue() {
        return balanceDue;
    }
    
    public void setBalanceDue(double balanceDue) {
        this.balanceDue = balanceDue;
    }
    
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public FulfillmentType getFulfillmentType() {
        return fulfillmentType;
    }
    
    public void setFulfillmentType(FulfillmentType fulfillmentType) {
        this.fulfillmentType = fulfillmentType;
    }
    
    public LocalDateTime getPickupDatetime() {
        return pickupDatetime;
    }
    
    public void setPickupDatetime(LocalDateTime pickupDatetime) {
        this.pickupDatetime = pickupDatetime;
    }
    
    public Integer getDeliveryAddressId() {
        return deliveryAddressId;
    }
    
    public void setDeliveryAddressId(Integer deliveryAddressId) {
        this.deliveryAddressId = deliveryAddressId;
    }
    
    public LocalDateTime getDeliveryDatetime() {
        return deliveryDatetime;
    }
    
    public void setDeliveryDatetime(LocalDateTime deliveryDatetime) {
        this.deliveryDatetime = deliveryDatetime;
    }
    
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
    
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
    
    public String getAdminNotes() {
        return adminNotes;
    }
    
    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }
    
    public String getCancellationReason() {
        return cancellationReason;
    }
    
    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
    
    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }
    
    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
    
    public String getCancelledBy() {
        return cancelledBy;
    }
    
    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }
    
    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getAssignedBaker() {
        return assignedBaker;
    }
    
    public void setAssignedBaker(String assignedBaker) {
        this.assignedBaker = assignedBaker;
    }
    
    public String getAssignedDecorator() {
        return assignedDecorator;
    }
    
    public void setAssignedDecorator(String assignedDecorator) {
        this.assignedDecorator = assignedDecorator;
    }
    
    public List<OrderAddOn> getAddons() {
        return addons;
    }
    
    public void setAddons(List<OrderAddOn> addons) {
        this.addons = addons;
    }
    
    public void addAddon(OrderAddOn addon) {
        this.addons.add(addon);
    }
    
    public List<String> getAttachments() {
        return attachments;
    }
    
    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }
    
    public void addAttachment(String attachment) {
        this.attachments.add(attachment);
    }
    
    /**
     * Calculate total amount including all fees and taxes
     */
    public void calculateTotal() {
        this.subtotal = this.basePrice + this.addonsTotal;
        this.totalAmount = this.subtotal - this.discountAmount + this.taxAmount + this.deliveryFee;
        this.balanceDue = this.totalAmount - this.depositPaid;
    }
    
    /**
     * Get status color for UI display
     */
    public String getStatusColor() {
        switch (orderStatus) {
            case PENDING:
                return "#FFA500";
            case CONFIRMED:
                return "#4CAF50";
            case IN_PRODUCTION:
                return "#2196F3";
            case READY:
                return "#9C27B0";
            case COMPLETED:
                return "#4CAF50";
            case CANCELLED:
                return "#F44336";
            default:
                return "#757575";
        }
    }
    
    @Override
    public String toString() {
        return "CustomOrder{" +
                "orderNumber='" + orderNumber + '\'' +
                ", customerId='" + customerId + '\'' +
                ", productName='" + productName + '\'' +
                ", servings=" + servings +
                ", totalAmount=" + totalAmount +
                ", orderStatus=" + orderStatus +
                ", pickupDatetime=" + pickupDatetime +
                '}';
    }
}


