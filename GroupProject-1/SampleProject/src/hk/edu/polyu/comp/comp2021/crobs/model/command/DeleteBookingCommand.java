package hk.edu.polyu.comp.comp2021.crobs.model.command;

import hk.edu.polyu.comp.comp2021.crobs.manager.BookingManager;
import hk.edu.polyu.comp.comp2021.crobs.model.Booking;

/**
 * deleteBooking; undo restores the booking.
 */
public class DeleteBookingCommand implements UndoableCommand {

    private final BookingManager bookingManager;
    private final String bookingId;
    private final String requesterUserId;
    private Booking booking;

    /**
     * @param bookingManager  booking manager
     * @param bookingId       booking ID
     * @param requesterUserId requester's user ID
     */
    public DeleteBookingCommand(BookingManager bookingManager, String bookingId, String requesterUserId) {
        this.bookingManager = bookingManager;
        this.bookingId = bookingId;
        this.requesterUserId = requesterUserId;
    }

    @Override
    public String execute() {
        booking = bookingManager.deleteBooking(bookingId, requesterUserId);
        return "Booking " + booking.getBookingId() + " deleted.";
    }

    @Override
    public void undo() {
        bookingManager.putBooking(booking);
    }

    @Override
    public void redo() {
        bookingManager.removeBooking(bookingId);
    }
}
