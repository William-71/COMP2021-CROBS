package hk.edu.polyu.comp.comp2021.crobs.manager;

import hk.edu.polyu.comp.comp2021.crobs.model.Booking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Placeholder for Member C — booking management (REQ10–REQ14, REQ17).
 * {@link #cancelBookingsByUser(String)} is provided for REQ3 integration.
 */
public class BookingManager {
    private final Map<String, Booking> bookings;
    private int nextBookingNumber;

    public BookingManager() {
        this.bookings = new HashMap<String, Booking>();
        this.nextBookingNumber = 1;
    }

    public Booking getBooking(String bookingId) {
        return bookings.get(bookingId);
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<Booking>(bookings.values());
    }

    /**
     * Registers a booking instance (used by Member C implementation and integration tests).
     */
    public void putBooking(Booking booking) {
        bookings.put(booking.getBookingId(), booking);
    }

    public String nextBookingId() {
        String id = "b" + nextBookingNumber;
        nextBookingNumber++;
        return id;
    }

    /**
     * Cancels all bookings owned by the given user (used by {@code removeUser}).
     *
     * @param userId owner user ID
     * @return number of cancelled bookings
     */
    public int cancelBookingsByUser(String userId) {
        int removed = 0;
        Iterator<Map.Entry<String, Booking>> it = bookings.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Booking> entry = it.next();
            if (userId.equals(entry.getValue().getOwnerUserId())) {
                it.remove();
                removed++;
            }
        }
        return removed;
    }

    /**
     * Cancels all future bookings for a room (used by {@code deleteRoom}).
     * Placeholder: currently removes all bookings for the room.
     */
    public int cancelBookingsByRoom(String roomId) {
        int removed = 0;
        Iterator<Map.Entry<String, Booking>> it = bookings.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Booking> entry = it.next();
            if (roomId.equals(entry.getValue().getRoomId())) {
                it.remove();
                removed++;
            }
        }
        return removed;
    }

    // ---- Stubs for Member C ----

    public Booking book(String roomId, String date, String startTime, String endTime,
                        String purpose, String ownerUserId) {
        throw new UnsupportedOperationException("Not implemented: book");
    }

    public Booking modifyBooking(String bookingId, String roomId, String date,
                                 String startTime, String endTime, String purpose,
                                 String requesterUserId) {
        throw new UnsupportedOperationException("Not implemented: modifyBooking");
    }

    public Booking deleteBooking(String bookingId, String requesterUserId) {
        throw new UnsupportedOperationException("Not implemented: deleteBooking");
    }
}
