module com.example.escaperoombusinesssystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
<<<<<<< HEAD
    requires io.github.cdimascio.dotenv.java;
=======
    requires jbcrypt;
>>>>>>> 6ba90a3ec4477c740073692730067ae1eab6564a


    opens com.example.escaperoombusinesssystem to javafx.fxml;
    exports com.example.escaperoombusinesssystem;
}