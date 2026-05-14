package transport.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import transport.core.TransportService;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Initialize the transport service
            TransportService.getInstance();

            // Load the main view - fix the path
            Parent root = FXMLLoader.load(getClass().getResource("/transport/ui/DashboardView.fxml"));

            // Set up the scene
            Scene scene = new Scene(root, 1024, 768);
            scene.getStylesheets().add(getClass().getResource("/transport/ui/styles.css").toExternalForm());

            // Configure the stage
            primaryStage.setTitle("ESI-RUN Transport System");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Application Error", "Failed to start application",
                          "Error: " + e.getMessage() + "\n\nCheck the console for more details.");
        }
    }

    @Override
    public void stop() {
        // Save data when application closes
        try {
            TransportService.getInstance().saveToFile();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
