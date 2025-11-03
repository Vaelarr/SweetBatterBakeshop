package kiosk.view.admin;

import kiosk.controller.CustomOrderController;
import kiosk.model.CustomOrder;
import kiosk.model.CustomOrder.*;
import kiosk.database.dao.CustomOrderDAO;
import kiosk.view.BakeryTheme;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Admin Panel for Managing Custom Orders
 * Integrates with existing AdminPanel
 */
public class CustomOrdersAdminPanel extends JPanel {
    private final Color PRIMARY_COLOR = BakeryTheme.PRIMARY_COLOR;
    private final Color ACCENT_COLOR = BakeryTheme.ACCENT_COLOR;
    private final Color SUCCESS_COLOR = BakeryTheme.SUCCESS;
    private final Color WARNING_COLOR = BakeryTheme.WARNING;
    private final Color ERROR_COLOR = BakeryTheme.ERROR;
    private final Color CARD_BG = BakeryTheme.CARD_COLOR;
    
    private CustomOrderController controller;
    private CustomOrderDAO orderDAO;
    private JTable ordersTable;
    private DefaultTableModel tableModel;
    
    // Statistics labels
    private JLabel totalOrdersLabel;
    private JLabel pendingOrdersLabel;
    private JLabel confirmedOrdersLabel;
    private JLabel totalRevenueLabel;
    
    // Alert system
    private JLabel alertBadge;
    private JPanel alertsPanel;
    private Timer alertCheckTimer;
    private int lastOrderCount = 0;
    private static final int ALERT_CHECK_INTERVAL = 15000; // 15 seconds
    
    public CustomOrdersAdminPanel(CustomOrderController controller) {
        this.controller = controller;
        this.orderDAO = new CustomOrderDAO();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        createUI();
        loadData();
        startAlertMonitoring();
    }
    
