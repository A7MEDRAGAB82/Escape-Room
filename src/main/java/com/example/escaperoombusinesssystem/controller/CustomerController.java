package com.example.escaperoombusinesssystem.controller;

import com.example.escaperoombusinesssystem.model.*;
import com.example.escaperoombusinesssystem.model.user.Customer;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {

    // UI Components
    @FXML private Label welcomeLabel;

    // Rooms Table
    @FXML private TableView<EscapeRoom> roomsTable;
    @FXML private TableColumn<EscapeRoom, String> roomIdColumn;
    @FXML private TableColumn<EscapeRoom, String> roomNameColumn;
    @FXML private TableColumn<EscapeRoom, String> difficultyColumn;
    @FXML private TableColumn<EscapeRoom, String> maxPlayersColumn;
    @FXML private TableColumn<EscapeRoom, Void> roomActionColumn;

    // Bookings Table
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> bookingIdColumn;
    @FXML private TableColumn<Booking, String> roomColumn;
    @FXML private TableColumn<Booking, LocalDate> dateColumn;
    @FXML private TableColumn<Booking, LocalTime> timeColumn;
    @FXML private TableColumn<Booking, Integer> playersColumn;
    @FXML private TableColumn<Booking, String> statusColumn;
    @FXML private TableColumn<Booking, Void> bookingActionColumn;

    // View Containers
    @FXML private VBox roomsView;
    @FXML private VBox bookingsView;

    // Navigation Buttons
    @FXML private Button roomsButton;
    @FXML private Button bookingsButton;

    // Data
    private ObservableList<EscapeRoom> roomData = FXCollections.observableArrayList();
    private ObservableList<Booking> bookingData = FXCollections.observableArrayList();
    private Customer currentCustomer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupRoomTable();
        setupBookingTable();
    }

    public void setCustomer(Customer customer) {
        this.currentCustomer = customer;
        loadSampleData();
        welcomeLabel.setText("Welcome, " + customer.getUsername());
    }

    private void setupRoomTable() {
        roomIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        roomNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        difficultyColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty("Level " + cellData.getValue().getDifficulty()));
        maxPlayersColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getMaxPlayers())));

        roomActionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button bookBtn = new Button("Make Booking");

            {
                bookBtn.getStyleClass().add("action-button");
                bookBtn.setOnAction(event -> {
                    EscapeRoom room = getTableView().getItems().get(getIndex());
                    handleBookRoom(room);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(10); // 10 pixels spacing
                    buttons.setAlignment(Pos.CENTER);
                    buttons.getChildren().add(bookBtn);
                    setGraphic(buttons);
                }
            }
        });

        roomsTable.setItems(roomData);
    }

    private void setupBookingTable() {
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        roomColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRoom().getName()));
        dateColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getDateTime().toLocalDate()));
        timeColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getDateTime().toLocalTime()));
        playersColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getPlayers().size()).asObject());
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        bookingActionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button cancelBtn = new Button("Cancel Booking");

            {
                cancelBtn.getStyleClass().add("action-button");
                cancelBtn.setOnAction(event -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    handleCancelBooking(booking);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Booking booking = getTableView().getItems().get(getIndex());
                    cancelBtn.setDisable(booking.getStatus() == BookingStatus.CANCELLED);
                    setGraphic(cancelBtn);
                }
            }
        });

        bookingsTable.setItems(bookingData);
    }

    private void loadSampleData() {
        // Add sample rooms to Business if empty
        if (Business.getRooms().isEmpty()) {
            try {
                EscapeRoom hauntedMansion = new EscapeRoom("1", "Haunted Mansion", 5, 7);
                EscapeRoom prisonBreak = new EscapeRoom("2", "Prison Break", 6, 6);
                EscapeRoom spaceStation = new EscapeRoom("3", "Space Station", 4, 5);
                
                Business.addRoom(hauntedMansion);
                Business.addRoom(prisonBreak);
                Business.addRoom(spaceStation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Load rooms from Business class
        roomData.addAll(Business.getRooms());

        // Add sample bookings for the current customer
        if (currentCustomer != null) {
            // Sample booking 1
            EscapeRoom room1 = Business.getRooms().get(0); // Get first room
            LocalDateTime dateTime1 = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0);
            try {
                Booking booking1 = currentCustomer.makeBooking(room1, dateTime1, 4);
                bookingData.add(booking1);
            } catch (Exception e) {
                // Handle exception if needed
            }

            // Sample booking 2
            EscapeRoom room2 = Business.getRooms().get(1); // Get second room
            LocalDateTime dateTime2 = LocalDateTime.now().plusDays(2).withHour(16).withMinute(0);
            try {
                Booking booking2 = currentCustomer.makeBooking(room2, dateTime2, 6);
                bookingData.add(booking2);
            } catch (Exception e) {
                // Handle exception if needed
            }
        }
    }

    // Navigation Methods
    @FXML
    private void showRooms() {
        roomsView.setVisible(true);
        bookingsView.setVisible(false);
        updateActiveButton("Rooms");
    }

    @FXML
    private void showBookings() {
        roomsView.setVisible(false);
        bookingsView.setVisible(true);
        updateActiveButton("Bookings");
    }

    private void updateActiveButton(String activeButton) {
        roomsButton.getStyleClass().remove("active");
        bookingsButton.getStyleClass().remove("active");

        switch (activeButton) {
            case "Rooms":
                roomsButton.getStyleClass().add("active");
                break;
            case "Bookings":
                bookingsButton.getStyleClass().add("active");
                break;
        }
    }

    // Action Methods
    private void handleBookRoom(EscapeRoom room) {
        Dialog<Booking> dialog = new Dialog<>();
        dialog.setTitle("Book Room");
        dialog.setHeaderText("Book " + room.getName());

        // Create the custom dialog content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        DatePicker datePicker = new DatePicker(LocalDate.now());
        ComboBox<LocalTime> timeComboBox = new ComboBox<>();
        timeComboBox.getItems().addAll(
                LocalTime.of(10, 0), LocalTime.of(12, 0), LocalTime.of(14, 0),
                LocalTime.of(16, 0), LocalTime.of(18, 0), LocalTime.of(20, 0)
        );
        timeComboBox.setValue(LocalTime.of(10, 0));

        Spinner<Integer> playersSpinner = new Spinner<>(2, room.getMaxPlayers(), 2);

        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Time:"), 0, 1);
        grid.add(timeComboBox, 1, 1);
        grid.add(new Label("Players:"), 0, 2);
        grid.add(playersSpinner, 1, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType bookButtonType = new ButtonType("Book", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(bookButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == bookButtonType) {
                try {
                    LocalDateTime dateTime = LocalDateTime.of(datePicker.getValue(), timeComboBox.getValue());
                    return currentCustomer.makeBooking(room, dateTime, playersSpinner.getValue());
                } catch (Exception e) {
                    showAlert("Error", e.getMessage());
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(booking -> {
            currentCustomer.makeBooking(booking.getRoom(), booking.getDateTime(), booking.getPlayers().size());
            bookingData.add(booking);
            showAlert("Success", "Room booked successfully!");
        });
    }

    private void handleCancelBooking(Booking booking) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Booking");
        alert.setHeaderText("Cancel Booking");
        alert.setContentText("Are you sure you want to cancel this booking?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                currentCustomer.cancelBooking(booking);
                bookingsTable.refresh();
                showAlert("Success", "Booking cancelled successfully!");
            }
        });
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/escaperoombusinesssystem/view/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Escape Room System - Login");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load login screen");
        }
    }

    @FXML
    private void refreshData() {
        roomsTable.refresh();
        bookingsTable.refresh();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 