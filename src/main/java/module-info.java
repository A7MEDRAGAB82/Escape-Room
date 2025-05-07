module com.example.escaperoombusinesssystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;
    requires jbcrypt;
    requires mysql.connector.j;


    // Add this line to export your controller package:
    exports com.example.escaperoombusinesssystem.controller to javafx.fxml;

    // If you're using @FXML annotations, you also need:
    opens com.example.escaperoombusinesssystem.controller to javafx.fxml;

    opens com.example.escaperoombusinesssystem to javafx.fxml;
    exports com.example.escaperoombusinesssystem;
    exports com.example.escaperoombusinesssystem.model.user;
    opens com.example.escaperoombusinesssystem.model.user to javafx.fxml;
    opens com.example.escaperoombusinesssystem.model to javafx.fxml;
    exports com.example.escaperoombusinesssystem.model;
}