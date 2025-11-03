package kiosk.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Splash screen for SweetBatter Bakeshop Kiosk
 * Displays logo and loading animation during application startup
 */
public class SplashScreen extends JWindow {
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JLabel logoLabel;
    private static final int SPLASH_WIDTH = 600;
    private static final int SPLASH_HEIGHT = 500;
    
    // Modern Bakery theme colors - Updated
    private static final Color PRIMARY_COLOR = new Color(232, 146, 124);  // Warm Coral
    private static final Color ACCENT_COLOR = new Color(212, 137, 109);   // Terracotta
    private static final Color BACKGROUND_COLOR = new Color(255, 250, 245); // Warm White
    private static final Color TEXT_COLOR = new Color(62, 39, 35);  // Dark Espresso
    
    public SplashScreen() {
        setSize(SPLASH_WIDTH, SPLASH_HEIGHT);
        setLocationRelativeTo(null);
        
        // Create rounded window effect
        setBackground(new Color(0, 0, 0, 0));
        
        // Main panel with rounded corners
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded rectangle background
                g2d.setColor(BACKGROUND_COLOR);
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));
                
                // Draw border
                g2d.setColor(PRIMARY_COLOR);
                g2d.setStroke(new BasicStroke(3));
                g2d.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, 30, 30));
                
                g2d.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Logo panel
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setOpaque(false);
        
        // Load bakery logo image from lib/images
        try {
            String logoPath = "lib/images/store-logo.png";
            java.io.File logoFile = new java.io.File(logoPath);
            if (logoFile.exists()) {
                ImageIcon logoIcon = new ImageIcon(logoPath);
                // Scale the logo to fit nicely in the splash screen
                Image scaledLogo = logoIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                logoLabel = new JLabel(new ImageIcon(scaledLogo));
            } else {
                // Fallback to text-based logo if image not found
                logoLabel = new JLabel("<html><div style='text-align: center;'>"
                        + "<span style='font-size: 72px; color: #CD853F;'>🧁</span><br>"
                        + "<span style='font-size: 42px; color: #654321; font-weight: bold;'>Sweet</span>"
                        + "<span style='font-size: 42px; color: #CD853F; font-weight: bold;'>Batter</span><br>"
                        + "<span style='font-size: 24px; color: #8B4513; font-style: italic;'>Bakeshop</span>"
                        + "</div></html>");
            }
        } catch (Exception e) {
            // Fallback to text-based logo on error
            logoLabel = new JLabel("<html><div style='text-align: center;'>"
                    + "<span style='font-size: 72px; color: #CD853F;'>🧁</span><br>"
                    + "<span style='font-size: 42px; color: #654321; font-weight: bold;'>Sweet</span>"
                    + "<span style='font-size: 42px; color: #CD853F; font-weight: bold;'>Batter</span><br>"
                    + "<span style='font-size: 24px; color: #8B4513; font-style: italic;'>Bakeshop</span>"
                    + "</div></html>");
        }
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        logoPanel.add(logoLabel, BorderLayout.CENTER);
        
        // Title panel with better spacing
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("SweetBatter Bakeshop");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Artisan Baked Goods & Delights");
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        subtitleLabel.setForeground(PRIMARY_COLOR);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        titlePanel.add(subtitleLabel);
        
        // Progress panel with enhanced styling
        JPanel progressPanel = new JPanel(new BorderLayout(10, 15));
        progressPanel.setOpaque(false);
        progressPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        statusLabel = new JLabel("Initializing...", JLabel.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        statusLabel.setForeground(TEXT_COLOR);
        
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setForeground(PRIMARY_COLOR);
        progressBar.setBackground(new Color(240, 240, 240));
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        progressBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2, true),
            BorderFactory.createEmptyBorder(3, 3, 3, 3)
        ));
        progressBar.setPreferredSize(new Dimension(500, 32));
        
        // Custom UI for modern look
        progressBar.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
            @Override
            protected void paintDeterminate(Graphics g, javax.swing.JComponent c) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Paint background
                g2d.setColor(progressBar.getBackground());
                g2d.fillRoundRect(2, 2, progressBar.getWidth() - 4, progressBar.getHeight() - 4, 8, 8);
                
                // Paint progress with gradient
                if (progressBar.getValue() > 0) {
                    int width = (int) ((progressBar.getWidth() - 4) * ((double) progressBar.getValue() / progressBar.getMaximum()));
                    GradientPaint gradient = new GradientPaint(
                        0, 0, PRIMARY_COLOR,
                        width, 0, ACCENT_COLOR
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRoundRect(2, 2, width, progressBar.getHeight() - 4, 8, 8);
                }
                
                // Paint string
                if (progressBar.isStringPainted()) {
                    paintString(g, 0, 0, progressBar.getWidth(), progressBar.getHeight(), 0, null);
                }
            }
        });
        
        progressPanel.add(statusLabel, BorderLayout.NORTH);
        progressPanel.add(progressBar, BorderLayout.CENTER);
        
        // Combine all panels with increased vertical spacing
        JPanel centerPanel = new JPanel(new BorderLayout(10, 30));
        centerPanel.setOpaque(false);
        centerPanel.add(logoPanel, BorderLayout.NORTH);
        centerPanel.add(titlePanel, BorderLayout.CENTER);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(progressPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setVisible(true);
    }
    
    /**
     * Update the progress and status message
     * @param progress Progress value (0-100)
     * @param status Status message to display
     */
    public void updateProgress(int progress, String status) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(progress);
            statusLabel.setText(status);
        });
    }
    
    /**
     * Close the splash screen with a fade effect
     */
    public void closeSplash() {
        SwingUtilities.invokeLater(() -> {
            // Simple fade out effect
            new Thread(() -> {
                try {
                    for (float opacity = 1.0f; opacity >= 0.0f; opacity -= 0.1f) {
                        Thread.sleep(30);
                        final float currentOpacity = opacity;
                        SwingUtilities.invokeLater(() -> setOpacity(currentOpacity));
                    }
                    dispose();
                } catch (InterruptedException e) {
                    dispose();
                }
            }).start();
        });
    }
}


