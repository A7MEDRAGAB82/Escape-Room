module com.example.escaperoombusinesssystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;
    requires jbcrypt;


    opens com.example.escaperoombusinesssystem to javafx.fxml;
    exports com.example.escaperoombusinesssystem;
}