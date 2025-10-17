package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String transactionId;
    private LocalDateTime dateTime;
    private List<CartItem> items;
    private double totalAmount;
    private String paymentMethod;
    private String status; // "Completed", "Pending", "Cancelled"

    public Transaction(String transactionId, List<CartItem> items, double totalAmount, 
                      String paymentMethod, String status) {
        this.transactionId = transactionId;
        this.dateTime = LocalDateTime.now();
        this.items = new ArrayList<>(items);
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    // Getters
    public String getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Transaction #" + transactionId + " - ₱" + String.format("%.2f", totalAmount) + 
               " - " + dateTime.toString();
    }
}
