import model.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * RestaurantReviewApp - Main application for managing restaurant reviews.
 * Provides a command-line interface for CRUD operations on reviews stored in MySQL database.
 */
public class RestaurantReviewApp {
    
    private static ReviewManager reviewManager;
    private static Scanner scanner;
    
    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        
        System.out.println("=================================================");
        System.out.println("   Sweet Batter Bakeshop - Review System");
        System.out.println("=================================================\n");
        
        // Initialize database connection
        if (!initializeDatabase()) {
            System.err.println("Failed to initialize database. Exiting...");
            return;
        }
        
        // Main application loop
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    addReview();
                    break;
                case 2:
                    viewAllReviews();
                    break;
                case 3:
                    viewReviewById();
                    break;
                case 4:
                    searchReviewsByRestaurant();
                    break;
                case 5:
                    searchReviewsByRating();
                    break;
                case 6:
                    updateReview();
                    break;
                case 7:
                    deleteReview();
                    break;
                case 8:
                    viewStatistics();
                    break;
                case 9:
                    running = false;
                    System.out.println("\nThank you for using Sweet Batter Bakeshop Review System!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.\n");
            }
        }
        
        scanner.close();
    }
    
    /**
     * Initializes the database connection and repository.
     * 
     * @return true if successful, false otherwise
     */
    private static boolean initializeDatabase() {
        try {
            System.out.println("Connecting to database...");
            
            // Create database connector with default XAMPP settings
            DatabaseConnector dbConnector = new DatabaseConnector();
            
            // Initialize database and table
            dbConnector.initializeDatabase();
            
            // Test connection
            if (!dbConnector.testConnection()) {
                System.err.println("Database connection test failed!");
                return false;
            }
            
            // Create repository and manager
            ReviewDatabaseRepository repository = new ReviewDatabaseRepository(dbConnector);
            reviewManager = new ReviewManager(repository);
            
            System.out.println("Database connected successfully!\n");
            return true;
            
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
            System.err.println("\nPlease ensure:");
            System.err.println("1. XAMPP MySQL server is running");
            System.err.println("2. MySQL JDBC driver (mysql-connector-java.jar) is in classpath");
            System.err.println("3. Database credentials are correct (default: root with no password)");
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Displays the main menu.
     */
    private static void displayMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. Add New Review");
        System.out.println("2. View All Reviews");
        System.out.println("3. View Review by ID");
        System.out.println("4. Search Reviews by Restaurant");
        System.out.println("5. Search Reviews by Rating");
        System.out.println("6. Update Review");
        System.out.println("7. Delete Review");
        System.out.println("8. View Statistics");
        System.out.println("9. Exit");
        System.out.println();
    }
    
    /**
     * Adds a new review.
     */
    private static void addReview() {
        System.out.println("\n=== Add New Review ===");
        
        try {
            String restaurant = getStringInput("Restaurant name: ");
            String reviewer = getStringInput("Your name: ");
            int rating = getIntInput("Rating (1-10): ");
            String review = getStringInput("Your review: ");
            
            Review savedReview = reviewManager.addReview(restaurant, reviewer, rating, review);
            System.out.println("\n✓ Review added successfully! (ID: " + savedReview.getId() + ")");
            
        } catch (IllegalArgumentException e) {
            System.out.println("\n✗ Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n✗ Failed to add review: " + e.getMessage());
        }
    }
    
    /**
     * Views all reviews.
     */
    private static void viewAllReviews() {
        System.out.println("\n=== All Reviews ===");
        
        try {
            List<Review> reviews = reviewManager.getAllReviews();
            
            if (reviews.isEmpty()) {
                System.out.println("No reviews found.");
            } else {
                System.out.println("Total reviews: " + reviews.size() + "\n");
                for (Review review : reviews) {
                    displayReview(review);
                }
            }
            
        } catch (Exception e) {
            System.out.println("\n✗ Failed to retrieve reviews: " + e.getMessage());
        }
    }
    
    /**
     * Views a review by ID.
     */
    private static void viewReviewById() {
        System.out.println("\n=== View Review by ID ===");
        
        try {
            long id = getIntInput("Enter review ID: ");
            Optional<Review> review = reviewManager.getReview(id);
            
            if (review.isPresent()) {
                displayReview(review.get());
            } else {
                System.out.println("Review with ID " + id + " not found.");
            }
            
        } catch (Exception e) {
            System.out.println("\n✗ Failed to retrieve review: " + e.getMessage());
        }
    }
    
    /**
     * Searches reviews by restaurant name.
     */
    private static void searchReviewsByRestaurant() {
        System.out.println("\n=== Search Reviews by Restaurant ===");
        
        try {
            String restaurant = getStringInput("Restaurant name: ");
            List<Review> reviews = reviewManager.getReviewsByRestaurant(restaurant);
            
            if (reviews.isEmpty()) {
                System.out.println("No reviews found for '" + restaurant + "'.");
            } else {
                System.out.println("Found " + reviews.size() + " review(s):\n");
                for (Review review : reviews) {
                    displayReview(review);
                }
            }
            
        } catch (Exception e) {
            System.out.println("\n✗ Failed to search reviews: " + e.getMessage());
        }
    }
    
    /**
     * Searches reviews by rating.
     */
    private static void searchReviewsByRating() {
        System.out.println("\n=== Search Reviews by Rating ===");
        
        try {
            int rating = getIntInput("Rating (1-10): ");
            List<Review> reviews = reviewManager.getReviewsByRating(rating);
            
            if (reviews.isEmpty()) {
                System.out.println("No reviews found with rating " + rating + ".");
            } else {
                System.out.println("Found " + reviews.size() + " review(s):\n");
                for (Review review : reviews) {
                    displayReview(review);
                }
            }
            
        } catch (Exception e) {
            System.out.println("\n✗ Failed to search reviews: " + e.getMessage());
        }
    }
    
    /**
     * Updates an existing review.
     */
    private static void updateReview() {
        System.out.println("\n=== Update Review ===");
        
        try {
            long id = getIntInput("Enter review ID to update: ");
            
            Optional<Review> existingReview = reviewManager.getReview(id);
            if (!existingReview.isPresent()) {
                System.out.println("Review with ID " + id + " not found.");
                return;
            }
            
            System.out.println("\nCurrent review:");
            displayReview(existingReview.get());
            
            System.out.println("\nEnter new values:");
            String restaurant = getStringInput("Restaurant name: ");
            String reviewer = getStringInput("Reviewer name: ");
            int rating = getIntInput("Rating (1-10): ");
            String review = getStringInput("Review: ");
            
            Review updated = reviewManager.updateReview(id, restaurant, reviewer, rating, review);
            System.out.println("\n✓ Review updated successfully!");
            displayReview(updated);
            
        } catch (IllegalArgumentException e) {
            System.out.println("\n✗ Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n✗ Failed to update review: " + e.getMessage());
        }
    }
    
    /**
     * Deletes a review.
     */
    private static void deleteReview() {
        System.out.println("\n=== Delete Review ===");
        
        try {
            long id = getIntInput("Enter review ID to delete: ");
            
            Optional<Review> existingReview = reviewManager.getReview(id);
            if (!existingReview.isPresent()) {
                System.out.println("Review with ID " + id + " not found.");
                return;
            }
            
            System.out.println("\nReview to delete:");
            displayReview(existingReview.get());
            
            String confirm = getStringInput("\nAre you sure you want to delete this review? (yes/no): ");
            if (confirm.equalsIgnoreCase("yes")) {
                if (reviewManager.deleteReview(id)) {
                    System.out.println("\n✓ Review deleted successfully!");
                } else {
                    System.out.println("\n✗ Failed to delete review.");
                }
            } else {
                System.out.println("\nDeletion cancelled.");
            }
            
        } catch (Exception e) {
            System.out.println("\n✗ Failed to delete review: " + e.getMessage());
        }
    }
    
    /**
     * Views statistics about reviews.
     */
    private static void viewStatistics() {
        System.out.println("\n=== Review Statistics ===");
        
        try {
            long totalReviews = reviewManager.getTotalReviews();
            double averageRating = reviewManager.getAverageRating();
            
            System.out.println("Total Reviews: " + totalReviews);
            System.out.println("Average Rating: " + String.format("%.2f", averageRating) + "/10");
            
        } catch (Exception e) {
            System.out.println("\n✗ Failed to retrieve statistics: " + e.getMessage());
        }
    }
    
    /**
     * Displays a review in a formatted way.
     * 
     * @param review The review to display
     */
    private static void displayReview(Review review) {
        System.out.println("─────────────────────────────────────");
        System.out.println("ID: " + review.getId());
        System.out.println("Restaurant: " + review.getRestaurant());
        System.out.println("Reviewer: " + review.getReviewer());
        System.out.println("Rating: " + review.getRating() + "/10");
        System.out.println("Review: " + review.getReview());
        System.out.println("─────────────────────────────────────\n");
    }
    
    /**
     * Gets string input from user.
     * 
     * @param prompt The prompt to display
     * @return The user's input
     */
    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    /**
     * Gets integer input from user with validation.
     * 
     * @param prompt The prompt to display
     * @return The user's input as integer
     */
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}
