package main.java.kiosk.util;

import java.io.IOException;

/**
 * Interface for data persistence operations
 * @param <T> The type of data to persist
 */
public interface DataPersistence<T> {
    /**
     * Save data to persistent storage
     * @throws IOException if save fails
     */
    void save() throws IOException;
    
    /**
     * Load data from persistent storage
     * @throws IOException if load fails
     * @throws ClassNotFoundException if class not found during deserialization
     */
    void load() throws IOException, ClassNotFoundException;
}
