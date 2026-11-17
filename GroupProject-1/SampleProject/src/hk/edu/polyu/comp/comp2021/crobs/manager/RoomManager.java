package hk.edu.polyu.comp.comp2021.crobs.manager;

import hk.edu.polyu.comp.comp2021.crobs.model.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomManager {

    public static final String ERR_ROOM_NOT_FOUND = "Error: Room not found.";
    public static final String ERR_INVALID_FIELD = "Error: Invalid field.";

    public static final String FIELD_ROOM_TYPE = "roomType";
    public static final String FIELD_CAPACITY = "capacity";
    public static final String FIELD_FEATURES = "features";

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
        return roomId != null && rooms.containsKey(roomId);
    }

    public List<Room> getAllRooms() {
        return new ArrayList<Room>(rooms.values());
    }

    /**
     * Creates a room with a generated ID (r1, r2, ...). The caller must check admin privilege.
     *
     * @throws IllegalArgumentException if the type, capacity or features are invalid
     */
    public Room addRoom(String roomType, String capacity, String features) {
        return addRoom(roomType, Room.parseCapacity(capacity), features);
    }

    /**
     * Creates a room with a generated ID (r1, r2, ...). The caller must check admin privilege.
     *
     * @throws IllegalArgumentException if the type, capacity or features are invalid
     */
    public Room addRoom(String roomType, int capacity, String features) {
        String roomId = "r" + nextRoomNumber;
        Room room = new Room(roomId, roomType, capacity, features);
        nextRoomNumber++;
        rooms.put(roomId, room);
        return room;
    }

    /**
     * Modifies one field of a room. The caller must check admin privilege.
     *
     * @param field one of {@code roomType}, {@code capacity}, {@code features}
     * @throws IllegalArgumentException if the room, field or value is invalid
     */
    public Room modifyRoom(String roomId, String field, String newValue) {
        Room room = requireRoom(roomId);
        if (FIELD_ROOM_TYPE.equals(field)) {
            room.setRoomType(newValue);
        } else if (FIELD_CAPACITY.equals(field)) {
            room.setCapacity(Room.parseCapacity(newValue));
        } else if (FIELD_FEATURES.equals(field)) {
            room.setFeatures(newValue);
        } else {
            throw new IllegalArgumentException(ERR_INVALID_FIELD);
        }
        return room;
    }

    public Room deleteRoom(String roomId) {
        throw new UnsupportedOperationException("Not implemented: deleteRoom");
    }

    // Throws IllegalArgumentException if the room does not exist
    public Room requireRoom(String roomId) {
        Room room = roomId == null ? null : rooms.get(roomId);
        if (room == null) {
            throw new IllegalArgumentException(ERR_ROOM_NOT_FOUND);
        }
        return room;
    }
}
