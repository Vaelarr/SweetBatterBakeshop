package kiosk.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import kiosk.util.CartManager;
import kiosk.util.HelpRequestManager;

public class KioskMainPage extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private CakesPage cakesPage;
    private PastriesPage pastriesPage;
    private BreadsPage breadsPage;
    private BeveragesPage beveragesPage;
    private CartPage cartPage;
    
    // Modern Bakery Theme Colors - imported from BakeryTheme
    private final Color PRIMARY_COLOR = BakeryTheme.PRIMARY_COLOR;
    private final Color ACCENT_COLOR = BakeryTheme.ACCENT_COLOR;
    private final Color BACKGROUND_COLOR = BakeryTheme.BACKGROUND_COLOR;
    private final Color CARD_COLOR = BakeryTheme.CARD_COLOR;
    private final Color TEXT_DARK = BakeryTheme.TEXT_DARK;
    private final Font TITLE_FONT = BakeryTheme.TITLE_FONT;
    private final Font SUBTITLE_FONT = BakeryTheme.SUBTITLE_FONT;
    private final Font REGULAR_FONT = BakeryTheme.REGULAR_FONT;
    private final Font BUTTON_FONT = BakeryTheme.BUTTON_FONT;

    public KioskMainPage() {
        this(true);
    }
    
    public KioskMainPage(boolean showImmediately) {
        setTitle("SweetBatter Bakeshop - Kiosk System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Set up keyboard shortcut for admin (Ctrl+Shift+A)
        setupAdminKeyboardShortcut();

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        add(contentPanel);

        // Create main panel first
        JPanel mainPanel = createMainPanel();
        contentPanel.add(mainPanel, "main");

        // Initialize all pages with this instance
        cartPage = new CartPage(this);
        cakesPage = new CakesPage(this);
        pastriesPage = new PastriesPage(this);
        breadsPage = new BreadsPage(this);
        beveragesPage = new BeveragesPage(this);

        // Add pages to card layout
        contentPanel.add(cakesPage, "cakes");
        contentPanel.add(pastriesPage, "pastries");
        contentPanel.add(breadsPage, "breads");
        contentPanel.add(beveragesPage, "beverages");
        contentPanel.add(cartPage, "cart");

        showMainPage();
        
        if (showImmediately) {
            setVisible(true);
        }
    }
    
    public void showMainPage() {
        cardLayout.show(contentPanel, "main");
    }

    public void showCakesPage() {
        cardLayout.show(contentPanel, "cakes");
    }

    public void showPastriesPage() {
        cardLayout.show(contentPanel, "pastries");
    }

    public void showBreadsPage() {
        cardLayout.show(contentPanel, "breads");
    }

    public void showBeveragesPage() {
        cardLayout.show(contentPanel, "beverages");
    }

    public void showCartPage() {
        cardLayout.show(contentPanel, "cart");
        cartPage.refreshCart(); // Ensure cart is up-to-date when shown
        updateAllCartCounters();
    }

    /**
     * Sets up the keyboard shortcut (Ctrl+Shift+A) to open the admin panel
     */
    private void setupAdminKeyboardShortcut() {
        KeyStroke adminKeyStroke = KeyStroke.getKeyStroke(
            java.awt.event.KeyEvent.VK_A, 
            java.awt.event.InputEvent.CTRL_DOWN_MASK | java.awt.event.InputEvent.SHIFT_DOWN_MASK
        );
        
        getRootPane().registerKeyboardAction(
            e -> {
                // Dispose of the main page first to prevent hanging
                dispose();
                // Then show the admin login panel
                SwingUtilities.invokeLater(() -> {
                    AdminLoginPanel adminPanel = new AdminLoginPanel();
                    adminPanel.setVisible(true);
                });
            },
            adminKeyStroke,
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    // Call this after checkout or cart update to sync all cart counters
    public void updateAllCartCounters() {
        cartPage.updateCartCount();
        if (cakesPage != null) cakesPage.updateCartCount();
        if (pastriesPage != null) pastriesPage.updateCartCount();
        if (breadsPage != null) breadsPage.updateCartCount();
        if (beveragesPage != null) beveragesPage.updateCartCount();
        // If you have a cart count label in main page, update it here as well
        // Example: updateMainCartCount();
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        // Top Panel with cart and help buttons
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Title panel with welcome message
        JPanel titlePanel = createTitlePanel();
        
        // Categories Panel - larger for kiosk
        JPanel categoriesPanel = createCategoriesPanel();
        
        // Create a main content panel to hold title and categories
        JPanel contentPanel = new JPanel(new BorderLayout(0, 30));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));
        contentPanel.add(titlePanel, BorderLayout.NORTH);
        contentPanel.add(categoriesPanel, BorderLayout.CENTER);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PRIMARY_COLOR);
        topPanel.setPreferredSize(new Dimension(0, 80)); // Taller for touch
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Left section with store logo and name
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);
        
        // Load and display the store logo
        JLabel logoLabel = new JLabel();
        try {
            ImageIcon originalIcon = new ImageIcon("lib/images/store-logo.png");
            Image scaledImage = originalIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            // Fallback to emoji if logo can't be loaded
            logoLabel.setText("🧁");
            logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        }
        
        JLabel storeLabel = new JLabel("SweetBatter Bakeshop");
        storeLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        storeLabel.setForeground(Color.WHITE);
        storeLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0)); // Vertical centering
        
        leftPanel.add(logoLabel);
        leftPanel.add(storeLabel);
        
        // Right section with help and cart - using GridBagLayout for better alignment
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 10, 0, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Create text-based buttons instead of icon buttons
        JButton helpButton = new JButton("Help");
        helpButton.setToolTipText("Ask for assistance");
        helpButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        helpButton.setForeground(Color.WHITE);
        helpButton.setBackground(PRIMARY_COLOR);
        helpButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1, true),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        helpButton.setFocusPainted(false);
        helpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        helpButton.addActionListener(e -> showHelpRequestDialog());
        
        JPanel cartPanel = new JPanel(new BorderLayout(5, 0));
        cartPanel.setOpaque(false);
        
        JButton cartButton = new JButton("Cart");
        cartButton.setToolTipText("View Cart");
        cartButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cartButton.setForeground(Color.WHITE);
        cartButton.setBackground(PRIMARY_COLOR);
        cartButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1, true),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        cartButton.setFocusPainted(false);
        cartButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel cartCountLabel = new JLabel();
        cartCountLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        cartCountLabel.setOpaque(true);
        cartCountLabel.setBackground(new Color(231, 76, 60));
        cartCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cartCountLabel.setPreferredSize(new Dimension(28, 28));
        cartCountLabel.setBorder(new LineBorder(Color.WHITE, 2, true));
        
        // Update cart count from cart manager
        int count = CartManager.getTotalItems();
        cartCountLabel.setText(String.valueOf(count));
        cartCountLabel.setVisible(count > 0);
        
        cartPanel.add(cartButton, BorderLayout.WEST);
        cartPanel.add(cartCountLabel, BorderLayout.EAST);
        
        // Make the cart button open the cart directly
        cartButton.addActionListener(e -> showCartPage());
        
        // Add close button
        JButton closeButton = new JButton("Exit");
        closeButton.setToolTipText("Close Application");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(PRIMARY_COLOR);
        closeButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1, true),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> System.exit(0));
        
        // Add components to right panel with GridBagLayout for proper alignment
        rightPanel.add(helpButton, gbc);
        
        gbc.gridx++;
        rightPanel.add(cartPanel, gbc);
        
        gbc.gridx++;
        rightPanel.add(closeButton, gbc);
        
        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);
        return topPanel;
    }
    
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create multi-stop gradient background for depth
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(232, 146, 124),  // Warm coral
                    0, getHeight(), new Color(212, 137, 109)  // Deeper terracotta
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Add subtle overlay pattern for texture
                g2d.setColor(new Color(255, 255, 255, 15));
                for (int i = 0; i < getHeight(); i += 4) {
                    g2d.drawLine(0, i, getWidth(), i);
                }
            }
        };
        titlePanel.setBorder(BorderFactory.createEmptyBorder(40, 45, 40, 45));
        titlePanel.setPreferredSize(new Dimension(0, 180));
        
        JLabel welcomeLabel = new JLabel("Welcome to SweetBatter Bakeshop", SwingConstants.LEFT);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 44));
        welcomeLabel.setForeground(Color.WHITE);
        
        // Add subtle shadow effect to text
        JLabel subtitleLabel = new JLabel("✨ Handcrafted with love - Select a category to begin your order", SwingConstants.LEFT);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        subtitleLabel.setForeground(new Color(255, 255, 255, 245));
        
        JPanel labelPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        labelPanel.setOpaque(false);
        labelPanel.add(welcomeLabel);
        labelPanel.add(subtitleLabel);
        
        titlePanel.add(labelPanel, BorderLayout.CENTER);
        return titlePanel;
    }
    
    private JPanel createCategoriesPanel() {
        JPanel categoriesPanel = new JPanel(new GridLayout(2, 2, 30, 30));
        categoriesPanel.setOpaque(false);
        categoriesPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Breads & Rolls card - Golden wheat tones
        JPanel breadsCard = createCategoryCard(
            "Breads & Rolls", 
            "🥖", 
            new Color(244, 164, 96),  // Sandy brown
            new Color(210, 140, 70),   // Darker for hover
            e -> showBreadsPage()
        );
        
        // Pastries & Desserts card - Delicate pink
        JPanel pastriesCard = createCategoryCard(
            "Pastries & Desserts", 
            "🥐", 
            new Color(255, 182, 193),  // Light pink
            new Color(255, 160, 175),   // Darker for hover
            e -> showPastriesPage()
        );
        
        // Cakes & Special Occasions card - Vibrant celebration pink
        JPanel cakesCard = createCategoryCard(
            "Cakes & Special Occasions", 
            "🎂", 
            new Color(255, 105, 180),  // Hot pink
            new Color(230, 90, 160),    // Darker for hover
            e -> showCakesPage()
        );
        
        // Beverages & Extras card - Rich coffee brown
        JPanel beveragesCard = createCategoryCard(
            "Beverages & Extras", 
            "☕", 
            new Color(139, 115, 85),   // Coffee brown
            new Color(115, 95, 70),     // Darker for hover
            e -> showBeveragesPage()
        );
        
        categoriesPanel.add(breadsCard);
        categoriesPanel.add(pastriesCard);
        categoriesPanel.add(cakesCard);
        categoriesPanel.add(beveragesCard);
        
        return categoriesPanel;
    }
    
    private JPanel createCategoryCard(String title, String emoji, Color accentColor, Color hoverColor, java.awt.event.ActionListener action) {
        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add subtle shadow effect
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 3, new Color(0, 0, 0, 30)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            )
        ));
        
        // Icon panel with emoji and gradient background
        JPanel iconPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create radial-like gradient for modern look
                GradientPaint gradient = new GradientPaint(
                    getWidth() / 2, 0, CARD_COLOR,
                    getWidth() / 2, getHeight(), accentColor.brighter()
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Add decorative circles for visual interest
                g2d.setColor(new Color(255, 255, 255, 40));
                g2d.fillOval(getWidth() - 80, -40, 120, 120);
                g2d.fillOval(-30, getHeight() - 60, 100, 100);
            }
        };
        iconPanel.setPreferredSize(new Dimension(0, 240));
        iconPanel.setBorder(BorderFactory.createEmptyBorder(35, 20, 35, 20));
        
        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 110));
        emojiLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconPanel.add(emojiLabel);
        
        // Title panel with bold color
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(accentColor);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(22, 18, 22, 18));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // Assemble card
        card.add(iconPanel, BorderLayout.CENTER);
        card.add(titlePanel, BorderLayout.SOUTH);
        
        // Add hover and click effects with smooth transitions
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            private Timer hoverTimer;
            
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // Add click animation
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(hoverColor, 3, true),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
                ));
                
                Timer clickTimer = new Timer(100, evt -> {
                    action.actionPerformed(null);
                });
                clickTimer.setRepeats(false);
                clickTimer.start();
            }
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(accentColor, 4, true),
                    BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 5, 5, new Color(0, 0, 0, 50)),
                        BorderFactory.createEmptyBorder(3, 3, 3, 3)
                    )
                ));
                titlePanel.setBackground(hoverColor);
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                    BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 3, 3, new Color(0, 0, 0, 30)),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                    )
                ));
                titlePanel.setBackground(accentColor);
            }
        });
        
        return card;
    }
    
    /**
     * Shows a dialog for the customer to request assistance
     */
    private void showHelpRequestDialog() {
        JDialog helpDialog = new JDialog(this, "Request Assistance", true);
        helpDialog.setSize(450, 350);
        helpDialog.setLocationRelativeTo(this);
        helpDialog.setLayout(new BorderLayout());
        
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(BACKGROUND_COLOR);
        
        // Header
        JLabel headerLabel = new JLabel("How can we help you?");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(PRIMARY_COLOR);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 1, 0, 15));
        formPanel.setBackground(BACKGROUND_COLOR);
        
        // Issue type selection
        JPanel issuePanel = new JPanel(new BorderLayout(0, 5));
        issuePanel.setBackground(BACKGROUND_COLOR);
        JLabel issueLabel = new JLabel("Type of Assistance:");
        issueLabel.setFont(REGULAR_FONT);
        String[] issueTypes = {
            "General Assistance",
            "Product Information",
            "Technical Problem",
            "Payment Issue",
            "Other"
        };
        JComboBox<String> issueComboBox = new JComboBox<>(issueTypes);
        issueComboBox.setFont(REGULAR_FONT);
        issuePanel.add(issueLabel, BorderLayout.NORTH);
        issuePanel.add(issueComboBox, BorderLayout.CENTER);
        
        // Details field
        JPanel detailsPanel = new JPanel(new BorderLayout(0, 5));
        detailsPanel.setBackground(BACKGROUND_COLOR);
        JLabel detailsLabel = new JLabel("Additional Details (optional):");
        detailsLabel.setFont(REGULAR_FONT);
        JTextArea detailsArea = new JTextArea(4, 20);
        detailsArea.setFont(REGULAR_FONT);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane detailsScrollPane = new JScrollPane(detailsArea);
        detailsPanel.add(detailsLabel, BorderLayout.NORTH);
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        // Urgency level
        JPanel urgencyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        urgencyPanel.setBackground(BACKGROUND_COLOR);
        JLabel urgencyLabel = new JLabel("Is this urgent?");
        urgencyLabel.setFont(REGULAR_FONT);
        JCheckBox urgentCheckBox = new JCheckBox("Yes, I need immediate assistance");
        urgentCheckBox.setFont(REGULAR_FONT);
        urgentCheckBox.setBackground(BACKGROUND_COLOR);
        urgencyPanel.add(urgencyLabel);
        urgencyPanel.add(urgentCheckBox);
        
        // Add all form components
        formPanel.add(issuePanel);
        formPanel.add(detailsPanel);
        formPanel.add(urgencyPanel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(REGULAR_FONT);
        cancelButton.addActionListener(e -> helpDialog.dispose());
        
        JButton submitButton = new JButton("Request Help");
        submitButton.setFont(BUTTON_FONT);
        submitButton.setBackground(ACCENT_COLOR);
        submitButton.setForeground(Color.WHITE);
        submitButton.addActionListener(e -> {
            String issueType = (String) issueComboBox.getSelectedItem();
            String details = detailsArea.getText().trim();
            boolean isUrgent = urgentCheckBox.isSelected();
             
            // Format details with urgency info
            if (isUrgent) {
                details = "[URGENT] " + details;
            }
            
            // Submit help request to the manager
            HelpRequestManager.getInstance().submitRequest(
                "Main Menu", 
                issueType, 
                details
            );
            
            // Confirmation message
            JOptionPane.showMessageDialog(helpDialog,
                "Your help request has been submitted.\nA staff member will assist you shortly.",
                "Help Request Submitted",
                JOptionPane.INFORMATION_MESSAGE);
            
            helpDialog.dispose();
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);
        
        // Assemble dialog
        contentPanel.add(headerLabel, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);
        
        helpDialog.add(contentPanel, BorderLayout.CENTER);
        helpDialog.add(buttonPanel, BorderLayout.SOUTH);
        helpDialog.setVisible(true);
    }

    /**
     * Verifies age before showing pastries page (legacy method - can be removed if not needed)
     */
    private void verifyAgeBeforeShowingTobaccoPage() {
        int response = JOptionPane.showConfirmDialog(
            this,
            "You must be 18 or older to view this category.\nAre you 18 or older?",
            "Age Verification",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (response == JOptionPane.YES_OPTION) {
            showPastriesPage();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "You must be 18 or older to view tobacco and alcohol products.",
                "Age Verification Failed",
                JOptionPane.WARNING_MESSAGE
            );
            // Return to main page or stay on current page
            showMainPage();
        }
    }

    private static void showPage(JFrame currentFrame, JPanel newPanel) {
        currentFrame.getContentPane().removeAll();
        currentFrame.add(newPanel);
        currentFrame.revalidate();
        currentFrame.repaint();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(KioskMainPage::new);
    }
}




