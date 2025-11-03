package kiosk.database.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kiosk.database.DatabaseConnection;
import kiosk.model.CustomProduct;
import kiosk.model.OrderAddOn;
import kiosk.model.OrderAddOn.PriceType;
import kiosk.model.AddonCategory;
import kiosk.model.AddonCategory.SelectionType;

/**
 * Data Access Object for Custom Products and Add-ons
 */
public class CustomProductDAO {
    private Connection connection;
    
    public CustomProductDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Get all active custom products
     */
    public List<CustomProduct> getAllProducts() {
        List<CustomProduct> products = new ArrayList<>();
        String sql = "SELECT bp.*, cat.category_name " +
                    "FROM custom_order_base_products bp " +
                    "JOIN custom_order_categories cat ON bp.category_code = cat.category_code " +
                    "WHERE bp.is_active = TRUE " +
                    "ORDER BY cat.display_order, bp.product_name";
        
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                products.add(extractProductFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all products: " + e.getMessage());
        }
        
        return products;
    }
    
    /**
     * Get products by category
     */
    public List<CustomProduct> getProductsByCategory(String categoryCode) {
        List<CustomProduct> products = new ArrayList<>();
        String sql = "SELECT bp.*, cat.category_name " +
                    "FROM custom_order_base_products bp " +
                    "JOIN custom_order_categories cat ON bp.category_code = cat.category_code " +
                    "WHERE bp.category_code = ? AND bp.is_active = TRUE " +
                    "ORDER BY bp.product_name";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, categoryCode);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                products.add(extractProductFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting products by category: " + e.getMessage());
        }
        
