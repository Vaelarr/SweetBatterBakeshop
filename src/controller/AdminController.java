package controller;

import model.*;
import view.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminController {
    private ProductCatalog productCatalog;
    private TransactionManager transactionManager;
    private AdminManager adminManager;
    private AdminDashboardView dashboardView;
    private Admin currentAdmin;

    public AdminController() {
        // Initialize managers
        this.productCatalog = new ProductCatalog();
        this.transactionManager = new TransactionManager();
        this.adminManager = new AdminManager();
    }

    public void showLoginDialog() {
        AdminLoginView loginView = new AdminLoginView(null);
        loginView.setVisible(true);

        if (loginView.isLoginSuccessful()) {
            String username = loginView.getEnteredUsername();
            String password = loginView.getEnteredPassword();

            Admin admin = adminManager.authenticate(username, password);
            if (admin != null) {
                currentAdmin = admin;
                showDashboard();
            } else {
                JOptionPane.showMessageDialog(null,
                    "Invalid username or password!",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
                showLoginDialog(); // Try again
            }
        }
    }

    public void showDashboard() {
        if (dashboardView != null) {
            dashboardView.dispose();
        }
        dashboardView = new AdminDashboardView(currentAdmin, this);
        dashboardView.setVisible(true);
    }

    // Product Management
    public void handleAddProduct() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField nameField = new JTextField();
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{
            "Cakes", "Cupcakes", "Pastries", "Cookies", "Breads"
        });
        JTextField priceField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField stockField = new JTextField("100");

        panel.add(new JLabel("Product Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryCombo);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Initial Stock:"));
        panel.add(stockField);

        int result = JOptionPane.showConfirmDialog(dashboardView, panel,
            "Add New Product", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                String category = (String) categoryCombo.getSelectedItem();
                double price = Double.parseDouble(priceField.getText().trim());
                String description = descriptionField.getText().trim();
                int stock = Integer.parseInt(stockField.getText().trim());

                if (name.isEmpty() || description.isEmpty()) {
                    throw new IllegalArgumentException("Name and description cannot be empty");
                }

                Product newProduct = new Product(name, category, price, description, stock);
                productCatalog.addProduct(newProduct);
                dashboardView.loadProductData();

                JOptionPane.showMessageDialog(dashboardView,
                    "Product added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(dashboardView,
                    "Error adding product: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void handleEditProduct(int selectedRow) {
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(dashboardView,
                "Please select a product to edit.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Product> products = productCatalog.getAllProducts();
        Product product = products.get(selectedRow);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField nameField = new JTextField(product.getName());
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{
            "Cakes", "Cupcakes", "Pastries", "Cookies", "Breads"
        });
        categoryCombo.setSelectedItem(product.getCategory());
        JTextField priceField = new JTextField(String.valueOf(product.getPrice()));
        JTextField descriptionField = new JTextField(product.getDescription());

        panel.add(new JLabel("Product Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryCombo);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);

        int result = JOptionPane.showConfirmDialog(dashboardView, panel,
            "Edit Product", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                product.setName(nameField.getText().trim());
                product.setCategory((String) categoryCombo.getSelectedItem());
                product.setPrice(Double.parseDouble(priceField.getText().trim()));
                product.setDescription(descriptionField.getText().trim());

                dashboardView.loadProductData();

                JOptionPane.showMessageDialog(dashboardView,
                    "Product updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(dashboardView,
                    "Error updating product: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void handleDeleteProduct(int selectedRow) {
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(dashboardView,
                "Please select a product to delete.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(dashboardView,
            "Are you sure you want to delete this product?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            List<Product> products = productCatalog.getAllProducts();
            Product product = products.get(selectedRow);
            productCatalog.removeProduct(product);
            dashboardView.loadProductData();

            JOptionPane.showMessageDialog(dashboardView,
                "Product deleted successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void handleUpdateStock(int selectedRow) {
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(dashboardView,
                "Please select a product to update stock.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Product> products = productCatalog.getAllProducts();
        Product product = products.get(selectedRow);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        JLabel currentStockLabel = new JLabel("Current: " + product.getStock());
        JTextField newStockField = new JTextField(String.valueOf(product.getStock()));
        JCheckBox availableCheckBox = new JCheckBox("Available", product.isAvailable());

        panel.add(new JLabel("Product:"));
        panel.add(new JLabel(product.getName()));
        panel.add(new JLabel("Current Stock:"));
        panel.add(currentStockLabel);
        panel.add(new JLabel("New Stock:"));
        panel.add(newStockField);
        panel.add(new JLabel("Availability:"));
        panel.add(availableCheckBox);

        int result = JOptionPane.showConfirmDialog(dashboardView, panel,
            "Update Stock", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int newStock = Integer.parseInt(newStockField.getText().trim());
                product.setStock(newStock);
                product.setAvailable(availableCheckBox.isSelected());

                dashboardView.loadProductData();

                JOptionPane.showMessageDialog(dashboardView,
                    "Stock updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(dashboardView,
                    "Error updating stock: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Transaction Management
    public void handleViewTransaction(int selectedRow) {
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(dashboardView,
                "Please select a transaction to view.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Transaction> transactions = transactionManager.getAllTransactions();
        Transaction transaction = transactions.get(selectedRow);

        StringBuilder details = new StringBuilder();
        details.append("Transaction ID: ").append(transaction.getTransactionId()).append("\n");
        details.append("Date & Time: ").append(transaction.getDateTime()).append("\n");
        details.append("Payment Method: ").append(transaction.getPaymentMethod()).append("\n");
        details.append("Status: ").append(transaction.getStatus()).append("\n\n");
        details.append("Items:\n");
        details.append("─────────────────────────────────────\n");

        for (CartItem item : transaction.getItems()) {
            details.append(item.getProduct().getName())
                   .append(" x").append(item.getQuantity())
                   .append(" - ₱").append(String.format("%.2f", item.getSubtotal()))
                   .append("\n");
            if (!item.getToppings().isEmpty()) {
                details.append("  Toppings: ").append(item.getToppings()).append("\n");
            }
            if (!item.getSpecialNote().isEmpty()) {
                details.append("  Note: ").append(item.getSpecialNote()).append("\n");
            }
        }

        details.append("─────────────────────────────────────\n");
        details.append("Total: ₱").append(String.format("%.2f", transaction.getTotalAmount()));

        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(400, 300));

        JOptionPane.showMessageDialog(dashboardView,
            scrollPane,
            "Transaction Details",
            JOptionPane.INFORMATION_MESSAGE);
    }

    // Reports
    public void handleGenerateReport(String reportType) {
        StringBuilder report = new StringBuilder();
        report.append("═══════════════════════════════════════════════════\n");
        report.append("  ").append(reportType.toUpperCase()).append("\n");
        report.append("  Generated: ").append(java.time.LocalDateTime.now()).append("\n");
        report.append("═══════════════════════════════════════════════════\n\n");

        switch (reportType) {
            case "Sales Summary":
                report.append(generateSalesSummary());
                break;
            case "Product Inventory":
                report.append(generateInventoryReport());
                break;
            case "Top Selling Products":
                report.append(generateTopSellingReport());
                break;
            case "Transaction History":
                report.append(generateTransactionHistory());
                break;
        }

        dashboardView.displayReport(report.toString());
    }

    private String generateSalesSummary() {
        StringBuilder sb = new StringBuilder();
        double totalRevenue = transactionManager.getTotalRevenue();
        int totalTransactions = transactionManager.getTotalTransactionCount();
        int completedTransactions = transactionManager.getTransactionsByStatus("Completed").size();

        sb.append("Total Revenue: ₱").append(String.format("%.2f", totalRevenue)).append("\n");
        sb.append("Total Transactions: ").append(totalTransactions).append("\n");
        sb.append("Completed Transactions: ").append(completedTransactions).append("\n");
        sb.append("Average Transaction Value: ₱");
        if (completedTransactions > 0) {
            sb.append(String.format("%.2f", totalRevenue / completedTransactions));
        } else {
            sb.append("0.00");
        }
        sb.append("\n");

        return sb.toString();
    }

    private String generateInventoryReport() {
        StringBuilder sb = new StringBuilder();
        List<Product> products = productCatalog.getAllProducts();

        sb.append(String.format("%-30s %-15s %-10s %-10s %-10s\n",
            "Product", "Category", "Price", "Stock", "Status"));
        sb.append("─────────────────────────────────────────────────────────────────────────\n");

        for (Product product : products) {
            sb.append(String.format("%-30s %-15s ₱%-9.2f %-10d %-10s\n",
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getStock(),
                product.isAvailable() ? "Available" : "Unavailable"));
        }

        return sb.toString();
    }

    private String generateTopSellingReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("Top Selling Products Analysis\n\n");
        sb.append("This feature tracks product sales based on transaction history.\n");
        sb.append("Implementation: Count product occurrences in completed transactions.\n\n");

        // Placeholder for actual implementation
        sb.append("Feature coming soon...\n");

        return sb.toString();
    }

    private String generateTransactionHistory() {
        StringBuilder sb = new StringBuilder();
        List<Transaction> transactions = transactionManager.getAllTransactions();

        sb.append(String.format("%-15s %-25s %-15s %-15s %-12s\n",
            "ID", "Date & Time", "Amount", "Payment", "Status"));
        sb.append("─────────────────────────────────────────────────────────────────────────────────────\n");

        for (Transaction transaction : transactions) {
            sb.append(String.format("%-15s %-25s ₱%-14.2f %-15s %-12s\n",
                transaction.getTransactionId(),
                transaction.getDateTime().toString(),
                transaction.getTotalAmount(),
                transaction.getPaymentMethod(),
                transaction.getStatus()));
        }

        return sb.toString();
    }

    // Getters for dashboard
    public List<Product> getAllProducts() {
        return productCatalog.getAllProducts();
    }

    public List<Transaction> getAllTransactions() {
        return transactionManager.getAllTransactions();
    }

    public double getTotalRevenue() {
        return transactionManager.getTotalRevenue();
    }

    public int getTotalTransactionCount() {
        return transactionManager.getTotalTransactionCount();
    }

    public ProductCatalog getProductCatalog() {
        return productCatalog;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void backToMainApp() {
        dashboardView.dispose();
        SwingUtilities.invokeLater(() -> {
            MainView mainView = new MainView();
            mainView.setVisible(true);
        });
    }
}
