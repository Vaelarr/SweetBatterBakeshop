package kiosk.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Customer account model
 */
public class Customer {
    private int id;
    private String customerId;
    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String phone;
    private LocalDate dateOfBirth;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String stateProvince;
    private String postalCode;
    private String country;
    private CustomerType customerType;
    private int loyaltyPoints;
    private int totalOrders;
    private double totalSpent;
    private boolean isActive;
    private boolean isEmailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    
    public enum CustomerType {
        REGULAR, VIP, WHOLESALE
    }
    
    // Constructors
    public Customer() {
        this.country = "Philippines";
        this.customerType = CustomerType.REGULAR;
        this.loyaltyPoints = 0;
        this.totalOrders = 0;
        this.totalSpent = 0.0;
        this.isActive = true;
        this.isEmailVerified = false;
    }
    
    public Customer(String email, String passwordHash, String firstName, String lastName, String phone) {
        this();
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getAddressLine1() {
        return addressLine1;
    }
    
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }
    
    public String getAddressLine2() {
        return addressLine2;
    }
    
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getStateProvince() {
        return stateProvince;
    }
    
    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public CustomerType getCustomerType() {
        return customerType;
    }
    
    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }
    
    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }
    
    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }
    
    public int getTotalOrders() {
        return totalOrders;
    }
    
    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }
    
    public double getTotalSpent() {
        return totalSpent;
    }
    
    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public boolean isEmailVerified() {
        return isEmailVerified;
    }
    
    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    @Override
    public String toString() {
        return "Customer{" +
                "customerId='" + customerId + '\'' +
                ", name='" + getFullName() + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", customerType=" + customerType +
                ", loyaltyPoints=" + loyaltyPoints +
                ", totalOrders=" + totalOrders +
                ", totalSpent=" + totalSpent +
                '}';
    }
}


