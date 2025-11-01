package main.java.kiosk.util;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGUniverse;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;

/**
 * Utility class for loading and rendering SVG icons
 * Uses SVG Salamander library for high-quality vector graphics
 */
public class SVGIconUtil {
    
    private static final SVGUniverse svgUniverse = new SVGUniverse();
    
    /**
     * Load an SVG icon from resources and render it at the specified size
     * 
     * @param resourcePath Path to SVG file in resources (e.g., "/icons/cart.svg")
     * @param width Desired width in pixels
     * @param height Desired height in pixels
     * @return ImageIcon containing the rendered SVG
     */
    public static ImageIcon loadSVGIcon(String resourcePath, int width, int height) {
        return loadSVGIcon(resourcePath, width, height, null);
    }
    
    /**
     * Load an SVG icon with custom color tinting
     * 
     * @param resourcePath Path to SVG file in resources
     * @param width Desired width in pixels
     * @param height Desired height in pixels
     * @param tintColor Color to tint the icon (null for original colors)
     * @return ImageIcon containing the rendered SVG
     */
    public static ImageIcon loadSVGIcon(String resourcePath, int width, int height, Color tintColor) {
        try {
            URL resource = SVGIconUtil.class.getResource(resourcePath);
            if (resource == null) {
                System.err.println("SVG resource not found: " + resourcePath);
                return createPlaceholderIcon(width, height);
            }
            
            URI svgURI = resource.toURI();
            SVGDiagram diagram = svgUniverse.getDiagram(svgURI);
            
            if (diagram == null) {
                System.err.println("Failed to load SVG diagram: " + resourcePath);
                return createPlaceholderIcon(width, height);
            }
            
            // Create buffered image to render SVG
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            
            // Enable anti-aliasing for smooth rendering
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            
            // Calculate scale to fit the icon
            float scaleX = (float) width / diagram.getWidth();
            float scaleY = (float) height / diagram.getHeight();
            float scale = Math.min(scaleX, scaleY);
            
            // Center the icon
            float offsetX = (width - diagram.getWidth() * scale) / 2;
            float offsetY = (height - diagram.getHeight() * scale) / 2;
            
            g2d.translate(offsetX, offsetY);
            g2d.scale(scale, scale);
            
            // Apply tint color if specified
            if (tintColor != null) {
                g2d.setColor(tintColor);
                g2d.setComposite(AlphaComposite.SrcAtop);
            }
            
            // Render the SVG
            diagram.render(g2d);
            g2d.dispose();
            
            return new ImageIcon(image);
            
        } catch (Exception e) {
            System.err.println("Error loading SVG icon: " + resourcePath);
            e.printStackTrace();
            return createPlaceholderIcon(width, height);
        }
    }
    
    /**
     * Create a simple placeholder icon when SVG loading fails
     */
    private static ImageIcon createPlaceholderIcon(int width, int height) {
        BufferedImage placeholder = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = placeholder.createGraphics();
        g2d.setColor(new Color(200, 200, 200));
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(new Color(150, 150, 150));
        g2d.drawRect(0, 0, width - 1, height - 1);
        g2d.drawLine(0, 0, width, height);
        g2d.drawLine(0, height, width, 0);
        g2d.dispose();
        return new ImageIcon(placeholder);
    }
    
    /**
     * Create a button with an SVG icon
     * 
     * @param text Button text
     * @param iconPath Path to SVG icon
     * @param iconSize Size of the icon (square)
     * @return JButton with SVG icon
     */
    public static JButton createIconButton(String text, String iconPath, int iconSize) {
        JButton button = new JButton(text);
        ImageIcon icon = loadSVGIcon(iconPath, iconSize, iconSize);
        button.setIcon(icon);
        return button;
    }
    
    /**
     * Create a button with an SVG icon and custom color
     * 
     * @param text Button text
     * @param iconPath Path to SVG icon
     * @param iconSize Size of the icon (square)
     * @param iconColor Color to tint the icon
     * @return JButton with colored SVG icon
     */
    public static JButton createIconButton(String text, String iconPath, int iconSize, Color iconColor) {
        JButton button = new JButton(text);
        ImageIcon icon = loadSVGIcon(iconPath, iconSize, iconSize, iconColor);
        button.setIcon(icon);
        return button;
    }
    
    /**
     * Create a label with an SVG icon
     * 
     * @param text Label text
     * @param iconPath Path to SVG icon
     * @param iconSize Size of the icon (square)
     * @return JLabel with SVG icon
     */
    public static JLabel createIconLabel(String text, String iconPath, int iconSize) {
        JLabel label = new JLabel(text);
        ImageIcon icon = loadSVGIcon(iconPath, iconSize, iconSize);
        label.setIcon(icon);
        return label;
    }
}
