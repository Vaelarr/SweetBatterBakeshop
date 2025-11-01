package view;

import util.FlatLafUtil;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class SplashScreenView extends JWindow {
    private int duration;

    public SplashScreenView(int d) {
        duration = d;
        // ensure FlatLaf is initialized for styling of splash components
        FlatLafUtil.setupLookAndFeel();
    }

    public void showSplash() {
        JPanel content = (JPanel) getContentPane();
        content.setBackground(Color.WHITE);

        // Set the window to full screen
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screen.width;
        int height = screen.height;
        setBounds(0, 0, width, height);

        // Load and display logo
        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        try {
            BufferedImage logoImg = ImageIO.read(new File("images/store-logo.png"));
            // Scale the logo to fit nicely on full screen (max 600px width, maintain aspect ratio)
            int logoWidth = 600;
            int logoHeight = (int) ((double) logoImg.getHeight() / logoImg.getWidth() * logoWidth);
            Image scaledLogo = logoImg.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaledLogo));
        } catch (Exception e) {
            // Fallback to text if logo not found
            logoLabel.setText("<html><div style='text-align: center;'><span style='font-size: 64px; color: #A9907E;'>SWEET BATTER</span><br><span style='font-size: 64px; color: black;'>BAKESHOP</span></div></html>");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 64));
        }
        
        JLabel subtitle = new JLabel("Freshly Baked Goodness Every Day", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI Light", Font.PLAIN, 32));
        subtitle.setForeground(new Color(120, 120, 120));

        JLabel loading = new JLabel("Loading, please wait...", SwingConstants.CENTER);
        loading.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        loading.setForeground(new Color(100, 100, 100));

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setForeground(new Color(0xA9907E));
        progressBar.setBackground(new Color(240, 240, 240));
        progressBar.setPreferredSize(new Dimension(700, 15));
        progressBar.setBorderPainted(false);

        // Layout
        content.setLayout(new BorderLayout());
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(60, 50, 60, 50));
        
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        loading.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add decorative line
        JPanel decorativeLine = new JPanel();
        decorativeLine.setBackground(new Color(0xA9907E));
        decorativeLine.setPreferredSize(new Dimension(300, 4));
        decorativeLine.setMaximumSize(new Dimension(300, 4));
        decorativeLine.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(logoLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(decorativeLine);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(subtitle);
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(loading);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(progressBar);
        
        content.add(centerPanel, BorderLayout.CENTER);

        // Add shadow effect with compound border
        content.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xA9907E), 3),
            BorderFactory.createLineBorder(Color.WHITE, 5)
        ));

        // Display it
        setVisible(true);
        toFront();

        // Wait a little while using a timer instead of blocking
        Timer timer = new Timer(duration, e -> {
            setVisible(false);
            dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }
}
