package main.java.kiosk.util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import main.java.kiosk.controller.SalesController;
import main.java.kiosk.model.CartItem;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Handles the generation and display of sales reports.
 * Delegates to SalesController (MVC Pattern)
 */
public class SalesReport {
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final DecimalFormat currencyFormat = new DecimalFormat("₱#,##0.00");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Records a new sale transaction
     * @param items Map containing items and quantities
     * @param prices Map containing items and prices
     * @param total The total sale amount
     * @param discountApplied Whether a discount was applied
     */
    public static void recordSale(Map<String, Integer> items, Map<String, Double> prices, 
                                double total, boolean discountApplied) {
        // This method is kept for backward compatibility but now uses the controller
        // The CartController.saveReceipt() method now handles this automatically
    }
    
    /**
     * Creates a panel displaying the sales report
     * @return JPanel containing the sales report
     */
    public JPanel createSalesReportPanel() {
        JPanel reportPanel = new JPanel(new BorderLayout(10, 10));
        reportPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Report header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Sales Report");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Date range selection
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        String[] dateRanges = {"Today", "Last 7 Days", "Last 30 Days", "All Time"};
        JComboBox<String> dateRangeCombo = new JComboBox<>(dateRanges);
        datePanel.add(new JLabel("Date Range:"));
        datePanel.add(dateRangeCombo);
        headerPanel.add(datePanel, BorderLayout.EAST);
        
        reportPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create tabbed pane for different reports
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Summary panel
        tabbedPane.addTab("Summary", createSummaryPanel());
        
        // Transactions panel
        tabbedPane.addTab("Transactions", createTransactionsPanel());
        
        // Items sold panel
        tabbedPane.addTab("Items Sold", createItemsPanel());
        
        reportPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Date range action listener
        dateRangeCombo.addActionListener(e -> {
            String selected = (String)dateRangeCombo.getSelectedItem();
            Date startDate = getStartDateFromRange(selected);
            updateReports(tabbedPane, startDate);
        });
        
        return reportPanel;
    }
    
    /**
     * Updates all report tabs based on date range
     */
    private void updateReports(JTabbedPane tabbedPane, Date startDate) {
        tabbedPane.setComponentAt(0, createSummaryPanel(startDate));
        tabbedPane.setComponentAt(1, createTransactionsPanel(startDate));
        tabbedPane.setComponentAt(2, createItemsPanel(startDate));
    }
    
    /**
     * Creates the summary panel with key metrics
     */
    private JPanel createSummaryPanel() {
        return createSummaryPanel(null); // All time by default
    }
    
    private JPanel createSummaryPanel(Date startDate) {
        JPanel summaryPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        ArrayList<main.java.kiosk.model.SaleTransaction> filteredTransactions = filterTransactionsByDate(startDate);
        
        // Calculate metrics
        int totalSales = filteredTransactions.size();
        double totalRevenue = filteredTransactions.stream()
                .mapToDouble(main.java.kiosk.model.SaleTransaction::getTotal)
                .sum();
        int totalItems = filteredTransactions.stream()
                .mapToInt(t -> t.getItems().stream().mapToInt(CartItem::getQuantity).sum())
                .sum();
        long discountedSales = filteredTransactions.stream()
                .filter(main.java.kiosk.model.SaleTransaction::isDiscountApplied)
                .count();
        
        // Create metric panels
        summaryPanel.add(createMetricPanel("Total Sales", String.valueOf(totalSales), "receipt.png"));
        summaryPanel.add(createMetricPanel("Total Revenue", currencyFormat.format(totalRevenue), "money.png"));
        summaryPanel.add(createMetricPanel("Items Sold", String.valueOf(totalItems), "items.png"));
        summaryPanel.add(createMetricPanel("Discounted Sales", String.valueOf(discountedSales), "discount.png"));
        
        return summaryPanel;
    }
    
    /**
     * Creates the transactions panel showing all sales
     */
    private JScrollPane createTransactionsPanel() {
        return createTransactionsPanel(null); // All time by default
    }
    
