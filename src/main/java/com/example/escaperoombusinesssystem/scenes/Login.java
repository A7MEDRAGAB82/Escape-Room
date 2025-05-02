package com.example.escaperoombusinesssystem.scenes;


import com.example.escaperoombusinesssystem.DBConnector;
import com.example.escaperoombusinesssystem.EscapeRoomApp;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class Login {
    Stage stage;
    Label sceneTitle;
    Label usernameLabel, passwordLabel;
    TextField usernameTextField, passwordTextField;
    Button loginButton;
    GridPane loginGrid;

    public Login(Stage stage) {
        this.stage = stage;
        initControls();
        initActions();
    }

    public void initControls(){
        sceneTitle = new Label("Login");
        sceneTitle.getStyleClass().add("sceneTitle");
        usernameLabel = new Label("Username: ");
        passwordLabel = new Label("Password: ");
        usernameTextField = new TextField();
        passwordTextField = new TextField();
        loginButton = new Button("Login");
        loginGrid = new GridPane();

        loginGrid.addColumn(0,sceneTitle,usernameLabel,usernameTextField,passwordLabel,
                passwordTextField, loginButton);
        loginGrid.getStyleClass().add("loginGrid");
        loginGrid.setAlignment(Pos.CENTER);
        loginGrid.setVgap(10);

    }

    public void initActions(){
        loginButton.setOnAction(e->{
            if (usernameTextField.getText().isEmpty() || passwordTextField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Please fill all the fields");
                alert.showAndWait();
            } else {
                // TODO: implement db query here
                EscapeRoomApp.isLoggedIn = true;
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Logged in Successfully");
                alert.setTitle("Success");
                alert.showAndWait();
                stage.setScene((new Login(stage)).getScene());
            }

        });
    }

    public Scene getScene() {

        ImageView bgImage = new ImageView(new Image(String.valueOf(getClass().getResource("bg.jpg"))));
        bgImage.setFitWidth(700);
        bgImage.setFitHeight(500);
        bgImage.setOpacity(0.1);
        StackPane root = new StackPane(bgImage, loginGrid);
        VBox container =new VBox();
        container.getChildren().addAll((new TitleBar(stage)).getTitleBar(),root);
        container.setPadding(new Insets(10,10,10,10));
        container.setAlignment(Pos.CENTER);
        Scene signUpScene =  new Scene(container,700,550);
        signUpScene.getStylesheets().add(getClass().getResource("main.css").toExternalForm());
        return signUpScene;
    }
}