    private void createUI() {
        // Header
        JPanel headerPanel = createHeader();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(800);
        splitPane.setResizeWeight(0.7);
        
        // Left side - Main content
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setOpaque(false);
        
        // Statistics Dashboard
        JPanel statsPanel = createStatisticsPanel();
        contentPanel.add(statsPanel, BorderLayout.NORTH);
        
        // Orders Table
        JPanel tablePanel = createTablePanel();
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Action Buttons
        JPanel actionsPanel = createActionsPanel();
        contentPanel.add(actionsPanel, BorderLayout.SOUTH);
        
        // Right side - Alerts Panel
        JPanel alertsSidePanel = createAlertsPanel();
        
        splitPane.setLeftComponent(contentPanel);
        splitPane.setRightComponent(alertsSidePanel);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("🎂 Custom Orders Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        // Alert badge
        alertBadge = new JLabel("0");
        alertBadge.setFont(new Font("Segoe UI", Font.BOLD, 16));
        alertBadge.setForeground(Color.WHITE);
        alertBadge.setBackground(ERROR_COLOR);
        alertBadge.setOpaque(true);
        alertBadge.setHorizontalAlignment(SwingConstants.CENTER);
        alertBadge.setPreferredSize(new Dimension(40, 40));
        alertBadge.setBorder(new LineBorder(Color.WHITE, 2, true));
        alertBadge.setToolTipText("Pending orders requiring attention");
        alertBadge.setVisible(false);
        
        leftPanel.add(titleLabel);
        leftPanel.add(alertBadge);
        
        JButton refreshButton = new JButton("🔄 Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        refreshButton.setBackground(Color.WHITE);
        refreshButton.setForeground(PRIMARY_COLOR);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> {
            loadData();
            updateAlerts();
        });
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(refreshButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Total Orders Card
        totalOrdersLabel = new JLabel("0");
        panel.add(createStatCard("Total Orders", totalOrdersLabel, PRIMARY_COLOR, "📋"));
        
        // Pending Orders Card
        pendingOrdersLabel = new JLabel("0");
        panel.add(createStatCard("Pending", pendingOrdersLabel, WARNING_COLOR, "⏳"));
        
        // Confirmed Orders Card
        confirmedOrdersLabel = new JLabel("0");
        panel.add(createStatCard("Confirmed", confirmedOrdersLabel, SUCCESS_COLOR, "✓"));
        
        // Total Revenue Card
        totalRevenueLabel = new JLabel("₱0.00");
        panel.add(createStatCard("Total Revenue", totalRevenueLabel, ACCENT_COLOR, "💰"));
        
        return panel;
    }
    
    private JPanel createStatCard(String title, JLabel valueLabel, Color color, String icon) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(color, 2, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(Color.GRAY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLabel);
        
        return card;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // Table
        String[] columns = {"Order #", "Customer", "Product", "Servings", "Total", 
                           "Deposit", "Balance", "Status", "Pickup Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        ordersTable = new JTable(tableModel);
        ordersTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ordersTable.setRowHeight(35);
        ordersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        ordersTable.getTableHeader().setBackground(PRIMARY_COLOR);
        ordersTable.getTableHeader().setForeground(Color.WHITE);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ordersTable.setGridColor(new Color(230, 230, 230));
        
        // Custom renderer for status column
        ordersTable.getColumnModel().getColumn(7).setCellRenderer(new StatusCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setOpaque(false);
        
        JButton viewButton = createActionButton("📋 View Details", PRIMARY_COLOR);
        viewButton.addActionListener(e -> viewOrderDetails());
        
        JButton confirmButton = createActionButton("✅ Confirm Order", SUCCESS_COLOR);
        confirmButton.addActionListener(e -> updateStatus(OrderStatus.CONFIRMED));
        
        JButton inProgressButton = createActionButton("👨‍🍳 In Progress", new Color(33, 150, 243));
        inProgressButton.addActionListener(e -> updateStatus(OrderStatus.IN_PRODUCTION));
        
        JButton completeButton = createActionButton("🎉 Mark Complete", new Color(76, 175, 80));
        completeButton.addActionListener(e -> updateStatus(OrderStatus.COMPLETED));
        
        JButton cancelButton = createActionButton("❌ Cancel Order", ERROR_COLOR);
        cancelButton.addActionListener(e -> cancelOrder());
        
        panel.add(viewButton);
        panel.add(confirmButton);
        panel.add(inProgressButton);
        panel.add(completeButton);
        panel.add(cancelButton);
        
        return panel;
    }
    
    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 35));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private void loadData() {
        // Load statistics
        CustomOrderDAO.OrderStatistics stats = controller.getOrderStatistics();
        totalOrdersLabel.setText(String.valueOf(stats.totalOrders));
        pendingOrdersLabel.setText(String.valueOf(stats.pendingOrders));
        confirmedOrdersLabel.setText(String.valueOf(stats.confirmedOrders));
        totalRevenueLabel.setText(String.format("₱%.2f", stats.totalRevenue));
        
        // Load orders
        tableModel.setRowCount(0);
        List<CustomOrder> orders = controller.getAllOrders();
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
        
        for (CustomOrder order : orders) {
            Object[] row = {
                order.getOrderNumber(),
                order.getCustomerId(),
                order.getProductName(),
                order.getServings(),
                String.format("₱%.2f", order.getTotalAmount()),
                String.format("₱%.2f", order.getDepositPaid()),
                String.format("₱%.2f", order.getBalanceDue()),
                order.getOrderStatus().toString(),
                order.getPickupDatetime().format(dateFormatter)
            };
            tableModel.addRow(row);
        }
    }
    
    private void viewOrderDetails() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an order to view", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String orderNumber = (String) tableModel.getValueAt(selectedRow, 0);
        CustomOrder order = controller.getOrder(orderNumber);
        
        if (order != null) {
            showOrderDetailsDialog(order);
        }
    }
    
    private void showOrderDetailsDialog(CustomOrder order) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                     "Order Details", true);
        dialog.setSize(600, 700);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        // Order info
        panel.add(createDetailRow("Order Number:", order.getOrderNumber()));
        panel.add(createDetailRow("Customer ID:", order.getCustomerId()));
        panel.add(createDetailRow("Product:", order.getProductName()));
        panel.add(createDetailRow("Servings:", String.valueOf(order.getServings())));
        panel.add(createDetailRow("Message on Item:", order.getMessageOnItem()));
        panel.add(createDetailRow("Special Instructions:", order.getSpecialInstructions()));
        
