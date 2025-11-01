package util;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

/**
 * Small utility to load icons from the project's images/ folder (SVG preferred) or provide a simple placeholder.
 * It will attempt in order:
 *  1) classpath /images/{name}.svg -> FlatSVGIcon
 *  2) working-dir images/{name}.svg -> FlatSVGIcon
 *  3) classpath /images/{name}.png -> ImageIcon
 *  4) working-dir images/{name}.png -> ImageIcon
 *  5) placeholder drawn icon
 */
public final class IconUtil {
    private IconUtil() {}

    public static Icon getIcon(String name, int width, int height) {
        // 1) classpath SVG (FlatSVGIcon expects a resource path string)
        String svgResourcePath = "images/" + name + ".svg";
        URL svgUrl = IconUtil.class.getResource("/" + svgResourcePath);
        try {
            if (svgUrl != null) {
                return new FlatSVGIcon(svgResourcePath, width, height);
            }

            // 2) filesystem SVG (pass absolute file path)
            File svgFile = new File("images" + File.separator + name + ".svg");
            if (svgFile.exists()) {
                return new FlatSVGIcon(svgFile.getAbsolutePath(), width, height);
            }
        } catch (Exception ex) {
            // fall through to PNG attempts
            System.err.println("Failed to load SVG for " + name + ": " + ex.getMessage());
        }

        // 3) classpath PNG
        String pngResource = "/images/" + name + ".png";
        URL pngUrl = IconUtil.class.getResource(pngResource);
        Image img = null;
        if (pngUrl != null) {
            img = new ImageIcon(pngUrl).getImage();
        } else {
            // 4) filesystem PNG
            File pngFile = new File("images" + File.separator + name + ".png");
            if (pngFile.exists()) {
                img = new ImageIcon(pngFile.getAbsolutePath()).getImage();
            }
        }

        if (img != null) {
            Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }

        // 5) Fallback: placeholder icon
        return new PlaceholderIcon(width, height);
    }

    private static class PlaceholderIcon implements Icon {
        private final int w;
        private final int h;
        private final Color bg = new Color(0xA9907E);

        PlaceholderIcon(int w, int h) { this.w = w; this.h = h; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillOval(x, y, w, h);
            g2.dispose();
        }

        @Override
        public int getIconWidth() { return w; }

        @Override
        public int getIconHeight() { return h; }
    }
}
