package main.java.kiosk.view;

import java.awt.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.*;

import main.java.kiosk.model.InventoryItem;
import main.java.kiosk.util.CartManager;
import main.java.kiosk.util.HelpRequestManager;
import main.java.kiosk.util.InventoryManager;

import java.awt.image.BufferedImage;

/**
 * Modern, comprehensive cart page for SweetBatterBakeshop Kiosk
 * Features: Enhanced UI, promo codes, better payment flow, and recommendations
 */
public class CartPage extends JPanel implements KioskPage {
    private final Map<String, String> imageFileMap = new HashMap<>();
    
    // Modern Bakery Theme Colors
    private final Color PRIMARY_COLOR = BakeryTheme.PRIMARY_COLOR;
    private final Color ACCENT_COLOR = BakeryTheme.BACKGROUND_COLOR;
    private final Color TEXT_COLOR = BakeryTheme.TEXT_DARK;
    private final Color TEXT_LIGHT = BakeryTheme.TEXT_LIGHT;
    private final Color BUTTON_COLOR = BakeryTheme.BUTTON_SUCCESS;
    private final Color REMOVE_COLOR = BakeryTheme.ERROR;
    private final Color CARD_BG = BakeryTheme.CARD_COLOR;
    private final Color SUCCESS_COLOR = BakeryTheme.SUCCESS;
    
    // Fonts
    private final Font HEADER_FONT = BakeryTheme.HEADER_FONT;
    private final Font BUTTON_FONT = BakeryTheme.BUTTON_FONT;
    
    // UI Components
    private JLabel totalLabel;
    private JPanel itemsPanel;
    private JPanel summaryPanel;
    private JButton discountBtn;
    private JButton promoBtn;
    private JTextField promoCodeField;
    private boolean isDiscountApplied = false;
    private boolean isPromoApplied = false;
    private double promoDiscountPercent = 0.0;
    private String appliedPromoCode = "";
    private JDialog verificationDialog;
    private KioskMainPage parent;
    private JLabel cartCountLabel;
    private JLabel itemCountLabel;
    private JLabel savingsLabel;
    
    // Promo codes (in production, this would be from a database)
    private final Map<String, Double> PROMO_CODES = new HashMap<String, Double>() {{
        put("SWEET10", 0.10);    // 10% off
        put("BAKERY15", 0.15);   // 15% off
        put("TASTY5", 0.05);     // 5% off
        put("PASTRY20", 0.20);   // 20% off
    }};

    public CartPage(KioskMainPage parent) {
        this.parent = parent;
        setLayout(new BorderLayout(20, 20));
        setBackground(ACCENT_COLOR);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Initialize image mapping
        initializeImageMap();

        // Header Panel with enhanced design
        JPanel headerPanel = createModernHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main content area with items
        JScrollPane scrollPane = createItemsScrollPane();
        add(scrollPane, BorderLayout.CENTER);

        // Enhanced summary panel
        JPanel summaryPanel = createEnhancedSummaryPanel();
        add(summaryPanel, BorderLayout.SOUTH);

        // Update the initial state
        updateCartSummary();

        // Check if cart is empty and show message if needed
        if (CartManager.isCartEmpty()) {
            showEmptyCartMessage();
        }
    }

    private void initializeImageMap() {
        // Add all image mappings
        imageFileMap.put("Chippy", "chippy.jpg");
        imageFileMap.put("Chips Ahoy", "chipsahoy.jpg");
        imageFileMap.put("Chips Delight", "chipsdelight.jpg");
        imageFileMap.put("Choco Mallows", "chocomallows.jpg");
        imageFileMap.put("Cupp Keyk Topps Sarap", "cuppkeyktoppssarap.jpg");
        imageFileMap.put("Doowee Donut Chocolate", "doowee.jpg");
        imageFileMap.put("Foods", "foods.png");
        imageFileMap.put("Fudgee Barr", "fudgeebarr.jpg");
        imageFileMap.put("Nova", "nova.jpg");
        imageFileMap.put("Piattos", "piattos.jpg");
        imageFileMap.put("Rebisco Choco Crackers", "rebiscochoco.jpg");
        imageFileMap.put("Roller Coaster", "rollercoaster.jpg");
        imageFileMap.put("Tortillos", "tortillos.jpg");
        imageFileMap.put("Alcohol", "alcohol.png");
        imageFileMap.put("Household", "household.png");
        imageFileMap.put("Personal Care", "personal_care.png");
        imageFileMap.put("V-Cut", "vcut.jpg");
        imageFileMap.put("Cream-O", "creamo.jpg");
        imageFileMap.put("Stik-O", "stiko.jpg");
        imageFileMap.put("Cheeseburger", "cheeseburger.png");
        imageFileMap.put("Hotdog", "hotdogsandwich.png");
        imageFileMap.put("Siopao", "siopao.png");
        imageFileMap.put("Sisig with Rice", "sisig.jpg");
        imageFileMap.put("Bavarian Filled Donut", "bavarian.jpg");
        imageFileMap.put("BurgerSteak with Rice", "burgersteak.jpg");
        imageFileMap.put("Chocolate Donut", "chocolatedonut.png");
        imageFileMap.put("Cup Noodles Very Veggie", "cupnoodles.png");
        imageFileMap.put("Fried Chicken with Rice", "friedchicken.png");
        imageFileMap.put("Pancit Canton", "pancitcanton.png");
    }

