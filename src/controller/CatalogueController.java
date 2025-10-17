package controller;

import model.*;
import view.CatalogueView;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Optional;

public class CatalogueController {
    private CatalogueView view;
    private ShoppingCart cart;
    private SerializationUtil<ShoppingCart> cartSerializer;
    private static final String CART_FILE = "shopping_cart.ser";

    public CatalogueController(CatalogueView view, ShoppingCart cart) {
        this.view = view;
        this.cart = cart;
        this.cartSerializer = new SerializationUtil<>();
        loadCart(); // Load saved cart on initialization
    }
    
    /**
     * Loads the shopping cart from file if it exists.
     * Uses generics and Optional for type-safe deserialization.
     */
    private void loadCart() {
        Optional<ShoppingCart> loadedCart = cartSerializer.load(CART_FILE);
        if (loadedCart.isPresent()) {
            // Transfer items from loaded cart to current cart
            ShoppingCart saved = loadedCart.get();
            for (int i = 0; i < saved.getItems().size(); i++) {
                cart.getItems().add(saved.getItems().get(i));
            }
            System.out.println("Cart loaded successfully with " + cart.getItems().size() + " items");
        }
    }
    
    /**
     * Saves the current shopping cart to file.
     * Automatically called after cart modifications.
     */
    private void saveCart() {
        if (cartSerializer.save(cart, CART_FILE)) {
            System.out.println("Cart saved successfully");
        } else {
            System.err.println("Failed to save cart");
        }
    }
    
    /**
     * Clears the saved cart file.
     * Called after successful checkout.
     */
    private void clearSavedCart() {
        cartSerializer.delete(CART_FILE);
    }

