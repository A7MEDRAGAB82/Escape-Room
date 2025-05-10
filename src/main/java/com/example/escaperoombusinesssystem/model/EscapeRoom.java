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

   public void addClue(Clue clue) {
       if (clue == null) {
           throw new IllegalArgumentException("Clue cannot be null");
       }

       Connection conn = DBConnector.connect();
       try {
           // Insert clue and get generated clue id
           String clueSql = "INSERT INTO clues (description, solution, solved, type) VALUES (?, ?, ?, ?) RETURNING id";
           int clueId = -1;
           try (PreparedStatement pst = conn.prepareStatement(clueSql)) {
               pst.setString(1, clue.getDescription());
               pst.setString(2, clue.getSolution());
               pst.setBoolean(3, clue.isSolved());
               pst.setString(4, clue.getType());
               ResultSet rs = pst.executeQuery();
               if (rs.next()) {
                   clueId = rs.getInt(1);
               }
           }

           // Link clue to this room
           if (clueId != -1) {
               String linkSql = "INSERT INTO escape_room_clues (room_id, clue_id) VALUES (?, ?)";
               try (PreparedStatement pst = conn.prepareStatement(linkSql)) {
                   pst.setString(1, this.id);
                   pst.setInt(2, clueId);
                   pst.executeUpdate();
               }
           }

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

    public static List<Clue> loadCluesForRoom(String roomId) {
        List<Clue> clues = new ArrayList<>();
        try (Connection conn = DBConnector.connect()) {
            String sql = "SELECT c.id, c.description, c.solution, c.type, c.solved " +
                         "FROM clues c JOIN escape_room_clues erc ON c.id = erc.clue_id WHERE erc.room_id = ?";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, roomId);
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    Clue clue = new Clue(
                        rs.getString("description"),
                        rs.getString("solution"),
                        rs.getString("type")
                    );
                    if (rs.getBoolean("solved")) clue.solve();
                    clues.add(clue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clues;
    }
}
