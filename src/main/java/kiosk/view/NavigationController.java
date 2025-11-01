package main.java.kiosk.view;

/**
 * Interface for navigation between different views in the kiosk
 */
public interface NavigationController {
    /**
     * Navigate to the main page
     */
    void showMainPage();
    
    /**
     * Navigate to the cakes page
     */
    void showCakesPage();
    
    /**
     * Navigate to the pastries page
     */
    void showPastriesPage();
    
    /**
     * Navigate to the breads page
     */
    void showBreadsPage();
    
    /**
     * Navigate to the beverages page
     */
    void showBeveragesPage();
    
    /**
     * Navigate to the cart page
     */
    void showCartPage();
    
    /**
     * Navigate to the admin panel
     */
    void showAdminPanel();
    
    /**
     * Update all cart counters across all pages
     */
    void updateAllCartCounters();
    
    /**
     * Exit the application
     */
    void exitApplication();
}
