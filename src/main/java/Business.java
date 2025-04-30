import java.util.ArrayList;

public class Business {
    private String name;
    private ArrayList<EscapeRoom> rooms;


    public Business(String name) {
        this.name = name;
        this.rooms = new ArrayList<>();
    }

    // Core Methods

    /**
     * Adds a new escape room to the business
     * @throws Exception if room already exists
     */
    public void addRoom(EscapeRoom room) throws Exception { //throws means "I might throw this type of exception - be ready to handle it!"
        if (rooms.contains(room)) { //contains is a method in arraylist
            throw new Exception("Room '" + room.getName() + "' already exists!");
        }
        rooms.add(room);
    }

    /**
     * Searches rooms by name (case-insensitive partial match)
     * @return List of matching rooms
     */
    public ArrayList<EscapeRoom> searchRooms(String query) {
        ArrayList<EscapeRoom> results = new ArrayList<>(); //result for searching
        String lowerQuery = query.toLowerCase(); //converting to lower case to prevent search conflict
/*
* "Pirate".equals("pirate")  // → false
"Pirate".contains("pir")   // → false (because 'P' vs 'p') */
        for (EscapeRoom room : rooms) {
            if (room.getName().toLowerCase().contains(lowerQuery)) {
                results.add(room); //result for searching
            }
        }
        return results;
    }


    public String getName() {
        return name;
    }

    public ArrayList<EscapeRoom> getRooms() {
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