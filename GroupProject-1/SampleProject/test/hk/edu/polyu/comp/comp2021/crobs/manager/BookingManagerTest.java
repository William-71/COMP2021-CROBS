package hk.edu.polyu.comp.comp2021.crobs.manager;

import hk.edu.polyu.comp.comp2021.crobs.model.Booking;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BookingManagerTest {

    @Test
    void cancelBookingsByUserRemovesOnlyMatching() {
        BookingManager bm = new BookingManager();
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
        BookingManager bm = new BookingManager();
        bm.putBooking(new Booking("b1", "u1", "r1", "2026-01-01", "09:00", "10:00", "a"));
        bm.putBooking(new Booking("b2", "u2", "r2", "2026-01-01", "10:00", "11:00", "b"));
        assertEquals(1, bm.cancelBookingsByRoom("r1"));
        assertNull(bm.getBooking("b1"));
        assertEquals("b2", bm.getBooking("b2").getBookingId());
    }
}
