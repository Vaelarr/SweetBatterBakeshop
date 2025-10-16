import controller.ApplicationController;

public class App {
    public static void main(String[] args) throws Exception {
        // Start the application using MVC architecture
        ApplicationController controller = new ApplicationController();
        controller.start();
    }
}
