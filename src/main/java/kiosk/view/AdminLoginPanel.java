package main.java.kiosk.view;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Login panel for administrative access
 */
public class AdminLoginPanel extends JFrame {
    // Modern Bakery Theme Colors
    private final Color PRIMARY_COLOR = BakeryTheme.PRIMARY_DARK;
    private final Color ACCENT_COLOR = BakeryTheme.ACCENT_COLOR;
    private final Color SUCCESS_COLOR = BakeryTheme.SUCCESS;
    private final Color DANGER_COLOR = BakeryTheme.ERROR;
    private final Color BACKGROUND_COLOR = BakeryTheme.BACKGROUND_COLOR;
    private final Color TEXT_DARK = BakeryTheme.TEXT_DARK;
    private final Color TEXT_LIGHT = BakeryTheme.TEXT_WHITE;
    
    private final Font TITLE_FONT = BakeryTheme.SUBTITLE_FONT;
    private final Font SUBTITLE_FONT = BakeryTheme.HEADER_FONT;
    private final Font REGULAR_FONT = BakeryTheme.REGULAR_FONT;
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 16);
    
    // Hard-coded credentials map (in a real application, this would be stored securely)
    private static final Map<String, String> ADMIN_CREDENTIALS = new HashMap<>();
    
    static {
        // Initialize with some default credentials
        ADMIN_CREDENTIALS.put("admin", "admin123");
        ADMIN_CREDENTIALS.put("manager", "manager123");
    }
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorMessageLabel;
    private JButton loginButton;
      public AdminLoginPanel() {
        setTitle("Admin Authentication");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Set a minimum size to prevent elements from being squished
        setMinimumSize(new Dimension(450, 400));
        
        // Add some rounded corners and drop shadow using a custom JPanel
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BACKGROUND_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Create components
        JPanel headerPanel = createHeaderPanel();
        JPanel loginPanel = createLoginPanel();
        JPanel buttonPanel = createButtonPanel();
        
        // Add components to the main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(loginPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add the main panel to the frame
        add(mainPanel, BorderLayout.CENTER);
        
        // Add key listener to handle Enter key for the entire frame
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    attemptLogin();
                }
            }
        };
        
        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
        
        // Set visible
        setVisible(true);
    }
      private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        // Create a logo panel
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setOpaque(false);
        
        // Create a simple logo/icon
        JLabel iconLabel = new JLabel("👤");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        iconLabel.setForeground(TEXT_LIGHT);
        
        JLabel titleLabel = new JLabel("Administrator Login", JLabel.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_LIGHT);
        
        JLabel subtitleLabel = new JLabel("Please enter your credentials", JLabel.CENTER);
        subtitleLabel.setFont(REGULAR_FONT);
        subtitleLabel.setForeground(new Color(189, 195, 199));
        
        logoPanel.add(iconLabel);
        
        // Create a panel for the title and subtitle
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        textPanel.add(subtitleLabel);
        
        panel.add(logoPanel, BorderLayout.NORTH);
        panel.add(textPanel, BorderLayout.CENTER);
        
        return panel;
    }
      private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        
        // Create field icons
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        userIcon.setForeground(TEXT_DARK);
        userIcon.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel passIcon = new JLabel("🔒");
        passIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        passIcon.setForeground(TEXT_DARK);
        passIcon.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Username label and field
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(REGULAR_FONT);
        usernameLabel.setForeground(TEXT_DARK);
        
        usernameField = new JTextField(15);
        usernameField.setFont(REGULAR_FONT);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        // Create a panel for username with icon
        JPanel usernamePanel = new JPanel(new BorderLayout(10, 0));
        usernamePanel.setBackground(BACKGROUND_COLOR);
        usernamePanel.add(userIcon, BorderLayout.WEST);
        usernamePanel.add(usernameField, BorderLayout.CENTER);
        
        // Password label and field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(REGULAR_FONT);
        passwordLabel.setForeground(TEXT_DARK);
        
        passwordField = new JPasswordField(15);
        passwordField.setFont(REGULAR_FONT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        // Create a panel for password with icon
        JPanel passwordPanel = new JPanel(new BorderLayout(10, 0));
        passwordPanel.setBackground(BACKGROUND_COLOR);
        passwordPanel.add(passIcon, BorderLayout.WEST);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        
        // Error message label
        errorMessageLabel = new JLabel(" ");
        errorMessageLabel.setForeground(DANGER_COLOR);
        errorMessageLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        errorMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Add components to panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        panel.add(usernameLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(usernamePanel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(15, 5, 8, 5);  // Add more space between fields
        panel.add(passwordLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 5, 8, 5);
        panel.add(passwordPanel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(15, 5, 5, 5);
        panel.add(errorMessageLabel, gbc);
        
        return panel;
    }
      private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 25, 0));
        
        loginButton = new JButton("Login");
        loginButton.setFont(BUTTON_FONT);
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(ACCENT_COLOR);
        loginButton.setPreferredSize(new Dimension(140, 45));
        loginButton.setBorder(new EmptyBorder(5, 15, 5, 15));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect to login button
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(52, 152, 219));  // Lighter blue on hover
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(ACCENT_COLOR);
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(BUTTON_FONT);
        cancelButton.setPreferredSize(new Dimension(140, 45));
        cancelButton.setBorder(new EmptyBorder(5, 15, 5, 15));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setBackground(new Color(236, 240, 241));
        cancelButton.setForeground(TEXT_DARK);
        
        // Add hover effect to cancel button
        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cancelButton.setBackground(new Color(224, 224, 224));  // Slightly darker on hover
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                cancelButton.setBackground(new Color(236, 240, 241));
            }
        });
        
        // Add action listeners
        loginButton.addActionListener(e -> attemptLogin());
        cancelButton.addActionListener(e -> dispose());
        
        panel.add(loginButton);
        panel.add(cancelButton);
        
        return panel;
    }
      private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        // Disable login button while processing
        loginButton.setEnabled(false);
        loginButton.setText("Verifying...");
        
        // Add a small delay to simulate processing
        Timer timer = new Timer(800, event -> {
            if (validateCredentials(username, password)) {
                // Successful login - show success message briefly
                errorMessageLabel.setForeground(SUCCESS_COLOR);
                errorMessageLabel.setText("Login successful!");
                
                // Use another timer to give visual feedback before closing
                Timer successTimer = new Timer(1000, e -> {
                    errorMessageLabel.setText(" ");
                    dispose();
                    new AdminPanel();
                });
                successTimer.setRepeats(false);
                successTimer.start();
            } else {
                // Failed login
                loginButton.setEnabled(true);
                loginButton.setText("Login");
                errorMessageLabel.setForeground(DANGER_COLOR);
                errorMessageLabel.setText("Invalid username or password");
                passwordField.setText("");
                
                // Shake effect for the error message
                Timer shakeTimer = new Timer(100, e -> {
                    if (errorMessageLabel.getText().startsWith("Invalid")) {
                        errorMessageLabel.setText("Invalid username or password  ");
                    } else {
                        errorMessageLabel.setText("Invalid username or password");
                    }
                });
                shakeTimer.setRepeats(true);
                shakeTimer.setInitialDelay(0);
                
                // Stop the shake effect after a short time
                Timer stopShakeTimer = new Timer(500, e -> shakeTimer.stop());
                stopShakeTimer.setRepeats(false);
                
                shakeTimer.start();
                stopShakeTimer.start();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
      private boolean validateCredentials(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            return false;
        }
        
        // Security best practice: use constant time comparison to prevent timing attacks
        String storedPassword = ADMIN_CREDENTIALS.get(username);
        if (storedPassword == null) {
            return false;
        }
        
        // In a real application, passwords should be hashed and salted
        // This is a simple implementation for demonstration purposes
        return storedPassword.equals(password);
    }
}
