package com.example.escaperoombusinesssystem.model.user;

import com.example.escaperoombusinesssystem.model.Booking;
import com.example.escaperoombusinesssystem.model.BookingStatus;
import com.example.escaperoombusinesssystem.model.EscapeRoom;

import java.time.LocalDateTime;

public class Customer extends User {

    public Customer(String username, String password) {
        super(username, "Customer", password);
    }

    // Check room availability directly through EscapeRoom
    public boolean isRoomAvailable(EscapeRoom room, LocalDateTime dateTime) {
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
        room.addBooking(newBooking); // Auto-registers with the room
        return newBooking;
    }


    public BookingStatus checkStatus(Booking booking) {
        return booking.getStatus();
    }

    public void cancelBooking(Booking booking) {
        booking.cancel();
    }

    @Override
    public void accessDashboard() {
        System.out.println("Customer Dashboard: Book/Cancel Rooms");
    }
}