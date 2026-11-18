package hk.edu.polyu.comp.comp2021.crobs.manager;

import hk.edu.polyu.comp.comp2021.crobs.model.Room;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores and manages rooms.
 */
public class RoomManager {

    /**
     * Error for an unknown room ID.
     */
    public static final String ERR_ROOM_NOT_FOUND = "Error: Room not found.";

    /**
     * Message when there are no rooms.
     */
    public static final String NO_ROOMS = "No rooms.";

    private static final String ERR_INVALID_FIELD = "Error: Invalid field.";
    private static final String FIELD_ROOM_TYPE = "roomType";
    private static final String FIELD_CAPACITY = "capacity";
    private static final String FIELD_FEATURES = "features";

    private final Map<String, Room> rooms;
    private int nextRoomNumber;

    /**
     * Creates an empty room manager.
     */
    public RoomManager() {
        this.rooms = new LinkedHashMap<String, Room>();
        this.nextRoomNumber = 1;
    }

    /**
     * @param roomId room ID
     * @return the room, or null if none
     */
    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    /**
     * @param roomId room ID
     * @return true if the room exists
     */
    public boolean hasRoom(String roomId) {
        return rooms.containsKey(roomId);
    }

    /**
     * @return all rooms sorted by room ID
     */
    public List<Room> getAllRooms() {
        return new ArrayList<Room>(rooms.values());
    }

    /**
     * Caller must check admin privilege.
     *
     * @param roomType room type
     * @param capacity capacity text
     * @param features comma-separated, no spaces
     * @return the new room
     */
    public Room addRoom(String roomType, String capacity, String features) {
        String roomId = "r" + nextRoomNumber;
        Room room = new Room(roomId, roomType, Room.parseCapacity(capacity), features);
        nextRoomNumber++;
        rooms.put(roomId, room);
        return room;
    }

    /**
     * Caller must check admin privilege.
     *
     * @param roomId   room ID
     * @param field    roomType, capacity or features
     * @param newValue new value
     * @return the modified room
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
     * Caller must check admin privilege and cancel the room's future bookings.
     *
     * @param roomId room ID
     * @return the deleted room
     */
    public Room deleteRoom(String roomId) {
        Room room = requireRoom(roomId);
        rooms.remove(roomId);
        return room;
    }

    /**
     * @param roomId room ID
     * @return the room on one line
     */
    public String listRoom(String roomId) {
        return requireRoom(roomId).toString();
    }

    /**
     * @return one line per room sorted by room ID, or NO_ROOMS
     */
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

    /**
     * @param roomId room ID
     * @return the room
     */
    public Room requireRoom(String roomId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new IllegalArgumentException(ERR_ROOM_NOT_FOUND);
        }
        return room;
    }
}
