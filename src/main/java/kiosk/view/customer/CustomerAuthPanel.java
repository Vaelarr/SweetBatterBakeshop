package kiosk.view.customer;

import kiosk.controller.CustomerController;
import kiosk.controller.CustomerController.*;
import kiosk.view.BakeryTheme;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Customer Login and Registration Panel
 */
public class CustomerAuthPanel extends JPanel {
    private final Color PRIMARY_COLOR = BakeryTheme.PRIMARY_COLOR;
    private final Color ACCENT_COLOR = BakeryTheme.ACCENT_COLOR;
    private final Color BACKGROUND_COLOR = BakeryTheme.BACKGROUND_COLOR;
    private final Color CARD_BG = BakeryTheme.CARD_COLOR;
    
    private CustomerController customerController;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    
    // Login components
    private JTextField loginEmailField;
    private JPasswordField loginPasswordField;
    
    // Registration components
    private JTextField regEmailField;
    private JPasswordField regPasswordField;
    private JPasswordField regConfirmPasswordField;
    private JTextField regFirstNameField;
    private JTextField regLastNameField;
    private JTextField regPhoneField;
    
    private AuthSuccessListener authListener;
    
    public CustomerAuthPanel(CustomerController controller) {
        this.customerController = controller;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        
        createUI();
    }
    
    private void createUI() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Logo and title panel
        JPanel logoTitlePanel = new JPanel();
        logoTitlePanel.setLayout(new BoxLayout(logoTitlePanel, BoxLayout.Y_AXIS));
        logoTitlePanel.setOpaque(false);
        
