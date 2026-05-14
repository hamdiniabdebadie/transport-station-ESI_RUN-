package transport.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import transport.core.Employe;
import transport.core.TransportService;
import transport.core.Fonction; // Change from TypeFonction to Fonction
import transport.core.Usager;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddUserController implements Initializable {
    
    @FXML private RadioButton usagerRadio;
    @FXML private RadioButton employeRadio;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private DatePicker dateNaissancePicker;
    @FXML private CheckBox handicapCheck;
    @FXML private Label matriculeLabel;
    @FXML private TextField matriculeField;
    @FXML private Label fonctionLabel;
    @FXML private ComboBox<Fonction> fonctionCombo; // Change from TypeFonction to Fonction
    
    private TransportService service = TransportService.getInstance();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize the function combo box
        fonctionCombo.getItems().addAll(Fonction.values());
        fonctionCombo.getSelectionModel().selectFirst();
        
        // Set today's date as default
        dateNaissancePicker.setValue(LocalDate.now().minusYears(30));
        
        // Add listener to show/hide employee-specific fields
        employeRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            matriculeLabel.setVisible(newVal);
            matriculeField.setVisible(newVal);
            fonctionLabel.setVisible(newVal);
            fonctionCombo.setVisible(newVal);
        });
    }
    
    @FXML
    private void handleAddUser(ActionEvent event) {
        try {
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            LocalDate dateNaissance = dateNaissancePicker.getValue();
            boolean handicap = handicapCheck.isSelected();
            
            if (nom.isEmpty() || prenom.isEmpty() || dateNaissance == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Champs obligatoires", 
                          "Veuillez remplir tous les champs obligatoires.");
                return;
            }
            
            if (employeRadio.isSelected()) {
                String matricule = matriculeField.getText().trim();
                Fonction fonction = fonctionCombo.getValue();
                
                if (matricule.isEmpty() || fonction == null) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Champs obligatoires", 
                              "Veuillez remplir tous les champs obligatoires pour l'employé.");
                    return;
                }
                
                Employe employe = new Employe(nom, prenom, dateNaissance, handicap, matricule, fonction);
                service.ajouterPersonne(employe);
                
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Employé ajouté", 
                          "L'employé a été ajouté avec succès.");
            } else {
                Usager usager = new Usager(nom, prenom, dateNaissance, handicap);
                service.ajouterPersonne(usager);
                
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Usager ajouté", 
                          "L'usager a été ajouté avec succès.");
            }
            
            clearFields();
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout", 
                      "Une erreur est survenue: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        clearFields();
    }
    
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/transport/ui/WelcomeView.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Add missing navigation methods and fix the loadView method
    @FXML
    private void handleHome(ActionEvent event) {
        loadView(event, "DashboardView.fxml");
    }

    @FXML
    private void handleUsers(ActionEvent event) {
        loadView(event, "UsersView.fxml");
    }

    @FXML
    private void handleFareMedia(ActionEvent event) {
        loadView(event, "DisplayFareMediaView.fxml");
    }

    @FXML
    private void handleValidation(ActionEvent event) {
        loadView(event, "ValidateFareMediaView.fxml");
    }

    @FXML
    private void handleComplaints(ActionEvent event) {
        loadView(event, "ComplaintSystemView.fxml");
    }

    private void loadView(ActionEvent event, String viewName) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/transport/ui/" + viewName));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1024, 768);
            scene.getStylesheets().add(getClass().getResource("/transport/ui/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Show error alert to make debugging easier
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Failed to load view: " + viewName);
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    private void clearFields() {
        nomField.clear();
        prenomField.clear();
        dateNaissancePicker.setValue(LocalDate.now().minusYears(30));
        handicapCheck.setSelected(false);
        matriculeField.clear();
        fonctionCombo.getSelectionModel().selectFirst();
        usagerRadio.setSelected(true);
    }
    
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
