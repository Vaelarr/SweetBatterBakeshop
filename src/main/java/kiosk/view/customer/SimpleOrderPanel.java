package kiosk.view.customer;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import kiosk.controller.CustomOrderController;
import kiosk.database.dao.CustomOrderDAO;
import kiosk.database.dao.CustomProductDAO;
import kiosk.model.*;
import kiosk.view.BakeryTheme;

/**
 * Comprehensive Order Panel - Full-featured custom order placement system
 * Features:
 * - Product browsing with details
 * - Add-ons selection system
 * - Real-time price calculation
 * - Order customization (message, special instructions)
 * - Delivery/Pickup options
 * - Order history with filtering
 * - Order details view
 * - Order cancellation
 * - Payment tracking
 */
public class SimpleOrderPanel extends JPanel {
    
    private final Color PRIMARY_COLOR = BakeryTheme.PRIMARY_COLOR;
    private final Color ACCENT_COLOR = BakeryTheme.ACCENT_COLOR;
    private final Color SUCCESS_COLOR = BakeryTheme.SUCCESS;
    private final Color WARNING_COLOR = BakeryTheme.WARNING;
    private final Color ERROR_COLOR = BakeryTheme.ERROR;
    private final Color BACKGROUND_COLOR = BakeryTheme.BACKGROUND_COLOR;
    private final Color CARD_BG = BakeryTheme.CARD_COLOR;
    
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    
    private CustomOrderController controller;
    private CustomProductDAO productDAO;
    private CustomOrderDAO orderDAO;
    
    private String customerId;
    private String customerName;
    private Runnable onLogout;
    
    // Current order tracking
    private List<CustomProduct> availableProducts;
    private Map<String, OrderAddOn> selectedAddons;
    private CustomProduct selectedProduct;
    
    // UI Components
    private JLabel welcomeLabel;
    private JTabbedPane tabbedPane;
    
    // New Order Tab
    private JComboBox<String> productComboBox;
    private JTextArea productDescriptionArea;
    private JLabel productImageLabel;
    private JLabel priceRangeLabel;
    private JLabel prepTimeLabel;
    private JSpinner servingsSpinner;
    private JLabel servingsRangeLabel;
    private JTextField messageOnItemField;
    private JTextArea specialInstructionsArea;
    private JComboBox<String> fulfillmentComboBox;
    private JSpinner pickupDateSpinner;
    private JSpinner pickupTimeSpinner;
    private JTextField deliveryAddressField;
    
    // Add-ons panel
    private JPanel addonsPanel;
    private Map<String, JCheckBox> addonCheckboxes;
    
    // Price breakdown
    private JLabel basePriceLabel;
    private JLabel addonsLabel;
    private JLabel subtotalLabel;
    private JLabel taxLabel;
    private JLabel deliveryFeeLabel;
    private JLabel totalLabel;
    private JLabel depositLabel;
    private JProgressBar depositProgressBar;
    
    // Order History Tab
    private JTable ordersTable;
    private DefaultTableModel ordersTableModel;
    private JComboBox<String> statusFilterCombo;
    private JButton viewDetailsButton;
    private JButton cancelOrderButton;
    private JTextArea orderDetailsArea;
    
    // Notification system
    private Timer notificationTimer;
    private Map<String, String> lastOrderStatuses;
    private static final int NOTIFICATION_CHECK_INTERVAL = 30000; // 30 seconds
    private JLabel notificationBadge;
    
    public SimpleOrderPanel(Runnable onLogout) {
        this.onLogout = onLogout;
        this.controller = new CustomOrderController();
        this.productDAO = new CustomProductDAO();
        this.orderDAO = new CustomOrderDAO();
        this.selectedAddons = new HashMap<>();
        this.addonCheckboxes = new HashMap<>();
        this.availableProducts = new ArrayList<>();
        this.lastOrderStatuses = new HashMap<>();
        
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        initComponents();
        initNotificationSystem();
    }
    
    public void setCustomer(String customerId, String customerName) {
        this.customerId = customerId;
        this.customerName = customerName;
        welcomeLabel.setText("Welcome, " + customerName + "!");
        loadOrderHistory();
        startNotificationMonitoring();
    }
    
    private void initComponents() {
        // Header with logo, welcome and logout
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_BG);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(PRIMARY_COLOR, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Left panel with logo and welcome message
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setBackground(CARD_BG);
        
        // Load and add logo
        try {
            java.net.URL logoURL = getClass().getResource("/store-logo.png");
            if (logoURL != null) {
                ImageIcon logoIcon = new ImageIcon(logoURL);
                // Scale logo to smaller size for header
                Image scaledLogo = logoIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
                leftPanel.add(logoLabel);
            } else {
                System.err.println("Could not find store logo at /store-logo.png");
            }
        } catch (Exception e) {
            System.err.println("Could not load store logo: " + e.getMessage());
        }
        
        JPanel welcomePanel = new JPanel(new BorderLayout(10, 5));
        welcomePanel.setBackground(CARD_BG);
        
        welcomeLabel = new JLabel("Welcome!");
        welcomeLabel.setFont(TITLE_FONT);
        welcomeLabel.setForeground(PRIMARY_COLOR);
        
        JLabel subtitleLabel = new JLabel("Create your perfect custom order");
        subtitleLabel.setFont(SMALL_FONT);
        subtitleLabel.setForeground(Color.GRAY);
        
        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);
        welcomePanel.add(subtitleLabel, BorderLayout.CENTER);
        
