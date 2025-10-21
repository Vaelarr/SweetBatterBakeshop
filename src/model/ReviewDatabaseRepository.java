package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DatabaseRepository implementation for Review entity.
 * Provides CRUD operations using MySQL database instead of in-memory storage.
 */
public class ReviewDatabaseRepository implements Repository<Review, Long> {
    private final DatabaseConnector dbConnector;
    
    /**
     * Constructor with DatabaseConnector dependency.
     * 
     * @param dbConnector The database connector to use
     */
    public ReviewDatabaseRepository(DatabaseConnector dbConnector) {
        this.dbConnector = dbConnector;
    }
    
    /**
     * Saves a review to the database.
     * 
     * @param review The review to save
     * @return The saved review with generated ID
     */
    @Override
    public Review save(Review review) {
        String sql = "INSERT INTO reviews (restaurant, reviewer, rating, review) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, review.getRestaurant());
            pstmt.setString(2, review.getReviewer());
            pstmt.setInt(3, review.getRating());
            pstmt.setString(4, review.getReview());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating review failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    review.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating review failed, no ID obtained.");
                }
            }
            
            return review;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error saving review: " + e.getMessage(), e);
        }
    }
    
    /**
     * Finds a review by its ID.
     * 
     * @param id The review ID to search for
     * @return Optional containing the review if found, empty otherwise
     */
    @Override
    public Optional<Review> findById(Long id) {
        String sql = "SELECT * FROM reviews WHERE id = ?";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToReview(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new RuntimeException("Error finding review by ID: " + e.getMessage(), e);
        }
    }
    
    /**
     * Returns all reviews from the database.
     * 
     * @return List of all reviews
     */
    @Override
    public List<Review> findAll() {
        String sql = "SELECT * FROM reviews ORDER BY id DESC";
        List<Review> reviews = new ArrayList<>();
        
        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                reviews.add(mapResultSetToReview(rs));
            }
            
            return reviews;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all reviews: " + e.getMessage(), e);
        }
    }
    
    /**
     * Updates an existing review in the database.
     * 
     * @param review The review to update
     * @return The updated review
     */
    @Override
    public Review update(Review review) {
        if (review.getId() == null) {
            throw new IllegalArgumentException("Cannot update review without ID");
        }
        
        String sql = "UPDATE reviews SET restaurant = ?, reviewer = ?, rating = ?, review = ? WHERE id = ?";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, review.getRestaurant());
            pstmt.setString(2, review.getReviewer());
            pstmt.setInt(3, review.getRating());
            pstmt.setString(4, review.getReview());
            pstmt.setLong(5, review.getId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating review failed, no rows affected.");
            }
            
            return review;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error updating review: " + e.getMessage(), e);
        }
    }
    
    /**
     * Deletes a review by its ID.
     * 
     * @param id The ID of the review to delete
     * @return true if deleted successfully, false otherwise
     */
    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM reviews WHERE id = ?";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting review: " + e.getMessage(), e);
        }
    }
    
    /**
     * Checks if a review exists with the given ID.
     * 
     * @param id The ID to check
     * @return true if exists, false otherwise
     */
    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE id = ?";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
            return false;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error checking review existence: " + e.getMessage(), e);
        }
    }
    
    /**
     * Returns the count of all reviews.
     * 
     * @return The total count of reviews
     */
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM reviews";
        
        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            
            return 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error counting reviews: " + e.getMessage(), e);
        }
    }
    
    /**
     * Finds reviews by restaurant name.
     * 
     * @param restaurant The restaurant name to search for
     * @return List of reviews for the specified restaurant
     */
    public List<Review> findByRestaurant(String restaurant) {
        String sql = "SELECT * FROM reviews WHERE restaurant LIKE ? ORDER BY id DESC";
        List<Review> reviews = new ArrayList<>();
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + restaurant + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapResultSetToReview(rs));
                }
            }
            
            return reviews;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error finding reviews by restaurant: " + e.getMessage(), e);
        }
    }
    
    /**
     * Finds reviews by rating.
     * 
     * @param rating The rating to search for
     * @return List of reviews with the specified rating
     */
    public List<Review> findByRating(int rating) {
        String sql = "SELECT * FROM reviews WHERE rating = ? ORDER BY id DESC";
        List<Review> reviews = new ArrayList<>();
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, rating);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapResultSetToReview(rs));
                }
            }
            
            return reviews;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error finding reviews by rating: " + e.getMessage(), e);
        }
    }
    
    /**
     * Helper method to map ResultSet to Review object.
     * 
     * @param rs The ResultSet to map
     * @return A Review object
     * @throws SQLException if mapping fails
     */
    private Review mapResultSetToReview(ResultSet rs) throws SQLException {
        return new Review(
            rs.getLong("id"),
            rs.getString("restaurant"),
            rs.getString("reviewer"),
            rs.getInt("rating"),
            rs.getString("review")
        );
    }
}
