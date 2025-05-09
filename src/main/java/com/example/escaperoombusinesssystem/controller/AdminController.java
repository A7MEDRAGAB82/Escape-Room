package com.example.escaperoombusinesssystem.controller;

import com.example.escaperoombusinesssystem.model.*;
import com.example.escaperoombusinesssystem.model.user.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class AdminController implements Initializable {

    // UI Components
    @FXML private Label welcomeLabel;

    // Rooms Table
    @FXML private TableView<EscapeRoom> roomsTable;
    @FXML private TableColumn<EscapeRoom, String> idColumn;
    @FXML private TableColumn<EscapeRoom, String> nameColumn;
    @FXML private TableColumn<EscapeRoom, String> difficultyColumn;
    @FXML private TableColumn<EscapeRoom, String> statusColumn;
    @FXML private TableColumn<EscapeRoom, Void> actionColumn;

    // Bookings Table
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> bookingIdColumn;
    @FXML private TableColumn<Booking, String> customerColumn;
    @FXML private TableColumn<Booking, String> roomColumn;
    @FXML private TableColumn<Booking, LocalDate> dateColumn;
    @FXML private TableColumn<Booking, LocalTime> timeColumn;
    @FXML private TableColumn<Booking, Integer> playersColumn;
    @FXML private TableColumn<Booking, Integer> solvedCluesColumn;
    @FXML private TableColumn<Booking, String> timeElapsedColumn;
    @FXML private TableColumn<Booking, String> bookingStatusColumn;

    // Users Table
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> roleColumn;

    // Players Table
    @FXML private TableView<Player> playersTable;
    @FXML private TableColumn<Player, String> playerNameColumn;
    @FXML private TableColumn<Player, String> currentRoomColumn;
    @FXML private TableColumn<Player, Integer> cluesSolvedColumn;
    @FXML private TableColumn<Player, String> timePlayedColumn;
    @FXML private TableColumn<Player, Void> playerActionColumn;

    // View Containers
    @FXML private VBox roomsView;
    @FXML private VBox bookingsView;
    @FXML private VBox usersView;
    @FXML private VBox playersView;

    // Navigation Buttons
    @FXML private Button roomsButton;
    @FXML private Button bookingsButton;
    @FXML private Button usersButton;
    @FXML private Button playersButton;

    // Data
    private ObservableList<User> userData = FXCollections.observableArrayList();
    private ObservableList<EscapeRoom> roomData = FXCollections.observableArrayList();
    private ObservableList<Booking> bookingData = FXCollections.observableArrayList();
    private ObservableList<Player> playerData = FXCollections.observableArrayList();
    private Map<Player, EscapeRoom> playerRoomMap = new HashMap<>();

    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupRoomTable();
        setupBookingTable();
        setupUserTable();
        setupPlayerTable();
        refreshTables();
    }

    public void setUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getUsername() + " (" + user.getRole() + ")");
    }

    private void setupRoomTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        difficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isActive() ? "Active" : "Inactive"));

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button toggleBtn = new Button();
            private final HBox buttons = new HBox(5, toggleBtn);

            {
                toggleBtn.getStyleClass().add("toggle-button");

                toggleBtn.setOnAction(event -> {
                    EscapeRoom room = getTableView().getItems().get(getIndex());
                    if (currentUser instanceof Admin) {
                        Admin admin = (Admin) currentUser;
                        try {
                            admin.toggleRoomIsActive(room);
                            refreshTables();
                        } catch (Exception e) {
                            showAlert("Error", e.getMessage());
                        }
                    } else {
                        showAlert("Error", "Only admin users can toggle room status");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    EscapeRoom room = getTableView().getItems().get(getIndex());
                    toggleBtn.setText(room.isActive() ? "Deactivate" : "Activate");
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
        bookingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        solvedCluesColumn.setCellValueFactory(cellData -> {
            int total = 0;
            for (Player player : cellData.getValue().getPlayers()) {
                total += player.getSolvedClues().size();
            }
            return new SimpleIntegerProperty(total).asObject();
        });

        timeElapsedColumn.setCellValueFactory(cellData -> {
            Booking booking = cellData.getValue();
            if (booking.getDateTime().isBefore(LocalDateTime.now())) {
                Duration duration = Duration.between(booking.getDateTime(), LocalDateTime.now());
                return new SimpleStringProperty(formatDuration(duration));
            }
            return new SimpleStringProperty("Not started");
        });

        customerColumn.setCellValueFactory(cellData -> {
            if (!cellData.getValue().getPlayers().isEmpty()) {
                return new SimpleStringProperty(cellData.getValue().getPlayers().get(0).getName());
            }
            return new SimpleStringProperty("No players");
        });

        bookingsTable.setItems(bookingData);
    }

    private void setupUserTable() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        usersTable.setItems(userData);
    }

    private void setupPlayerTable() {
        playerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        cluesSolvedColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getSolvedClues().size()).asObject());
        timePlayedColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatDuration(cellData.getValue().getTimeElapsed())));

        currentRoomColumn.setCellValueFactory(cellData -> {
            EscapeRoom room = playerRoomMap.get(cellData.getValue());
            return new SimpleStringProperty(room != null ? room.getName() : "Not in room");
        });

        playerActionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewCluesBtn = new Button("View Solved Clues");
            private final HBox buttons = new HBox(5, viewCluesBtn);

            {
                viewCluesBtn.setOnAction(event -> {
                    Player player = getTableView().getItems().get(getIndex());
                    viewPlayerClues(player);
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

        playersTable.setItems(playerData);
    }

    // Navigation Methods
    @FXML
    private void showRooms() {
        roomsView.setVisible(true);
        bookingsView.setVisible(false);
        usersView.setVisible(false);
        playersView.setVisible(false);
        updateActiveButton("Rooms");
    }

    @FXML
    private void showBookings() {
        roomsView.setVisible(false);
        bookingsView.setVisible(true);
        usersView.setVisible(false);
        playersView.setVisible(false);
        updateActiveButton("Bookings");
    }

    @FXML
    private void showUsers() {
        roomsView.setVisible(false);
        bookingsView.setVisible(false);
        usersView.setVisible(true);
        playersView.setVisible(false);
        updateActiveButton("Users");
    }

    @FXML
    private void showPlayers() {
        roomsView.setVisible(false);
        bookingsView.setVisible(false);
        usersView.setVisible(false);
        playersView.setVisible(true);
        updateActiveButton("Players");
    }

    private void updateActiveButton(String activeButton) {
        roomsButton.getStyleClass().remove("active");
        bookingsButton.getStyleClass().remove("active");
        usersButton.getStyleClass().remove("active");
        playersButton.getStyleClass().remove("active");

        switch (activeButton) {
            case "Rooms":
                roomsButton.getStyleClass().add("active");
                break;
            case "Bookings":
                bookingsButton.getStyleClass().add("active");
                break;
            case "Users":
                usersButton.getStyleClass().add("active");
                break;
            case "Players":
                playersButton.getStyleClass().add("active");
                break;
        }
    }

    // Action Methods
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
    private void handleAddRoom() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Room");
        dialog.setHeaderText("Create a new escape room");
        dialog.setContentText("Enter room name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(roomName -> {
            if (roomName.trim().isEmpty()) {
                showAlert("Error", "Room name cannot be empty");
            } else {
                try {
                    String newId = String.valueOf(roomData.size() + 1);
                    EscapeRoom newRoom = new EscapeRoom(newId, roomName, 3, 6); // Default difficulty 3, max players 6

                    if (currentUser instanceof Admin) {
                        Admin admin = (Admin) currentUser;
                        admin.addRoom(newRoom);
                        try {
//                            newRoom.saveToDatabase(); // Save to DB
                            roomData.add(newRoom);
                            showAlert("Success", "Room '" + roomName + "' added successfully");
                        } catch (Exception e) {
                            showAlert("Error", "Failed to save room to database: " + e.getMessage());
                        }
                    } else {
                        showAlert("Error", "Only admin users can add rooms");
                    }
                } catch (Exception e) {
                    showAlert("Error", e.getMessage());
                }
            }
        });
    }

    private User createUserByRole(String username, String password, String role) {
        switch (role.toLowerCase()) {
            case "admin":
                return new Admin(username, password);
            case "staff":
                return new Staff(username, password);
            case "customer":
                return new Customer(username, password);
            default:
                return new Staff(username, password);
        }
    }

    private void viewPlayerClues(Player player) {
        VBox clueBox = new VBox(10);
        clueBox.setPadding(new Insets(15));

        Label header = new Label("Clues for " + player.getName());
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        clueBox.getChildren().add(header);

        if (player.getSolvedClues().isEmpty()) {
            clueBox.getChildren().add(new Label("No clues available for this player"));
        } else {
            for (Clue clue : player.getSolvedClues()) {
                VBox clueCard = new VBox(5);
                clueCard.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 10;");

                Label descLabel = new Label("Description: " + clue.getDescription());
                Label typeLabel = new Label("Type: " + clue.getType());
                Label statusLabel = new Label("Status: " + (clue.isSolved() ? "SOLVED" : "UNSOLVED"));
                statusLabel.setStyle("-fx-text-fill: " + (clue.isSolved() ? "green" : "orange") + ";");

                Button hintBtn = new Button("Get Hint");
                hintBtn.setOnAction(e -> {
                    Alert hintAlert = new Alert(Alert.AlertType.INFORMATION);
                    hintAlert.setTitle("Clue Hint");
                    hintAlert.setHeaderText("Hint for this clue:");
                    hintAlert.setContentText(clue.getHint());
                    hintAlert.showAndWait();
                });

                clueCard.getChildren().addAll(descLabel, typeLabel, statusLabel, hintBtn);
                clueBox.getChildren().add(clueCard);
            }
        }

        ScrollPane scrollPane = new ScrollPane(clueBox);
        scrollPane.setFitToWidth(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Player Clues");
        alert.setHeaderText("Total clues: " + player.getSolvedClues().size());
        alert.getDialogPane().setContent(scrollPane);
        alert.getDialogPane().setPrefSize(400, 300);
        alert.showAndWait();
    }

    @FXML
    private void refreshTables() {
        try (Connection conn = DBConnector.connect()) {
            // Refresh users table
            userData.clear();
            String userSql = "SELECT username, role, hashed_password FROM users";
            try (PreparedStatement pst = conn.prepareStatement(userSql)) {
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    String username = rs.getString("username");
                    String role = rs.getString("role");
                    String hashedPassword = rs.getString("hashed_password");
                    User user = createUserByRole(username, hashedPassword, role);
                    userData.add(user);
                }
            }

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
                    LocalDateTime bookTime = rs.getTimestamp("booking_time").toLocalDateTime();
                    String status = rs.getString("status");
                    String roomName = rs.getString("room_name");
                    EscapeRoom room = roomData.stream().filter(r -> r.getId().equals(roomId)).findFirst().orElse(null);
                    if (room != null) {
                        Booking booking = new Booking(room, bookTime, 2); // Default to minimum valid players
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
                    String bookingId = rs.getString("booking");
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

    // Helper Methods
    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.minusHours(hours).toMinutes();
        return String.format("%02d:%02d", hours, minutes);
    }

    // Add these new handler methods
    @FXML
    private void handleAddClueToRoom() {
        EscapeRoom selectedRoom = roomsTable.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            showAlert("Error", "Please select a room first");
            return;
        }

        Dialog<Clue> dialog = new Dialog<>();
        dialog.setTitle("Add New Clue");
        dialog.setHeaderText("Add clue to " + selectedRoom.getName());

        // Set the button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create the clue form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");
        TextField solutionField = new TextField();
        solutionField.setPromptText("Solution");
        ChoiceBox<String> typeChoice = new ChoiceBox<>();
        typeChoice.getItems().addAll("Physical", "Puzzle", "Riddle", "Hidden", "Code");
        typeChoice.setValue("Physical");

        grid.add(new Label("Description:"), 0, 0);
        grid.add(descriptionField, 1, 0);
        grid.add(new Label("Solution:"), 0, 1);
        grid.add(solutionField, 1, 1);
        grid.add(new Label("Type:"), 0, 2);
        grid.add(typeChoice, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Convert result to a clue
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Clue(
                        descriptionField.getText(),
                        solutionField.getText(),
                        typeChoice.getValue()
                );
            }
            return null;
        });

        Optional<Clue> result = dialog.showAndWait();
        result.ifPresent(clue -> {
            selectedRoom.addClue(clue);
            refreshTables();
            showAlert("Success", "New clue added to " + selectedRoom.getName());
        });
    }

    @FXML
    private void handleViewRoomClues() {
        EscapeRoom selectedRoom = roomsTable.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            showAlert("Error", "Please select a room first");
            return;
        }

        VBox clueBox = new VBox(10);
        clueBox.setPadding(new Insets(15));

        Label header = new Label("Clues in " + selectedRoom.getName());
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        clueBox.getChildren().add(header);

        if (selectedRoom.getClues().isEmpty()) {
            clueBox.getChildren().add(new Label("No clues available for this room"));
        } else {
            for (Clue clue : selectedRoom.getClues()) {
                VBox clueCard = new VBox(5);
                clueCard.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 10;");

                Label descLabel = new Label("Description: " + clue.getDescription());
                Label typeLabel = new Label("Type: " + clue.getType());
                Label statusLabel = new Label("Status: " + (clue.isSolved() ? "SOLVED" : "UNSOLVED"));
                statusLabel.setStyle("-fx-text-fill: " + (clue.isSolved() ? "green" : "orange") + ";");

                clueCard.getChildren().addAll(descLabel, typeLabel, statusLabel);
                clueBox.getChildren().add(clueCard);
            }
        }

        ScrollPane scrollPane = new ScrollPane(clueBox);
        scrollPane.setFitToWidth(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Room Clues");
        alert.setHeaderText("Clues in " + selectedRoom.getName());
        alert.getDialogPane().setContent(scrollPane);
        alert.getDialogPane().setPrefSize(400, 300);
        alert.showAndWait();
    }

    @FXML
    private void handleGenerateReport() {
        if (currentUser instanceof Admin) {
            Admin admin = (Admin) currentUser;
            Report report = admin.generateReport();
            
            // Store the report in the database (simulated for now)
            storeReport(report);
            
            // Show success message
            showAlert("Success", "Report generated successfully!\nReport ID: " + report.getReportId());
        } else {
            showAlert("Error", "Only admin users can generate reports");
        }
    }

    private void storeReport(Report report) {
        // TODO: Implement actual database storage
        ((Admin) currentUser).generateReport();
        // For now, we'll just print the report data
        System.out.println("Storing report: " + report.getReportId());
        System.out.println("Generated on: " + report.getGeneratedDate());
        System.out.println("Report data:");
        report.getData().forEach((key, value) -> System.out.println(key + ": " + value));

    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleAddUser() {
        ChoiceDialog<String> roleDialog = new ChoiceDialog<>("Staff", "Admin", "Staff", "Customer");
        roleDialog.setTitle("Add New User");
        roleDialog.setHeaderText("Select user role");
        roleDialog.setContentText("Role:");

        Optional<String> roleResult = roleDialog.showAndWait();
        roleResult.ifPresent(role -> {
            TextInputDialog userDialog = new TextInputDialog();
            userDialog.setTitle("Add New User");
            userDialog.setHeaderText("Create a new " + role + " user");
            userDialog.setContentText("Username:");

            Optional<String> userResult = userDialog.showAndWait();
            userResult.ifPresent(username -> {
                if (username.trim().isEmpty()) {
                    showAlert("Error", "Username cannot be empty");
                } else {
                    User newUser = createUserByRole(username, "default123", role);
                    try {
                        // Save user to database
                        Connection conn = DBConnector.connect();
                        String sql = "INSERT INTO users (username, role, password_hash) VALUES (?, ?, ?) " +
                                   "ON CONFLICT (username) DO UPDATE SET role = ?, password_hash = ?";
                        try (PreparedStatement pst = conn.prepareStatement(sql)) {
                            pst.setString(1, username);
                            pst.setString(2, role);
                            pst.setString(3, newUser.getHashedPassword());
                            pst.setString(4, role);
                            pst.setString(5, newUser.getHashedPassword());
                            pst.executeUpdate();
                        }
                        userData.add(newUser);
                        showAlert("Success", role + " user '" + username + "' added successfully");
                    } catch (Exception e) {
                        showAlert("Error", "Failed to add user: " + e.getMessage());
                    }
                }
            });
        });
    }
}