        leftPanel.add(welcomePanel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(CARD_BG);
        
        // Notification badge
        notificationBadge = new JLabel("🔔");
        notificationBadge.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        notificationBadge.setToolTipText("No new notifications");
        notificationBadge.setCursor(new Cursor(Cursor.HAND_CURSOR));
        notificationBadge.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showNotificationHistory();
            }
        });
        
        JButton refreshButton = createStyledButton("🔄 Refresh", ACCENT_COLOR);
        refreshButton.addActionListener(e -> {
            loadProducts();
            loadOrderHistory();
            JOptionPane.showMessageDialog(this, "Data refreshed!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton logoutButton = createStyledButton("Logout", ERROR_COLOR);
        logoutButton.addActionListener(e -> {
            stopNotificationMonitoring();
            onLogout.run();
        });
        
        buttonPanel.add(notificationBadge);
        buttonPanel.add(refreshButton);
        buttonPanel.add(logoutButton);
        
        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(SUBTITLE_FONT);
        tabbedPane.setBackground(CARD_BG);
        
        tabbedPane.addTab("📝 Place Order", createNewOrderPanel());
        tabbedPane.addTab("📋 My Orders", createOrderHistoryPanel());
        
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(REGULAR_FONT);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(backgroundColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }
    
    private JPanel createNewOrderPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Left panel - Product selection and details
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(BACKGROUND_COLOR);
        leftPanel.setPreferredSize(new Dimension(400, 0));
        
        // Product selection card
        JPanel productSelectionCard = createCard("Product Selection");
        productSelectionCard.setLayout(new BorderLayout(10, 10));
        
        JPanel productSelectPanel = new JPanel(new BorderLayout(5, 5));
        productSelectPanel.setBackground(CARD_BG);
        
        JLabel productLabel = new JLabel("Choose Product:");
        productLabel.setFont(REGULAR_FONT);
        
        productComboBox = new JComboBox<>();
        productComboBox.setFont(REGULAR_FONT);
        productComboBox.addActionListener(e -> onProductSelected());
        
        productSelectPanel.add(productLabel, BorderLayout.NORTH);
        productSelectPanel.add(productComboBox, BorderLayout.CENTER);
        
        productSelectionCard.add(productSelectPanel, BorderLayout.NORTH);
        
        // Product details
        JPanel detailsPanel = new JPanel(new BorderLayout(10, 10));
        detailsPanel.setBackground(CARD_BG);
        
        productImageLabel = new JLabel("Select a product to view details", SwingConstants.CENTER);
        productImageLabel.setPreferredSize(new Dimension(350, 150));
        productImageLabel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
        productImageLabel.setOpaque(true);
        productImageLabel.setBackground(Color.WHITE);
        
        productDescriptionArea = new JTextArea(4, 30);
        productDescriptionArea.setLineWrap(true);
        productDescriptionArea.setWrapStyleWord(true);
        productDescriptionArea.setEditable(false);
        productDescriptionArea.setFont(SMALL_FONT);
        productDescriptionArea.setBackground(BakeryTheme.SOFT_CREAM);
        JScrollPane descScroll = new JScrollPane(productDescriptionArea);
        
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoPanel.setBackground(CARD_BG);
        
        priceRangeLabel = new JLabel("Price: --");
        priceRangeLabel.setFont(REGULAR_FONT);
        priceRangeLabel.setForeground(PRIMARY_COLOR);
        
        prepTimeLabel = new JLabel("Prep time: --");
        prepTimeLabel.setFont(SMALL_FONT);
        prepTimeLabel.setForeground(Color.GRAY);
        
        infoPanel.add(priceRangeLabel);
        infoPanel.add(prepTimeLabel);
        
        detailsPanel.add(productImageLabel, BorderLayout.NORTH);
        detailsPanel.add(descScroll, BorderLayout.CENTER);
        detailsPanel.add(infoPanel, BorderLayout.SOUTH);
        
        productSelectionCard.add(detailsPanel, BorderLayout.CENTER);
        
        leftPanel.add(productSelectionCard, BorderLayout.CENTER);
        
        // Center panel - Order configuration
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(BACKGROUND_COLOR);
        
        // Scrollable configuration panel
        JPanel configPanel = new JPanel();
        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
        configPanel.setBackground(BACKGROUND_COLOR);
        
        // Servings configuration
        JPanel servingsCard = createCard("Servings");
        servingsCard.setLayout(new BorderLayout(10, 10));
        
        JPanel servingsInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        servingsInputPanel.setBackground(CARD_BG);
        
        JLabel servingsLabel = new JLabel("Number of Servings:");
        servingsLabel.setFont(REGULAR_FONT);
        
        servingsSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 500, 1));
        servingsSpinner.setFont(REGULAR_FONT);
        ((JSpinner.DefaultEditor) servingsSpinner.getEditor()).getTextField().setColumns(5);
        servingsSpinner.addChangeListener(e -> updatePriceCalculation());
        
        servingsRangeLabel = new JLabel("(Min: 10, Max: 500)");
        servingsRangeLabel.setFont(SMALL_FONT);
        servingsRangeLabel.setForeground(Color.GRAY);
        
        servingsInputPanel.add(servingsLabel);
        servingsInputPanel.add(servingsSpinner);
        servingsInputPanel.add(servingsRangeLabel);
        
        servingsCard.add(servingsInputPanel, BorderLayout.CENTER);
        
        // Message on item
        JPanel messageCard = createCard("Message on Item (Optional)");
        messageCard.setLayout(new BorderLayout(5, 5));
        
        messageOnItemField = new JTextField();
        messageOnItemField.setFont(REGULAR_FONT);
        messageOnItemField.setToolTipText("e.g., 'Happy Birthday Sarah!'");
        
        JLabel messageHint = new JLabel("Custom message to be written on your order");
        messageHint.setFont(SMALL_FONT);
        messageHint.setForeground(Color.GRAY);
        
        messageCard.add(messageOnItemField, BorderLayout.CENTER);
        messageCard.add(messageHint, BorderLayout.SOUTH);
        
        // Add-ons selection
        JPanel addonsCard = createCard("Add-ons (Optional)");
        addonsCard.setLayout(new BorderLayout(5, 5));
        
        addonsPanel = new JPanel();
        addonsPanel.setLayout(new BoxLayout(addonsPanel, BoxLayout.Y_AXIS));
        addonsPanel.setBackground(CARD_BG);
        
        JScrollPane addonsScroll = new JScrollPane(addonsPanel);
        addonsScroll.setPreferredSize(new Dimension(0, 150));
        addonsScroll.setBorder(null);
        
        addonsCard.add(addonsScroll, BorderLayout.CENTER);
        
        // Special Instructions
        JPanel instructionsCard = createCard("Special Instructions");
        instructionsCard.setLayout(new BorderLayout(5, 5));
        
        specialInstructionsArea = new JTextArea(4, 30);
        specialInstructionsArea.setLineWrap(true);
        specialInstructionsArea.setWrapStyleWord(true);
        specialInstructionsArea.setFont(REGULAR_FONT);
        specialInstructionsArea.setBorder(new LineBorder(Color.LIGHT_GRAY));
        JScrollPane instructionsScroll = new JScrollPane(specialInstructionsArea);
        
        instructionsCard.add(instructionsScroll, BorderLayout.CENTER);
        
        // Fulfillment details
        JPanel fulfillmentCard = createCard("Delivery/Pickup Details");
        fulfillmentCard.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Fulfillment type
        gbc.gridx = 0; gbc.gridy = row;
        fulfillmentCard.add(new JLabel("Fulfillment:"), gbc);
        
        fulfillmentComboBox = new JComboBox<>(new String[]{"PICKUP", "DELIVERY"});
        fulfillmentComboBox.setFont(REGULAR_FONT);
        fulfillmentComboBox.addActionListener(e -> {
            updateDeliveryField();
            updatePriceCalculation();
        });
        gbc.gridx = 1; gbc.gridy = row++;
        gbc.weightx = 1.0;
        fulfillmentCard.add(fulfillmentComboBox, gbc);
        
        // Pickup Date
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.0;
        fulfillmentCard.add(new JLabel("Date:"), gbc);
        
        pickupDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(pickupDateSpinner, "yyyy-MM-dd");
        pickupDateSpinner.setEditor(dateEditor);
        pickupDateSpinner.setFont(REGULAR_FONT);
        gbc.gridx = 1; gbc.gridy = row++;
        gbc.weightx = 1.0;
        fulfillmentCard.add(pickupDateSpinner, gbc);
        
        // Pickup Time
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.0;
        fulfillmentCard.add(new JLabel("Time:"), gbc);
        
        pickupTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(pickupTimeSpinner, "HH:mm");
        pickupTimeSpinner.setEditor(timeEditor);
        pickupTimeSpinner.setFont(REGULAR_FONT);
        gbc.gridx = 1; gbc.gridy = row++;
        gbc.weightx = 1.0;
        fulfillmentCard.add(pickupTimeSpinner, gbc);
        
        // Minimum time info label
        gbc.gridx = 0; gbc.gridy = row++;
        gbc.gridwidth = 2;
        JLabel minTimeLabel = new JLabel("ℹ️ Minimum time will be set based on product prep time");
        minTimeLabel.setFont(SMALL_FONT);
        minTimeLabel.setForeground(BakeryTheme.TEXT_LIGHT);
        fulfillmentCard.add(minTimeLabel, gbc);
        gbc.gridwidth = 1;
        
        // Delivery Address
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.0;
        fulfillmentCard.add(new JLabel("Address:"), gbc);
        
        deliveryAddressField = new JTextField(30);
        deliveryAddressField.setFont(REGULAR_FONT);
        deliveryAddressField.setEnabled(false);
        gbc.gridx = 1; gbc.gridy = row++;
        gbc.weightx = 1.0;
        fulfillmentCard.add(deliveryAddressField, gbc);
        
        // Add all cards to config panel
        configPanel.add(servingsCard);
        configPanel.add(Box.createVerticalStrut(10));
        configPanel.add(messageCard);
        configPanel.add(Box.createVerticalStrut(10));
        configPanel.add(addonsCard);
        configPanel.add(Box.createVerticalStrut(10));
        configPanel.add(instructionsCard);
        configPanel.add(Box.createVerticalStrut(10));
        configPanel.add(fulfillmentCard);
        configPanel.add(Box.createVerticalGlue());
        
        JScrollPane configScroll = new JScrollPane(configPanel);
        configScroll.setBorder(null);
        configScroll.getVerticalScrollBar().setUnitIncrement(16);
        
        centerPanel.add(configScroll, BorderLayout.CENTER);
        
        // Right panel - Price summary and actions
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(BACKGROUND_COLOR);
        rightPanel.setPreferredSize(new Dimension(300, 0));
        
        JPanel summaryCard = createCard("Order Summary");
        summaryCard.setLayout(new BorderLayout(10, 10));
        
        JPanel priceBreakdownPanel = new JPanel(new GridLayout(0, 2, 10, 8));
        priceBreakdownPanel.setBackground(CARD_BG);
        
        basePriceLabel = createPriceLabel("Base Price:", "₱0.00");
        addonsLabel = createPriceLabel("Add-ons:", "₱0.00");
        subtotalLabel = createPriceLabel("Subtotal:", "₱0.00");
        taxLabel = createPriceLabel("Tax (12%):", "₱0.00");
        deliveryFeeLabel = createPriceLabel("Delivery Fee:", "₱0.00");
        
        priceBreakdownPanel.add(new JLabel("Base Price:"));
        priceBreakdownPanel.add(basePriceLabel);
        priceBreakdownPanel.add(new JLabel("Add-ons:"));
        priceBreakdownPanel.add(addonsLabel);
        priceBreakdownPanel.add(new JLabel("Subtotal:"));
        priceBreakdownPanel.add(subtotalLabel);
        priceBreakdownPanel.add(new JLabel("Tax (12%):"));
        priceBreakdownPanel.add(taxLabel);
        priceBreakdownPanel.add(new JLabel("Delivery:"));
        priceBreakdownPanel.add(deliveryFeeLabel);
        
        JPanel totalPanel = new JPanel(new BorderLayout(10, 10));
        totalPanel.setBackground(CARD_BG);
        totalPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(10, 0, 10, 0)
        ));
        
        totalLabel = new JLabel("₱0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        totalLabel.setForeground(PRIMARY_COLOR);
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JLabel totalLabelText = new JLabel("TOTAL:");
        totalLabelText.setFont(SUBTITLE_FONT);
        
        totalPanel.add(totalLabelText, BorderLayout.WEST);
        totalPanel.add(totalLabel, BorderLayout.EAST);
        
        JPanel depositPanel = new JPanel(new BorderLayout(5, 5));
        depositPanel.setBackground(BakeryTheme.lighter(BakeryTheme.WARNING, 0.8f));
        depositPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(WARNING_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        depositLabel = new JLabel("Required Deposit (50%): ₱0.00");
        depositLabel.setFont(SUBTITLE_FONT);
        depositLabel.setForeground(WARNING_COLOR.darker());
        
        depositProgressBar = new JProgressBar(0, 100);
        depositProgressBar.setValue(0);
        depositProgressBar.setStringPainted(true);
        depositProgressBar.setString("0% paid");
        depositProgressBar.setForeground(SUCCESS_COLOR);
        
        depositPanel.add(depositLabel, BorderLayout.NORTH);
        depositPanel.add(depositProgressBar, BorderLayout.SOUTH);
        
        JPanel actionPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        actionPanel.setBackground(CARD_BG);
        
        JButton calculateButton = createStyledButton("🧮 Calculate Total", ACCENT_COLOR);
        calculateButton.addActionListener(e -> updatePriceCalculation());
        
        JButton clearButton = createStyledButton("🗑️ Clear Form", WARNING_COLOR);
        clearButton.addActionListener(e -> clearForm());
        
        JButton submitButton = createStyledButton("✅ Place Order", SUCCESS_COLOR);
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        submitButton.addActionListener(e -> placeOrder());
        
        actionPanel.add(calculateButton);
        actionPanel.add(clearButton);
        actionPanel.add(submitButton);
        
        summaryCard.add(priceBreakdownPanel, BorderLayout.NORTH);
        summaryCard.add(totalPanel, BorderLayout.CENTER);
        summaryCard.add(depositPanel, BorderLayout.SOUTH);
        
        JPanel actionsCard = createCard("Actions");
        actionsCard.add(actionPanel);
        
        JPanel rightContentPanel = new JPanel(new BorderLayout(10, 10));
        rightContentPanel.setBackground(BACKGROUND_COLOR);
        rightContentPanel.add(summaryCard, BorderLayout.CENTER);
        rightContentPanel.add(actionsCard, BorderLayout.SOUTH);
        
        rightPanel.add(rightContentPanel, BorderLayout.NORTH);
        
        // Assemble main panel
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        
        // Load initial data
        loadProducts();
        loadAddons();
        
        return mainPanel;
    }
    
    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BakeryTheme.BORDER_LIGHT, 1),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            SUBTITLE_FONT,
            PRIMARY_COLOR
        ));
        return card;
    }
    
    private JLabel createPriceLabel(String text, String value) {
        JLabel label = new JLabel(value);
        label.setFont(REGULAR_FONT);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }
    
    private JPanel createOrderHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Top panel with filters
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(CARD_BG);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BakeryTheme.BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel filterLabel = new JLabel("Filter by Status:");
        filterLabel.setFont(REGULAR_FONT);
        
        statusFilterCombo = new JComboBox<>(new String[]{
            "All Orders", "PENDING", "CONFIRMED", "IN_PROGRESS", "COMPLETED", "CANCELLED"
        });
        statusFilterCombo.setFont(REGULAR_FONT);
        statusFilterCombo.addActionListener(e -> loadOrderHistory());
        
        topPanel.add(filterLabel);
        topPanel.add(statusFilterCombo);
        
        // Center panel with table
        String[] columns = {
            "Order #", "Product", "Status", "Fulfillment", 
            "Date", "Total", "Deposit", "Balance"
        };
        ordersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };
        
        ordersTable = new JTable(ordersTableModel);
        ordersTable.setFont(REGULAR_FONT);
        ordersTable.setRowHeight(32);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ordersTable.getTableHeader().setFont(SUBTITLE_FONT);
        ordersTable.getTableHeader().setBackground(PRIMARY_COLOR);
        ordersTable.getTableHeader().setForeground(Color.WHITE);
        ordersTable.setGridColor(BakeryTheme.BORDER_LIGHT);
        
        // Set column widths
        ordersTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        ordersTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        ordersTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        ordersTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        ordersTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        ordersTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        ordersTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        ordersTable.getColumnModel().getColumn(7).setPreferredWidth(100);
        
        // Custom cell renderer for status column
        ordersTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String status = value.toString();
                    switch (status) {
                        case "PENDING":
                            c.setBackground(BakeryTheme.lighter(BakeryTheme.WARNING, 0.85f));
                            c.setForeground(BakeryTheme.darker(WARNING_COLOR, 0.2f));
                            break;
                        case "CONFIRMED":
                        case "IN_PROGRESS":
                            c.setBackground(BakeryTheme.lighter(BakeryTheme.INFO, 0.85f));
                            c.setForeground(BakeryTheme.INFO);
                            break;
                        case "COMPLETED":
                            c.setBackground(BakeryTheme.lighter(BakeryTheme.SUCCESS, 0.85f));
                            c.setForeground(BakeryTheme.darker(SUCCESS_COLOR, 0.2f));
                            break;
                        case "CANCELLED":
                            c.setBackground(BakeryTheme.lighter(BakeryTheme.ERROR, 0.85f));
                            c.setForeground(ERROR_COLOR);
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                            c.setForeground(Color.BLACK);
                    }
                }
                setHorizontalAlignment(CENTER);
                return c;
            }
        });
        
        // Add selection listener
        ordersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = ordersTable.getSelectedRow();
                viewDetailsButton.setEnabled(selectedRow >= 0);
                cancelOrderButton.setEnabled(selectedRow >= 0);
            }
        });
        
        JScrollPane tableScroll = new JScrollPane(ordersTable);
        tableScroll.setBorder(new LineBorder(BakeryTheme.BORDER_LIGHT, 1));
        
        // Bottom panel with order details and actions
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.setPreferredSize(new Dimension(0, 250));
        
        // Order details card
        JPanel detailsCard = createCard("Order Details");
        detailsCard.setLayout(new BorderLayout());
        
        orderDetailsArea = new JTextArea();
        orderDetailsArea.setFont(SMALL_FONT);
        orderDetailsArea.setEditable(false);
        orderDetailsArea.setLineWrap(true);
        orderDetailsArea.setWrapStyleWord(true);
        orderDetailsArea.setText("Select an order to view details");
        orderDetailsArea.setBackground(BakeryTheme.SOFT_CREAM);
        
        JScrollPane detailsScroll = new JScrollPane(orderDetailsArea);
        detailsCard.add(detailsScroll, BorderLayout.CENTER);
        
        // Action buttons panel
        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionButtonsPanel.setBackground(CARD_BG);
        
        JButton refreshButton = createStyledButton("🔄 Refresh", PRIMARY_COLOR);
        refreshButton.addActionListener(e -> loadOrderHistory());
        
        viewDetailsButton = createStyledButton("👁️ View Full Details", ACCENT_COLOR);
        viewDetailsButton.setEnabled(false);
        viewDetailsButton.addActionListener(e -> showOrderDetails());
        
        cancelOrderButton = createStyledButton("❌ Cancel Order", ERROR_COLOR);
        cancelOrderButton.setEnabled(false);
        cancelOrderButton.addActionListener(e -> cancelOrder());
        
        actionButtonsPanel.add(refreshButton);
        actionButtonsPanel.add(viewDetailsButton);
        actionButtonsPanel.add(cancelOrderButton);
        
        detailsCard.add(actionButtonsPanel, BorderLayout.SOUTH);
        
        bottomPanel.add(detailsCard, BorderLayout.CENTER);
        
        // Assemble panel
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadProducts() {
        availableProducts = productDAO.getAllProducts();
        productComboBox.removeAllItems();
        
        for (CustomProduct product : availableProducts) {
            String display = String.format("%s - ₱%.2f/serving", 
                product.getProductName(), product.getPricePerServing());
            productComboBox.addItem(display);
        }
        
        if (!availableProducts.isEmpty()) {
            productComboBox.setSelectedIndex(0);
            onProductSelected();
        }
    }
    
    private void loadAddons() {
        // Load available add-ons from database
        List<Addon> addons = productDAO.getAllAddons();
        
        addonsPanel.removeAll();
        addonCheckboxes.clear();
        
        if (addons.isEmpty()) {
            JLabel noAddonsLabel = new JLabel("No add-ons available");
            noAddonsLabel.setFont(SMALL_FONT);
            noAddonsLabel.setForeground(Color.GRAY);
            addonsPanel.add(noAddonsLabel);
        } else {
            for (Addon addon : addons) {
                JPanel addonItem = new JPanel(new BorderLayout(10, 5));
                addonItem.setBackground(CARD_BG);
                addonItem.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                
                JCheckBox checkbox = new JCheckBox();
                checkbox.setBackground(CARD_BG);
                
                // Create OrderAddOn from Addon for storage
                OrderAddOn orderAddon = new OrderAddOn();
                orderAddon.setAddonCode(String.valueOf(addon.getAddonId()));
                orderAddon.setAddonName(addon.getAddonName());
                orderAddon.setCategoryCode(String.valueOf(addon.getCategoryId()));
                orderAddon.setPriceModifier(addon.getAddonPrice());
                orderAddon.setPriceType(OrderAddOn.PriceType.FLAT); // Default to FLAT
                orderAddon.setDescription(addon.getDescription());
                
                checkbox.addActionListener(e -> {
                    if (checkbox.isSelected()) {
                        selectedAddons.put(orderAddon.getAddonCode(), orderAddon);
                    } else {
                        selectedAddons.remove(orderAddon.getAddonCode());
                    }
                    updatePriceCalculation();
                });
                
                addonCheckboxes.put(String.valueOf(addon.getAddonId()), checkbox);
                
                JPanel infoPanel = new JPanel(new BorderLayout());
                infoPanel.setBackground(CARD_BG);
                
                JLabel nameLabel = new JLabel(addon.getAddonName());
                nameLabel.setFont(REGULAR_FONT);
                
                String priceText = String.format("₱%.2f", addon.getAddonPrice());
                
                JLabel priceLabel = new JLabel(priceText);
                priceLabel.setFont(SMALL_FONT);
                priceLabel.setForeground(ACCENT_COLOR);
                
                infoPanel.add(nameLabel, BorderLayout.WEST);
                infoPanel.add(priceLabel, BorderLayout.EAST);
                
                if (addon.getDescription() != null && !addon.getDescription().isEmpty()) {
                    JLabel descLabel = new JLabel(addon.getDescription());
                    descLabel.setFont(SMALL_FONT);
                    descLabel.setForeground(Color.GRAY);
                    infoPanel.add(descLabel, BorderLayout.SOUTH);
                }
                
                addonItem.add(checkbox, BorderLayout.WEST);
                addonItem.add(infoPanel, BorderLayout.CENTER);
                
                addonsPanel.add(addonItem);
                addonsPanel.add(Box.createVerticalStrut(5));
            }
        }
        
        addonsPanel.revalidate();
        addonsPanel.repaint();
    }
    
    private void onProductSelected() {
        int selectedIndex = productComboBox.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= availableProducts.size()) {
            return;
        }
        
        selectedProduct = availableProducts.get(selectedIndex);
        
        // Update product details
        productDescriptionArea.setText(selectedProduct.getDescription() != null ? 
            selectedProduct.getDescription() : "No description available");
        
        priceRangeLabel.setText(String.format("₱%.2f per serving (Base: ₱%.2f)", 
            selectedProduct.getPricePerServing(), selectedProduct.getBasePrice()));
        
        prepTimeLabel.setText(String.format("Preparation time: %d hours (Ready after %s)", 
            selectedProduct.getPreparationTimeHours(),
            getMinimumReadyDateTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a"))));
        
        // Update servings range
        servingsRangeLabel.setText(String.format("(Min: %d, Max: %d)", 
            selectedProduct.getMinServings(), selectedProduct.getMaxServings()));
        
        // Set default servings to minimum
        servingsSpinner.setModel(new SpinnerNumberModel(
            selectedProduct.getMinServings(),
            selectedProduct.getMinServings(),
            selectedProduct.getMaxServings(),
            1
        ));
        
        // Update image (placeholder for now)
        productImageLabel.setText(selectedProduct.getProductName());
        productImageLabel.setFont(TITLE_FONT);
        productImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Update minimum pickup date/time based on preparation time
        updateMinimumPickupDateTime();
        
        // Update price calculation
        updatePriceCalculation();
    }
    
    private void updateDeliveryField() {
        boolean isDelivery = "DELIVERY".equals(fulfillmentComboBox.getSelectedItem());
        deliveryAddressField.setEnabled(isDelivery);
        if (!isDelivery) {
            deliveryAddressField.setText("");
        }
    }
    
    private void updatePriceCalculation() {
        if (selectedProduct == null) {
            return;
        }
        
        try {
            int servings = (int) servingsSpinner.getValue();
            double basePrice = selectedProduct.getPricePerServing() * servings;
            
            // Calculate add-ons total
            double addonsTotal = 0.0;
            for (OrderAddOn addon : selectedAddons.values()) {
                switch (addon.getPriceType()) {
                    case FLAT:
                        addonsTotal += addon.getPriceModifier();
                        break;
                    case PERCENTAGE:
                        addonsTotal += basePrice * addon.getPriceModifier();
                        break;
                    case PER_SERVING:
                        addonsTotal += addon.getPriceModifier() * servings;
                        break;
                }
            }
            
            double subtotal = basePrice + addonsTotal;
            double tax = subtotal * 0.12; // 12% VAT
            
            double deliveryFee = 0.0;
            if ("DELIVERY".equals(fulfillmentComboBox.getSelectedItem())) {
                deliveryFee = 200.0;
            }
            
            double total = subtotal + tax + deliveryFee;
            double deposit = total * 0.5;
            
            // Update UI
            basePriceLabel.setText(String.format("₱%.2f", basePrice));
            addonsLabel.setText(String.format("₱%.2f", addonsTotal));
            subtotalLabel.setText(String.format("₱%.2f", subtotal));
            taxLabel.setText(String.format("₱%.2f", tax));
            deliveryFeeLabel.setText(String.format("₱%.2f", deliveryFee));
            totalLabel.setText(String.format("₱%.2f", total));
            depositLabel.setText(String.format("Required Deposit (50%%): ₱%.2f", deposit));
            
        } catch (Exception ex) {
            System.err.println("Error calculating price: " + ex.getMessage());
        }
    }
    
    /**
     * Get the minimum ready date/time based on product preparation time
     */
    private LocalDateTime getMinimumReadyDateTime() {
        if (selectedProduct == null) {
            return LocalDateTime.now().plusHours(24); // Default 24 hours
        }
        return LocalDateTime.now().plusHours(selectedProduct.getPreparationTimeHours());
    }
    
    /**
     * Update the minimum pickup date/time spinners based on preparation time
     */
    private void updateMinimumPickupDateTime() {
        if (selectedProduct == null) {
            return;
        }
        
        LocalDateTime minDateTime = getMinimumReadyDateTime();
        java.util.Date minDate = java.util.Date.from(
            minDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant()
        );
        
        // Update date spinner with minimum date
        pickupDateSpinner.setValue(minDate);
        
        // Update time spinner with minimum time
        pickupTimeSpinner.setValue(minDate);
        
        // Add tooltip to inform user
        String tooltipText = String.format(
            "Earliest available: %s (requires %d hours preparation)",
            minDateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a")),
            selectedProduct.getPreparationTimeHours()
        );
        pickupDateSpinner.setToolTipText(tooltipText);
        pickupTimeSpinner.setToolTipText(tooltipText);
    }
    
    private void placeOrder() {
        try {
            // Validate inputs
            if (selectedProduct == null) {
                JOptionPane.showMessageDialog(this, "Please select a product", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int servings = (int) servingsSpinner.getValue();
            
            // Validate servings range
            if (servings < selectedProduct.getMinServings() || servings > selectedProduct.getMaxServings()) {
                JOptionPane.showMessageDialog(this, 
                    String.format("Servings must be between %d and %d", 
                        selectedProduct.getMinServings(), selectedProduct.getMaxServings()), 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String messageOnItem = messageOnItemField.getText().trim();
            String specialInstructions = specialInstructionsArea.getText().trim();
            String fulfillmentStr = (String) fulfillmentComboBox.getSelectedItem();
            CustomOrder.FulfillmentType fulfillment = CustomOrder.FulfillmentType.valueOf(fulfillmentStr);
            
            // Get pickup date/time
            java.util.Date dateValue = (java.util.Date) pickupDateSpinner.getValue();
            java.util.Date timeValue = (java.util.Date) pickupTimeSpinner.getValue();
            
            LocalDateTime pickupDateTime = LocalDateTime.of(
                dateValue.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate(),
                timeValue.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime()
            );
            
            // Validate date is in the future and meets minimum preparation time
            LocalDateTime minimumDateTime = getMinimumReadyDateTime();
            if (pickupDateTime.isBefore(minimumDateTime)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a");
                String fulfillmentText = fulfillment == CustomOrder.FulfillmentType.DELIVERY ? 
                    "Delivery" : "Pickup";
                
                JOptionPane.showMessageDialog(this, 
                    String.format(
                        "%s time is too early!\n\n" +
                        "This product requires %d hours of preparation time.\n\n" +
                        "Selected: %s\n" +
                        "Earliest available: %s\n\n" +
                        "Please select a later date/time.",
                        fulfillmentText,
                        selectedProduct.getPreparationTimeHours(),
                        pickupDateTime.format(formatter),
                        minimumDateTime.format(formatter)
                    ), 
                    "Invalid " + fulfillmentText + " Time", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Validate delivery address if needed
            Integer deliveryAddressId = null;
            if (fulfillment == CustomOrder.FulfillmentType.DELIVERY) {
                String address = deliveryAddressField.getText().trim();
                if (address.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter delivery address", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // TODO: For now, we skip delivery address validation
                // In production, you should create/lookup the address in customer_addresses table
                // and get the actual ID. For now, we'll leave it as null to avoid FK constraint errors.
                deliveryAddressId = null;
                
                JOptionPane.showMessageDialog(this, 
                    "Note: Delivery address will be recorded as: " + address + "\n" +
                    "Delivery address management is not yet fully implemented.", 
                    "Delivery Address", JOptionPane.INFORMATION_MESSAGE);
            }
            
            // Confirm order with user
            int confirm = JOptionPane.showConfirmDialog(this, 
                buildOrderConfirmationMessage(), 
                "Confirm Order", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
            
            // Create order
            controller.startNewOrder(customerId, selectedProduct.getProductCode(), servings);
            
            // Add message on item if provided
            if (!messageOnItem.isEmpty()) {
                controller.setOrderMessage(messageOnItem);
            }
            
            // Add special instructions
            if (!specialInstructions.isEmpty()) {
                controller.setSpecialInstructions(specialInstructions);
            }
            
            // Add selected add-ons
            for (OrderAddOn addon : selectedAddons.values()) {
                controller.addAddon(addon.getAddonCode());
            }
            
            // Set fulfillment details
            controller.setFulfillmentDetails(fulfillment, pickupDateTime, deliveryAddressId);
            
            // Submit order
            CustomOrderController.SubmitResult result = controller.submitOrder();
            
            if (result.success) {
                JOptionPane.showMessageDialog(this, 
                    buildSuccessMessage(result.orderNumber), 
                    "Order Placed Successfully!", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Clear form and refresh
                clearForm();
                loadOrderHistory();
                tabbedPane.setSelectedIndex(1); // Switch to order history tab
                
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to place order:\n" + result.message, 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error placing order:\n" + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private String buildOrderConfirmationMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Please confirm your order:\n\n");
        sb.append("Product: ").append(selectedProduct.getProductName()).append("\n");
        sb.append("Servings: ").append(servingsSpinner.getValue()).append("\n");
        
        if (!messageOnItemField.getText().trim().isEmpty()) {
            sb.append("Message: ").append(messageOnItemField.getText().trim()).append("\n");
        }
        
        if (!selectedAddons.isEmpty()) {
            sb.append("\nAdd-ons:\n");
            for (OrderAddOn addon : selectedAddons.values()) {
                sb.append("  • ").append(addon.getAddonName()).append("\n");
            }
        }
        
        sb.append("\nFulfillment: ").append(fulfillmentComboBox.getSelectedItem()).append("\n");
        sb.append("Date/Time: ").append(pickupDateSpinner.getValue()).append(" ");
        sb.append(pickupTimeSpinner.getValue()).append("\n");
        
        sb.append("\n").append(totalLabel.getText());
        
        return sb.toString();
    }
    
    private String buildSuccessMessage(String orderNumber) {
        StringBuilder sb = new StringBuilder();
        sb.append("Your order has been placed successfully!\n\n");
        sb.append("Order Number: ").append(orderNumber).append("\n\n");
        sb.append("You will receive a confirmation email shortly.\n");
        sb.append("Please pay the required deposit to confirm your order.\n\n");
        sb.append(depositLabel.getText());
        
        return sb.toString();
    }
    
    private void clearForm() {
        if (productComboBox.getItemCount() > 0) {
            productComboBox.setSelectedIndex(0);
        }
        
        if (selectedProduct != null) {
            servingsSpinner.setValue(selectedProduct.getMinServings());
        }
        
        messageOnItemField.setText("");
        specialInstructionsArea.setText("");
        fulfillmentComboBox.setSelectedIndex(0);
        deliveryAddressField.setText("");
        
        // Clear selected add-ons
        selectedAddons.clear();
        for (JCheckBox checkbox : addonCheckboxes.values()) {
            checkbox.setSelected(false);
        }
        
        // Reset price labels
        basePriceLabel.setText("₱0.00");
        addonsLabel.setText("₱0.00");
        subtotalLabel.setText("₱0.00");
        taxLabel.setText("₱0.00");
        deliveryFeeLabel.setText("₱0.00");
        totalLabel.setText("₱0.00");
        depositLabel.setText("Required Deposit (50%): ₱0.00");
        depositProgressBar.setValue(0);
        depositProgressBar.setString("0% paid");
        
        // Recalculate
        updatePriceCalculation();
    }
    
    private void loadOrderHistory() {
        ordersTableModel.setRowCount(0);
        
        if (customerId == null || customerId.isEmpty()) {
            return;
        }
        
        List<CustomOrder> orders = orderDAO.findByCustomerId(customerId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        // Apply status filter
        String selectedFilter = (String) statusFilterCombo.getSelectedItem();
        
        for (CustomOrder order : orders) {
            // Filter by status
            if (!"All Orders".equals(selectedFilter) && 
                !order.getOrderStatus().toString().equals(selectedFilter)) {
                continue;
            }
            
            ordersTableModel.addRow(new Object[]{
                order.getOrderNumber(),
                order.getProductName(),
                order.getOrderStatus(),
                order.getFulfillmentType(),
                order.getPickupDatetime() != null ? 
                    order.getPickupDatetime().format(formatter) : "N/A",
                String.format("₱%.2f", order.getTotalAmount()),
                String.format("₱%.2f", order.getDepositPaid()),
                String.format("₱%.2f", order.getBalanceDue())
            });
        }
        
        // Update summary
        updateOrderSummary();
    }
    
    private void updateOrderSummary() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow < 0) {
            orderDetailsArea.setText("Select an order to view details");
            return;
        }
        
        if (customerId == null || customerId.isEmpty()) {
            return;
        }
        
        String orderNumber = (String) ordersTable.getValueAt(selectedRow, 0);
        CustomOrder order = orderDAO.findByOrderNumber(orderNumber);
        
        if (order == null) {
            orderDetailsArea.setText("Order not found");
            return;
        }
        
        StringBuilder details = new StringBuilder();
        details.append("═══════════════════════════════════\n");
        details.append("         ORDER SUMMARY\n");
        details.append("═══════════════════════════════════\n\n");
        
        details.append(String.format("Order Number: %s\n", order.getOrderNumber()));
        details.append(String.format("Status: %s\n", order.getOrderStatus()));
        details.append(String.format("Product: %s\n", order.getProductName()));
        details.append(String.format("Servings: %d\n", order.getServings()));
        
        if (order.getMessageOnItem() != null && !order.getMessageOnItem().isEmpty()) {
            details.append(String.format("Message: %s\n", order.getMessageOnItem()));
        }
        
        details.append("\n--- PRICING ---\n");
        details.append(String.format("Base Price: ₱%.2f\n", order.getBasePrice()));
        details.append(String.format("Add-ons: ₱%.2f\n", order.getAddonsTotal()));
        details.append(String.format("Subtotal: ₱%.2f\n", order.getSubtotal()));
        details.append(String.format("Tax: ₱%.2f\n", order.getTaxAmount()));
        details.append(String.format("Delivery Fee: ₱%.2f\n", order.getDeliveryFee()));
        details.append(String.format("TOTAL: ₱%.2f\n", order.getTotalAmount()));
        
        details.append("\n--- PAYMENT ---\n");
        details.append(String.format("Deposit Required: ₱%.2f\n", order.getDepositRequired()));
        details.append(String.format("Deposit Paid: ₱%.2f\n", order.getDepositPaid()));
        details.append(String.format("Balance Due: ₱%.2f\n", order.getBalanceDue()));
        details.append(String.format("Payment Status: %s\n", order.getPaymentStatus()));
        
        details.append("\n--- FULFILLMENT ---\n");
        details.append(String.format("Type: %s\n", order.getFulfillmentType()));
        if (order.getPickupDatetime() != null) {
            details.append(String.format("Date/Time: %s\n", 
                order.getPickupDatetime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        }
        
        if (order.getSpecialInstructions() != null && !order.getSpecialInstructions().isEmpty()) {
            details.append("\n--- SPECIAL INSTRUCTIONS ---\n");
            details.append(order.getSpecialInstructions()).append("\n");
        }
        
        orderDetailsArea.setText(details.toString());
        
        // Update deposit progress bar
        double depositPercent = (order.getDepositPaid() / order.getDepositRequired()) * 100;
        depositProgressBar.setValue((int) depositPercent);
        depositProgressBar.setString(String.format("%.0f%% paid", depositPercent));
    }
    
    private void showOrderDetails() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }
        
        updateOrderSummary();
        
        // Show in a dialog for full view
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Order Details", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        
        JTextArea detailsArea = new JTextArea();
        detailsArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        detailsArea.setEditable(false);
        detailsArea.setText(orderDetailsArea.getText());
        detailsArea.setCaretPosition(0);
        
        JScrollPane scroll = new JScrollPane(detailsArea);
        dialog.add(scroll, BorderLayout.CENTER);
        
        JButton closeButton = createStyledButton("Close", PRIMARY_COLOR);
        closeButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void cancelOrder() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }
        
        String orderNumber = (String) ordersTable.getValueAt(selectedRow, 0);
        String status = (String) ordersTable.getValueAt(selectedRow, 2);
        
        // Check if order can be cancelled
        if ("CANCELLED".equals(status) || "COMPLETED".equals(status)) {
            JOptionPane.showMessageDialog(this, 
                "This order cannot be cancelled (Status: " + status + ")", 
                "Cannot Cancel", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Confirm cancellation
        String reason = JOptionPane.showInputDialog(this, 
            "Please provide a reason for cancellation:", 
            "Cancel Order", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (reason == null || reason.trim().isEmpty()) {
            return; // User cancelled
        }
        
        try {
            boolean success = controller.cancelOrder(orderNumber, reason.trim(), customerId);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Order has been cancelled successfully.\nOrder Number: " + orderNumber, 
                    "Order Cancelled", JOptionPane.INFORMATION_MESSAGE);
                loadOrderHistory();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to cancel order. Please try again or contact support.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error cancelling order:\n" + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    // ==================== NOTIFICATION SYSTEM ====================
    
    /**
     * Initialize the notification system
     */
    private void initNotificationSystem() {
        lastOrderStatuses = new HashMap<>();
    }
    
    /**
     * Start monitoring order status changes
     */
    private void startNotificationMonitoring() {
        if (customerId == null || customerId.isEmpty()) {
            return;
        }
        
        // Initialize order statuses
        List<CustomOrder> orders = orderDAO.findByCustomerId(customerId);
        for (CustomOrder order : orders) {
            lastOrderStatuses.put(order.getOrderNumber(), order.getOrderStatus().toString());
        }
        
        // Create and start timer for periodic checks
        notificationTimer = new Timer(NOTIFICATION_CHECK_INTERVAL, e -> checkForStatusUpdates());
        notificationTimer.start();
    }
    
    /**
     * Stop notification monitoring
     */
    private void stopNotificationMonitoring() {
        if (notificationTimer != null) {
            notificationTimer.stop();
            notificationTimer = null;
        }
    }
    
    /**
     * Check for order status updates
     */
    private void checkForStatusUpdates() {
        if (customerId == null || customerId.isEmpty()) {
            return;
        }
        
        try {
            List<CustomOrder> orders = orderDAO.findByCustomerId(customerId);
            
            for (CustomOrder order : orders) {
                String orderNumber = order.getOrderNumber();
                String currentStatus = order.getOrderStatus().toString();
                String lastStatus = lastOrderStatuses.get(orderNumber);
                
                // Check if status changed
                if (lastStatus != null && !lastStatus.equals(currentStatus)) {
                    // Check if it's a notification-worthy status
                    if (isNotificationWorthyStatus(currentStatus, order.getFulfillmentType())) {
                        showNotification(order);
                    }
                    
                    // Update stored status
                    lastOrderStatuses.put(orderNumber, currentStatus);
                } else if (lastStatus == null) {
                    // New order detected
                    lastOrderStatuses.put(orderNumber, currentStatus);
                }
            }
            
            // Update notification badge
            updateNotificationBadge();
            
        } catch (Exception ex) {
            System.err.println("Error checking order status updates: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    /**
     * Determine if a status change is worth notifying about
     */
    private boolean isNotificationWorthyStatus(String status, CustomOrder.FulfillmentType fulfillmentType) {
        // Notify when order is ready for pickup or delivery
        if ("COMPLETED".equals(status)) {
            return true;
        }
        
        // Notify when order is in progress (being prepared)
        if ("IN_PROGRESS".equals(status)) {
            return true;
        }
        
        // Notify when order is confirmed
        if ("CONFIRMED".equals(status)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Show notification for an order status change
     */
    private void showNotification(CustomOrder order) {
        SwingUtilities.invokeLater(() -> {
            String title = getNotificationTitle(order);
            String message = getNotificationMessage(order);
            
            // Create custom notification dialog
            JDialog notificationDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                title, false);
            notificationDialog.setLayout(new BorderLayout(10, 10));
            notificationDialog.setSize(400, 250);
            notificationDialog.setLocationRelativeTo(this);
            notificationDialog.setAlwaysOnTop(true);
            
            // Icon panel
            JPanel iconPanel = new JPanel();
            iconPanel.setBackground(Color.WHITE);
            iconPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
            
            JLabel iconLabel = new JLabel(getStatusIcon(order.getOrderStatus().toString()));
            iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
            iconPanel.add(iconLabel);
            
            // Message panel
            JPanel messagePanel = new JPanel(new BorderLayout(10, 10));
            messagePanel.setBackground(Color.WHITE);
            messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
            
            JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titleLabel.setForeground(PRIMARY_COLOR);
            
            JTextArea messageArea = new JTextArea(message);
            messageArea.setFont(REGULAR_FONT);
            messageArea.setEditable(false);
            messageArea.setLineWrap(true);
            messageArea.setWrapStyleWord(true);
            messageArea.setBackground(Color.WHITE);
            messageArea.setBorder(null);
            
            messagePanel.add(titleLabel, BorderLayout.NORTH);
            messagePanel.add(messageArea, BorderLayout.CENTER);
            
            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            buttonPanel.setBackground(Color.WHITE);
            
            JButton viewButton = createStyledButton("View Order", PRIMARY_COLOR);
            viewButton.addActionListener(e -> {
                notificationDialog.dispose();
                tabbedPane.setSelectedIndex(1); // Switch to order history
                highlightOrder(order.getOrderNumber());
            });
            
            JButton dismissButton = createStyledButton("Dismiss", Color.GRAY);
            dismissButton.addActionListener(e -> notificationDialog.dispose());
            
            buttonPanel.add(viewButton);
            buttonPanel.add(dismissButton);
            
            // Assemble dialog
            notificationDialog.add(iconPanel, BorderLayout.NORTH);
            notificationDialog.add(messagePanel, BorderLayout.CENTER);
            notificationDialog.add(buttonPanel, BorderLayout.SOUTH);
            
            notificationDialog.setVisible(true);
            
            // Auto-dismiss after 10 seconds
            Timer dismissTimer = new Timer(10000, e -> notificationDialog.dispose());
            dismissTimer.setRepeats(false);
            dismissTimer.start();
            
            // Play notification sound (beep)
            Toolkit.getDefaultToolkit().beep();
        });
    }
    
    /**
     * Get notification title based on order status
     */
    private String getNotificationTitle(CustomOrder order) {
        String status = order.getOrderStatus().toString();
        switch (status) {
            case "CONFIRMED":
                return "Order Confirmed!";
            case "IN_PROGRESS":
                return "Order Being Prepared!";
            case "COMPLETED":
                return order.getFulfillmentType() == CustomOrder.FulfillmentType.DELIVERY
                    ? "Order Ready for Delivery!"
                    : "Order Ready for Pickup!";
            default:
                return "Order Update";
        }
    }
    
    /**
     * Get notification message based on order status
     */
    private String getNotificationMessage(CustomOrder order) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a");
        String status = order.getOrderStatus().toString();
        
        switch (status) {
            case "CONFIRMED":
                return String.format(
                    "Your order #%s has been confirmed!\n\n" +
                    "Product: %s\n" +
                    "Scheduled for: %s\n\n" +
                    "We'll notify you when it's ready.",
                    order.getOrderNumber(),
                    order.getProductName(),
                    order.getPickupDatetime() != null ? order.getPickupDatetime().format(formatter) : "TBD"
                );
            case "IN_PROGRESS":
                return String.format(
                    "Good news! Your order #%s is being prepared.\n\n" +
                    "Product: %s\n" +
                    "We're working on it now!",
                    order.getOrderNumber(),
                    order.getProductName()
                );
            case "COMPLETED":
                if (order.getFulfillmentType() == CustomOrder.FulfillmentType.DELIVERY) {
                    return String.format(
                        "Your order #%s is ready for delivery!\n\n" +
                        "Product: %s\n" +
                        "Scheduled delivery: %s\n\n" +
                        "Please ensure someone is available to receive it.",
                        order.getOrderNumber(),
                        order.getProductName(),
                        order.getDeliveryDatetime() != null ? order.getDeliveryDatetime().format(formatter) : "Soon"
                    );
                } else {
                    return String.format(
                        "Your order #%s is ready for pickup!\n\n" +
                        "Product: %s\n" +
                        "Pickup time: %s\n\n" +
                        "Please bring your order number.",
                        order.getOrderNumber(),
                        order.getProductName(),
                        order.getPickupDatetime() != null ? order.getPickupDatetime().format(formatter) : "Anytime"
                    );
                }
            default:
                return String.format(
                    "Order #%s has been updated.\n\nNew status: %s",
                    order.getOrderNumber(),
                    order.getOrderStatus()
                );
        }
    }
    
    /**
     * Get icon for order status
     */
    private String getStatusIcon(String status) {
        switch (status) {
            case "CONFIRMED":
                return "✅";
            case "IN_PROGRESS":
                return "👨‍🍳";
            case "COMPLETED":
                return "🎉";
            default:
                return "📦";
        }
    }
    
    /**
     * Update notification badge appearance
     */
    private void updateNotificationBadge() {
        // Count orders that are ready
        int readyCount = 0;
        
        if (customerId != null && !customerId.isEmpty()) {
            List<CustomOrder> orders = orderDAO.findByCustomerId(customerId);
            for (CustomOrder order : orders) {
                String status = order.getOrderStatus().toString();
                if ("COMPLETED".equals(status)) {
                    readyCount++;
                }
            }
        }
        
        if (readyCount > 0) {
            notificationBadge.setText("🔔 " + readyCount);
            notificationBadge.setForeground(ERROR_COLOR);
            notificationBadge.setToolTipText(readyCount + " order(s) ready!");
        } else {
            notificationBadge.setText("🔔");
            notificationBadge.setForeground(Color.GRAY);
            notificationBadge.setToolTipText("No new notifications");
        }
    }
    
    /**
     * Show notification history dialog
     */
    private void showNotificationHistory() {
        if (customerId == null || customerId.isEmpty()) {
            return;
        }
        
        List<CustomOrder> orders = orderDAO.findByCustomerId(customerId);
        List<CustomOrder> readyOrders = new ArrayList<>();
        
        for (CustomOrder order : orders) {
            if ("COMPLETED".equals(order.getOrderStatus().toString())) {
                readyOrders.add(order);
            }
        }
        
        if (readyOrders.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "You have no orders ready for pickup or delivery.",
                "No Notifications",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder message = new StringBuilder();
        message.append("Orders Ready:\n\n");
        
        for (CustomOrder order : readyOrders) {
            message.append(String.format("📦 Order #%s - %s\n",
                order.getOrderNumber(),
                order.getProductName()));
            message.append(String.format("   Status: %s for %s\n\n",
                "Ready",
                order.getFulfillmentType() == CustomOrder.FulfillmentType.DELIVERY ? "delivery" : "pickup"));
        }
        
        JOptionPane.showMessageDialog(this,
            message.toString(),
            "Ready Orders (" + readyOrders.size() + ")",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Highlight an order in the table
     */
    private void highlightOrder(String orderNumber) {
        for (int i = 0; i < ordersTable.getRowCount(); i++) {
            String tableOrderNumber = (String) ordersTable.getValueAt(i, 0);
            if (orderNumber.equals(tableOrderNumber)) {
                ordersTable.setRowSelectionInterval(i, i);
                ordersTable.scrollRectToVisible(ordersTable.getCellRect(i, 0, true));
                updateOrderSummary();
                break;
            }
        }
    }
}




