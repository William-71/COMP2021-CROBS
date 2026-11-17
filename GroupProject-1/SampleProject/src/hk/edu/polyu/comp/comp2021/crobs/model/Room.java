package hk.edu.polyu.comp.comp2021.crobs.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Room {

    public static final List<String> ROOM_TYPES = Collections.unmodifiableList(Arrays.asList(
            "lectureTheatre", "computerLab", "seminarRoom", "meetingRoom"));

    public static final String ERR_INVALID_TYPE = "Error: Invalid room type.";
    public static final String ERR_INVALID_CAPACITY = "Error: Invalid capacity.";
    public static final String ERR_INVALID_FEATURES = "Error: Invalid features.";

    private final String roomId;
    private String roomType;
    private int capacity;
    private List<String> features;

    /**
     * @param features comma-separated list without spaces, e.g. {@code projector,whiteboard}
     * @throws IllegalArgumentException if any field is invalid
     */
    public Room(String roomId, String roomType, int capacity, String features) {
        this.roomId = roomId;
        setRoomType(roomType);
        setCapacity(capacity);
        setFeatures(features);
    }

    public static boolean isValidRoomType(String roomType) {
        return roomType != null && ROOM_TYPES.contains(roomType);
    }

    // Throws IllegalArgumentException if capacity is not a positive integer
    public static int parseCapacity(String capacity) {
        if (capacity == null || !capacity.matches("\\d+")) {
            throw new IllegalArgumentException(ERR_INVALID_CAPACITY);
        }
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

    // Parses a comma-separated feature list without spaces, dropping duplicates.
    // Throws IllegalArgumentException if the list is empty or has whitespace or empty items
    public static List<String> parseFeatures(String features) {
        if (features == null || features.isEmpty() || !features.matches("[^\\s,]+(,[^\\s,]+)*")) {
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

    public String getRoomId() {
        return roomId;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        if (!isValidRoomType(roomType)) {
            throw new IllegalArgumentException(ERR_INVALID_TYPE);
        }
        this.roomType = roomType;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException(ERR_INVALID_CAPACITY);
        }
        this.capacity = capacity;
    }

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

    public List<String> getFeatureList() {
        return Collections.unmodifiableList(features);
    }

    public void setFeatures(String features) {
        this.features = parseFeatures(features);
    }

    public boolean hasFeature(String feature) {
        return features.contains(feature);
    }

    public boolean hasAllFeatures(List<String> required) {
        return features.containsAll(required);
    }
}