    private JScrollPane createTransactionsPanel(Date startDate) {
        String[] columns = {"Date & Time", "Items", "Total", "Discount Applied"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        ArrayList<main.java.kiosk.model.SaleTransaction> filteredTransactions = filterTransactionsByDate(startDate);
        
        for (main.java.kiosk.model.SaleTransaction transaction : filteredTransactions) {
            int itemCount = transaction.getItems().stream().mapToInt(CartItem::getQuantity).sum();
            String itemCountStr = itemCount + " item" + (itemCount > 1 ? "s" : "");
            
            // Convert LocalDateTime to Date for formatting
            Date transactionDate = Date.from(transaction.getTransactionDate()
                .atZone(ZoneId.systemDefault()).toInstant());
            
            model.addRow(new Object[]{
                dateFormat.format(transactionDate),
                itemCountStr,
                currencyFormat.format(transaction.getTotal()),
                transaction.isDiscountApplied() ? "Yes" : "No"
            });
        }
        
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        return new JScrollPane(table);
    }
    
    /**
     * Creates the items panel showing sales by product
     */
    private JScrollPane createItemsPanel() {
        return createItemsPanel(null); // All time by default
    }
    
    private JScrollPane createItemsPanel(Date startDate) {
        String[] columns = {"Item Name", "Quantity Sold", "Revenue"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        ArrayList<main.java.kiosk.model.SaleTransaction> filteredTransactions = filterTransactionsByDate(startDate);
        
        // Aggregate item sales
        Map<String, Integer> itemQuantities = new HashMap<>();
        Map<String, Double> itemRevenues = new HashMap<>();
        
        for (main.java.kiosk.model.SaleTransaction transaction : filteredTransactions) {
            for (CartItem cartItem : transaction.getItems()) {
                String item = cartItem.getItemName();
                int quantity = cartItem.getQuantity();
                double price = cartItem.getPrice();
                
                itemQuantities.put(item, itemQuantities.getOrDefault(item, 0) + quantity);
                itemRevenues.put(item, itemRevenues.getOrDefault(item, 0.0) + (price * quantity));
            }
        }
        
        // Sort items by revenue (highest first)
        java.util.List<Map.Entry<String, Double>> sortedItems = new ArrayList<>(itemRevenues.entrySet());
        sortedItems.sort(Map.Entry.<String, Double>comparingByValue().reversed());
        
        // Add to table
        for (Map.Entry<String, Double> entry : sortedItems) {
            String item = entry.getKey();
            model.addRow(new Object[]{
                item,
                itemQuantities.get(item),
                currencyFormat.format(entry.getValue())
            });
        }
        
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        return new JScrollPane(table);
    }
    
    /**
     * Creates a metric card with an icon and value
     */
    private JPanel createMetricPanel(String title, String value, String iconFile) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(100, 100, 100));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Filters transactions based on the selected date range
     */
    private ArrayList<main.java.kiosk.model.SaleTransaction> filterTransactionsByDate(Date startDate) {
        java.util.List<main.java.kiosk.model.SaleTransaction> allTransactions = SalesController.getInstance().getAllTransactions();
        
        if (startDate == null) {
            return new ArrayList<>(allTransactions);
        }
        
        ArrayList<main.java.kiosk.model.SaleTransaction> filtered = new ArrayList<>();
        for (main.java.kiosk.model.SaleTransaction transaction : allTransactions) {
            // Convert LocalDateTime to Date for comparison
            Date transactionDate = Date.from(transaction.getTransactionDate()
                .atZone(ZoneId.systemDefault()).toInstant());
            
            if (transactionDate.after(startDate) || transactionDate.equals(startDate)) {
                filtered.add(transaction);
            }
        }
        return filtered;
    }
    
    /**
     * Determines the start date based on the selected range
     */
    private Date getStartDateFromRange(String range) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        switch (range) {
            case "Today":
                return cal.getTime();
            case "Last 7 Days":
                cal.add(Calendar.DAY_OF_MONTH, -6);
                return cal.getTime();
            case "Last 30 Days":
                cal.add(Calendar.DAY_OF_MONTH, -29);
                return cal.getTime();
            case "All Time":
            default:
                return null;
        }
    }
    
    /**
     * Internal class to represent a sale transaction
     */
    static class SaleTransaction {
        private final Date date;
        private final Map<String, Integer> items;
        private final Map<String, Double> prices;
        private final double total;
        private final boolean discountApplied;
        
        public SaleTransaction(Date date, Map<String, Integer> items, Map<String, Double> prices, 
                             double total, boolean discountApplied) {
            this.date = date;
            this.items = items;
            this.prices = prices;
            this.total = total;
            this.discountApplied = discountApplied;
        }
        
        public Date getDate() {
            return date;
        }
        
        public Map<String, Integer> getItems() {
            return items;
        }
        
        public Map<String, Double> getPrices() {
            return prices;
        }
        
        public double getTotal() {
            return total;
        }
        
        public boolean isDiscountApplied() {
            return discountApplied;
        }
    }
}
