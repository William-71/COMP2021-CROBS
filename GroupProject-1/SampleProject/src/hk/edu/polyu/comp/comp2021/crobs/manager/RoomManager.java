package hk.edu.polyu.comp.comp2021.crobs.manager;

import hk.edu.polyu.comp.comp2021.crobs.model.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Placeholder for Member B — room management (REQ7–REQ9, REQ15–REQ16).
 */
public class RoomManager {
    private final Map<String, Room> rooms;
    private int nextRoomNumber;

    public RoomManager() {
        this.rooms = new HashMap<String, Room>();
        this.nextRoomNumber = 1;
    }

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public boolean hasRoom(String roomId) {
        return rooms.containsKey(roomId);
    }

    public List<Room> getAllRooms() {
        return new ArrayList<Room>(rooms.values());
    }

    // ---- Stubs for Member B ----

    public Room addRoom(String roomType, int capacity, String features) {
        throw new UnsupportedOperationException("Not implemented: addRoom");
    }

    public Room modifyRoom(String roomId, String field, String newValue) {
        throw new UnsupportedOperationException("Not implemented: modifyRoom");
    }

    public Room deleteRoom(String roomId) {
        throw new UnsupportedOperationException("Not implemented: deleteRoom");
    }
}
