package transport.control;

import javafx.collections.FXCollections;
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
import transport.core.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class BuyFareMediaController implements Initializable {

    @FXML private ComboBox<Personne> personneCombo;
    @FXML private ComboBox<String> modePaiementCombo;
    @FXML private Label prixCarteLabel;
    @FXML private RadioButton ticketRadio;
    @FXML private RadioButton carteRadio;
    @FXML private VBox resultPanel;
    @FXML private Label resultTitleLabel;
    @FXML private Label resultDetailsLabel;

    private TransportService service = TransportService.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize payment methods
        modePaiementCombo.getItems().addAll("Espèces", "Dahabia", "BaridiMob");
        modePaiementCombo.getSelectionModel().selectFirst();

        // Initialize person combo
        personneCombo.getItems().addAll(service.getPersonnes());
        personneCombo.setConverter(new javafx.util.StringConverter<Personne>() {
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
            updatePrice();
        }

        // Add listener to update price when person or ticket type changes
        personneCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updatePrice();
        });

        // Set up toggle group for ticket/card selection
        ToggleGroup group = new ToggleGroup();
        ticketRadio.setToggleGroup(group);
        carteRadio.setToggleGroup(group);
        ticketRadio.setSelected(true);

        // Add listeners to radio buttons
        ticketRadio.setOnAction(e -> updatePrice());
        carteRadio.setOnAction(e -> updatePrice());

        // Hide result panel initially
        resultPanel.setVisible(false);
        resultPanel.setManaged(false);

        // Initial price update
        updatePrice();
    }

    private void updatePrice() {
        if (ticketRadio.isSelected()) {
            // Ticket price is fixed at 50 DA
            prixCarteLabel.setText("50.00 DA");
            return;
        }

        // Card pricing logic
        Personne personne = personneCombo.getValue();
        if (personne == null) {
            prixCarteLabel.setText("5000.00 DA");
            return;
        }

        double basePrice = 5000.0;
        double finalPrice = basePrice;

        // Apply reductions based on person type and age
        if (personne instanceof Usager) {
            Usager usager = (Usager) personne;
            int age = usager.getAge();
            boolean handicap = usager.estHandicape();

            if (handicap) {
                // Carte solidarité: 50% reduction for people with disabilities
                finalPrice = basePrice * 0.5;
            } else if (age < 25) {
                // Carte junior: 30% reduction for users under 25
                finalPrice = basePrice * 0.7;
            } else if (age > 65) {
                // Carte senior: 25% reduction for users over 65
                finalPrice = basePrice * 0.75;
            }
        } else if (personne instanceof Employe) {
            // Carte partenaire: 40% reduction for employees
            finalPrice = basePrice * 0.6;
        }

        prixCarteLabel.setText(String.format("%.2f DA", finalPrice));
    }

    @FXML
    private void handleBuyFareMedia(ActionEvent event) {
        try {
            Personne personne = personneCombo.getValue();
            String modePaiement = modePaiementCombo.getValue();

            if (personne == null || modePaiement == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Champs obligatoires",
                        "Veuillez sélectionner une personne et un mode de paiement.");
                return;
            }

            if (ticketRadio.isSelected()) {
                // Buy ticket
                Ticket ticket = service.acheterTicket(personne, modePaiement);

                // Show success message
                resultPanel.setVisible(true);
                resultPanel.setManaged(true);
                resultTitleLabel.setText("Ticket acheté avec succès!");
                resultDetailsLabel.setText(
                        "ID: " + ticket.getId() + "\n" +
                                "Type: Ticket\n" +
                                "Prix: 50.00 DA\n" +
                                "Date d'émission: " + ticket.getDateAchat() + "\n" +
                                "Date d'expiration: " + ticket.getDateAchat() + "\n" +
                                "Mode de paiement: " + modePaiement
                );
            } else if (carteRadio.isSelected()) {
                // Buy card
                CartePersonnelle carte = service.acheterCarte(personne);

                // Determine card type
                String cardType = "Standard";
                if (personne instanceof Usager) {
                    Usager usager = (Usager) personne;
                    int age = usager.getAge();
                    boolean handicap = usager.estHandicape();

                    if (handicap) {
                        cardType = "Solidarité";
                    } else if (age < 25) {
                        cardType = "Junior";
                    } else if (age > 65) {
                        cardType = "Senior";
                    }
                } else if (personne instanceof Employe) {
                    cardType = "Partenaire";
                }

                // Show success message
                resultPanel.setVisible(true);
                resultPanel.setManaged(true);
                resultTitleLabel.setText("Carte achetée avec succès!");
                resultDetailsLabel.setText(
                        "ID: " + carte.getId() + "\n" +
                                "Type: Carte " + cardType + "\n" +
                                "Prix: " + String.format("%.2f", carte.getPrix()) + " DA\n" +
                                "Date d'émission: " + carte.getDateAchat() + "\n" +
                                "Date d'expiration: " + carte.getDateAchat().plusYears(1) + "\n" +
                                "Propriétaire: " + personne.getPrenom() + " " + personne.getNom()
                );
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Type de titre non sélectionné",
                        "Veuillez sélectionner le type de titre (Ticket ou Carte).");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur d'achat",
                    "Une erreur est survenue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCloseResult() {
        resultPanel.setVisible(false);
        resultPanel.setManaged(false);
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
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load view: " + viewName,
                    "Error: " + e.getMessage());
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