package hk.edu.polyu.comp.comp2021.crobs.model.command;

import hk.edu.polyu.comp.comp2021.crobs.manager.BookingManager;
import hk.edu.polyu.comp.comp2021.crobs.model.Booking;

/**
 * book; undo removes the booking.
 */
public class BookCommand implements UndoableCommand {

    private final BookingManager bookingManager;
    private final String roomId;
    private final String date;
    private final String startTime;
    private final String endTime;
    private final String purpose;
    private final String ownerUserId;
    private Booking booking;

    /**
     * @param bookingManager booking manager
     * @param roomId         room ID
     * @param date           yyyy-MM-dd
     * @param startTime      HH:mm
     * @param endTime        HH:mm
     * @param purpose        purpose
     * @param ownerUserId    owner's user ID
     */
    public BookCommand(BookingManager bookingManager, String roomId, String date, String startTime,
                       String endTime, String purpose, String ownerUserId) {
        this.bookingManager = bookingManager;
        this.roomId = roomId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
        this.ownerUserId = ownerUserId;
    }

    @Override
    public String execute() {
        booking = bookingManager.book(roomId, date, startTime, endTime, purpose, ownerUserId);
        return "Booking " + booking.getBookingId() + " created.";
    }

    @Override
    public void undo() {
        bookingManager.removeBooking(booking.getBookingId());
    }

    @Override
    public void redo() {
        bookingManager.putBooking(booking);
    }
}
