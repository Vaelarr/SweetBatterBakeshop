package main.java.kiosk.database.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import main.java.kiosk.database.DatabaseConnection;
import main.java.kiosk.model.InventoryItem;

/**
 * Data Access Object for Inventory Items
 */
public class InventoryDAO {
    private Connection connection;
    
    public InventoryDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Create inventory table if not exists
     */
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS inventory (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL UNIQUE, " +
                    "category VARCHAR(100) NOT NULL, " +
                    "price DECIMAL(10, 2) NOT NULL, " +
                    "stock_quantity INT NOT NULL, " +
                    "expiration_date DATE, " +
                    "barcode VARCHAR(50) UNIQUE, " +
                    "supplier VARCHAR(255), " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                    ")";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Inventory table created/verified successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating inventory table: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Insert a new inventory item
     */
    public boolean insert(InventoryItem item) {
        String sql = "INSERT INTO inventory (name, category, price, stock_quantity, " +
                    "expiration_date, barcode, supplier) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, item.getName());
            pstmt.setString(2, item.getCategory());
            pstmt.setDouble(3, item.getPrice());
            pstmt.setInt(4, item.getStockQuantity());
            pstmt.setDate(5, item.getExpirationDate() != null ? 
                         Date.valueOf(item.getExpirationDate()) : null);
            pstmt.setString(6, item.getBarcode());
            pstmt.setString(7, item.getSupplier());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting inventory item: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update an existing inventory item
     */
    public boolean update(InventoryItem item) {
        String sql = "UPDATE inventory SET category = ?, price = ?, stock_quantity = ?, " +
                    "expiration_date = ?, barcode = ?, supplier = ? WHERE name = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, item.getCategory());
            pstmt.setDouble(2, item.getPrice());
            pstmt.setInt(3, item.getStockQuantity());
            pstmt.setDate(4, item.getExpirationDate() != null ? 
                         Date.valueOf(item.getExpirationDate()) : null);
            pstmt.setString(5, item.getBarcode());
            pstmt.setString(6, item.getSupplier());
            pstmt.setString(7, item.getName());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating inventory item: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete an inventory item by name
     */
    public boolean delete(String name) {
        String sql = "DELETE FROM inventory WHERE name = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting inventory item: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get an inventory item by name
     */
    public InventoryItem getByName(String name) {
        String sql = "SELECT * FROM inventory WHERE name = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractItemFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting inventory item: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Get all inventory items
     */
    public List<InventoryItem> getAll() {
        List<InventoryItem> items = new ArrayList<>();
        String sql = "SELECT * FROM inventory ORDER BY category, name";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                items.add(extractItemFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all inventory items: " + e.getMessage());
        }
        return items;
    }
    
    /**
     * Get inventory items by category
     */
    public List<InventoryItem> getByCategory(String category) {
        List<InventoryItem> items = new ArrayList<>();
        String sql = "SELECT * FROM inventory WHERE category = ? ORDER BY name";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                items.add(extractItemFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting items by category: " + e.getMessage());
        }
        return items;
    }
    
    /**
     * Get low stock items
     */
    public List<InventoryItem> getLowStock(int threshold) {
        List<InventoryItem> items = new ArrayList<>();
        String sql = "SELECT * FROM inventory WHERE stock_quantity <= ? ORDER BY stock_quantity";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, threshold);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                items.add(extractItemFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting low stock items: " + e.getMessage());
        }
        return items;
    }
    
    /**
     * Get expiring items within specified days
     */
    public List<InventoryItem> getExpiringSoon(int daysWarning) {
        List<InventoryItem> items = new ArrayList<>();
        String sql = "SELECT * FROM inventory WHERE expiration_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY) ORDER BY expiration_date";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, daysWarning);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                items.add(extractItemFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting expiring items: " + e.getMessage());
        }
        return items;
    }
    
    /**
     * Get expired items
     */
    public List<InventoryItem> getExpired() {
        List<InventoryItem> items = new ArrayList<>();
        String sql = "SELECT * FROM inventory WHERE expiration_date < CURDATE() ORDER BY expiration_date";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                items.add(extractItemFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting expired items: " + e.getMessage());
        }
        return items;
    }
    
    /**
     * Update stock quantity for an item
     */
    public boolean updateStock(String name, int quantity) {
        String sql = "UPDATE inventory SET stock_quantity = ? WHERE name = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, quantity);
            pstmt.setString(2, name);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating stock: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Extract InventoryItem from ResultSet
     */
    private InventoryItem extractItemFromResultSet(ResultSet rs) throws SQLException {
        String name = rs.getString("name");
        String category = rs.getString("category");
        double price = rs.getDouble("price");
        int stockQuantity = rs.getInt("stock_quantity");
        Date expirationDate = rs.getDate("expiration_date");
        String barcode = rs.getString("barcode");
        String supplier = rs.getString("supplier");
        
        return new InventoryItem(
            name,
            category,
            price,
            stockQuantity,
            expirationDate != null ? expirationDate.toLocalDate() : null,
            barcode,
            supplier
        );
    }
    
    /**
     * Clear all inventory items (use with caution)
     */
    public boolean deleteAll() {
        String sql = "DELETE FROM inventory";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            System.err.println("Error clearing inventory: " + e.getMessage());
            return false;
        }
    }
}
