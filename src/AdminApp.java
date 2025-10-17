import controller.AdminController;

public class AdminApp {
    public static void main(String[] args) {
        // Start the admin application
        javax.swing.SwingUtilities.invokeLater(() -> {
            AdminController adminController = new AdminController();
            adminController.showLoginDialog();
        });
    }
}
