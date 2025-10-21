package model;

/**
 * Base interface for entities with identifiable IDs.
 * Allows generic repository operations on entities with IDs.
 */
public interface IdentifiableEntity {
    Long getId();
    void setId(Long id);
}
