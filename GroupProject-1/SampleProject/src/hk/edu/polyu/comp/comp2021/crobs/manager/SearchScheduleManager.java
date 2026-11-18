package hk.edu.polyu.comp.comp2021.crobs.manager;

import hk.edu.polyu.comp.comp2021.crobs.model.Booking;
import hk.edu.polyu.comp.comp2021.crobs.model.Room;
import hk.edu.polyu.comp.comp2021.crobs.model.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Implements room searching and weekly room schedules for REQ13 and REQ14.
 */
public class SearchScheduleManager {
    private static final int DAYS_IN_WEEK = 7;
    private static final String NO_BOOKINGS = "No bookings.";

    private final RoomManager roomManager;
    private final BookingManager bookingManager;

    /**
     * Creates a search and schedule manager using the shared room and booking data.
     *
     * @param roomManager shared room manager
     * @param bookingManager shared booking manager
     */
    public SearchScheduleManager(RoomManager roomManager, BookingManager bookingManager) {
        if (roomManager == null || bookingManager == null) {
            throw new IllegalArgumentException("Managers cannot be null.");
        }
        this.roomManager = roomManager;
        this.bookingManager = bookingManager;
    }

    /**
     * Finds rooms with sufficient capacity and features that are free during a time slot.
     *
     * @param date booking date in yyyy-MM-dd format
     * @param startTime start time in HH:mm format
     * @param endTime end time in HH:mm format
     * @param minCapacity minimum required room capacity
     * @param features comma-separated required features
     * @return matching rooms sorted by room ID
     */
    public List<Room> search(String date, String startTime, String endTime,
                             String minCapacity, String features) {
        validateDateAndTime(date, startTime, endTime);
        int requiredCapacity = Room.parseCapacity(minCapacity);
        List<String> requiredFeatures = Room.parseFeatures(features);
        List<Room> result = new ArrayList<Room>();

        for (Room room : roomManager.getAllRooms()) {
            if (room.getCapacity() >= requiredCapacity
                    && room.hasAllFeatures(requiredFeatures)
                    && bookingManager.isRoomAvailable(
                    room.getRoomId(), date, startTime, endTime)) {
                result.add(room);
            }
        }
        return result;
    }

    /**
     * Returns bookings in the seven-day period beginning on the supplied date.
     *
     * @param roomId room to inspect
     * @param weekStartDate first date of the seven-day period
     * @return bookings sorted by date and start time
     */
    public List<Booking> schedule(String roomId, String weekStartDate) {
        roomManager.requireRoom(roomId);
        LocalDate firstDate = parseDate(weekStartDate);
        LocalDate dateAfterWeek = firstDate.plusDays(DAYS_IN_WEEK);
        List<Booking> result = new ArrayList<Booking>();

        for (Booking booking : bookingManager.getAllBookings()) {
            LocalDate bookingDate = LocalDate.parse(booking.getDate());
            if (roomId.equals(booking.getRoomId())
                    && !bookingDate.isBefore(firstDate)
                    && bookingDate.isBefore(dateAfterWeek)) {
                result.add(booking);
            }
        }

        Collections.sort(result, new Comparator<Booking>() {
            @Override
            public int compare(Booking first, Booking second) {
                int dateComparison = first.getDate().compareTo(second.getDate());
                if (dateComparison != 0) {
                    return dateComparison;
                }
                return first.getStartTime().compareTo(second.getStartTime());
            }
        });
        return result;
    }

    /**
     * Formats room search results for the command-line interface.
     *
     * @param rooms rooms returned by {@link #search(String, String, String, String, String)}
     * @return one room per line, or a message when no room matches
     */
    public String formatSearchResults(List<Room> rooms) {
        if (rooms == null) {
            throw new IllegalArgumentException("Rooms cannot be null.");
        }
        if (rooms.isEmpty()) {
            return RoomManager.NO_ROOMS;
        }
        StringBuilder result = new StringBuilder();
        for (Room room : rooms) {
            appendLineSeparator(result);
            result.append(room.toString());
        }
        return result.toString();
    }

    /**
     * Formats a schedule while applying the REQ14 user-ID visibility rule.
     *
     * @param bookings bookings returned by {@link #schedule(String, String)}
     * @param viewer user viewing the schedule
     * @return formatted schedule
     */
    public String formatSchedule(List<Booking> bookings, User viewer) {
        if (bookings == null || viewer == null) {
            throw new IllegalArgumentException("Bookings and viewer cannot be null.");
        }
        if (bookings.isEmpty()) {
            return NO_BOOKINGS;
        }

        StringBuilder result = new StringBuilder();
        for (Booking booking : bookings) {
            appendLineSeparator(result);
            result.append(booking.getBookingId())
                    .append(" | date: ").append(booking.getDate())
                    .append(" | time: ").append(booking.getStartTime())
                    .append('-').append(booking.getEndTime())
                    .append(" | purpose: ").append(booking.getPurpose());
            if (viewer.isAdmin() || viewer.getUserId().equals(booking.getOwnerUserId())) {
                result.append(" | user: ").append(booking.getOwnerUserId());
            }
        }
        return result.toString();
    }

    private void validateDateAndTime(String date, String startTime, String endTime) {
        parseDate(date);
        try {
            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);
            if (!start.isBefore(end)) {
                throw new IllegalArgumentException("Start time must be before end time.");
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Time must use HH:mm.");
        }
    }

    private LocalDate parseDate(String date) {
        if (date == null) {
            throw new IllegalArgumentException("Date must use yyyy-MM-dd.");
        }
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date must use yyyy-MM-dd.");
        }
    }

    private void appendLineSeparator(StringBuilder result) {
        if (result.length() > 0) {
            result.append(System.lineSeparator());
        }
    }
}
