import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.UIManager;

/**
 * Simple utility to initialize FlatLaf Look & Feel in an idempotent way.
 * Keep this call cheap and safe to invoke from multiple view constructors.
 */
public final class FlatLafUtil {
    private static boolean initialized = false;

    private FlatLafUtil() {}

    public static synchronized void setupLookAndFeel() {
        if (initialized) return;
        try {
            // Use FlatLightLaf as the default theme. This will be resolved from Maven dependencies.
            FlatLightLaf.setup();
            // Optionally adjust global UI defaults here if desired, e.g. fonts or scaling.
            UIManager.put("Button.focusPainted", Boolean.FALSE);
            initialized = true;
        } catch (Exception ex) {
            System.err.println("FlatLaf initialization failed: " + ex.getMessage());
        }
    }
}
