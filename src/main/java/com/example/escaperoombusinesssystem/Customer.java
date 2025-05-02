package com.example.escaperoombusinesssystem;

import javafx.stage.Stage;

public class Customer extends User {
    public Customer(String username , String plainTextPassword) {
        super(username, "Customer" ,  plainTextPassword);
    }

    @Override
    public void accessDashboard(Stage stage) {

    }


    // public com.example.escaperoombusinesssystem.Booking makeBooking(com.example.escaperoombusinesssystem.EscapeRoom room, LocalDateTime dateTime, int players) throws exception{}
    // public String viewProgress(com.example.escaperoombusinesssystem.Booking booking)


}
