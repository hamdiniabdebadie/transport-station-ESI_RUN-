package transport.control;

import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.util.Callback;
import transport.core.*;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class UsersController implements Initializable {
    
    @FXML private ComboBox<String> filterCombo;
    @FXML private TextField searchField;
    @FXML private TableView<Personne> usersTable;
    @FXML private TableColumn<Personne, String> idColumn;
    @FXML private TableColumn<Personne, String> nomColumn;
    @FXML private TableColumn<Personne, String> prenomColumn;
    @FXML private TableColumn<Personne, String> typeColumn;
    @FXML private TableColumn<Personne, Integer> ageColumn;
    @FXML private TableColumn<Personne, Boolean> handicapColumn;
    @FXML private TableColumn<Personne, String> detailsColumn;
    @FXML private TableColumn<Personne, Personne> actionsColumn;
    
    @FXML private VBox userDetailsPanel;
    @FXML private Label detailIdLabel;
    @FXML private Label detailNomLabel;
    @FXML private Label detailPrenomLabel;
    @FXML private Label detailDateNaissanceLabel;
    @FXML private Label detailAgeLabel;
    @FXML private Label detailHandicapLabel;
    @FXML private Label detailMatriculeTitle;
    @FXML private Label detailMatriculeLabel;
    @FXML private Label detailFonctionTitle;
    @FXML private Label detailFonctionLabel;
    
    private TransportService service = TransportService.getInstance();
    private ObservableList<Personne> userData = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize filter combo
        filterCombo.getItems().addAll("Tous", "Usagers", "Employés");
        filterCombo.getSelectionModel().selectFirst();
        
        // Initialize table columns
        idColumn.setCellValueFactory(cellData -> {
            Personne p = cellData.getValue();
            if (p instanceof Usager) {
                return new SimpleStringProperty("U-" + ((Usager) p).getId());
            } else if (p instanceof Employe) {
                return new SimpleStringProperty("E-" + ((Employe) p).getMatricule());
            }
            return new SimpleStringProperty("N/A");
        });
        
        nomColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getNom()));
        
        prenomColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getPrenom()));
        
        typeColumn.setCellValueFactory(cellData -> {
            Personne p = cellData.getValue();
            if (p instanceof Usager) {
                return new SimpleStringProperty("Usager");
            } else if (p instanceof Employe) {
                return new SimpleStringProperty("Employé");
            }
            return new SimpleStringProperty("Inconnu");
        });
        
        ageColumn.setCellValueFactory(cellData -> 
                new SimpleIntegerProperty(cellData.getValue().getAge()).asObject());
        
        handicapColumn.setCellValueFactory(cellData -> 
                new SimpleBooleanProperty(cellData.getValue().estHandicape()));
        
        detailsColumn.setCellValueFactory(cellData -> {
            Personne p = cellData.getValue();
            String details = "";
            if (p instanceof Employe) {
                Employe e = (Employe) p;
                details = "Fonction: " + e.getFonction();
            }
            return new SimpleStringProperty(details);
        });
        
        // Setup actions column with buttons
        actionsColumn.setCellFactory(createActionsColumnCellFactory());
        
        // Add listeners
        filterCombo.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> updateTableData());
        
        searchField.textProperty().addListener(
                (obs, oldVal, newVal) -> updateTableData());
        
        // Load initial data
        updateTableData();
    }
    
    private Callback<TableColumn<Personne, Personne>, TableCell<Personne, Personne>> createActionsColumnCellFactory() {
        return param -> new TableCell<Personne, Personne>() {
            private final Button detailsButton = new Button("Détails");
            
            {
                detailsButton.getStyleClass().add("button");
                detailsButton.setOnAction(event -> {
                    Personne personne = getTableView().getItems().get(getIndex());
                    showUserDetails(personne);
                });
            }
            
            @Override
            protected void updateItem(Personne personne, boolean empty) {
                super.updateItem(personne, empty);
                
                if (personne == null || empty) {
                    setGraphic(null);
                } else {
                    setGraphic(detailsButton);
                }
            }
        };
    }
    
    private void showUserDetails(Personne personne) {
        // Set user details
        if (personne instanceof Usager) {
            Usager u = (Usager) personne;
            detailIdLabel.setText("U-" + u.getId());
            detailMatriculeTitle.setVisible(false);
            detailMatriculeLabel.setVisible(false);
            detailFonctionTitle.setVisible(false);
            detailFonctionLabel.setVisible(false);
        } else if (personne instanceof Employe) {
            Employe e = (Employe) personne;
            detailIdLabel.setText("E-" + e.getMatricule());
            detailMatriculeTitle.setVisible(true);
            detailMatriculeLabel.setVisible(true);
            detailMatriculeLabel.setText(e.getMatricule());
            detailFonctionTitle.setVisible(true);
            detailFonctionLabel.setVisible(true);
            detailFonctionLabel.setText(e.getFonction().toString());
        }
        
        detailNomLabel.setText(personne.getNom());
        detailPrenomLabel.setText(personne.getPrenom());
        detailDateNaissanceLabel.setText(personne.getDateNaissance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        detailAgeLabel.setText(personne.getAge() + " ans");
        detailHandicapLabel.setText(personne.estHandicape() ? "Oui" : "Non");
        
        // Show the panel
        userDetailsPanel.setVisible(true);
        userDetailsPanel.setManaged(true);
    }
    
    @FXML
    private void handleCloseDetails() {
        userDetailsPanel.setVisible(false);
        userDetailsPanel.setManaged(false);
    }
    
    private void updateTableData() {
        String filter = filterCombo.getValue();
        String search = searchField.getText().toLowerCase();
        
        userData.clear();
        
        for (Personne p : service.getPersonnes()) {
            boolean matchesFilter = "Tous".equals(filter) ||
                    ("Usagers".equals(filter) && p instanceof Usager) ||
                    ("Employés".equals(filter) && p instanceof Employe);
            
            boolean matchesSearch = search.isEmpty() ||
                    p.getNom().toLowerCase().contains(search) ||
                    p.getPrenom().toLowerCase().contains(search);
            
            if (matchesFilter && matchesSearch) {
                userData.add(p);
            }
        }
        
        usersTable.setItems(userData);
    }
    
    @FXML
    private void handleAddUser(ActionEvent event) {
        loadView(event, "AddUserView.fxml");
    }
    
    @FXML
    private void handleHome(ActionEvent event) {
        loadView(event, "DashboardView.fxml");
    }
    
    @FXML
    private void handleUsers(ActionEvent event) {
        // Already on users page, just refresh
        updateTableData();
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
}
