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

    /**
     * @param bookingId   booking ID
     * @param ownerUserId owner's user ID
     * @param roomId      room ID
     * @param date        yyyy-MM-dd
     * @param startTime   HH:mm
     * @param endTime     HH:mm
     * @param purpose     purpose
     */
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

    /**
     * @return the booking ID
     */
    public String getBookingId() {
        return bookingId;
    }

    /**
     * @return the owner's user ID
     */
    public String getOwnerUserId() {
        return ownerUserId;
    }

    /**
     * @return the room ID
     */
    public String getRoomId() {
        return roomId;
    }

    /**
     * @param roomId room ID
     */
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date yyyy-MM-dd
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the start time
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * @param startTime HH:mm
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the end time
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * @param endTime HH:mm
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the purpose
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * @param purpose purpose
     */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}
