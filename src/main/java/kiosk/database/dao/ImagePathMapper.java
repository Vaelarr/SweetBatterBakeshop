package kiosk.database.dao;

import kiosk.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to map and update product image paths in the database.
 * Maps product names to their corresponding image files in resources.
 */
public class ImagePathMapper {
    
    /**
     * Mapping of product name patterns to image paths.
     * This map contains all available product images from resources.
     */
    private static final Map<String, String> IMAGE_MAPPINGS = new HashMap<>();
    
    static {
        // BREADS & ROLLS
        IMAGE_MAPPINGS.put("French Baguette", "breads&rolls/frenchbaguette.jpg");
        IMAGE_MAPPINGS.put("Pan de Sal Classic", "breads&rolls/pandesalclassic.jpg");
        IMAGE_MAPPINGS.put("Ube Pan de Sal", "breads&rolls/ubepandesal.jpg");
        IMAGE_MAPPINGS.put("Dinner Rolls", "breads&rolls/dinnerrolls.jpg");
        IMAGE_MAPPINGS.put("Garlic Bread", "breads&rolls/garlicbread.jpg");
        IMAGE_MAPPINGS.put("Cheese Bread", "breads&rolls/cheesebread.jpg");
        IMAGE_MAPPINGS.put("Whole Wheat Bread", "breads&rolls/wholewheatbread.jpg");
        
        // PASTRIES & DESSERTS
        IMAGE_MAPPINGS.put("Chocolate Croissant", "pastries&desserts/chocolatecroissant.jpg");
        IMAGE_MAPPINGS.put("Ensaymada", "pastries&desserts/ensaymada.jpg");
        IMAGE_MAPPINGS.put("Cinnamon Roll", "pastries&desserts/cinnamonroll.jpg");
        IMAGE_MAPPINGS.put("Blueberry Muffin", "pastries&desserts/blueberrymuffin.jpg");
        IMAGE_MAPPINGS.put("Brownies", "pastries&desserts/brownies.jpg");
        IMAGE_MAPPINGS.put("Chocolate Chip Cookie", "pastries&desserts/chocolatechipcookie.jpg");
        IMAGE_MAPPINGS.put("Apple Danish", "pastries&desserts/appledanish.jpg");
        IMAGE_MAPPINGS.put("Cheese Danish", "pastries&desserts/cheesedanish.jpg");
        
        // CAKES & SPECIAL OCCASIONS
        IMAGE_MAPPINGS.put("Chocolate Cake Slice", "cakes/chocolatecakeslice.jpg");
        IMAGE_MAPPINGS.put("Ube Cake Slice", "cakes/ubecakeslice.jpg");
        IMAGE_MAPPINGS.put("Cheesecake Slice", "cakes/cheesecakeslice.jpg");
        IMAGE_MAPPINGS.put("Red Velvet Slice", "cakes/redvelvetslice.jpg");
        IMAGE_MAPPINGS.put("Carrot Cake Slice", "cakes/carrotcakeslice.jpg");
        IMAGE_MAPPINGS.put("Black Forest Slice", "cakes/blackforestslice.jpg");
        
        // BEVERAGES & EXTRAS
        IMAGE_MAPPINGS.put("Fresh Brewed Coffee", "beverages&extras/freshbrewedcoffee.jpg");
        IMAGE_MAPPINGS.put("Cappuccino", "beverages&extras/cappuccino.jpg");
        IMAGE_MAPPINGS.put("Iced Coffee", "beverages&extras/icedcoffee.jpg");
        IMAGE_MAPPINGS.put("Hot Chocolate", "beverages&extras/hotchocolate.jpg");
        IMAGE_MAPPINGS.put("Orange Juice", "beverages&extras/orangejuice.jpg");
        IMAGE_MAPPINGS.put("Mango Smoothie", "beverages&extras/mangosmoothie.jpg");
        IMAGE_MAPPINGS.put("Green Tea", "beverages&extras/greentea.jpg");
        IMAGE_MAPPINGS.put("Bottled Water", "beverages&extras/bottledwater.jpg");
    }
    
    /**
     * Updates all inventory items with their corresponding image paths.
     * Matches product names to available images and updates the database.
     * 
     * @return number of items updated
     */
    public static int updateAllImagePaths() {
        int updatedCount = 0;
        DatabaseConnection dbConn = DatabaseConnection.getInstance();
        
        String selectSql = "SELECT name FROM inventory";
        String updateSql = "UPDATE inventory SET image_path = ? WHERE name = ?";
        
        try (Connection conn = dbConn.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql);
             ResultSet rs = selectStmt.executeQuery()) {
            
            while (rs.next()) {
                String productName = rs.getString("name");
                String imagePath = findImagePath(productName);
                
                if (imagePath != null) {
                    updateStmt.setString(1, imagePath);
                    updateStmt.setString(2, productName);
                    int rowsAffected = updateStmt.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        updatedCount++;
                        System.out.println("✅ Updated: " + productName + " -> " + imagePath);
                    }
                }
            }
            
