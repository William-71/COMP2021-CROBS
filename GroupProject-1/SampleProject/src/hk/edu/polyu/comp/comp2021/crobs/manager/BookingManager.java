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

    /**
     * @param roomManager room manager
     */
    public BookingManager(RoomManager roomManager) {
        if (roomManager == null) {
            throw new IllegalArgumentException("Error: Invalid arguments.");
        }
        this.bookings = new HashMap<String, Booking>();
        this.roomManager = roomManager;
        this.nextBookingNumber = 1;
    }

    /**
     * @param bookingId booking ID
     * @return the booking, or null if none
     */
    public Booking getBooking(String bookingId) {
        return bookings.get(bookingId);
    }

    /**
     * @return all bookings
     */
    public List<Booking> getAllBookings() {
        return new ArrayList<Booking>(bookings.values());
    }

    /**
     * Registers a booking instance.
     *
     * @param booking booking to add
     */
    public void putBooking(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Error: Invalid arguments.");
        }
        if (booking.getBookingId() == null || booking.getOwnerUserId() == null || booking.getRoomId() == null
            || booking.getDate() == null || booking.getStartTime() == null || booking.getEndTime() == null
            || booking.getPurpose() == null) {
            throw new IllegalArgumentException("Error: Invalid arguments.");
        }
        roomManager.requireRoom(booking.getRoomId());
        validateDateAndTime(booking.getDate(), booking.getStartTime(), booking.getEndTime());
        bookings.put(booking.getBookingId(), booking);
    }

    /**
     * @return the next booking ID
     */
    public String nextBookingId() {
        String id = "b" + nextBookingNumber;
        nextBookingNumber++;
        return id;
    }

    /**
     * @param roomId    room ID
     * @param date      yyyy-MM-dd
     * @param startTime HH:mm
     * @param endTime   HH:mm
     * @return true if the room is free
     */
    public boolean isRoomAvailable(String roomId, String date, String startTime, String endTime) {
        if (roomId == null || date == null || startTime == null || endTime == null) {
            throw new IllegalArgumentException("Error: Invalid arguments.");
        }
        if (roomId.trim().isEmpty() || date.trim().isEmpty() || startTime.trim().isEmpty() || endTime.trim().isEmpty()) {
            throw new IllegalArgumentException("Error: Invalid arguments.");
        }
        roomManager.requireRoom(roomId);
        validateDateAndTime(date, startTime, endTime);
        return !hasConflict(null, roomId, date, startTime, endTime);
    }

    /**
     * Cancels all bookings of the specified user (used by {@code removeUser}).
     *
     * @param userId user whose bookings should be cancelled
     * @return number of cancelled bookings
     */
    public int cancelBookingsByUser(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("Error: Invalid arguments.");
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
     *
     * @param roomId room ID
     * @return number of cancelled bookings
     */
    public int cancelBookingsByRoom(String roomId) {
        List<Booking> future = getFutureBookingsByRoom(roomId);
        for (Booking booking : future) {
            bookings.remove(booking.getBookingId());
        }
        return future.size();
    }

    /**
     * @param roomId room ID
     * @return the room's bookings dated today or later
     */
    public List<Booking> getFutureBookingsByRoom(String roomId) {
        if (roomId == null || roomId.trim().isEmpty()) {
            throw new IllegalArgumentException("Error: Invalid arguments.");
        }
        LocalDate today = LocalDate.now();
        List<Booking> result = new ArrayList<Booking>();
        for (Booking booking : bookings.values()) {
            boolean sameRoom = roomId.equals(booking.getRoomId());
            boolean todayOrFuture = !LocalDate.parse(booking.getDate()).isBefore(today);
            if (sameRoom && todayOrFuture) {
                result.add(booking);
            }
        }
        return result;
    }

    /**
     * @param bookingId booking ID
     * @return the removed booking, or null if none
     */
    public Booking removeBooking(String bookingId) {
        return bookings.remove(bookingId);
    }

    /**
     * @param roomId      room ID
     * @param date        yyyy-MM-dd
     * @param startTime   HH:mm
     * @param endTime     HH:mm
     * @param purpose     purpose
     * @param ownerUserId owner's user ID
     * @return the new booking
     */
    public Booking book(String roomId, String date, String startTime, String endTime,
                        String purpose, String ownerUserId) {
        if (roomId == null || date == null || startTime == null || endTime == null 
            || purpose == null || ownerUserId == null) {
            throw new IllegalArgumentException("Error: Invalid arguments.");
        }

        if (roomId.trim().isEmpty() || date.trim().isEmpty() || startTime.trim().isEmpty() 
            || endTime.trim().isEmpty() || purpose.trim().isEmpty() || ownerUserId.trim().isEmpty()) {
            throw new IllegalArgumentException("Error: Invalid arguments.");
        }

        if (!roomManager.hasRoom(roomId)) {
            throw new IllegalArgumentException(RoomManager.ERR_ROOM_NOT_FOUND);
        }

        validateDateAndTime(date, startTime, endTime);

        if (hasConflict(null, roomId, date, startTime, endTime)) {
            throw new IllegalArgumentException("Error: Room already booked.");
        }

        String bookingId = nextBookingId();

        Booking booking = new Booking(bookingId, ownerUserId, roomId, date, startTime, endTime, purpose);

        putBooking(booking);
        return booking;
    }

    /**
     * @param bookingId       booking ID
     * @param roomId          room ID
     * @param date            yyyy-MM-dd
     * @param startTime       HH:mm
     * @param endTime         HH:mm
     * @param purpose         purpose
     * @param requesterUserId requester's user ID
     * @return the modified booking
     */
    public Booking modifyBooking(String bookingId, String roomId, String date,
                                 String startTime, String endTime, String purpose,
                                 String requesterUserId) {
        if (bookingId == null || roomId == null || date == null || startTime == null || endTime == null
            || purpose == null || requesterUserId == null) {
            throw new IllegalArgumentException("Error: Invalid arguments.");
        }

        if (bookingId.trim().isEmpty() || requesterUserId.trim().isEmpty() || roomId.trim().isEmpty()
            || date.trim().isEmpty() || startTime.trim().isEmpty() || endTime.trim().isEmpty()
            || purpose.trim().isEmpty()) {
            throw new IllegalArgumentException("Error: Invalid arguments.");
        }

        Booking booking = bookings.get(bookingId);

        if (booking == null) {
            throw new IllegalArgumentException("Error: Booking not found.");
        }

        if (!requesterUserId.equals(booking.getOwnerUserId())) {
            throw new IllegalArgumentException("Error: Access denied.");
        }

        if (!roomManager.hasRoom(roomId)) {
            throw new IllegalArgumentException(RoomManager.ERR_ROOM_NOT_FOUND);
        }

        validateDateAndTime(date, startTime, endTime);

        if (hasConflict(bookingId, roomId, date, startTime, endTime)) {
            throw new IllegalArgumentException("Error: Room already booked.");
        }

        booking.setRoomId(roomId);
        booking.setDate(date);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setPurpose(purpose);

        return booking;
    }

    /**
     * @param bookingId       booking ID
     * @param requesterUserId requester's user ID
     * @return the deleted booking
     */
    public Booking deleteBooking(String bookingId, String requesterUserId) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new IllegalArgumentException("Error: Invalid arguments.");
        }

        if (requesterUserId == null || requesterUserId.trim().isEmpty()) {
            throw new IllegalArgumentException("Error: Invalid arguments.");
        }

        Booking booking = bookings.get(bookingId);

        if (booking == null) {
            throw new IllegalArgumentException("Error: Booking not found.");
        }

        if (!requesterUserId.equals(booking.getOwnerUserId())) {
            throw new IllegalArgumentException("Error: Access denied.");
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

    /**
     * @param ownerUserId owner's user ID
     * @return the user's bookings
     */
    public List<Booking> listBookings(String ownerUserId) {
        if (ownerUserId == null || ownerUserId.trim().isEmpty()) {
            throw new IllegalArgumentException("Error: Invalid arguments.");
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
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Error: Invalid date.");
        }
        try {
            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);
            if (!start.isBefore(end)) {
                throw new IllegalArgumentException("Error: Invalid time range.");
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Error: Invalid time.");
        }
    }
}
