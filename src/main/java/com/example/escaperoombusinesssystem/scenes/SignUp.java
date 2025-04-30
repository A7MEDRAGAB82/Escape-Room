package com.example.escaperoombusinesssystem.scenes;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class SignUp {
    Stage stage;
    Label sceneTitle;
    Label usernameLabel, passwordLabel, confirmPasswordLabel;
    TextField usernameTextField, passwordTextField, confirmPasswordTextField;
    Button signUpButton;
    GridPane signUpGrid;

    public SignUp(Stage stage) {
        this.stage = stage;
        initControls();
        initActions();
    }

    public void initControls(){
        sceneTitle = new Label("Sign Up");
        usernameLabel = new Label("Username: ");
        passwordLabel = new Label("Password: ");
        confirmPasswordLabel = new Label("Confirm Password: ");
        usernameTextField = new TextField();
        passwordTextField = new TextField();
        confirmPasswordTextField = new TextField();
        signUpButton = new Button("Sign Up");
        signUpGrid = new GridPane();

        signUpGrid.add(sceneTitle, 0, 0);
        signUpGrid.add(usernameLabel, 0, 1);
        signUpGrid.add(usernameTextField, 0, 1);
        signUpGrid.add(passwordLabel, 0, 2);
        signUpGrid.add(passwordTextField, 0, 2);
        signUpGrid.add(confirmPasswordLabel, 0, 3);
        signUpGrid.add(confirmPasswordTextField, 0, 3);
        signUpGrid.add(signUpButton, 0, 4);

        signUpGrid.setAlignment(Pos.CENTER);
        signUpGrid.setHgap(10);
        signUpGrid.setVgap(10);

    }

    public void initActions(){

    }

    public Scene getScene() {
        return new Scene(signUpGrid,400,500);
    }
}