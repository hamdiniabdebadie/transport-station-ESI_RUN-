package transport.control;

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
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import transport.core.ActivityLog;
import transport.core.TransportService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.scene.control.Alert;

public class DashboardController implements Initializable {

    @FXML private Label userCountLabel;
    @FXML private Label ticketCountLabel;
    @FXML private Label cardCountLabel;
    @FXML private Label complaintCountLabel;

    @FXML private PieChart fareMediaChart;
    @FXML private PieChart activityChart;

    @FXML private TableView<ActivityLog> recentActivityTable;
    @FXML private TableColumn<ActivityLog, String> activityDateColumn;
    @FXML private TableColumn<ActivityLog, String> activityTypeColumn;
    @FXML private TableColumn<ActivityLog, String> activityDetailsColumn;

    private TransportService service = TransportService.getInstance();
    private ObservableList<ActivityLog> activityData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize statistics
        updateStatistics();

        // Initialize charts
        updateCharts();

        // Initialize activity table
        activityDateColumn.setCellValueFactory(cellData -> {
            LocalDateTime timestamp = cellData.getValue().getTimestamp();
            String formattedDate = timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            return new SimpleStringProperty(formattedDate);
        });

        activityTypeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCategory() + " - " + cellData.getValue().getAction()));

        activityDetailsColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDetails()));

        // Load activity data
        updateActivityTable();
    }

    private void updateStatistics() {
        userCountLabel.setText(String.valueOf(service.getUserCount()));
        ticketCountLabel.setText(String.valueOf(service.getTicketCount()));
        cardCountLabel.setText(String.valueOf(service.getCardCount()));
        complaintCountLabel.setText(String.valueOf(service.getComplaintCount()));
    }

    private void updateCharts() {
        // Update fare media distribution chart
        ObservableList<PieChart.Data> fareMediaData = FXCollections.observableArrayList(
            new PieChart.Data("Tickets", service.getTicketCount()),
            new PieChart.Data("Cartes", service.getCardCount())
        );
        fareMediaChart.setData(fareMediaData);

        // Update activity by category chart
        Map<String, Long> activityByCategory = service.getRecentActivity().stream()
            .collect(Collectors.groupingBy(ActivityLog::getCategory, Collectors.counting()));

        ObservableList<PieChart.Data> activityData = FXCollections.observableArrayList();
        for (Map.Entry<String, Long> entry : activityByCategory.entrySet()) {
            activityData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
        activityChart.setData(activityData);
    }

    private void updateActivityTable() {
        activityData.clear();
        activityData.addAll(service.getRecentActivity());
        recentActivityTable.setItems(activityData);
    }

    @FXML
    private void handleHome(ActionEvent event) {
        // Already on home page, just refresh
        updateStatistics();
        updateCharts();
        updateActivityTable();
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

    @FXML
    private void handleAddUser(ActionEvent event) {
        loadView(event, "AddUserView.fxml");
    }

    @FXML
    private void handleBuyFareMedium(ActionEvent event) {
        loadView(event, "BuyFareMediaView.fxml");
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
