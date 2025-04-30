module com.example.escaperoombusinesssystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.escaperoombusinesssystem to javafx.fxml;
    exports com.example.escaperoombusinesssystem;
}