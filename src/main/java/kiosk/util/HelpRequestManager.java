package main.java.kiosk.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Manages customer help requests throughout the kiosk system
 */
public class HelpRequestManager {
    private static final HelpRequestManager instance = new HelpRequestManager();
    
    // List of active help requests
    private List<HelpRequest> activeRequests;
    
    // Callbacks for listeners (admin panels)
    private List<Consumer<HelpRequest>> listeners;
    
    private HelpRequestManager() {
        activeRequests = new ArrayList<>();
        listeners = new ArrayList<>();
    }
    
    public static HelpRequestManager getInstance() {
        return instance;
    }
    
    /**
     * Customer submits a help request
     * 
     * @param location The location (screen) where help was requested
     * @param issueType The type of help needed
     * @param details Additional details
     * @return The created help request
     */
    public HelpRequest submitRequest(String location, String issueType, String details) {
        HelpRequest request = new HelpRequest(location, issueType, details);
        activeRequests.add(request);
        
        // Notify all listeners
        for (Consumer<HelpRequest> listener : listeners) {
            listener.accept(request);
        }
        
        return request;
    }
    
    /**
     * Register a listener to be notified of new help requests
     * 
     * @param listener The listener to add
     */
    public void addListener(Consumer<HelpRequest> listener) {
        listeners.add(listener);
    }
    
    /**
     * Remove a listener
     * 
     * @param listener The listener to remove
     */
    public void removeListener(Consumer<HelpRequest> listener) {
        listeners.remove(listener);
    }
    
    /**
     * Get all active help requests
     * 
     * @return List of active help requests
     */
    public List<HelpRequest> getActiveRequests() {
        return new ArrayList<>(activeRequests);
    }
    
    /**
     * Mark a help request as resolved
     * 
     * @param requestId The ID of the request to mark as resolved
     * @return true if the request was found and resolved, false otherwise
     */
    public boolean resolveRequest(String requestId) {
        for (int i = 0; i < activeRequests.size(); i++) {
            if (activeRequests.get(i).getId().equals(requestId)) {
                activeRequests.get(i).setResolved(true);
                activeRequests.remove(i);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Inner class representing a help request
     */
    public static class HelpRequest {
        private String id;
        private String location;
        private String issueType;
        private String details;
        private LocalDateTime timestamp;
        private boolean resolved;
        
        public HelpRequest(String location, String issueType, String details) {
            this.location = location;
            this.issueType = issueType;
            this.details = details;
            this.timestamp = LocalDateTime.now();
            this.resolved = false;
            
            // Generate a unique ID
            this.id = "HELP-" + timestamp.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        }
        
        public String getId() {
            return id;
        }
        
        public String getLocation() {
            return location;
        }
        
        public String getIssueType() {
            return issueType;
        }
        
        public String getDetails() {
            return details;
        }
        
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
        
        public String getFormattedTimestamp() {
            return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        
        public boolean isResolved() {
            return resolved;
        }
        
        public void setResolved(boolean resolved) {
            this.resolved = resolved;
        }
        
        @Override
        public String toString() {
            return String.format("[%s] Help needed at %s - %s", 
                getFormattedTimestamp(), location, issueType);
        }
    }
}
