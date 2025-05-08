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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorMessageLabel;

//    private DBConnector dbConnector = DBConnector.getInstance();

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
//            errorMessageLabel.setText("Please enter both username and password.");
            return;
        }
        System.out.println("first check");
        // Try to authenticate user
        User user = authenticateUser(username, password);
        System.out.println(user.getRole());

        if (user != null) {
            try {
                // Load appropriate dashboard based on user role
                String dashboardPath = getDashboardPathForRole(user.getRole());

                Parent root = FXMLLoader.load(getClass().getResource(dashboardPath));

                FXMLLoader loader = new FXMLLoader(getClass().getResource(dashboardPath));

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
//                errorMessageLabel.setText("Error loading dashboard. Please try again.");
            }
        } else {
//            errorMessageLabel.setText("Invalid username or password. Please try again.");
        }
    }

    private User authenticateUser(String username, String password) {
        // In a real application, this would check against a database
        // For simplicity, we'll use some hardcoded users

        // Normally, you would use the DBConnector to fetch from database:
        // return dbConnector.getUser(username, password);

        // Mock users for development/testing
        if (username.equals("admin") && password.equals("admin123")) {
            System.out.println("Admin logged in");
            return new Admin(username, password);
        } else if (username.equals("staff") && password.equals("staff123")) {
            return new Staff(username, password);
        } else if (username.equals("customer") && password.equals("customer123")) {
            return new Customer(username, password);
        }

        return null; // Authentication failed
    }

    private String getDashboardPathForRole(String role) {
        switch (role.toLowerCase()) {
            case "admin":
                return "/com/example/escaperoombusinesssystem/view/adminDashboard.fxml";
            case "staff":
                return "/com/example/escaperoombusinesssystem/view/staffDashboard.fxml";
            case "customer":
                return "/com/example/escaperoombusinesssystem/view/CustomerDashboard.fxml";
            default:
                return "/com/example/escaperoombusinesssystem/view/LoginView.fxml";
        }
    }

    private void passUserToController(Object controller, User user) {
        // Pass the authenticated user to the appropriate controller
//        if (controller instanceof AdminController && user instanceof Admin) {
//            ((AdminController) controller).setAdmin((Admin) user);
//        } else if (controller instanceof StaffController && user instanceof Staff) {
//            ((StaffController) controller).setStaff((Staff) user);
//        } else if (controller instanceof CustomerController && user instanceof Customer) {
//            ((CustomerController) controller).setCustomer((Customer) user);
//        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        // In a real application, this would navigate to a registration form
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration");
        alert.setHeaderText(null);
        alert.setContentText("Registration feature not implemented yet.");
        alert.showAndWait();
    }
}