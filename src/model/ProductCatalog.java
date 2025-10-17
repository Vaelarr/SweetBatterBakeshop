package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductCatalog implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Product> products;

    public ProductCatalog() {
        products = new ArrayList<>();
        initializeProducts();
    }

    private void initializeProducts() {
        // Cakes
        products.add(new Product("Chocolate Cake", "Cakes", 450.00, "Rich chocolate layered cake"));
        products.add(new Product("Red Velvet Cake", "Cakes", 480.00, "Classic red velvet with cream cheese"));
        products.add(new Product("Carrot Cake", "Cakes", 420.00, "Moist carrot cake with walnuts"));
        products.add(new Product("Strawberry Cake", "Cakes", 550.00, "Fresh strawberry delight"));
        products.add(new Product("Mocha Cake", "Cakes", 380.00, "Coffee-flavored chocolate cake"));

        // Cupcakes
        products.add(new Product("Vanilla Cupcake", "Cupcakes", 45.00, "Classic vanilla cupcake"));
        products.add(new Product("Chocolate Cupcake", "Cupcakes", 45.00, "Rich chocolate cupcake"));
        products.add(new Product("Red Velvet Cupcake", "Cupcakes", 55.00, "Mini red velvet treat"));
        products.add(new Product("Lemon Cupcake", "Cupcakes", 50.00, "Tangy lemon cupcake"));
        products.add(new Product("Blueberry Cupcake", "Cupcakes", 40.00, "Fresh blueberry cupcake"));

        // Pastries
        products.add(new Product("Croissant", "Pastries", 65.00, "Buttery French croissant"));
        products.add(new Product("Danish Pastry", "Pastries", 75.00, "Fruit-filled Danish"));
        products.add(new Product("Cinnamon Roll", "Pastries", 60.00, "Warm cinnamon swirl"));
        products.add(new Product("Apple Turnover", "Pastries", 70.00, "Flaky apple-filled pastry"));
        products.add(new Product("Eclair", "Pastries", 68.00, "Chocolate-topped cream puff"));

        // Cookies
        products.add(new Product("Chocolate Chip Cookie", "Cookies", 25.00, "Classic chocolate chip"));
        products.add(new Product("Oatmeal Cookie", "Cookies", 22.00, "Hearty oatmeal cookie"));
        products.add(new Product("Sugar Cookie", "Cookies", 18.00, "Sweet sugar cookie"));
        products.add(new Product("Peanut Butter Cookie", "Cookies", 28.00, "Creamy peanut butter"));
        products.add(new Product("Double Chocolate Cookie", "Cookies", 25.00, "Extra chocolatey"));

        // Breads
        products.add(new Product("Pandesal", "Breads", 30.00, "Filipino bread roll"));
        products.add(new Product("Ensaymada", "Breads", 45.00, "Sweet buttered bread"));
        products.add(new Product("Spanish Bread", "Breads", 35.00, "Bread with sweet filling"));
        products.add(new Product("Cheese Bread", "Breads", 40.00, "Savory cheese bread"));
        products.add(new Product("Garlic Bread", "Breads", 85.00, "Toasted garlic bread"));
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public List<Product> getProductsByCategory(String category) {
        if (category.equals("All")) {
            return getAllProducts();
        }
        return products.stream()
                .filter(p -> p.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        categories.add("All");
        categories.add("Cakes");
        categories.add("Cupcakes");
        categories.add("Pastries");
        categories.add("Cookies");
        categories.add("Breads");
        return categories;
    }

    // Admin methods
    public void addProduct(Product product) {
        products.add(product);
    }

    public boolean removeProduct(Product product) {
        return products.remove(product);
    }

    public Product getProductByName(String name) {
        return products.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