        return products;
    }
    
    /**
     * Get product by code
     */
    public CustomProduct findByProductCode(String productCode) {
        String sql = "SELECT bp.*, cat.category_name " +
                    "FROM custom_order_base_products bp " +
                    "JOIN custom_order_categories cat ON bp.category_code = cat.category_code " +
                    "WHERE bp.product_code = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, productCode);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractProductFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding product: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get all addon categories
     */
    public List<AddonCategory> getAllAddonCategories() {
        List<AddonCategory> categories = new ArrayList<>();
        String sql = "SELECT * FROM addon_categories ORDER BY display_order";
        
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                AddonCategory category = new AddonCategory();
                category.setId(rs.getInt("id"));
                category.setCategoryCode(rs.getString("category_code"));
                category.setCategoryName(rs.getString("category_name"));
                category.setDescription(rs.getString("description"));
                
                String selectionType = rs.getString("selection_type");
                if (selectionType != null) {
                    category.setSelectionType(SelectionType.valueOf(selectionType));
                }
                
                category.setMaxSelections(rs.getInt("max_selections"));
                category.setRequired(rs.getBoolean("is_required"));
                category.setDisplayOrder(rs.getInt("display_order"));
                
                categories.add(category);
            }
        } catch (SQLException e) {
            System.err.println("Error getting addon categories: " + e.getMessage());
        }
        
        return categories;
    }
    
    /**
     * Get addons by category
     */
    public List<OrderAddOn> getAddonsByCategory(String categoryCode) {
        List<OrderAddOn> addons = new ArrayList<>();
        String sql = "SELECT a.*, ac.category_name " +
                    "FROM addons a " +
                    "JOIN addon_categories ac ON a.category_code = ac.category_code " +
                    "WHERE a.category_code = ? AND a.is_active = TRUE " +
                    "ORDER BY a.is_premium DESC, a.addon_name";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, categoryCode);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                OrderAddOn addon = new OrderAddOn();
                addon.setId(rs.getInt("id"));
                addon.setAddonCode(rs.getString("addon_code"));
                addon.setAddonName(rs.getString("addon_name"));
                addon.setCategoryCode(rs.getString("category_code"));
                addon.setCategoryName(rs.getString("category_name"));
                addon.setDescription(rs.getString("description"));
                addon.setPriceModifier(rs.getDouble("price_modifier"));
                
                String priceType = rs.getString("price_type");
                if (priceType != null) {
                    addon.setPriceType(PriceType.valueOf(priceType));
                }
                
                addon.setPremium(rs.getBoolean("is_premium"));
                
                addons.add(addon);
            }
        } catch (SQLException e) {
            System.err.println("Error getting addons by category: " + e.getMessage());
        }
        
        return addons;
    }
    
    /**
     * Get compatible addon categories for a product
     */
    public List<AddonCategory> getCompatibleAddonCategories(String productCode) {
        List<AddonCategory> categories = new ArrayList<>();
        String sql = "SELECT ac.*, pac.is_required, pac.display_order " +
                    "FROM addon_categories ac " +
                    "JOIN product_addon_compatibility pac ON ac.category_code = pac.addon_category_code " +
                    "WHERE pac.product_code = ? " +
                    "ORDER BY pac.display_order, ac.category_name";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, productCode);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                AddonCategory category = new AddonCategory();
                category.setId(rs.getInt("id"));
                category.setCategoryCode(rs.getString("category_code"));
                category.setCategoryName(rs.getString("category_name"));
                category.setDescription(rs.getString("description"));
                
                String selectionType = rs.getString("selection_type");
                if (selectionType != null) {
                    category.setSelectionType(SelectionType.valueOf(selectionType));
                }
                
                category.setMaxSelections(rs.getInt("max_selections"));
                category.setRequired(rs.getBoolean("is_required"));
                category.setDisplayOrder(rs.getInt("display_order"));
                
                categories.add(category);
            }
        } catch (SQLException e) {
            System.err.println("Error getting compatible addon categories: " + e.getMessage());
        }
        
        return categories;
    }
    
    /**
     * Get all addons for a product organized by category
     */
    public Map<String, List<OrderAddOn>> getProductAddons(String productCode) {
        Map<String, List<OrderAddOn>> addonsByCategory = new HashMap<>();
        
        List<AddonCategory> categories = getCompatibleAddonCategories(productCode);
        
        for (AddonCategory category : categories) {
            List<OrderAddOn> addons = getAddonsByCategory(category.getCategoryCode());
            addonsByCategory.put(category.getCategoryCode(), addons);
        }
        
        return addonsByCategory;
    }
    
    /**
     * Get addon by code
     */
    public OrderAddOn findAddonByCode(String addonCode) {
        String sql = "SELECT a.*, ac.category_name " +
                    "FROM addons a " +
                    "JOIN addon_categories ac ON a.category_code = ac.category_code " +
                    "WHERE a.addon_code = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, addonCode);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                OrderAddOn addon = new OrderAddOn();
                addon.setId(rs.getInt("id"));
                addon.setAddonCode(rs.getString("addon_code"));
                addon.setAddonName(rs.getString("addon_name"));
                addon.setCategoryCode(rs.getString("category_code"));
                addon.setCategoryName(rs.getString("category_name"));
                addon.setDescription(rs.getString("description"));
                addon.setPriceModifier(rs.getDouble("price_modifier"));
                
                String priceType = rs.getString("price_type");
                if (priceType != null) {
                    addon.setPriceType(PriceType.valueOf(priceType));
                }
                
                addon.setPremium(rs.getBoolean("is_premium"));
                
                return addon;
            }
        } catch (SQLException e) {
            System.err.println("Error finding addon: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get all product categories
     */
    public List<String[]> getAllCategories() {
        List<String[]> categories = new ArrayList<>();
        String sql = "SELECT category_code, category_name, description, min_order_lead_time_days " +
                    "FROM custom_order_categories " +
                    "WHERE is_active = TRUE " +
                    "ORDER BY display_order";
        
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                String[] category = new String[4];
                category[0] = rs.getString("category_code");
                category[1] = rs.getString("category_name");
                category[2] = rs.getString("description");
                category[3] = String.valueOf(rs.getInt("min_order_lead_time_days"));
                categories.add(category);
            }
        } catch (SQLException e) {
            System.err.println("Error getting categories: " + e.getMessage());
        }
        
        return categories;
    }
    
    /**
     * Extract CustomProduct from ResultSet
     */
    private CustomProduct extractProductFromResultSet(ResultSet rs) throws SQLException {
        CustomProduct product = new CustomProduct();
        product.setId(rs.getInt("id"));
        product.setProductCode(rs.getString("product_code"));
        product.setCategoryCode(rs.getString("category_code"));
        product.setCategoryName(rs.getString("category_name"));
        product.setProductName(rs.getString("product_name"));
        product.setDescription(rs.getString("description"));
        product.setBasePrice(rs.getDouble("base_price"));
        product.setPricePerServing(rs.getDouble("price_per_serving"));
        product.setMinServings(rs.getInt("min_servings"));
        product.setMaxServings(rs.getInt("max_servings"));
        product.setPreparationTimeHours(rs.getInt("preparation_time_hours"));
        product.setImagePath(rs.getString("image_path"));
        product.setActive(rs.getBoolean("is_active"));
        
        return product;
    }
    
    /**
     * Insert new product (for admin)
     */
    public boolean insertProduct(CustomProduct product) {
        String sql = "INSERT INTO custom_order_base_products " +
                    "(product_code, category_code, product_name, description, base_price, " +
                    "price_per_serving, min_servings, max_servings, preparation_time_hours, " +
                    "image_path, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, product.getProductCode());
            pstmt.setString(2, product.getCategoryCode());
            pstmt.setString(3, product.getProductName());
            pstmt.setString(4, product.getDescription());
            pstmt.setDouble(5, product.getBasePrice());
            pstmt.setDouble(6, product.getPricePerServing());
            pstmt.setInt(7, product.getMinServings());
            pstmt.setInt(8, product.getMaxServings());
            pstmt.setInt(9, product.getPreparationTimeHours());
            pstmt.setString(10, product.getImagePath());
            pstmt.setBoolean(11, product.isActive());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting product: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update product
     */
    public boolean updateProduct(CustomProduct product) {
        String sql = "UPDATE custom_order_base_products SET " +
                    "category_code = ?, product_name = ?, description = ?, base_price = ?, " +
                    "price_per_serving = ?, min_servings = ?, max_servings = ?, " +
                    "preparation_time_hours = ?, image_path = ?, is_active = ? " +
                    "WHERE product_code = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, product.getCategoryCode());
            pstmt.setString(2, product.getProductName());
            pstmt.setString(3, product.getDescription());
            pstmt.setDouble(4, product.getBasePrice());
            pstmt.setDouble(5, product.getPricePerServing());
            pstmt.setInt(6, product.getMinServings());
            pstmt.setInt(7, product.getMaxServings());
            pstmt.setInt(8, product.getPreparationTimeHours());
            pstmt.setString(9, product.getImagePath());
            pstmt.setBoolean(10, product.isActive());
            pstmt.setString(11, product.getProductCode());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete product permanently (use with caution)
     * Note: This will fail if there are existing orders referencing this product
     */
    public boolean deleteProduct(String productCode) {
        String sql = "DELETE FROM custom_order_base_products WHERE product_code = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, productCode);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting product: " + e.getMessage());
            System.err.println("Note: Cannot delete product if it has existing orders. Consider deactivating instead.");
            return false;
        }
    }
    
    /**
     * Deactivate product (soft delete - recommended over hard delete)
     */
    public boolean deactivateProduct(String productCode) {
        String sql = "UPDATE custom_order_base_products SET is_active = FALSE WHERE product_code = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, productCode);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deactivating product: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get all addons as simple Addon objects
     */
    public List<kiosk.model.Addon> getAllAddons() {
        List<kiosk.model.Addon> addons = new ArrayList<>();
        String sql = "SELECT addon_id, addon_name, category_id, addon_price, description, is_available " +
                    "FROM addons " +
                    "WHERE is_available = TRUE " +
                    "ORDER BY category_id, addon_name";
        
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                kiosk.model.Addon addon = new kiosk.model.Addon();
                addon.setAddonId(rs.getInt("addon_id"));
                addon.setAddonName(rs.getString("addon_name"));
                addon.setCategoryId(rs.getInt("category_id"));
                addon.setAddonPrice(rs.getDouble("addon_price"));
                addon.setDescription(rs.getString("description"));
                addon.setAvailable(rs.getBoolean("is_available"));
                addons.add(addon);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all addons: " + e.getMessage());
        }
        
        return addons;
    }
    
    /**
     * Get addons grouped by category name
     */
    public Map<String, List<kiosk.model.Addon>> getAddonsByCategory() {
        Map<String, List<kiosk.model.Addon>> addonsByCategory = new java.util.LinkedHashMap<>();
        String sql = "SELECT a.addon_id, a.addon_name, a.category_id, a.addon_price, a.description, " +
                    "a.is_available, c.category_name " +
                    "FROM addons a " +
                    "JOIN addon_categories c ON a.category_id = c.category_id " +
                    "WHERE a.is_available = TRUE " +
                    "ORDER BY c.display_order, a.addon_name";
        
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                String categoryName = rs.getString("category_name");
                
                kiosk.model.Addon addon = new kiosk.model.Addon();
                addon.setAddonId(rs.getInt("addon_id"));
                addon.setAddonName(rs.getString("addon_name"));
                addon.setCategoryId(rs.getInt("category_id"));
                addon.setAddonPrice(rs.getDouble("addon_price"));
                addon.setDescription(rs.getString("description"));
                addon.setAvailable(rs.getBoolean("is_available"));
                
                addonsByCategory.computeIfAbsent(categoryName, k -> new ArrayList<>()).add(addon);
            }
        } catch (SQLException e) {
            System.err.println("Error getting addons by category: " + e.getMessage());
        }
        
        return addonsByCategory;
    }
    
    /**
     * Find addon by ID
     */
    public kiosk.model.Addon findAddonById(int addonId) {
        String sql = "SELECT addon_id, addon_name, category_id, addon_price, description, is_available " +
                    "FROM addons " +
                    "WHERE addon_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, addonId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                kiosk.model.Addon addon = new kiosk.model.Addon();
                addon.setAddonId(rs.getInt("addon_id"));
                addon.setAddonName(rs.getString("addon_name"));
                addon.setCategoryId(rs.getInt("category_id"));
                addon.setAddonPrice(rs.getDouble("addon_price"));
                addon.setDescription(rs.getString("description"));
                addon.setAvailable(rs.getBoolean("is_available"));
                return addon;
            }
        } catch (SQLException e) {
            System.err.println("Error finding addon by ID: " + e.getMessage());
        }
        
        return null;
    }
}


