package kiosk.database.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import kiosk.database.DatabaseConnection;
import kiosk.database.DatabaseConfig;
import kiosk.database.SqlDialect;
import kiosk.model.CartItem;
import kiosk.model.SaleTransaction;

/**
 * Data Access Object for Sales Transactions
 */
public class SalesDAO {
    private Connection connection;
    
    public SalesDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Create sales tables if not exist
     */
    public void createTables() {
        String salesTable = "CREATE TABLE IF NOT EXISTS sales_transactions (" +
                           SqlDialect.primaryKeyAutoIncrement("id") + ", " +
                           "transaction_id VARCHAR(50) NOT NULL UNIQUE, " +
                           "transaction_date " + SqlDialect.timestamp() + " NOT NULL, " +
                           "subtotal DECIMAL(10, 2) NOT NULL, " +
                           "discount_amount DECIMAL(10, 2) DEFAULT 0, " +
                           "total DECIMAL(10, 2) NOT NULL, " +
                           "discount_applied " + (DatabaseConfig.isSqlite() ? "INTEGER" : "BOOLEAN") + " DEFAULT 0, " +
                           SqlDialect.createdAtColumn() +
                           ")";
        
        // For SQLite, we need to handle foreign keys differently
        String foreignKeyClause = DatabaseConfig.isSqlite() 
            ? "" 
            : ", FOREIGN KEY (transaction_id) REFERENCES sales_transactions(transaction_id) ON DELETE CASCADE";
        
        String salesItemsTable = "CREATE TABLE IF NOT EXISTS sales_items (" +
                                SqlDialect.primaryKeyAutoIncrement("id") + ", " +
                                "transaction_id VARCHAR(50) NOT NULL, " +
                                "item_name VARCHAR(255) NOT NULL, " +
                                "price DECIMAL(10, 2) NOT NULL, " +
                                "quantity " + SqlDialect.integer() + " NOT NULL, " +
                                "subtotal DECIMAL(10, 2) NOT NULL" +
                                foreignKeyClause +
                                ")";
        
        try (Statement stmt = connection.createStatement()) {
            // Enable foreign keys for SQLite
            if (DatabaseConfig.isSqlite()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }
            
            stmt.execute(salesTable);
            stmt.execute(salesItemsTable);
            System.out.println("Sales tables created/verified successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating sales tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Insert a new sale transaction with items
     */
    public boolean insert(SaleTransaction transaction) {
        String salesSql = "INSERT INTO sales_transactions (transaction_id, transaction_date, " +
                         "subtotal, discount_amount, total, discount_applied) VALUES (?, ?, ?, ?, ?, ?)";
        
        String itemsSql = "INSERT INTO sales_items (transaction_id, item_name, price, quantity, subtotal) " +
                         "VALUES (?, ?, ?, ?, ?)";
        
        try {
            connection.setAutoCommit(false);
            
            // Insert transaction
            try (PreparedStatement pstmt = connection.prepareStatement(salesSql)) {
                pstmt.setString(1, transaction.getTransactionId());
                pstmt.setTimestamp(2, Timestamp.valueOf(transaction.getTransactionDate()));
                pstmt.setDouble(3, transaction.getSubtotal());
                pstmt.setDouble(4, transaction.getDiscountAmount());
                pstmt.setDouble(5, transaction.getTotal());
                // SQLite uses 0/1 for boolean, MySQL uses true/false
                if (DatabaseConfig.isSqlite()) {
                    pstmt.setInt(6, transaction.isDiscountApplied() ? 1 : 0);
                } else {
                    pstmt.setBoolean(6, transaction.isDiscountApplied());
                }
                pstmt.executeUpdate();
            }
            
            // Insert items
            try (PreparedStatement pstmt = connection.prepareStatement(itemsSql)) {
                for (CartItem item : transaction.getItems()) {
                    pstmt.setString(1, transaction.getTransactionId());
                    pstmt.setString(2, item.getItemName());
                    pstmt.setDouble(3, item.getPrice());
                    pstmt.setInt(4, item.getQuantity());
                    pstmt.setDouble(5, item.getSubtotal());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            
            connection.commit();
            connection.setAutoCommit(true);
            return true;
            
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.err.println("Error inserting sale transaction: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get all transactions
     */
    public List<SaleTransaction> getAll() {
        List<SaleTransaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM sales_transactions ORDER BY transaction_date DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                SaleTransaction transaction = extractTransactionFromResultSet(rs);
                if (transaction != null) {
                    transactions.add(transaction);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting all transactions: " + e.getMessage());
        }
        return transactions;
    }
    
    /**
     * Get transaction by ID
     */
    public SaleTransaction getById(String transactionId) {
        String sql = "SELECT * FROM sales_transactions WHERE transaction_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, transactionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractTransactionFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting transaction by ID: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Get transactions by date range
     */
    public List<SaleTransaction> getByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<SaleTransaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM sales_transactions WHERE transaction_date BETWEEN ? AND ? ORDER BY transaction_date DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(startDate));
            pstmt.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                SaleTransaction transaction = extractTransactionFromResultSet(rs);
                if (transaction != null) {
                    transactions.add(transaction);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting transactions by date range: " + e.getMessage());
        }
        return transactions;
    }
    
    /**
     * Get items for a specific transaction
     */
    private List<CartItem> getTransactionItems(String transactionId) {
        List<CartItem> items = new ArrayList<>();
        String sql = "SELECT * FROM sales_items WHERE transaction_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, transactionId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String itemName = rs.getString("item_name");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                
                items.add(new CartItem(itemName, price, quantity));
            }
        } catch (SQLException e) {
            System.err.println("Error getting transaction items: " + e.getMessage());
        }
        return items;
    }
    
    /**
     * Extract SaleTransaction from ResultSet
     */
    private SaleTransaction extractTransactionFromResultSet(ResultSet rs) throws SQLException {
        String transactionId = rs.getString("transaction_id");
        LocalDateTime transactionDate = rs.getTimestamp("transaction_date").toLocalDateTime();
        double subtotal = rs.getDouble("subtotal");
        double discountAmount = rs.getDouble("discount_amount");
        double total = rs.getDouble("total");
        boolean discountApplied = rs.getBoolean("discount_applied");
        
        List<CartItem> items = getTransactionItems(transactionId);
        
        return new SaleTransaction(
            transactionId,
            transactionDate,
            items,
            subtotal,
            discountAmount,
            total,
            discountApplied
        );
    }
    
    /**
     * Get total sales for a date range
     */
    public double getTotalSales(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT SUM(total) as total_sales FROM sales_transactions WHERE transaction_date BETWEEN ? AND ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(startDate));
            pstmt.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total_sales");
            }
        } catch (SQLException e) {
            System.err.println("Error getting total sales: " + e.getMessage());
        }
        return 0.0;
    }
    
    /**
     * Get transaction count for a date range
     */
    public int getTransactionCount(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT COUNT(*) as count FROM sales_transactions WHERE transaction_date BETWEEN ? AND ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(startDate));
            pstmt.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting transaction count: " + e.getMessage());
        }
        return 0;
    }
    
