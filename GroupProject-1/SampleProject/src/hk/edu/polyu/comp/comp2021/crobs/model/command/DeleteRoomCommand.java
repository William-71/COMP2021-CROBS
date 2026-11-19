package hk.edu.polyu.comp.comp2021.crobs.model.command;

import hk.edu.polyu.comp.comp2021.crobs.manager.BookingManager;
import hk.edu.polyu.comp.comp2021.crobs.manager.RoomManager;
import hk.edu.polyu.comp.comp2021.crobs.model.Booking;
import hk.edu.polyu.comp.comp2021.crobs.model.Room;

import java.util.List;

/**
 * deleteRoom; undo restores the room and its cancelled future bookings.
 */
public class DeleteRoomCommand implements UndoableCommand {

    private final RoomManager roomManager;
    private final BookingManager bookingManager;
    private final String roomId;
    private Room room;
    private List<Booking> cancelled;

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
        cancelled = bookingManager.getFutureBookingsByRoom(roomId);
        removeCancelled();
        return "Room " + room.getRoomId() + " deleted.";
    }

    @Override
    public void undo() {
        roomManager.restoreRoom(room);
        for (Booking booking : cancelled) {
            bookingManager.putBooking(booking);
        }
    }

    @Override
    public void redo() {
        roomManager.deleteRoom(roomId);
        removeCancelled();
    }

    private void removeCancelled() {
        for (Booking booking : cancelled) {
            bookingManager.removeBooking(booking.getBookingId());
        }
    }
}
