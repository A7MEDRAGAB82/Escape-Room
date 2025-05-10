package com.example.escaperoombusinesssystem.controller;

import com.example.escaperoombusinesssystem.model.*;
import com.example.escaperoombusinesssystem.model.user.Staff;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class StaffController implements Initializable {

    // UI Components
    @FXML private Label welcomeLabel;
    @FXML private StackPane contentPane;

    // Navigation buttons
    @FXML private Button roomMonitorBtn;
    @FXML private Button bookingManageBtn;
    @FXML private Button playerAssistBtn;

    // View containers
    @FXML private VBox roomMonitorView;
    @FXML private VBox bookingManageView;
    @FXML private VBox playerAssistView;

    // Rooms table
    @FXML private TableView<EscapeRoom> roomsTable;
    @FXML private TableColumn<EscapeRoom, String> roomIdColumn;
    @FXML private TableColumn<EscapeRoom, String> roomNameColumn;
    @FXML private TableColumn<EscapeRoom, String> roomStatusColumn;
    @FXML private TableColumn<EscapeRoom, String> currentBookingColumn;
    @FXML private TableColumn<EscapeRoom, Void> roomActionColumn;

    // Bookings table
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> bookingIdColumn;
    @FXML private TableColumn<Booking, String> bookingRoomColumn;
    @FXML private TableColumn<Booking, String> bookingTimeColumn;
    @FXML private TableColumn<Booking, Integer> bookingPlayersColumn;
    @FXML private TableColumn<Booking, String> bookingStatusColumn;
    @FXML private TableColumn<Booking, Void> bookingActionColumn;

    // Players table
    @FXML private TableView<Player> playersTable;
    @FXML private TableColumn<Player, String> playerNameColumn;
    @FXML private TableColumn<Player, String> playerRoomColumn;
    @FXML private TableColumn<Player, Integer> playerCluesColumn;
    @FXML private TableColumn<Player, String> playerTimeColumn;
    @FXML private TableColumn<Player, Void> playerActionColumn;

    // Data
    private ObservableList<EscapeRoom> roomData = FXCollections.observableArrayList();
    private ObservableList<Booking> bookingData = FXCollections.observableArrayList();
    private ObservableList<Player> playerData = FXCollections.observableArrayList();

    private Staff currentStaff;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupRoomTable();
        setupBookingTable();
        setupPlayerTable();
        showRoomMonitoring();
        refreshTables(); // Load data from DB on startup
    }

    public void setStaff(Staff staff) {
        this.currentStaff = staff;
        welcomeLabel.setText("Welcome, " + staff.getUsername());
    }

    private void setupRoomTable() {
        roomIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        roomNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        roomStatusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isActive() ? "Active" : "Inactive"));

        currentBookingColumn.setCellValueFactory(cellData -> {
            EscapeRoom room = cellData.getValue();
            Booking current = findCurrentBooking(room);
            return new SimpleStringProperty(current != null ?
                    "Booked until " + formatTime(current.getDateTime()) : "Available");
        });

        roomActionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button resetBtn = new Button("Reset Room");
            private final Button viewCluesBtn = new Button("View/Edit Clues");
            private final HBox buttons = new HBox(5, resetBtn, viewCluesBtn);

            {
                resetBtn.getStyleClass().add("action-button");
                viewCluesBtn.getStyleClass().add("edit-button");

                resetBtn.setOnAction(event -> {
                    EscapeRoom room = getTableView().getItems().get(getIndex());
                    handleResetRoom(room);
                });

                viewCluesBtn.setOnAction(event -> {
                    EscapeRoom room = getTableView().getItems().get(getIndex());
                    handleViewEditRoomClues(room);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });

        roomsTable.setItems(roomData);
    }

    private void setupBookingTable() {
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        bookingRoomColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRoom().getName()));
        bookingTimeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatTime(cellData.getValue().getDateTime())));
        bookingPlayersColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getPlayers().size()).asObject());
        bookingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        bookingsTable.setItems(bookingData);
    }

    private void setupPlayerTable() {
        playerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        playerRoomColumn.setCellValueFactory(cellData -> {
            EscapeRoom room = findPlayerRoom(cellData.getValue());
            return new SimpleStringProperty(room != null ? room.getName() : "Not in room");
        });
        playerCluesColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getSolvedClues().size()).asObject());
        playerTimeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatDuration(cellData.getValue().getTimeElapsed())));

        playerActionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button hintBtn = new Button("Give Hint");

            {
                hintBtn.getStyleClass().add("action-button");
                hintBtn.setOnAction(event -> {
                    Player player = getTableView().getItems().get(getIndex());
                    handleGiveHint(player);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(hintBtn);
                }
            }
        });

        playersTable.setItems(playerData);
    }


    // Navigation handlers
    @FXML
    private void showRoomMonitoring() {
        setActiveView(roomMonitorView);
        setActiveButton(roomMonitorBtn);
    }

    @FXML
    private void showBookingManagement() {
        setActiveView(bookingManageView);
        setActiveButton(bookingManageBtn);
    }

    @FXML
    private void showPlayerAssistance() {
        setActiveView(playerAssistView);
        setActiveButton(playerAssistBtn);
    }

    // Action handlers
    @FXML
    private void handleResetRoom(EscapeRoom room) {
        EscapeRoom selected = room;
        if (selected != null) {
            currentStaff.resetRoom(selected);
            refreshData();
            showAlert("Success", "Room " + selected.getName() + " has been reset");
        } else {
            showAlert("Error", "Please select a room to reset");
        }
    }

    @FXML
    private void handleAddClue() {
        // Implementation for adding a new clue
        showAlert("Info", "Add clue functionality will be implemented here");
    }

    @FXML
    private void handleGiveHint(Player player) {
        Player selected = playersTable.getSelectionModel().getSelectedItem();
        if (selected != null && !selected.getSolvedClues().isEmpty()) {
            Clue lastClue = selected.getSolvedClues().get(selected.getSolvedClues().size() - 1);
            showAlert("Hint", lastClue.getHint());
        } else {
            showAlert("Error", "No clues available to hint");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            // Load the login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/escaperoombusinesssystem/view/LoginView.fxml"));
            Parent root = loader.load();
            
            // Get the current stage and set the new scene
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Escape Room System - Login");
            
            // Clear the current staff data
            this.currentStaff = null;
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load login screen");
        }
    }

    @FXML
    private void refreshData() {
        roomsTable.refresh();
        bookingsTable.refresh();
        playersTable.refresh();
    }

    // Helper methods
    private void setActiveView(VBox view) {
        roomMonitorView.setVisible(false);
        bookingManageView.setVisible(false);
        playerAssistView.setVisible(false);
        view.setVisible(true);
    }

    private void setActiveButton(Button button) {
        roomMonitorBtn.getStyleClass().remove("active");
        bookingManageBtn.getStyleClass().remove("active");
        playerAssistBtn.getStyleClass().remove("active");

        if (button != null) {
            button.getStyleClass().add("active");
        }
    }

    // Update the showAlert method to match admin styling
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Get the dialog pane and add style class
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("/css/styles.css").toExternalForm()
        );
        dialogPane.getStyleClass().add("alert-dialog");

        alert.showAndWait();
    }

    private Booking findCurrentBooking(EscapeRoom room) {
        LocalDateTime now = LocalDateTime.now();
        for (Booking booking : bookingData) {
            if (booking.getRoom().equals(room) &&
                    booking.getDateTime().isAfter(now.minusHours(1)) &&
                    booking.getDateTime().isBefore(now.plusHours(2))) {
                return booking;
            }
        }
        return null;
    }

    private EscapeRoom findPlayerRoom(Player player) {
        for (Booking booking : bookingData) {
            if (booking.getPlayers().contains(player)) {
                return booking.getRoom();
            }
        }
        return null;
    }

    private String formatTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm, dd MMM"));
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.minusHours(hours).toMinutes();
        return String.format("%02d:%02d", hours, minutes);
    }

    @FXML
    private void handleResetRoom() {
        EscapeRoom selectedRoom = roomsTable.getSelectionModel().getSelectedItem();
        if (selectedRoom != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Reset");
            confirmation.setHeaderText("Reset " + selectedRoom.getName() + "?");
            confirmation.setContentText("This will unsolve all clues and clear player progress for this room.");

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                currentStaff.resetRoom(selectedRoom);
                refreshData();
                showAlert("Success", selectedRoom.getName() + " has been reset");
            }
        } else {
            showAlert("Error", "Please select a room to reset");
        }
    }

    @FXML
    private void handleGiveHint() {
        Player selectedPlayer = playersTable.getSelectionModel().getSelectedItem();
        if (selectedPlayer != null) {
            if (!selectedPlayer.getSolvedClues().isEmpty()) {
                Clue lastClue = selectedPlayer.getSolvedClues().get(
                        selectedPlayer.getSolvedClues().size() - 1);

                Alert hintAlert = new Alert(Alert.AlertType.INFORMATION);
                hintAlert.setTitle("Hint for " + selectedPlayer.getName());
                hintAlert.setHeaderText("Your hint:");
                hintAlert.setContentText(lastClue.getHint());
                hintAlert.showAndWait();
            } else {
                showAlert("No Clues", "This player hasn't solved any clues yet");
            }
        } else {
            showAlert("Error", "Please select a player to give a hint");
        }
    }

    @FXML
    private void refreshTables() {
        try (Connection conn = DBConnector.connect()) {
            // Refresh rooms table
            roomData.clear();
            String roomSql = "SELECT id, name, difficulty, max_players, is_active FROM escape_rooms";
            try (PreparedStatement pst = conn.prepareStatement(roomSql)) {
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    String id = rs.getString("id");
                    String name = rs.getString("name");
                    int difficulty = rs.getInt("difficulty");
                    int maxPlayers = rs.getInt("max_players");
                    boolean isActive = rs.getBoolean("is_active");
                    EscapeRoom room = new EscapeRoom(id, name, difficulty, maxPlayers);
                    if (!isActive) room.deactivate();
                    roomData.add(room);
                }
            }

            // Refresh bookings table
            bookingData.clear();
            String bookingSql = "SELECT b.booking_id, b.room_id, b.booking_time, b.status, r.name as room_name " +
                                "FROM bookings b JOIN escape_rooms r ON b.room_id = r.id";
            try (PreparedStatement pst = conn.prepareStatement(bookingSql)) {
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    String bookingId = rs.getString("booking_id");
                    String roomId = rs.getString("room_id");
                    LocalDateTime bookingTime = rs.getTimestamp("booking_time").toLocalDateTime();
                    String status = rs.getString("status");
                    String roomName = rs.getString("room_name");
                    EscapeRoom room = roomData.stream().filter(r -> r.getId().equals(roomId)).findFirst().orElse(null);
                    if (room != null) {
                        Booking booking = new Booking(room, bookingTime, 2);
                        booking.setBookingId(bookingId);
                        booking.setStatus(BookingStatus.valueOf(status));
                        bookingData.add(booking);
                    }
                }
            }

            // Refresh players table
            playerData.clear();
            String playerSql = "SELECT p.id, p.name, p.start_time FROM players p";
            try (PreparedStatement pst = conn.prepareStatement(playerSql)) {
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    String name = rs.getString("name");
                    String bookingId = rs.getString("booking_id");
                    Booking booking = bookingData.stream().filter(b -> b.getBookingId().equals(bookingId)).findFirst().orElse(null);
                    if (booking != null) {
                        Player player = new Player(name);
                        playerData.add(player);
                        booking.addPlayer(player);
                    }
                }
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to refresh data: " + e.getMessage());
        }
    }

    // Add popup for viewing/editing clues for a room
    private void handleViewEditRoomClues(EscapeRoom room) {
        if (room == null) {
            showAlert("Error", "Please select a room first");
            return;
        }
        VBox clueBox = new VBox(10);
        clueBox.setPadding(new Insets(15));
        Label header = new Label("Clues in " + room.getName());
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        clueBox.getChildren().add(header);

        // Add Clue Section
        HBox addClueRow = new HBox(10);
        TextField newDescField = new TextField();
        newDescField.setPromptText("Description");
        TextField newSolutionField = new TextField();
        newSolutionField.setPromptText("Solution");
        ChoiceBox<String> typeChoice = new ChoiceBox<>();
        typeChoice.getItems().addAll("Physical", "Puzzle", "Riddle", "Hidden", "Code");
        typeChoice.setValue("Physical");
        Button addBtn = new Button("Add Clue");
        final boolean[] shouldReopen = {false};
        addBtn.setOnAction(e -> {
            String desc = newDescField.getText();
            String sol = newSolutionField.getText();
            String type = typeChoice.getValue();
            if (!desc.trim().isEmpty() && !sol.trim().isEmpty()) {
                Clue newClue = new Clue(desc, sol, type);
                room.addClue(newClue);
                showAlert("Success", "Clue added.");
                refreshTables();
                shouldReopen[0] = true;
                ((Stage) clueBox.getScene().getWindow()).close();
            } else {
                showAlert("Error", "Description and solution cannot be empty.");
            }
        });
        addClueRow.getChildren().addAll(new Label("Add:"), newDescField, newSolutionField, typeChoice, addBtn);
        clueBox.getChildren().add(addClueRow);

        // Always reload clues from DB
        java.util.List<Clue> clues = EscapeRoom.loadCluesForRoom(room.getId());
        if (clues.isEmpty()) {
            clueBox.getChildren().add(new Label("No clues available for this room"));
        } else {
            for (Clue clue : clues) {
                HBox clueRow = new HBox(10);
                clueRow.setPadding(new Insets(5));
                Label descLabel = new Label("Description: ");
                TextField descField = new TextField(clue.getDescription());
                descField.setPrefWidth(200);
                Button saveBtn = new Button("Save");
                saveBtn.setOnAction(ev -> {
                    String newDesc = descField.getText();
                    if (!newDesc.trim().isEmpty()) {
                        currentStaff.updateClue(clue, newDesc, clue.getSolution());
                        showAlert("Success", "Clue description updated.");
                        refreshTables();
                        shouldReopen[0] = true;
                        ((Stage) clueBox.getScene().getWindow()).close();
                    } else {
                        showAlert("Error", "Description cannot be empty.");
                    }
                });
                Button markSolvedBtn = new Button("Mark as Solved");
                markSolvedBtn.setOnAction(ev -> {
                    clue.solve();
                    showAlert("Success", "Clue marked as solved.");
                    refreshTables();
                    shouldReopen[0] = true;
                    ((Stage) clueBox.getScene().getWindow()).close();
                });
                clueRow.getChildren().addAll(descLabel, descField, saveBtn, markSolvedBtn);
                clueBox.getChildren().add(clueRow);
            }
        }
        ScrollPane scrollPane = new ScrollPane(clueBox);
        scrollPane.setFitToWidth(true);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Room Clues");
        alert.setHeaderText("Clues in " + room.getName());
        alert.getDialogPane().setContent(scrollPane);
        alert.getDialogPane().setPrefSize(600, 400);
        alert.showAndWait();
        refreshTables();
        if (shouldReopen[0]) {
            handleViewEditRoomClues(room);
        }
    }

}