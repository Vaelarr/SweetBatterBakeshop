
import controller.ApplicationController;
import util.FlatLafUtil;

public class App {
    public static void main(String[] args) throws Exception {
        // Initialize FlatLaf (idempotent)
        FlatLafUtil.setupLookAndFeel();
        // Start the application using MVC architecture
        ApplicationController controller = new ApplicationController();
        controller.start();
    }
}
