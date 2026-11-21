package hk.edu.polyu.comp.comp2021.crobs.model;

import hk.edu.polyu.comp.comp2021.crobs.manager.BookingManager;
import hk.edu.polyu.comp.comp2021.crobs.manager.RoomManager;
import hk.edu.polyu.comp.comp2021.crobs.manager.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UndoRedoTest {

    private static final String UNDONE = "Undo successful.";
    private static final String REDONE = "Redo successful.";

    @TempDir
    Path tempDir;

    private CROBS crobs;
    private UserManager users;
    private RoomManager rooms;
    private BookingManager bookings;

    @BeforeEach
    void setUp() {
        crobs = new CROBS(tempDir.resolve("log.html").toString(), tempDir.resolve("log.txt").toString());
        users = crobs.getUserManager();
        rooms = crobs.getRoomManager();
        bookings = crobs.getBookingManager();
        crobs.processCommand("login u0");
    }

    private String run(String command) {
        return crobs.processCommand(command);
    }

    @Test
    void undoAndRedoNeedLoginAndNoArguments() {
        assertEquals("Error: Invalid arguments.", run("undo now"));
        assertEquals("Error: Invalid arguments.", run("redo now"));
        run("logout");
        assertEquals("Error: No user is logged in.", run("undo"));
        assertEquals("Error: No user is logged in.", run("redo"));
    }

    @Test
    void nothingToUndoOrRedo() {
        assertEquals("Error: Nothing to undo.", run("undo"));
        assertEquals("Error: Nothing to redo.", run("redo"));
    }

    @Test
    void failedCommandsAreNotUndoable() {
        assertEquals("Error: User not found.", run("removeUser u99"));
        assertEquals("Error: Nothing to undo.", run("undo"));
    }

    @Test
    void historyIsClearedOnLoginAndLogout() {
        run("addUser Alice");
        run("logout");
        run("login u0");
        assertEquals("Error: Nothing to undo.", run("undo"));
        run("addUser Bob");
        run("undo");
        run("login u0");
        assertEquals("Error: Nothing to redo.", run("redo"));
        assertTrue(users.hasUser("u1"));
        assertFalse(users.hasUser("u2"));
    }

    @Test
    void newCommandAfterUndoClearsRedo() {
        run("addUser Alice");
        run("undo");
        run("addUser Bob");
        assertEquals("Error: Nothing to redo.", run("redo"));
        assertTrue(users.hasUser("u2"));
        assertFalse(users.hasUser("u1"));
    }

    @Test
    void addUserUndoRedoKeepsSameUser() {
        run("addUser Alice");
        User alice = users.getUser("u1");
        assertEquals(UNDONE, run("undo"));
        assertFalse(users.hasUser("u1"));
        assertEquals(REDONE, run("redo"));
        assertSame(alice, users.getUser("u1"));
        assertEquals("User u2 created.", run("addUser Bob"));
    }

    @Test
    void removeUserUndoRestoresUserAndBookings() {
        run("addRoom meetingRoom 8 tv");
        run("addUser Alice");
        run("login u1");
        run("book r1 2099-01-01 09:00 10:00 first");
        run("book r1 2099-01-02 09:00 10:00 second");
        run("login u0");
        run("removeUser u1");
        assertFalse(users.hasUser("u1"));
        assertTrue(bookings.getAllBookings().isEmpty());

        assertEquals(UNDONE, run("undo"));
        assertTrue(users.hasUser("u1"));
        assertEquals(2, bookings.listBookings("u1").size());

        assertEquals(REDONE, run("redo"));
        assertFalse(users.hasUser("u1"));
        assertTrue(bookings.getAllBookings().isEmpty());
    }

    @Test
    void changeRoleUndoRedo() {
        run("addUser Alice");
        run("changeRole u1 admin");
        run("undo");
        assertTrue(users.getUser("u1").isRegular());
        run("redo");
        assertFalse(users.getUser("u1").isRegular());
    }

    @Test
    void addRoomUndoRedoKeepsIdAndOrder() {
        run("addRoom meetingRoom 8 tv");
        run("addRoom meetingRoom 12 whiteboard");
        Room second = rooms.getRoom("r2");
        run("undo");
        assertFalse(rooms.hasRoom("r2"));
        run("redo");
        assertSame(second, rooms.getRoom("r2"));
        assertEquals("Room r3 created.", run("addRoom lectureTheatre 100 projector"));
    }

    @Test
    void modifyRoomUndoRedo() {
        run("addRoom lectureTheatre 100 projector");
        run("modifyRoom r1 capacity 120");
        run("undo");
        assertEquals(100, rooms.getRoom("r1").getCapacity());
        run("redo");
        assertEquals(120, rooms.getRoom("r1").getCapacity());
    }

    @Test
    void deleteRoomUndoRestoresRoomBookingsAndOrder() {
        for (int i = 0; i < 10; i++) {
            run("addRoom meetingRoom 8 tv");
        }
        run("book r2 2099-01-01 09:00 10:00 first");
        run("deleteRoom r2");
        assertFalse(rooms.hasRoom("r2"));
        assertTrue(bookings.getAllBookings().isEmpty());

        run("undo");
        assertTrue(rooms.hasRoom("r2"));
        assertEquals("r2", bookings.getBooking("b1").getRoomId());
        assertEquals("r1", rooms.getAllRooms().get(0).getRoomId());
        assertEquals("r2", rooms.getAllRooms().get(1).getRoomId());
        assertEquals("r10", rooms.getAllRooms().get(9).getRoomId());

        run("redo");
        assertFalse(rooms.hasRoom("r2"));
        assertNull(bookings.getBooking("b1"));
    }

    @Test
    void bookUndoRedoKeepsSameBooking() {
        run("addRoom meetingRoom 8 tv");
        run("book r1 2099-01-01 09:00 10:00 first");
        Booking booking = bookings.getBooking("b1");
        run("undo");
        assertNull(bookings.getBooking("b1"));
        run("redo");
        assertSame(booking, bookings.getBooking("b1"));
        assertEquals("Booking b2 created.", run("book r1 2099-01-02 09:00 10:00 second"));
    }

    @Test
    void modifyBookingUndoRedo() {
        run("addRoom meetingRoom 8 tv");
        run("addRoom meetingRoom 12 whiteboard");
        run("book r1 2099-01-10 09:00 10:00 team meeting");
        run("modifyBooking b1 r2 2099-01-11 10:00 11:00 updated purpose");
        Booking booking = bookings.getBooking("b1");

        run("undo");
        assertEquals("r1", booking.getRoomId());
        assertEquals("2099-01-10", booking.getDate());
        assertEquals("09:00", booking.getStartTime());
        assertEquals("10:00", booking.getEndTime());
        assertEquals("team meeting", booking.getPurpose());

        run("redo");
        assertEquals("r2", booking.getRoomId());
        assertEquals("2099-01-11", booking.getDate());
        assertEquals("10:00", booking.getStartTime());
        assertEquals("11:00", booking.getEndTime());
        assertEquals("updated purpose", booking.getPurpose());
    }

    @Test
    void deleteBookingUndoRedo() {
        run("addRoom meetingRoom 8 tv");
        run("book r1 2099-01-01 09:00 10:00 first");
        Booking booking = bookings.getBooking("b1");
        run("deleteBooking b1");
        run("undo");
        assertSame(booking, bookings.getBooking("b1"));
        run("redo");
        assertNull(bookings.getBooking("b1"));
    }

    @Test
    void multipleUndosWalkBackThroughHistory() {
        run("addRoom meetingRoom 8 tv");
        run("book r1 2099-01-01 09:00 10:00 first");
        run("undo");
        run("undo");
        assertFalse(rooms.hasRoom("r1"));
        assertEquals("Error: Nothing to undo.", run("undo"));
        run("redo");
        run("redo");
        assertEquals("r1", bookings.getBooking("b1").getRoomId());
    }
}
