package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AdminManager implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<Admin> admins;

    public AdminManager() {
        this.admins = new ArrayList<>();
        initializeDefaultAdmin();
    }

    private void initializeDefaultAdmin() {
        // Create a default admin account
        admins.add(new Admin("admin", "admin123", "System Administrator", "Super Admin"));
    }

    public Admin authenticate(String username, String password) {
        for (Admin admin : admins) {
            if (admin.getUsername().equals(username) && admin.authenticate(password)) {
                return admin;
            }
        }
        return null;
    }

    public boolean addAdmin(Admin admin) {
        if (getAdminByUsername(admin.getUsername()) != null) {
            return false; // Username already exists
        }
        return admins.add(admin);
    }

    public boolean removeAdmin(String username) {
        return admins.removeIf(a -> a.getUsername().equals(username));
    }

    public Admin getAdminByUsername(String username) {
        return admins.stream()
                .filter(a -> a.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public List<Admin> getAllAdmins() {
        return new ArrayList<>(admins);
    }

    public boolean updateAdmin(String username, String newPassword, String newFullName, String newRole) {
        Admin admin = getAdminByUsername(username);
        if (admin != null) {
            if (newPassword != null && !newPassword.isEmpty()) {
                admin.setPassword(newPassword);
            }
            if (newFullName != null && !newFullName.isEmpty()) {
                admin.setFullName(newFullName);
            }
            if (newRole != null && !newRole.isEmpty()) {
                admin.setRole(newRole);
            }
            return true;
        }
        return false;
    }
}
