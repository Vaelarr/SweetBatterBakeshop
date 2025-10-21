import model.*;

import java.util.List;
import java.util.Optional;

/**
 * ReviewModelTest - Demonstrates and tests the Review model with in-memory storage.
 * This test uses InMemoryRepository to verify the Review model works correctly
 * without requiring database connectivity.
 */
public class ReviewModelTest {
    
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   Review Model Test (In-Memory)");
        System.out.println("=================================================\n");
        
        try {
            // Create an in-memory repository for testing
            InMemoryRepository<Review> repository = new InMemoryRepository<>();
            ReviewManager reviewManager = new ReviewManager(repository);
            
            // Test 1: Add reviews
            System.out.println("Test 1: Adding Reviews");
            System.out.println("----------------------");
            
            Review review1 = reviewManager.addReview(
                "Sweet Batter Bakeshop", 
                "John Doe", 
                9, 
                "Amazing pastries! The croissants are heavenly."
            );
            System.out.println("✓ Added: " + review1);
            
            Review review2 = reviewManager.addReview(
                "Sweet Batter Bakeshop", 
                "Jane Smith", 
                10, 
                "Best birthday cake ever! Highly recommended."
            );
            System.out.println("✓ Added: " + review2);
            
            Review review3 = reviewManager.addReview(
                "Another Bakery", 
                "Bob Johnson", 
                7, 
                "Good, but a bit pricey."
            );
            System.out.println("✓ Added: " + review3);
            
            // Test 2: Get all reviews
            System.out.println("\nTest 2: Getting All Reviews");
            System.out.println("----------------------------");
            List<Review> allReviews = reviewManager.getAllReviews();
            System.out.println("Total reviews: " + allReviews.size());
            for (Review r : allReviews) {
                System.out.println("  - " + r);
            }
            
            // Test 3: Get review by ID
            System.out.println("\nTest 3: Getting Review by ID");
            System.out.println("-----------------------------");
            Optional<Review> foundReview = reviewManager.getReview(2L);
            if (foundReview.isPresent()) {
                System.out.println("✓ Found: " + foundReview.get());
            } else {
                System.out.println("✗ Review not found");
            }
            
            // Test 4: Update review
            System.out.println("\nTest 4: Updating Review");
            System.out.println("-----------------------");
            Review updated = reviewManager.updateReview(
                1L, 
                "Sweet Batter Bakeshop", 
                "John Doe", 
                10, 
                "Amazing pastries! The croissants are heavenly. Updated to 10/10!"
            );
            System.out.println("✓ Updated: " + updated);
            
            // Test 5: Statistics
            System.out.println("\nTest 5: Statistics");
            System.out.println("------------------");
            System.out.println("Total reviews: " + reviewManager.getTotalReviews());
            System.out.println("Average rating: " + String.format("%.2f", reviewManager.getAverageRating()) + "/10");
            
            // Test 6: Delete review
            System.out.println("\nTest 6: Deleting Review");
            System.out.println("-----------------------");
            boolean deleted = reviewManager.deleteReview(3L);
            System.out.println("✓ Deleted review #3: " + deleted);
            System.out.println("Remaining reviews: " + reviewManager.getTotalReviews());
            
            // Test 7: Validation tests
            System.out.println("\nTest 7: Validation Tests");
            System.out.println("------------------------");
            
            testValidation(reviewManager, "Empty restaurant name", 
                "", "Test", 5, "Good");
            
            testValidation(reviewManager, "Empty reviewer name", 
                "Test Restaurant", "", 5, "Good");
            
            testValidation(reviewManager, "Invalid rating (0)", 
                "Test Restaurant", "Test", 0, "Good");
            
            testValidation(reviewManager, "Invalid rating (11)", 
                "Test Restaurant", "Test", 11, "Good");
            
            testValidation(reviewManager, "Empty review text", 
                "Test Restaurant", "Test", 5, "");
            
            System.out.println("\n=================================================");
            System.out.println("   All Tests Completed Successfully!");
            System.out.println("=================================================\n");
            
            System.out.println("Note: This test uses in-memory storage.");
            System.out.println("To test database functionality, use RestaurantReviewApp");
            System.out.println("with MySQL server running.");
            
        } catch (Exception e) {
            System.err.println("Test failed with error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Helper method to test validation.
     */
    private static void testValidation(ReviewManager manager, String testName, 
                                       String restaurant, String reviewer, int rating, String review) {
        try {
            manager.addReview(restaurant, reviewer, rating, review);
            System.out.println("✗ " + testName + ": Should have thrown exception");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ " + testName + ": " + e.getMessage());
        }
    }
}
