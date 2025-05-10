package com.example.escaperoombusinesssystem.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class EscapeRoom {
   private String id ;
   private String name;
    private int difficulty;
    private ArrayList<Clue> clues;
    private int maxPlayers;
    private List<Booking> bookings = new ArrayList<>();
    private boolean isActive = true; // Default to active

    public EscapeRoom(String id, String name, int difficulty, int maxPlayers){
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
           throw new IllegalArgumentException("Clue cannot be null");
       }

       Connection conn = DBConnector.connect();
       String sql = "insert into clues (description , solution , solved , type ) values (? , ? , ? , ?) ";


       try (PreparedStatement pst = conn.prepareStatement(sql)) {

           pst.setString(1, clue.getDescription());
           pst.setString(2, clue.getSolution());
           pst.setBoolean(3, clue.isSolved());
           pst.setString(4, clue.getType());
           pst.executeQuery();

           this.clues.add(clue);

       } catch (SQLException e) {
           throw new RuntimeException(e);
       }

    }

    // Static method to fetch EscapeRoom by ID from database
    public static EscapeRoom getById(String roomId) {
        Connection conn = DBConnector.connect();
        String sql = "SELECT * FROM escape_rooms WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, roomId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // Create and return the EscapeRoom object
                EscapeRoom room = new EscapeRoom(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getInt("difficulty"),
                        rs.getInt("max_players")
                );

                // Set active status if stored in DB
                if (rs.getBoolean("is_active")) {
                    room.activate();
                } else {
                    room.deactivate();
                }

                return room;
            } else {
                throw new RuntimeException("EscapeRoom not found with ID: " + roomId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
    public void toggleIsActive() {
        this.isActive = !isActive;
    }

    public void activate(){
        this.isActive = true;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getId() {
        return id;
    }

    public void setName(String roomName) {
        this.name = roomName;
    }
}
