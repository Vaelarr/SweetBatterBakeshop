package view;

import util.FlatLafUtil;
import util.IconUtil;
import javax.swing.*;
import java.awt.*;
import controller.AdminController;

public class MainView extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainView() {
        // Ensure FlatLaf is initialized before creating Swing components
        FlatLafUtil.setupLookAndFeel();
        setTitle("Sweet Batter Bakeshop - Transaction System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Set to fullscreen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        
        setResizable(true);
        setMinimumSize(new Dimension(1200, 800));

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add panels
        mainPanel.add(createHomePanel(), "home");
        mainPanel.add(new CatalogueView(this), "catalogue");

        add(mainPanel);
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Header with gradient effect
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
        headerPanel.setPreferredSize(new Dimension(1400, 160));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(35, 60, 35, 60));



    // FlatLaf SVG Icon example (uses a built-in FlatLaf icon, you can use your own SVGs as well)
    JLabel iconLabel = new JLabel(IconUtil.getIcon("cupcake", 48, 48));

    JLabel titleLabel = new JLabel("Sweet Batter Bakeshop");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 52));
    titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Freshly Baked Goodness Every Day");
        subtitleLabel.setFont(new Font("Segoe UI Light", Font.PLAIN, 22));
        subtitleLabel.setForeground(new Color(255, 255, 255, 230));


    JPanel titlePanel = new JPanel();
    titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
    titlePanel.setOpaque(false);
    // Add icon and text side by side
    titlePanel.add(iconLabel);
    JPanel textPanel = new JPanel();
    textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
    textPanel.setOpaque(false);
    titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    textPanel.add(titleLabel);
    textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    textPanel.add(subtitleLabel);
    titlePanel.add(Box.createRigidArea(new Dimension(20, 0)));
    titlePanel.add(textPanel);


        // Add close button for fullscreen mode with FlatLaf icon
    JButton closeButton = new JButton(IconUtil.getIcon("close", 32, 32));
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 24));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(0x8B7355));
        closeButton.setPreferredSize(new Dimension(60, 60));
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setToolTipText("Exit");
        closeButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION
            );
            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(closeButton, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Center - Main Menu
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(60, 50, 60, 50));

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(50, 80, 50, 80));

        JLabel welcomeLabel = new JLabel("Welcome to Our Bakeshop!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        welcomeLabel.setForeground(Color.BLACK);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel infoLabel = new JLabel("What would you like to do today?");
        infoLabel.setFont(new Font("Segoe UI Light", Font.PLAIN, 22));
        infoLabel.setForeground(new Color(100, 100, 100));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


        // Buttons with FlatLaf icons
        JButton browseBtn = createMenuButton("Browse Products", new Color(0xA9907E),
            IconUtil.getIcon("list", 28, 28));
        browseBtn.addActionListener(e -> showPanel("catalogue"));

        JButton aboutBtn = createMenuButton("About Us", new Color(100, 100, 100),
            IconUtil.getIcon("info", 28, 28));
        aboutBtn.addActionListener(e -> showAboutDialog());

        JButton adminBtn = createMenuButton("Admin Access", new Color(139, 69, 19),
            IconUtil.getIcon("user", 28, 28));
        adminBtn.addActionListener(e -> openAdminPanel());

        menuPanel.add(welcomeLabel);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        menuPanel.add(infoLabel);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 50)));
        menuPanel.add(browseBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        menuPanel.add(aboutBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        menuPanel.add(adminBtn);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        centerPanel.add(menuPanel);
        panel.add(centerPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(245, 245, 245));
        footerPanel.setPreferredSize(new Dimension(1400, 70));

        JLabel footerLabel = new JLabel("© 2025 Sweet Batter Bakeshop | Open Daily 7AM - 9PM");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        footerLabel.setForeground(new Color(100, 100, 100));
        footerPanel.add(footerLabel);

        panel.add(footerPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createMenuButton(String text, Color color, Icon icon) {
        JButton button = new JButton(text, icon) {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isRollover()) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(color.brighter());
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                } else {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(color);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        button.setPreferredSize(new Dimension(500, 75));
        button.setMaximumSize(new Dimension(500, 75));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(20);

        return button;
    }

    private void showAboutDialog() {
        String aboutText = 
            "🧁 SWEET BATTER BAKESHOP 🧁\n\n" +
            "Welcome to our premium bakery!\n\n" +
            "We specialize in:\n" +
            "• Freshly baked cakes and cupcakes\n" +
            "• Artisan pastries and croissants\n" +
            "• Homemade cookies\n" +
            "• Traditional breads\n\n" +
            "All our products are made fresh daily\n" +
            "using the finest ingredients.\n\n" +
            "Thank you for choosing us!\n\n" +
            "Version 2.0 (MVC) | 2025";

        JOptionPane.showMessageDialog(
            this,
            aboutText,
            "About Sweet Batter Bakeshop",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void openAdminPanel() {
        dispose(); // Close main view
        AdminController adminController = new AdminController();
        adminController.showLoginDialog();
    }

    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }
}
