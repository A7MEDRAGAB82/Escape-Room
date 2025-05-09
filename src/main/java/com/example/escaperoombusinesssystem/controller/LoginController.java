package com.example.escaperoombusinesssystem.controller;

import com.example.escaperoombusinesssystem.model.user.Admin;
import com.example.escaperoombusinesssystem.model.user.Customer;
import com.example.escaperoombusinesssystem.model.user.Staff;
import com.example.escaperoombusinesssystem.model.user.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorMessageLabel;

//  private DBConnector dbConnector = DBConnector.getInstance();

    @FXML
    private void initialize() {
        // Clear any error messages when the form loads
//        errorMessageLabel.setText("");

        // Add listeners or initial setup if needed
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            return;
        }

        // TODO: Replace authenticateUser with a database call to validate user credentials
        User user = authenticateUser(username, password);

        if (user != null) {
            try {
                // TODO: Optionally, fetch user details/roles from the database here if not already done
                // Load appropriate dashboard based on user role
                String dashboardPath = getDashboardPathForRole(user.getRole());
                FXMLLoader loader = new FXMLLoader(getClass().getResource(dashboardPath));
                Parent root = loader.load();

                // Get the controller and set the user
                Object controller = loader.getController();
                if (controller != null) {
                    passUserToController(controller, user);
                }

                // Show the dashboard
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle(user.getRole() + " Dashboard");
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private User authenticateUser(String username, String password) {
        // TODO: Implement user authentication using the database
        // Example: return dbConnector.getUser(username, password);

        // only demo , delete after db addition
        if (username.equals("admin") && password.equals("admin123")) {
            System.out.println("Admin logged in");
            return new Admin(username, password);
        } else if (username.equals("staff") && password.equals("staff123")) {
            return new Staff(username, password);
        } else if (username.equals("customer") && password.equals("customer123")) {
            return new Customer(username, password);
        }

        return null;
    }

    private String getDashboardPathForRole(String role) {
        // TODO: If dashboard paths are stored in the database, fetch them here
        return switch (role.toLowerCase()) {
            case "admin" -> "/com/example/escaperoombusinesssystem/view/adminDashboard.fxml";
            case "staff" -> "/com/example/escaperoombusinesssystem/view/staffDashboard.fxml";
            case "customer" -> "/com/example/escaperoombusinesssystem/view/CustomerDashboard.fxml";
            default -> "/com/example/escaperoombusinesssystem/view/LoginView.fxml";
        };
    }

    private void passUserToController(Object controller, User user) {
        // TODO: If user objects are loaded from the database, ensure all required fields are set before passing
        if (controller instanceof AdminController && user instanceof Admin) {
            ((AdminController) controller).setUser(user);
        } else if (controller instanceof StaffController && user instanceof Staff) {
            ((StaffController) controller).setStaff((Staff) user);
        } else if (controller instanceof CustomerController && user instanceof Customer) {
            ((CustomerController) controller).setCustomer((Customer) user);
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        // Create the registration dialog
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Register New User");
        dialog.setHeaderText("Please enter your details:");

        // Set up the form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("admin", "staff", "customer");
        roleBox.setValue("customer");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);
        grid.add(new Label("Role:"), 0, 3);
        grid.add(roleBox, 1, 3);

        dialog.getDialogPane().setContent(grid);

        ButtonType registerButtonType = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(registerButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == registerButtonType) {
                String username = usernameField.getText().trim();
                String password = passwordField.getText();
                String confirmPassword = confirmPasswordField.getText();
                String role = roleBox.getValue();

                // Basic validation
                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    showAlert("Error", "All fields are required.");
                    return null;
                }
                if (!password.equals(confirmPassword)) {
                    showAlert("Error", "Passwords do not match.");
                    return null;
                }
                // TODO: Check if username already exists in the database

                // TODO: Save the new user to the database here
                // For now, just create the user object
                switch (role.toLowerCase()) {
                    case "admin": return new Admin(username, password);
                    case "staff": return new Staff(username, password);
                    case "customer": return new Customer(username, password);
                    default: return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(user -> {
            showAlert("Success", "Registration successful for user: " + user.getUsername());
            // TODO: Optionally, log the user in or redirect to login screen
        });
    }

    // Helper method for alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}