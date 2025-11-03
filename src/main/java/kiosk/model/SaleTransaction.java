package kiosk.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing a completed sale transaction
 */
public class SaleTransaction implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String transactionId;
    private LocalDateTime transactionDate;
    private List<CartItem> items;
    private double subtotal;
    private double discountAmount;
    private double total;
    private boolean discountApplied;
    
    public SaleTransaction(String transactionId, LocalDateTime transactionDate, 
                          List<CartItem> items, double subtotal, 
                          double discountAmount, double total, boolean discountApplied) {
        this.transactionId = transactionId;
        this.transactionDate = transactionDate;
        this.items = new ArrayList<>(items);
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.total = total;
        this.discountApplied = discountApplied;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
    
    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }
    
    public void setItems(List<CartItem> items) {
        this.items = new ArrayList<>(items);
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
    
    public double getTotal() {
        return total;
    }
    
    public void setTotal(double total) {
        this.total = total;
    }
    
    public boolean isDiscountApplied() {
        return discountApplied;
    }
    
    public void setDiscountApplied(boolean discountApplied) {
        this.discountApplied = discountApplied;
    }
    
    public int getTotalItemCount() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }
    
    @Override
    public String toString() {
        return String.format("Transaction{id='%s', date=%s, items=%d, total=%.2f}",
                transactionId, transactionDate, getTotalItemCount(), total);
    }
}


