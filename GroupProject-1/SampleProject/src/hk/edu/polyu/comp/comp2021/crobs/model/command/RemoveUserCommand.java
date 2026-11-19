package hk.edu.polyu.comp.comp2021.crobs.model.command;

import hk.edu.polyu.comp.comp2021.crobs.manager.BookingManager;
import hk.edu.polyu.comp.comp2021.crobs.manager.UserManager;
import hk.edu.polyu.comp.comp2021.crobs.model.Booking;
import hk.edu.polyu.comp.comp2021.crobs.model.User;

import java.util.List;

/**
 * removeUser; undo restores the user and their cancelled bookings.
 */
public class RemoveUserCommand implements UndoableCommand {

    private final UserManager userManager;
    private final BookingManager bookingManager;
    private final String targetUserId;
    private User user;
    private List<Booking> cancelled;

    /**
     * @param userManager    user manager
     * @param bookingManager booking manager
     * @param targetUserId   user to remove
     */
    public RemoveUserCommand(UserManager userManager, BookingManager bookingManager, String targetUserId) {
        this.userManager = userManager;
        this.bookingManager = bookingManager;
        this.targetUserId = targetUserId;
    }

    @Override
    public String execute() {
        user = userManager.removeUser(targetUserId);
        cancelled = bookingManager.listBookings(user.getUserId());
        bookingManager.cancelBookingsByUser(user.getUserId());
        return "User " + user.getUserId() + " removed.";
    }

    @Override
    public void undo() {
        userManager.restoreUser(user);
        for (Booking booking : cancelled) {
            bookingManager.putBooking(booking);
        }
    }

    @Override
    public void redo() {
        userManager.discardUser(user.getUserId());
        bookingManager.cancelBookingsByUser(user.getUserId());
    }
}
