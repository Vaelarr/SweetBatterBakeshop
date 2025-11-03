package kiosk.controller;

import kiosk.database.dao.CustomerDAO;
import kiosk.model.Customer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Controller for Customer account operations
 */
public class CustomerController {
    private CustomerDAO customerDAO;
    private Customer currentCustomer;
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^(09|\\+639)\\d{9}$");
    
    public CustomerController() {
        this.customerDAO = new CustomerDAO();
    }
    
    /**
     * Register a new customer
     */
    public RegisterResult register(String email, String password, String firstName, 
                                   String lastName, String phone) {
        // Validate inputs
        if (email == null || email.trim().isEmpty()) {
            return new RegisterResult(false, "Email is required");
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return new RegisterResult(false, "Invalid email format");
        }
        
        if (customerDAO.emailExists(email)) {
            return new RegisterResult(false, "Email already registered");
        }
        
        if (password == null || password.length() < 6) {
            return new RegisterResult(false, "Password must be at least 6 characters");
        }
        
        if (firstName == null || firstName.trim().isEmpty()) {
            return new RegisterResult(false, "First name is required");
        }
        
        if (lastName == null || lastName.trim().isEmpty()) {
            return new RegisterResult(false, "Last name is required");
        }
        
        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            return new RegisterResult(false, "Invalid phone number format (use 09XXXXXXXXX)");
        }
        
        // Hash password
        String passwordHash = hashPassword(password);
        
        // Create customer
        Customer customer = new Customer(email, passwordHash, firstName, lastName, phone);
        
        // Register
        boolean success = customerDAO.register(customer);
        
        if (success) {
            this.currentCustomer = customer;
            return new RegisterResult(true, "Registration successful!", customer);
        } else {
            return new RegisterResult(false, "Registration failed. Please try again.");
        }
    }
    
    /**
     * Login customer
     */
    public LoginResult login(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            return new LoginResult(false, "Email is required");
        }
        
        if (password == null || password.trim().isEmpty()) {
            return new LoginResult(false, "Password is required");
        }
        
        String passwordHash = hashPassword(password);
        Customer customer = customerDAO.login(email, passwordHash);
        
        if (customer != null) {
            this.currentCustomer = customer;
            return new LoginResult(true, "Login successful!", customer);
        } else {
            return new LoginResult(false, "Invalid email or password");
        }
    }
    
    /**
     * Logout current customer
     */
    public void logout() {
        this.currentCustomer = null;
    }
    
    /**
     * Get current logged-in customer
     */
    public Customer getCurrentCustomer() {
        return currentCustomer;
    }
    
    /**
     * Check if a customer is logged in
     */
    public boolean isLoggedIn() {
        return currentCustomer != null;
    }
    
    /**
     * Update customer profile
     */
    public boolean updateProfile(Customer customer) {
        boolean success = customerDAO.update(customer);
        
        if (success && currentCustomer != null && 
            currentCustomer.getCustomerId().equals(customer.getCustomerId())) {
            this.currentCustomer = customer;
        }
        
        return success;
    }
    
    /**
     * Change password
     */
    public boolean changePassword(String customerId, String oldPassword, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            return false;
        }
        
        // Verify old password
        Customer customer = customerDAO.findByCustomerId(customerId);
        if (customer == null) {
            return false;
        }
        
        String oldPasswordHash = hashPassword(oldPassword);
        if (!customer.getPasswordHash().equals(oldPasswordHash)) {
            return false;
        }
        
        // Update to new password
        String newPasswordHash = hashPassword(newPassword);
        return customerDAO.updatePassword(customerId, newPasswordHash);
    }
    
    /**
     * Get customer by ID
     */
    public Customer getCustomerById(String customerId) {
        return customerDAO.findByCustomerId(customerId);
    }
    
    /**
     * Get all customers (admin function)
     */
    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }
    
    /**
     * Search customers (admin function)
     */
    public List<Customer> searchCustomers(String searchTerm) {
        return customerDAO.searchCustomers(searchTerm);
    }
    
    /**
     * Deactivate customer account (admin function)
     */
    public boolean deactivateCustomer(String customerId) {
        return customerDAO.deactivate(customerId);
    }
    
    /**
     * Activate customer account (admin function)
     */
    public boolean activateCustomer(String customerId) {
        return customerDAO.activate(customerId);
    }
    
    /**
     * Hash password using SHA-256
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Result class for registration
     */
    public static class RegisterResult {
        public final boolean success;
        public final String message;
        public final Customer customer;
        
        public RegisterResult(boolean success, String message) {
            this(success, message, null);
        }
        
        public RegisterResult(boolean success, String message, Customer customer) {
            this.success = success;
            this.message = message;
            this.customer = customer;
        }
    }
    
    /**
     * Result class for login
     */
    public static class LoginResult {
        public final boolean success;
        public final String message;
        public final Customer customer;
        
        public LoginResult(boolean success, String message) {
            this(success, message, null);
        }
        
        public LoginResult(boolean success, String message, Customer customer) {
            this.success = success;
            this.message = message;
            this.customer = customer;
        }
    }
}


