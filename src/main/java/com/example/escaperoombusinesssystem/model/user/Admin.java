package com.example.escaperoombusinesssystem.model.user;

import com.example.escaperoombusinesssystem.model.*;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

import static com.example.escaperoombusinesssystem.model.BookingStatus.CANCELLED;
import static com.example.escaperoombusinesssystem.model.BookingStatus.CONFIRMED;

public class Admin extends User {

    public Admin(String username, String plainTextPassword) {
        super(username, "Admin", plainTextPassword);
    }

    public Report generateReport() {
        // TODO: Fetch all rooms and bookings from the database instead of Business
        // class
        Map<String, Object> reportData = new HashMap<>();

        Connection connection = null;

        try {

            connection = DBConnector.connect();

            // Calculate basic statistics
            int totalBookings = 0;
            int confirmedBookings = 0;
            int cancelledBookings = 0;
            List<EscapeRoom> allRooms = Business.getRooms(); // Get all escape rooms from the Business class

            if (allRooms == null || allRooms.isEmpty()) {
                reportData.put("error", "No rooms available");
                return new Report(reportData);
            }

            for (EscapeRoom room : allRooms) {
                if (room.getBookings() == null)
                    continue; // Skip null bookings
                totalBookings += room.getBookings().size();
                for (Booking booking : room.getBookings()) {
                    BookingStatus status = booking.getStatus();
                    if (CONFIRMED.equals(status)) {
                        confirmedBookings++;
                    } else if (CANCELLED.equals(status)) {
                        cancelledBookings++;
                    }
                }
            }

            // Insertion Sort implementation for sorting rooms by popularity
            for (int i = 1; i < allRooms.size(); i++) { // Start from the 2nd element (index 1)
                EscapeRoom key = allRooms.get(i); // Current room to be placed in the correct position
                int j = i - 1; // Start comparing with the previous room (sorted part of the list)

                // Shift elements of the sorted section to the right until the correct position
                // for 'key' is found
                while (j >= 0 && allRooms.get(j).getBookings().size() < key.getBookings().size()) {
                    // Move the room with fewer bookings to the right
                    allRooms.set(j + 1, allRooms.get(j));
                    j--; // Move left to check the next room in the sorted section
                }

                // Place 'key' in its correct position (now the list is sorted up to index i)
                allRooms.set(j + 1, key);
            }

            // Add core data to the map
            reportData.put("totalBookings", Optional.of(totalBookings));
            reportData.put("confirmedBookings", Optional.of(confirmedBookings));
            reportData.put("cancelledBookings", Optional.of(cancelledBookings));
            reportData.put(
                    "popularRooms", // Key for the report data map
                    allRooms.subList(0, Math.min(3, allRooms.size())) // Value: Top 3 rooms (or fewer if the list is
                                                                      // small)
            // =============================================
            // 1. `Math.min(3, allRooms.size())` ensures we never exceed the list's bounds.
            // - If `allRooms` has 2 rooms, it returns 2.
            // - If `allRooms` has 5 rooms, it returns 3.
            // 2. `subList(0, X)` extracts the first X rooms (already sorted by popularity).
            // - Safely avoids `IndexOutOfBoundsException`.
            );
            reportData.put("notes", "Report generated by " + username);

            // Insert into Supabase
            String insertSql = "INSERT INTO reports (data) VALUES (?::jsonb)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertSql)) {
                pstmt.setString(1, new Gson().toJson(reportData));
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            reportData.put("error", "Failed to generate report: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return new Report(reportData);
    }

    public void addRoom(EscapeRoom room) throws IllegalArgumentException, IllegalStateException {
        // TODO: Add the new room to the database

        if (room == null) {
            throw new IllegalArgumentException("Room cannot be null!");
        }
        if (Business.getRooms().contains(room)) {
            throw new IllegalStateException("Room '" + room.getName() + "' already exists!");
        }

        Connection conn = DBConnector.connect();
        String sql = "insert into escape_rooms (name , difficulty  , max_players ,  is_active , created_at) values(?, ?, ?, ?, ?) ";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, room.getName());
            pst.setInt(2, room.getDifficulty());
            pst.setInt(3, room.getMaxPlayers());
            pst.setBoolean(4, room.isActive());
            pst.setObject(5, LocalDateTime.now());
            int row = pst.executeUpdate();
            Business.addRoom(room);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void toggleRoomIsActive(EscapeRoom room) {
        if (room == null)
            throw new IllegalArgumentException("Room cannot be null!");

        Connection conn = DBConnector.connect();
        String sql = "UPDATE escape_rooms SET is_active = ? WHERE id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            room.toggleIsActive(); // Toggle the status in memory
            pst.setBoolean(1, room.isActive());
            pst.setString(2, room.getId());
            pst.executeUpdate();
            System.out.println("Room '" + room.getName() + (room.isActive() ? "' activated." : "' deactivated."));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update room status: " + e.getMessage());
        }
    }

    @Override
    public void accessDashboard() {

    }
}
