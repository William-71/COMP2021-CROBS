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
 * Manages room bookings for REQ10–REQ12 and REQ17.
 * {@link #cancelBookingsByUser(String)} is provided for REQ3 integration.
 */
public class BookingManager {
    private final Map<String, Booking> bookings;
    private final RoomManager roomManager;
    private int nextBookingNumber;

    public BookingManager(RoomManager roomManager) {
        if (roomManager == null) {
            throw new IllegalArgumentException("RoomManager cannot be null.");
        }
        this.bookings = new HashMap<String, Booking>();
        this.roomManager = roomManager;
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
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null.");
        }
        if (booking.getBookingId() == null || booking.getOwnerUserId() == null || booking.getRoomId() == null
            || booking.getDate() == null || booking.getStartTime() == null || booking.getEndTime() == null
            || booking.getPurpose() == null) {
            throw new IllegalArgumentException("Booking fields cannot be null.");
        }
        roomManager.requireRoom(booking.getRoomId());
        validateDateAndTime(booking.getDate(), booking.getStartTime(), booking.getEndTime());
        bookings.put(booking.getBookingId(), booking);
    }

    public String nextBookingId() {
        String id = "b" + nextBookingNumber;
        nextBookingNumber++;
        return id;
    }

    public boolean isRoomAvailable(String roomId, String date, String startTime, String endTime) {
        if (roomId == null || date == null || startTime == null || endTime == null) {
            throw new IllegalArgumentException("Room and time information cannot be null.");
        }
        if (roomId.trim().isEmpty() || date.trim().isEmpty() || startTime.trim().isEmpty() || endTime.trim().isEmpty()) {
            throw new IllegalArgumentException("Room and time information cannot be blank.");
        }
        roomManager.requireRoom(roomId);
        validateDateAndTime(date, startTime, endTime);
        return !hasConflict(null, roomId, date, startTime, endTime);
    }

    /**
     * Cancels bookings dated today or later for the specified room. (used by {@code removeUser}).
     *
     * @param roomId room whose future bookings should be cancelled
     * @return number of cancelled bookings
     */
    public int cancelBookingsByUser(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or blank.");
        }
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
    //public int cancelBookingsByRoom(String roomId) {
    //    int removed = 0;
    //    Iterator<Map.Entry<String, Booking>> it = bookings.entrySet().iterator();
    //    while (it.hasNext()) {
    //        Map.Entry<String, Booking> entry = it.next();
    //        if (roomId.equals(entry.getValue().getRoomId())) {
    //            it.remove();
    //            removed++;
    //        }
    //    }
    //    return removed;
    //}
    public int cancelBookingsByRoom(String roomId) {// only remove future bookings for the room, preserve past bookings
        if (roomId == null || roomId.trim().isEmpty()) {
            throw new IllegalArgumentException("Room ID cannot be null or blank.");
        }
        int removed = 0;
        LocalDate today = LocalDate.now();
        Iterator<Map.Entry<String, Booking>> it = bookings.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Booking> entry = it.next();
            Booking booking = entry.getValue();
            LocalDate bookingDate = LocalDate.parse(booking.getDate());
            boolean sameRoom = roomId.equals(booking.getRoomId());
            boolean todayOrFuture = !bookingDate.isBefore(today);
            if (sameRoom && todayOrFuture) {
                it.remove();
                removed++;
            }
        }
        return removed;
    }

    // ---- Stubs for Member C ----

    public Booking book(String roomId, String date, String startTime, String endTime,
                        String purpose, String ownerUserId) {
        if (roomId == null || date == null || startTime == null || endTime == null 
            || purpose == null || ownerUserId == null) {
            throw new IllegalArgumentException("Booking information cannot be null.");
        }

        if (roomId.trim().isEmpty() || date.trim().isEmpty() || startTime.trim().isEmpty() 
            || endTime.trim().isEmpty() || purpose.trim().isEmpty() || ownerUserId.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking information cannot be blank.");
        }

        if (!roomManager.hasRoom(roomId)) {
            throw new IllegalArgumentException(RoomManager.ERR_ROOM_NOT_FOUND);
        }

        validateDateAndTime(date, startTime, endTime);

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
        if (bookingId == null || roomId == null || date == null || startTime == null || endTime == null
            || purpose == null || requesterUserId == null) {
            throw new IllegalArgumentException("Booking information cannot be null.");
        }

        if (bookingId.trim().isEmpty() || requesterUserId.trim().isEmpty() || roomId.trim().isEmpty()
            || date.trim().isEmpty() || startTime.trim().isEmpty() || endTime.trim().isEmpty()
            || purpose.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking information cannot be blank.");
        }

        Booking booking = bookings.get(bookingId);

        if (booking == null) {
            throw new IllegalArgumentException("Booking does not exist: " + bookingId);
        }

        if (!requesterUserId.equals(booking.getOwnerUserId())) {
            throw new IllegalArgumentException("You do not own this booking.");
        }

        if (!roomManager.hasRoom(roomId)) {
            throw new IllegalArgumentException(RoomManager.ERR_ROOM_NOT_FOUND);
        }

        validateDateAndTime(date, startTime, endTime);

        if (hasConflict(bookingId, roomId, date, startTime, endTime)) {
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
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking ID cannot be null or blank.");
        }

        if (requesterUserId == null || requesterUserId.trim().isEmpty()) {
            throw new IllegalArgumentException("Requester user ID cannot be null or blank.");
        }

        Booking booking = bookings.get(bookingId);

        if (booking == null) {
            throw new IllegalArgumentException("Booking does not exist: " + bookingId);
        }

        if (!requesterUserId.equals(booking.getOwnerUserId())) {
            throw new IllegalArgumentException("You do not own this booking.");
        }

        return bookings.remove(bookingId);
    }

    private boolean hasConflict(String excludedBookingId, String roomId, String date, String startTime, String endTime) {
        LocalTime requestedStart = LocalTime.parse(startTime);
        LocalTime requestedEnd = LocalTime.parse(endTime);

        for (Booking booking : bookings.values()) {
            if (excludedBookingId != null && excludedBookingId.equals(booking.getBookingId())) {
                continue;
            }

            boolean sameRoom = roomId.equals(booking.getRoomId());
            boolean sameDate = date.equals(booking.getDate());

            if (sameRoom && sameDate) {
                LocalTime existingStart = LocalTime.parse(booking.getStartTime());
                LocalTime existingEnd = LocalTime.parse(booking.getEndTime());

                boolean overlaps = requestedStart.isBefore(existingEnd) && requestedEnd.isAfter(existingStart);

                if (overlaps) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Booking> listBookings(String ownerUserId) {
        if (ownerUserId == null || ownerUserId.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner user ID cannot be null or blank.");
        }

        List<Booking> result = new ArrayList<Booking>();

        for (Booking booking : bookings.values()) {
            if (ownerUserId.equals(booking.getOwnerUserId())) {
                result.add(booking);
            }
        }

        result.sort((booking1, booking2) -> {
            int dateComparison = booking1.getDate().compareTo(booking2.getDate());
            if (dateComparison != 0) return dateComparison;
            return booking1.getStartTime().compareTo(booking2.getStartTime());
        });
        return result;
    }

    private void validateDateAndTime(String date, String startTime, String endTime) {
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
    }
}
