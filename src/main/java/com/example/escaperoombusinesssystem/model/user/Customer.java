package com.example.escaperoombusinesssystem.model.user;

import com.example.escaperoombusinesssystem.model.Booking;
import com.example.escaperoombusinesssystem.model.BookingStatus;
import com.example.escaperoombusinesssystem.model.DBConnector;
import com.example.escaperoombusinesssystem.model.EscapeRoom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class Customer extends User {

    public Customer(String username, String password) {
        super(username, "Customer", password);
    }

    // Check room availability directly through EscapeRoom
    public boolean isRoomAvailable(EscapeRoom room, LocalDateTime dateTime) {
        // TODO: Check room availability from the database
        for (Booking booking : room.getBookings()) {
            if (booking.getDateTime().equals(dateTime) && booking.isActive()) {
                return false; // Room is booked at this time
            }
        }
        return true; // Time slot available
    }

    // Book a room (automatically adds to EscapeRoom's bookings)
    public Booking makeBooking(EscapeRoom room, LocalDateTime dateTime, int players) {
        if (!isRoomAvailable(room, dateTime)) {
            throw new RuntimeException("Room not available at " + dateTime);
        }
        Booking newBooking = new Booking(room, dateTime, players);

        Connection conn = DBConnector.connect();
        String sql = "insert into bookings ( room_id , booking_time , status , created_at) values ( ?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, room.getId());
            pst.setObject(2, newBooking.getDateTime());
            if (newBooking.getStatus() == BookingStatus.CONFIRMED) {
                pst.setString(3, "CONFIRMED");
            } else {
                throw new IllegalStateException("Failed to confirm your booking");
            }
            pst.setObject(4, LocalDateTime.now());
            pst.executeQuery();

            room.addBooking(newBooking); // Auto-registers with the room
            return newBooking;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

        public BookingStatus checkStatus(Booking booking) {
        // TODO: Fetch the latest booking status from the database
        return booking.getStatus();
    }

    public void cancelBooking(Booking booking) {
        Connection conn = DBConnector.connect();
        String sql = "update bookings set status = ? where booking_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, "CANCELLED");
            pst.setString(2, booking.getBookingId());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        booking.cancel();
    }

    @Override
    public void accessDashboard() {
        System.out.println("Customer Dashboard: Book/Cancel Rooms");
    }
}