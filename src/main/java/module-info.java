module com.example.escaperoombusinesssystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires rt;


    opens com.example.escaperoombusinesssystem to javafx.fxml;
    exports com.example.escaperoombusinesssystem;
}