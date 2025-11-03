package kiosk.view;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import javax.swing.*; // Explicit import for java.util.List
import javax.swing.border.*;

import kiosk.util.CartManager;
import kiosk.util.HelpRequestManager;
import kiosk.database.dao.InventoryDAO;
import kiosk.model.InventoryItem;

public class BreadsPage extends JPanel implements KioskPage {
    private KioskMainPage parent;
    private JPanel productPanel;
    private InventoryDAO inventoryDAO;

    @Override
    public void backToMain() {
        if (parent != null) {
            parent.showMainPage();
        }
    }
    private Map<String, java.util.List<String>> products;
    private Map<String, java.util.List<Double>> prices;
    private JButton artisanBreadsButton, softRollsButton, specialtyBreadsButton, sweetBunsButton;
    private JButton activeButton;
    private JLabel cartCountLabel;
    private JTextField searchBar;
    private String currentCategory = "Breads & Rolls";
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

    public BreadsPage(KioskMainPage parent) {
        this.parent = parent;
        this.inventoryDAO = new InventoryDAO();
        setLayout(new BorderLayout(0, 0));
        setBackground(BACKGROUND_COLOR);

        initProducts();

        // Create main components
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
        showProducts("Breads & Rolls");
    }

    private void initProducts() {
        // Bakery product data
        products = new HashMap<>();
        prices = new HashMap<>();

        // Define category mappings for breads
        String[] breadCategories = {
            "Breads & Rolls"
        };

        // Retrieve items from database for each category
        for (String category : breadCategories) {
            List<InventoryItem> items = inventoryDAO.getByCategory(category);
            
            List<String> productNames = new ArrayList<>();
            List<Double> productPrices = new ArrayList<>();
            
            for (InventoryItem item : items) {
                productNames.add(item.getName());
                productPrices.add(item.getPrice());
            }
            
            products.put(category, productNames);
            prices.put(category, productPrices);
        }
        
        // If no items found in database, log a warning
        if (products.isEmpty() || products.values().stream().allMatch(List::isEmpty)) {
            System.out.println("Warning: No bread items found in database. Please ensure inventory is populated.");
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
        searchBar.setPreferredSize(new Dimension(280, 45));  // Increased height for touch
        searchBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(240, 240, 240), 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        searchBar.setText("Search products...");
        searchBar.setForeground(Color.GRAY);
        searchBar.setFont(new Font("Segoe UI", Font.PLAIN, 16));  // Increased font size
        
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
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 20));  // Increased font size
        searchButton.setPreferredSize(new Dimension(50, 45));  // Set specific button size
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
        
