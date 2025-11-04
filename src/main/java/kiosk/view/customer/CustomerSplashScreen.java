package kiosk.view.customer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import kiosk.view.BakeryTheme;


public class CustomerSplashScreen extends JWindow {
    
    private final Color PRIMARY_COLOR = BakeryTheme.PRIMARY_COLOR;
    private final Color ACCENT_COLOR = BakeryTheme.ACCENT_COLOR;
    private final Color BACKGROUND_COLOR = BakeryTheme.CARD_COLOR;
    
    private JProgressBar progressBar;
    private JLabel statusLabel;
    
    public CustomerSplashScreen() {
        setSize(600, 400);
        setLocationRelativeTo(null);
        
        // Create main panel
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, BACKGROUND_COLOR,
                    0, getHeight(), BakeryTheme.SOFT_CREAM
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2));
        
        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        // Logo/Icon area
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        JLabel logoLabel = new JLabel("🎂");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 100));
        logoPanel.add(logoLabel);
        
        // Title
        JLabel titleLabel = new JLabel("Sweet Batter Bakeshop");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Custom Orders Portal");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        subtitleLabel.setForeground(ACCENT_COLOR);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Tagline
        JLabel taglineLabel = new JLabel("Crafting Sweet Memories, One Order at a Time");
        taglineLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        taglineLabel.setForeground(BakeryTheme.TEXT_LIGHT);
        taglineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(ACCENT_COLOR);
        progressBar.setBackground(BakeryTheme.BACKGROUND_COLOR);
        progressBar.setMaximumSize(new Dimension(400, 25));
        progressBar.setPreferredSize(new Dimension(400, 25));
        progressBar.setBorder(BorderFactory.createLineBorder(BakeryTheme.BORDER_LIGHT, 1));
        
        JPanel progressPanel = new JPanel();
        progressPanel.setOpaque(false);
        progressPanel.add(progressBar);
        
        // Status label
        statusLabel = new JLabel("Initializing...");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(BakeryTheme.TEXT_LIGHT);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Version label
        JLabel versionLabel = new JLabel("Version 1.0.0");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        versionLabel.setForeground(BakeryTheme.TEXT_LIGHT);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add components with spacing
        contentPanel.add(logoPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(taglineLabel);
        contentPanel.add(Box.createVerticalStrut(40));
        contentPanel.add(progressPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(statusLabel);
        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(versionLabel);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
        
        // Round corners
        setShape(new RoundRectangle2D.Double(0, 0, 600, 400, 20, 20));
    }
    
    /**
     * Update progress bar value
     */
    public void setProgress(int value) {
        progressBar.setValue(value);
    }
    
    /**
     * Update status message
     */
    public void setStatus(String status) {
        statusLabel.setText(status);
    }
    
    /**
     * Show splash screen and simulate loading process
     */
    public void showSplashAndLoad(Runnable onComplete) {
        setVisible(true);
        
        // Simulate loading in background thread
        new Thread(() -> {
            try {
                // Loading database connection
                setStatus("Connecting to database...");
                setProgress(20);
                Thread.sleep(300);
                
                // Loading products
                setStatus("Loading products...");
                setProgress(40);
                Thread.sleep(300);
                
                // Loading theme
                setStatus("Applying theme...");
                setProgress(60);
                Thread.sleep(300);
                
                // Initializing components
                setStatus("Initializing components...");
                setProgress(80);
                Thread.sleep(300);
                
                // Ready
                setStatus("Ready!");
                setProgress(100);
                Thread.sleep(400);
                
                // Close splash and run completion callback
                SwingUtilities.invokeLater(() -> {
                    setVisible(false);
                    dispose();
                    if (onComplete != null) {
                        onComplete.run();
                    }
                });
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}


