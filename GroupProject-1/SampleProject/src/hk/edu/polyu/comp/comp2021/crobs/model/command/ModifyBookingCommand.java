package hk.edu.polyu.comp.comp2021.crobs.model.command;

import hk.edu.polyu.comp.comp2021.crobs.manager.BookingManager;
import hk.edu.polyu.comp.comp2021.crobs.model.Booking;

/**
 * modifyBooking; undo restores the booking's previous values.
 */
public class ModifyBookingCommand implements UndoableCommand {

    private final BookingManager bookingManager;
    private final String bookingId;
    private final String roomId;
    private final String date;
    private final String startTime;
    private final String endTime;
    private final String purpose;
    private final String requesterUserId;
    private Booking booking;
    private String[] oldValues;
    private String[] newValues;

    /**
     * @param bookingManager  booking manager
     * @param bookingId       booking ID
     * @param roomId          room ID
     * @param date            yyyy-MM-dd
     * @param startTime       HH:mm
     * @param endTime         HH:mm
     * @param purpose         purpose
     * @param requesterUserId requester's user ID
     */
    public ModifyBookingCommand(BookingManager bookingManager, String bookingId, String roomId, String date,
                                String startTime, String endTime, String purpose, String requesterUserId) {
        this.bookingManager = bookingManager;
        this.bookingId = bookingId;
        this.roomId = roomId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
        this.requesterUserId = requesterUserId;
    }

    @Override
    public String execute() {
        Booking target = bookingManager.getBooking(bookingId);
        if (target != null) {
            oldValues = valuesOf(target);
        }
        booking = bookingManager.modifyBooking(bookingId, roomId, date, startTime, endTime, purpose,
                requesterUserId);
        newValues = valuesOf(booking);
        return "Booking " + booking.getBookingId() + " modified.";
    }

    @Override
    public void undo() {
        apply(oldValues);
    }

    @Override
    public void redo() {
        apply(newValues);
    }

    private static String[] valuesOf(Booking b) {
        return new String[]{b.getRoomId(), b.getDate(), b.getStartTime(), b.getEndTime(), b.getPurpose()};
    }

    private void apply(String[] values) {
        int i = 0;
        booking.setRoomId(values[i++]);
        booking.setDate(values[i++]);
        booking.setStartTime(values[i++]);
        booking.setEndTime(values[i++]);
        booking.setPurpose(values[i]);
    }
}
