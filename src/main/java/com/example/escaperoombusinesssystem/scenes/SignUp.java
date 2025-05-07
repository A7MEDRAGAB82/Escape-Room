package com.example.escaperoombusinesssystem.scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class SignUp {
    // declare all scene components
    Stage stage;
    Label sceneTitle;
    Label usernameLabel, passwordLabel, confirmPasswordLabel;
    TextField usernameTextField;
    PasswordField passwordTextField, confirmPasswordTextField;
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
        passwordTextField = new PasswordField();
        confirmPasswordTextField = new PasswordField();
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
            if (usernameTextField.getText().isEmpty() || passwordTextField.getText().isEmpty() || confirmPasswordTextField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Please fill all the fields");
                alert.setTitle("Error");
                alert.showAndWait();
            } else if (!passwordTextField.getText().equals(confirmPasswordTextField.getText())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Passwords do not match");
                alert.setTitle("Error");
                alert.showAndWait();
            } else {
                // TODO: implement db query here
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Signed Up Successfully");
                alert.setTitle("Success");
                alert.showAndWait();
                stage.setScene((new Login(stage)).getScene());
            }
        });


        signInButton.setOnAction(e->{
           stage.setScene((new Login(stage)).getScene());
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
        signUpScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        return signUpScene;
    }
}