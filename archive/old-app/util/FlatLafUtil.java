package util;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.UIManager;


public final class FlatLafUtil {
    private static boolean initialized = false;

    private FlatLafUtil() {}

    public static synchronized void setupLookAndFeel() {
        if (initialized) return;
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.focusPainted", Boolean.FALSE);
            initialized = true;
        } catch (Exception ex) {
            System.err.println("FlatLaf initialization failed: " + ex.getMessage());
        }
    }
}
