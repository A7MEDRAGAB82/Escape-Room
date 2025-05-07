package com.example.escaperoombusinesssystem.model;

import java.util.*;

public class EscapeRoom {
   private String id ;
   private String name;
    private int difficulty;
    private ArrayList<Clue> clues;
    private int maxPlayers;
    private List<Booking> bookings = new ArrayList<>();
    private boolean isActive = true; // Default to active

    EscapeRoom(String id ,String name,int difficulty,int maxPlayers){
        if (id == null || name == null) {
            throw new IllegalArgumentException("ID and name cannot be null");
        }
        if (difficulty <= 0 || maxPlayers <= 0) {
            throw new IllegalArgumentException("Difficulty and maxPlayers must be positive");
        }
        this.id=id;
        this.name=name;
        this.difficulty=difficulty;
        this.maxPlayers=maxPlayers;
        this.clues = new ArrayList<>();
    }

   public void addClue(Clue clue){
       if (clue == null) {
           throw new IllegalArgumentException("com.example.escaperoombusinesssystem.model.Clue cannot be null");
       }
this.clues.addLast(clue);
    }

   public ArrayList<Clue> getClues(){
return clues;
    }

    public String getName() {
        return name;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    // The getBookings() method implementation
    public List<Booking> getBookings() {
        return new ArrayList<>(bookings); // Return a copy
    }

    public void deactivate() {
        this.isActive = false;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getId() {
        return id;
    }
}