    public void addToCart(Product product) {
        // Create custom modern input dialog with customization options - FULL SCREEN
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view.getParentComponent()), "Customize Your Order", true);
        dialog.setLayout(new BorderLayout());
        
        // Make dialog full screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setSize(screenSize);
        dialog.setLocationRelativeTo(view.getParentComponent());
        dialog.setUndecorated(false);
        dialog.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(0xA9907E), 3));

        // Main container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.WHITE);

        // Header Panel
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
        headerPanel.setPreferredSize(new Dimension(screenSize.width, 100));
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 50, 30));

        JLabel titleLabel = new JLabel("Customize Your Order - " + product.getName());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // Content Panel - Simple BoxLayout
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 100, 40, 100));

        // Product price
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pricePanel.setBackground(new Color(250, 250, 250));
        pricePanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        JLabel priceLabel = new JLabel("Price: ₱" + String.format("%.2f", product.getPrice()) + " each");
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        priceLabel.setForeground(new Color(0xA9907E));
        pricePanel.add(priceLabel);

        // Quantity
        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        qtyPanel.setBackground(Color.WHITE);
        JLabel qtyLabel = new JLabel("Quantity:");
        qtyLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        JTextField quantityField = new JTextField("1", 10);
        quantityField.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        quantityField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xA9907E), 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        qtyPanel.add(qtyLabel);
        qtyPanel.add(quantityField);

        // Toppings
        JPanel toppingsContainer = new JPanel(new BorderLayout());
        toppingsContainer.setBackground(Color.WHITE);
        JLabel toppingsLabel = new JLabel("Add Toppings (Optional):");
        toppingsLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        toppingsLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        JPanel toppingsPanel = new JPanel(new GridLayout(2, 3, 30, 20));
        toppingsPanel.setBackground(new Color(250, 250, 250));
        toppingsPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JCheckBox sprinklesBox = new JCheckBox("Sprinkles");
        JCheckBox chocolateBox = new JCheckBox("Chocolate Chips");
        JCheckBox caramelBox = new JCheckBox("Caramel Drizzle");
        JCheckBox nutsBox = new JCheckBox("Crushed Nuts");
        JCheckBox berriesBox = new JCheckBox("Fresh Berries");
        JCheckBox creamBox = new JCheckBox("Whipped Cream");
        
        JCheckBox[] toppingBoxes = {sprinklesBox, chocolateBox, caramelBox, nutsBox, berriesBox, creamBox};
        for (JCheckBox box : toppingBoxes) {
            box.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            box.setBackground(new Color(250, 250, 250));
            box.setFocusPainted(false);
            toppingsPanel.add(box);
        }
        
        toppingsContainer.add(toppingsLabel, BorderLayout.NORTH);
        toppingsContainer.add(toppingsPanel, BorderLayout.CENTER);

        // Special note
        JPanel noteContainer = new JPanel(new BorderLayout());
        noteContainer.setBackground(Color.WHITE);
        JLabel noteLabel = new JLabel("Special Request/Note (Optional):");
        noteLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        noteLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        JTextArea noteArea = new JTextArea(4, 40);
        noteArea.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        JScrollPane noteScroll = new JScrollPane(noteArea);
        noteScroll.setBorder(BorderFactory.createLineBorder(new Color(0xA9907E), 2));
        
        noteContainer.add(noteLabel, BorderLayout.NORTH);
        noteContainer.add(noteScroll, BorderLayout.CENTER);

        // Add all to content
        contentPanel.add(pricePanel);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(qtyPanel);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(toppingsContainer);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(noteContainer);
        contentPanel.add(Box.createVerticalStrut(40));

        // Buttons at bottom
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30));
        bottomPanel.setBackground(Color.WHITE);

        JButton confirmBtn = createLargeDialogButton("Add to Cart", new Color(0xA9907E));
        confirmBtn.addActionListener(e -> {
            try {
                int quantity = Integer.parseInt(quantityField.getText().trim());
                if (quantity > 0) {
                    StringBuilder toppings = new StringBuilder();
                    for (JCheckBox box : toppingBoxes) {
                        if (box.isSelected()) {
                            if (toppings.length() > 0) toppings.append(", ");
                            toppings.append(box.getText());
                        }
                    }
                    
                    String specialNote = noteArea.getText().trim();
                    
                    cart.addItem(product, quantity, toppings.toString(), specialNote);
                    saveCart(); // Auto-save after adding item
                    view.updateCartDisplay();
                    dialog.dispose();
                    showModernMessage("Success", quantity + "x " + product.getName() + " added to cart!", false);
                } else {
                    showModernMessage("Invalid Quantity", "Quantity must be greater than 0!", true);
                }
            } catch (NumberFormatException ex) {
                showModernMessage("Invalid Input", "Please enter a valid number!", true);
            }
        });

        JButton cancelBtn = createLargeDialogButton("Cancel", new Color(128, 128, 128));
        cancelBtn.addActionListener(e -> dialog.dispose());

        bottomPanel.add(confirmBtn);
        bottomPanel.add(cancelBtn);

        // Scrollable content
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(scrollPane, BorderLayout.CENTER);
        mainContainer.add(bottomPanel, BorderLayout.SOUTH);

        dialog.add(mainContainer);
        
        dialog.getRootPane().registerKeyboardAction(
            e -> dialog.dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        quantityField.requestFocus();
        quantityField.selectAll();
        dialog.setVisible(true);
    }

    public void showCart() {
        if (cart.isEmpty()) {
            showModernMessage("Cart Empty", "Your cart is empty!", true);
            return;
        }

        JDialog cartDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view.getParentComponent()), "Shopping Cart", true);
        cartDialog.setLayout(new BorderLayout());
        
        // Make dialog full screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        cartDialog.setSize(screenSize);
        cartDialog.setLocationRelativeTo(view.getParentComponent());
        cartDialog.setUndecorated(false);
        cartDialog.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(0xA9907E), 3));

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
        headerPanel.setPreferredSize(new Dimension(screenSize.width, 120));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel headerLabel = new JLabel("Your Shopping Cart");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        headerLabel.setForeground(Color.WHITE);
        
        JLabel cartCount = new JLabel(cart.getItems().size() + " item(s)");
        cartCount.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        cartCount.setForeground(new Color(255, 255, 255, 200));
        
        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(cartCount, BorderLayout.EAST);

        // Content wrapper - centered
        JPanel contentWrapper = new JPanel(new GridBagLayout());
        contentWrapper.setBackground(Color.WHITE);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setPreferredSize(new Dimension(1200, screenSize.height - 300));
        contentPanel.setBackground(Color.WHITE);

        // Cart Items List
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (CartItem item : cart.getItems()) {
            listModel.addElement(item.toString());
        }

        JList<String> cartList = new JList<>(listModel);
        cartList.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        cartList.setBackground(Color.WHITE);
        cartList.setForeground(Color.BLACK);
        cartList.setSelectionBackground(new Color(169, 144, 126, 100));
        cartList.setSelectionForeground(Color.BLACK);
        cartList.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        cartList.setFixedCellHeight(60);
        
        JScrollPane scrollPane = new JScrollPane(cartList);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(30, 0, 20, 0),
            BorderFactory.createLineBorder(new Color(0xA9907E), 2)
        ));

        // Total Panel
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 20));
        totalPanel.setBackground(new Color(250, 250, 250));
        totalPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(0xA9907E)),
            BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        JLabel totalLabel = new JLabel("TOTAL: ₱" + String.format("%.2f", cart.getTotal()));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        totalLabel.setForeground(new Color(0xA9907E));
        totalPanel.add(totalLabel);

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(totalPanel, BorderLayout.SOUTH);

        contentWrapper.add(contentPanel);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 30));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        
        JButton updateBtn = createLargeDialogButton("Update Quantity", new Color(0xA9907E));
        updateBtn.addActionListener(e -> {
            int selectedIndex = cartList.getSelectedIndex();
            if (selectedIndex >= 0) {
                showUpdateQuantityDialog(cartDialog, selectedIndex, listModel, totalLabel, cartCount);
            } else {
                showModernMessage("No Selection", "Please select an item first!", true);
            }
        });

        JButton removeBtn = createLargeDialogButton("Remove Item", new Color(220, 53, 69));
        removeBtn.addActionListener(e -> {
            int selectedIndex = cartList.getSelectedIndex();
            if (selectedIndex >= 0) {
                cart.removeItem(selectedIndex);
                saveCart(); // Auto-save after removing item
                listModel.remove(selectedIndex);
                view.updateCartDisplay();
                totalLabel.setText("TOTAL: ₱" + String.format("%.2f", cart.getTotal()));
                cartCount.setText(cart.getItems().size() + " item(s)");
                if (cart.isEmpty()) {
                    cartDialog.dispose();
                    showModernMessage("Cart Empty", "Your cart is now empty!", false);
                }
            } else {
                showModernMessage("No Selection", "Please select an item to remove!", true);
            }
        });

        JButton closeBtn = createLargeDialogButton("Close", new Color(100, 100, 100));
        closeBtn.addActionListener(e -> cartDialog.dispose());

        buttonPanel.add(updateBtn);
        buttonPanel.add(removeBtn);
        buttonPanel.add(closeBtn);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.WHITE);
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(contentWrapper, BorderLayout.CENTER);
        mainContainer.add(buttonPanel, BorderLayout.SOUTH);

        cartDialog.add(mainContainer);
        
        // ESC key to close
        cartDialog.getRootPane().registerKeyboardAction(
            e -> cartDialog.dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        cartDialog.setVisible(true);
    }

    private void showUpdateQuantityDialog(JDialog parentDialog, int itemIndex, DefaultListModel<String> listModel, JLabel totalLabel, JLabel cartCount) {
        JDialog dialog = new JDialog(parentDialog, "Update Quantity", true);
        dialog.setSize(420, 280);
        dialog.setLocationRelativeTo(parentDialog);
        dialog.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(0xA9907E), 2));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(35, 40, 35, 40));

        JLabel label = new JLabel("Enter new quantity:");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField field = new JTextField(String.valueOf(cart.getItems().get(itemIndex).getQuantity()));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setMaximumSize(new Dimension(180, 50));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 18, 10, 18)
        ));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setBackground(Color.WHITE);

        JButton saveBtn = createModernDialogButton("Save", new Color(0xA9907E));
        saveBtn.addActionListener(e -> {
            try {
                int newQty = Integer.parseInt(field.getText().trim());
                if (newQty > 0) {
                    cart.updateQuantity(itemIndex, newQty);
                    saveCart(); // Auto-save after updating quantity
                    listModel.set(itemIndex, cart.getItems().get(itemIndex).toString());
                    view.updateCartDisplay();
                    totalLabel.setText("TOTAL: ₱" + String.format("%.2f", cart.getTotal()));
                    dialog.dispose();
                } else {
                    showModernMessage("Invalid Input", "Quantity must be greater than 0!", true);
                }
            } catch (NumberFormatException ex) {
                showModernMessage("Invalid Input", "Please enter a valid number!", true);
            }
        });

        JButton cancelBtn = createModernDialogButton("Cancel", new Color(128, 128, 128));
        cancelBtn.addActionListener(e -> dialog.dispose());

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(field);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(btnPanel);

        dialog.add(panel);
        field.requestFocus();
        field.selectAll();
        dialog.setVisible(true);
    }

    public void checkout() {
        if (cart.isEmpty()) {
            showModernMessage("Cart Empty", "Your cart is empty!", true);
            return;
        }

        // Create modern confirmation dialog - FULL SCREEN
        JDialog confirmDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view.getParentComponent()), "Confirm Order", true);
        
        // Make dialog full screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        confirmDialog.setSize(screenSize);
        confirmDialog.setLocationRelativeTo(view.getParentComponent());
        confirmDialog.setUndecorated(false);
        confirmDialog.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(0xA9907E), 3));

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.WHITE);

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
        headerPanel.setPreferredSize(new Dimension(screenSize.width, 100));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 50, 25, 50));

        JLabel headerLabel = new JLabel("Order Summary & Receipt");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 38));
        headerLabel.setForeground(Color.WHITE);
        
        JLabel totalHeader = new JLabel("Total: ₱" + String.format("%.2f", cart.getTotal()));
        totalHeader.setFont(new Font("Segoe UI", Font.BOLD, 28));
        totalHeader.setForeground(new Color(255, 255, 255, 230));
        
        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(totalHeader, BorderLayout.EAST);

        // Content - Scrollable receipt
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 80, 20, 80));

        // Receipt content with better formatting
        StringBuilder receipt = new StringBuilder();
        receipt.append("╔════════════════════════════════════════════════╗\n");
        receipt.append("║      SWEET BATTER BAKESHOP - RECEIPT          ║\n");
        receipt.append("╚════════════════════════════════════════════════╝\n\n");
        
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy  hh:mm a");
        receipt.append("Date: ").append(now.format(formatter)).append("\n");
        receipt.append("Order ID: #").append(String.format("%05d", (int)(Math.random() * 99999))).append("\n");
        receipt.append("================================================\n\n");
        receipt.append("ITEMS ORDERED:\n");
        receipt.append("------------------------------------------------\n\n");
        
        for (CartItem item : cart.getItems()) {
            receipt.append(String.format("• %s\n", item.getProduct().getName()));
            receipt.append(String.format("  Quantity: %d x ₱%.2f\n", item.getQuantity(), item.getProduct().getPrice()));
            
            // Add toppings if any
            if (item.getToppings() != null && !item.getToppings().trim().isEmpty()) {
                receipt.append(String.format("  Toppings: %s\n", item.getToppings()));
            }
            
            // Add special note if any
            if (item.getSpecialNote() != null && !item.getSpecialNote().trim().isEmpty()) {
                receipt.append(String.format("  Special Note: %s\n", item.getSpecialNote()));
            }
            
            receipt.append(String.format("  Subtotal: ₱%.2f\n", item.getSubtotal()));
            receipt.append("\n");
        }
        
        receipt.append("------------------------------------------------\n");
        receipt.append(String.format("%-30s ₱%.2f\n", "SUBTOTAL:", cart.getTotal()));
        receipt.append(String.format("%-30s ₱%.2f\n", "Tax (0%):", 0.00));
        receipt.append("================================================\n");
        receipt.append(String.format("%-30s ₱%.2f\n", "TOTAL AMOUNT:", cart.getTotal()));
        receipt.append("================================================\n\n");
        receipt.append("         Salamat po sa inyong pagbili!\n");
        receipt.append("         Thank you for your purchase!\n\n");
        receipt.append("            Visit us again soon! 🧁\n\n");
        receipt.append("================================================\n");
        receipt.append("   Open Daily: 7:00 AM - 9:00 PM\n");
        receipt.append("   Contact: (02) 1234-5678\n");
        receipt.append("================================================\n");

        JTextArea receiptArea = new JTextArea(receipt.toString());
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Consolas", Font.PLAIN, 18));
        receiptArea.setBackground(new Color(255, 255, 255));
        receiptArea.setForeground(Color.BLACK);
        receiptArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xA9907E), 2),
            BorderFactory.createEmptyBorder(40, 60, 40, 60)
        ));
        receiptArea.setLineWrap(false);
        receiptArea.setWrapStyleWord(false);
        
        JScrollPane scrollPane = new JScrollPane(receiptArea);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel with better spacing
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 35, 0));

        JButton confirmBtn = createLargeDialogButton("✓ Confirm Order", new Color(40, 167, 69));
        confirmBtn.addActionListener(e -> {
            confirmDialog.dispose();
            showModernMessage("Payment Successful", "Thank you for your order!\nSalamat po!", false);
            cart.clear();
            clearSavedCart(); // Clear saved cart after successful checkout
            view.updateCartDisplay();
        });

        JButton cancelBtn = createLargeDialogButton("✕ Cancel", new Color(220, 53, 69));
        cancelBtn.addActionListener(e -> confirmDialog.dispose());

        buttonPanel.add(confirmBtn);
        buttonPanel.add(cancelBtn);

        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(contentPanel, BorderLayout.CENTER);
        mainContainer.add(buttonPanel, BorderLayout.SOUTH);

        confirmDialog.add(mainContainer);
        
        // ESC key to close
        confirmDialog.getRootPane().registerKeyboardAction(
            e -> confirmDialog.dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        confirmDialog.setVisible(true);
    }

    private JButton createLargeDialogButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2d.setColor(color.brighter());
                } else {
                    g2d.setColor(color);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 24));
        button.setPreferredSize(new Dimension(220, 70));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createModernDialogButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2d.setColor(color.brighter());
                } else {
                    g2d.setColor(color);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        button.setPreferredSize(new Dimension(130, 50));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void showModernMessage(String title, String message, boolean isError) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view.getParentComponent()), title, true);
        dialog.setSize(420, 240);
        dialog.setLocationRelativeTo(view.getParentComponent());
        dialog.setUndecorated(false);
        dialog.getRootPane().setBorder(BorderFactory.createLineBorder(
            isError ? new Color(220, 53, 69) : new Color(0xA9907E), 2));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(35, 35, 30, 35));

        JLabel iconLabel = new JLabel(isError ? "❌" : "✓", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 45));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel messageLabel = new JLabel("<html><center>" + message + "</center></html>", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        messageLabel.setForeground(Color.BLACK);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton okButton = createModernDialogButton("OK", isError ? new Color(220, 53, 69) : new Color(0xA9907E));
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        okButton.addActionListener(e -> dialog.dispose());

        panel.add(iconLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 18)));
        panel.add(messageLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(okButton);

        dialog.add(panel);
        dialog.setVisible(true);
    }
}
