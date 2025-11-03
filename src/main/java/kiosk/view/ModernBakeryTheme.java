package kiosk.view;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Font;

/**
 * Custom FlatLaf theme for SweetBatterBakeshop Kiosk
 * Extends FlatLightLaf with elegant, modern pastry-inspired colors
 */
public class ModernBakeryTheme extends FlatLightLaf {
    
    public static boolean setup() {
        // Install custom FlatLaf theme
        FlatLaf.registerCustomDefaultsSource("kiosk.view");
        
        // Set custom properties before installing Look and Feel
        UIManager.put("Button.arc", 12);  // Rounded corners for buttons
        UIManager.put("Component.arc", 8);  // Rounded corners for components
        UIManager.put("TextComponent.arc", 8);  // Rounded corners for text fields
        UIManager.put("ProgressBar.arc", 12);  // Rounded progress bars
        UIManager.put("ScrollBar.thumbArc", 999);  // Circular scrollbar thumbs
        UIManager.put("ScrollBar.thumbInsets", new java.awt.Insets(2, 2, 2, 2));
        
        // SweetBatterBakeshop color scheme
        UIManager.put("Component.focusWidth", 2);
        
        // Primary colors - Elegant pastry tones
        UIManager.put("Button.background", BakeryTheme.BUTTON_PRIMARY);
        UIManager.put("Button.foreground", BakeryTheme.TEXT_DARK);
        UIManager.put("Button.hoverBackground", BakeryTheme.BUTTON_PRIMARY_HOVER);
        UIManager.put("Button.hoverForeground", BakeryTheme.TEXT_WHITE);
        UIManager.put("Button.pressedBackground", BakeryTheme.PRIMARY_DARK);
        UIManager.put("Button.pressedForeground", BakeryTheme.TEXT_WHITE);
        UIManager.put("Button.selectedBackground", BakeryTheme.PRIMARY_COLOR);
        UIManager.put("Button.disabledBackground", BakeryTheme.lighter(BakeryTheme.BACKGROUND_COLOR, 0.5f));
        UIManager.put("Button.borderColor", BakeryTheme.BORDER_LIGHT);
        UIManager.put("Button.focusedBorderColor", BakeryTheme.ACCENT_COLOR);
        
        // Toggle/Radio buttons
        UIManager.put("ToggleButton.background", BakeryTheme.CARD_COLOR);
        UIManager.put("ToggleButton.selectedBackground", BakeryTheme.ACCENT_LIGHT);
        UIManager.put("ToggleButton.selectedForeground", BakeryTheme.TEXT_DARK);
        
        // Text components - White background with warm grey borders
        UIManager.put("TextField.background", BakeryTheme.CARD_COLOR);
        UIManager.put("TextField.foreground", BakeryTheme.TEXT_DARK);
        UIManager.put("TextField.selectionBackground", BakeryTheme.PRIMARY_COLOR);
        UIManager.put("TextField.selectionForeground", BakeryTheme.TEXT_DARK);
        UIManager.put("TextField.caretForeground", BakeryTheme.ACCENT_COLOR);
        UIManager.put("TextField.placeholderForeground", BakeryTheme.TEXT_LIGHT);
        UIManager.put("TextField.border", javax.swing.BorderFactory.createLineBorder(BakeryTheme.BORDER_LIGHT));
        
        UIManager.put("TextArea.background", BakeryTheme.CARD_COLOR);
        UIManager.put("TextArea.foreground", BakeryTheme.TEXT_DARK);
        UIManager.put("TextArea.selectionBackground", BakeryTheme.PRIMARY_COLOR);
        
        UIManager.put("PasswordField.background", BakeryTheme.CARD_COLOR);
        UIManager.put("PasswordField.foreground", BakeryTheme.TEXT_DARK);
        
        // Panels and containers - Soft White background
        UIManager.put("Panel.background", BakeryTheme.BACKGROUND_COLOR);
        UIManager.put("Panel.foreground", BakeryTheme.TEXT_DARK);
        
        // Scrollbar - modern minimal style with new colors
        UIManager.put("ScrollBar.track", BakeryTheme.BACKGROUND_COLOR);
        UIManager.put("ScrollBar.thumb", BakeryTheme.PRIMARY_COLOR);
        UIManager.put("ScrollBar.hoverThumbColor", BakeryTheme.ACCENT_COLOR);
        UIManager.put("ScrollBar.pressedThumbColor", BakeryTheme.PRIMARY_DARK);
        UIManager.put("ScrollBar.width", 12);
        UIManager.put("ScrollBar.thumbInsets", new java.awt.Insets(2, 2, 2, 2));
        UIManager.put("ScrollBar.track", BakeryTheme.BACKGROUND_COLOR);
        
        // Table
        UIManager.put("Table.background", BakeryTheme.CARD_COLOR);
        UIManager.put("Table.foreground", BakeryTheme.TEXT_DARK);
        UIManager.put("Table.selectionBackground", BakeryTheme.PRIMARY_COLOR);
        UIManager.put("Table.selectionForeground", BakeryTheme.TEXT_DARK);
        UIManager.put("Table.gridColor", BakeryTheme.BORDER_LIGHT);
        UIManager.put("Table.alternateRowColor", BakeryTheme.PRIMARY_LIGHT);
        UIManager.put("TableHeader.background", BakeryTheme.PRIMARY_DARK);
        UIManager.put("TableHeader.foreground", BakeryTheme.TEXT_WHITE);
        UIManager.put("TableHeader.separatorColor", BakeryTheme.BORDER_MEDIUM);
        
        // Menu and MenuBar - Cocoa Brown or Frosted Pink
        UIManager.put("MenuBar.background", BakeryTheme.PRIMARY_DARK);
        UIManager.put("MenuBar.foreground", BakeryTheme.TEXT_WHITE);
        UIManager.put("Menu.background", BakeryTheme.PRIMARY_DARK);
        UIManager.put("Menu.foreground", BakeryTheme.TEXT_WHITE);
        UIManager.put("Menu.selectionBackground", BakeryTheme.PRIMARY_COLOR);
        UIManager.put("MenuItem.background", BakeryTheme.CARD_COLOR);
        UIManager.put("MenuItem.foreground", BakeryTheme.TEXT_DARK);
        UIManager.put("MenuItem.selectionBackground", BakeryTheme.PRIMARY_COLOR);
        
        // ComboBox
        UIManager.put("ComboBox.background", BakeryTheme.CARD_COLOR);
        UIManager.put("ComboBox.foreground", BakeryTheme.TEXT_DARK);
        UIManager.put("ComboBox.selectionBackground", BakeryTheme.PRIMARY_COLOR);
        UIManager.put("ComboBox.selectionForeground", BakeryTheme.TEXT_DARK);
        UIManager.put("ComboBox.buttonBackground", BakeryTheme.PRIMARY_COLOR);
        
        // List
        UIManager.put("List.background", BakeryTheme.CARD_COLOR);
        UIManager.put("List.foreground", BakeryTheme.TEXT_DARK);
        UIManager.put("List.selectionBackground", BakeryTheme.PRIMARY_COLOR);
        UIManager.put("List.selectionForeground", BakeryTheme.TEXT_DARK);
        
        // Progress Bar
        UIManager.put("ProgressBar.background", BakeryTheme.BACKGROUND_COLOR);
        UIManager.put("ProgressBar.foreground", BakeryTheme.ACCENT_LIGHT);
        UIManager.put("ProgressBar.selectionBackground", BakeryTheme.TEXT_WHITE);
        UIManager.put("ProgressBar.selectionForeground", BakeryTheme.TEXT_DARK);
        
        // Separator
        UIManager.put("Separator.foreground", BakeryTheme.BORDER_LIGHT);
        
        // TabbedPane
        UIManager.put("TabbedPane.background", BakeryTheme.BACKGROUND_COLOR);
        UIManager.put("TabbedPane.foreground", BakeryTheme.TEXT_DARK);
        UIManager.put("TabbedPane.selectedBackground", BakeryTheme.CARD_COLOR);
        UIManager.put("TabbedPane.selectedForeground", BakeryTheme.ACCENT_COLOR);
        UIManager.put("TabbedPane.hoverColor", BakeryTheme.PRIMARY_COLOR);
        UIManager.put("TabbedPane.focusColor", BakeryTheme.ACCENT_COLOR);
        UIManager.put("TabbedPane.underlineColor", BakeryTheme.ACCENT_COLOR);
        UIManager.put("TabbedPane.contentAreaColor", BakeryTheme.PRIMARY_COLOR);
        
        // ToolTip
        UIManager.put("ToolTip.background", BakeryTheme.PRIMARY_DARK);
        UIManager.put("ToolTip.foreground", BakeryTheme.TEXT_WHITE);
        UIManager.put("ToolTip.border", new javax.swing.border.EmptyBorder(4, 8, 4, 8));
        
        // CheckBox and RadioButton
        UIManager.put("CheckBox.icon.checkmarkColor", BakeryTheme.TEXT_WHITE);
        UIManager.put("CheckBox.icon.background", BakeryTheme.CARD_COLOR);
        UIManager.put("CheckBox.icon.selectedBackground", BakeryTheme.ACCENT_LIGHT);
        UIManager.put("CheckBox.icon.focusedBackground", BakeryTheme.PRIMARY_COLOR);
        
        UIManager.put("RadioButton.icon.centerDiameter", 8);
        UIManager.put("RadioButton.icon.background", BakeryTheme.CARD_COLOR);
        UIManager.put("RadioButton.icon.selectedBackground", BakeryTheme.ACCENT_LIGHT);
        
        // Spinner
        UIManager.put("Spinner.background", BakeryTheme.CARD_COLOR);
        UIManager.put("Spinner.foreground", BakeryTheme.TEXT_DARK);
        UIManager.put("Spinner.buttonBackground", BakeryTheme.PRIMARY_COLOR);
        
        // Slider
        UIManager.put("Slider.trackColor", BakeryTheme.BORDER_LIGHT);
        UIManager.put("Slider.thumbColor", BakeryTheme.ACCENT_COLOR);
        UIManager.put("Slider.focusedColor", BakeryTheme.PRIMARY_COLOR);
        
        // Font settings - Using Segoe UI for modern look
        Font baseFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font boldFont = new Font("Segoe UI", Font.BOLD, 14);
        
        UIManager.put("defaultFont", baseFont);
        UIManager.put("Button.font", boldFont);
        UIManager.put("Label.font", baseFont);
        UIManager.put("TextField.font", baseFont);
        UIManager.put("TextArea.font", baseFont);
        UIManager.put("Table.font", baseFont);
        UIManager.put("TableHeader.font", boldFont);
        UIManager.put("Menu.font", boldFont);
        UIManager.put("MenuItem.font", baseFont);
        
        return true;
    }
    
    @Override
    public String getName() {
        return "SweetBatterBakeshop Theme";
    }
    
    @Override
    public String getDescription() {
        return "An elegant, modern theme inspired by fine pastries with soft, sophisticated colors";
    }
    
    @Override
    public boolean isDark() {
        return false;
    }
}


