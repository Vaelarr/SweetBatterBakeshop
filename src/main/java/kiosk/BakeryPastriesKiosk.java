package main.java.kiosk;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import main.java.kiosk.controller.CartController;
import main.java.kiosk.controller.InventoryController;
import main.java.kiosk.controller.SalesController;
import main.java.kiosk.view.KioskMainPage;
import main.java.kiosk.view.ModernBakeryTheme;
import main.java.kiosk.view.SplashScreen;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Main application class for SweetBatter Bakeshop Kiosk
 * Uses MVC architecture with serialization and generics
 * Features modern FlatLaf UI with bakery-themed colors
 */
public class BakeryPastriesKiosk {
    public static void main(String[] args) {
        // Show splash screen
        SplashScreen splash = new SplashScreen();
        
        // Initialize in background thread
        new Thread(() -> {
            try {
                // Step 1: Initialize controllers
                splash.updateProgress(20, "Loading data...");
                initializeControllers();
                Thread.sleep(500);
                
                // Step 2: Initialize UI theme
                splash.updateProgress(40, "Setting up theme...");
                SwingUtilities.invokeAndWait(() -> {
                    try {
                        // Initialize modern bakery theme with FlatLaf
                        System.setProperty("flatlaf.useWindowDecorations", "false");
                        System.setProperty("flatlaf.animation", "true");
                        System.setProperty("flatlaf.animatedLafChange", "true");
                        
                        ModernBakeryTheme.setup();
                        FlatLightLaf.setup();
                        UIManager.setLookAndFeel(new FlatLightLaf());
                        FlatLaf.updateUI();
                        
                        System.out.println("✓ Modern Bakery Theme initialized successfully");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                Thread.sleep(500);
                
                // Step 3: Create main window
                splash.updateProgress(60, "Creating interface...");
                Thread.sleep(500);
                
                // Step 4: Initialize components
                splash.updateProgress(80, "Initializing kiosk...");
                Thread.sleep(500);
                
                // Step 5: Finalize
                splash.updateProgress(100, "Ready!");
                Thread.sleep(300);
                
                // Launch main application
                SwingUtilities.invokeLater(() -> {
                    try {
                        // Create the main window (without showing it yet)
                        KioskMainPage mainPage = new KioskMainPage(false);
                        
                        // Configure for fullscreen
                        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                        GraphicsDevice gd = ge.getDefaultScreenDevice();
                        
                        // Set to fullscreen
                        if (gd.isFullScreenSupported()) {
                            gd.setFullScreenWindow(mainPage);
                        } else {
                            // Fallback to maximized if fullscreen not supported
                            mainPage.setExtendedState(JFrame.MAXIMIZED_BOTH);
                            mainPage.setVisible(true);
                        }
                        
                        // Close splash screen after a brief delay
                        new Thread(() -> {
                            try {
                                Thread.sleep(200);
                                splash.closeSplash();
                            } catch (InterruptedException e) {
                                splash.closeSplash();
                            }
                        }).start();
                        
                        // Print debugging information
                        System.out.println("=".repeat(60));
                        System.out.println("  SweetBatter Bakeshop Kiosk - Initialized Successfully");
                        System.out.println("=".repeat(60));
                        System.out.println("Mode: Full-Screen Kiosk");
                        System.out.println("MVC Architecture: Enabled");
                        System.out.println("Inventory items loaded: " + InventoryController.getInstance().getAllItems().size());
                        System.out.println("Cart items: " + CartController.getInstance().getTotalItems());
                        System.out.println("Sales transactions: " + SalesController.getInstance().getAllTransactions().size());
                        System.out.println("=".repeat(60));
                        
                        // Add shutdown hook to save data on exit
                        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                            saveAllData();
                        }));
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                        splash.closeSplash();
                    }
                });
                
            } catch (Exception e) {
                e.printStackTrace();
                splash.closeSplash();
            }
        }).start();
    }
    
    /**
     * Initialize controllers and load persisted data
     */
    private static void initializeControllers() {
        System.out.println("Initializing MVC controllers...");
        
        try {
            // Try to load existing data
            InventoryController.getInstance().load();
            System.out.println("Inventory data loaded successfully");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing inventory data found, using sample data");
        }
        
        try {
            CartController.getInstance().load();
            System.out.println("Cart data loaded successfully");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing cart data found, starting with empty cart");
        }
        
        try {
            SalesController.getInstance().load();
            System.out.println("Sales data loaded successfully");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing sales data found, starting fresh");
        }
    }
    
    /**
     * Save all data to persistent storage
     */
    private static void saveAllData() {
        System.out.println("Saving application data...");
        
        try {
            InventoryController.getInstance().save();
            System.out.println("Inventory data saved");
        } catch (IOException e) {
            System.err.println("Failed to save inventory data: " + e.getMessage());
        }
        
        try {
            CartController.getInstance().save();
            System.out.println("Cart data saved");
        } catch (IOException e) {
            System.err.println("Failed to save cart data: " + e.getMessage());
        }
        
        try {
            SalesController.getInstance().save();
            System.out.println("Sales data saved");
        } catch (IOException e) {
            System.err.println("Failed to save sales data: " + e.getMessage());
        }
        
        System.out.println("Application shutdown complete");
    }
}
