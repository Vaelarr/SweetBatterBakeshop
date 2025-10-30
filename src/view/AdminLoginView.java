package view;

import util.FlatLafUtil;
import javax.swing.*;
import java.awt.*;

public class AdminLoginView extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private boolean loginSuccessful = false;
    private String enteredUsername;
    private String enteredPassword;

    public AdminLoginView(JFrame parent) {
        super(parent, "Admin Login", true);
        // ensure FlatLaf is initialized (safe to call multiple times)
        FlatLafUtil.setupLookAndFeel();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setUndecorated(true); // Remove window decorations for fullscreen look
        
        // Get screen size and set to fullscreen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize);
        setLocationRelativeTo(null);

        // Header with gradient
        JPanel headerPanel = new JPanel() {
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
        headerPanel.setPreferredSize(new Dimension(screenSize.width, 150));
        headerPanel.setLayout(new GridBagLayout());
        
        JLabel headerLabel = new JLabel("🔐 Admin Access");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        headerLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Management Dashboard");
        subtitleLabel.setFont(new Font("Segoe UI Light", Font.PLAIN, 20));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        
        JPanel headerContent = new JPanel();
        headerContent.setOpaque(false);
        headerContent.setLayout(new BoxLayout(headerContent, BoxLayout.Y_AXIS));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerContent.add(headerLabel);
        headerContent.add(Box.createRigidArea(new Dimension(0, 5)));
        headerContent.add(subtitleLabel);
        
        headerPanel.add(headerContent);
        add(headerPanel, BorderLayout.NORTH);

        // Center panel with form - centered in fullscreen
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(245, 245, 245));
        
        // Create the login form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(60, 80, 60, 80)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel userLabel = new JLabel("👤 Username");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        userLabel.setForeground(new Color(80, 80, 80));
        formPanel.add(userLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        usernameField = new JTextField(25);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        usernameField.setPreferredSize(new Dimension(400, 50));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.insets = new Insets(30, 10, 10, 10);
        JLabel passLabel = new JLabel("🔒 Password");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        passLabel.setForeground(new Color(80, 80, 80));
        formPanel.add(passLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10);
        passwordField = new JPasswordField(25);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        passwordField.setPreferredSize(new Dimension(400, 50));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        formPanel.add(passwordField, gbc);
        
        // Button panel within form
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(40, 10, 10, 10);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        loginButton = createStyledButton("Login", new Color(0xA9907E), Color.WHITE);
        loginButton.setPreferredSize(new Dimension(180, 50));
        loginButton.addActionListener(e -> handleLogin());

        cancelButton = createStyledButton("Cancel", new Color(120, 120, 120), Color.WHITE);
        cancelButton.setPreferredSize(new Dimension(180, 50));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        formPanel.add(buttonPanel, gbc);
        
        // Center the form panel in the screen
        centerPanel.add(formPanel);
        add(centerPanel, BorderLayout.CENTER);

        // Enter key to login
        passwordField.addActionListener(e -> handleLogin());
        
        // Focus on username field
        SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
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
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void handleLogin() {
        enteredUsername = usernameField.getText().trim();
        enteredPassword = new String(passwordField.getPassword());

        if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter both username and password.",
                "Input Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        loginSuccessful = true;
        dispose();
    }

    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }

    public String getEnteredUsername() {
        return enteredUsername;
    }

    public String getEnteredPassword() {
        return enteredPassword;
    }
}
