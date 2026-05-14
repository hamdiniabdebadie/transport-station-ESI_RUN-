package transport.control;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import transport.core.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ComplaintSystemController implements Initializable {
    
    @FXML private ComboBox<Personne> personneCombo;
    @FXML private ComboBox<TypeReclamation> typeReclamationCombo;
    @FXML private RadioButton stationRadio;
    @FXML private RadioButton transportRadio;
    @FXML private ComboBox<Suspendable> cibleCombo;
    @FXML private TextArea descriptionArea;
    @FXML private Label submitResultLabel;
    
    @FXML private TableView<Reclamation> complaintsTable;
    @FXML private TableColumn<Reclamation, Integer> numeroColumn;
    @FXML private TableColumn<Reclamation, LocalDate> dateColumn;
    @FXML private TableColumn<Reclamation, String> typeColumn;
    @FXML private TableColumn<Reclamation, String> cibleColumn;
    @FXML private TableColumn<Reclamation, String> personneColumn;
    @FXML private TableColumn<Reclamation, String> descriptionColumn;
    
    @FXML private VBox warningBox;
    @FXML private Label warningLabel;
    
    private TransportService service = TransportService.getInstance();
    private ObservableList<Reclamation> reclamationsData = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize complaint type combo
        typeReclamationCombo.getItems().addAll(TypeReclamation.values());
        typeReclamationCombo.getSelectionModel().selectFirst();
        
        // Initialize person combo
        personneCombo.getItems().addAll(service.getPersonnes());
        personneCombo.setConverter(new StringConverter<Personne>() {
            @Override
            public String toString(Personne personne) {
                return personne == null ? "" : personne.getPrenom() + " " + personne.getNom();
            }
            
            @Override
            public Personne fromString(String string) {
                return null; // Not needed for ComboBox
            }
        });
        
        if (!personneCombo.getItems().isEmpty()) {
            personneCombo.getSelectionModel().selectFirst();
        }
        
        // Add listeners for target type
        stationRadio.selectedProperty().addListener((obs, oldVal, newVal) -> updateTargetCombo());
        
        // Initialize target combo
        updateTargetCombo();
        
        // Initialize table columns
        numeroColumn.setCellValueFactory(cellData -> 
                new SimpleIntegerProperty(cellData.getValue().getNumero()).asObject());
        
        dateColumn.setCellValueFactory(cellData -> 
                new SimpleObjectProperty<>(cellData.getValue().getDate()));
        
        typeColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getType().toString()));
        
        cibleColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getCible().toString()));
        
        personneColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getPersonne().toString()));
        
        descriptionColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getDescription()));
        
        // Load initial data
        updateComplaintsTable();
    }
    
    private void updateTargetCombo() {
        cibleCombo.getItems().clear();
        
        if (stationRadio.isSelected()) {
            cibleCombo.getItems().addAll(service.getStations());
        } else {
            cibleCombo.getItems().addAll(service.getMoyensTransport());
        }
        
        if (!cibleCombo.getItems().isEmpty()) {
            cibleCombo.getSelectionModel().selectFirst();
        }
    }
    
    @FXML
    private void handleSubmitComplaint(ActionEvent event) {
        try {
            Personne personne = personneCombo.getValue();
            TypeReclamation type = typeReclamationCombo.getValue();
            Suspendable cible = cibleCombo.getValue();
            String description = descriptionArea.getText().trim();
            
            if (personne == null || type == null || cible == null || description.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Champs obligatoires", 
                          "Veuillez remplir tous les champs obligatoires.");
                return;
            }
            
            Reclamation reclamation = new Reclamation(personne, type, cible, description);
            service.soumettreReclamation(reclamation);
            
            submitResultLabel.setText("Réclamation soumise avec succès!");
            clearFields();
            updateComplaintsTable();
            checkSuspensions();
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de soumission", 
                      "Une erreur est survenue: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleClear(ActionEvent event) {
        clearFields();
    }
    
    @FXML
    private void handleRefreshComplaints(ActionEvent event) {
        updateComplaintsTable();
        checkSuspensions();
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

    // Fix the loadView method to use the correct path for FXML files and add error handling
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

    // Add missing navigation methods
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
        // Already on complaints page, just refresh
        updateComplaintsTable();
        checkSuspensions();
    }
    
    private void clearFields() {
        if (!personneCombo.getItems().isEmpty()) {
            personneCombo.getSelectionModel().selectFirst();
        }
        if (!typeReclamationCombo.getItems().isEmpty()) {
            typeReclamationCombo.getSelectionModel().selectFirst();
        }
        stationRadio.setSelected(true);
        updateTargetCombo();
        descriptionArea.clear();
        submitResultLabel.setText("");
    }
    
    private void updateComplaintsTable() {
        reclamationsData.clear();
        reclamationsData.addAll(service.getReclamations());
        complaintsTable.setItems(reclamationsData);
    }
    
    private void checkSuspensions() {
        List<Suspendable> suspended = new ArrayList<>();
        
        // Check stations
        for (Station station : service.getStations()) {
            if (station.estSuspendu()) {
                suspended.add(station);
            }
        }
        
        // Check transport means
        for (MoyenTransport moyen : service.getMoyensTransport()) {
            if (moyen.estSuspendu()) {
                suspended.add(moyen);
            }
        }
        
        if (!suspended.isEmpty()) {
            warningBox.setVisible(true);
            StringBuilder sb = new StringBuilder();
            sb.append("Les éléments suivants sont suspendus en raison de nombreuses réclamations:\n\n");
            
            for (Suspendable s : suspended) {
                sb.append("- ").append(s.toString())
                  .append(" (").append(service.getNombreReclamations(s)).append(" réclamations)\n");
            }
            
            warningLabel.setText(sb.toString());
        } else {
            warningBox.setVisible(false);
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
