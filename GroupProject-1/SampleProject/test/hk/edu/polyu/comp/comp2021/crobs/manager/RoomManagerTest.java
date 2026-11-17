package hk.edu.polyu.comp.comp2021.crobs.manager;

import hk.edu.polyu.comp.comp2021.crobs.model.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoomManagerTest {

    private RoomManager rm;

    @BeforeEach
    void setUp() {
        rm = new RoomManager();
    }

    private static void assertError(String expectedMessage, Executable action) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, action);
        assertEquals(expectedMessage, ex.getMessage());
    }


    @Test
    void addRoomGeneratesSequentialIds() {
        Room r1 = rm.addRoom("lectureTheatre", "100", "projector,mic");
        Room r2 = rm.addRoom("computerLab", 40, "pc");
        assertEquals("r1", r1.getRoomId());
        assertEquals("r2", r2.getRoomId());
        assertEquals(100, r1.getCapacity());
        assertEquals("projector,mic", r1.getFeatures());
        assertSame(r1, rm.getRoom("r1"));
        assertTrue(rm.hasRoom("r2"));
    }

    @Test
    void addRoomRejectsInvalidInputWithoutConsumingId() {
        assertError(Room.ERR_INVALID_TYPE, new Executable() {
            @Override
            public void execute() {
                rm.addRoom("office", "10", "pc");
            }
        });
        assertError(Room.ERR_INVALID_CAPACITY, new Executable() {
            @Override
            public void execute() {
                rm.addRoom("computerLab", "ten", "pc");
            }
        });
        assertError(Room.ERR_INVALID_FEATURES, new Executable() {
            @Override
            public void execute() {
                rm.addRoom("computerLab", "10", "pc,,tv");
            }
        });
        assertTrue(rm.getAllRooms().isEmpty());
        assertEquals("r1", rm.addRoom("computerLab", "10", "pc").getRoomId());
    }




}
