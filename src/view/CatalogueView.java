package view;

import model.*;
import controller.CatalogueController;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class CatalogueView extends JPanel {
    private ProductCatalog catalog;
    private ShoppingCart cart;
    private JPanel productsPanel;
    private JLabel cartCountLabel;
    private JLabel totalLabel;
    private String selectedCategory = "All";
    private MainView mainView;
    private CatalogueController controller;

    public CatalogueView(MainView mainView) {
        this.mainView = mainView;
        this.catalog = new ProductCatalog();
        this.cart = new ShoppingCart();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top Panel - Header and Category Filter
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Center Panel - Products Grid
        JScrollPane scrollPane = createProductsScrollPane();
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel - Cart Summary and Checkout
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        this.controller = new CatalogueController(this, cart);
        // Initialize products display
        displayProducts();
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(0xA9907E),
                        0, getHeight(), new Color(0x8B7355));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        // Title
        JLabel titleLabel = new JLabel("Pastry Catalogue");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        titleLabel.setForeground(Color.WHITE);

        // Category Filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setOpaque(false);

        JLabel categoryLabel = new JLabel("Category: ");
        categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 19));
        categoryLabel.setForeground(Color.WHITE);

        JComboBox<String> categoryCombo = new JComboBox<String>(catalog.getCategories().toArray(new String[0])) {
            @Override
            public void updateUI() {
                super.updateUI();
                setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
                    @Override
                    protected JButton createArrowButton() {
                        JButton button = new JButton("▼");
                        button.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                        button.setBackground(Color.WHITE);
                        button.setForeground(new Color(0xA9907E));
                        button.setBorder(BorderFactory.createEmptyBorder());
                        button.setFocusPainted(false);
                        return button;
                    }
                });
            }
        };
        categoryCombo.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        categoryCombo.setPreferredSize(new Dimension(200, 45));
        categoryCombo.setBackground(Color.WHITE);
        categoryCombo.setForeground(Color.BLACK);
        categoryCombo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        categoryCombo.setFocusable(false);
        categoryCombo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        categoryCombo.addActionListener(e -> {
            selectedCategory = (String) categoryCombo.getSelectedItem();
            displayProducts();
        });

        filterPanel.add(categoryLabel);
        filterPanel.add(categoryCombo);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(filterPanel, BorderLayout.EAST);

        return panel;
    }

    private JScrollPane createProductsScrollPane() {
        productsPanel = new JPanel(new GridLayout(0, 3, 25, 25));
        productsPanel.setBackground(Color.WHITE);
        productsPanel.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));

        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);

        return scrollPane;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(25, 40, 25, 40)));

        // Left side - Cart info
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(new Color(245, 245, 245));

        cartCountLabel = new JLabel("Cart: 0 items");
        cartCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 19));
        cartCountLabel.setForeground(Color.BLACK);

        leftPanel.add(cartCountLabel);

        // Center - Total
        totalLabel = new JLabel("Total: ₱0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        totalLabel.setForeground(Color.BLACK);
        totalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        totalLabel.setForeground(Color.BLACK);
        totalLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Right side - Buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setBackground(new Color(245, 245, 245));

        JButton viewCartBtn = new JButton("View Cart");
        styleButton(viewCartBtn, new Color(120, 120, 120));
        viewCartBtn.addActionListener(e -> controller.showCart());

        JButton checkoutBtn = new JButton("Checkout");
        styleButton(checkoutBtn, new Color(0xA9907E));
        checkoutBtn.addActionListener(e -> controller.checkout());

        JButton backBtn = new JButton("← Back");
        styleButton(backBtn, new Color(80, 80, 80));
        backBtn.addActionListener(e -> mainView.showPanel("home"));

        rightPanel.add(viewCartBtn);
        rightPanel.add(checkoutBtn);
        rightPanel.add(backBtn);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(totalLabel, BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    public void displayProducts() {
        productsPanel.removeAll();

        List<Product> products = selectedCategory.equals("All")
                ? catalog.getAllProducts()
                : catalog.getProductsByCategory(selectedCategory);

        for (Product product : products) {
            JPanel productCard = createProductCard(product);
            productsPanel.add(productCard);
        }

        productsPanel.revalidate();
        productsPanel.repaint();
    }

    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(0, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        // Product Image Preview
        JPanel imagePanel = createProductImage(product);

        // Product Info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(Color.BLACK);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel categoryLabel = new JLabel(product.getCategory());
        categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        categoryLabel.setForeground(new Color(0xA9907E));
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel("<html>" + product.getDescription() + "</html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setForeground(new Color(100, 100, 100));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel priceLabel = new JLabel("₱" + String.format("%.2f", product.getPrice()));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        priceLabel.setForeground(new Color(0xA9907E));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        infoPanel.add(categoryLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        infoPanel.add(descLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        infoPanel.add(priceLabel);

        // Add to Cart Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        buttonPanel.setBackground(Color.WHITE);

        JButton addButton = new JButton("Add to Cart") {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isRollover()) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(0xA9907E).brighter());
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                } else {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(0xA9907E));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                super.paintComponent(g);
            }
        };
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        addButton.setPreferredSize(new Dimension(220, 45));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setContentAreaFilled(false);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> controller.addToCart(product));

        buttonPanel.add(addButton);

        // Assemble card
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(infoPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        card.add(imagePanel, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createProductImage(Product product) {
        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Create gradient background based on category
                Color startColor, endColor;
                switch (product.getCategory()) {
                    case "Cakes":
                        startColor = new Color(255, 228, 225);
                        endColor = new Color(255, 192, 203);
                        break;
                    case "Cupcakes":
                        startColor = new Color(255, 240, 245);
                        endColor = new Color(255, 182, 193);
                        break;
                    case "Pastries":
                        startColor = new Color(255, 248, 220);
                        endColor = new Color(255, 228, 181);
                        break;
                    case "Cookies":
                        startColor = new Color(245, 222, 179);
                        endColor = new Color(210, 180, 140);
                        break;
                    case "Breads":
                        startColor = new Color(245, 245, 220);
                        endColor = new Color(222, 184, 135);
                        break;
                    default:
                        startColor = new Color(245, 245, 245);
                        endColor = new Color(220, 220, 220);
                }

                GradientPaint gradient = new GradientPaint(
                        0, 0, startColor,
                        0, getHeight(), endColor);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Draw product icon
                g2d.setColor(new Color(255, 255, 255, 180));
                String icon = getProductIcon(product.getCategory());
                g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(icon)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(icon, x, y);
            }
        };
        imagePanel.setPreferredSize(new Dimension(300, 170));
        imagePanel.setBackground(new Color(240, 240, 240));
        return imagePanel;
    }

    private String getProductIcon(String category) {
        switch (category) {
            case "Cakes":
                return "★";
            case "Cupcakes":
                return "◆";
            case "Pastries":
                return "●";
            case "Cookies":
                return "■";
            case "Breads":
                return "▲";
            default:
                return "◆";
        }
    }

    public void updateCartDisplay() {
        cartCountLabel.setText("Cart: " + cart.getItemCount() + " items");
        totalLabel.setText("Total: ₱" + String.format("%.2f", cart.getTotal()));
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(140, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder());

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }

    public ShoppingCart getCart() {
        return cart;
    }

    public Component getParentComponent() {
        return this;
    }
}
