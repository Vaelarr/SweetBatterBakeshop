package controller;

import view.SplashScreenView;
import view.MainView;
import javax.swing.*;

public class ApplicationController {
    
    public void start() {
        // Show splash screen first
        SplashScreenView splash = new SplashScreenView(3000);
        splash.showSplash();
        
        // Delay main window to show after splash
        Timer timer = new Timer(3000, e -> {
            SwingUtilities.invokeLater(() -> {
                MainView mainFrame = new MainView();
                mainFrame.setVisible(true);
            });
        });
        timer.setRepeats(false);
        timer.start();
    }
}
