package kiosk.view;

import java.awt.*;
import java.awt.print.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import kiosk.controller.CustomOrderController;
import kiosk.view.admin.CustomOrdersAdminPanel;
import kiosk.controller.SalesController;
import kiosk.model.InventoryItem;
import kiosk.model.SaleTransaction;
import kiosk.util.HelpRequestManager;
import kiosk.util.InventoryManager;

/**
 * Modern Admin Panel with Dashboard and Sales Reports
 * Features: Statistics dashboard, inventory management, sales analytics
 */
public class AdminPanel extends JFrame {
    // Modern Bakery Theme Colors
    private final Color PRIMARY_COLOR = BakeryTheme.PRIMARY_COLOR;
    private final Color ACCENT_COLOR = BakeryTheme.ACCENT_COLOR;
    private final Color WARNING_COLOR = BakeryTheme.WARNING;
    private final Color DANGER_COLOR = BakeryTheme.ERROR;
    private final Color SUCCESS_COLOR = BakeryTheme.SUCCESS;
    private final Color BACKGROUND_COLOR = BakeryTheme.BACKGROUND_COLOR;
    private final Color CARD_BG = BakeryTheme.CARD_COLOR;
    
    // Custom cell renderer for status column
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value != null) {
                String status = value.toString();
                
                switch (status) {
                    case "Expired":
                        c.setForeground(DANGER_COLOR);
                        break;
                    case "Expiring Soon":
                        c.setForeground(WARNING_COLOR);
                        break;
                    case "Low Stock":
                        c.setForeground(ACCENT_COLOR);
                        break;
                    default:
                        c.setForeground(SUCCESS_COLOR);
                        break;
                }
            }
            
            return c;
        }
    }
    
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font STAT_NUMBER_FONT = new Font("Segoe UI", Font.BOLD, 36);
    private final Font STAT_LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    
    private InventoryManager inventoryManager;
    private SalesController salesController;
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> categoryFilter;
    private JTextField searchField;
    private JLabel alertsCountLabel;
    private JLabel dashboardAlertsCountLabel;
    private JPanel alertsPanel;
    private JPanel dashboardAlertsPanel;
    private JPanel helpRequestsPanel;
    private JPanel dashboardHelpRequestsPanel;
    private JLabel helpRequestCountLabel;
    private JLabel dashboardHelpRequestCountLabel;
    private JTabbedPane mainTabbedPane;
    
    // Dashboard stat labels
    private JLabel todaySalesLabel;
    private JLabel totalRevenueLabel;
    private JLabel lowStockCountLabel;
    private JLabel expiredItemsCountLabel;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Track the current view state
    private enum ViewMode { ALL, EXPIRED, EXPIRING_SOON, LOW_STOCK }
    private ViewMode currentView = ViewMode.ALL;
    
    public AdminPanel() {
        setTitle("SweetBatterBakeshop - Admin Dashboard");
        
        // Make the window full screen on startup
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Keep a minimum size for when user un-maximizes
        setMinimumSize(new Dimension(1200, 800));
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());
        
        inventoryManager = InventoryManager.getInstance();
        salesController = SalesController.getInstance();
        
        // Register as a help request listener
        HelpRequestManager.getInstance().addListener(this::handleNewHelpRequest);
        
        // Create the main components
        JPanel headerPanel = createHeaderPanel();
        mainTabbedPane = createMainTabbedPane();
        
        // Add components to frame
        add(headerPanel, BorderLayout.NORTH);
        add(mainTabbedPane, BorderLayout.CENTER);
        
        // Load initial data
        refreshDashboard();
        refreshTableData();
        updateAlerts();
        
        setVisible(true);
    }
    
    /**
     * Creates the main tabbed pane with Dashboard, Inventory, and Sales Reports
     */
    private JTabbedPane createMainTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tabbedPane.setBackground(BACKGROUND_COLOR);
        
        // Dashboard Tab
        JPanel dashboardPanel = createDashboardPanel();
        tabbedPane.addTab("📊 Dashboard", dashboardPanel);
        
        // Inventory Tab
        JPanel inventoryPanel = createInventoryPanel();
        tabbedPane.addTab("📦 Inventory Management", inventoryPanel);
        
        // Sales Reports Tab
        JPanel salesReportsPanel = createSalesReportsPanel();
        tabbedPane.addTab("💰 Sales Reports", salesReportsPanel);
        
        // Custom Orders Tab
        CustomOrdersAdminPanel customOrdersPanel = new CustomOrdersAdminPanel(new CustomOrderController());
        tabbedPane.addTab("🎂 Custom Orders", customOrdersPanel);
        
        return tabbedPane;
    }
    
    /**
     * Creates the modern dashboard with statistics cards
     */
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Dashboard title
        JLabel dashboardTitle = new JLabel("Dashboard Overview");
        dashboardTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        dashboardTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Statistics cards panel - made more compact
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(BACKGROUND_COLOR);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        
        // Create stat cards
        JPanel todaySalesCard = createStatCard("Today's Sales", "₱0.00", SUCCESS_COLOR, "💵");
        JPanel totalRevenueCard = createStatCard("Total Revenue", "₱0.00", ACCENT_COLOR, "💰");
        JPanel lowStockCard = createStatCard("Low Stock Items", "0", WARNING_COLOR, "📉");
        JPanel expiredItemsCard = createStatCard("Expired Items", "0", DANGER_COLOR, "❌");
        
        // Store labels for updating
        todaySalesLabel = (JLabel) ((JPanel) todaySalesCard.getComponent(1)).getComponent(0);
        totalRevenueLabel = (JLabel) ((JPanel) totalRevenueCard.getComponent(1)).getComponent(0);
        lowStockCountLabel = (JLabel) ((JPanel) lowStockCard.getComponent(1)).getComponent(0);
        expiredItemsCountLabel = (JLabel) ((JPanel) expiredItemsCard.getComponent(1)).getComponent(0);
        
        statsPanel.add(todaySalesCard);
        statsPanel.add(totalRevenueCard);
        statsPanel.add(lowStockCard);
        statsPanel.add(expiredItemsCard);
        
        // Quick actions panel
        JPanel quickActionsPanel = createQuickActionsPanel();
        
        // Alerts and help requests panel - given more space
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        
        JPanel alertsCard = createAlertsCard();
        JPanel helpRequestsCard = createHelpRequestsCard();
        
        bottomPanel.add(alertsCard);
        bottomPanel.add(helpRequestsCard);
        
        // Assemble dashboard with better proportions
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(BACKGROUND_COLOR);
        topSection.add(dashboardTitle, BorderLayout.NORTH);
        topSection.add(statsPanel, BorderLayout.CENTER);
        topSection.add(quickActionsPanel, BorderLayout.SOUTH);
        topSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        panel.add(topSection, BorderLayout.NORTH);
        panel.add(bottomPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates a statistics card
     */
    private JPanel createStatCard(String label, String value, Color accentColor, String icon) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        
        // Icon and label panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(CARD_BG);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setForeground(accentColor);
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelComp.setForeground(new Color(100, 100, 100));
        labelComp.setHorizontalAlignment(SwingConstants.RIGHT);
        
        topPanel.add(iconLabel, BorderLayout.WEST);
        topPanel.add(labelComp, BorderLayout.EAST);
        
        // Value panel
        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
        valuePanel.setBackground(CARD_BG);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(accentColor);
        
        valuePanel.add(valueLabel);
        
        card.add(topPanel, BorderLayout.NORTH);
        card.add(valuePanel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Creates quick actions panel
     */
    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        JLabel quickActionsLabel = new JLabel("Quick Actions:");
        quickActionsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JButton addItemBtn = createActionButton("➕ Add New Item", ACCENT_COLOR);
        addItemBtn.addActionListener(e -> showAddItemDialog());
        
        JButton viewInventoryBtn = createActionButton("📦 View Inventory", PRIMARY_COLOR);
        viewInventoryBtn.addActionListener(e -> mainTabbedPane.setSelectedIndex(1));
        
        JButton viewReportsBtn = createActionButton("💰 Sales Reports", SUCCESS_COLOR);
        viewReportsBtn.addActionListener(e -> mainTabbedPane.setSelectedIndex(2));
        
        JButton refreshBtn = createActionButton("🔄 Refresh Data", new Color(100, 100, 100));
        refreshBtn.addActionListener(e -> {
            refreshDashboard();
            refreshTableData();
            updateAlerts();
        });
        
        panel.add(quickActionsLabel);
        panel.add(addItemBtn);
        panel.add(viewInventoryBtn);
        panel.add(viewReportsBtn);
        panel.add(refreshBtn);
        
        return panel;
    }
    
    /**
     * Creates an action button
     */
    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(bgColor, 1, true),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BakeryTheme.darker(bgColor, 0.15f));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    /**
     * Refreshes dashboard statistics
     */
    private void refreshDashboard() {
        // Today's sales
        double todaysTotal = salesController.getTodayTotalSales();
        todaySalesLabel.setText(String.format("₱%.2f", todaysTotal));
        
        // Total revenue - get all time sales
        List<SaleTransaction> allSales = salesController.getAllTransactions();
        double totalRevenue = allSales.stream()
            .mapToDouble(SaleTransaction::getTotal)
            .sum();
        totalRevenueLabel.setText(String.format("₱%.2f", totalRevenue));
        
        // Low stock count
        int lowStockCount = inventoryManager.getLowStockItems(10).size();
        lowStockCountLabel.setText(String.valueOf(lowStockCount));
        
        // Expired items count
        int expiredCount = inventoryManager.getExpiredItems().size();
        expiredItemsCountLabel.setText(String.valueOf(expiredCount));
    }
    
    /**
     * Creates alerts card for dashboard
     */
    private JPanel createAlertsCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_BG);
        
        JLabel titleLabel = new JLabel("⚠️ Alerts");
        titleLabel.setFont(SUBTITLE_FONT);
        
        dashboardAlertsCountLabel = new JLabel("0");
        dashboardAlertsCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dashboardAlertsCountLabel.setForeground(Color.WHITE);
        dashboardAlertsCountLabel.setBackground(DANGER_COLOR);
        dashboardAlertsCountLabel.setOpaque(true);
        dashboardAlertsCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dashboardAlertsCountLabel.setPreferredSize(new Dimension(30, 30));
        dashboardAlertsCountLabel.setBorder(new LineBorder(DANGER_COLOR, 2, true));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(dashboardAlertsCountLabel, BorderLayout.EAST);
        
        // Alerts list
        dashboardAlertsPanel = new JPanel();
        dashboardAlertsPanel.setLayout(new BoxLayout(dashboardAlertsPanel, BoxLayout.Y_AXIS));
        dashboardAlertsPanel.setBackground(CARD_BG);
        
        JScrollPane scrollPane = new JScrollPane(dashboardAlertsPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Creates help requests card for dashboard
     */
    private JPanel createHelpRequestsCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_BG);
        
        JLabel titleLabel = new JLabel("🆘 Customer Assistance");
        titleLabel.setFont(SUBTITLE_FONT);
        
        dashboardHelpRequestCountLabel = new JLabel("0");
        dashboardHelpRequestCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dashboardHelpRequestCountLabel.setForeground(Color.WHITE);
        dashboardHelpRequestCountLabel.setBackground(WARNING_COLOR);
        dashboardHelpRequestCountLabel.setOpaque(true);
        dashboardHelpRequestCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dashboardHelpRequestCountLabel.setPreferredSize(new Dimension(30, 30));
        dashboardHelpRequestCountLabel.setBorder(new LineBorder(WARNING_COLOR, 2, true));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(dashboardHelpRequestCountLabel, BorderLayout.EAST);
        
        // Help requests list
        dashboardHelpRequestsPanel = new JPanel();
        dashboardHelpRequestsPanel.setLayout(new BoxLayout(dashboardHelpRequestsPanel, BoxLayout.Y_AXIS));
        dashboardHelpRequestsPanel.setBackground(CARD_BG);
        
        JScrollPane scrollPane = new JScrollPane(dashboardHelpRequestsPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        
        // Load initial help requests
        updateHelpRequests();
        
        return card;
    }
    
    /**
     * Creates the inventory management panel
     */
    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Left navigation panel
        JPanel leftPanel = createLeftPanel();
        
        // Main inventory table panel
        JPanel mainPanel = createMainPanel();
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(mainPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Inventory Management System");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(DANGER_COLOR);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> {
            dispose();
            new KioskMainPage();
        });
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(logoutButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(280, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
        
        // Navigation panel
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(Color.WHITE);
        navPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel navLabel = new JLabel("Navigation");
        navLabel.setFont(SUBTITLE_FONT);
        navLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton allItemsBtn = createNavButton("All Inventory Items", "📋");
        JButton expiredItemsBtn = createNavButton("Expired Items", "❌");
        JButton expiringItemsBtn = createNavButton("Expiring Soon", "⚠️");
        JButton lowStockBtn = createNavButton("Low Stock Items", "📉");
        JButton addItemBtn = createNavButton("Add New Item", "➕");
        
        allItemsBtn.addActionListener(e -> {
            currentView = ViewMode.ALL;
            refreshTableData();
        });
        
        expiredItemsBtn.addActionListener(e -> {
            currentView = ViewMode.EXPIRED;
            refreshTableData();
        });
        
        expiringItemsBtn.addActionListener(e -> {
            currentView = ViewMode.EXPIRING_SOON;
            refreshTableData();
        });
        
        lowStockBtn.addActionListener(e -> {
            currentView = ViewMode.LOW_STOCK;
            refreshTableData();
        });
        
        addItemBtn.addActionListener(e -> showAddItemDialog());
        
        navPanel.add(navLabel);
        navPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        navPanel.add(allItemsBtn);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(expiredItemsBtn);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(expiringItemsBtn);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(lowStockBtn);
        navPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        navPanel.add(new JSeparator());
        navPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        navPanel.add(addItemBtn);
        
        // Alerts panel
        JPanel alertsHeaderPanel = new JPanel(new BorderLayout());
        alertsHeaderPanel.setBackground(Color.WHITE);
        alertsHeaderPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        
        JLabel alertsLabel = new JLabel("Alerts");
        alertsLabel.setFont(SUBTITLE_FONT);
        
        alertsCountLabel = new JLabel("0");
        alertsCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        alertsCountLabel.setForeground(Color.WHITE);
        alertsCountLabel.setBackground(DANGER_COLOR);
        alertsCountLabel.setOpaque(true);
        alertsCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        alertsCountLabel.setPreferredSize(new Dimension(30, 30));
        alertsCountLabel.setBorder(new LineBorder(Color.WHITE, 2, true));
        
        alertsHeaderPanel.add(alertsLabel, BorderLayout.WEST);
        alertsHeaderPanel.add(alertsCountLabel, BorderLayout.EAST);
        
        alertsPanel = new JPanel();
        alertsPanel.setLayout(new BoxLayout(alertsPanel, BoxLayout.Y_AXIS));
        alertsPanel.setBackground(Color.WHITE);
        alertsPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));
        
        JScrollPane alertsScrollPane = new JScrollPane(alertsPanel);
        alertsScrollPane.setBorder(null);
        
        // Help requests panel
        JPanel helpRequestsHeaderPanel = new JPanel(new BorderLayout());
        helpRequestsHeaderPanel.setBackground(Color.WHITE);
        helpRequestsHeaderPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        
        JLabel helpRequestsLabel = new JLabel("Customer Assistance");
        helpRequestsLabel.setFont(SUBTITLE_FONT);
        
        helpRequestCountLabel = new JLabel("0");
        helpRequestCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        helpRequestCountLabel.setForeground(Color.WHITE);
        helpRequestCountLabel.setBackground(WARNING_COLOR);
        helpRequestCountLabel.setOpaque(true);
        helpRequestCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        helpRequestCountLabel.setPreferredSize(new Dimension(30, 30));
        helpRequestCountLabel.setBorder(new LineBorder(Color.WHITE, 2, true));
        
        helpRequestsHeaderPanel.add(helpRequestsLabel, BorderLayout.WEST);
        helpRequestsHeaderPanel.add(helpRequestCountLabel, BorderLayout.EAST);
        
        helpRequestsPanel = new JPanel();
        helpRequestsPanel.setLayout(new BoxLayout(helpRequestsPanel, BoxLayout.Y_AXIS));
        helpRequestsPanel.setBackground(Color.WHITE);
        helpRequestsPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));
        
        JScrollPane helpRequestsScrollPane = new JScrollPane(helpRequestsPanel);
        helpRequestsScrollPane.setBorder(null);
        helpRequestsScrollPane.setPreferredSize(new Dimension(280, 200));
        
        // Assemble left panel
        panel.add(navPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(alertsHeaderPanel, BorderLayout.NORTH);
        centerPanel.add(alertsScrollPane, BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(Color.WHITE);
        southPanel.add(helpRequestsHeaderPanel, BorderLayout.NORTH);
        southPanel.add(helpRequestsScrollPane, BorderLayout.CENTER);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(southPanel, BorderLayout.SOUTH);
        
        // Load initial help requests
        updateHelpRequests();
        
        return panel;
    }
    
    private JButton createNavButton(String text, String icon) {
        JButton button = new JButton(icon + " " + text);
        button.setFont(REGULAR_FONT);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(250, 40));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(BACKGROUND_COLOR);
                button.setContentAreaFilled(true);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setContentAreaFilled(false);
            }
        });
        
        return button;
    }
    
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Table header panel with filters
        JPanel tableHeaderPanel = new JPanel(new BorderLayout());
        tableHeaderPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel tableTitle = new JLabel("Inventory Items");
        tableTitle.setFont(SUBTITLE_FONT);
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel filterLabel = new JLabel("Filter by Category:");
        filterLabel.setFont(REGULAR_FONT);
        
        String[] categories = {"All Categories", "Breads & Rolls", "Pastries & Desserts", "Cakes & Special Occasions", "Beverages & Extras"};
        categoryFilter = new JComboBox<>(categories);
        categoryFilter.setFont(REGULAR_FONT);
        categoryFilter.addActionListener(e -> refreshTableData());
        
        searchField = new JTextField(15);
        searchField.setFont(REGULAR_FONT);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.setText("Search items...");
        searchField.setForeground(Color.GRAY);
        
        // Add focus listener for placeholder behavior
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("Search items...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search items...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });
        
        // Add action listener for Enter key
        searchField.addActionListener(e -> {
            String query = searchField.getText().trim().toLowerCase();
            if (!query.isEmpty() && !query.equals("search items...")) {
                filterTableBySearch(query);
            } else {
                refreshTableData();
            }
        });
        
        JButton searchButton = new JButton("Search");
        searchButton.setFont(REGULAR_FONT);
        searchButton.setBackground(ACCENT_COLOR);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim().toLowerCase();
            if (!query.isEmpty() && !query.equals("search items...")) {
                filterTableBySearch(query);
            } else {
                refreshTableData();
            }
        });
        
        filterPanel.add(filterLabel);
        filterPanel.add(categoryFilter);
        filterPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        
        tableHeaderPanel.add(tableTitle, BorderLayout.WEST);
        tableHeaderPanel.add(filterPanel, BorderLayout.EAST);
        
        // Table panel
        String[] columnNames = {"Item Name", "Category", "Price", "Stock", "Expiration Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        inventoryTable = new JTable(tableModel);
        inventoryTable.setFont(REGULAR_FONT);
        inventoryTable.setRowHeight(30);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryTable.getTableHeader().setReorderingAllowed(false);
        inventoryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Add custom cell renderer for status column
        inventoryTable.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());
        
        JScrollPane tableScrollPane = new JScrollPane(inventoryTable);
        
        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setBackground(BACKGROUND_COLOR);
        
        JButton editButton = new JButton("Edit Selected");
        editButton.setFont(REGULAR_FONT);
        editButton.setBackground(ACCENT_COLOR);
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        editButton.addActionListener(e -> {
            int selectedRow = inventoryTable.getSelectedRow();
            if (selectedRow >= 0) {
                String itemName = (String) tableModel.getValueAt(selectedRow, 0);
                InventoryItem item = inventoryManager.getItem(itemName);
                if (item != null) {
                    showEditItemDialog(item);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please select an item to edit.", 
                    "No Selection", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.setFont(REGULAR_FONT);
        deleteButton.setBackground(DANGER_COLOR);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> {
            int selectedRow = inventoryTable.getSelectedRow();
            if (selectedRow >= 0) {
                String itemName = (String) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete '" + itemName + "'?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    inventoryManager.removeItem(itemName);
                    refreshTableData();
                    updateAlerts();
                    refreshDashboard(); // Auto-refresh dashboard stats
                    JOptionPane.showMessageDialog(this, 
                        "Item '" + itemName + "' deleted successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please select an item to delete.", 
                    "No Selection", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.setFont(REGULAR_FONT);
        refreshButton.setBackground(SUCCESS_COLOR);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> {
            refreshTableData();
            updateAlerts();
        });
        
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        actionPanel.add(refreshButton);
        
        // Assemble main panel
        panel.add(tableHeaderPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void refreshTableData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Get selected category filter
        String selectedCategory = (String) categoryFilter.getSelectedItem();
        
        // Get items based on current view
        List<InventoryItem> items;
        switch (currentView) {
            case EXPIRED:
                items = inventoryManager.getExpiredItems();
                break;
            case EXPIRING_SOON:
                items = inventoryManager.getExpiringItems(7); // 7 days warning
                break;
            case LOW_STOCK:
                items = inventoryManager.getLowStockItems(10); // 10 items threshold
                break;
            case ALL:
            default:
                items = inventoryManager.getAllItems();
                break;
        }
        
        // Apply category filter if needed
        if (!"All Categories".equals(selectedCategory)) {
            items = items.stream()
                    .filter(item -> item.getCategory().equals(selectedCategory))
                    .collect(java.util.stream.Collectors.toList());
        }
        
        // Add filtered items to table
        for (InventoryItem item : items) {
            String status = getItemStatus(item);
            
            tableModel.addRow(new Object[]{
                item.getName(),
                item.getCategory(),
                String.format("₱%.2f", item.getPrice()),
                item.getStockQuantity(),
                item.getFormattedExpirationDate(),
                status
            });
        }
    }
    
    private void filterTableBySearch(String query) {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Get selected category filter
        String selectedCategory = (String) categoryFilter.getSelectedItem();
        
        // Get items based on current view
        List<InventoryItem> items;
        switch (currentView) {
            case EXPIRED:
                items = inventoryManager.getExpiredItems();
                break;
            case EXPIRING_SOON:
                items = inventoryManager.getExpiringItems(7);
                break;
            case LOW_STOCK:
                items = inventoryManager.getLowStockItems(10);
                break;
            case ALL:
            default:
                items = inventoryManager.getAllItems();
                break;
        }
        
        // Apply category filter if needed
        if (!"All Categories".equals(selectedCategory)) {
            items = items.stream()
                    .filter(item -> item.getCategory().equals(selectedCategory))
                    .collect(java.util.stream.Collectors.toList());
        }
        
        // Apply search filter
        List<InventoryItem> filteredItems = items.stream()
                .filter(item -> item.getName().toLowerCase().contains(query) ||
                               item.getCategory().toLowerCase().contains(query))
                .collect(java.util.stream.Collectors.toList());
        
        // Add filtered items to table
        for (InventoryItem item : filteredItems) {
            String status = getItemStatus(item);
            
            tableModel.addRow(new Object[]{
                item.getName(),
                item.getCategory(),
                String.format("₱%.2f", item.getPrice()),
                item.getStockQuantity(),
                item.getFormattedExpirationDate(),
                status
            });
        }
        
        // Show message if no results
        if (filteredItems.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No items found matching '" + query + "'",
                "No Results",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private String getItemStatus(InventoryItem item) {
        if (item.isExpired()) {
            return "Expired";
        } else if (item.isExpiringSoon(7)) {
            return "Expiring Soon";
        } else if (item.isLowStock(10)) {
            return "Low Stock";
        } else {
            return "In Stock";
        }
    }
    
    private void updateAlerts() {
        List<InventoryItem> expiredItems = inventoryManager.getExpiredItems();
        List<InventoryItem> expiringItems = inventoryManager.getExpiringItems(7);
        List<InventoryItem> lowStockItems = inventoryManager.getLowStockItems(10);
        
        int totalAlerts = expiredItems.size() + expiringItems.size() + lowStockItems.size();
        
        // Update both alert panels (dashboard and inventory sidebar)
        if (alertsPanel != null && alertsCountLabel != null) {
            updateAlertPanel(alertsPanel, alertsCountLabel, expiredItems, expiringItems, lowStockItems, totalAlerts);
        }
        if (dashboardAlertsPanel != null && dashboardAlertsCountLabel != null) {
            updateAlertPanel(dashboardAlertsPanel, dashboardAlertsCountLabel, expiredItems, expiringItems, lowStockItems, totalAlerts);
        }
    }
    
    /**
     * Updates a specific alert panel with current alerts
     */
    private void updateAlertPanel(JPanel panel, JLabel countLabel, 
                                   List<InventoryItem> expiredItems, 
                                   List<InventoryItem> expiringItems, 
                                   List<InventoryItem> lowStockItems, 
                                   int totalAlerts) {
        panel.removeAll();
        
        countLabel.setText(String.valueOf(totalAlerts));
        
        if (totalAlerts == 0) {
            JLabel noAlertsLabel = new JLabel("No alerts to display");
            noAlertsLabel.setFont(REGULAR_FONT);
            noAlertsLabel.setForeground(Color.GRAY);
            noAlertsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(noAlertsLabel);
        } else {
            // Add expired items alerts
            for (InventoryItem item : expiredItems) {
                JLabel alertLabel = new JLabel("<html>❌ <b>" + item.getName() + "</b> is expired</html>");
                alertLabel.setFont(REGULAR_FONT);
                alertLabel.setForeground(DANGER_COLOR);
                alertLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(alertLabel);
                panel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
            
            // Add expiring soon alerts
            for (InventoryItem item : expiringItems) {
                JLabel alertLabel = new JLabel("<html>⚠️ <b>" + item.getName() + 
                                             "</b> expires on " + item.getFormattedExpirationDate() + "</html>");
                alertLabel.setFont(REGULAR_FONT);
                alertLabel.setForeground(WARNING_COLOR);
                alertLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(alertLabel);
                panel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
            
            // Add low stock alerts
            for (InventoryItem item : lowStockItems) {
                if (!item.isExpired() && !item.isExpiringSoon(7)) { // Avoid duplicate alerts
                    JLabel alertLabel = new JLabel("<html>📉 <b>" + item.getName() + 
                                                 "</b> is low in stock (" + item.getStockQuantity() + ")</html>");
                    alertLabel.setFont(REGULAR_FONT);
                    alertLabel.setForeground(ACCENT_COLOR);
                    alertLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    panel.add(alertLabel);
                    panel.add(Box.createRigidArea(new Dimension(0, 5)));
                }
            }
        }
        
        panel.revalidate();
        panel.repaint();
    }
    
    private void updateHelpRequests() {
        List<HelpRequestManager.HelpRequest> requests = HelpRequestManager.getInstance().getActiveRequests();
        
        // Update both help request panels (dashboard and inventory sidebar)
        if (helpRequestsPanel != null && helpRequestCountLabel != null) {
            updateHelpRequestPanel(helpRequestsPanel, helpRequestCountLabel, requests);
        }
        if (dashboardHelpRequestsPanel != null && dashboardHelpRequestCountLabel != null) {
            updateHelpRequestPanel(dashboardHelpRequestsPanel, dashboardHelpRequestCountLabel, requests);
        }
    }
    
    /**
     * Updates a specific help request panel with current requests
     */
    private void updateHelpRequestPanel(JPanel panel, JLabel countLabel, 
                                        List<HelpRequestManager.HelpRequest> requests) {
        panel.removeAll();
        
        countLabel.setText(String.valueOf(requests.size()));
        countLabel.setVisible(requests.size() > 0);
        
        if (requests.isEmpty()) {
            JLabel noRequestsLabel = new JLabel("No assistance requests");
            noRequestsLabel.setFont(REGULAR_FONT);
            noRequestsLabel.setForeground(Color.GRAY);
            noRequestsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(noRequestsLabel);
        } else {
            for (HelpRequestManager.HelpRequest request : requests) {
                JPanel requestPanel = createHelpRequestPanel(request);
                panel.add(requestPanel);
                panel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        
        panel.revalidate();
        panel.repaint();
    }
    
    private JPanel createHelpRequestPanel(HelpRequestManager.HelpRequest request) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(255, 250, 240)); // Light yellow background
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(WARNING_COLOR),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String urgentPrefix = request.getDetails().startsWith("[URGENT]") ? "⚠️ URGENT: " : "";
        
        JLabel locationLabel = new JLabel(urgentPrefix + "Location: " + request.getLocation());
        locationLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        locationLabel.setForeground(Color.BLACK);
        locationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel typeLabel = new JLabel("Issue: " + request.getIssueType());
        typeLabel.setFont(REGULAR_FONT);
        typeLabel.setForeground(Color.BLACK);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel timeLabel = new JLabel("Time: " + request.getFormattedTimestamp());
        timeLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        timeLabel.setForeground(Color.GRAY);
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String details = request.getDetails();
        if (details != null && !details.isEmpty()) {
            if (details.startsWith("[URGENT]")) {
                details = details.substring(8);
            }
            
            JTextArea detailsArea = new JTextArea(details);
            detailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            detailsArea.setBackground(new Color(255, 250, 240));
            detailsArea.setWrapStyleWord(true);
            detailsArea.setLineWrap(true);
            detailsArea.setEditable(false);
            detailsArea.setOpaque(false);
            detailsArea.setAlignmentX(Component.LEFT_ALIGNMENT);
            detailsArea.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            
            panel.add(locationLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 3)));
            panel.add(typeLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 3)));
            panel.add(timeLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
            panel.add(new JSeparator());
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
            panel.add(detailsArea);
        } else {
            panel.add(locationLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 3)));
        }
        
        // Add action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JButton resolveButton = new JButton("Mark Resolved");
        resolveButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        resolveButton.setForeground(Color.WHITE);
        resolveButton.setBackground(SUCCESS_COLOR);
        resolveButton.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        resolveButton.setFocusPainted(false);
        resolveButton.addActionListener(e -> {
            HelpRequestManager.getInstance().resolveRequest(request.getId());
            updateHelpRequests();
        });
        
        buttonPanel.add(resolveButton);
        
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(buttonPanel);
        
        return panel;
    }
    
    /**
     * Creates the Sales Reports panel with daily, weekly, and monthly tabs
     */
    private JPanel createSalesReportsPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel headerLabel = new JLabel("📈 Sales Reports & Analytics");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Create nested tabbed pane for different report periods
        JTabbedPane reportTabs = new JTabbedPane(JTabbedPane.LEFT);
        reportTabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        reportTabs.setBackground(CARD_BG);
        
        // Daily Report Tab
        JPanel dailyReportPanel = createDailyReportPanel();
        reportTabs.addTab("📅 Daily", dailyReportPanel);
        
        // Weekly Report Tab
        JPanel weeklyReportPanel = createWeeklyReportPanel();
        reportTabs.addTab("📊 Weekly", weeklyReportPanel);
        
        // Monthly Report Tab
        JPanel monthlyReportPanel = createMonthlyReportPanel();
        reportTabs.addTab("📆 Monthly", monthlyReportPanel);
        
        // All Time Summary Tab
        JPanel allTimeReportPanel = createAllTimeReportPanel();
        reportTabs.addTab("🌟 All Time", allTimeReportPanel);
        
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        mainPanel.add(reportTabs, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    /**
     * Creates daily sales report panel
     */
    private JPanel createDailyReportPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(CARD_BG);
        
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Date selector
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        datePanel.setBackground(CARD_BG);
        
        JLabel dateLabel = new JLabel("Select Date:");
        dateLabel.setFont(SUBTITLE_FONT);
        
        // Date spinner for selecting date
        SpinnerDateModel spinnerModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(spinnerModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setFont(REGULAR_FONT);
        
        JButton generateBtn = createActionButton("Generate Report", ACCENT_COLOR);
        
        datePanel.add(dateLabel);
        datePanel.add(dateSpinner);
        datePanel.add(generateBtn);
        
        // Report content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(CARD_BG);
        
        // Summary stats panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(CARD_BG);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JPanel totalSalesCard = createMiniStatCard("Total Sales", "₱0.00", SUCCESS_COLOR);
        JPanel transactionsCard = createMiniStatCard("Transactions", "0", PRIMARY_COLOR);
        JPanel itemsSoldCard = createMiniStatCard("Items Sold", "0", ACCENT_COLOR);
        JPanel avgTransactionCard = createMiniStatCard("Avg Transaction", "₱0.00", new Color(150, 150, 150));
        
        statsPanel.add(totalSalesCard);
        statsPanel.add(transactionsCard);
        statsPanel.add(itemsSoldCard);
        statsPanel.add(avgTransactionCard);
        
        // Transactions table
        String[] columns = {"Time", "Transaction ID", "Items", "Subtotal", "Discount", "Total"};
        DefaultTableModel dailyTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable transactionsTable = new JTable(dailyTableModel);
        transactionsTable.setFont(REGULAR_FONT);
        transactionsTable.setRowHeight(28);
        transactionsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JScrollPane tableScrollPane = new JScrollPane(transactionsTable);
        tableScrollPane.setBorder(new LineBorder(new Color(220, 220, 220), 1));
        
        contentPanel.add(statsPanel, BorderLayout.NORTH);
        contentPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Generate button action - Print the report
        generateBtn.addActionListener(e -> {
            java.util.Date selectedDate = (java.util.Date) dateSpinner.getValue();
            LocalDate localDate = selectedDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            updateDailyReport(localDate, dailyTableModel, totalSalesCard, transactionsCard, itemsSoldCard, avgTransactionCard);
            
            // Prepare statistics for printing
            Map<String, String> stats = new java.util.LinkedHashMap<>();
            stats.put("Total Sales", getStatCardValue(totalSalesCard));
            stats.put("Transactions", getStatCardValue(transactionsCard));
            stats.put("Items Sold", getStatCardValue(itemsSoldCard));
            stats.put("Avg Transaction", getStatCardValue(avgTransactionCard));
            
            // Print the report
            printSalesReport("Daily Sales Report", 
                           "Date: " + localDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")),
                           dailyTableModel, stats);
        });
        
        // Load today's report by default
        updateDailyReport(LocalDate.now(), dailyTableModel, totalSalesCard, transactionsCard, itemsSoldCard, avgTransactionCard);
        
        panel.add(datePanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // Wrap in scroll pane to prevent cutoff
        JScrollPane outerScrollPane = new JScrollPane(panel);
        outerScrollPane.setBorder(null);
        outerScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        outerScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        outerScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        outerPanel.add(outerScrollPane, BorderLayout.CENTER);
        return outerPanel;
    }
    
    /**
     * Updates daily report with data
     */
    private void updateDailyReport(LocalDate date, DefaultTableModel tableModel, 
                                   JPanel totalSalesCard, JPanel transactionsCard, 
                                   JPanel itemsSoldCard, JPanel avgTransactionCard) {
        tableModel.setRowCount(0);
        
        // Get transactions for the selected day
        java.time.LocalDateTime startOfDay = date.atStartOfDay();
        java.time.LocalDateTime endOfDay = date.atTime(23, 59, 59);
        List<SaleTransaction> sales = salesController.getTransactionsByDateRange(startOfDay, endOfDay);
        
        double totalSales = 0;
        int totalItems = 0;
        
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        
        for (SaleTransaction sale : sales) {
            totalSales += sale.getTotal();
            totalItems += sale.getTotalItemCount();
            
            String time = sale.getTransactionDate().format(timeFormatter);
            tableModel.addRow(new Object[]{
                time,
                sale.getTransactionId(),
                sale.getTotalItemCount(),
                String.format("₱%.2f", sale.getSubtotal()),
                String.format("₱%.2f", sale.getDiscountAmount()),
                String.format("₱%.2f", sale.getTotal())
            });
        }
        
        double avgTransaction = sales.isEmpty() ? 0 : totalSales / sales.size();
        
        // Update stat cards
        updateMiniStatCard(totalSalesCard, String.format("₱%.2f", totalSales));
        updateMiniStatCard(transactionsCard, String.valueOf(sales.size()));
        updateMiniStatCard(itemsSoldCard, String.valueOf(totalItems));
        updateMiniStatCard(avgTransactionCard, String.format("₱%.2f", avgTransaction));
    }
    
    /**
     * Creates weekly sales report panel
     */
    private JPanel createWeeklyReportPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(CARD_BG);
        
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Week selector
        JPanel weekPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        weekPanel.setBackground(CARD_BG);
        
        JLabel weekLabel = new JLabel("Select Week:");
        weekLabel.setFont(SUBTITLE_FONT);
        
        JButton currentWeekBtn = createActionButton("Current Week", PRIMARY_COLOR);
        JButton lastWeekBtn = createActionButton("Last Week", ACCENT_COLOR);
        JButton generateBtn = createActionButton("Generate Report", SUCCESS_COLOR);
        
        weekPanel.add(weekLabel);
        weekPanel.add(currentWeekBtn);
        weekPanel.add(lastWeekBtn);
        weekPanel.add(generateBtn);
        
        // Report content
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(CARD_BG);
        
        // Weekly summary stats
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(CARD_BG);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JPanel weekTotalCard = createMiniStatCard("Week Total", "₱0.00", SUCCESS_COLOR);
        JPanel dailyAvgCard = createMiniStatCard("Daily Average", "₱0.00", PRIMARY_COLOR);
        JPanel weekTransactionsCard = createMiniStatCard("Transactions", "0", ACCENT_COLOR);
        JPanel topItemCard = createMiniStatCard("Top Item", "N/A", WARNING_COLOR);
        
        statsPanel.add(weekTotalCard);
        statsPanel.add(dailyAvgCard);
        statsPanel.add(weekTransactionsCard);
        statsPanel.add(topItemCard);
        
        // Daily breakdown table
        String[] columns = {"Day", "Date", "Sales", "Transactions", "Items Sold", "Avg Transaction"};
        DefaultTableModel weeklyTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable dailyBreakdownTable = new JTable(weeklyTableModel);
        dailyBreakdownTable.setFont(REGULAR_FONT);
        dailyBreakdownTable.setRowHeight(30);
        dailyBreakdownTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JScrollPane tableScrollPane = new JScrollPane(dailyBreakdownTable);
        tableScrollPane.setBorder(new LineBorder(new Color(220, 220, 220), 1));
        
        contentPanel.add(statsPanel, BorderLayout.NORTH);
        contentPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Button actions
        final LocalDate[] selectedWeekStart = {LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1)};
        
        currentWeekBtn.addActionListener(e -> {
            selectedWeekStart[0] = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
            updateWeeklyReport(selectedWeekStart[0], weeklyTableModel, weekTotalCard, dailyAvgCard, weekTransactionsCard, topItemCard);
        });
        
        lastWeekBtn.addActionListener(e -> {
            selectedWeekStart[0] = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() + 6);
            updateWeeklyReport(selectedWeekStart[0], weeklyTableModel, weekTotalCard, dailyAvgCard, weekTransactionsCard, topItemCard);
        });
        
        // Generate button action - Print the report
        generateBtn.addActionListener(e -> {
            updateWeeklyReport(selectedWeekStart[0], weeklyTableModel, weekTotalCard, dailyAvgCard, weekTransactionsCard, topItemCard);
            
            // Prepare statistics for printing
            Map<String, String> stats = new java.util.LinkedHashMap<>();
            stats.put("Week Total", getStatCardValue(weekTotalCard));
            stats.put("Daily Average", getStatCardValue(dailyAvgCard));
            stats.put("Transactions", getStatCardValue(weekTransactionsCard));
            stats.put("Top Item", getStatCardValue(topItemCard));
            
            LocalDate weekEnd = selectedWeekStart[0].plusDays(6);
            // Print the report
            printSalesReport("Weekly Sales Report", 
                           "Week: " + selectedWeekStart[0].format(DateTimeFormatter.ofPattern("MMM dd")) + 
                           " - " + weekEnd.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                           weeklyTableModel, stats);
        });
        
        // Load current week by default
        updateWeeklyReport(selectedWeekStart[0], weeklyTableModel, weekTotalCard, dailyAvgCard, weekTransactionsCard, topItemCard);
        
        panel.add(weekPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // Wrap in scroll pane to prevent cutoff
        JScrollPane outerScrollPane = new JScrollPane(panel);
        outerScrollPane.setBorder(null);
        outerScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        outerScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        outerScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        outerPanel.add(outerScrollPane, BorderLayout.CENTER);
        return outerPanel;
    }
    
    /**
     * Updates weekly report with data
     */
    private void updateWeeklyReport(LocalDate weekStart, DefaultTableModel tableModel,
                                    JPanel weekTotalCard, JPanel dailyAvgCard,
                                    JPanel weekTransactionsCard, JPanel topItemCard) {
        tableModel.setRowCount(0);
        
        double weekTotal = 0;
        int weekTransactions = 0;
        Map<String, Integer> itemCounts = new java.util.HashMap<>();
        
        String[] dayNames = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        
        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            java.time.LocalDateTime startOfDay = date.atStartOfDay();
            java.time.LocalDateTime endOfDay = date.atTime(23, 59, 59);
            List<SaleTransaction> daySales = salesController.getTransactionsByDateRange(startOfDay, endOfDay);
            
            double dayTotal = daySales.stream().mapToDouble(SaleTransaction::getTotal).sum();
            int dayTransactions = daySales.size();
            int dayItems = daySales.stream().mapToInt(SaleTransaction::getTotalItemCount).sum();
            double dayAvg = dayTransactions > 0 ? dayTotal / dayTransactions : 0;
            
            weekTotal += dayTotal;
            weekTransactions += dayTransactions;
            
            // Count items
            for (SaleTransaction sale : daySales) {
                for (kiosk.model.CartItem item : sale.getItems()) {
                    itemCounts.merge(item.getItemName(), item.getQuantity(), Integer::sum);
                }
            }
            
            tableModel.addRow(new Object[]{
                dayNames[i],
                date.toString(),
                String.format("₱%.2f", dayTotal),
                dayTransactions,
                dayItems,
                String.format("₱%.2f", dayAvg)
            });
        }
        
        double dailyAverage = weekTotal / 7;
        
        // Find top item
        String topItem = itemCounts.isEmpty() ? "N/A" : 
            itemCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
        
        // Update stat cards
        updateMiniStatCard(weekTotalCard, String.format("₱%.2f", weekTotal));
        updateMiniStatCard(dailyAvgCard, String.format("₱%.2f", dailyAverage));
        updateMiniStatCard(weekTransactionsCard, String.valueOf(weekTransactions));
        updateMiniStatCard(topItemCard, topItem);
    }
    
    /**
     * Creates monthly sales report panel
     */
    private JPanel createMonthlyReportPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(CARD_BG);
        
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Month selector
        JPanel monthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        monthPanel.setBackground(CARD_BG);
        
        JLabel monthLabel = new JLabel("Select Month:");
        monthLabel.setFont(SUBTITLE_FONT);
        
        String[] months = {"January", "February", "March", "April", "May", "June",
                          "July", "August", "September", "October", "November", "December"};
        JComboBox<String> monthCombo = new JComboBox<>(months);
        monthCombo.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        monthCombo.setFont(REGULAR_FONT);
        
        Integer[] years = {2023, 2024, 2025, 2026};
        JComboBox<Integer> yearCombo = new JComboBox<>(years);
        yearCombo.setSelectedItem(LocalDate.now().getYear());
        yearCombo.setFont(REGULAR_FONT);
        
        JButton generateBtn = createActionButton("Generate Report", SUCCESS_COLOR);
        
        monthPanel.add(monthLabel);
        monthPanel.add(monthCombo);
        monthPanel.add(yearCombo);
        monthPanel.add(generateBtn);
        
        // Report content
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(CARD_BG);
        
        // Monthly summary stats
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(CARD_BG);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JPanel monthTotalCard = createMiniStatCard("Month Total", "₱0.00", SUCCESS_COLOR);
        JPanel monthAvgCard = createMiniStatCard("Daily Average", "₱0.00", PRIMARY_COLOR);
        JPanel monthTransactionsCard = createMiniStatCard("Transactions", "0", ACCENT_COLOR);
        JPanel topDayCard = createMiniStatCard("Best Day", "N/A", WARNING_COLOR);
        
        statsPanel.add(monthTotalCard);
        statsPanel.add(monthAvgCard);
        statsPanel.add(monthTransactionsCard);
        statsPanel.add(topDayCard);
        
        // Weekly breakdown table
        String[] columns = {"Week", "Period", "Sales", "Transactions", "Daily Avg"};
        DefaultTableModel monthlyTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable weeklyBreakdownTable = new JTable(monthlyTableModel);
        weeklyBreakdownTable.setFont(REGULAR_FONT);
        weeklyBreakdownTable.setRowHeight(30);
        weeklyBreakdownTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JScrollPane tableScrollPane = new JScrollPane(weeklyBreakdownTable);
        tableScrollPane.setBorder(new LineBorder(new Color(220, 220, 220), 1));
        
        contentPanel.add(statsPanel, BorderLayout.NORTH);
        contentPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Generate button action - Print the report
        generateBtn.addActionListener(e -> {
            int month = monthCombo.getSelectedIndex() + 1;
            int year = (Integer) yearCombo.getSelectedItem();
            updateMonthlyReport(year, month, monthlyTableModel, monthTotalCard, monthAvgCard, monthTransactionsCard, topDayCard);
            
            // Prepare statistics for printing
            Map<String, String> stats = new java.util.LinkedHashMap<>();
            stats.put("Month Total", getStatCardValue(monthTotalCard));
            stats.put("Daily Average", getStatCardValue(monthAvgCard));
            stats.put("Transactions", getStatCardValue(monthTransactionsCard));
            stats.put("Best Day", getStatCardValue(topDayCard));
            
            String[] monthNames = {"January", "February", "March", "April", "May", "June",
                                  "July", "August", "September", "October", "November", "December"};
            // Print the report
            printSalesReport("Monthly Sales Report", 
                           "Month: " + monthNames[month - 1] + " " + year,
                           monthlyTableModel, stats);
        });
        
        // Load current month by default
        updateMonthlyReport(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), 
            monthlyTableModel, monthTotalCard, monthAvgCard, monthTransactionsCard, topDayCard);
        
        panel.add(monthPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // Wrap in scroll pane to prevent cutoff
        JScrollPane outerScrollPane = new JScrollPane(panel);
        outerScrollPane.setBorder(null);
        outerScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        outerScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        outerScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        outerPanel.add(outerScrollPane, BorderLayout.CENTER);
        return outerPanel;
    }
    
    /**
     * Updates monthly report with data
     */
    private void updateMonthlyReport(int year, int month, DefaultTableModel tableModel,
                                     JPanel monthTotalCard, JPanel monthAvgCard,
                                     JPanel monthTransactionsCard, JPanel topDayCard) {
        tableModel.setRowCount(0);
        
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.plusMonths(1).minusDays(1);
        
        double monthTotal = 0;
        int monthTransactions = 0;
        Map<LocalDate, Double> dailyTotals = new java.util.HashMap<>();
        
        // Calculate weekly breakdown
        LocalDate weekStart = firstDay;
        int weekNum = 1;
        
        while (weekStart.isBefore(lastDay) || weekStart.isEqual(lastDay)) {
            LocalDate weekEnd = weekStart.plusDays(6);
            if (weekEnd.isAfter(lastDay)) {
                weekEnd = lastDay;
            }
            
            double weekTotal = 0;
            int weekTransactions = 0;
            int daysInWeek = (int) (weekEnd.toEpochDay() - weekStart.toEpochDay() + 1);
            
            for (LocalDate date = weekStart; !date.isAfter(weekEnd); date = date.plusDays(1)) {
                java.time.LocalDateTime startOfDay = date.atStartOfDay();
                java.time.LocalDateTime endOfDay = date.atTime(23, 59, 59);
                List<SaleTransaction> daySales = salesController.getTransactionsByDateRange(startOfDay, endOfDay);
                double dayTotal = daySales.stream().mapToDouble(SaleTransaction::getTotal).sum();
                weekTotal += dayTotal;
                weekTransactions += daySales.size();
                monthTotal += dayTotal;
                monthTransactions += daySales.size();
                dailyTotals.put(date, dayTotal);
            }
            
            double dailyAvg = weekTotal / daysInWeek;
            
            tableModel.addRow(new Object[]{
                "Week " + weekNum,
                weekStart + " to " + weekEnd,
                String.format("₱%.2f", weekTotal),
                weekTransactions,
                String.format("₱%.2f", dailyAvg)
            });
            
            weekStart = weekEnd.plusDays(1);
            weekNum++;
        }
        
        int daysInMonth = lastDay.getDayOfMonth();
        double dailyAverage = monthTotal / daysInMonth;
        
        // Find best day
        String bestDay = dailyTotals.isEmpty() ? "N/A" :
            dailyTotals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> e.getKey().toString())
                .orElse("N/A");
        
        // Update stat cards
        updateMiniStatCard(monthTotalCard, String.format("₱%.2f", monthTotal));
        updateMiniStatCard(monthAvgCard, String.format("₱%.2f", dailyAverage));
        updateMiniStatCard(monthTransactionsCard, String.valueOf(monthTransactions));
        updateMiniStatCard(topDayCard, bestDay);
    }
    
    /**
     * Creates all-time sales report panel
     */
    private JPanel createAllTimeReportPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(CARD_BG);
        
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        headerPanel.setBackground(CARD_BG);
        
        JLabel titleLabel = new JLabel("All-Time Summary");
        titleLabel.setFont(SUBTITLE_FONT);
        
        JButton refreshBtn = createActionButton("🔄 Refresh", ACCENT_COLOR);
        
        headerPanel.add(titleLabel);
        headerPanel.add(refreshBtn);
        
        // Summary stats
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        statsPanel.setBackground(CARD_BG);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        JPanel totalRevenueCard = createMiniStatCard("Total Revenue", "₱0.00", SUCCESS_COLOR);
        JPanel totalTransactionsCard = createMiniStatCard("Total Transactions", "0", PRIMARY_COLOR);
        JPanel totalItemsCard = createMiniStatCard("Items Sold", "0", ACCENT_COLOR);
        JPanel avgTransactionCard = createMiniStatCard("Avg Transaction", "₱0.00", WARNING_COLOR);
        JPanel bestDayCard = createMiniStatCard("Best Day", "N/A", new Color(100, 100, 255));
        JPanel topProductCard = createMiniStatCard("Top Product", "N/A", new Color(255, 100, 100));
        
        statsPanel.add(totalRevenueCard);
        statsPanel.add(totalTransactionsCard);
        statsPanel.add(totalItemsCard);
        statsPanel.add(avgTransactionCard);
        statsPanel.add(bestDayCard);
        statsPanel.add(topProductCard);
        
        // Top products table
        JLabel topProductsLabel = new JLabel("🏆 Top Selling Products");
        topProductsLabel.setFont(SUBTITLE_FONT);
        topProductsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        String[] columns = {"Rank", "Product Name", "Units Sold", "Revenue", "Avg Price"};
        DefaultTableModel topProductsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable topProductsTable = new JTable(topProductsModel);
        topProductsTable.setFont(REGULAR_FONT);
        topProductsTable.setRowHeight(30);
        topProductsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JScrollPane tableScrollPane = new JScrollPane(topProductsTable);
        tableScrollPane.setBorder(new LineBorder(new Color(220, 220, 220), 1));
        tableScrollPane.setPreferredSize(new Dimension(0, 300));
        
        // Assemble
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(CARD_BG);
        contentPanel.add(statsPanel, BorderLayout.NORTH);
        contentPanel.add(topProductsLabel, BorderLayout.CENTER);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(tableScrollPane, BorderLayout.SOUTH);
        
        // Refresh action
        refreshBtn.addActionListener(e -> {
            updateAllTimeReport(totalRevenueCard, totalTransactionsCard, totalItemsCard,
                avgTransactionCard, bestDayCard, topProductCard, topProductsModel);
        });
        
        // Load data
        updateAllTimeReport(totalRevenueCard, totalTransactionsCard, totalItemsCard,
            avgTransactionCard, bestDayCard, topProductCard, topProductsModel);
        
        // Wrap in scroll pane to prevent cutoff
        JScrollPane outerScrollPane = new JScrollPane(panel);
        outerScrollPane.setBorder(null);
        outerScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        outerScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        outerScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        outerPanel.add(outerScrollPane, BorderLayout.CENTER);
        return outerPanel;
    }
    
    /**
     * Updates all-time report with data
     */
    private void updateAllTimeReport(JPanel totalRevenueCard, JPanel totalTransactionsCard,
                                     JPanel totalItemsCard, JPanel avgTransactionCard,
                                     JPanel bestDayCard, JPanel topProductCard,
                                     DefaultTableModel topProductsModel) {
        List<SaleTransaction> allSales = salesController.getAllTransactions();
        
        double totalRevenue = allSales.stream().mapToDouble(SaleTransaction::getTotal).sum();
        int totalTransactions = allSales.size();
        int totalItems = allSales.stream().mapToInt(SaleTransaction::getTotalItemCount).sum();
        double avgTransaction = totalTransactions > 0 ? totalRevenue / totalTransactions : 0;
        
        // Find best day
        Map<LocalDate, Double> dailyTotals = new java.util.HashMap<>();
        Map<String, Integer> productCounts = new java.util.HashMap<>();
        Map<String, Double> productRevenue = new java.util.HashMap<>();
        
        for (SaleTransaction sale : allSales) {
            LocalDate date = sale.getTransactionDate().toLocalDate();
            dailyTotals.merge(date, sale.getTotal(), Double::sum);
            
            for (kiosk.model.CartItem item : sale.getItems()) {
                productCounts.merge(item.getItemName(), item.getQuantity(), Integer::sum);
                productRevenue.merge(item.getItemName(), item.getSubtotal(), Double::sum);
            }
        }
        
        String bestDay = dailyTotals.isEmpty() ? "N/A" :
            dailyTotals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> e.getKey().toString())
                .orElse("N/A");
        
        String topProduct = productCounts.isEmpty() ? "N/A" :
            productCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
        
        // Update stat cards
        updateMiniStatCard(totalRevenueCard, String.format("₱%.2f", totalRevenue));
        updateMiniStatCard(totalTransactionsCard, String.valueOf(totalTransactions));
        updateMiniStatCard(totalItemsCard, String.valueOf(totalItems));
        updateMiniStatCard(avgTransactionCard, String.format("₱%.2f", avgTransaction));
        updateMiniStatCard(bestDayCard, bestDay);
        updateMiniStatCard(topProductCard, topProduct);
        
        // Update top products table
        topProductsModel.setRowCount(0);
        productCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(10)
            .forEach(entry -> {
                String productName = entry.getKey();
                int unitsSold = entry.getValue();
                double revenue = productRevenue.getOrDefault(productName, 0.0);
                double avgPrice = revenue / unitsSold;
                
                topProductsModel.addRow(new Object[]{
                    topProductsModel.getRowCount() + 1,
                    productName,
                    unitsSold,
                    String.format("₱%.2f", revenue),
                    String.format("₱%.2f", avgPrice)
                });
            });
    }
    
    /**
     * Creates a mini stat card for reports
     */
    private JPanel createMiniStatCard(String label, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(color, 2, true),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelComp.setForeground(new Color(100, 100, 100));
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueComp.setForeground(color);
        
        card.add(labelComp, BorderLayout.NORTH);
        card.add(valueComp, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Updates a mini stat card value
     */
    private void updateMiniStatCard(JPanel card, String newValue) {
        JLabel valueLabel = (JLabel) card.getComponent(1);
        valueLabel.setText(newValue);
    }
    
    private void showAddItemDialog() {
        JDialog dialog = new JDialog(this, "Add New Inventory Item", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel nameLabel = new JLabel("Item Name:");
        JTextField nameField = new JTextField();
        
        JLabel categoryLabel = new JLabel("Category:");
        String[] categories = {"Breads & Rolls", "Pastries & Desserts", "Cakes & Special Occasions", "Beverages & Extras"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);
        
        JLabel priceLabel = new JLabel("Price (₱):");
        JTextField priceField = new JTextField();
        
        JLabel stockLabel = new JLabel("Stock Quantity:");
        JTextField stockField = new JTextField();
        
        JLabel expirationLabel = new JLabel("Expiration Date (YYYY-MM-DD):");
        JTextField expirationField = new JTextField();
        
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(categoryLabel);
        formPanel.add(categoryCombo);
        formPanel.add(priceLabel);
        formPanel.add(priceField);
        formPanel.add(stockLabel);
        formPanel.add(stockField);
        formPanel.add(expirationLabel);
        formPanel.add(expirationField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton addButton = new JButton("Add Item");
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        addButton.addActionListener(e -> {
            // Validate input
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter an item name", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double price;
            try {
                price = Double.parseDouble(priceField.getText().trim());
                if (price <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid price", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int stock;
            try {
                stock = Integer.parseInt(stockField.getText().trim());
                if (stock < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid stock quantity", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            LocalDate expirationDate;
            try {
                expirationDate = LocalDate.parse(expirationField.getText().trim(), dateFormatter);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid date in format YYYY-MM-DD", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create new item
            String name = nameField.getText().trim();
            String category = (String) categoryCombo.getSelectedItem();
            
            // Create new item with proper constructor
            InventoryItem newItem = new InventoryItem(
                name,
                category,
                price,
                stock,
                expirationDate,
                "AUTO-" + System.currentTimeMillis(), // Generate barcode
                "Bakery Supply"  // Default supplier
            );
            
            inventoryManager.addItem(newItem);
            
            refreshTableData();
            updateAlerts();
            refreshDashboard(); // Auto-refresh dashboard stats
            dialog.dispose();
            
            JOptionPane.showMessageDialog(dialog, "Item added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(addButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void showEditItemDialog(InventoryItem item) {
        JDialog dialog = new JDialog(this, "Edit Inventory Item", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel nameLabel = new JLabel("Item Name:");
        JTextField nameField = new JTextField(item.getName());
        nameField.setEditable(false); // Don't allow name editing
        
        JLabel categoryLabel = new JLabel("Category:");
        String[] categories = {"Breads & Rolls", "Pastries & Desserts", "Cakes & Special Occasions", "Beverages & Extras"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);
        categoryCombo.setSelectedItem(item.getCategory());
        
        JLabel priceLabel = new JLabel("Price (₱):");
        JTextField priceField = new JTextField(String.valueOf(item.getPrice()));
        
        JLabel stockLabel = new JLabel("Stock Quantity:");
        JTextField stockField = new JTextField(String.valueOf(item.getStockQuantity()));
        
        JLabel expirationLabel = new JLabel("Expiration Date (YYYY-MM-DD):");
        JTextField expirationField = new JTextField(item.getFormattedExpirationDate());
        
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(categoryLabel);
        formPanel.add(categoryCombo);
        formPanel.add(priceLabel);
        formPanel.add(priceField);
        formPanel.add(stockLabel);
        formPanel.add(stockField);
        formPanel.add(expirationLabel);
        formPanel.add(expirationField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save Changes");
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        saveButton.addActionListener(e -> {
            // Validate input
            double price;
            try {
                price = Double.parseDouble(priceField.getText().trim());
                if (price <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid price", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int stock;
            try {
                stock = Integer.parseInt(stockField.getText().trim());
                if (stock < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid stock quantity", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            LocalDate expirationDate;
            try {
                expirationDate = LocalDate.parse(expirationField.getText().trim(), dateFormatter);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid date in format YYYY-MM-DD", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update item
            item.setCategory((String) categoryCombo.getSelectedItem());
            item.setPrice(price);
            item.setStockQuantity(stock);
            item.setExpirationDate(expirationDate);
            
            inventoryManager.updateItem(item);
            
            refreshTableData();
            updateAlerts();
            refreshDashboard(); // Auto-refresh dashboard stats
            dialog.dispose();
            
            JOptionPane.showMessageDialog(dialog, "Item updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /**
     * Called when a new help request is received
     */
    private void handleNewHelpRequest(HelpRequestManager.HelpRequest request) {
        // Update the UI
        updateHelpRequests();
        
        // Show a notification
        boolean isUrgent = request.getDetails().startsWith("[URGENT]");
        
        // Play notification sound
        Toolkit.getDefaultToolkit().beep();
        
        // Display the notification dialog
        SwingUtilities.invokeLater(() -> {
            JDialog dialog = new JDialog(this, "Customer Assistance Request", false);
            dialog.setSize(400, 200);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());
            
            JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            contentPanel.setBackground(Color.WHITE);
            
            JLabel iconLabel = new JLabel(isUrgent ? "⚠️" : "ℹ️");
            iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 48));
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            JLabel titleLabel = new JLabel("New Assistance Request");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            JLabel detailsLabel = new JLabel(
                "<html><b>Location:</b> " + request.getLocation() + 
                "<br><b>Type:</b> " + request.getIssueType() + 
                (isUrgent ? "<br><span style='color:red'><b>URGENT ASSISTANCE NEEDED</b></span>" : "") +
                "</html>"
            );
            detailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            JPanel infoPanel = new JPanel(new BorderLayout(0, 10));
            infoPanel.setBackground(Color.WHITE);
            infoPanel.add(titleLabel, BorderLayout.NORTH);
            infoPanel.add(detailsLabel, BorderLayout.CENTER);
            
            contentPanel.add(iconLabel, BorderLayout.WEST);
            contentPanel.add(infoPanel, BorderLayout.CENTER);
            
            JButton okButton = new JButton("OK");
            okButton.addActionListener(e -> dialog.dispose());
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.add(okButton);
            
            dialog.add(contentPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);
            
            // Auto-dismiss after 10 seconds if urgent, 15 seconds otherwise
            new Timer(isUrgent ? 10000 : 15000, e -> dialog.dispose()).start();
        });
    }
    
    /**
     * Helper method to extract the stat value text from a mini stat card
     */
    private String getStatCardValue(JPanel statCard) {
        try {
            // The value is in: statCard -> component(1) which is valuePanel -> component(0) which is valueLabel
            JPanel valuePanel = (JPanel) statCard.getComponent(1);
            JLabel valueLabel = (JLabel) valuePanel.getComponent(0);
            return valueLabel.getText();
        } catch (Exception e) {
            return "N/A";
        }
    }
    
    /**
     * Exports a sales report to PDF with the given title, date range, and table model
     */
    private void printSalesReport(String reportTitle, String dateInfo, DefaultTableModel tableModel, 
                                   Map<String, String> statistics) {
        // Create file chooser for PDF export
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report as PDF");
        
        // Set default filename based on report title and date
        String defaultFilename = reportTitle.replaceAll(" ", "_") + "_" + 
                                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";
        fileChooser.setSelectedFile(new java.io.File(defaultFilename));
        
        // Set file filter to only show PDF files
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(java.io.File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".pdf");
            }
            
            @Override
            public String getDescription() {
                return "PDF Documents (*.pdf)";
            }
        });
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            
            // Ensure .pdf extension
            if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
                fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".pdf");
            }
            
            try {
                // Use PrinterJob to render to PDF
                PrinterJob job = PrinterJob.getPrinterJob();
                job.setJobName(reportTitle);
                
                // Create print service for PDF export
                javax.print.PrintService pdfService = null;
                for (javax.print.PrintService service : PrinterJob.lookupPrintServices()) {
                    if (service.getName().toLowerCase().contains("pdf") || 
                        service.getName().toLowerCase().contains("adobe")) {
                        pdfService = service;
                        break;
                    }
                }
                
                // If no PDF printer found, use default and write to file using attributes
                javax.print.attribute.PrintRequestAttributeSet attributes = 
                    new javax.print.attribute.HashPrintRequestAttributeSet();
                attributes.add(new javax.print.attribute.standard.Destination(fileToSave.toURI()));
                
                job.setPrintable(new Printable() {
                    @Override
                    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
                        if (pageIndex > 0) {
                            return NO_SUCH_PAGE;
                        }
                        
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.translate(pf.getImageableX(), pf.getImageableY());
                        
                        // Set up fonts
                        Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
                        Font headerFont = new Font("Segoe UI", Font.BOLD, 14);
                        Font normalFont = new Font("Segoe UI", Font.PLAIN, 11);
                        Font smallFont = new Font("Segoe UI", Font.PLAIN, 9);
                        
                        int y = 30;
                        int lineHeight = 18;
                        
                        // Print title
                        g2d.setFont(titleFont);
                        g2d.drawString("SweetBatterBakeshop", 30, y);
                        y += lineHeight + 10;
                        
                        g2d.setFont(headerFont);
                        g2d.drawString(reportTitle, 30, y);
                        y += lineHeight + 5;
                        
                        g2d.setFont(normalFont);
                        g2d.drawString(dateInfo, 30, y);
                        y += lineHeight;
                        
                        g2d.drawString("Generated: " + java.time.LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 30, y);
                        y += lineHeight + 15;
                        
                        // Print statistics box
                        g2d.setFont(headerFont);
                        g2d.drawString("Summary Statistics:", 30, y);
                        y += lineHeight;
                        
                        // Draw border around statistics
                        int statsBoxY = y;
                        g2d.setFont(normalFont);
                        for (Map.Entry<String, String> entry : statistics.entrySet()) {
                            g2d.drawString("• " + entry.getKey() + ": " + entry.getValue(), 40, y);
                            y += lineHeight;
                        }
                        g2d.drawRect(30, statsBoxY - 5, 500, (statistics.size() * lineHeight) + 5);
                        y += 15;
                        
                        // Print table header
                        g2d.setFont(headerFont);
                        g2d.drawString("Detailed Transactions:", 30, y);
                        y += lineHeight + 5;
                        
                        // Print table column headers
                        g2d.setFont(smallFont);
                        int x = 30;
                        int[] colWidths = new int[tableModel.getColumnCount()];
                        int availableWidth = (int) pf.getImageableWidth() - 60;
                        int equalWidth = availableWidth / tableModel.getColumnCount();
                        
                        for (int i = 0; i < tableModel.getColumnCount(); i++) {
                            colWidths[i] = equalWidth;
                            g2d.drawString(tableModel.getColumnName(i), x, y);
                            x += colWidths[i];
                        }
                        y += lineHeight;
                        
                        // Draw line under headers
                        g2d.drawLine(30, y, 30 + availableWidth, y);
                        y += 5;
                        
                        // Print table rows
                        int maxRows = Math.min(tableModel.getRowCount(), 30);
                        for (int row = 0; row < maxRows; row++) {
                            x = 30;
                            for (int col = 0; col < tableModel.getColumnCount(); col++) {
                                Object value = tableModel.getValueAt(row, col);
                                String text = value != null ? value.toString() : "";
                                int maxChars = colWidths[col] / 6;
                                if (text.length() > maxChars) {
                                    text = text.substring(0, maxChars - 2) + "..";
                                }
                                g2d.drawString(text, x, y);
                                x += colWidths[col];
                            }
                            y += lineHeight;
                            
                            if (y > pf.getImageableHeight() - 60) {
                                break;
                            }
                        }
                        
                        // Print footer
                        y = (int) pf.getImageableHeight() - 30;
                        g2d.drawLine(30, y, 30 + availableWidth, y);
                        y += 15;
                        g2d.setFont(smallFont);
                        g2d.drawString("Page 1 of 1", 30, y);
                        String footer = "SweetBatterBakeshop © 2025 | Confidential";
                        g2d.drawString(footer, (int) pf.getImageableWidth() - 180, y);
                        
                        return PAGE_EXISTS;
                    }
                });
                
                // Try to print to PDF
                try {
                    if (pdfService != null) {
                        job.setPrintService(pdfService);
                    }
                    job.print(attributes);
                    
                    JOptionPane.showMessageDialog(this,
                        "Report exported to PDF successfully!\n" + fileToSave.getAbsolutePath(),
                        "Export Success",
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    // If PDF export fails, create a simple text report
                    exportAsTextReport(fileToSave, reportTitle, dateInfo, tableModel, statistics);
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error exporting report: " + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Fallback method to export report as formatted text file
     */
    private void exportAsTextReport(java.io.File file, String reportTitle, String dateInfo,
                                   DefaultTableModel tableModel, Map<String, String> statistics) {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
            writer.println("═══════════════════════════════════════════════════════");
            writer.println("           SWEETBATTERBAKESHOP SALES REPORT           ");
            writer.println("═══════════════════════════════════════════════════════");
            writer.println();
            writer.println(reportTitle);
            writer.println(dateInfo);
            writer.println("Generated: " + java.time.LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.println();
            writer.println("───────────────────────────────────────────────────────");
            writer.println("SUMMARY STATISTICS");
            writer.println("───────────────────────────────────────────────────────");
            for (Map.Entry<String, String> entry : statistics.entrySet()) {
                writer.printf("%-20s : %s%n", entry.getKey(), entry.getValue());
            }
            writer.println();
            writer.println("───────────────────────────────────────────────────────");
            writer.println("DETAILED TRANSACTIONS");
            writer.println("───────────────────────────────────────────────────────");
            
            // Print column headers
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                writer.printf("%-15s", tableModel.getColumnName(i));
            }
            writer.println();
            writer.println("───────────────────────────────────────────────────────");
            
            // Print rows
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                for (int col = 0; col < tableModel.getColumnCount(); col++) {
                    Object value = tableModel.getValueAt(row, col);
                    String text = value != null ? value.toString() : "";
                    writer.printf("%-15s", text.length() > 15 ? text.substring(0, 12) + "..." : text);
                }
                writer.println();
            }
            
            writer.println();
            writer.println("═══════════════════════════════════════════════════════");
            writer.println("         SweetBatterBakeshop © 2025 | Confidential    ");
            writer.println("═══════════════════════════════════════════════════════");
            
            JOptionPane.showMessageDialog(this,
                "Report exported as text file successfully!\n" + file.getAbsolutePath(),
                "Export Success",
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error creating text report: " + e.getMessage(),
                "Export Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public void dispose() {
        HelpRequestManager.getInstance().removeListener(this::handleNewHelpRequest);
        super.dispose();
    }
    
}


