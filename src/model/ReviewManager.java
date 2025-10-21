package model;

import java.util.List;
import java.util.Optional;

/**
 * ReviewManager handles business logic for restaurant reviews.
 * Provides a facade for review operations with validation.
 */
public class ReviewManager {
    private final Repository<Review, Long> reviewRepository;
    
    /**
     * Constructor with repository dependency.
     * 
     * @param reviewRepository The repository to use for data operations
     */
    public ReviewManager(Repository<Review, Long> reviewRepository) {
        this.reviewRepository = reviewRepository;
    }
    
    /**
     * Adds a new review with validation.
     * 
     * @param restaurant The restaurant name
     * @param reviewer The reviewer name
     * @param rating The rating (1-10)
     * @param review The review text
     * @return The saved review
     * @throws IllegalArgumentException if validation fails
     */
    public Review addReview(String restaurant, String reviewer, int rating, String review) {
        // Validate inputs
        validateReviewInput(restaurant, reviewer, rating, review);
        
        Review newReview = new Review(restaurant, reviewer, rating, review);
        return reviewRepository.save(newReview);
    }
    
    /**
     * Gets a review by ID.
     * 
     * @param id The review ID
     * @return Optional containing the review if found
     */
    public Optional<Review> getReview(Long id) {
        return reviewRepository.findById(id);
    }
    
    /**
     * Gets all reviews.
     * 
     * @return List of all reviews
     */
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
    
    /**
     * Updates an existing review with validation.
     * 
     * @param id The review ID to update
     * @param restaurant The new restaurant name
     * @param reviewer The new reviewer name
     * @param rating The new rating
     * @param review The new review text
     * @return The updated review
     * @throws IllegalArgumentException if validation fails or review not found
     */
    public Review updateReview(Long id, String restaurant, String reviewer, int rating, String review) {
        // Validate inputs
        validateReviewInput(restaurant, reviewer, rating, review);
        
        // Check if review exists
        Optional<Review> existingReview = reviewRepository.findById(id);
        if (!existingReview.isPresent()) {
            throw new IllegalArgumentException("Review with ID " + id + " not found");
        }
        
        Review updatedReview = existingReview.get();
        updatedReview.setRestaurant(restaurant);
        updatedReview.setReviewer(reviewer);
        updatedReview.setRating(rating);
        updatedReview.setReview(review);
        
        return reviewRepository.update(updatedReview);
    }
    
    /**
     * Deletes a review by ID.
     * 
     * @param id The review ID to delete
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteReview(Long id) {
        return reviewRepository.deleteById(id);
    }
    
    /**
     * Gets the total count of reviews.
     * 
     * @return The total number of reviews
     */
    public long getTotalReviews() {
        return reviewRepository.count();
    }
    
    /**
     * Gets reviews by restaurant name (if using ReviewDatabaseRepository).
     * 
     * @param restaurant The restaurant name to search for
     * @return List of reviews for the specified restaurant
     */
    public List<Review> getReviewsByRestaurant(String restaurant) {
        if (reviewRepository instanceof ReviewDatabaseRepository) {
            return ((ReviewDatabaseRepository) reviewRepository).findByRestaurant(restaurant);
        }
        throw new UnsupportedOperationException("Restaurant search not supported by current repository");
    }
    
    /**
     * Gets reviews by rating (if using ReviewDatabaseRepository).
     * 
     * @param rating The rating to search for
     * @return List of reviews with the specified rating
     */
    public List<Review> getReviewsByRating(int rating) {
        if (reviewRepository instanceof ReviewDatabaseRepository) {
            return ((ReviewDatabaseRepository) reviewRepository).findByRating(rating);
        }
        throw new UnsupportedOperationException("Rating search not supported by current repository");
    }
    
    /**
     * Validates review input parameters.
     * 
     * @param restaurant The restaurant name
     * @param reviewer The reviewer name
     * @param rating The rating
     * @param review The review text
     * @throws IllegalArgumentException if validation fails
     */
    private void validateReviewInput(String restaurant, String reviewer, int rating, String review) {
        if (restaurant == null || restaurant.trim().isEmpty()) {
            throw new IllegalArgumentException("Restaurant name cannot be empty");
        }
        
        if (reviewer == null || reviewer.trim().isEmpty()) {
            throw new IllegalArgumentException("Reviewer name cannot be empty");
        }
        
        if (rating < 1 || rating > 10) {
            throw new IllegalArgumentException("Rating must be between 1 and 10");
        }
        
        if (review == null || review.trim().isEmpty()) {
            throw new IllegalArgumentException("Review text cannot be empty");
        }
        
        if (review.length() > 1000) {
            throw new IllegalArgumentException("Review text is too long (max 1000 characters)");
        }
    }
    
    /**
     * Gets the average rating for all reviews.
     * 
     * @return The average rating, or 0 if no reviews exist
     */
    public double getAverageRating() {
        List<Review> reviews = reviewRepository.findAll();
        if (reviews.isEmpty()) {
            return 0.0;
        }
        
        double sum = 0;
        for (Review review : reviews) {
            sum += review.getRating();
        }
        
        return sum / reviews.size();
    }
}
