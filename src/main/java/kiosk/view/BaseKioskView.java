package main.java.kiosk.view;

import javax.swing.*;

import main.java.kiosk.controller.CartController;
import main.java.kiosk.controller.InventoryController;

import java.awt.*;

/**
 * Base class for all kiosk page views
 * Provides common functionality for navigation and cart management
 */
public abstract class BaseKioskView extends JPanel {
    // Modern Bakery Theme Colors
    protected static final Color PRIMARY_COLOR = BakeryTheme.PRIMARY_COLOR;
    protected static final Color ACCENT_COLOR = BakeryTheme.ACCENT_COLOR;
    protected static final Color BACKGROUND_COLOR = BakeryTheme.BACKGROUND_COLOR;
    protected static final Color CARD_COLOR = BakeryTheme.CARD_COLOR;
    protected static final Color TEXT_DARK = BakeryTheme.TEXT_DARK;
    protected static final Color SUCCESS_COLOR = BakeryTheme.SUCCESS;
    protected static final Color WARNING_COLOR = BakeryTheme.WARNING;
    protected static final Color DANGER_COLOR = BakeryTheme.ERROR;
    
    protected static final Font TITLE_FONT = BakeryTheme.TITLE_FONT;
    protected static final Font SUBTITLE_FONT = BakeryTheme.SUBTITLE_FONT;
    protected static final Font HEADING_FONT = BakeryTheme.HEADER_FONT;
    protected static final Font REGULAR_FONT = BakeryTheme.REGULAR_FONT;
    protected static final Font BUTTON_FONT = BakeryTheme.BUTTON_FONT;
    protected static final Font SMALL_FONT = BakeryTheme.SMALL_FONT;
    
    protected InventoryController inventoryController;
    protected CartController cartController;
    
    public BaseKioskView() {
        this.inventoryController = InventoryController.getInstance();
        this.cartController = CartController.getInstance();
        setBackground(BACKGROUND_COLOR);
    }
    
    /**
     * Create a styled button with consistent appearance
     */
    protected JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 50));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    /**
     * Create a styled panel with border
     */
    protected JPanel createCard() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        return panel;
    }
    
    /**
     * Create a label with specific style
     */
    protected JLabel createStyledLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }
    
    /**
     * Update cart count display
     */
    protected void updateCartCount(JLabel cartCountLabel) {
        int itemCount = cartController.getTotalItems();
        cartCountLabel.setText("Cart (" + itemCount + ")");
    }
    
    /**
     * Show success message
     */
    protected void showSuccessMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Show error message
     */
    protected void showErrorMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Show confirmation dialog
     */
    protected boolean showConfirmDialog(Component parent, String message) {
        int result = JOptionPane.showConfirmDialog(parent, message, "Confirm",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }
    
    /**
     * Format currency
     */
    protected String formatCurrency(double amount) {
        return String.format("₱%.2f", amount);
    }
    
    /**
     * Refresh the view - to be implemented by subclasses
     */
    public abstract void refresh();
}
