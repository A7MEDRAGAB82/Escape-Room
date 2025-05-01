package com.example.escaperoombusinesssystem.scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class TitleBar {
    Stage stage;
    TitleBar(Stage stage) {
        this.stage = stage;
    }
    public HBox getTitleBar() {
        HBox titleBar = new HBox();
        Label title = new Label("Escape Room");
        title.setFont(Font.font(20));
        Button exit = new Button("\uD83D\uDDD9");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        titleBar.getChildren().addAll(title,spacer, exit);
        titleBar.setAlignment(Pos.CENTER);
        titleBar.setPadding(new Insets(10,20,10,20));
        exit.setOnAction(e -> {stage.close();});

        return titleBar;
    }
}