            System.out.println("\n📊 Summary: Updated " + updatedCount + " product images");
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating image paths: " + e.getMessage());
            e.printStackTrace();
        }
        
        return updatedCount;
    }
    
    /**
     * Finds the appropriate image path for a product name.
     * Uses exact matching and fuzzy matching for flexible mapping.
     * 
     * @param productName the name of the product
     * @return image path or null if not found
     */
    private static String findImagePath(String productName) {
        // Try exact match first
        if (IMAGE_MAPPINGS.containsKey(productName)) {
            return IMAGE_MAPPINGS.get(productName);
        }
        
        // Try fuzzy matching for variations
        String normalizedName = productName.toLowerCase().trim();
        
        for (Map.Entry<String, String> entry : IMAGE_MAPPINGS.entrySet()) {
            String mappedName = entry.getKey().toLowerCase();
            
            // Check if names are similar
            if (normalizedName.contains(mappedName) || mappedName.contains(normalizedName)) {
                return entry.getValue();
            }
        }
        
        return null;
    }
    
    /**
     * Updates a single product's image path.
     * 
     * @param productName the product name
     * @param imagePath the image path
     * @return true if updated successfully
     */
    public static boolean updateProductImagePath(String productName, String imagePath) {
        DatabaseConnection dbConn = DatabaseConnection.getInstance();
        String sql = "UPDATE inventory SET image_path = ? WHERE name = ?";
        
        try (Connection conn = dbConn.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, imagePath);
            stmt.setString(2, productName);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating image path for " + productName + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets the image path for a product.
     * 
     * @param productName the product name
     * @return image path from database or null
     */
    public static String getProductImagePath(String productName) {
        DatabaseConnection dbConn = DatabaseConnection.getInstance();
        String sql = "SELECT image_path FROM inventory WHERE name = ?";
        
        try (Connection conn = dbConn.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, productName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("image_path");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting image path for " + productName + ": " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Prints a report of all products and their image mapping status.
     */
    public static void printImageMappingReport() {
        DatabaseConnection dbConn = DatabaseConnection.getInstance();
        String sql = "SELECT name, category, image_path FROM inventory ORDER BY category, name";
        
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║           PRODUCT IMAGE MAPPING REPORT                         ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
        
        int totalProducts = 0;
        int mappedProducts = 0;
        String currentCategory = "";
        
        try (Connection conn = dbConn.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String name = rs.getString("name");
                String category = rs.getString("category");
                String imagePath = rs.getString("image_path");
                
                // Print category header
                if (!category.equals(currentCategory)) {
                    currentCategory = category;
                    System.out.println("\n📂 " + category.toUpperCase());
                    System.out.println("─".repeat(65));
                }
                
                totalProducts++;
                String status = "❌ No Image";
                
                if (imagePath != null && !imagePath.trim().isEmpty()) {
                    status = "✅ " + imagePath;
                    mappedProducts++;
                }
                
                System.out.printf("  %-40s %s%n", name, status);
            }
            
            // Print summary
            System.out.println("\n" + "═".repeat(65));
            System.out.println("📊 SUMMARY");
            System.out.println("─".repeat(65));
            System.out.printf("  Total Products:    %d%n", totalProducts);
            System.out.printf("  Mapped Images:     %d%n", mappedProducts);
            System.out.printf("  Missing Images:    %d%n", (totalProducts - mappedProducts));
            System.out.printf("  Coverage:          %.1f%%%n", 
                (totalProducts > 0 ? (mappedProducts * 100.0 / totalProducts) : 0));
            System.out.println("═".repeat(65) + "\n");
            
        } catch (SQLException e) {
            System.err.println("Error generating report: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Main method to run the image path mapper as a standalone utility.
     */
    public static void main(String[] args) {
        System.out.println("🖼️  SweetBatterBakeshop Image Path Mapper\n");
        
        // Print current status
        printImageMappingReport();
        
        // Ask for confirmation
        System.out.print("\nUpdate all product image paths? (yes/no): ");
        try {
            java.util.Scanner scanner = new java.util.Scanner(System.in);
            String response = scanner.nextLine().trim().toLowerCase();
            
            if (response.equals("yes") || response.equals("y")) {
                System.out.println("\n⏳ Updating image paths...\n");
                int updated = updateAllImagePaths();
                System.out.println("\n✅ Process completed! " + updated + " products updated.");
                
                // Show updated report
                System.out.println("\n📊 Updated mapping status:");
                printImageMappingReport();
            } else {
                System.out.println("❌ Update cancelled.");
            }
            
            scanner.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
