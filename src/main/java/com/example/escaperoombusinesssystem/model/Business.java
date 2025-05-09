package com.example.escaperoombusinesssystem.model;

import java.util.ArrayList;

public class Business {
    private String name;
    private static ArrayList<EscapeRoom> rooms = new ArrayList<>();


    public Business(String name) {
        this.name = name;
    }

    // Core Methods

    /**
     * Adds a new escape room to the business
     * @throws Exception if room already exists
     */
    public static void addRoom(EscapeRoom room) throws IllegalArgumentException,IllegalStateException {
        if (rooms.contains(room)) { //contains is a method in arraylist
            throw new IllegalStateException("Room '" + room.getName() + "' already exists!");
        }
        rooms.add(room);
    }

    /**
     * Searches for a room by its unique ID (exact match, case-sensitive).
     * Uses binary search for O(log n) efficiency.
     * @return The found room, or null if not found.
     */
    // selection sort and binary search for learning purpose
    public EscapeRoom searchRoomById(String id) {
        // Ensure rooms are sorted by ID first
        selectionSortRoomsById(rooms);

        int left = 0;
        int right = rooms.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            String currentId = rooms.get(mid).getId();
            int comparison = currentId.compareTo(id);

            if (comparison == 0) {
                return rooms.get(mid); // Exact match found
            } else if (comparison < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return null; // Not found
    }

    /**
     * Helper: Selection Sort to sort rooms by ID.
     */
    private void selectionSortRoomsById(ArrayList<EscapeRoom> rooms) {
        for (int i = 0; i < rooms.size() - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < rooms.size(); j++) {
                if (rooms.get(j).getId().compareTo(rooms.get(minIndex).getId()) < 0) {
                    minIndex = j;
                }
            }
            // Swap
            EscapeRoom temp = rooms.get(minIndex);
            rooms.set(minIndex, rooms.get(i));
            rooms.set(i, temp);
        }
    }


    public String getName() {
        return name;
    }

    public static ArrayList<EscapeRoom> getRooms() {
        return new ArrayList<>(rooms); // Returns a copy to prevent external modification
    }

      //Gets total number of rooms

    public int getRoomCount() {
        return rooms.size();
    }

   
    public boolean removeRoom(EscapeRoom room) {
        return rooms.remove(room);
    }


}