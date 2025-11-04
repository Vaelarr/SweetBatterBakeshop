package kiosk.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for loading and caching product images from resources.
 * Provides methods to load, resize, and cache images for efficient display.
 */
public class ImageLoader {
    
    // Cache to store loaded images
    private static final Map<String, ImageIcon> imageCache = new HashMap<>();
    
    // Default placeholder image for missing products
    private static ImageIcon placeholderIcon = null;
    
    // Standard image dimensions
    public static final int SMALL_SIZE = 50;
    public static final int MEDIUM_SIZE = 100;
    public static final int LARGE_SIZE = 200;
    public static final int CARD_SIZE = 150;
    
    /**
     * Loads a product image from the resources folder.
     * Images are cached for performance.
     * 
     * @param imagePath the relative path from resources (e.g., "breads&rolls/frenchbaguette.jpg")
     * @return ImageIcon of the product, or placeholder if not found
     */
    public static ImageIcon loadProductImage(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return getPlaceholderIcon();
        }
        
        // Check cache first
        if (imageCache.containsKey(imagePath)) {
            return imageCache.get(imagePath);
        }
        
        try {
            // Load from resources
            URL imageUrl = ImageLoader.class.getClassLoader().getResource(imagePath);
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
                imageCache.put(imagePath, icon);
                return icon;
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + imagePath + " - " + e.getMessage());
        }
        
        return getPlaceholderIcon();
    }
    
    /**
     * Loads and resizes a product image to specified dimensions.
     * 
     * @param imagePath the relative path from resources
     * @param width desired width
     * @param height desired height
     * @return resized ImageIcon
     */
    public static ImageIcon loadProductImage(String imagePath, int width, int height) {
        ImageIcon original = loadProductImage(imagePath);
        return resizeIcon(original, width, height);
    }
    
    /**
     * Loads a product image and resizes it to a square dimension.
     * 
     * @param imagePath the relative path from resources
     * @param size the square dimension (width and height)
     * @return resized square ImageIcon
     */
    public static ImageIcon loadProductImageSquare(String imagePath, int size) {
        return loadProductImage(imagePath, size, size);
    }
    
    /**
     * Resizes an existing ImageIcon to new dimensions.
     * 
     * @param icon the original icon
     * @param width new width
     * @param height new height
     * @return resized ImageIcon
     */
    public static ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        if (icon == null) {
            return getPlaceholderIcon(width, height);
        }
        
        Image img = icon.getImage();
        Image resized = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resized);
    }
    
    /**
     * Creates a rounded image icon for product displays.
     * 
     * @param imagePath the relative path from resources
     * @param size the dimension of the rounded image
     * @return rounded ImageIcon
     */
    public static ImageIcon loadRoundedProductImage(String imagePath, int size) {
        ImageIcon original = loadProductImage(imagePath, size, size);
        return createRoundedIcon(original, size);
    }
    
    /**
     * Creates a circular/rounded version of an ImageIcon.
     * 
     * @param icon the original icon
     * @param size the diameter of the circle
     * @return rounded ImageIcon
     */
    private static ImageIcon createRoundedIcon(ImageIcon icon, int size) {
        if (icon == null) {
            return getPlaceholderIcon(size, size);
        }
        
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        
        // Enable anti-aliasing for smooth edges
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Create circular clip
        g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, size, size));
        
        // Draw the image
        g2.drawImage(icon.getImage(), 0, 0, size, size, null);
        g2.dispose();
        
        return new ImageIcon(image);
    }
    
    /**
     * Gets a placeholder icon for missing images.
     * 
     * @return default placeholder ImageIcon
     */
    public static ImageIcon getPlaceholderIcon() {
        if (placeholderIcon == null) {
            placeholderIcon = createPlaceholderIcon(MEDIUM_SIZE, MEDIUM_SIZE);
        }
        return placeholderIcon;
    }
    
    /**
     * Gets a placeholder icon with specific dimensions.
     * 
     * @param width desired width
     * @param height desired height
     * @return placeholder ImageIcon
     */
    public static ImageIcon getPlaceholderIcon(int width, int height) {
        return createPlaceholderIcon(width, height);
    }
    
    /**
     * Creates a placeholder image with "No Image" text.
     * 
     * @param width image width
     * @param height image height
     * @return placeholder ImageIcon
     */
    private static ImageIcon createPlaceholderIcon(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        
        // Background
        g2.setColor(new Color(240, 240, 240));
        g2.fillRect(0, 0, width, height);
        
        // Border
        g2.setColor(new Color(200, 200, 200));
        g2.drawRect(0, 0, width - 1, height - 1);
        
        // Icon symbol
        g2.setColor(new Color(180, 180, 180));
        int iconSize = Math.min(width, height) / 3;
        int x = (width - iconSize) / 2;
        int y = (height - iconSize) / 2 - 10;
        g2.fillRect(x, y, iconSize, iconSize);
        
        // Text
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        String text = "No Image";
        FontMetrics fm = g2.getFontMetrics();
        int textX = (width - fm.stringWidth(text)) / 2;
        int textY = y + iconSize + 20;
        g2.drawString(text, textX, textY);
        
        g2.dispose();
        return new ImageIcon(image);
    }
    
    /**
     * Clears the image cache to free memory.
     */
    public static void clearCache() {
        imageCache.clear();
    }
    
    /**
     * Preloads all product images for a given category.
     * Useful for improving performance when displaying catalogs.
     * 
     * @param category the product category
     */
    public static void preloadCategoryImages(String category) {
        String categoryPath = category.toLowerCase().replace(" & ", "&").replace(" ", "");
        String[] commonImages = {
            "frenchbaguette.jpg", "pandesalclassic.jpg", "ubepandesal.jpg",
            "chocolatecroissant.jpg", "ensaymada.jpg", "cinnamonroll.jpg",
            "chocolatecakeslice.jpg", "cheesecakeslice.jpg",
            "freshbrewedcoffee.jpg", "cappuccino.jpg"
        };
        
        for (String image : commonImages) {
            String path = categoryPath + "/" + image;
            loadProductImage(path);
        }
    }
    
    /**
     * Gets the number of cached images.
     * 
     * @return cache size
     */
    public static int getCacheSize() {
        return imageCache.size();
    }
    
    /**
     * Creates a product card image with border and shadow effect.
     * 
     * @param imagePath the product image path
     * @param width card width
     * @param height card height
     * @return styled product card ImageIcon
     */
    public static ImageIcon createProductCard(String imagePath, int width, int height) {
        ImageIcon productIcon = loadProductImage(imagePath, width - 10, height - 10);
        
        BufferedImage card = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = card.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Shadow
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fillRoundRect(3, 3, width - 6, height - 6, 10, 10);
        
        // White background
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, width - 6, height - 6, 10, 10);
        
        // Image
        g2.drawImage(productIcon.getImage(), 5, 5, null);
        
        // Border
        g2.setColor(new Color(220, 220, 220));
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(0, 0, width - 6, height - 6, 10, 10);
        
        g2.dispose();
        return new ImageIcon(card);
    }
}
