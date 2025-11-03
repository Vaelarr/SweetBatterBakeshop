package kiosk.view.pages;

import javax.swing.*;

import kiosk.model.InventoryItem;
import kiosk.view.BaseKioskView;
import kiosk.view.NavigationController;

import java.awt.*;
import java.util.List;

/**
 * Example view demonstrating the use of BaseKioskView
 * This shows how to create new views using the MVC infrastructure
 */
public class ExampleProductListView extends BaseKioskView {
    private NavigationController navigator;
    private JPanel productsPanel;
    private JLabel cartCountLabel;
    
    public ExampleProductListView(NavigationController navigator) {
        super(); // Initialize controllers from BaseKioskView
        this.navigator = navigator;
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header with title and cart
        add(createHeader(), BorderLayout.NORTH);
        
        // Products grid
        add(createProductsPanel(), BorderLayout.CENTER);
        
        // Footer with navigation
        add(createFooter(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeader() {
        JPanel header = createCard(); // From BaseKioskView
        header.setLayout(new BorderLayout());
        
        // Title
        JLabel titleLabel = createStyledLabel(
            "Products", TITLE_FONT, TEXT_DARK); // From BaseKioskView
        header.add(titleLabel, BorderLayout.WEST);
        
        // Cart button
        cartCountLabel = createStyledLabel(
            "Cart (0)", HEADING_FONT, PRIMARY_COLOR);
        updateCartCount(cartCountLabel); // From BaseKioskView
        
        JButton cartButton = createStyledButton("View Cart", ACCENT_COLOR);
        cartButton.addActionListener(e -> navigator.showCartPage());
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(cartCountLabel);
        rightPanel.add(cartButton);
        
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }
    
    private JScrollPane createProductsPanel() {
        productsPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        productsPanel.setBackground(BACKGROUND_COLOR);
        
        loadProducts();
        
        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        return scrollPane;
    }
    
    private void loadProducts() {
        productsPanel.removeAll();
        
        // Use inherited inventoryController from BaseKioskView
        List<InventoryItem> items = inventoryController.getItemsByCategory("Snacks");
        
        for (InventoryItem item : items) {
            JPanel productCard = createProductCard(item);
            productsPanel.add(productCard);
        }
        
        productsPanel.revalidate();
        productsPanel.repaint();
    }
    
    private JPanel createProductCard(InventoryItem item) {
        JPanel card = createCard(); // From BaseKioskView
        card.setLayout(new BorderLayout(10, 10));
        card.setPreferredSize(new Dimension(250, 200));
        
        // Product info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        
        JLabel nameLabel = createStyledLabel(
            item.getName(), HEADING_FONT, TEXT_DARK);
        JLabel priceLabel = createStyledLabel(
            formatCurrency(item.getPrice()), SUBTITLE_FONT, SUCCESS_COLOR);
        JLabel stockLabel = createStyledLabel(
            "Stock: " + item.getStockQuantity(), SMALL_FONT, TEXT_DARK);
        
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(stockLabel);
        
        card.add(infoPanel, BorderLayout.CENTER);
        
        // Add to cart button
        JButton addButton = createStyledButton("Add to Cart", PRIMARY_COLOR);
        addButton.setPreferredSize(new Dimension(200, 40));
        addButton.addActionListener(e -> addToCart(item));
        
        card.add(addButton, BorderLayout.SOUTH);
        
        return card;
    }
    
    private void addToCart(InventoryItem item) {
        // Use inherited cartController from BaseKioskView
        cartController.addItem(item.getName(), item.getPrice());
        
        // Update cart count
        updateCartCount(cartCountLabel);
        
        // Show success message using BaseKioskView method
        showSuccessMessage(this, item.getName() + " added to cart!");
    }
    
    private JPanel createFooter() {
        JPanel footer = createCard();
        footer.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        JButton backButton = createStyledButton("Back to Main", PRIMARY_COLOR);
        backButton.addActionListener(e -> navigator.showMainPage());
        
        JButton refreshButton = createStyledButton("Refresh", ACCENT_COLOR);
        refreshButton.addActionListener(e -> refresh());
        
        footer.add(backButton);
        footer.add(refreshButton);
        
        return footer;
    }
    
    @Override
    public void refresh() {
        // Refresh the product list
        loadProducts();
        
        // Update cart count
        updateCartCount(cartCountLabel);
        
        // Could also reload from database/file if needed
        // inventoryController.load();
    }
}


