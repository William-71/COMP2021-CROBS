package hk.edu.polyu.comp.comp2021.crobs.model.command;

import hk.edu.polyu.comp.comp2021.crobs.manager.BookingManager;
import hk.edu.polyu.comp.comp2021.crobs.manager.RoomManager;
import hk.edu.polyu.comp.comp2021.crobs.model.Booking;
import hk.edu.polyu.comp.comp2021.crobs.model.Room;

import java.util.List;

/**
 * deleteRoom; undo restores the room and its removed future bookings.
 */
public class DeleteRoomCommand implements UndoableCommand {

    private final RoomManager roomManager;
    private final BookingManager bookingManager;
    private final String roomId;
    private Room room;
    private List<Booking> removedBookings;

    /**
     * @param roomManager    room manager
     * @param bookingManager booking manager
     * @param roomId         room ID
     */
    public DeleteRoomCommand(RoomManager roomManager, BookingManager bookingManager, String roomId) {
        this.roomManager = roomManager;
        this.bookingManager = bookingManager;
        this.roomId = roomId;
    }

    @Override
    public String execute() {
        room = roomManager.deleteRoom(roomId);
        removedBookings = bookingManager.getFutureBookingsByRoom(roomId);
        removeCancelled();
        return "Room " + room.getRoomId() + " deleted.";
    }

    @Override
    public void undo() {
        roomManager.restoreRoom(room);
        for (Booking booking : removedBookings) {
            bookingManager.putBooking(booking);
        }
    }

    @Override
    public void redo() {
        roomManager.deleteRoom(roomId);
        removeCancelled();
    }

    private void removeCancelled() {
        for (Booking booking : removedBookings) {
            bookingManager.removeBooking(booking.getBookingId());
        }
    }
}
