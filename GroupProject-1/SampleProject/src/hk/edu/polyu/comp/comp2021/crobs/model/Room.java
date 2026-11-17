package hk.edu.polyu.comp.comp2021.crobs.model;

/**
 * Placeholder for Member B — room entity.
 * Fields follow REQ7–REQ9 / REQ15–REQ16.
 */
public class Room {
    private String roomId;
    private String roomType;
    private int capacity;
    private String features;

    public Room(String roomId, String roomType, int capacity, String features) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.capacity = capacity;
        this.features = features;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }
}
