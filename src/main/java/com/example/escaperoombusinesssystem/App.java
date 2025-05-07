package com.example.escaperoombusinesssystem;

import com.example.escaperoombusinesssystem.model.user.User;
import com.example.escaperoombusinesssystem.scenes.SignUp;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {


    @Override
    public void start(Stage stage) throws IOException {


        Parent root = FXMLLoader.load(getClass().getResource("view/loginView.fxml"));
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icon.png")));
        stage.getIcons().add(icon);
        stage.setTitle("Escape Room Booking System");
        stage.setScene(new Scene(root, 800, 600));
        stage.show();


//        stage.initStyle(StageStyle.UNDECORATED);
//
//
//        Scene scene = (new SignUp(stage)).getScene();
//        stage.setTitle("Escape Room");
//        stage.setScene(scene);
//        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}