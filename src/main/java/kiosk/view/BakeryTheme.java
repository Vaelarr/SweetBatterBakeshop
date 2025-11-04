package kiosk.view;

import java.awt.Color;
import java.awt.Font;

/**
 * SweetBatterBakeshop - Modern Color Palette
 * Vibrant, welcoming colors inspired by artisan bakeries and confections
 */
public class BakeryTheme {
    
    // Primary Colors - Warm and Inviting Bakery Palette
    public static final Color PRIMARY_COLOR = new Color(0xE8D0D3);          // Soft Blush Pink - gentle and inviting
    public static final Color PRIMARY_LIGHT = new Color(0xF5E9DB);          // Cream Beige - warm background
    public static final Color PRIMARY_DARK = new Color(0x8A4B2D);           // Warm Brown - premium chocolate
    
    // Accent Colors - Vibrant and Eye-catching
    public static final Color ACCENT_COLOR = new Color(0xD6A5AC);           // Muted Rose Pink - soft accent
    public static final Color ACCENT_LIGHT = new Color(0xF5E9DB);           // Cream Beige - soft accent
    public static final Color SECONDARY_ACCENT = new Color(0xCBB7A5);       // Light Mocha - subtle tones
    
    // Background Colors - Clean and modern
    public static final Color BACKGROUND_COLOR = new Color(0xE8D0D3);       // Soft Blush Pink - inviting base
    public static final Color CARD_COLOR = new Color(0xF5E9DB);             // Cream Beige - clean cards
    public static final Color SOFT_CREAM = new Color(0xF5E9DB);             // Cream Beige - subtle warmth
    
    // Text Colors - Rich and readable
    public static final Color TEXT_DARK = new Color(0x000000);              // Pure Black - primary text for maximum readability
    public static final Color TEXT_MEDIUM = new Color(0x2C2C2C);            // Very Dark Gray - secondary text
    public static final Color TEXT_LIGHT = new Color(0x7D6B60);             // Soft Taupe - subtle text/hints
    public static final Color TEXT_WHITE = new Color(0xFFFFFF);             // Pure White - light text
    
    // Button Colors - Eye-catching and interactive
    public static final Color BUTTON_PRIMARY = new Color(0x8A4B2D);         // Warm Brown - primary action
    public static final Color BUTTON_PRIMARY_HOVER = new Color(0xB57A5A);   // Lighter Warm Brown - hover state
    public static final Color BUTTON_SECONDARY = new Color(0xD6A5AC);       // Muted Rose Pink - secondary button
    public static final Color BUTTON_SUCCESS = new Color(0xB9C2A0);         // Sage Green - success actions
    public static final Color BUTTON_WARNING = new Color(0xFFB74D);         // Warm Orange - warnings
    
    // Category Colors - Distinctive per category
    public static final Color CATEGORY_BREAD = new Color(0xF4A460);         // Sandy Brown - warm bread tones
    public static final Color CATEGORY_PASTRY = new Color(0xFFB6C1);        // Light Pink - delicate pastries
    public static final Color CATEGORY_CAKE = new Color(0xFF69B4);          // Hot Pink - celebration cakes
    public static final Color CATEGORY_BEVERAGE = new Color(0x8B7355);      // Coffee Brown - beverages
    
    // Utility Colors
    public static final Color SUCCESS = new Color(0xB9C2A0);                // Sage Green - success
    public static final Color ERROR = new Color(0xE57373);                  // Soft Red - errors
    public static final Color WARNING = new Color(0xFFB74D);                // Warm Orange - warnings
    public static final Color INFO = new Color(0x64B5F6);                   // Sky Blue - information
    
    // Border and Shadow
    public static final Color BORDER_LIGHT = new Color(0xCBB7A5);           // Light Mocha - subtle borders
    public static final Color BORDER_MEDIUM = new Color(0xCBB7A5);          // Light Mocha - medium borders
    public static final Color SHADOW = new Color(0, 0, 0, 50);              // Deeper shadow for depth
    
    // Fonts - Modern, clear, and touch-friendly
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 48);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 32);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 18);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    public static final Font PRICE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font CATEGORY_FONT = new Font("Segoe UI", Font.BOLD, 24);
    
    // Spacing Constants - Touch-friendly spacing
    public static final int PADDING_SMALL = 8;
    public static final int PADDING_MEDIUM = 16;
    public static final int PADDING_LARGE = 24;
    public static final int PADDING_XLARGE = 32;
    
    public static final int BUTTON_HEIGHT = 55;
    public static final int CARD_BORDER_RADIUS = 12;
    public static final int BUTTON_BORDER_RADIUS = 8;
    
    // Prevent instantiation
    private BakeryTheme() {
        throw new AssertionError("Cannot instantiate BakeryTheme");
    }
    
    /**
     * Get a lighter version of a color (for hover effects)
     */
    public static Color lighter(Color color, float factor) {
        return new Color(
            Math.min((int)(color.getRed() + (255 - color.getRed()) * factor), 255),
            Math.min((int)(color.getGreen() + (255 - color.getGreen()) * factor), 255),
            Math.min((int)(color.getBlue() + (255 - color.getBlue()) * factor), 255),
            color.getAlpha()
        );
    }
    
    /**
     * Get a darker version of a color (for press effects)
     */
    public static Color darker(Color color, float factor) {
        return new Color(
            Math.max((int)(color.getRed() * (1 - factor)), 0),
            Math.max((int)(color.getGreen() * (1 - factor)), 0),
            Math.max((int)(color.getBlue() * (1 - factor)), 0),
            color.getAlpha()
        );
    }
}