        panel.add(Box.createVerticalStrut(15));
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(15));
        
        // Add-ons
        JLabel addonsLabel = new JLabel("Add-ons:");
        addonsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(addonsLabel);
        
        for (kiosk.model.OrderAddOn addon : order.getAddons()) {
            panel.add(createDetailRow("  • " + addon.getCategoryName(), 
                                     addon.getAddonName() + " (+" + String.format("₱%.2f", addon.getTotalAddonPrice()) + ")"));
        }
        
        panel.add(Box.createVerticalStrut(15));
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(15));
        
        // Pricing
        panel.add(createDetailRow("Base Price:", String.format("₱%.2f", order.getBasePrice())));
        panel.add(createDetailRow("Add-ons Total:", String.format("₱%.2f", order.getAddonsTotal())));
        panel.add(createDetailRow("Subtotal:", String.format("₱%.2f", order.getSubtotal())));
        panel.add(createDetailRow("Tax (12%):", String.format("₱%.2f", order.getTaxAmount())));
        panel.add(createDetailRow("Delivery Fee:", String.format("₱%.2f", order.getDeliveryFee())));
        panel.add(createDetailRow("Total Amount:", String.format("₱%.2f", order.getTotalAmount())));
        panel.add(createDetailRow("Deposit Paid:", String.format("₱%.2f", order.getDepositPaid())));
        panel.add(createDetailRow("Balance Due:", String.format("₱%.2f", order.getBalanceDue())));
        
        JScrollPane scrollPane = new JScrollPane(panel);
        dialog.add(scrollPane);
        dialog.setVisible(true);
    }
    
    private JPanel createDetailRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JLabel valueComp = new JLabel(value != null ? value : "N/A");
        valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        row.add(labelComp, BorderLayout.WEST);
        row.add(valueComp, BorderLayout.CENTER);
        
        return row;
    }
    
    private void confirmOrder() {
        updateStatus(OrderStatus.CONFIRMED);
    }
    
    private void updateStatus(OrderStatus newStatus) {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an order", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String orderNumber = (String) tableModel.getValueAt(selectedRow, 0);
        
        boolean success = controller.updateOrderStatus(orderNumber, newStatus);
        
        if (success) {
            JOptionPane.showMessageDialog(this, 
                "Order status updated to " + newStatus, 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to update order status", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cancelOrder() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an order to cancel", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String reason = JOptionPane.showInputDialog(this, 
            "Enter cancellation reason:", 
            "Cancel Order", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (reason != null && !reason.trim().isEmpty()) {
            String orderNumber = (String) tableModel.getValueAt(selectedRow, 0);
            boolean success = controller.cancelOrder(orderNumber, reason, "Admin");
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Order cancelled successfully", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadData();
            }
        }
    }
    
    // ==================== ALERT SYSTEM ====================
    
    /**
     * Creates the alerts side panel
     */
    private JPanel createAlertsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY),
            new EmptyBorder(15, 15, 15, 15)
        ));
        panel.setPreferredSize(new Dimension(350, 0));
        
        // Header
        JLabel titleLabel = new JLabel("⚠️ Order Alerts");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        
        // Alerts list panel
        alertsPanel = new JPanel();
        alertsPanel.setLayout(new BoxLayout(alertsPanel, BoxLayout.Y_AXIS));
        alertsPanel.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(alertsPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.WHITE);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Start monitoring for new orders and alerts
     */
    private void startAlertMonitoring() {
        // Initialize order count
        lastOrderCount = orderDAO.getAllOrders().size();
        
        // Create timer for periodic checks
        alertCheckTimer = new Timer(ALERT_CHECK_INTERVAL, e -> checkForNewOrders());
        alertCheckTimer.start();
        
        // Initial alert update
        updateAlerts();
    }
    
    /**
     * Check for new orders
     */
    private void checkForNewOrders() {
        List<CustomOrder> allOrders = orderDAO.getAllOrders();
        int currentCount = allOrders.size();
        
        if (currentCount > lastOrderCount) {
            // New order(s) detected
            int newOrdersCount = currentCount - lastOrderCount;
            showNewOrderNotification(newOrdersCount);
            loadData();
        }
        
        lastOrderCount = currentCount;
        updateAlerts();
    }
    
    /**
     * Show notification for new orders
     */
    private void showNewOrderNotification(int count) {
        SwingUtilities.invokeLater(() -> {
            String message = count == 1 ? 
                "1 new custom order received!" :
                count + " new custom orders received!";
            
            JOptionPane.showMessageDialog(this,
                message,
                "New Order Alert",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Play notification sound
            Toolkit.getDefaultToolkit().beep();
        });
    }
    
    /**
     * Update alerts panel with current alerts
     */
    private void updateAlerts() {
        if (alertsPanel == null) return;
        
        alertsPanel.removeAll();
        
        List<CustomOrder> allOrders = orderDAO.getAllOrders();
        List<CustomOrder> pendingOrders = new java.util.ArrayList<>();
        List<CustomOrder> overdueOrders = new java.util.ArrayList<>();
        List<CustomOrder> readyOrders = new java.util.ArrayList<>();
        
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        
        for (CustomOrder order : allOrders) {
            OrderStatus status = order.getOrderStatus();
            
            // Pending orders (need confirmation)
            if (status == OrderStatus.PENDING) {
                pendingOrders.add(order);
            }
            
            // Orders ready for pickup/delivery
            if (status == OrderStatus.READY || status == OrderStatus.COMPLETED) {
                readyOrders.add(order);
            }
            
            // Overdue orders (pickup time passed but not completed)
            if (order.getPickupDatetime() != null && 
                order.getPickupDatetime().isBefore(now) &&
                (status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED || 
                 status == OrderStatus.IN_PRODUCTION)) {
                overdueOrders.add(order);
            }
        }
        
        int totalAlerts = pendingOrders.size() + overdueOrders.size();
        
        // Update badge
        if (totalAlerts > 0) {
            alertBadge.setText(String.valueOf(totalAlerts));
            alertBadge.setVisible(true);
        } else {
            alertBadge.setVisible(false);
        }
        
        // Add alerts to panel
        if (totalAlerts == 0) {
            JLabel noAlertsLabel = new JLabel("✅ No alerts - All orders on track!");
            noAlertsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            noAlertsLabel.setForeground(Color.GRAY);
            noAlertsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            alertsPanel.add(noAlertsLabel);
        } else {
            // Add pending orders alerts
            if (!pendingOrders.isEmpty()) {
                JLabel sectionLabel = new JLabel("⏳ Pending Confirmation (" + pendingOrders.size() + ")");
                sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                sectionLabel.setForeground(WARNING_COLOR);
                sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                alertsPanel.add(sectionLabel);
                alertsPanel.add(Box.createVerticalStrut(10));
                
                for (CustomOrder order : pendingOrders) {
                    alertsPanel.add(createAlertCard(order, "Needs confirmation"));
                    alertsPanel.add(Box.createVerticalStrut(8));
                }
                
                alertsPanel.add(Box.createVerticalStrut(10));
            }
            
            // Add overdue orders alerts
            if (!overdueOrders.isEmpty()) {
                JLabel sectionLabel = new JLabel("🚨 Overdue Orders (" + overdueOrders.size() + ")");
                sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                sectionLabel.setForeground(ERROR_COLOR);
                sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                alertsPanel.add(sectionLabel);
                alertsPanel.add(Box.createVerticalStrut(10));
                
                for (CustomOrder order : overdueOrders) {
                    alertsPanel.add(createAlertCard(order, "Overdue!"));
                    alertsPanel.add(Box.createVerticalStrut(8));
                }
                
                alertsPanel.add(Box.createVerticalStrut(10));
            }
            
            // Add ready orders info
            if (!readyOrders.isEmpty()) {
                JLabel sectionLabel = new JLabel("✅ Ready for Pickup (" + readyOrders.size() + ")");
                sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                sectionLabel.setForeground(SUCCESS_COLOR);
                sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                alertsPanel.add(sectionLabel);
                alertsPanel.add(Box.createVerticalStrut(10));
                
                for (CustomOrder order : readyOrders.subList(0, Math.min(3, readyOrders.size()))) {
                    alertsPanel.add(createAlertCard(order, "Ready"));
                    alertsPanel.add(Box.createVerticalStrut(8));
                }
                
                if (readyOrders.size() > 3) {
                    JLabel moreLabel = new JLabel("+ " + (readyOrders.size() - 3) + " more...");
                    moreLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                    moreLabel.setForeground(Color.GRAY);
                    moreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    alertsPanel.add(moreLabel);
                }
            }
        }
        
        alertsPanel.revalidate();
        alertsPanel.repaint();
    }
    
    /**
     * Create an alert card for an order
     */
    private JPanel createAlertCard(CustomOrder order, String alertType) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        Color borderColor;
        Color bgColor;
        
        switch (alertType) {
            case "Needs confirmation":
                borderColor = WARNING_COLOR;
                bgColor = new Color(255, 248, 225);
                break;
            case "Overdue!":
                borderColor = ERROR_COLOR;
                bgColor = new Color(255, 235, 238);
                break;
            case "Ready":
                borderColor = SUCCESS_COLOR;
                bgColor = new Color(232, 245, 233);
                break;
            default:
                borderColor = Color.LIGHT_GRAY;
                bgColor = Color.WHITE;
                break;
        }
        
        card.setBackground(bgColor);
        card.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(borderColor, 2),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel orderNumLabel = new JLabel("Order: " + order.getOrderNumber());
        orderNumLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        orderNumLabel.setForeground(Color.BLACK);
        orderNumLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel productLabel = new JLabel(order.getProductName());
        productLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        productLabel.setForeground(Color.DARK_GRAY);
        productLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel alertLabel = new JLabel(alertType);
        alertLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        alertLabel.setForeground(borderColor);
        alertLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        card.add(orderNumLabel);
        card.add(Box.createVerticalStrut(3));
        card.add(productLabel);
        card.add(Box.createVerticalStrut(3));
        card.add(alertLabel);
        
        // Make card clickable
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                highlightOrderInTable(order.getOrderNumber());
            }
        });
        
        return card;
    }
    
    /**
     * Highlight an order in the table
     */
    private void highlightOrderInTable(String orderNumber) {
        for (int i = 0; i < ordersTable.getRowCount(); i++) {
            String tableOrderNum = (String) ordersTable.getValueAt(i, 0);
            if (orderNumber.equals(tableOrderNum)) {
                ordersTable.setRowSelectionInterval(i, i);
                ordersTable.scrollRectToVisible(ordersTable.getCellRect(i, 0, true));
                break;
            }
        }
    }
    
    /**
     * Stop alert monitoring when panel is disposed
     */
    public void stopAlertMonitoring() {
        if (alertCheckTimer != null) {
            alertCheckTimer.stop();
            alertCheckTimer = null;
        }
    }
    
    /**
     * Custom cell renderer for status column
     */
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                                                      boolean isSelected, boolean hasFocus, 
                                                      int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value != null) {
                String status = value.toString();
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                
                switch (status) {
                    case "PENDING":
                        c.setForeground(WARNING_COLOR);
                        break;
                    case "CONFIRMED":
                        c.setForeground(SUCCESS_COLOR);
                        break;
                    case "IN_PRODUCTION":
                        c.setForeground(new Color(33, 150, 243));
                        break;
                    case "READY":
                        c.setForeground(new Color(156, 39, 176));
                        break;
                    case "COMPLETED":
                        c.setForeground(SUCCESS_COLOR);
                        break;
                    case "CANCELLED":
                        c.setForeground(ERROR_COLOR);
                        break;
                    default:
                        c.setForeground(Color.BLACK);
                }
            }
            
            return c;
        }
    }
}


