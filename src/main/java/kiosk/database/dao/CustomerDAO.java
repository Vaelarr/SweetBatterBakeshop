package kiosk.database.dao;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import kiosk.database.DatabaseConnection;
import kiosk.model.Customer;
import kiosk.model.Customer.CustomerType;

/**
 * Data Access Object for Customer accounts
 */
public class CustomerDAO {
    private Connection connection;
    
    public CustomerDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Generate unique customer ID
     */
    private String generateCustomerId() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sql = "SELECT COUNT(*) FROM customers WHERE customer_id LIKE ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "CUST-" + datePart + "%");
            ResultSet rs = pstmt.executeQuery();
            
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            
            return String.format("CUST-%s-%04d", datePart, count + 1);
        } catch (SQLException e) {
            System.err.println("Error generating customer ID: " + e.getMessage());
            return "CUST-" + datePart + "-0001";
        }
    }
    
    /**
     * Register a new customer
     */
    public boolean register(Customer customer) {
        String sql = "INSERT INTO customers (customer_id, email, password_hash, first_name, " +
                    "last_name, phone, date_of_birth, address_line1, address_line2, city, " +
                    "state_province, postal_code, country, customer_type) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            String customerId = generateCustomerId();
            customer.setCustomerId(customerId);
            
            pstmt.setString(1, customerId);
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPasswordHash());
            pstmt.setString(4, customer.getFirstName());
            pstmt.setString(5, customer.getLastName());
            pstmt.setString(6, customer.getPhone());
            pstmt.setDate(7, customer.getDateOfBirth() != null ? 
                         Date.valueOf(customer.getDateOfBirth()) : null);
            pstmt.setString(8, customer.getAddressLine1());
            pstmt.setString(9, customer.getAddressLine2());
            pstmt.setString(10, customer.getCity());
            pstmt.setString(11, customer.getStateProvince());
            pstmt.setString(12, customer.getPostalCode());
            pstmt.setString(13, customer.getCountry());
            pstmt.setString(14, customer.getCustomerType().toString());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    customer.setId(generatedKeys.getInt(1));
                }
                return true;
            }
            
            return false;
        } catch (SQLException e) {
            System.err.println("Error registering customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Login customer by email and password
     */
    public Customer login(String email, String password) {
        String sql = "SELECT * FROM customers WHERE email = ? AND password_hash = ? AND is_active = TRUE";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Customer customer = extractCustomerFromResultSet(rs);
                
                // Update last login
                updateLastLogin(customer.getCustomerId());
                
                return customer;
            }
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Update last login timestamp
     */
    private void updateLastLogin(String customerId) {
        String sql = "UPDATE customers SET last_login = NOW() WHERE customer_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating last login: " + e.getMessage());
        }
    }
    
    /**
     * Find customer by ID
     */
    public Customer findByCustomerId(String customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractCustomerFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding customer: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Find customer by email
     */
    public Customer findByEmail(String email) {
        String sql = "SELECT * FROM customers WHERE email = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractCustomerFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding customer by email: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Update customer information
     */
    public boolean update(Customer customer) {
        String sql = "UPDATE customers SET email = ?, first_name = ?, last_name = ?, phone = ?, " +
                    "date_of_birth = ?, address_line1 = ?, address_line2 = ?, city = ?, " +
                    "state_province = ?, postal_code = ?, country = ? WHERE customer_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customer.getEmail());
            pstmt.setString(2, customer.getFirstName());
            pstmt.setString(3, customer.getLastName());
            pstmt.setString(4, customer.getPhone());
            pstmt.setDate(5, customer.getDateOfBirth() != null ? 
                         Date.valueOf(customer.getDateOfBirth()) : null);
            pstmt.setString(6, customer.getAddressLine1());
            pstmt.setString(7, customer.getAddressLine2());
            pstmt.setString(8, customer.getCity());
            pstmt.setString(9, customer.getStateProvince());
            pstmt.setString(10, customer.getPostalCode());
            pstmt.setString(11, customer.getCountry());
            pstmt.setString(12, customer.getCustomerId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating customer: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update customer password
     */
    public boolean updatePassword(String customerId, String newPasswordHash) {
        String sql = "UPDATE customers SET password_hash = ? WHERE customer_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newPasswordHash);
            pstmt.setString(2, customerId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get all customers (for admin panel)
     */
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY created_at DESC";
        
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                customers.add(extractCustomerFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all customers: " + e.getMessage());
        }
        
        return customers;
    }
    
    /**
     * Search customers by name or email
     */
    public List<Customer> searchCustomers(String searchTerm) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE " +
                    "first_name LIKE ? OR last_name LIKE ? OR email LIKE ? " +
                    "ORDER BY created_at DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String pattern = "%" + searchTerm + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                customers.add(extractCustomerFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching customers: " + e.getMessage());
        }
        
        return customers;
    }
    
    /**
     * Deactivate customer account
     */
    public boolean deactivate(String customerId) {
        String sql = "UPDATE customers SET is_active = FALSE WHERE customer_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deactivating customer: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Activate customer account
     */
    public boolean activate(String customerId) {
        String sql = "UPDATE customers SET is_active = TRUE WHERE customer_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error activating customer: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete customer account permanently (use with caution)
     * This is a hard delete - consider using deactivate() for soft delete
     */
    public boolean delete(String customerId) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting customer: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Extract Customer object from ResultSet
     */
    private Customer extractCustomerFromResultSet(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getInt("id"));
        customer.setCustomerId(rs.getString("customer_id"));
        customer.setEmail(rs.getString("email"));
        customer.setPasswordHash(rs.getString("password_hash"));
        customer.setFirstName(rs.getString("first_name"));
        customer.setLastName(rs.getString("last_name"));
        customer.setPhone(rs.getString("phone"));
        
        Date dob = rs.getDate("date_of_birth");
        if (dob != null) {
            customer.setDateOfBirth(dob.toLocalDate());
        }
        
        customer.setAddressLine1(rs.getString("address_line1"));
        customer.setAddressLine2(rs.getString("address_line2"));
        customer.setCity(rs.getString("city"));
        customer.setStateProvince(rs.getString("state_province"));
        customer.setPostalCode(rs.getString("postal_code"));
        customer.setCountry(rs.getString("country"));
        
        String typeStr = rs.getString("customer_type");
        if (typeStr != null) {
            customer.setCustomerType(CustomerType.valueOf(typeStr));
        }
        
        customer.setLoyaltyPoints(rs.getInt("loyalty_points"));
        customer.setTotalOrders(rs.getInt("total_orders"));
        customer.setTotalSpent(rs.getDouble("total_spent"));
        customer.setActive(rs.getBoolean("is_active"));
        customer.setEmailVerified(rs.getBoolean("is_email_verified"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            customer.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            customer.setLastLogin(lastLogin.toLocalDateTime());
        }
        
        return customer;
    }
    
    /**
     * Check if email already exists
     */
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM customers WHERE email = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking email: " + e.getMessage());
        }
        
        return false;
    }
}


