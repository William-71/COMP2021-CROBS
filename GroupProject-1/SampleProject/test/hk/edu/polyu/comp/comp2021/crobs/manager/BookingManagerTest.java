package hk.edu.polyu.comp.comp2021.crobs.manager;

import hk.edu.polyu.comp.comp2021.crobs.model.Booking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingManagerTest {

    private static BookingManager newManagerWithTwoRooms() {
        RoomManager rm = new RoomManager();
        rm.addRoom("meetingRoom", "10", "tv");
        rm.addRoom("meetingRoom", "10", "tv");
        return new BookingManager(rm);
    }

    private static void assertError(String expectedMessage, Executable action) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, action);
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void cancelBookingsByUserRemovesOnlyMatching() {
        BookingManager bm = newManagerWithTwoRooms();
        bm.putBooking(new Booking("b1", "u1", "r1", "2026-01-01", "09:00", "10:00", "a"));
        bm.putBooking(new Booking("b2", "u2", "r1", "2026-01-01", "10:00", "11:00", "b"));
        bm.putBooking(new Booking("b3", "u1", "r2", "2026-01-02", "09:00", "10:00", "c"));

        assertEquals(2, bm.cancelBookingsByUser("u1"));
        assertNull(bm.getBooking("b1"));
        assertNull(bm.getBooking("b3"));
        assertEquals("b2", bm.getBooking("b2").getBookingId());
    }

    @Test
    void cancelBookingsByRoom() {
        BookingManager bm = newManagerWithTwoRooms();
        bm.putBooking(new Booking("b1", "u1", "r1", "2099-01-01", "09:00", "10:00", "a"));
        bm.putBooking(new Booking("b2", "u2", "r2", "2099-01-01", "10:00", "11:00", "b"));
        assertEquals(1, bm.cancelBookingsByRoom("r1"));
        assertNull(bm.getBooking("b1"));
        assertEquals("b2", bm.getBooking("b2").getBookingId());
    }

    @Test
    void cancelBookingsByRoomKeepsPastBookings() {
        BookingManager bm = newManagerWithTwoRooms();
        bm.putBooking(new Booking("b1", "u1", "r1", "2000-01-01", "09:00", "10:00", "past"));
        bm.putBooking(new Booking("b2", "u1", "r1", "2099-01-01", "09:00", "10:00", "future"));
        assertEquals(1, bm.cancelBookingsByRoom("r1"));
        assertEquals("b1", bm.getBooking("b1").getBookingId());
        assertNull(bm.getBooking("b2"));
    }

    @Test
    void bookStoresFieldsAndGeneratesSequentialIds() {
        BookingManager bm = newManagerWithTwoRooms();
        Booking b1 = bm.book("r1", "2099-01-01", "09:00", "10:00", "lecture", "u1");
        Booking b2 = bm.book("r2", "2099-01-01", "09:00", "10:00", "seminar", "u2");

        assertEquals("b1", b1.getBookingId());
        assertEquals("b2", b2.getBookingId());
        assertEquals("u1", b1.getOwnerUserId());
        assertEquals("r1", b1.getRoomId());
        assertEquals("2099-01-01", b1.getDate());
        assertEquals("09:00", b1.getStartTime());
        assertEquals("10:00", b1.getEndTime());
        assertEquals("lecture", b1.getPurpose());
        assertSame(b1, bm.getBooking("b1"));
        assertEquals(2, bm.getAllBookings().size());
    }

    @Test
    void bookRejectsOverlapsButAllowsAdjacentAndOtherRoomsOrDates() {
        final BookingManager bm = newManagerWithTwoRooms();
        bm.book("r1", "2099-01-01", "09:00", "11:00", "first", "u1");

        for (final String[] slot : new String[][]{
            {"09:00", "11:00"}, {"08:00", "09:30"}, {"10:30", "12:00"}, {"09:30", "10:30"}, {"08:00", "12:00"}}) {
            assertError("Error: Room already booked.", new Executable() {
                @Override
                public void execute() {
                    bm.book("r1", "2099-01-01", slot[0], slot[1], "clash", "u2");
                }
            });
        }

        assertEquals("b2", bm.book("r1", "2099-01-01", "11:00", "12:00", "after", "u2").getBookingId());
        assertEquals("b3", bm.book("r1", "2099-01-01", "08:00", "09:00", "before", "u2").getBookingId());
        assertEquals("b4", bm.book("r2", "2099-01-01", "09:00", "11:00", "other room", "u2").getBookingId());
        assertEquals("b5", bm.book("r1", "2099-01-02", "09:00", "11:00", "other date", "u2").getBookingId());
    }

    @Test
    void bookRejectsInvalidInputWithoutConsumingId() {
        final BookingManager bm = newManagerWithTwoRooms();
        assertError(RoomManager.ERR_ROOM_NOT_FOUND, new Executable() {
            @Override
            public void execute() {
                bm.book("r9", "2099-01-01", "09:00", "10:00", "x", "u1");
            }
        });
        assertError("Error: Invalid date.", new Executable() {
            @Override
            public void execute() {
                bm.book("r1", "2099-02-30", "09:00", "10:00", "x", "u1");
            }
        });
        assertError("Error: Invalid time.", new Executable() {
            @Override
            public void execute() {
                bm.book("r1", "2099-01-01", "9am", "10:00", "x", "u1");
            }
        });
        assertError("Error: Invalid time range.", new Executable() {
            @Override
            public void execute() {
                bm.book("r1", "2099-01-01", "10:00", "10:00", "x", "u1");
            }
        });
        assertError("Error: Invalid arguments.", new Executable() {
            @Override
            public void execute() {
                bm.book("r1", "2099-01-01", "09:00", "10:00", " ", "u1");
            }
        });
        assertTrue(bm.getAllBookings().isEmpty());
        assertEquals("b1", bm.book("r1", "2099-01-01", "09:00", "10:00", "x", "u1").getBookingId());
    }

    @Test
    void modifyBookingUpdatesAllFieldsForOwner() {
        BookingManager bm = newManagerWithTwoRooms();
        bm.book("r1", "2099-01-01", "09:00", "10:00", "old", "u1");

        Booking modified = bm.modifyBooking("b1", "r2", "2099-01-02", "13:00", "14:00", "new", "u1");

        assertEquals("b1", modified.getBookingId());
        assertEquals("u1", modified.getOwnerUserId());
        assertEquals("r2", modified.getRoomId());
        assertEquals("2099-01-02", modified.getDate());
        assertEquals("13:00", modified.getStartTime());
        assertEquals("14:00", modified.getEndTime());
        assertEquals("new", modified.getPurpose());
    }

    @Test
    void modifyBookingMayOverlapItselfButNotOthers() {
        final BookingManager bm = newManagerWithTwoRooms();
        bm.book("r1", "2099-01-01", "09:00", "10:00", "mine", "u1");
        bm.book("r1", "2099-01-01", "10:00", "11:00", "theirs", "u2");

        assertEquals("09:30", bm.modifyBooking("b1", "r1", "2099-01-01", "09:00", "09:30", "shorter", "u1")
                .getEndTime());
        assertError("Error: Room already booked.", new Executable() {
            @Override
            public void execute() {
                bm.modifyBooking("b1", "r1", "2099-01-01", "09:30", "10:30", "clash", "u1");
            }
        });
        assertEquals("shorter", bm.getBooking("b1").getPurpose());
    }

    @Test
    void modifyBookingRejectsBadRequestsAndKeepsOldValues() {
        final BookingManager bm = newManagerWithTwoRooms();
        bm.book("r1", "2099-01-01", "09:00", "10:00", "keep", "u1");

        assertError("Error: Booking not found.", new Executable() {
            @Override
            public void execute() {
                bm.modifyBooking("b9", "r1", "2099-01-01", "09:00", "10:00", "x", "u1");
            }
        });
        assertError("Error: Access denied.", new Executable() {
            @Override
            public void execute() {
                bm.modifyBooking("b1", "r1", "2099-01-01", "09:00", "10:00", "x", "u2");
            }
        });
        assertError(RoomManager.ERR_ROOM_NOT_FOUND, new Executable() {
            @Override
            public void execute() {
                bm.modifyBooking("b1", "r9", "2099-01-01", "09:00", "10:00", "x", "u1");
            }
        });
        assertError("Error: Invalid time range.", new Executable() {
            @Override
            public void execute() {
                bm.modifyBooking("b1", "r1", "2099-01-01", "11:00", "10:00", "x", "u1");
            }
        });

        Booking booking = bm.getBooking("b1");
        assertEquals("r1", booking.getRoomId());
        assertEquals("09:00", booking.getStartTime());
        assertEquals("keep", booking.getPurpose());
    }

    @Test
    void deleteBookingOnlyByOwner() {
        final BookingManager bm = newManagerWithTwoRooms();
        bm.book("r1", "2099-01-01", "09:00", "10:00", "x", "u1");

        assertError("Error: Access denied.", new Executable() {
            @Override
            public void execute() {
                bm.deleteBooking("b1", "u2");
            }
        });
        assertError("Error: Booking not found.", new Executable() {
            @Override
            public void execute() {
                bm.deleteBooking("b9", "u1");
            }
        });
        assertEquals("b1", bm.deleteBooking("b1", "u1").getBookingId());
        assertNull(bm.getBooking("b1"));
        assertEquals("b2", bm.book("r1", "2099-01-01", "09:00", "10:00", "rebook", "u2").getBookingId());
    }

    @Test
    void listBookingsReturnsOwnBookingsSortedByDateThenTime() {
        BookingManager bm = newManagerWithTwoRooms();
        bm.book("r1", "2099-01-02", "09:00", "10:00", "c", "u1");
        bm.book("r1", "2099-01-01", "13:00", "14:00", "b", "u1");
        bm.book("r2", "2099-01-01", "09:00", "10:00", "a", "u1");
        bm.book("r2", "2099-01-01", "11:00", "12:00", "other user", "u2");

        List<Booking> list = bm.listBookings("u1");
        assertEquals(3, list.size());
        assertEquals("a", list.get(0).getPurpose());
        assertEquals("b", list.get(1).getPurpose());
        assertEquals("c", list.get(2).getPurpose());
        assertTrue(bm.listBookings("u3").isEmpty());
    }

    @Test
    void isRoomAvailableChecksExistingBookings() {
        final BookingManager bm = newManagerWithTwoRooms();
        bm.book("r1", "2099-01-01", "09:00", "10:00", "x", "u1");

        assertFalse(bm.isRoomAvailable("r1", "2099-01-01", "09:30", "10:30"));
        assertTrue(bm.isRoomAvailable("r1", "2099-01-01", "10:00", "11:00"));
        assertTrue(bm.isRoomAvailable("r2", "2099-01-01", "09:30", "10:30"));
        assertError(RoomManager.ERR_ROOM_NOT_FOUND, new Executable() {
            @Override
            public void execute() {
                bm.isRoomAvailable("r9", "2099-01-01", "09:00", "10:00");
            }
        });
    }
}
