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
    @FXML private Button clueManageBtn;
    @FXML private Button playerAssistBtn;

    // View containers
    @FXML private VBox roomMonitorView;
    @FXML private VBox bookingManageView;
    @FXML private VBox clueManageView;
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

    // Clues table
    @FXML private TableView<Clue> cluesTable;
    @FXML private TableColumn<Clue, String> clueDescriptionColumn;
    @FXML private TableColumn<Clue, String> clueTypeColumn;
    @FXML private TableColumn<Clue, String> clueStatusColumn;
    @FXML private TableColumn<Clue, Void> clueActionColumn;

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
    private ObservableList<Clue> clueData = FXCollections.observableArrayList();
    private ObservableList<Player> playerData = FXCollections.observableArrayList();

    private Staff currentStaff;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupRoomTable();
        setupBookingTable();
        setupClueTable();
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

            {
                resetBtn.getStyleClass().add("action-button");
                resetBtn.setOnAction(event -> {
                    EscapeRoom room = getTableView().getItems().get(getIndex());
                    handleResetRoom(room);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(resetBtn);
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

    private void setupClueTable() {
        clueDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        clueTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        clueStatusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isSolved() ? "Solved" : "Unsolved"));

        clueActionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button resetBtn = new Button("Reset");
            private final Button editBtn = new Button("Edit");
            private final HBox buttons = new HBox(5, resetBtn, editBtn);

            {
                resetBtn.getStyleClass().add("action-button");
                editBtn.getStyleClass().add("edit-button");

                resetBtn.setOnAction(event -> {
                    Clue clue = getTableView().getItems().get(getIndex());
                    handleResetClue(clue);
                });

                editBtn.setOnAction(event -> {
                    Clue clue = getTableView().getItems().get(getIndex());
                    handleEditClue(clue);
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

        cluesTable.setItems(clueData);
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

    private void loadSampleData() {
        // Sample rooms
        EscapeRoom hauntedMansion = new EscapeRoom("1", "Haunted Mansion", 5, 7);
        EscapeRoom prisonBreak = new EscapeRoom("2", "Prison Break", 6, 6);
        roomData.addAll(hauntedMansion, prisonBreak);

        // Sample clues
        Clue clue1 = new Clue("Look behind the painting", "Key is taped behind frame", "Physical");
        Clue clue2 = new Clue("Solve the riddle", "Answer is 'time'", "Puzzle");
        clueData.addAll(clue1, clue2);

        // Sample players
        Player player1 = new Player("John Doe");
        player1.addSolvedClue(clue1);
        Player player2 = new Player("Jane Smith");
        playerData.addAll(player1, player2);

        // Sample bookings
//        LocalDateTime now = LocalDateTime.now();
//        Booking booking1 = new Booking(hauntedMansion, now.plusHours(1), 4);
//        booking1.setBookingId("B001");
//        booking1.addPlayer(player1);
//        booking1.setStatus(BookingStatus.CONFIRMED);
//
//        Booking booking2 = new Booking(prisonBreak, now.plusHours(3), 6);
//        booking2.setBookingId("B002");
//        booking2.addPlayer(player2);
//        booking2.setStatus(BookingStatus.CONFIRMED);
//
//        bookingData.addAll(booking1, booking2);
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
    private void showClueManagement() {
        setActiveView(clueManageView);
        setActiveButton(clueManageBtn);
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
    private void handleResetClue(Clue clue) {
        Clue selected = cluesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.unsolve();
            refreshData();
            showAlert("Success", "Clue has been reset");
        } else {
            showAlert("Error", "Please select a clue to reset");
        }
    }

    @FXML
    private void handleEditClue(Clue clue) {
        // Implementation for editing a clue
        showAlert("Info", "Edit clue functionality will be implemented here");
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
        cluesTable.refresh();
        playersTable.refresh();
    }

    // Helper methods
    private void setActiveView(VBox view) {
        roomMonitorView.setVisible(false);
        bookingManageView.setVisible(false);
        clueManageView.setVisible(false);
        playerAssistView.setVisible(false);
        view.setVisible(true);
    }

    private void setActiveButton(Button button) {
        roomMonitorBtn.getStyleClass().remove("active");
        bookingManageBtn.getStyleClass().remove("active");
        clueManageBtn.getStyleClass().remove("active");
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
    private void handleResetClue() {
        Clue selectedClue = cluesTable.getSelectionModel().getSelectedItem();
        if (selectedClue != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Reset");
            confirmation.setHeaderText("Reset this clue?");
            confirmation.setContentText("This will mark the clue as unsolved.");

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                selectedClue.unsolve();
                refreshData();
                showAlert("Success", "Clue has been reset");
            }
        } else {
            showAlert("Error", "Please select a clue to reset");
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

}