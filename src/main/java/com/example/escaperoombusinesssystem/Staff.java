package com.example.escaperoombusinesssystem;

public class Staff extends User {
    public Staff(String username , String plainTextPassword) {
        super(username, "Staff" ,  plainTextPassword);
    }

    @Override
    public void accessDashboard() {

    }


    // public void resetRoom(Room com.example.escaperoombusinesssystem.EscapeRoom){}
    // public void updateClue(com.example.escaperoombusinesssystem.Clue clue ,String description){}



}
