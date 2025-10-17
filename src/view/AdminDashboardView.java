package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import model.*;
import controller.AdminController;

public class AdminDashboardView extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private AdminController controller;
    private Admin currentAdmin;
    
    private JTabbedPane tabbedPane;
    private JLabel welcomeLabel;
    private JLabel totalRevenueLabel;
    private JLabel totalTransactionsLabel;
    
    // Product Management
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private JButton addProductButton;
    private JButton editProductButton;
    private JButton deleteProductButton;
    private JButton updateStockButton;
    
    // Transaction Management
    private JTable transactionTable;
    private DefaultTableModel transactionTableModel;
    private JButton viewTransactionButton;
    private JButton refreshTransactionsButton;
    
    // Reports
    private JTextArea reportArea;
    private JButton generateReportButton;
    private JComboBox<String> reportTypeCombo;

    public AdminDashboardView(Admin admin, AdminController controller) {
        this.currentAdmin = admin;
        this.controller = controller;
        initComponents();
        loadData();
    }

    private void initComponents() {
        setTitle("Sweet Batter Bakeshop - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true); // Remove decorations for fullscreen experience

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed pane with modern styling
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(new Color(80, 80, 80));
        
        tabbedPane.addTab("📊 Dashboard", createDashboardPanel());
        tabbedPane.addTab("🎂 Products", createProductPanel());
        tabbedPane.addTab("💰 Transactions", createTransactionPanel());
        tabbedPane.addTab("📈 Reports", createReportsPanel());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(0xA9907E),
                    0, getHeight(), new Color(0x8B7355)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setPreferredSize(new Dimension(1200, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("🧁 Sweet Batter Bakeshop - Admin Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);

        welcomeLabel = new JLabel("Welcome, " + currentAdmin.getFullName() + " (" + currentAdmin.getRole() + ")");
        welcomeLabel.setFont(new Font("Segoe UI Light", Font.PLAIN, 15));
        welcomeLabel.setForeground(new Color(255, 255, 255, 220));

        // Right panel with logout and close buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        
        JButton logoutButton = createModernButton("🚪 Logout", Color.WHITE, new Color(0x8B7355));
        logoutButton.setPreferredSize(new Dimension(120, 38));
        logoutButton.addActionListener(e -> handleLogout());
        
        JButton closeButton = new JButton("✕") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(200, 50, 50));
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(220, 80, 80));
                } else {
                    g2d.setColor(new Color(169, 144, 126, 100));
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        closeButton.setForeground(Color.WHITE);
        closeButton.setPreferredSize(new Dimension(45, 38));
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setToolTipText("Exit Admin Dashboard");
        closeButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION
            );
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
            }
        });

        rightPanel.add(logoutButton);
        rightPanel.add(closeButton);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(welcomeLabel, BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JButton createModernButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(bgColor.brighter());
                } else {
                    g2d.setColor(bgColor);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(248, 248, 248));

        // Summary cards
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        summaryPanel.setBackground(new Color(248, 248, 248));
        
        totalRevenueLabel = new JLabel("₱0.00", SwingConstants.CENTER);
        totalRevenueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        totalRevenueLabel.setForeground(new Color(0xA9907E));
        JPanel revenueCard = createSummaryCard("💵 Total Revenue", totalRevenueLabel, new Color(255, 248, 240));
        
        totalTransactionsLabel = new JLabel("0", SwingConstants.CENTER);
        totalTransactionsLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        totalTransactionsLabel.setForeground(new Color(0x8B7355));
        JPanel transactionCard = createSummaryCard("📝 Total Transactions", totalTransactionsLabel, new Color(240, 255, 248));
        
        JLabel productCountLabel = new JLabel("0", SwingConstants.CENTER);
        productCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        productCountLabel.setForeground(new Color(0xA9907E));
        JPanel productCard = createSummaryCard("🎂 Total Products", productCountLabel, new Color(248, 240, 255));
        
        summaryPanel.add(revenueCard);
        summaryPanel.add(transactionCard);
        summaryPanel.add(productCard);
        
        panel.add(summaryPanel, BorderLayout.NORTH);

        // Quick actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 20));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                "Quick Actions",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                new Color(80, 80, 80)
            ),
            BorderFactory.createEmptyBorder(10, 15, 15, 15)
        ));
        
        JButton refreshButton = createModernButton("🔄 Refresh Data", new Color(0xA9907E), Color.WHITE);
        refreshButton.setPreferredSize(new Dimension(160, 42));
        refreshButton.addActionListener(e -> loadData());
        
        JButton backToMainButton = createModernButton("🏠 Back to Main App", new Color(0x8B7355), Color.WHITE);
        backToMainButton.setPreferredSize(new Dimension(180, 42));
        backToMainButton.addActionListener(e -> backToMainApp());
        
        actionsPanel.add(refreshButton);
        actionsPanel.add(backToMainButton);
        
        panel.add(actionsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSummaryCard(String title, JLabel valueLabel, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xA9907E), 1),
            BorderFactory.createEmptyBorder(30, 25, 30, 25)
        ));
        card.setOpaque(false);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(80, 80, 80));

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(valueLabel);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(centerPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Table
        String[] columns = {"Name", "Category", "Price", "Stock", "Available"};
        productTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(productTableModel);
        productTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        productTable.setRowHeight(32);
        productTable.setGridColor(new Color(240, 240, 240));
        productTable.setSelectionBackground(new Color(169, 144, 126, 50));
        productTable.setSelectionForeground(Color.BLACK);
        productTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        productTable.getTableHeader().setBackground(new Color(0xA9907E));
        productTable.getTableHeader().setForeground(Color.WHITE);
        productTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
        
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 15));
        buttonPanel.setBackground(new Color(250, 250, 250));
        
        addProductButton = createModernButton("➕ Add Product", new Color(0xA9907E), Color.WHITE);
        addProductButton.setPreferredSize(new Dimension(150, 40));
        addProductButton.addActionListener(e -> controller.handleAddProduct());
        
        editProductButton = createModernButton("✏️ Edit Product", new Color(0x8B7355), Color.WHITE);
        editProductButton.setPreferredSize(new Dimension(150, 40));
        editProductButton.addActionListener(e -> controller.handleEditProduct(productTable.getSelectedRow()));
        
        deleteProductButton = createModernButton("🗑️ Delete Product", new Color(180, 60, 60), Color.WHITE);
        deleteProductButton.setPreferredSize(new Dimension(160, 40));
        deleteProductButton.addActionListener(e -> controller.handleDeleteProduct(productTable.getSelectedRow()));
        
        updateStockButton = createModernButton("📦 Update Stock", new Color(70, 130, 180), Color.WHITE);
        updateStockButton.setPreferredSize(new Dimension(160, 40));
        updateStockButton.addActionListener(e -> controller.handleUpdateStock(productTable.getSelectedRow()));
        
        buttonPanel.add(addProductButton);
        buttonPanel.add(editProductButton);
        buttonPanel.add(deleteProductButton);
        buttonPanel.add(updateStockButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTransactionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Table
        String[] columns = {"Transaction ID", "Date & Time", "Total Amount", "Payment Method", "Status"};
        transactionTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transactionTable = new JTable(transactionTableModel);
        transactionTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        transactionTable.setRowHeight(32);
        transactionTable.setGridColor(new Color(240, 240, 240));
        transactionTable.setSelectionBackground(new Color(139, 115, 85, 50));
        transactionTable.setSelectionForeground(Color.BLACK);
        transactionTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        transactionTable.getTableHeader().setBackground(new Color(0x8B7355));
        transactionTable.getTableHeader().setForeground(Color.WHITE);
        transactionTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 15));
        buttonPanel.setBackground(new Color(250, 250, 250));
        
        viewTransactionButton = createModernButton("👁️ View Details", new Color(0xA9907E), Color.WHITE);
        viewTransactionButton.setPreferredSize(new Dimension(150, 40));
        viewTransactionButton.addActionListener(e -> controller.handleViewTransaction(transactionTable.getSelectedRow()));
        
        refreshTransactionsButton = createModernButton("🔄 Refresh", new Color(0x8B7355), Color.WHITE);
        refreshTransactionsButton.setPreferredSize(new Dimension(120, 40));
        refreshTransactionsButton.addActionListener(e -> loadTransactionData());
        
        buttonPanel.add(viewTransactionButton);
        buttonPanel.add(refreshTransactionsButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Report type selector
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        topPanel.setBackground(new Color(250, 250, 250));
        
        JLabel reportLabel = new JLabel("📋 Report Type:");
        reportLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        reportLabel.setForeground(new Color(80, 80, 80));
        topPanel.add(reportLabel);
        
        reportTypeCombo = new JComboBox<>(new String[]{
            "Sales Summary",
            "Product Inventory",
            "Top Selling Products",
            "Transaction History"
        });
        reportTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        reportTypeCombo.setPreferredSize(new Dimension(220, 38));
        reportTypeCombo.setBackground(Color.WHITE);
        topPanel.add(reportTypeCombo);
        
        generateReportButton = createModernButton("📊 Generate Report", new Color(0xA9907E), Color.WHITE);
        generateReportButton.setPreferredSize(new Dimension(180, 38));
        generateReportButton.addActionListener(e -> controller.handleGenerateReport(
            (String) reportTypeCombo.getSelectedItem()
        ));
        topPanel.add(generateReportButton);

        // Report display area
        reportArea = new JTextArea();
        reportArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        reportArea.setEditable(false);
        reportArea.setBackground(new Color(252, 252, 252));
        reportArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    public void loadData() {
        loadProductData();
        loadTransactionData();
        loadDashboardStats();
    }

    public void loadProductData() {
        productTableModel.setRowCount(0);
        for (Product product : controller.getAllProducts()) {
            productTableModel.addRow(new Object[]{
                product.getName(),
                product.getCategory(),
                "₱" + String.format("%.2f", product.getPrice()),
                product.getStock(),
                product.isAvailable() ? "Yes" : "No"
            });
        }
    }

    public void loadTransactionData() {
        transactionTableModel.setRowCount(0);
        for (Transaction transaction : controller.getAllTransactions()) {
            transactionTableModel.addRow(new Object[]{
                transaction.getTransactionId(),
                transaction.getDateTime().toString(),
                "₱" + String.format("%.2f", transaction.getTotalAmount()),
                transaction.getPaymentMethod(),
                transaction.getStatus()
            });
        }
    }

    public void loadDashboardStats() {
        double totalRevenue = controller.getTotalRevenue();
        int totalTransactions = controller.getTotalTransactionCount();
        
        totalRevenueLabel.setText("₱" + String.format("%.2f", totalRevenue));
        totalTransactionsLabel.setText(String.valueOf(totalTransactions));
    }

    public void displayReport(String report) {
        reportArea.setText(report);
    }

    private void handleLogout() {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            dispose();
            controller.showLoginDialog();
        }
    }

    private void backToMainApp() {
        int result = JOptionPane.showConfirmDialog(this,
            "Return to customer application?",
            "Confirm",
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            controller.backToMainApp();
        }
    }

    public JTable getProductTable() {
        return productTable;
    }

    public JTable getTransactionTable() {
        return transactionTable;
    }
}
