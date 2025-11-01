package model;

import java.io.Serializable;

public class Admin implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String password;
    private String fullName;
    private String role; // "Super Admin", "Manager", "Staff"

    public Admin(String username, String password, String fullName, String role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean authenticate(String password) {
        return this.password.equals(password);
    }
}
