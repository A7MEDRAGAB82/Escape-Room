package com.example.escaperoombusinesssystem;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import static com.example.escaperoombusinesssystem.BookingStatus.CANCELLED;
import static com.example.escaperoombusinesssystem.BookingStatus.CONFIRMED;

public class Booking {
    private String bookingId;
    private EscapeRoom room;
    private LocalDateTime dateTime;
    private ArrayList <Player> players;
    private String status;
    private static final LocalTime opening = LocalTime.of(10, 0);
    private static final LocalTime closing = LocalTime.of(22, 0);


    public Booking(EscapeRoom room, LocalDateTime dateTime, int players) {
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

       if (players > 5 || players < 2) {
            throw new IllegalArgumentException("Players must be at least 2 and at most 5");
       }
       else {
           this.players = new ArrayList<>(players);
       }

      this.status = CONFIRMED;
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

    public String getStatus() {
        return status;
    }

    public void cancel() {
        this.status = CANCELLED;
    }


    public boolean isActive() {
        return !this.status.equals(CANCELLED);
    }

}
