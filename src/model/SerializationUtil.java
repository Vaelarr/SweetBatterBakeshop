package model;

import java.io.*;
import java.nio.file.*;
import java.util.Optional;

/**
 * Generic utility class for serializing and deserializing objects.
 * Provides type-safe save/load operations for any Serializable object.
 * 
 * @param <T> The type of object to serialize (must implement Serializable)
 */
public class SerializationUtil<T extends Serializable> {
    private static final String DATA_DIRECTORY = "data";
    
    /**
     * Saves an object to a file using serialization.
     * Creates the data directory if it doesn't exist.
     * 
     * @param object The object to save
     * @param filename The name of the file to save to
     * @return true if successful, false otherwise
     */
    public boolean save(T object, String filename) {
        try {
            // Create data directory if it doesn't exist
            Path dataDir = Paths.get(DATA_DIRECTORY);
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            
            // Save object to file
            String filepath = DATA_DIRECTORY + File.separator + filename;
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(filepath))) {
                oos.writeObject(object);
                return true;
            }
        } catch (IOException e) {
            System.err.println("Error saving object: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Loads an object from a file using deserialization.
     * 
     * @param filename The name of the file to load from
     * @return Optional containing the loaded object, or empty if failed
     */
    @SuppressWarnings("unchecked")
    public Optional<T> load(String filename) {
        try {
            String filepath = DATA_DIRECTORY + File.separator + filename;
            File file = new File(filepath);
            
            if (!file.exists()) {
                return Optional.empty();
            }
            
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(filepath))) {
                T object = (T) ois.readObject();
                return Optional.of(object);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading object: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Checks if a saved file exists.
     * 
     * @param filename The name of the file to check
     * @return true if the file exists, false otherwise
     */
    public boolean exists(String filename) {
        String filepath = DATA_DIRECTORY + File.separator + filename;
        return Files.exists(Paths.get(filepath));
    }
    
    /**
     * Deletes a saved file.
     * 
     * @param filename The name of the file to delete
     * @return true if successful, false otherwise
     */
    public boolean delete(String filename) {
        try {
            String filepath = DATA_DIRECTORY + File.separator + filename;
            return Files.deleteIfExists(Paths.get(filepath));
        } catch (IOException e) {
            System.err.println("Error deleting file: " + e.getMessage());
            return false;
        }
    }
}
