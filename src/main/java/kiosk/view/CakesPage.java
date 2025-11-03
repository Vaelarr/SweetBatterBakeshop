package kiosk.view;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

import kiosk.util.CartManager;
import kiosk.util.HelpRequestManager;
import kiosk.database.dao.InventoryDAO;
import kiosk.model.InventoryItem;

public class CakesPage extends JPanel implements KioskPage {
    private JPanel productPanel;
    private Map<String, List<String>> products;
    private Map<String, List<Double>> prices;
    private Map<String, List<String>> images; // Map to store image filenames
    private InventoryDAO inventoryDAO;
    private JButton cakeSlicesButton, cupcakesButton, customCakesButton, specialtyButton;
    private JButton activeButton;
    private JLabel cartCountLabel;
    private JTextField searchBar;
    private String currentCategory = "Cakes & Special Occasions";
    private final DecimalFormat priceFormat = new DecimalFormat("0.00");
    
    // Modern Bakery Theme Colors
    private final Color PRIMARY_COLOR = BakeryTheme.PRIMARY_COLOR;
    private final Color ACCENT_COLOR = BakeryTheme.ACCENT_COLOR;
    private final Color BACKGROUND_COLOR = BakeryTheme.BACKGROUND_COLOR;
    private final Color CARD_COLOR = BakeryTheme.CARD_COLOR;
    private final Color TEXT_DARK = BakeryTheme.TEXT_DARK;
    private final Color TEXT_LIGHT = BakeryTheme.TEXT_WHITE;
    private final Font TITLE_FONT = BakeryTheme.TITLE_FONT;
    private final Font SUBTITLE_FONT = BakeryTheme.SUBTITLE_FONT;
    private final Font REGULAR_FONT = BakeryTheme.REGULAR_FONT;
    private final Font SMALL_FONT = BakeryTheme.SMALL_FONT;

    private KioskMainPage parent;

