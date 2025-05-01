package com.example.escaperoombusinesssystem.scenes;

import com.example.escaperoombusinesssystem.DBConnector;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class SignUp {
    Stage stage;
    Label sceneTitle;
    Label usernameLabel, passwordLabel, confirmPasswordLabel;
    TextField usernameTextField, passwordTextField, confirmPasswordTextField;
    Button signUpButton, signInButton;
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
        signInButton = new Button("Sign In");
        signUpGrid = new GridPane();

      signUpGrid.addColumn(0,sceneTitle,usernameLabel,usernameTextField,passwordLabel,
              passwordTextField,confirmPasswordLabel,confirmPasswordTextField, signUpButton, signInButton);
      signUpGrid.getStyleClass().add("signUpGrid");
        signUpGrid.setAlignment(Pos.CENTER);
        signUpGrid.setVgap(10);

    }

    public void initActions(){
        signUpButton.setOnAction(e->{
            try {
                Connection con = DBConnector.connect();
                String query = "INSERT INTO users (username, password) VALUES (?,?)";
                PreparedStatement statement = con.prepareStatement(query);
                statement.setString(1, usernameTextField.getText());
                statement.setString(2, passwordTextField.getText());
                int row = statement.executeUpdate();
                if (row > 0) {
                    System.out.println("Row inserted successfully");
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

        });
    }

    public Scene getScene() {

        ImageView bgImage = new ImageView(new Image(String.valueOf(getClass().getResource("bg.jpg"))));
        bgImage.setFitWidth(700);
        bgImage.setFitHeight(500);
        bgImage.setOpacity(0.1);
        StackPane root = new StackPane(bgImage, signUpGrid);
        VBox container =new VBox();
        container.getChildren().addAll((new TitleBar(stage)).getTitleBar(),root);
        container.setPadding(new Insets(10,10,10,10));
        container.setAlignment(Pos.CENTER);
        Scene signUpScene =  new Scene(container,700,550);
        signUpScene.getStylesheets().add(getClass().getResource("main.css").toExternalForm());
        return signUpScene;
    }
}