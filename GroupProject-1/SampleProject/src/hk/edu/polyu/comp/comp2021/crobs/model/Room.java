package hk.edu.polyu.comp.comp2021.crobs.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A bookable room.
 */
public class Room {

    private static final List<String> ROOM_TYPES = Collections.unmodifiableList(Arrays.asList(
            "lectureTheatre", "computerLab", "seminarRoom", "meetingRoom"));

    private static final String ERR_INVALID_TYPE = "Error: Invalid room type.";
    private static final String ERR_INVALID_CAPACITY = "Error: Invalid capacity.";
    private static final String ERR_INVALID_FEATURES = "Error: Invalid features.";

    private final String roomId;
    private String roomType;
    private int capacity;
    private List<String> features;

    /**
     * @param roomId   room ID
     * @param roomType lectureTheatre, computerLab, seminarRoom or meetingRoom
     * @param capacity positive capacity
     * @param features comma-separated, no spaces
     */
    public Room(String roomId, String roomType, int capacity, String features) {
        this.roomId = roomId;
        setRoomType(roomType);
        setCapacity(capacity);
        setFeatures(features);
    }

    /**
     * @param capacity capacity text
     * @return the capacity
     */
    public static int parseCapacity(String capacity) {
        int value;
        try {
            value = Integer.parseInt(capacity);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(ERR_INVALID_CAPACITY);
        }
        if (value <= 0) {
            throw new IllegalArgumentException(ERR_INVALID_CAPACITY);
        }
        return value;
    }

    /**
     * @param features comma-separated, no spaces
     * @return the features without duplicates
     */
    public static List<String> parseFeatures(String features) {
        if (features == null || !features.matches("[^\\s,]+(,[^\\s,]+)*")) {
            throw new IllegalArgumentException(ERR_INVALID_FEATURES);
        }
        List<String> result = new ArrayList<String>();
        for (String feature : features.split(",")) {
            if (!result.contains(feature)) {
                result.add(feature);
            }
        }
        return result;
    }

    /**
     * @return the room ID
     */
    public String getRoomId() {
        return roomId;
    }

    /**
     * @return the room type
     */
    public String getRoomType() {
        return roomType;
    }

    /**
     * @param roomType lectureTheatre, computerLab, seminarRoom or meetingRoom
     */
    public void setRoomType(String roomType) {
        if (!ROOM_TYPES.contains(roomType)) {
            throw new IllegalArgumentException(ERR_INVALID_TYPE);
        }
        this.roomType = roomType;
    }

    /**
     * @return the capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * @param capacity positive capacity
     */
    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException(ERR_INVALID_CAPACITY);
        }
        this.capacity = capacity;
    }

    /**
     * @return the features, comma-separated
     */
    public String getFeatures() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < features.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(features.get(i));
        }
        return sb.toString();
    }

    /**
     * @param features comma-separated, no spaces
     */
    public void setFeatures(String features) {
        this.features = parseFeatures(features);
    }

    /**
     * @param required required features
     * @return true if the room has all of them
     */
    public boolean hasAllFeatures(List<String> required) {
        return features.containsAll(required);
    }

    /**
     * @return the room on one line
     */
    @Override
    public String toString() {
        return roomId + " | type: " + roomType + " | capacity: " + capacity + " | features: " + getFeatures();
    }
}
