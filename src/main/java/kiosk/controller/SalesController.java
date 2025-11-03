package kiosk.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import kiosk.database.dao.SalesDAO;
import kiosk.model.SaleTransaction;
import kiosk.util.DataPersistence;

/**
 * Controller for managing sales transactions with MySQL database
 */
public class SalesController implements DataPersistence<SaleTransaction> {
    private static final SalesController instance = new SalesController();
    private SalesDAO salesDAO;
    
    private SalesController() {
        salesDAO = new SalesDAO();
        salesDAO.createTables();
    }
    
    public static SalesController getInstance() {
        return instance;
    }
    
    /**
     * Record a new transaction
     */
    public void recordTransaction(SaleTransaction transaction) {
        salesDAO.insert(transaction);
    }
    
    /**
     * Get all transactions
     */
    public List<SaleTransaction> getAllTransactions() {
        return salesDAO.getAll();
    }
    
    /**
     * Get transactions within a date range
     */
    public List<SaleTransaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return salesDAO.getByDateRange(startDate, endDate);
    }
    
    /**
     * Get transactions for today
     */
    public List<SaleTransaction> getTodayTransactions() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        return getTransactionsByDateRange(startOfDay, endOfDay);
    }
    
    /**
     * Get transactions for last N days
     */
    public List<SaleTransaction> getLastNDaysTransactions(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        LocalDateTime endDate = LocalDateTime.now();
        return salesDAO.getByDateRange(startDate, endDate);
    }
    
    /**
     * Calculate total sales for a date range
     */
    public double getTotalSales(LocalDateTime startDate, LocalDateTime endDate) {
        return salesDAO.getTotalSales(startDate, endDate);
    }
    
    /**
     * Calculate total sales for today
     */
    public double getTodayTotalSales() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        return getTotalSales(startOfDay, endOfDay);
    }
    
    /**
     * Get number of transactions for a date range
     */
    public int getTransactionCount(LocalDateTime startDate, LocalDateTime endDate) {
        return salesDAO.getTransactionCount(startDate, endDate);
    }
    
    /**
     * Get item sales summary (item name -> total quantity sold)
     */
    public Map<String, Integer> getItemSalesSummary(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Integer> itemSales = new HashMap<>();
        
        getTransactionsByDateRange(startDate, endDate).forEach(transaction -> 
            transaction.getItems().forEach(item -> {
                String itemName = item.getItemName();
                int quantity = item.getQuantity();
                itemSales.merge(itemName, quantity, Integer::sum);
            })
        );
        
        return itemSales;
    }
    
    /**
     * Get top selling items
     */
    public List<Map.Entry<String, Integer>> getTopSellingItems(int limit, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Integer> itemSales = getItemSalesSummary(startDate, endDate);
        
        return itemSales.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * Calculate average transaction value
     */
    public double getAverageTransactionValue(LocalDateTime startDate, LocalDateTime endDate) {
        List<SaleTransaction> transactions = getTransactionsByDateRange(startDate, endDate);
        
        if (transactions.isEmpty()) {
            return 0.0;
        }
        
        double total = transactions.stream()
                .mapToDouble(SaleTransaction::getTotal)
                .sum();
        
        return total / transactions.size();
    }
    
    /**
     * Get total discount given
     */
    public double getTotalDiscountGiven(LocalDateTime startDate, LocalDateTime endDate) {
        return getTransactionsByDateRange(startDate, endDate).stream()
                .mapToDouble(SaleTransaction::getDiscountAmount)
                .sum();
    }
    
    /**
     * Clear all transactions (use with caution)
     */
    public void clearAllTransactions() {
        salesDAO.deleteAll();
    }
    
    @Override
    public void save() throws IOException {
        // Data is automatically saved to database with each operation
        // This method is kept for interface compatibility
    }
    
    @Override
    public void load() throws IOException, ClassNotFoundException {
        // Data is automatically loaded from database
        // This method is kept for interface compatibility
    }
}