    public CakesPage(KioskMainPage parent) {
        this.parent = parent;
        this.inventoryDAO = new InventoryDAO();
        setLayout(new BorderLayout(0, 0));
        setBackground(BACKGROUND_COLOR);

        initProducts();
        JPanel topPanel = createTopPanel();
        JPanel headerPanel = createHeaderPanel();
        JScrollPane productScrollPane = createProductScrollPane();
        
        // Assemble the UI
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(headerPanel, BorderLayout.CENTER);
        
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        contentPanel.add(productScrollPane, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        
        // Initial display
        showProducts("Cakes & Special Occasions");
        
        setVisible(true);
    }

    private void initProducts() {
        products = new HashMap<>();
        prices = new HashMap<>();
        images = new HashMap<>();
        
        // Define category mappings for cakes
        String[] cakeCategories = {
            "Cakes & Special Occasions"
        };

        // Retrieve items from database for each category
        for (String category : cakeCategories) {
            List<InventoryItem> items = inventoryDAO.getByCategory(category);
            System.out.println("DEBUG CakesPage: Category '" + category + "' has " + items.size() + " items");
            
            List<String> productNames = new ArrayList<>();
            List<Double> productPrices = new ArrayList<>();
            List<String> productImages = new ArrayList<>();
            
            for (InventoryItem item : items) {
                System.out.println("  - " + item.getName() + " ($" + item.getPrice() + ")");
                productNames.add(item.getName());
                productPrices.add(item.getPrice());
                // Generate a default image filename based on product name
                String imageName = item.getName().toLowerCase()
                    .replaceAll("[^a-z0-9]+", "") + ".jpg";
                productImages.add(imageName);
            }
            
            products.put(category, productNames);
            prices.put(category, productPrices);
            images.put(category, productImages);
        }
        
        // If no items found in database, log a warning
        if (products.isEmpty() || products.values().stream().allMatch(List::isEmpty)) {
            System.out.println("Warning: No cake items found in database. Please ensure inventory is populated.");
        }
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PRIMARY_COLOR);
        topPanel.setPreferredSize(new Dimension(1100, 70));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Left section with search
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        leftPanel.setOpaque(false);
        
        searchBar = new JTextField(25);
        searchBar.setPreferredSize(new Dimension(280, 45));
        searchBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BakeryTheme.BORDER_LIGHT, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        searchBar.setText("Search products...");
        searchBar.setForeground(Color.GRAY);
        searchBar.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        searchBar.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchBar.getText().equals("Search products...")) {
                    searchBar.setText("");
                    searchBar.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (searchBar.getText().isEmpty()) {
                    searchBar.setText("Search products...");
                    searchBar.setForeground(Color.GRAY);
                }
            }
        });
        
        searchBar.addActionListener(e -> {
            String query = searchBar.getText().trim().toLowerCase();
            if (query.isEmpty() || query.equals("search products...")) {
                showProducts(currentCategory);
            } else {
                filterProducts(query);
            }
        });
        
        JButton searchButton = createIconButton("🔍", "Search");
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        searchButton.setPreferredSize(new Dimension(50, 45));
        searchButton.addActionListener(e -> {
            String query = searchBar.getText().trim().toLowerCase();
            if (query.isEmpty() || query.equals("search products...")) {
                showProducts(currentCategory);
            } else {
                filterProducts(query);
            }
        });
        
        leftPanel.add(searchBar);
        leftPanel.add(searchButton);
        
        // Right section with back, help and cart
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        rightPanel.setOpaque(false);
        
        JButton backButton = createIconButton("←", "Back");
        backButton.setToolTipText("Return to main menu");
        backButton.setPreferredSize(new Dimension(50, 45));
        backButton.addActionListener(e -> {
            if (parent != null) {
                parent.showMainPage();
            }
        });
        
        JButton helpButton = createIconButton("❓", "Help");
        helpButton.setToolTipText("Ask for assistance");
        helpButton.setPreferredSize(new Dimension(50, 45));
        helpButton.addActionListener(e -> showHelpRequestDialog());
        
        JPanel cartPanel = new JPanel(new BorderLayout(5, 0));
        cartPanel.setOpaque(false);
        
        JButton cartButton = createIconButton("🛒", "Cart");
        cartButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        cartButton.setPreferredSize(new Dimension(50, 45));
        
        cartCountLabel = new JLabel("0");
        cartCountLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        cartCountLabel.setForeground(Color.WHITE);
        cartCountLabel.setOpaque(true);
        cartCountLabel.setBackground(BakeryTheme.ERROR);
        cartCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cartCountLabel.setPreferredSize(new Dimension(28, 28));
        cartCountLabel.setBorder(new LineBorder(Color.WHITE, 2, true));
        updateCartCount();
        
        cartPanel.add(cartButton, BorderLayout.WEST);
        cartPanel.add(cartCountLabel, BorderLayout.EAST);
        
        cartButton.addActionListener(e -> {
            if (parent != null) {
                parent.showCartPage();
            }
        });
        
        rightPanel.add(backButton);
        rightPanel.add(helpButton);
        rightPanel.add(cartPanel);
            
        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);
        
        return topPanel;
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ACCENT_COLOR);
        headerPanel.setPreferredSize(new Dimension(1100, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        
        JLabel titleLabel = new JLabel("Cakes & Special Occasions", SwingConstants.LEFT);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        return headerPanel;
    }
    
    private JPanel createSubcategoryPanel() {
        JPanel subcategoryPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        subcategoryPanel.setOpaque(false);
        subcategoryPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // All subcategories now point to the same main category
        cakeSlicesButton = createCategoryButton("Cakes & Special Occasions", "🍰");
        cupcakesButton = createCategoryButton("Cakes & Special Occasions", "🧁");
        customCakesButton = createCategoryButton("Cakes & Special Occasions", "🎂");
        specialtyButton = createCategoryButton("Cakes & Special Occasions", "🍮");
        
        subcategoryPanel.add(cakeSlicesButton);
        subcategoryPanel.add(cupcakesButton);
        subcategoryPanel.add(customCakesButton);
        subcategoryPanel.add(specialtyButton);
        
        return subcategoryPanel;
    }
    
    private JScrollPane createProductScrollPane() {
        productPanel = new JPanel();
        productPanel.setLayout(new GridLayout(0, 3, 20, 20));
        productPanel.setBackground(BACKGROUND_COLOR);
        
        JScrollPane scrollPane = new JScrollPane(productPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(BACKGROUND_COLOR);
        
        return scrollPane;
    }
    
    private JButton createIconButton(String icon, String tooltip) {
        JButton button = new JButton(icon);
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(8, 8, 8, 8));
        return button;
    }
    
    private JButton createCategoryButton(String text, String icon) {
        JButton button = new JButton(icon + " " + text);
        button.setForeground(TEXT_DARK);
        button.setBackground(CARD_COLOR);
        button.setFont(SUBTITLE_FONT);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(
            new LineBorder(BakeryTheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addActionListener(e -> {
            currentCategory = text;
            showProducts(text);
            highlightButton(button);
        });
        
        return button;
    }

    private void showProducts(String category) {
        productPanel.removeAll();
        
        String actualCategory = null;
        for (String key : products.keySet()) {
            if (key.equalsIgnoreCase(category)) {
                actualCategory = key;
                break;
            }
        }
        
        if (actualCategory == null) {
            JLabel noProductsLabel = new JLabel("No products found for category: " + category);
            noProductsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            noProductsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            productPanel.setLayout(new BorderLayout());
            productPanel.add(noProductsLabel, BorderLayout.CENTER);
        } else {
            productPanel.setLayout(new GridLayout(0, 3, 20, 20));
            
            List<String> items = products.get(actualCategory);
            List<Double> itemPrices = prices.get(actualCategory);
            List<String> itemImages = images.get(actualCategory); // Get images for the category
            
            if (items != null && itemPrices != null && items.size() == itemPrices.size()) {
                for (int i = 0; i < items.size(); i++) {
                    String itemName = items.get(i);
                    double itemPrice = itemPrices.get(i);
                    String itemImage = itemImages != null && i < itemImages.size() ? itemImages.get(i) : null;
                                
                    JPanel productCard = createProductCard(itemName, itemPrice, itemImage);
                    productPanel.add(productCard);
                }
            }
        }
        
        productPanel.revalidate();
        productPanel.repaint();
    }
    
    private void filterProducts(String query) {
        productPanel.removeAll();
        
        int resultCount = 0;
        
        for (String category : products.keySet()) {
            List<String> items = products.get(category);
            List<Double> itemPrices = prices.get(category);
            List<String> itemImages = images.get(category); // Get images for the category
            
            for (int i = 0; i < items.size(); i++) {
                String itemName = items.get(i);
                
                if (itemName.toLowerCase().contains(query)) {
                    double itemPrice = itemPrices.get(i);
                    String itemImage = itemImages != null && i < itemImages.size() ? itemImages.get(i) : null;
                    JPanel productCard = createProductCard(itemName, itemPrice, itemImage);
                    productPanel.add(productCard);
                    resultCount++;
                }
            }
        }
        
        if (resultCount == 0) {
            JPanel noResultsPanel = new JPanel();
            noResultsPanel.setLayout(new BoxLayout(noResultsPanel, BoxLayout.Y_AXIS));
            noResultsPanel.setBackground(BACKGROUND_COLOR);
            noResultsPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
            
            JLabel iconLabel = new JLabel("🔎");
            iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 48));
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel messageLabel = new JLabel("No products found for \"" + query + "\"");
            messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JButton clearButton = new JButton("Clear Search");
            clearButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            clearButton.addActionListener(e -> {
                searchBar.setText("Search products...");
                searchBar.setForeground(Color.GRAY);
                showProducts(currentCategory);
            });
            
            noResultsPanel.add(iconLabel);
            noResultsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            noResultsPanel.add(messageLabel);
            noResultsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            noResultsPanel.add(clearButton);
                
            productPanel.setLayout(new BorderLayout());
            productPanel.add(noResultsPanel, BorderLayout.CENTER);
        } else {
            productPanel.setLayout(new GridLayout(0, 3, 20, 20));
        }
        
        productPanel.revalidate();
        productPanel.repaint();
    }

    private JPanel createProductCard(String itemName, double itemPrice, String itemImage) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(0, 0));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(0, 0, 10, 0)
        ));
        
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(CARD_COLOR);
        imagePanel.setPreferredSize(new Dimension(150, 150));
          JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        if (itemImage != null && !itemImage.isEmpty()) {
            ImageIcon icon = loadImage(itemImage);
            if (icon != null) {
                // Resize the image to fit nicely in the product card
                Image img = icon.getImage();
                Image resizedImg = img.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(resizedImg));
            } else {
                // If image loading fails, create a placeholder
                createPlaceholderImage(imageLabel, itemName);
            }
        } else {
            // If no image is specified, create a placeholder
            createPlaceholderImage(imageLabel, itemName);
        }
        
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(CARD_COLOR);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
        
        JLabel nameLabel = new JLabel(itemName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setForeground(TEXT_DARK);
        
        JLabel priceLabel = new JLabel("₱" + priceFormat.format(itemPrice));
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        priceLabel.setForeground(new Color(26, 188, 156));
        
        int currentQty = CartManager.getItemQuantity(itemName);
        
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controlsPanel.setBackground(CARD_COLOR);
        
        JButton decrementBtn = new JButton("-");
        decrementBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        decrementBtn.setFocusPainted(false);
        decrementBtn.setPreferredSize(new Dimension(45, 40));
        decrementBtn.setEnabled(currentQty > 0);
        decrementBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        decrementBtn.setMargin(new Insets(5, 10, 5, 10));
        
        JLabel quantityLabel = new JLabel(String.valueOf(currentQty));
        quantityLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        quantityLabel.setHorizontalAlignment(SwingConstants.CENTER);
        quantityLabel.setPreferredSize(new Dimension(50, 40));
        quantityLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 0, 5, 0)
        ));
        
        JButton incrementBtn = new JButton("+");
        incrementBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        incrementBtn.setFocusPainted(false);
        incrementBtn.setPreferredSize(new Dimension(45, 40));
        incrementBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        incrementBtn.setMargin(new Insets(5, 10, 5, 10));
        
        controlsPanel.add(decrementBtn);
        controlsPanel.add(quantityLabel);
        controlsPanel.add(incrementBtn);
        
        decrementBtn.addActionListener(e -> {
            CartManager.removeItem(itemName);
            updateProductCard(card, itemName, itemPrice);
            updateCartCount();
            // Update all cart counters after any cart change
            if (parent != null) parent.updateAllCartCounters();
        });

        incrementBtn.addActionListener(e -> {
            CartManager.addItem(itemName, itemPrice);
            updateProductCard(card, itemName, itemPrice);
            updateCartCount();
            // Update all cart counters after any cart change
            if (parent != null) parent.updateAllCartCounters();
            showAddToCartFeedback(itemName);
        });
        
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(controlsPanel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            
        card.add(imagePanel, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void showAddToCartFeedback(String itemName) {
        JWindow notification = new JWindow(SwingUtilities.getWindowAncestor(this));
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(50, 50, 50, 220));
        content.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel messageLabel = new JLabel(itemName + " added to cart");
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        content.add(messageLabel, BorderLayout.CENTER);
        
        notification.setContentPane(content);
        notification.pack();
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        notification.setLocation(
            screenSize.width - notification.getWidth() - 20,
            screenSize.height - notification.getHeight() - 50
        );
        
        notification.setVisible(true);
        
        new javax.swing.Timer(1500, e -> notification.dispose()).start();
    }    private void updateProductCard(JPanel card, String itemName, double itemPrice) {
        Container parent = card.getParent();
        int index = -1;
        for (int i = 0; i < parent.getComponentCount(); i++) {
            if (parent.getComponent(i) == card) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            parent.remove(card);
            
            // Find the image filename for this product
            String imageFileName = null;
            for (String category : products.keySet()) {
                List<String> items = products.get(category);
                int itemIndex = items.indexOf(itemName);
                if (itemIndex >= 0) {
                    List<String> itemImages = images.get(category);
                    if (itemImages != null && itemIndex < itemImages.size()) {
                        imageFileName = itemImages.get(itemIndex);
                    }
                    break;
                }
            }
            
            parent.add(createProductCard(itemName, itemPrice, imageFileName), index);
            parent.revalidate();
            parent.repaint();
        }
        // After updating, also update all cart counters
        if (parent != null && this.parent != null) {
            this.parent.updateAllCartCounters();
        }
    }

    @Override
    public void backToMain() {
        if (parent != null) {
            parent.showMainPage();
        }
    }

    @Override
    public void updateCartCount() {
        int count = CartManager.getTotalItems();
        cartCountLabel.setText(String.valueOf(count));
        cartCountLabel.setVisible(count > 0);
    }

    private void highlightButton(JButton button) {
        if (activeButton != null) {
            activeButton.setBackground(CARD_COLOR);
            activeButton.setForeground(TEXT_DARK);
        }
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        activeButton = button;
    }

    private void showHelpRequestDialog() {
        JDialog helpDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Request Assistance", true);
        helpDialog.setSize(450, 350);
        helpDialog.setLocationRelativeTo(this);
        helpDialog.setLayout(new BorderLayout());
        
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel headerLabel = new JLabel("How can we help you?");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(PRIMARY_COLOR);
        
        JPanel formPanel = new JPanel(new GridLayout(3, 1, 0, 15));
        formPanel.setBackground(BACKGROUND_COLOR);
        
        JPanel issuePanel = new JPanel(new BorderLayout(0, 5));
        issuePanel.setBackground(BACKGROUND_COLOR);
        JLabel issueLabel = new JLabel("Type of Assistance:");
        issueLabel.setFont(REGULAR_FONT);
        
        String[] issueTypes = {
            "Product Recommendation",
            "Product Information",
            "Find a Specific Product",
            "Price Inquiry",
            "Other"
        };
        
        JComboBox<String> issueComboBox = new JComboBox<>(issueTypes);
        issueComboBox.setFont(REGULAR_FONT);
        
        issuePanel.add(issueLabel, BorderLayout.NORTH);
        issuePanel.add(issueComboBox, BorderLayout.CENTER);
        
        JPanel detailsPanel = new JPanel(new BorderLayout(0, 5));
        detailsPanel.setBackground(BACKGROUND_COLOR);
        JLabel detailsLabel = new JLabel("Additional Details (optional):");
        detailsLabel.setFont(REGULAR_FONT);
        
        JTextArea detailsArea = new JTextArea(4, 20);
        detailsArea.setFont(REGULAR_FONT);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        JScrollPane detailsScrollPane = new JScrollPane(detailsArea);
        
        detailsPanel.add(detailsLabel, BorderLayout.NORTH);
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        JPanel urgencyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        urgencyPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel urgencyLabel = new JLabel("Is this urgent?");
        urgencyLabel.setFont(REGULAR_FONT);
        
        JCheckBox urgentCheckBox = new JCheckBox("Yes, I need immediate assistance");
        urgentCheckBox.setFont(REGULAR_FONT);
        urgentCheckBox.setBackground(BACKGROUND_COLOR);
        
        urgencyPanel.add(urgencyLabel);
        urgencyPanel.add(urgentCheckBox);
        
        formPanel.add(issuePanel);
        formPanel.add(detailsPanel);
        formPanel.add(urgencyPanel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(REGULAR_FONT);
        cancelButton.addActionListener(e -> helpDialog.dispose());
        
        JButton submitButton = new JButton("Request Help");
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitButton.setBackground(ACCENT_COLOR);
        submitButton.setForeground(Color.WHITE);
        submitButton.addActionListener(e -> {
            String issueType = (String) issueComboBox.getSelectedItem();
            String details = detailsArea.getText().trim();
            boolean isUrgent = urgentCheckBox.isSelected();
            
            if (isUrgent) {
                details = "[URGENT] " + details;
            }
            
            details += "\nCategory: " + currentCategory;
            
            HelpRequestManager.getInstance().submitRequest(
                "Personal Care & Hygiene", 
                issueType, 
                details
            );
            
            JOptionPane.showMessageDialog(helpDialog,
                "Your help request has been submitted.\nA staff member will assist you shortly.",
                "Help Request Submitted",
                JOptionPane.INFORMATION_MESSAGE);
                
            helpDialog.dispose();
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);
        
        contentPanel.add(headerLabel, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);
        
        helpDialog.add(contentPanel, BorderLayout.CENTER);
        helpDialog.add(buttonPanel, BorderLayout.SOUTH);
        helpDialog.setVisible(true);
    }

    private ImageIcon loadImage(String imageName) {
        URL imageUrl = getClass().getClassLoader().getResource("kiosk/resources/" + imageName);
        if (imageUrl != null) {
            return new ImageIcon(imageUrl);
        } else {
            // Try with lowercase filename as a fallback
            imageUrl = getClass().getClassLoader().getResource("kiosk/resources/" + imageName.toLowerCase());
            if (imageUrl != null) {
                return new ImageIcon(imageUrl);
            }

            // Try without the file extension
            int dotIndex = imageName.lastIndexOf('.');
            if (dotIndex > 0) {
                String nameWithoutExtension = imageName.substring(0, dotIndex);
                String[] extensions = {".jpg", ".png", ".gif"};
                for (String extension : extensions) {
                    imageUrl = getClass().getClassLoader().getResource("kiosk/resources/" + nameWithoutExtension + extension);
                    if (imageUrl != null) {
                        return new ImageIcon(imageUrl);
                    }
                }
            }
            
            System.out.println("Image not found: " + imageName);
            return null;
        }
    }

    private void createPlaceholderImage(JLabel imageLabel, String itemName) {
        JPanel placeholder = new JPanel(new BorderLayout());
        placeholder.setPreferredSize(new Dimension(120, 120));
        placeholder.setBackground(new Color(240, 240, 240));
        placeholder.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JLabel placeholderText = new JLabel(itemName.substring(0, 1).toUpperCase());
        placeholderText.setFont(new Font("SansSerif", Font.BOLD, 48));
        placeholderText.setForeground(new Color(150, 150, 150));
        placeholderText.setHorizontalAlignment(SwingConstants.CENTER);
        
        placeholder.add(placeholderText, BorderLayout.CENTER);
        imageLabel.setLayout(new BorderLayout());
        imageLabel.add(placeholder, BorderLayout.CENTER);
    }

}



