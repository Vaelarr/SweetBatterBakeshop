package view;

import util.FlatLafUtil;
import util.IconUtil;
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
    private Color accent = new Color(0xA9907E);

    public CatalogueView(MainView mainView) {
        // ensure FlatLaf is initialized for consistent styling
        FlatLafUtil.setupLookAndFeel();
        this.mainView = mainView;
        this.catalog = new ProductCatalog();
        this.cart = new ShoppingCart();

        setLayout(new BorderLayout(0, 0));
        setBackground(Color.WHITE);

        add(createTopPanel(), BorderLayout.NORTH);
        add(createProductsScrollPane(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        this.controller = new CatalogueController(this, cart);
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
                        0, 0, new Color(0xC7B7A3),
                        0, getHeight(), accent);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(30, 45, 30, 45));

        JLabel title = new JLabel("🍰 Pastry Catalogue");
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 38));
        title.setForeground(Color.WHITE);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        filterPanel.setOpaque(false);

        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        categoryLabel.setForeground(Color.WHITE);

        JComboBox<String> combo = new JComboBox<>(catalog.getCategories().toArray(new String[0]));
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        combo.setPreferredSize(new Dimension(220, 45));
        combo.setBackground(Color.WHITE);
        combo.setForeground(Color.DARK_GRAY);
        combo.addActionListener(e -> {
            selectedCategory = (String) combo.getSelectedItem();
            displayProducts();
        });

        filterPanel.add(categoryLabel);
        filterPanel.add(combo);

        panel.add(title, BorderLayout.WEST);
        panel.add(filterPanel, BorderLayout.EAST);

        return panel;
    }

    private JScrollPane createProductsScrollPane() {
        productsPanel = new JPanel(new GridBagLayout());
        productsPanel.setBackground(Color.WHITE);
        productsPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
                new EmptyBorder(20, 40, 20, 40)));

        cartCountLabel = new JLabel("Cart: 0 items");
        cartCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        totalLabel = new JLabel("Total: ₱0.00", SwingConstants.CENTER);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        btnPanel.setBackground(Color.WHITE);

        JButton backBtn = createStyledButton("← Back", new Color(90, 90, 90), null);
        backBtn.addActionListener(e -> mainView.showPanel("home"));

    JButton viewCartBtn = createStyledButton("View Cart", new Color(110, 110, 110),
        IconUtil.getIcon("cart", 18, 18));
        viewCartBtn.addActionListener(e -> controller.showCart());

    JButton checkoutBtn = createStyledButton("Checkout", accent,
        IconUtil.getIcon("check", 18, 18));
        checkoutBtn.addActionListener(e -> controller.checkout());

        btnPanel.add(viewCartBtn);
        btnPanel.add(checkoutBtn);
        btnPanel.add(backBtn);

        panel.add(cartCountLabel, BorderLayout.WEST);
        panel.add(totalLabel, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.EAST);
        return panel;
    }

    public void displayProducts() {
        productsPanel.removeAll();

        List<Product> products = selectedCategory.equals("All")
                ? catalog.getAllProducts()
                : catalog.getProductsByCategory(selectedCategory);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0;
        gbc.gridy = 0;

        int cols = 3;
        for (Product product : products) {
            JPanel card = createProductCard(product);
            productsPanel.add(card, gbc);
            gbc.gridx++;
            if (gbc.gridx >= cols) {
                gbc.gridx = 0;
                gbc.gridy++;
            }
        }

        productsPanel.revalidate();
        productsPanel.repaint();
    }

    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(280, 360));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(10, 10, 10, 10)));

    JLabel imgLabel = new JLabel(IconUtil.getIcon("coffee", 60, 60), SwingConstants.CENTER);
        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.setPreferredSize(new Dimension(280, 160));
        imgPanel.setBackground(new Color(0xF8F3EF));
        imgPanel.add(imgLabel, BorderLayout.CENTER);

        JLabel name = new JLabel(product.getName());
        name.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));

        JLabel category = new JLabel(product.getCategory());
        category.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        category.setForeground(accent);

        JLabel desc = new JLabel("<html><p style='width:230px;'>" + product.getDescription() + "</p></html>");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        desc.setForeground(new Color(100, 100, 100));

        JLabel price = new JLabel("₱" + String.format("%.2f", product.getPrice()));
        price.setFont(new Font("Segoe UI", Font.BOLD, 22));
        price.setForeground(accent);

    JButton addBtn = createStyledButton("Add to Cart", accent,
        IconUtil.getIcon("add", 18, 18));
        addBtn.addActionListener(e -> controller.addToCart(product));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Color.WHITE);
        info.add(name);
        info.add(Box.createVerticalStrut(4));
        info.add(category);
        info.add(Box.createVerticalStrut(8));
        info.add(desc);
        info.add(Box.createVerticalStrut(10));
        info.add(price);
        info.add(Box.createVerticalStrut(15));
        info.add(addBtn);

        card.add(imgPanel, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);
        return card;
    }

    private JButton createStyledButton(String text, Color bg, Icon icon) {
        JButton btn = new JButton(text, icon);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 45));
        btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        btn.setIconTextGap(8);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.brighter()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    public void updateCartDisplay() {
        cartCountLabel.setText("Cart: " + cart.getItemCount() + " items");
        totalLabel.setText("Total: ₱" + String.format("%.2f", cart.getTotal()));
    }

    public ShoppingCart getCart() {
        return cart;
    }

    public Component getParentComponent() {
        return this;
    }
}

/**
 * Simple shadow border class for modern UI look.
 */
class DropShadowBorder extends AbstractBorder {
    private final Color color;
    private final int size;
    private final float opacity;

    public DropShadowBorder(Color color, int size, float opacity, int distance,
            boolean top, boolean left, boolean bottom, boolean right) {
        this.color = color;
        this.size = size;
        this.opacity = opacity;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (255 * opacity)));
        g2.fillRoundRect(x + size, y + size, w - size, h - size, 20, 20);
        g2.dispose();
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
}
