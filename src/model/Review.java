package model;

import java.io.Serializable;

/**
 * Review entity representing a restaurant/bakeshop review.
 * Implements IdentifiableEntity for use with Repository pattern.
 */
public class Review implements IdentifiableEntity, Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String restaurant;
    private String reviewer;
    private int rating;
    private String review;
    
    /**
     * Default constructor for database operations.
     */
    public Review() {
    }
    
    /**
     * Constructor with all fields except ID (for new reviews).
     * 
     * @param restaurant The name of the restaurant/bakeshop
     * @param reviewer The name of the reviewer
     * @param rating The rating score (1-10)
     * @param review The review text content
     */
    public Review(String restaurant, String reviewer, int rating, String review) {
        this.restaurant = restaurant;
        this.reviewer = reviewer;
        this.rating = rating;
        this.review = review;
    }
    
    /**
     * Constructor with all fields including ID (for database retrieval).
     * 
     * @param id The review ID
     * @param restaurant The name of the restaurant/bakeshop
     * @param reviewer The name of the reviewer
     * @param rating The rating score (1-10)
     * @param review The review text content
     */
    public Review(Long id, String restaurant, String reviewer, int rating, String review) {
        this.id = id;
        this.restaurant = restaurant;
        this.reviewer = reviewer;
        this.rating = rating;
        this.review = review;
    }
    
    // IdentifiableEntity implementation
    @Override
    public Long getId() {
        return id;
    }
    
    @Override
    public void setId(Long id) {
        this.id = id;
    }
    
    // Getters and Setters
    public String getRestaurant() {
        return restaurant;
    }
    
    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }
    
    public String getReviewer() {
        return reviewer;
    }
    
    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }
    
    public int getRating() {
        return rating;
    }
    
    public void setRating(int rating) {
        this.rating = rating;
    }
    
    public String getReview() {
        return review;
    }
    
    public void setReview(String review) {
        this.review = review;
    }
    
    @Override
    public String toString() {
        return "Review #" + id + " - " + restaurant + " by " + reviewer + 
               " (" + rating + "/10): " + review;
    }
}
