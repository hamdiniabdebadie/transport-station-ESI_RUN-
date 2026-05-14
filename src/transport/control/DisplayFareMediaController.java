package transport.control;

import javafx.beans.property.SimpleDoubleProperty;
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
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import transport.core.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DisplayFareMediaController implements Initializable {
    
    @FXML private ComboBox<String> filterCombo;
    @FXML private TableView<TitreTransport> fareMediaTable;
    @FXML private TableColumn<TitreTransport, Integer> idColumn;
    @FXML private TableColumn<TitreTransport, String> typeColumn;
    @FXML private TableColumn<TitreTransport, LocalDate> dateColumn;
    @FXML private TableColumn<TitreTransport, Double> prixColumn;
    @FXML private TableColumn<TitreTransport, String> detailsColumn;
    
    private TransportService service = TransportService.getInstance();
    private ObservableList<TitreTransport> titresData = FXCollections.observableArrayList();

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
        // Already on fare media page, just refresh
        updateTableData();
    }

    @FXML
    private void handleValidation(ActionEvent event) {
        loadView(event, "ValidateFareMediaView.fxml");
    }

    @FXML
    private void handleComplaints(ActionEvent event) {
        loadView(event, "ComplaintSystemView.fxml");
    }

    @FXML
    private void handleBuyFareMedium(ActionEvent event) {
        loadView(event, "BuyFareMediaView.fxml");
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
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize filter combo
        filterCombo.getItems().addAll("Tous", "Tickets", "Cartes");
        filterCombo.getSelectionModel().selectFirst();
        
        // Initialize table columns
        idColumn.setCellValueFactory(cellData -> 
                new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        
        typeColumn.setCellValueFactory(cellData -> {
            TitreTransport titre = cellData.getValue();
            String type = titre instanceof Ticket ? "Ticket" : 
                         (titre instanceof CartePersonnelle ? 
                          "Carte " + ((CartePersonnelle) titre).getType() : "Inconnu");
            return new SimpleStringProperty(type);
        });
        
        dateColumn.setCellValueFactory(cellData -> 
                new SimpleObjectProperty<>(cellData.getValue().getDateAchat()));
        
        prixColumn.setCellValueFactory(cellData -> 
                new SimpleDoubleProperty(cellData.getValue().getPrix()).asObject());
        
        detailsColumn.setCellValueFactory(cellData -> {
            TitreTransport titre = cellData.getValue();
            String details = "";
            if (titre instanceof CartePersonnelle) {
                CartePersonnelle carte = (CartePersonnelle) titre;
                details = carte.getPersonne().getPrenom() + " " + carte.getPersonne().getNom();
            }
            return new SimpleStringProperty(details);
        });
        
        // Add listener to filter combo
        filterCombo.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> updateTableData());
        
        // Load initial data
        updateTableData();
    }
    
    private void updateTableData() {
        String filter = filterCombo.getValue();
        List<TitreTransport> titres = service.getTitres();
        
        if ("Tickets".equals(filter)) {
            titres = titres.stream()
                    .filter(t -> t instanceof Ticket)
                    .collect(Collectors.toList());
        } else if ("Cartes".equals(filter)) {
            titres = titres.stream()
                    .filter(t -> t instanceof CartePersonnelle)
                    .collect(Collectors.toList());
        }
        
        titresData.clear();
        titresData.addAll(titres);
        fareMediaTable.setItems(titresData);
    }
    
    @FXML
    private void handleRefresh(ActionEvent event) {
        updateTableData();
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

    public void handleValidateSelected(ActionEvent actionEvent) {
    }

    public void handleCloseDetails(ActionEvent actionEvent) {
    }
}
