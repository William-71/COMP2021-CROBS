package hk.edu.polyu.comp.comp2021.crobs.manager;

import hk.edu.polyu.comp.comp2021.crobs.model.Booking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

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
        if (hasConflict(null, roomId, date, startTime, endTime)) {
            throw new IllegalArgumentException("The room is already booked during this time.");
        }

        if (roomId == null || date == null
                || startTime == null || endTime == null
                || purpose == null || ownerUserId == null) {
            throw new IllegalArgumentException("Booking information cannot be null.");
        }

        if (roomId.trim().isEmpty()
                || date.trim().isEmpty()
                || startTime.trim().isEmpty()
                || endTime.trim().isEmpty()
                || purpose.trim().isEmpty()
                || ownerUserId.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking information cannot be blank.");
        }

        try {
            LocalDate.parse(date);
            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);

            if (!start.isBefore(end)) {
                throw new IllegalArgumentException("Start time must be before end time.");
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date must use yyyy-MM-dd and time must use HH:mm.");
        }

        if (hasConflict(null, roomId, date, startTime, endTime)) {
            throw new IllegalArgumentException("The room is already booked during this time.");
        }

        String bookingId = nextBookingId();

        Booking booking = new Booking(bookingId, ownerUserId, roomId, date, startTime, endTime, purpose);

        putBooking(booking);

        return booking;
    }

    public Booking modifyBooking(String bookingId, String roomId, String date,
                                 String startTime, String endTime, String purpose,
                                 String requesterUserId) {
        if (bookingId == null || roomId == null || date == null
            || startTime == null || endTime == null
            || purpose == null || requesterUserId == null) {
        throw new IllegalArgumentException("Booking information cannot be null.");
        }

        Booking booking = bookings.get(bookingId);

        if (booking == null) {
            throw new IllegalArgumentException("Booking does not exist: " + bookingId);
        }

        if (!requesterUserId.equals(booking.getOwnerUserId())) {
            throw new IllegalArgumentException("You do not own this booking.");
        }

        if (roomId.trim().isEmpty()
                || date.trim().isEmpty()
                || startTime.trim().isEmpty()
                || endTime.trim().isEmpty()
                || purpose.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking information cannot be blank.");
        }

        try {
            LocalDate.parse(date);

            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);

            if (!start.isBefore(end)) {
                throw new IllegalArgumentException("Start time must be before end time.");
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date must use yyyy-MM-dd and time must use HH:mm.");
        }

        // Pass bookingId so hasConflict ignores the booking currently being modified.
        if (hasConflict(
                bookingId,
                roomId,
                date,
                startTime,
                endTime)) {
            throw new IllegalArgumentException("The room is already booked during this time.");
        }

        booking.setRoomId(roomId);
        booking.setDate(date);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setPurpose(purpose);

        return booking;
    }

    public Booking deleteBooking(String bookingId, String requesterUserId) {
        throw new UnsupportedOperationException("Not implemented: deleteBooking");
    }

    private boolean hasConflict(String excludedBookingId, String roomId, String date, String startTime, String endTime) {
        for (Booking booking : bookings.values()) {
            if (excludedBookingId != null
                && excludedBookingId.equals(booking.getBookingId())) {
                continue;
            }

            boolean sameRoom = roomId.equals(booking.getRoomId());
            boolean sameDate = date.equals(booking.getDate());

            if (sameRoom && sameDate) {
                String existingStart = booking.getStartTime();
                String existingEnd = booking.getEndTime();

                boolean overlaps =
                        startTime.compareTo(existingEnd) < 0
                        && endTime.compareTo(existingStart) > 0;

                if (overlaps) {
                    return true;
                }
            }
        }
        return false;
    }
}
