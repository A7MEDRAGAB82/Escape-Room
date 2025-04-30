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
        sceneTitle.getStyleClass().add("sceneTitle");
        usernameLabel = new Label("Username: ");
        passwordLabel = new Label("Password: ");
        confirmPasswordLabel = new Label("Confirm Password: ");
        usernameTextField = new TextField();
        passwordTextField = new TextField();
        confirmPasswordTextField = new TextField();
        signUpButton = new Button("Sign Up");
        signUpGrid = new GridPane();

      signUpGrid.addColumn(0,sceneTitle,usernameLabel,usernameTextField,passwordLabel,
              passwordTextField,confirmPasswordLabel,confirmPasswordTextField, signUpButton);
      signUpGrid.getStyleClass().add("signUpGrid");
        signUpGrid.setAlignment(Pos.CENTER);
//        signUpGrid.setHgap(10);
        signUpGrid.setVgap(15);

    }

    public void initActions(){

    }

    public Scene getScene() {
        Scene signUpScene =  new Scene(signUpGrid,700,500);
        signUpScene.getStylesheets().add(getClass().getResource("main.css").toExternalForm());
        return signUpScene;
    }
}