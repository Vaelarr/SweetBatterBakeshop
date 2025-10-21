package model;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Generic in-memory repository implementation.
 * Provides CRUD operations with automatic ID generation.
 * Thread-safe ID generation using AtomicLong.
 * 
 * @param <T> The type of entity (must extend IdentifiableEntity)
 */
public class InMemoryRepository<T extends IdentifiableEntity> implements Repository<T, Long> {
    private final Map<Long, T> storage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @Override
    public T save(T entity) {
        if (entity.getId() == null) {
            entity.setId(idGenerator.getAndIncrement());
        }
        storage.put(entity.getId(), entity);
        return entity;
    }
    
    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }
    
    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }
    
    @Override
    public T update(T entity) {
        if (entity.getId() == null || !storage.containsKey(entity.getId())) {
            throw new IllegalArgumentException("Entity does not exist");
        }
        storage.put(entity.getId(), entity);
        return entity;
    }
    
    @Override
    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }
    
    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
    
    @Override
    public long count() {
        return storage.size();
    }
    
    /**
     * Finds entities by a custom predicate using generics.
     * 
     * @param predicate The condition to match
     * @return List of matching entities
     */
    public List<T> findByPredicate(java.util.function.Predicate<T> predicate) {
        return storage.values().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
}
