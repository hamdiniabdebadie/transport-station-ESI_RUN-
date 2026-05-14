package transport.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import transport.core.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ValidateFareMediaController implements Initializable {
    
    @FXML private TextField idField;
    @FXML private DatePicker validationDatePicker;
    @FXML private VBox resultBox;
    @FXML private ImageView resultIcon;
    @FXML private Label resultLabel;
    @FXML private VBox detailsBox;
    @FXML private Label detailIdLabel;
    @FXML private Label detailTypeLabel;
    @FXML private Label detailDateLabel;
    @FXML private Label detailPrixLabel;
    @FXML private Label detailProprietaireTitle;
    @FXML private Label detailProprietaireLabel;
    @FXML private Label detailTypeCarteTitle;
    @FXML private Label detailTypeCarteLabel;
    
    private TransportService service = TransportService.getInstance();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set today's date as default
        validationDatePicker.setValue(LocalDate.now());
    }
    
    @FXML
    private void handleValidate(ActionEvent event) {
        try {
            String idText = idField.getText().trim();
            if (idText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "ID manquant", 
                          "Veuillez entrer l'ID du titre de transport.");
                return;
            }
            
            int id = Integer.parseInt(idText);
            LocalDate date = validationDatePicker.getValue();
            if (date == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Date manquante", 
                          "Veuillez sélectionner une date de validation.");
                return;
            }
            
            TitreTransport titre = service.getTitreById(id);
            if (titre == null) {
                resultBox.setVisible(true);
                resultBox.setManaged(true);
                resultIcon.setImage(new Image(getClass().getResourceAsStream("/transport/ui/images/error_icon.png")));
                resultLabel.setText("TITRE NON TROUVÉ");
                resultLabel.setTextFill(Color.RED);
                detailsBox.setVisible(false);
                return;
            }
            
            try {
                boolean valide = service.validerTitre(id, date);
                resultBox.setVisible(true);
                resultBox.setManaged(true);
                resultIcon.setImage(new Image(getClass().getResourceAsStream("/transport/ui/images/success_icon.png")));
                resultLabel.setText("TITRE VALIDE");
                resultLabel.setTextFill(Color.GREEN);
                detailsBox.setVisible(true);
                
                // Set details
                detailIdLabel.setText(String.valueOf(titre.getId()));
                detailDateLabel.setText(titre.getDateAchat().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                detailPrixLabel.setText(String.format("%.2f DA", titre.getPrix()));
                
                if (titre instanceof Ticket) {
                    detailTypeLabel.setText("Ticket");
                    detailProprietaireTitle.setVisible(false);
                    detailProprietaireLabel.setVisible(false);
                    detailTypeCarteTitle.setVisible(false);
                    detailTypeCarteLabel.setVisible(false);
                } else if (titre instanceof CartePersonnelle) {
                    CartePersonnelle carte = (CartePersonnelle) titre;
                    detailTypeLabel.setText("Carte personnelle");
                    detailProprietaireTitle.setVisible(true);
                    detailProprietaireLabel.setVisible(true);
                    detailProprietaireLabel.setText(carte.getPersonne().getPrenom() + " " + carte.getPersonne().getNom());
                    detailTypeCarteTitle.setVisible(true);
                    detailTypeCarteLabel.setVisible(true);
                    detailTypeCarteLabel.setText(carte.getType().toString());
                }
                
            } catch (TitreNonValideException e) {
                resultBox.setVisible(true);
                resultBox.setManaged(true);
                resultIcon.setImage(new Image(getClass().getResourceAsStream("/transport/ui/images/error_icon.png")));
                resultLabel.setText("TITRE NON VALIDE");
                resultLabel.setTextFill(Color.RED);
                detailsBox.setVisible(true);
                
                // Set details
                detailIdLabel.setText(String.valueOf(titre.getId()));
                detailDateLabel.setText(titre.getDateAchat().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                detailPrixLabel.setText(String.format("%.2f DA", titre.getPrix()));
                
                if (titre instanceof Ticket) {
                    detailTypeLabel.setText("Ticket - " + e.getMessage());
                    detailProprietaireTitle.setVisible(false);
                    detailProprietaireLabel.setVisible(false);
                    detailTypeCarteTitle.setVisible(false);
                    detailTypeCarteLabel.setVisible(false);
                } else if (titre instanceof CartePersonnelle) {
                    CartePersonnelle carte = (CartePersonnelle) titre;
                    detailTypeLabel.setText("Carte personnelle - " + e.getMessage());
                    detailProprietaireTitle.setVisible(true);
                    detailProprietaireLabel.setVisible(true);
                    detailProprietaireLabel.setText(carte.getPersonne().getPrenom() + " " + carte.getPersonne().getNom());
                    detailTypeCarteTitle.setVisible(true);
                    detailTypeCarteLabel.setVisible(true);
                    detailTypeCarteLabel.setText(carte.getType().toString());
                }
            }
            
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "ID invalide", 
                      "L'ID doit être un nombre entier.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de validation", 
                      "Une erreur est survenue: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCloseResult() {
        resultBox.setVisible(false);
        resultBox.setManaged(false);
    }
    
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
        // Already on validation page, just refresh
        idField.clear();
        validationDatePicker.setValue(LocalDate.now());
        resultBox.setVisible(false);
        resultBox.setManaged(false);
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
    
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
