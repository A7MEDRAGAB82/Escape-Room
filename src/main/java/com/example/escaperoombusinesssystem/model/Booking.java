package com.example.escaperoombusinesssystem.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class Booking {
    private String bookingId;
    private EscapeRoom room;
    private LocalDateTime dateTime;
    private ArrayList <Player> players;
    private BookingStatus status;
//    private String customerId;
    private static final LocalTime opening = LocalTime.of(10, 0);
    private static final LocalTime closing = LocalTime.of(22, 0);


    public Booking(EscapeRoom room, LocalDateTime dateTime, int players ) {
        // Validate dateTime (existing checks)
        if (dateTime == null) {
            throw new IllegalArgumentException("Date Time can't be NULL");
        }
        else if (dateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("It's not possible to make a booking for a past time");
        }
        LocalTime bookingTime = dateTime.toLocalTime();
        if (bookingTime.isBefore(opening) || bookingTime.isAfter(closing)) {
            throw new IllegalArgumentException("Working hours are from 10 AM to 10 PM");
        }
        else {
            this.dateTime = dateTime;
        }

        // Validate players (existing checks)
        if (players > room.getMaxPlayers() || players < 2) {
            throw new IllegalArgumentException("Players must be at least 2 and at most 5");
        }
        else {
            this.players = new ArrayList<>(players);
        }

        this.room = room;

        // Check for overlapping bookings
        for (Booking existingBooking : room.getBookings()) {
            if (existingBooking.getDateTime().equals(dateTime) && existingBooking.isActive()) {
                throw new IllegalStateException("Room is already booked for this time slot");
            }
        }

        this.status = BookingStatus.CONFIRMED;
//        this.customerId = customerId;
        // Add this booking to the room's bookings list
        room.addBooking(this);
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public EscapeRoom getRoom() {
        return room;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void cancel() {
        this.status = BookingStatus.CANCELLED;
    }


    public boolean isActive() {
        return this.status != BookingStatus.CANCELLED;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }
//
//    public String getCustomerId() {
//        return customerId;
//    }
}