        // Load and add logo
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/kiosk/resources/store-logo.png"));
            // Scale logo to appropriate size
            Image scaledLogo = logoIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoTitlePanel.add(logoLabel);
            logoTitlePanel.add(Box.createVerticalStrut(15));
        } catch (Exception e) {
            System.err.println("Could not load store logo: " + e.getMessage());
        }
        
        JLabel titleLabel = new JLabel("🧁 SweetBatter Customer Portal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Order Custom Cakes & Bulk Orders");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(BakeryTheme.lighter(Color.WHITE, 0.2f));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        logoTitlePanel.add(titleLabel);
        logoTitlePanel.add(Box.createVerticalStrut(5));
        logoTitlePanel.add(subtitleLabel);
        
        // Center the logo and title panel
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerWrapper.setOpaque(false);
        centerWrapper.add(logoTitlePanel);
        
        headerPanel.add(centerWrapper, BorderLayout.CENTER);
        
        // Card Panel for Login/Register
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(BACKGROUND_COLOR);
        
        cardPanel.add(createLoginPanel(), "LOGIN");
        cardPanel.add(createRegisterPanel(), "REGISTER");
        
        // Main content area
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.add(cardPanel);
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BG);
        panel.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(BakeryTheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(40, 50, 40, 50)
        ));
        panel.setPreferredSize(new Dimension(500, 450));
        
        // Title
        JLabel titleLabel = new JLabel("Login to Your Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(30));
        
        // Email
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        loginEmailField = new JTextField(25);
        loginEmailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loginEmailField.setMaximumSize(new Dimension(400, 40));
        loginEmailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BakeryTheme.BORDER_LIGHT),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        panel.add(emailLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(loginEmailField);
        panel.add(Box.createVerticalStrut(20));
        
        // Password
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        loginPasswordField = new JPasswordField(25);
        loginPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loginPasswordField.setMaximumSize(new Dimension(400, 40));
        loginPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BakeryTheme.BORDER_LIGHT),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        panel.add(passwordLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(loginPasswordField);
        panel.add(Box.createVerticalStrut(30));
        
        // Login Button
        JButton loginButton = createStyledButton("Login", PRIMARY_COLOR);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> handleLogin());
        
        panel.add(loginButton);
        panel.add(Box.createVerticalStrut(20));
        
        // Switch to Register
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        switchPanel.setOpaque(false);
        switchPanel.setMaximumSize(new Dimension(400, 40));
        
        JLabel switchLabel = new JLabel("Don't have an account? ");
        switchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JButton switchButton = new JButton("Register Here");
        switchButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        switchButton.setForeground(ACCENT_COLOR);
        switchButton.setBorderPainted(false);
        switchButton.setContentAreaFilled(false);
        switchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        switchButton.addActionListener(e -> cardLayout.show(cardPanel, "REGISTER"));
        
        switchPanel.add(switchLabel);
        switchPanel.add(switchButton);
        
        panel.add(switchPanel);
        
        return panel;
    }
    
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BG);
        panel.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(BakeryTheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(40, 50, 40, 50)
        ));
        panel.setPreferredSize(new Dimension(500, 650));
        
        // Title
        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        
        // First Name
        panel.add(createFieldLabel("First Name"));
        regFirstNameField = createTextField();
        panel.add(regFirstNameField);
        panel.add(Box.createVerticalStrut(15));
        
        // Last Name
        panel.add(createFieldLabel("Last Name"));
        regLastNameField = createTextField();
        panel.add(regLastNameField);
        panel.add(Box.createVerticalStrut(15));
        
        // Email
        panel.add(createFieldLabel("Email Address"));
        regEmailField = createTextField();
        panel.add(regEmailField);
        panel.add(Box.createVerticalStrut(15));
        
        // Phone
        panel.add(createFieldLabel("Phone Number (09XXXXXXXXX)"));
        regPhoneField = createTextField();
        panel.add(regPhoneField);
        panel.add(Box.createVerticalStrut(15));
        
        // Password
        panel.add(createFieldLabel("Password (min. 6 characters)"));
        regPasswordField = createPasswordField();
        panel.add(regPasswordField);
        panel.add(Box.createVerticalStrut(15));
        
        // Confirm Password
        panel.add(createFieldLabel("Confirm Password"));
        regConfirmPasswordField = createPasswordField();
        panel.add(regConfirmPasswordField);
        panel.add(Box.createVerticalStrut(25));
        
        // Register Button
        JButton registerButton = createStyledButton("Create Account", ACCENT_COLOR);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.addActionListener(e -> handleRegister());
        
        panel.add(registerButton);
        panel.add(Box.createVerticalStrut(15));
        
        // Switch to Login
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        switchPanel.setOpaque(false);
        switchPanel.setMaximumSize(new Dimension(400, 40));
        
        JLabel switchLabel = new JLabel("Already have an account? ");
        switchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JButton switchButton = new JButton("Login Here");
        switchButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        switchButton.setForeground(PRIMARY_COLOR);
        switchButton.setBorderPainted(false);
        switchButton.setContentAreaFilled(false);
        switchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        switchButton.addActionListener(e -> cardLayout.show(cardPanel, "LOGIN"));
        
        switchPanel.add(switchLabel);
        switchPanel.add(switchButton);
        
        panel.add(switchPanel);
        
        return panel;
    }
    
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField(25);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(400, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BakeryTheme.BORDER_LIGHT),
            new EmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }
    
    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField(25);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(400, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BakeryTheme.BORDER_LIGHT),
            new EmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(400, 45));
        button.setMaximumSize(new Dimension(400, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private void handleLogin() {
        String email = loginEmailField.getText().trim();
        String password = new String(loginPasswordField.getPassword());
        
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill in all fields", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        LoginResult result = customerController.login(email, password);
        
        if (result.success) {
            if (authListener != null) {
                authListener.onAuthSuccess(result.customer);
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                result.message, 
                "Login Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleRegister() {
        String firstName = regFirstNameField.getText().trim();
        String lastName = regLastNameField.getText().trim();
        String email = regEmailField.getText().trim();
        String phone = regPhoneField.getText().trim();
        String password = new String(regPasswordField.getPassword());
        String confirmPassword = new String(regConfirmPasswordField.getPassword());
        
        // Validate
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || 
            phone.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill in all fields", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, 
                "Passwords do not match", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        RegisterResult result = customerController.register(email, password, firstName, lastName, phone);
        
        if (result.success) {
            JOptionPane.showMessageDialog(this, 
                "Registration successful! You are now logged in.", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            if (authListener != null) {
                authListener.onAuthSuccess(result.customer);
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                result.message, 
                "Registration Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void setAuthSuccessListener(AuthSuccessListener listener) {
        this.authListener = listener;
    }
    
    public interface AuthSuccessListener {
        void onAuthSuccess(kiosk.model.Customer customer);
    }
}


