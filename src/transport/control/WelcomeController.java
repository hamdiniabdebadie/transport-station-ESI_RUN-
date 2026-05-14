package transport.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeController {
    
    @FXML
    private void handleAddUser(ActionEvent event) {
        loadView(event, "AddUserView.fxml");
    }
    
    @FXML
    private void handleBuyFareMedium(ActionEvent event) {
        loadView(event, "BuyFareMediaView.fxml");
    }
    
    @FXML
    private void handleDisplayFareMedia(ActionEvent event) {
        loadView(event, "DisplayFareMediaView.fxml");
    }
    
    @FXML
    private void handleValidateFareMedium(ActionEvent event) {
        loadView(event, "ValidateFareMediaView.fxml");
    }
    
    @FXML
    private void handleComplaintSystem(ActionEvent event) {
        loadView(event, "ComplaintSystemView.fxml");
    }
    
    private void loadView(ActionEvent event, String viewName) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/transport/ui/" + viewName));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