    /**
     * Update a sale transaction
     */
    public boolean update(SaleTransaction transaction) {
        String sql = "UPDATE sales_transactions SET transaction_date = ?, subtotal = ?, " +
                    "discount_amount = ?, total = ?, discount_applied = ? WHERE transaction_id = ?";
        
        try {
            connection.setAutoCommit(false);
            
            // Update main transaction
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setTimestamp(1, Timestamp.valueOf(transaction.getTransactionDate()));
                pstmt.setDouble(2, transaction.getSubtotal());
                pstmt.setDouble(3, transaction.getDiscountAmount());
                pstmt.setDouble(4, transaction.getTotal());
                if (DatabaseConfig.isSqlite()) {
                    pstmt.setInt(5, transaction.isDiscountApplied() ? 1 : 0);
                } else {
                    pstmt.setBoolean(5, transaction.isDiscountApplied());
                }
                pstmt.setString(6, transaction.getTransactionId());
                
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    // Delete old items
                    String deleteItemsSql = "DELETE FROM sales_items WHERE transaction_id = ?";
                    try (PreparedStatement deletePstmt = connection.prepareStatement(deleteItemsSql)) {
                        deletePstmt.setString(1, transaction.getTransactionId());
                        deletePstmt.executeUpdate();
                    }
                    
                    // Insert new items
                    String itemsSql = "INSERT INTO sales_items (transaction_id, item_name, price, quantity, subtotal) " +
                                     "VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement itemsPstmt = connection.prepareStatement(itemsSql)) {
                        for (CartItem item : transaction.getItems()) {
                            itemsPstmt.setString(1, transaction.getTransactionId());
                            itemsPstmt.setString(2, item.getItemName());
                            itemsPstmt.setDouble(3, item.getPrice());
                            itemsPstmt.setInt(4, item.getQuantity());
                            itemsPstmt.setDouble(5, item.getSubtotal());
                            itemsPstmt.addBatch();
                        }
                        itemsPstmt.executeBatch();
                    }
                    
                    connection.commit();
                    connection.setAutoCommit(true);
                    return true;
                }
            }
            
            connection.setAutoCommit(true);
            return false;
            
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.err.println("Error updating sale transaction: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete a single transaction by ID
     */
    public boolean delete(String transactionId) {
        try {
            connection.setAutoCommit(false);
            
            // Delete items first
            String deleteItemsSql = "DELETE FROM sales_items WHERE transaction_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteItemsSql)) {
                pstmt.setString(1, transactionId);
                pstmt.executeUpdate();
            }
            
            // Delete transaction
            String deleteTransactionSql = "DELETE FROM sales_transactions WHERE transaction_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteTransactionSql)) {
                pstmt.setString(1, transactionId);
                int rowsAffected = pstmt.executeUpdate();
                
                connection.commit();
                connection.setAutoCommit(true);
                return rowsAffected > 0;
            }
            
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.err.println("Error deleting sale transaction: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete all sales data (use with caution)
     */
    public boolean deleteAll() {
        String sql = "DELETE FROM sales_transactions";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            System.err.println("Error clearing sales data: " + e.getMessage());
            return false;
        }
    }
}


