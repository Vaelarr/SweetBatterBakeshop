package kiosk;

import javax.swing.*;
import java.awt.*;
import kiosk.controller.CustomOrderController;
import kiosk.controller.CustomerController;
import kiosk.database.DatabaseConnection;
import kiosk.database.dao.CustomerDAO;
import kiosk.view.customer.CustomerAuthPanel;
import kiosk.view.customer.SimpleOrderPanel;
import kiosk.view.customer.CustomerSplashScreen;
import kiosk.view.BakeryTheme;
import java.sql.Connection;

/**
 * Simple Customer Portal for Custom Orders
 * Simplified version that matches existing API exactly
 * No complex features, just basic order placement
 */
public class SimpleCustomerPortal extends JFrame {
    
    private final Color PRIMARY_COLOR = BakeryTheme.PRIMARY_COLOR;
    private final Color BACKGROUND_COLOR = BakeryTheme.BACKGROUND_COLOR;
    
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private CustomerAuthPanel authPanel;
    private SimpleOrderPanel orderPanel;
    private CustomerController customerController;
    
    private String currentCustomerId;
    private String currentCustomerName;
    
    public SimpleCustomerPortal() {
        // Initialize database connection first
        initializeDatabaseConnection();
        
        // Don't show frame yet - splash screen will be shown first
        initializeFrame();
    }
    
    /**
     * Initialize and verify database connection
     */
    private void initializeDatabaseConnection() {
        System.out.println("=== Initializing Database Connection ===");
        try {
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            Connection conn = dbConnection.getConnection();
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Database connection successful!");
                System.out.println("✓ Connected to: " + conn.getMetaData().getURL());
                System.out.println("✓ Database: " + conn.getCatalog());
                System.out.println("✓ Custom Orders System: Ready");
            } else {
                System.err.println("✗ Failed to establish database connection!");
                JOptionPane.showMessageDialog(null,
                    "Failed to connect to database.\nPlease check your database configuration.",
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            System.err.println("✗ Database connection error: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Database Error: " + e.getMessage() + 
                "\n\nPlease ensure:\n" +
                "1. MySQL is running\n" +
                "2. Database 'kiosk_db' exists\n" +
                "3. Credentials in config/database.properties are correct",
                "Database Connection Error",
                JOptionPane.ERROR_MESSAGE);
        }
        System.out.println("========================================");
    }
    
    private void initializeFrame() {
        setTitle("Sweet Batter Bakeshop - Custom Orders");
        
        // Set to windowed fullscreen (maximized)
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Apply theme
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Initialize controller
        customerController = new CustomerController();
        
        // Verify custom orders system is available
        System.out.println("Initializing Custom Orders System...");
        CustomOrderController orderController = new CustomOrderController();
        System.out.println("✓ Custom Orders Controller: Ready");
        
        // Create panels
        authPanel = new CustomerAuthPanel(customerController);
        orderPanel = new SimpleOrderPanel(this::onLogout);
        
        // Set auth success listener
        authPanel.setAuthSuccessListener(customer -> {
            onLoginSuccess(customer.getCustomerId(), customer.getFirstName() + " " + customer.getLastName());
        });
        
        // Create header
        JPanel headerPanel = createHeader();
        
        // Create card layout for switching between auth and order views
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        contentPanel.add(authPanel, "AUTH");
        contentPanel.add(orderPanel, "ORDER");
        
        // Layout
        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        
        // Show auth panel first
        cardLayout.show(contentPanel, "AUTH");
        
        // Frame is ready but not visible yet - will be shown after splash
    }
    
    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("🎂 Sweet Batter Bakeshop - Custom Orders");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        panel.add(titleLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    private void onLoginSuccess(String customerId, String customerName) {
        this.currentCustomerId = customerId;
        this.currentCustomerName = customerName;
        
        System.out.println("Customer logged in: " + customerName + " (ID: " + customerId + ")");
        System.out.println("✓ Customer can now place custom orders");
        System.out.println("✓ Orders will be visible in Admin Panel");
        
        // Initialize order panel with customer info
        orderPanel.setCustomer(customerId, customerName);
        
        // Switch to order panel
        cardLayout.show(contentPanel, "ORDER");
    }
    
    private void onLogout() {
        this.currentCustomerId = null;
        this.currentCustomerName = null;
        
        // Switch back to auth panel
        cardLayout.show(contentPanel, "AUTH");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Show splash screen first
            CustomerSplashScreen splash = new CustomerSplashScreen();
            
            // Create the main portal (but don't show it yet)
            SimpleCustomerPortal portal = new SimpleCustomerPortal();
            
            // Show splash and load, then show portal when done
            splash.showSplashAndLoad(() -> {
                portal.setVisible(true);
            });
        });
    }
}


