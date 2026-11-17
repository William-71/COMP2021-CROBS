package hk.edu.polyu.comp.comp2021.crobs.manager;

import hk.edu.polyu.comp.comp2021.crobs.model.Room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomManager {

    public static final String ERR_ROOM_NOT_FOUND = "Error: Room not found.";
    public static final String ERR_INVALID_FIELD = "Error: Invalid field.";
    public static final String NO_ROOMS = "No rooms.";

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

    // Returns all rooms sorted in ascending order of room ID
    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<Room>(rooms.values());
        Collections.sort(list, new Comparator<Room>() {
            @Override
            public int compare(Room a, Room b) {
                return Integer.compare(a.getIdNumber(), b.getIdNumber());
            }
        });
        return list;
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

    /**
     * Deletes a room. The caller must check admin privilege and cancel the room's future bookings.
     *
     * @throws IllegalArgumentException if the room does not exist
     */
    public Room deleteRoom(String roomId) {
        Room room = requireRoom(roomId);
        rooms.remove(roomId);
        return room;
    }

    // Throws IllegalArgumentException if the room does not exist
    public String listRoom(String roomId) {
        return requireRoom(roomId).toString();
    }

    // Returns one line per room sorted by room ID, or NO_ROOMS if there are none
    public String listAllRooms() {
        List<Room> list = getAllRooms();
        if (list.isEmpty()) {
            return NO_ROOMS;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(System.lineSeparator());
            }
            sb.append(list.get(i).toString());
        }
        return sb.toString();
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
