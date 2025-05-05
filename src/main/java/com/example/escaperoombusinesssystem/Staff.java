package com.example.escaperoombusinesssystem;

import java.time.LocalDateTime;

import static com.example.escaperoombusinesssystem.BookingStatus.CONFIRMED;

public class Staff extends User {
    public Staff(String username , String plainTextPassword) {
        super(username, "Staff" ,  plainTextPassword);
    }

    @Override
    public void accessDashboard() {

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

            // Reset all clues
            for (Clue clue : room.getClues()) {
                clue.unsolve();
            }

            // Clear player progress
            for (Booking booking : room.getBookings()) {
                for (Player player : booking.getPlayers()) {
                    player.getSolvedClues().clear();
                }

                // Mark old bookings as done
                if (booking.getDateTime().isBefore(LocalDateTime.now())) {
                    booking.setStatus(CONFIRMED);
                }
            }

            System.out.println("Reset room: " + room.getName());
        }

/**
 * Updates a clue's description and solution
 * @param clue The clue to update
 * @param newDescription The new description text
 * @param newSolution The new solution text
 */
public void updateClue(Clue clue, String newDescription, String newSolution) {
    if (clue == null) {
        throw new IllegalArgumentException("Clue cannot be null");
    }

    if (newDescription == null || newSolution == null) {
        throw new IllegalArgumentException("Description and solution cannot be null");
    }

    clue.setDescription(newDescription);
    clue.setSolution(newSolution);

    System.out.println("Clue updated successfully");
}



}

