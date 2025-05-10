package com.example.escaperoombusinesssystem.model.user;

import com.example.escaperoombusinesssystem.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static com.example.escaperoombusinesssystem.model.BookingStatus.CONFIRMED;

public class Staff extends User {
    public Staff(String username, String plainTextPassword) {
        super(username, "Staff", plainTextPassword);
    }

    @Override
    public void accessDashboard() {
        // Implementation for staff dashboard access
    }

    /**
     * Resets a room after a game by:
     * 1. Unsolving all clues
     * 2. Clearing player progress
     * 3. Marking past bookings as completed
     */
    public void resetRoom(EscapeRoom room) {
        if (room == null) {
            throw new IllegalArgumentException("Room is missing");
        }

        Connection conn = null;
        try {
            conn = DBConnector.connect();
            if (conn == null) {
                throw new SQLException("Failed to connect to database");
            }

            conn.setAutoCommit(false);

            // Reset all clues
            String sql = "UPDATE clues SET solved = false WHERE id IN (SELECT clue_id FROM escape_room_clues WHERE room_id = ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, room.getId());
                stmt.executeUpdate();
            }

            // Clear player progress
            String clearPlayerSql = "DELETE FROM solved_clues WHERE clue_id IN (SELECT clue_id FROM escape_room_clues WHERE room_id = ?)";
            try (PreparedStatement clearStmt = conn.prepareStatement(clearPlayerSql)) {
                clearStmt.setString(1, room.getId());
                clearStmt.executeUpdate();
            }
            String activateRoom = "UPDATE escape_rooms SET is_active = true WHERE id = ?";
            try (PreparedStatement clearStmt = conn.prepareStatement(activateRoom)) {
                clearStmt.setString(1, room.getId());
                clearStmt.executeUpdate();
            }

            // Mark old bookings as done
            String markBookingsSql = "UPDATE bookings SET status = ? WHERE room_id = ? AND booking_time < NOW()";
            try (PreparedStatement markStmt = conn.prepareStatement(markBookingsSql)) {
                markStmt.setString(1, BookingStatus.COMPLETED.name());
                markStmt.setString(2, room.getId());
                markStmt.executeUpdate();
            }

            conn.commit();
            System.out.println("Reset room: " + room.getName());

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Error resetting room: " + e.getMessage());
            throw new RuntimeException("Failed to reset room", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error closing connection: " + closeEx.getMessage());
                }
            }
        }
    }

    /**
     * Updates a clue's description and solution
     *
     * @param clue           The clue to update
     * @param newDescription The new description text
     * @param newSolution    The new solution text
     */
    public void updateClue(Clue clue, String newDescription, String newSolution) {
        if (clue == null) {
            throw new IllegalArgumentException("Clue cannot be null");
        }

        if (newDescription == null || newSolution == null) {
            throw new IllegalArgumentException("Description and solution cannot be null");
        }

        Connection conn = null;
        try {
            conn = DBConnector.connect();
            if (conn == null) {
                throw new SQLException("Failed to connect to database");
            }

            conn.setAutoCommit(false);

            String sql = "UPDATE clues SET description = ?, solution = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newDescription);
                stmt.setString(2, newSolution);
                stmt.setInt(3, clue.getId());
                stmt.executeUpdate();
            }

            conn.commit();
            clue.setDescription(newDescription);
            clue.setSolution(newSolution);
            System.out.println("Clue updated successfully in the database");

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Error updating clue: " + e.getMessage());
            throw new RuntimeException("Failed to update clue", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error closing connection: " + closeEx.getMessage());
                }
            }
        }
    }
}
