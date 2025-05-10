package com.example.escaperoombusinesssystem.model.user;

import com.example.escaperoombusinesssystem.model.Booking;
import com.example.escaperoombusinesssystem.model.BookingStatus;
import com.example.escaperoombusinesssystem.model.DBConnector;
import com.example.escaperoombusinesssystem.model.EscapeRoom;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Customer extends User {
    private final String customerId;  // final because it's set once at creation

    public Customer(String username, String password) {
        super(username, "Customer", password);
        this.customerId = UUID.randomUUID().toString();  // Auto-generate ID
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
        String sql = "INSERT INTO bookings (room_id, booking_time, status, created_at, customer_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, room.getId());
            pst.setObject(2, newBooking.getDateTime());
            pst.setString(3, "CONFIRMED");
            pst.setObject(4, LocalDateTime.now());
            pst.setString(5, this.customerId);
            pst.executeUpdate();

            // Get the generated booking ID
            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    newBooking.setBookingId(rs.getString(1));
                }
            }

            room.addBooking(newBooking);
            return newBooking;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Booking> getMyBookings() {
        List<Booking> myBookings = new ArrayList<>();
        Connection conn = DBConnector.connect();
        String sql = "SELECT * FROM bookings WHERE customer_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, this.customerId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                // You'll need to implement a method to get EscapeRoom by ID
                EscapeRoom room = EscapeRoom.getById(rs.getString("room_id"));
                Booking booking = new Booking(
                        room,
                        rs.getObject("booking_time", LocalDateTime.class),
                        rs.getInt("player_count")
                );
                booking.setBookingId(rs.getString("booking_id"));
                booking.setStatus(BookingStatus.valueOf(rs.getString("status")));
                myBookings.add(booking);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return myBookings;
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
    public String getCustomerId() {
        return customerId;
    }

    @Override
    public void accessDashboard() {
        System.out.println("Customer Dashboard: Book/Cancel Rooms");
    }
}