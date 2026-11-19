package hk.edu.polyu.comp.comp2021.crobs.manager;

import hk.edu.polyu.comp.comp2021.crobs.model.Booking;
import hk.edu.polyu.comp.comp2021.crobs.model.Role;
import hk.edu.polyu.comp.comp2021.crobs.model.Room;
import hk.edu.polyu.comp.comp2021.crobs.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchScheduleManagerTest {
    private RoomManager roomManager;
    private BookingManager bookingManager;
    private SearchScheduleManager searchScheduleManager;

    @BeforeEach
    void setUp() {
        roomManager = new RoomManager();
        roomManager.addRoom("seminarRoom", "20", "projector,whiteboard");
        roomManager.addRoom("lectureTheatre", "100", "projector,whiteboard,mic");
        roomManager.addRoom("meetingRoom", "10", "whiteboard");
        bookingManager = new BookingManager();
        searchScheduleManager = new SearchScheduleManager(roomManager, bookingManager);
    }

    @Test
    void searchFiltersCapacityFeaturesAndConflicts() {
        bookingManager.putBooking(
                new Booking("b1", "u1", "r1", "2026-11-20", "09:00", "10:00", "class"));

        List<Room> rooms = searchScheduleManager.search(
                "2026-11-20", "09:30", "10:30", "15", "projector,whiteboard");

        assertEquals(1, rooms.size());
        assertEquals("r2", rooms.get(0).getRoomId());
    }

    @Test
    void searchRejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> searchScheduleManager.search(
                "bad-date", "09:00", "10:00", "10", "projector"));
        assertThrows(IllegalArgumentException.class, () -> searchScheduleManager.search(
                "2026-11-20", "10:00", "09:00", "10", "projector"));
        assertThrows(IllegalArgumentException.class, () -> searchScheduleManager.search(
                "2026-11-20", "09:00", "10:00", "0", "projector"));
    }

    @Test
    void scheduleUsesSevenDayWindowAndSortsBookings() {
        bookingManager.putBooking(
                new Booking("b3", "u1", "r1", "2026-11-22", "11:00", "12:00", "third"));
        bookingManager.putBooking(
                new Booking("b2", "u2", "r1", "2026-11-20", "10:00", "11:00", "second"));
        bookingManager.putBooking(
                new Booking("b1", "u1", "r1", "2026-11-20", "09:00", "10:00", "first"));
        bookingManager.putBooking(
                new Booking("outside", "u1", "r1", "2026-11-27", "09:00", "10:00", "outside"));
        bookingManager.putBooking(
                new Booking("other-room", "u1", "r2", "2026-11-20", "09:00", "10:00", "other"));

        List<Booking> schedule = searchScheduleManager.schedule("r1", "2026-11-20");

        assertEquals(3, schedule.size());
        assertEquals("b1", schedule.get(0).getBookingId());
        assertEquals("b2", schedule.get(1).getBookingId());
        assertEquals("b3", schedule.get(2).getBookingId());
    }

    @Test
    void scheduleRejectsUnknownRoomAndInvalidDate() {
        assertThrows(IllegalArgumentException.class,
                () -> searchScheduleManager.schedule("r9", "2026-11-20"));
        assertThrows(IllegalArgumentException.class,
                () -> searchScheduleManager.schedule("r1", "bad-date"));
    }

    @Test
    void formatScheduleAppliesUserIdPrivacy() {
        Booking own = new Booking("b1", "u1", "r1", "2026-11-20", "09:00", "10:00", "own");
        Booking other = new Booking("b2", "u2", "r1", "2026-11-21", "09:00", "10:00", "other");
        bookingManager.putBooking(own);
        bookingManager.putBooking(other);
        List<Booking> schedule = searchScheduleManager.schedule("r1", "2026-11-20");

        User regular = new User("u1", "Alice", Role.REGULAR);
        String regularView = searchScheduleManager.formatSchedule(schedule, regular);
        assertTrue(regularView.contains("user: u1"));
        assertFalse(regularView.contains("user: u2"));

        User admin = new User("u0", "Admin", Role.SUPERADMIN);
        String adminView = searchScheduleManager.formatSchedule(schedule, admin);
        assertTrue(adminView.contains("user: u1"));
        assertTrue(adminView.contains("user: u2"));
    }
}
