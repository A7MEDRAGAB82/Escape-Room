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

import java.io.IOException;
import java.net.URL;
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
    @FXML private TableColumn<Booking, Void> bookingActionColumn;

    // Users Table
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, Void> userActionColumn;

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
        loadSampleData();
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
            private final Button editBtn = new Button("Edit");
            private final HBox buttons = new HBox(5, toggleBtn, editBtn);

            {
                toggleBtn.getStyleClass().add("toggle-button");
                editBtn.getStyleClass().add("edit-button");

                toggleBtn.setOnAction(event -> {
                    EscapeRoom room = getTableView().getItems().get(getIndex());
                    room.toggleIsActive();
                    refreshTables();
                });

                editBtn.setOnAction(event -> {
                    EscapeRoom room = getTableView().getItems().get(getIndex());
                    editRoom(room);
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
            if (!cellData.getValue().getPlayers().isEmpty()) {
                Duration duration = cellData.getValue().getPlayers().get(0).getTimeElapsed();
                return new SimpleStringProperty(formatDuration(duration));
            }
            return new SimpleStringProperty("N/A");
        });

        customerColumn.setCellValueFactory(cellData -> {
            if (!cellData.getValue().getPlayers().isEmpty()) {
                return new SimpleStringProperty(cellData.getValue().getPlayers().get(0).getName());
            }
            return new SimpleStringProperty("No players");
        });

        bookingActionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button cancelBtn = new Button("Cancel");
            private final Button confirmBtn = new Button("Confirm");
            private final HBox buttons = new HBox(5, confirmBtn, cancelBtn);

            {
                cancelBtn.getStyleClass().add("cancel-button");
                confirmBtn.getStyleClass().add("action-button");

                cancelBtn.setOnAction(event -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    cancelBooking(booking);
                });

                confirmBtn.setOnAction(event -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    confirmBooking(booking);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Booking booking = getTableView().getItems().get(getIndex());
                    cancelBtn.setDisable(!"CONFIRMED".equals(booking.getStatus().toString()));
                    confirmBtn.setDisable(!"PENDING".equals(booking.getStatus().toString()));
                    setGraphic(buttons);
                }
            }
        });

        bookingsTable.setItems(bookingData);
    }

    private void setupUserTable() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        userActionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button resetPassBtn = new Button("Reset Password");
            private final Button deleteBtn = new Button("Delete");
            private final HBox buttons = new HBox(5, resetPassBtn, deleteBtn);

            {
                resetPassBtn.getStyleClass().add("action-button");
                deleteBtn.getStyleClass().add("cancel-button");

                resetPassBtn.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    resetUserPassword(user);
                });

                deleteBtn.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    deleteUser(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
//                    User user = getTableView().getItems().get(getIndex());
//                    deleteBtn.setDisable(user.getUsername().equals(currentUser.getUsername()));
//                    setGraphic(buttons);
                }
            }
        });

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
            private final Button addClueBtn = new Button("Add Clue");
            private final Button viewCluesBtn = new Button("View Clues");
            private final HBox buttons = new HBox(5, addClueBtn, viewCluesBtn);

            {
                addClueBtn.getStyleClass().add("action-button");
                viewCluesBtn.getStyleClass().add("view-button");

                addClueBtn.setOnAction(event -> {
                    Player player = getTableView().getItems().get(getIndex());
                    addClueToPlayer(player);
                });

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

    private void loadSampleData() {
        // Sample Users
        userData.addAll(
                new Admin("admin1", "admin123"),
                new Staff("staff1", "staff123"),
                new Customer("customer1", "customer123"),
                new Customer("john_doe", "password123"),
                new Customer("jane_smith", "securepass")
        );

        // Sample Rooms
        EscapeRoom hauntedMansion = new EscapeRoom("1", "Haunted Mansion", 5, 7);
        EscapeRoom prisonBreak = new EscapeRoom("2", "Prison Break", 6, 6);
        EscapeRoom spaceStation = new EscapeRoom("3", "Space Station", 4, 8);

        roomData.addAll(hauntedMansion, prisonBreak, spaceStation);

        // Sample Clues with solutions and types
        Clue clue1 = new Clue("Look behind the painting",
                "Key is taped behind the frame", "Physical");
        Clue clue2 = new Clue("The combination is in the book",
                "Page 42 has the code 7392", "Puzzle");
        Clue clue3 = new Clue("Check under the desk",
                "Magnetic key under left drawer", "Physical");
        Clue clue4 = new Clue("The key is in the flower pot",
                "False bottom in the red vase", "Hidden");
        Clue clue5 = new Clue("Solve the riddle on the wall",
                "Answer is 'time'", "Riddle");

        // Mark some clues as solved
        clue1.solve();
        clue3.solve();

        // Sample Players with clues
        Player player1 = new Player("John Doe");
        player1.addSolvedClue(clue1);
        player1.addSolvedClue(clue2);

        Player player2 = new Player("Jane Smith");
        player2.addSolvedClue(clue3);

        Player player3 = new Player("Mike Johnson");
        player3.addSolvedClue(clue1);
        player3.addSolvedClue(clue4);
        player3.addSolvedClue(clue5);

        Player player4 = new Player("Sarah Williams");

        playerData.addAll(player1, player2, player3, player4);

        // Map players to rooms
        playerRoomMap.put(player1, hauntedMansion);
        playerRoomMap.put(player2, hauntedMansion);
        playerRoomMap.put(player3, prisonBreak);
        playerRoomMap.put(player4, spaceStation);

        // Sample Bookings
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        LocalDateTime dayAfter = LocalDateTime.now().plusDays(2);

//        Booking booking1 = new Booking(hauntedMansion, tomorrow.withHour(14).withMinute(0), 4);
//        booking1.setBookingId("B001");
//        booking1.addPlayer(player1);
//        booking1.addPlayer(player2);
//        booking1.setStatus(BookingStatus.CONFIRMED);

//        Booking booking2 = new Booking(prisonBreak, dayAfter.withHour(18).withMinute(30), 6);
//        booking2.setBookingId("B002");
//        booking2.addPlayer(player3);
//        booking2.addPlayer(player4);
//        booking2.setStatus(BookingStatus.CONFIRMED);

//        bookingData.addAll( booking2);
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
                String newId = String.valueOf(roomData.size() + 1);
                EscapeRoom newRoom = new EscapeRoom(newId, roomName, 3, 6); // Default difficulty 3, max players 6
            // TODO : add room to database
                roomData.add(newRoom);
                showAlert("Success", "Room '" + roomName + "' added successfully");
            }
        });
    }

    // TODO : delete edit room methon ( not required )
    private void editRoom(EscapeRoom room) {
        TextInputDialog dialog = new TextInputDialog(room.getName());
        dialog.setTitle("Edit Room");
        dialog.setHeaderText("Edit escape room details");
        dialog.setContentText("Enter new room name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(roomName -> {
            if (roomName.trim().isEmpty()) {
                showAlert("Error", "Room name cannot be empty");
            } else {
                room.setName(roomName);
                refreshTables();
                showAlert("Success", "Room updated successfully");
            }
        });
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
                    userData.add(newUser);
                    // TODO : add user to db
                    showAlert("Success", role + " user '" + username + "' added successfully");
                }
            });
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

    private void cancelBooking(Booking booking) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Cancellation");
        confirmation.setHeaderText("Cancel booking #" + booking.getBookingId() + "?");
        confirmation.setContentText("This will notify the customer.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                booking.setStatus(BookingStatus.CANCELLED);
                refreshTables();
                showAlert("Success", "Booking #" + booking.getBookingId() + " cancelled");
            }
        });
    }

    private void confirmBooking(Booking booking) {
        booking.setStatus(BookingStatus.CONFIRMED);
        refreshTables();
        showAlert("Success", "Booking #" + booking.getBookingId() + " confirmed");
    }

    // TODO : remove reset password
    private void resetUserPassword(User user) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reset Password");
        dialog.setHeaderText("Reset password for " + user.getUsername());
        dialog.setContentText("Enter new password:");

        dialog.showAndWait().ifPresent(newPassword -> {
            if (newPassword.length() < 6) {
                showAlert("Error", "Password must be at least 6 characters");
            } else {
                user.setPassword(newPassword); // In real app, hash this password
                showAlert("Success", "Password reset for " + user.getUsername());
            }
        });
    }

    private void deleteUser(User user) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete user " + user.getUsername() + "?");
        confirmation.setContentText("This action cannot be undone.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                userData.remove(user);
                showAlert("Success", "User " + user.getUsername() + " deleted");
            }
        });
    }

    private void addClueToPlayer(Player player) {
        Dialog<Clue> dialog = new Dialog<>();
        dialog.setTitle("Add New Clue");
        dialog.setHeaderText("Create a new clue for " + player.getName());

        // Set the button types
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

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

        // Convert the result to a clue when OK is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
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
            player.addSolvedClue(clue);
            refreshTables();
            showAlert("Success", "New clue added to " + player.getName());
        });
    }

    private void viewPlayerClues(Player player) {
        VBox clueBox = new VBox(10);
        clueBox.setPadding(new Insets(15));

        Label header = new Label("Clues Solved by " + player.getName());
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        int solvedCount = 0;
        for (Clue clue : player.getSolvedClues()) {
            if (clue.isSolved()) {
                solvedCount++;
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
        alert.setHeaderText("Total clues solved: " + solvedCount + "/" + player.getSolvedClues().size());
        alert.getDialogPane().setContent(scrollPane);
        alert.getDialogPane().setPrefSize(400, 300);
        alert.showAndWait();
    }

    @FXML
    private void refreshTables() {
        roomsTable.refresh();
        usersTable.refresh();
        bookingsTable.refresh();
        playersTable.refresh();
    }

    // Helper Methods
    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.minusHours(hours).toMinutes();
        return String.format("%02d:%02d", hours, minutes);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}