    /**
     * Creates a modern, enhanced header panel with better visual hierarchy
     */
    private JPanel createModernHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, 100));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 3, 0, BakeryTheme.ACCENT_COLOR),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        // Left side: Title and item count
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        titleRow.setOpaque(false);

        JLabel cartIcon = new JLabel("🛒");
        cartIcon.setFont(new Font("SansSerif", Font.PLAIN, 32));
        
        JLabel titleLabel = new JLabel("Your Shopping Cart");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);

        JButton helpButton = new JButton("❓ Help");
        helpButton.setToolTipText("Ask for assistance");
        helpButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        helpButton.setForeground(Color.WHITE);
        helpButton.setFocusPainted(false);
        helpButton.setBorderPainted(false);
        helpButton.setContentAreaFilled(false);
        helpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        helpButton.addActionListener(e -> showHelpRequestDialog());

        titleRow.add(cartIcon);
        titleRow.add(titleLabel);
        titleRow.add(helpButton);

        itemCountLabel = new JLabel(getItemCountText());
        itemCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        itemCountLabel.setForeground(new Color(255, 255, 255, 200));
        itemCountLabel.setBorder(BorderFactory.createEmptyBorder(5, 50, 0, 0));

        titlePanel.add(titleRow);
        titlePanel.add(itemCountLabel);

        // Right side: Back button (smaller size)
        JButton backBtn = createStyledButton("← Back to Shopping", BUTTON_COLOR);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setPreferredSize(new Dimension(180, 45));
        backBtn.addActionListener(e -> {
            if (parent != null) {
                parent.showMainPage();
                setVisible(false);
            }
        });

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(backBtn, BorderLayout.EAST);

        return headerPanel;
    }
    
    private String getItemCountText() {
        int itemCount = CartManager.getTotalItems();
        if (itemCount == 0) {
            return "No items in cart";
        } else if (itemCount == 1) {
            return "1 item in cart";
        } else {
            return itemCount + " items in cart";
        }
    }

    private JScrollPane createItemsScrollPane() {
        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(Color.WHITE);
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        populateCartItems();

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane;
    }

    public void clearCart() {
        // Clear the cart data
        CartManager.clearCart();

        // Reset discount states
        isDiscountApplied = false;
        isPromoApplied = false;
        promoDiscountPercent = 0.0;
        appliedPromoCode = "";
        
        // Reset promo code field if it exists
        if (promoCodeField != null) {
            promoCodeField.setText("");
            promoCodeField.setEnabled(true);
        }
        
        if (promoBtn != null) {
            promoBtn.setText("Apply");
            promoBtn.setBackground(BakeryTheme.ACCENT_COLOR);
            // Reset the action listener
            for (java.awt.event.ActionListener al : promoBtn.getActionListeners()) {
                promoBtn.removeActionListener(al);
            }
            promoBtn.addActionListener(e -> applyPromoCode());
        }

        // Update the UI
        itemsPanel.removeAll();
        showEmptyCartMessage();
        itemsPanel.revalidate();
        itemsPanel.repaint();

        // Update summary and cart count
        updateCartSummary();
        updateCartCount();
    }

    private void populateCartItems() {
        Map<String, Integer> cartItems = CartManager.getCartItems();
        Map<String, Double> cartPrices = CartManager.getCartPrices();

        if (cartItems.isEmpty()) {
            return;
        }

        for (String item : cartItems.keySet()) {
            JPanel itemCard = createItemCard(item, cartItems.get(item), cartPrices.get(item));
            itemsPanel.add(itemCard);

            // Add spacing between items
            itemsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        }
    }

    /**
     * Creates an enhanced, modern item card with better layout and information
     */
    private JPanel createItemCard(String itemName, int quantity, double price) {
        JPanel card = new JPanel(new BorderLayout(25, 0));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                    new LineBorder(BakeryTheme.BORDER_LIGHT, 1, true),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)
                ),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        
        // Add subtle shadow effect
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 0, 10), 1, true),
            BorderFactory.createCompoundBorder(
                new LineBorder(BakeryTheme.BORDER_LIGHT, 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
            )
        ));

        // LEFT: Product image with enhanced styling
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(CARD_BG);
        imagePanel.setPreferredSize(new Dimension(120, 120));
        imagePanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BakeryTheme.BORDER_LIGHT, 1, true),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        String fileName = imageFileMap.get(itemName);
        if (fileName != null) {
            URL imageUrl = getClass().getClassLoader().getResource("kiosk/resources/" + fileName);
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
                Image scaledImage = icon.getImage().getScaledInstance(110, 110, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                imageLabel.setText("📦");
                imageLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
            }
        } else {
            imageLabel.setText("📦");
            imageLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        }
        
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        // CENTER: Enhanced product details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(CARD_BG);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Product name
        JLabel nameLabel = new JLabel(itemName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Unit price with better styling
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        pricePanel.setBackground(CARD_BG);
        pricePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel priceTagLabel = new JLabel("Price: ");
        priceTagLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        priceTagLabel.setForeground(TEXT_LIGHT);
        
        JLabel priceLabel = new JLabel("₱" + new DecimalFormat("0.00").format(price));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        priceLabel.setForeground(new Color(60, 60, 60));
        
        pricePanel.add(priceTagLabel);
        pricePanel.add(priceLabel);

        // Subtotal with prominent display
        JPanel subtotalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        subtotalPanel.setBackground(CARD_BG);
        subtotalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtotalTagLabel = new JLabel("Subtotal: ");
        subtotalTagLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtotalTagLabel.setForeground(TEXT_LIGHT);
        
        JLabel subtotalLabel = new JLabel("₱" + new DecimalFormat("0.00").format(price * quantity));
        subtotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        subtotalLabel.setForeground(BakeryTheme.ACCENT_COLOR);
        
        subtotalPanel.add(subtotalTagLabel);
        subtotalPanel.add(subtotalLabel);

        detailsPanel.add(nameLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        detailsPanel.add(pricePanel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        detailsPanel.add(subtotalPanel);

        // RIGHT: Enhanced quantity controls and actions
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        controlsPanel.setBackground(CARD_BG);
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Quantity label
        JLabel qtyLabel = new JLabel("Quantity");
        qtyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        qtyLabel.setForeground(TEXT_LIGHT);
        qtyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Enhanced quantity control panel
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        quantityPanel.setBackground(CARD_BG);
        quantityPanel.setMaximumSize(new Dimension(250, 60));

        JButton decrementBtn = createQuantityButton("−");
        decrementBtn.setPreferredSize(new Dimension(55, 50));
        decrementBtn.setFont(new Font("Segoe UI", Font.BOLD, 24));
        decrementBtn.setBackground(new Color(240, 240, 240));
        decrementBtn.setForeground(TEXT_COLOR);

        JLabel quantityLabel = new JLabel(String.valueOf(quantity));
        quantityLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        quantityLabel.setHorizontalAlignment(SwingConstants.CENTER);
        quantityLabel.setPreferredSize(new Dimension(60, 50));
        quantityLabel.setOpaque(true);
        quantityLabel.setBackground(ACCENT_COLOR);
        quantityLabel.setBorder(BorderFactory.createLineBorder(BakeryTheme.BORDER_LIGHT, 1));

        JButton incrementBtn = createQuantityButton("+");
        incrementBtn.setPreferredSize(new Dimension(55, 50));
        incrementBtn.setFont(new Font("Segoe UI", Font.BOLD, 24));
        incrementBtn.setBackground(SUCCESS_COLOR);
        incrementBtn.setForeground(Color.WHITE);

        decrementBtn.addActionListener(e -> {
            CartManager.removeItem(itemName);
            int newQty = CartManager.getItemQuantity(itemName);
            quantityLabel.setText(String.valueOf(newQty));
            subtotalLabel.setText("₱" + new DecimalFormat("0.00").format(price * newQty));
            decrementBtn.setEnabled(newQty > 0);
            updateCartSummary();
            updateCartCount();

            if (newQty == 0) {
                itemsPanel.remove(card);
                // Remove the spacing component after the card too
                Component[] components = itemsPanel.getComponents();
                for (int i = 0; i < components.length; i++) {
                    if (components[i] == card && i + 1 < components.length) {
                        itemsPanel.remove(i + 1); // Remove spacing
                        break;
                    }
                }
                itemsPanel.revalidate();
                itemsPanel.repaint();

                if (CartManager.isCartEmpty()) {
                    showEmptyCartMessage();
                }
            }
        });

        incrementBtn.addActionListener(e -> {
            CartManager.addItem(itemName, price);
            int newQty = CartManager.getItemQuantity(itemName);
            quantityLabel.setText(String.valueOf(newQty));
            subtotalLabel.setText("₱" + new DecimalFormat("0.00").format(price * newQty));
            decrementBtn.setEnabled(true);
            updateCartSummary();
            updateCartCount();
        });

        quantityPanel.add(decrementBtn);
        quantityPanel.add(quantityLabel);
        quantityPanel.add(incrementBtn);

        // Remove button with enhanced styling
        JButton removeBtn = new JButton("🗑 Remove");
        removeBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setBackground(REMOVE_COLOR);
        removeBtn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(REMOVE_COLOR, 1, true),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        removeBtn.setFocusPainted(false);
        removeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        removeBtn.setMaximumSize(new Dimension(180, 45));
        
        // Hover effect for remove button
        removeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                removeBtn.setBackground(BakeryTheme.darker(REMOVE_COLOR, 0.15f));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                removeBtn.setBackground(REMOVE_COLOR);
            }
        });
        
        removeBtn.addActionListener(e -> {
            // Confirm removal for large quantities
            if (quantity > 3) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Remove " + quantity + " " + itemName + " from cart?",
                    "Confirm Removal",
                    JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            for (int i = 0; i < quantity; i++) {
                CartManager.removeItem(itemName);
            }
            itemsPanel.remove(card);
            // Remove the spacing component after the card too
            Component[] components = itemsPanel.getComponents();
            for (int i = 0; i < components.length; i++) {
                if (components[i] == card && i + 1 < components.length) {
                    itemsPanel.remove(i + 1); // Remove spacing
                    break;
                }
            }
            itemsPanel.revalidate();
            itemsPanel.repaint();
            updateCartSummary();
            updateCartCount();
            if (CartManager.isCartEmpty()) {
                showEmptyCartMessage();
            }
        });

        controlsPanel.add(qtyLabel);
        controlsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        controlsPanel.add(quantityPanel);
        controlsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        controlsPanel.add(removeBtn);

        card.add(imagePanel, BorderLayout.WEST);
        card.add(detailsPanel, BorderLayout.CENTER);
        card.add(controlsPanel, BorderLayout.EAST);

        return card;
    }

    private JButton createQuantityButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setBackground(ACCENT_COLOR);
        button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Creates an enhanced summary panel with detailed breakdown
     */
    private JPanel createEnhancedSummaryPanel() {
        summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(3, 0, 0, 0, BakeryTheme.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(25, 35, 25, 35)));
        summaryPanel.setBackground(CARD_BG);

        // Summary Header
        JLabel summaryHeaderLabel = new JLabel("Order Summary");
        summaryHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        summaryHeaderLabel.setForeground(TEXT_COLOR);
        summaryHeaderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Promo Code Section
        JPanel promoPanel = createPromoCodePanel();
        promoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Discount Button (PWD/Senior)
        discountBtn = createStyledButton("Apply 20% Discount (PWD/Senior)", SUCCESS_COLOR);
        discountBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        discountBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        discountBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        discountBtn.addActionListener(e -> {
            if (isDiscountApplied) {
                isDiscountApplied = false;
                CartManager.applyDiscount(false);
                updateCartSummary();
                discountBtn.setText("Apply 20% Discount (PWD/Senior)");
                discountBtn.setBackground(SUCCESS_COLOR);
            } else {
                showIdVerificationDialog();
            }
        });

        // Cost Breakdown Panel
        JPanel costBreakdownPanel = createCostBreakdownPanel();
        costBreakdownPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Checkout Button
        JButton checkoutBtn = createStyledButton("PROCEED TO CHECKOUT →", BUTTON_COLOR);
        checkoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 22));
        checkoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        checkoutBtn.addActionListener(e -> handleCheckout());

        // Add all components
        summaryPanel.add(summaryHeaderLabel);
        summaryPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        summaryPanel.add(promoPanel);
        summaryPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        summaryPanel.add(discountBtn);
        summaryPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        summaryPanel.add(costBreakdownPanel);
        summaryPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        summaryPanel.add(checkoutBtn);

        return summaryPanel;
    }
    
    /**
     * Creates promo code input panel
     */
    private JPanel createPromoCodePanel() {
        JPanel promoPanel = new JPanel();
        promoPanel.setLayout(new BoxLayout(promoPanel, BoxLayout.Y_AXIS));
        promoPanel.setBackground(CARD_BG);
        promoPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BakeryTheme.BORDER_LIGHT, 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel promoLabel = new JLabel("Have a promo code?");
        promoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        promoLabel.setForeground(TEXT_COLOR);
        promoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(CARD_BG);
        inputPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        promoCodeField = new JTextField();
        promoCodeField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        promoCodeField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BakeryTheme.BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        promoCodeField.setToolTipText("Enter promo code (e.g., SWEET10, BAKERY15)");

        promoBtn = createStyledButton("Apply", BakeryTheme.ACCENT_COLOR);
        promoBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        promoBtn.setPreferredSize(new Dimension(100, 45));
        promoBtn.addActionListener(e -> applyPromoCode());

        inputPanel.add(promoCodeField, BorderLayout.CENTER);
        inputPanel.add(promoBtn, BorderLayout.EAST);

        promoPanel.add(promoLabel);
        promoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        promoPanel.add(inputPanel);

        return promoPanel;
    }
    
    /**
     * Applies promo code discount
     */
    private void applyPromoCode() {
        String code = promoCodeField.getText().trim().toUpperCase();
        
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a promo code",
                "No Code Entered",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (PROMO_CODES.containsKey(code)) {
            if (!appliedPromoCode.equals(code)) {
                promoDiscountPercent = PROMO_CODES.get(code);
                appliedPromoCode = code;
                isPromoApplied = true;
                updateCartSummary();
                
                JOptionPane.showMessageDialog(this,
                    String.format("Promo code '%s' applied!\n%.0f%% discount on your order.",
                        code, promoDiscountPercent * 100),
                    "Promo Applied",
                    JOptionPane.INFORMATION_MESSAGE);
                    
                promoCodeField.setEnabled(false);
                promoBtn.setText("Remove");
                promoBtn.setBackground(REMOVE_COLOR);
                promoBtn.removeActionListener(promoBtn.getActionListeners()[0]);
                promoBtn.addActionListener(e -> removePromoCode());
            } else {
                JOptionPane.showMessageDialog(this,
                    "This promo code is already applied",
                    "Already Applied",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Invalid promo code. Please try again.",
                "Invalid Code",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Removes applied promo code
     */
    private void removePromoCode() {
        isPromoApplied = false;
        promoDiscountPercent = 0.0;
        appliedPromoCode = "";
        promoCodeField.setText("");
        promoCodeField.setEnabled(true);
        promoBtn.setText("Apply");
        promoBtn.setBackground(BakeryTheme.ACCENT_COLOR);
        promoBtn.removeActionListener(promoBtn.getActionListeners()[0]);
        promoBtn.addActionListener(e -> applyPromoCode());
        updateCartSummary();
    }
    
    /**
     * Creates detailed cost breakdown panel
     */
    private JPanel createCostBreakdownPanel() {
        JPanel costPanel = new JPanel();
        costPanel.setLayout(new BoxLayout(costPanel, BoxLayout.Y_AXIS));
        costPanel.setBackground(CARD_BG);
        costPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BakeryTheme.BORDER_LIGHT, 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        DecimalFormat df = new DecimalFormat("0.00");
        double subtotal = CartManager.getTotalPrice();
        double seniorDiscount = CartManager.getDiscountAmount();
        double promoDiscount = subtotal * promoDiscountPercent;
        double totalDiscount = seniorDiscount + promoDiscount;
        double taxableAmount = subtotal - totalDiscount;
        double tax = taxableAmount * 0.12; // 12% VAT
        double total = taxableAmount + tax;

        // Subtotal row
        costPanel.add(createCostRow("Subtotal:", "₱" + df.format(subtotal), false));
        costPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // Promo discount row (if applied)
        if (isPromoApplied && promoDiscount > 0) {
            costPanel.add(createCostRow("Promo Discount (" + appliedPromoCode + "):", 
                "-₱" + df.format(promoDiscount), true, SUCCESS_COLOR));
            costPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        // Senior/PWD discount row (if applied)
        if (seniorDiscount > 0) {
            costPanel.add(createCostRow("Senior/PWD Discount (20%):", 
                "-₱" + df.format(seniorDiscount), true, SUCCESS_COLOR));
            costPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        // Tax row
        costPanel.add(createCostRow("VAT (12%):", "₱" + df.format(tax), false));
        costPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Separator
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        separator.setForeground(BakeryTheme.BORDER_MEDIUM);
        costPanel.add(separator);
        costPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Total savings row (if any discounts)
        if (totalDiscount > 0) {
            savingsLabel = new JLabel("You're saving ₱" + df.format(totalDiscount) + "!");
            savingsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            savingsLabel.setForeground(SUCCESS_COLOR);
            savingsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            costPanel.add(savingsLabel);
            costPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        // Total row
        totalLabel = new JLabel("₱" + df.format(total));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        totalLabel.setForeground(TEXT_COLOR);
        JPanel totalPanel = createCostRow("Total:", totalLabel.getText(), true);
        ((JLabel) ((JPanel) totalPanel.getComponent(0)).getComponent(0)).setFont(
            new Font("Segoe UI", Font.BOLD, 28));
        ((JLabel) ((JPanel) totalPanel.getComponent(1)).getComponent(0)).setFont(
            new Font("Segoe UI", Font.BOLD, 28));
        costPanel.add(totalPanel);

        return costPanel;
    }
    
    /**
     * Creates a cost row with label and value
     */
    private JPanel createCostRow(String label, String value, boolean bold) {
        return createCostRow(label, value, bold, TEXT_COLOR);
    }
    
    private JPanel createCostRow(String label, String value, boolean bold, Color color) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(CARD_BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelPanel.setBackground(CARD_BG);
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(bold ? new Font("Segoe UI", Font.BOLD, 18) : 
                               new Font("Segoe UI", Font.PLAIN, 18));
        labelComp.setForeground(color);
        labelPanel.add(labelComp);

        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        valuePanel.setBackground(CARD_BG);
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(bold ? new Font("Segoe UI", Font.BOLD, 18) : 
                               new Font("Segoe UI", Font.PLAIN, 18));
        valueComp.setForeground(color);
        valuePanel.add(valueComp);

        row.add(labelPanel, BorderLayout.WEST);
        row.add(valuePanel, BorderLayout.EAST);

        return row;
    }

    private JButton createStyledButton(String text) {
        return createStyledButton(text, BUTTON_COLOR);
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(backgroundColor, 1, true),
            new EmptyBorder(12, 20, 12, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BakeryTheme.darker(backgroundColor, 0.1f));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }

    private void updateCartSummary() {
        double subtotal = CartManager.getTotalPrice();
        double seniorDiscount = CartManager.getDiscountAmount();
        double promoDiscount = subtotal * promoDiscountPercent;
        double totalDiscount = seniorDiscount + promoDiscount;
        double taxableAmount = subtotal - totalDiscount;
        double tax = taxableAmount * 0.12; // 12% VAT

        // Update the summary panel by recreating the cost breakdown
        if (summaryPanel != null) {
            // Remove old cost breakdown panel and recreate it
            Component[] components = summaryPanel.getComponents();
            for (int i = 0; i < components.length; i++) {
                if (components[i] instanceof JPanel) {
                    JPanel panel = (JPanel) components[i];
                    if (panel.getBorder() instanceof CompoundBorder) {
                        // This is likely the cost breakdown panel
                        summaryPanel.remove(panel);
                        JPanel newCostPanel = createCostBreakdownPanel();
                        newCostPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        // Insert at the same position (before checkout button)
                        summaryPanel.add(newCostPanel, components.length - 3);
                        break;
                    }
                }
            }
            summaryPanel.revalidate();
            summaryPanel.repaint();
        }

        // Update the discount button text
        if (discountBtn != null) {
            if (isDiscountApplied) {
                discountBtn.setText("✓ Senior/PWD Discount Applied - Click to Remove");
                discountBtn.setBackground(REMOVE_COLOR);
            } else {
                discountBtn.setText("Apply 20% Discount (PWD/Senior)");
                discountBtn.setBackground(SUCCESS_COLOR);
            }
        }
        
        // Update item count in header
        if (itemCountLabel != null) {
            itemCountLabel.setText(getItemCountText());
        }
    }

    private void handleCheckout() {
        if (CartManager.isCartEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Your cart is empty. Please add items before checkout.",
                    "Empty Cart",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Show payment dialog before receipt
        double total = CartManager.getTotal();
        JPanel paymentPanel = new JPanel();
        paymentPanel.setLayout(new BoxLayout(paymentPanel, BoxLayout.Y_AXIS));
        paymentPanel.setBackground(Color.WHITE);
        paymentPanel.setBorder(BorderFactory.createEmptyBorder(70, 100, 250, 100)); // Adds padding
        
        // Payment method selection
        JLabel methodLabel = new JLabel("Select payment method:");
        methodLabel.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Increased font size
        methodLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10)); // Spacing between elements
        radioPanel.setBackground(Color.WHITE);
        radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JRadioButton cashButton = new JRadioButton("Cash");
        cashButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cashButton.setBackground(Color.WHITE);
        cashButton.setSelected(true);
        
        JRadioButton ecashButton = new JRadioButton("E-Cash (QR Code)");
        ecashButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        ecashButton.setBackground(Color.WHITE);
        
        ButtonGroup paymentGroup = new ButtonGroup();
        paymentGroup.add(cashButton);
        paymentGroup.add(ecashButton);
        
        radioPanel.add(cashButton);
        radioPanel.add(ecashButton);
        
        // Cash panel components
        JPanel cashPanel = new JPanel();
        cashPanel.setLayout(new BoxLayout(cashPanel, BoxLayout.Y_AXIS));
        cashPanel.setBackground(Color.WHITE);
        cashPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel paymentLabel = new JLabel("Enter payment amount:");
        paymentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        paymentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField paymentField = new JTextField();
        paymentField.setFont(new Font("Segoe UI", Font.BOLD, 18));
        paymentField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        paymentField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel changeLabel = new JLabel("Change: ₱0.00");
        changeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        changeLabel.setForeground(new Color(39, 174, 96));
        changeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        cashPanel.add(paymentLabel);
        cashPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        cashPanel.add(paymentField);
        cashPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        cashPanel.add(changeLabel);
        
        // QR Code panel
        JPanel qrPanel = new JPanel();
        qrPanel.setLayout(new BoxLayout(qrPanel, BoxLayout.Y_AXIS));
        qrPanel.setBackground(Color.WHITE);
        qrPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        qrPanel.setVisible(false);
        
        JLabel qrInstructionLabel = new JLabel("Scan QR code with your mobile banking app:");
        qrInstructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        qrInstructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel qrCodeLabel = new JLabel();
        qrCodeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        qrCodeLabel.setPreferredSize(new Dimension(200, 200));
        
        JLabel qrAmountLabel = new JLabel(String.format("Amount: ₱%.2f", total));
        qrAmountLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        qrAmountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton completeQrPaymentButton = new JButton("I've Completed Payment");
        completeQrPaymentButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        completeQrPaymentButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        completeQrPaymentButton.setBackground(new Color(39, 174, 96));
        completeQrPaymentButton.setForeground(Color.WHITE);
        completeQrPaymentButton.setMaximumSize(new Dimension(250, 40));
        
        qrPanel.add(qrInstructionLabel);
        qrPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        qrPanel.add(qrCodeLabel);
        qrPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        qrPanel.add(qrAmountLabel);
        qrPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        qrPanel.add(completeQrPaymentButton);
        
        // Generate mock QR code
        generateMockQrCode(qrCodeLabel, total);
        
        // Toggle between payment panels
        cashButton.addActionListener(e -> {
            cashPanel.setVisible(true);
            qrPanel.setVisible(false);
            paymentPanel.revalidate();
            paymentPanel.repaint();
        });
        
        ecashButton.addActionListener(e -> {
            cashPanel.setVisible(false);
            qrPanel.setVisible(true);
            paymentPanel.revalidate();
            paymentPanel.repaint();
        });
        
        // Add components to main panel
        paymentPanel.add(methodLabel);
        paymentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        paymentPanel.add(radioPanel);
        paymentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        paymentPanel.add(cashPanel);
        paymentPanel.add(qrPanel);
        
        double[] paymentHolder = new double[1];
        double[] changeHolder = new double[1];
        boolean[] isEcashPayment = new boolean[1];

        // Create a final reference to the dialog we'll create
        final JDialog[] paymentDialog = new JDialog[1];
        // Create a safer dialog initialization
        try {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            if (parentWindow instanceof Frame) {
                paymentDialog[0] = new JDialog((Frame)parentWindow, "Payment", true);
            } else if (parentWindow instanceof Dialog) {
                paymentDialog[0] = new JDialog((Dialog)parentWindow, "Payment", true);
            } else {
                // If we can't find a proper parent, create a non-modal dialog
                paymentDialog[0] = new JDialog();
                paymentDialog[0].setTitle("Payment");
            }
        } catch (Exception ex) {
            // Fallback for any unexpected errors
            paymentDialog[0] = new JDialog();
            paymentDialog[0].setTitle("Payment");
        }
        
        paymentDialog[0].setContentPane(paymentPanel);
        paymentDialog[0].setSize(700, 900);
        paymentDialog[0].setLocationRelativeTo(null); // Center on screen instead of relative to component
        
        // Separate handling for QR code payment
        completeQrPaymentButton.addActionListener(e -> {
            isEcashPayment[0] = true;
            paymentHolder[0] = total;
            changeHolder[0] = 0.0;
            paymentDialog[0].dispose(); // Close the dialog when QR payment completes
        });

        // Add buttons for cash payment
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");
        
        confirmButton.addActionListener(e -> {
            if (cashButton.isSelected()) {
                // Validate cash payment
                String input = paymentField.getText().trim();
                try {
                    double payment = Double.parseDouble(input);
                    if (payment < total) {
                        JOptionPane.showMessageDialog(paymentDialog[0], 
                            "Insufficient payment. Please enter an amount equal to or greater than the total.",
                            "Insufficient Payment", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    double change = payment - total;
                    paymentHolder[0] = payment;
                    changeHolder[0] = change;
                    isEcashPayment[0] = false;
                    paymentDialog[0].dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(paymentDialog[0], 
                        "Please enter a valid payment amount.", 
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // For E-Cash, we wait for the completeQrPaymentButton action
                JOptionPane.showMessageDialog(paymentDialog[0],
                    "Please complete your payment by scanning the QR code and clicking 'I've Completed Payment'.",
                    "E-Cash Payment", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> {
            paymentDialog[0].dispose();
        });
        
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        paymentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        paymentPanel.add(buttonPanel);
        
        // Show the payment dialog and wait for user interaction
        paymentDialog[0].setVisible(true);
        
        // If dialog was closed without completing a payment, return
        if (!isEcashPayment[0] && paymentHolder[0] == 0.0) {
            return; // Payment was cancelled
        }

        // Show receipt preview with payment and change
        JTextArea receiptArea = new JTextArea(
            isEcashPayment[0] 
            ? getFormattedReceiptWithEcashPayment(total) 
            : getFormattedReceiptWithPayment(paymentHolder[0], changeHolder[0])
        );
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 16));

        JScrollPane receiptScrollPane = new JScrollPane(receiptArea);
        // Increase the preferred size to show more content without scrolling
        receiptScrollPane.setPreferredSize(new Dimension(800, 700));

        int option = JOptionPane.showConfirmDialog(this,
                receiptScrollPane,
                "Confirm Purchase",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) { // Process the purchase
            CartManager.saveReceipt();

            // Clear the cart after updating inventory
            clearCart();

            // Update cart count label to 0 after clearing cart
            updateCartCount();

            updateInventoryStock();

            // Also update cart counters in other pages if needed
            if (parent != null) {
                parent.updateAllCartCounters();
            }

            // Show success message
            JOptionPane.showMessageDialog(this,
                    "Thank you for your purchase!\nPlease collect your items.",
                    "Purchase Complete",
                    JOptionPane.INFORMATION_MESSAGE);

            // Return to main page
            if (parent != null) {
                parent.showMainPage();
            }
        }
    }

    private void updateInventoryStock() {
        // Get all items from the cart
        Map<String, Integer> cartItems = CartManager.getCartItems();

        // Update inventory stock for each item
        for (Map.Entry<String, Integer> entry : cartItems.entrySet()) {
            String itemName = entry.getKey();
            int quantity = entry.getValue();

            // Get the item from inventory
            InventoryItem item = InventoryManager.getInstance().getItemByName(itemName);

            if (item != null) {
                // Reduce stock quantity
                int currentStock = item.getStockQuantity();
                item.setStockQuantity(currentStock - quantity);

                // Update the item in inventory
                InventoryManager.getInstance().updateItem(item);
            }
        }
    }

    // Add this helper method to show payment and change in the receipt
    private String getFormattedReceiptWithPayment(double payment, double change) {
        StringBuilder receipt = new StringBuilder();
        receipt.append(CartManager.getFormattedReceipt());
        receipt.append(String.format("Payment:                      ₱%7.2f\n", payment));
        receipt.append(String.format("Change:                       ₱%7.2f\n", change));
        receipt.append("==================================\n");
        return receipt.toString();
    }

    // Add this helper method to generate a mock QR code
    private void generateMockQrCode(JLabel qrLabel, double amount) {
        // Create a simple mock QR code (a black and white grid)
        int size = 200;
        BufferedImage qrImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = qrImage.createGraphics();
        
        // Fill with white background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, size, size);
        
        // Draw black border
        g.setColor(Color.BLACK);
        g.drawRect(5, 5, size-10, size-10);
        
        // Generate random pattern for QR code
        g.setColor(Color.BLACK);
        int blockSize = 10;
        Random rand = new Random((long) (amount * 1000)); // Use amount as seed for consistent generation
        
        for (int y = 10; y < size-10; y += blockSize) {
            for (int x = 10; x < size-10; x += blockSize) {
                if (rand.nextBoolean()) {
                    g.fillRect(x, y, blockSize, blockSize);
                }
            }
        }
        
        // Add finder patterns (the three squares in corners of QR codes)
        drawFinderPattern(g, 20, 20, 40);
        drawFinderPattern(g, size-60, 20, 40);
        drawFinderPattern(g, 20, size-60, 40);
        
        // Add payment info in the center
        g.setColor(Color.WHITE);
        g.fillRect(70, 85, 60, 30);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Monospaced", Font.BOLD, 10));
        g.drawString("PAY", 80, 100);
        g.drawString(String.format("₱%.2f", amount), 80, 110);
        
        g.dispose();
        qrLabel.setIcon(new ImageIcon(qrImage));
    }
    
    private void drawFinderPattern(Graphics2D g, int x, int y, int size) {
        // Outer square
        g.setColor(Color.BLACK);
        g.fillRect(x, y, size, size);
        
        // Middle white square
        g.setColor(Color.WHITE);
        g.fillRect(x + size/7, y + size/7, size - 2*size/7, size - 2*size/7);
        
        // Inner black square
        g.setColor(Color.BLACK);
        g.fillRect(x + 2*size/7, y + 2*size/7, size - 4*size/7, size - 4*size/7);
    }
    
    // Add this helper method to show e-cash payment in the receipt
    private String getFormattedReceiptWithEcashPayment(double amount) {
        StringBuilder receipt = new StringBuilder();
        receipt.append(CartManager.getFormattedReceipt());
        receipt.append(String.format("Payment Method:               E-Cash\n"));
        receipt.append(String.format("Amount:                       ₱%7.2f\n", amount));
        receipt.append("==================================\n");
        return receipt.toString();
    }

    private void showEmptyCartMessage() {
        itemsPanel.removeAll();

        JPanel emptyCartPanel = new JPanel();
        emptyCartPanel.setLayout(new BoxLayout(emptyCartPanel, BoxLayout.Y_AXIS));
        emptyCartPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyCartPanel.setBackground(Color.WHITE);
        emptyCartPanel.setBorder(BorderFactory.createEmptyBorder(100, 50, 100, 50));

        // Large cart icon with modern styling
        JLabel emptyCartIcon = new JLabel("🛒");
        emptyCartIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 120));
        emptyCartIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Main message
        JLabel emptyCartLabel = new JLabel("Your cart is empty");
        emptyCartLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        emptyCartLabel.setForeground(TEXT_COLOR);
        emptyCartLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Suggestion text
        JLabel suggestionLabel = new JLabel("Discover our delicious selection of bakery items");
        suggestionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        suggestionLabel.setForeground(TEXT_LIGHT);
        suggestionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Browse button with modern styling
        JButton shopButton = createStyledButton("🎂 BROWSE OUR PRODUCTS", BUTTON_COLOR);
        shopButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        shopButton.setFont(new Font("Segoe UI", Font.BOLD, 22));
        shopButton.setMaximumSize(new Dimension(400, 70));
        shopButton.addActionListener(e -> {
            if (parent != null) {
                parent.showMainPage();
            }
        });

        // Assembly
        emptyCartPanel.add(emptyCartIcon);
        emptyCartPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        emptyCartPanel.add(emptyCartLabel);
        emptyCartPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        emptyCartPanel.add(suggestionLabel);
        emptyCartPanel.add(Box.createRigidArea(new Dimension(0, 50)));
        emptyCartPanel.add(shopButton);

        itemsPanel.add(emptyCartPanel);
        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    private void showIdVerificationDialog() {
        verificationDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "ID Verification",
                Dialog.ModalityType.APPLICATION_MODAL);
        verificationDialog.setSize(400, 300);
        verificationDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Please verify eligibility");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Checkbox for staff verification
        JCheckBox staffVerifiedCheckbox = new JCheckBox("ID has been verified by staff");
        staffVerifiedCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        staffVerifiedCheckbox.setAlignmentX(Component.CENTER_ALIGNMENT);
        staffVerifiedCheckbox.setBackground(panel.getBackground());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));

        JButton verifyButton = new JButton("Verify ID");
        verifyButton.setPreferredSize(new Dimension(120, 40));
        verifyButton.setEnabled(false); // Disabled by default

        // Enable verify button only if checkbox is checked
        staffVerifiedCheckbox.addActionListener(e -> {
            verifyButton.setEnabled(staffVerifiedCheckbox.isSelected());
        });

        verifyButton.addActionListener(e -> {
            isDiscountApplied = true;
            CartManager.applyDiscount(true);
            updateCartSummary();
            verificationDialog.dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.addActionListener(e -> verificationDialog.dispose());

        buttonPanel.add(verifyButton);
        buttonPanel.add(cancelButton);

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(staffVerifiedCheckbox);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttonPanel);

        verificationDialog.add(panel);
        verificationDialog.setVisible(true);
    }

    private void showHelpRequestDialog() {
        JDialog helpDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Request Assistance",
                Dialog.ModalityType.APPLICATION_MODAL);
        helpDialog.setSize(400, 300);
        helpDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Need help with your cart?");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton assistanceButton = createStyledButton("Request Staff Assistance");
        assistanceButton.setMaximumSize(new Dimension(250, 50));
        assistanceButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        assistanceButton.addActionListener(e -> {
            HelpRequestManager.getInstance().submitRequest(
                    "Shopping Cart",
                    "Customer Assistance",
                    "Customer needs help with shopping cart");
            helpDialog.dispose();
            JOptionPane.showMessageDialog(this,
                    "A staff member will assist you shortly.",
                    "Help Request Submitted",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(assistanceButton);

        helpDialog.add(panel);
        helpDialog.setVisible(true);
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
        if (cartCountLabel != null) {
            cartCountLabel.setText(String.valueOf(count));
            cartCountLabel.setVisible(count > 0);
        }
    }

    /**
     * Refreshes the cart UI to reflect the current cart contents.
     * Call this when switching to the cart page.
     */
    public void refreshCart() {
        itemsPanel.removeAll();
        populateCartItems();
        itemsPanel.revalidate();
        itemsPanel.repaint();

        if (CartManager.isCartEmpty()) {
            showEmptyCartMessage();
        } else {
        }
            updateCartSummary();
            updateCartCount();
    }
    
}