        JLabel titleLabel = new JLabel("Breads & Rolls", SwingConstants.LEFT);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        return headerPanel;
    }
    
    private JPanel createSubcategoryPanel() {
        JPanel subcategoryPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        subcategoryPanel.setOpaque(false);
        subcategoryPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        artisanBreadsButton = createCategoryButton("Breads & Rolls");
        softRollsButton = createCategoryButton("Breads & Rolls");
        specialtyBreadsButton = createCategoryButton("Breads & Rolls");
        sweetBunsButton = createCategoryButton("Breads & Rolls");
        
        subcategoryPanel.add(artisanBreadsButton);
        subcategoryPanel.add(softRollsButton);
        subcategoryPanel.add(specialtyBreadsButton);
        subcategoryPanel.add(sweetBunsButton);
        
        return subcategoryPanel;
    }
    
    private JScrollPane createProductScrollPane() {
        productPanel = new JPanel();
        productPanel.setLayout(new GridLayout(0, 3, 20, 20)); // Changed to 3 columns, variable rows
        productPanel.setBackground(BACKGROUND_COLOR);
        
        JScrollPane scrollPane = new JScrollPane(productPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smoother scrolling
        scrollPane.setBackground(BACKGROUND_COLOR);
        
        return scrollPane;
    }
    
    private JButton createIconButton(String icon, String tooltip) {
        JButton button = new JButton(icon);
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 20));  // Increased default font size
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(8, 8, 8, 8));  // Add more padding
        return button;
    }
    
    private JButton createCategoryButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(TEXT_DARK);
        button.setBackground(CARD_COLOR);
        button.setFont(SUBTITLE_FONT);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
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
        
        // Get correct category key from the map
        String actualCategory = null;
        for (String key : products.keySet()) {
            if (key.equalsIgnoreCase(category)) {
                actualCategory = key;
                break;
            }
        }
        
        if (actualCategory == null) {
            // Handle case where category is not found
            JLabel noProductsLabel = new JLabel("No products found for category: " + category);
            noProductsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            noProductsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            productPanel.setLayout(new BorderLayout());
            productPanel.add(noProductsLabel, BorderLayout.CENTER);
        } else {
            // Reset to grid layout if we're showing products
            productPanel.setLayout(new GridLayout(0, 3, 20, 20));
            
            List<String> items = products.get(actualCategory);
            List<Double> itemPrices = prices.get(actualCategory);
            
            if (items != null && itemPrices != null && items.size() == itemPrices.size()) {
                for (int i = 0; i < items.size(); i++) {
                    String itemName = items.get(i);
                    double itemPrice = itemPrices.get(i);
                    
                    JPanel productCard = createProductCard(itemName, itemPrice);
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
        
        // Search through all categories
        for (String category : products.keySet()) {
            List<String> items = products.get(category);
            List<Double> itemPrices = prices.get(category);
            
            for (int i = 0; i < items.size(); i++) {
                String itemName = items.get(i);
                
                if (itemName.toLowerCase().contains(query)) {
                    double itemPrice = itemPrices.get(i);
                    JPanel productCard = createProductCard(itemName, itemPrice);
                    productPanel.add(productCard);
                    resultCount++;
                }
            }
        }
        
        if (resultCount == 0) {
            // No results found
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
            // Ensure we're using the right layout
            productPanel.setLayout(new GridLayout(0, 3, 20, 20));
        }
        
        productPanel.revalidate();
        productPanel.repaint();
    }

    private JPanel createProductCard(String itemName, double itemPrice) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(0, 0));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(0, 0, 10, 0)
        ));
        
        // Image Panel
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(CARD_COLOR);
        imagePanel.setPreferredSize(new Dimension(150, 150));
        
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        String imageFileName = itemName.toLowerCase().replaceAll("[^a-z0-9]", "") + ".jpg";
        ImageIcon icon = loadImage(imageFileName);
        if (icon != null) {
            imageLabel.setIcon(new ImageIcon(icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH)));
        } else {
            // Create a placeholder with product name
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
        
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        // Info Panel
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
        priceLabel.setForeground(new Color(41, 128, 185));
        
        // Get the current quantity from cart
        int currentQty = CartManager.getItemQuantity(itemName);
        
        // Controls Panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controlsPanel.setBackground(CARD_COLOR);
        
        JButton decrementBtn = new JButton("-");
        decrementBtn.setFont(new Font("SansSerif", Font.BOLD, 18)); // Larger font
        decrementBtn.setFocusPainted(false);
        decrementBtn.setPreferredSize(new Dimension(45, 40)); // Larger button
        decrementBtn.setEnabled(currentQty > 0);
        decrementBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        decrementBtn.setMargin(new Insets(5, 10, 5, 10)); // Add more padding
        
        JLabel quantityLabel = new JLabel(String.valueOf(currentQty));
        quantityLabel.setFont(new Font("SansSerif", Font.BOLD, 18)); // Larger font
        quantityLabel.setHorizontalAlignment(SwingConstants.CENTER);
        quantityLabel.setPreferredSize(new Dimension(50, 40)); // Larger label
        quantityLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 0, 5, 0)
        ));
        
        JButton incrementBtn = new JButton("+");
        incrementBtn.setFont(new Font("SansSerif", Font.BOLD, 18)); // Larger font
        incrementBtn.setFocusPainted(false);
        incrementBtn.setPreferredSize(new Dimension(45, 40)); // Larger button
        incrementBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        incrementBtn.setMargin(new Insets(5, 10, 5, 10)); // Add more padding
        
        controlsPanel.add(decrementBtn);
        controlsPanel.add(quantityLabel);
        controlsPanel.add(incrementBtn);
        
        // Button Actions
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
        
        // Assemble the card
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
    
    /**
     * Shows a non-modal, auto-disappearing notification when an item is added to cart
     */
    private void showAddToCartFeedback(String itemName) {
        JWindow notification = new JWindow();
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(50, 50, 50, 220));
        content.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel messageLabel = new JLabel(itemName + " added to cart");
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        content.add(messageLabel, BorderLayout.CENTER);
        
        notification.setContentPane(content);
        notification.pack();
        
        // Position at bottom right of screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        notification.setLocation(
            screenSize.width - notification.getWidth() - 20,
            screenSize.height - notification.getHeight() - 50
        );
        
        notification.setVisible(true);
        
        // Auto-hide after 1.5 seconds
        new javax.swing.Timer(1500, e -> notification.dispose()).start();
    }

    private void updateProductCard(JPanel card, String itemName, double itemPrice) {
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
            parent.add(createProductCard(itemName, itemPrice), index);
            parent.revalidate();
            parent.repaint();
        }
        // After updating, also update all cart counters
        if (parent instanceof BreadsPage && this.parent != null) {
            this.parent.updateAllCartCounters();
        }
    }

    @Override
    public void updateCartCount() {
        int count = CartManager.getTotalItems();
        cartCountLabel.setText(String.valueOf(count));
        cartCountLabel.setVisible(count > 0);
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
            String nameWithoutExtension = imageName.substring(0, imageName.lastIndexOf('.'));
            String[] extensions = {".jpg", ".png", ".gif"};
            for (String extension : extensions) {
                imageUrl = getClass().getClassLoader().getResource("kiosk/resources/" + nameWithoutExtension + extension);
                if (imageUrl != null) {
                    return new ImageIcon(imageUrl);
                }
            }
            
            System.out.println("Image not found: " + imageName);
            return null;
        }
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

    /**
     * Shows a dialog for the customer to request assistance
     */
    private void showHelpRequestDialog() {
        JDialog helpDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Request Assistance", Dialog.ModalityType.APPLICATION_MODAL);
        helpDialog.setSize(450, 350);
        helpDialog.setLocationRelativeTo(this);
        helpDialog.setLayout(new BorderLayout());
        
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(BACKGROUND_COLOR);
        
        // Header
        JLabel headerLabel = new JLabel("How can we help you?");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(PRIMARY_COLOR);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 1, 0, 15));
        formPanel.setBackground(BACKGROUND_COLOR);
        
        // Issue type selection
        JPanel issuePanel = new JPanel(new BorderLayout(0, 5));
        issuePanel.setBackground(BACKGROUND_COLOR);
        JLabel issueLabel = new JLabel("Type of Assistance:");
        issueLabel.setFont(REGULAR_FONT);
        
        String[] issueTypes = {
            "Find a Product",
            "Price Information",
            "Product Information",
            "Technical Problem",
            "Other"
        };
        
        JComboBox<String> issueComboBox = new JComboBox<>(issueTypes);
        issueComboBox.setFont(REGULAR_FONT);
        
        issuePanel.add(issueLabel, BorderLayout.NORTH);
        issuePanel.add(issueComboBox, BorderLayout.CENTER);
        
        // Details field
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
        
        // Urgency level
        JPanel urgencyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        urgencyPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel urgencyLabel = new JLabel("Is this urgent?");
        urgencyLabel.setFont(REGULAR_FONT);
        
        JCheckBox urgentCheckBox = new JCheckBox("Yes, I need immediate assistance");
        urgentCheckBox.setFont(REGULAR_FONT);
        urgentCheckBox.setBackground(BACKGROUND_COLOR);
        
        urgencyPanel.add(urgencyLabel);
        urgencyPanel.add(urgentCheckBox);
        
        // Add all form components
        formPanel.add(issuePanel);
        formPanel.add(detailsPanel);
        formPanel.add(urgencyPanel);
        
        // Button panel
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
            
            // Format details with urgency info
            if (isUrgent) {
                details = "[URGENT] " + details;
            }
            
            // Add current category information
            details += "\nCategory: " + currentCategory;
            
            // Submit help request to the manager
            HelpRequestManager.getInstance().submitRequest(
                "Foods & Beverages", 
                issueType, 
                details
            );
            
            // Confirmation message
            JOptionPane.showMessageDialog(helpDialog,
                "Your help request has been submitted.\nA staff member will assist you shortly.",
                "Help Request Submitted",
                JOptionPane.INFORMATION_MESSAGE);
            
            helpDialog.dispose();
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);
        
        // Assemble dialog
        contentPanel.add(headerLabel, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);
        
        helpDialog.add(contentPanel, BorderLayout.CENTER);
        helpDialog.add(buttonPanel, BorderLayout.SOUTH);
        helpDialog.setVisible(true);
    }
}

