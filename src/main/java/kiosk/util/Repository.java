package main.java.kiosk.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Generic repository with serialization support for data persistence
 * @param <T> The type of objects to store (must be Serializable)
 */
public class Repository<T extends Serializable> {
    private List<T> items;
    private String filePath;
    
    public Repository(String filePath) {
        this.filePath = filePath;
        this.items = new ArrayList<>();
    }
    
    /**
     * Add an item to the repository
     */
    public void add(T item) {
        items.add(item);
    }
    
    /**
     * Remove an item from the repository
     */
    public boolean remove(T item) {
        return items.remove(item);
    }
    
    /**
     * Get all items
     */
    public List<T> getAll() {
        return new ArrayList<>(items);
    }
    
    /**
     * Clear all items
     */
    public void clear() {
        items.clear();
    }
    
    /**
     * Get the size of the repository
     */
    public int size() {
        return items.size();
    }
    
    /**
     * Check if repository is empty
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    /**
     * Save items to file using serialization
     */
    public void saveToFile() throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(items);
        }
    }
    
    /**
     * Load items from file using deserialization
     */
    @SuppressWarnings("unchecked")
    public void loadFromFile() throws IOException, ClassNotFoundException {
        File file = new File(filePath);
        if (!file.exists()) {
            items = new ArrayList<>();
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            items = (List<T>) ois.readObject();
        }
    }
    
    /**
     * Replace all items in the repository
     */
    public void setAll(List<T> newItems) {
        this.items = new ArrayList<>(newItems);
    }
}
