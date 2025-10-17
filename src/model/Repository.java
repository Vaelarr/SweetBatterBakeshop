package model;

import java.util.List;
import java.util.Optional;

/**
 * Generic Repository interface for CRUD operations.
 * Provides type-safe data access patterns using generics.
 * 
 * @param <T> The type of entity managed by this repository
 * @param <ID> The type of the entity's identifier
 */
public interface Repository<T, ID> {
    
    /**
     * Saves an entity to the repository.
     * 
     * @param entity The entity to save
     * @return The saved entity
     */
    T save(T entity);
    
    /**
     * Finds an entity by its identifier.
     * 
     * @param id The identifier to search for
     * @return Optional containing the entity if found, empty otherwise
     */
    Optional<T> findById(ID id);
    
    /**
     * Returns all entities in the repository.
     * 
     * @return List of all entities
     */
    List<T> findAll();
    
    /**
     * Updates an existing entity.
     * 
     * @param entity The entity to update
     * @return The updated entity
     */
    T update(T entity);
    
    /**
     * Deletes an entity by its identifier.
     * 
     * @param id The identifier of the entity to delete
     * @return true if deleted successfully, false otherwise
     */
    boolean deleteById(ID id);
    
    /**
     * Checks if an entity exists with the given identifier.
     * 
     * @param id The identifier to check
     * @return true if exists, false otherwise
     */
    boolean existsById(ID id);
    
    /**
     * Returns the count of all entities.
     * 
     * @return The total count of entities
     */
    long count();
}
