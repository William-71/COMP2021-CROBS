package hk.edu.polyu.comp.comp2021.crobs.model;

/**
 * Placeholder for Member C — booking entity.
 * Fields follow REQ10–REQ12 / REQ14 / REQ17.
 */
public class Booking {
    private final String bookingId;
    private final String ownerUserId;
    private String roomId;
    private String date;
    private String startTime;
    private String endTime;
    private String purpose;

    public Booking(String bookingId, String ownerUserId, String roomId,
                   String date, String startTime, String endTime, String purpose) {
        this.bookingId = bookingId;
        this.ownerUserId = ownerUserId;
        this.roomId = roomId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}
