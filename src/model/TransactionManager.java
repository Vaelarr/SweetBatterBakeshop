package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class TransactionManager implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<Transaction> transactions;
    private int transactionCounter;

    public TransactionManager() {
        this.transactions = new ArrayList<>();
        this.transactionCounter = 1000;
    }

    public Transaction createTransaction(List<CartItem> items, double totalAmount, 
                                        String paymentMethod, String status) {
        String transactionId = "TXN" + (++transactionCounter);
        Transaction transaction = new Transaction(transactionId, items, totalAmount, 
                                                  paymentMethod, status);
        transactions.add(transaction);
        return transaction;
    }

    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    public List<Transaction> getTransactionsByStatus(String status) {
        return transactions.stream()
                .filter(t -> t.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactionsByDateRange(LocalDateTime start, LocalDateTime end) {
        return transactions.stream()
                .filter(t -> !t.getDateTime().isBefore(start) && !t.getDateTime().isAfter(end))
                .collect(Collectors.toList());
    }

    public Transaction getTransactionById(String transactionId) {
        return transactions.stream()
                .filter(t -> t.getTransactionId().equals(transactionId))
                .findFirst()
                .orElse(null);
    }

    public double getTotalRevenue() {
        return transactions.stream()
                .filter(t -> t.getStatus().equals("Completed"))
                .mapToDouble(Transaction::getTotalAmount)
                .sum();
    }

    public int getTotalTransactionCount() {
        return transactions.size();
    }

    public void removeTransaction(String transactionId) {
        transactions.removeIf(t -> t.getTransactionId().equals(transactionId));
    }
}
