package com.example.escaperoombusinesssystem;

import com.example.escaperoombusinesssystem.scenes.SignUp;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class EscapeRoomApp extends Application {
    public static boolean isLoggedIn = false;
    public static User currentUser = null;

    @Override
    public void start(Stage stage) throws IOException {

        stage.initStyle(StageStyle.UNDECORATED);
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icon.png")));
        stage.getIcons().add(icon);

        Scene scene = (new SignUp(stage)).getScene();
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}