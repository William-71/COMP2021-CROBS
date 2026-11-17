package hk.edu.polyu.comp.comp2021.crobs.manager;

import hk.edu.polyu.comp.comp2021.crobs.model.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
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


    @Test
    void modifyRoomUpdatesEachField() {
        rm.addRoom("seminarRoom", "30", "whiteboard");
        rm.modifyRoom("r1", "roomType", "meetingRoom");
        rm.modifyRoom("r1", "capacity", "12");
        Room room = rm.modifyRoom("r1", "features", "tv,phone");
        assertEquals("meetingRoom", room.getRoomType());
        assertEquals(12, room.getCapacity());
        assertEquals("tv,phone", room.getFeatures());
    }

    @Test
    void modifyRoomRejectsBadInputAndKeepsOldValues() {
        rm.addRoom("seminarRoom", "30", "whiteboard");
        assertError(RoomManager.ERR_ROOM_NOT_FOUND, new Executable() {
            @Override
            public void execute() {
                rm.modifyRoom("r9", "capacity", "5");
            }
        });
        assertError(RoomManager.ERR_INVALID_FIELD, new Executable() {
            @Override
            public void execute() {
                rm.modifyRoom("r1", "roomId", "r5");
            }
        });
        assertError(Room.ERR_INVALID_TYPE, new Executable() {
            @Override
            public void execute() {
                rm.modifyRoom("r1", "roomType", "hall");
            }
        });
        assertError(Room.ERR_INVALID_CAPACITY, new Executable() {
            @Override
            public void execute() {
                rm.modifyRoom("r1", "capacity", "0");
            }
        });
        assertError(Room.ERR_INVALID_FEATURES, new Executable() {
            @Override
            public void execute() {
                rm.modifyRoom("r1", "features", "");
            }
        });
        Room room = rm.getRoom("r1");
        assertEquals("seminarRoom", room.getRoomType());
        assertEquals(30, room.getCapacity());
        assertEquals("whiteboard", room.getFeatures());
    }


    @Test
    void deleteRoomRemovesItAndIdIsNotReused() {
        rm.addRoom("meetingRoom", "8", "tv");
        Room deleted = rm.deleteRoom("r1");
        assertEquals("r1", deleted.getRoomId());
        assertFalse(rm.hasRoom("r1"));
        assertNull(rm.getRoom("r1"));
        assertEquals("r2", rm.addRoom("meetingRoom", "8", "tv").getRoomId());
    }

    @Test
    void deleteUnknownRoomFails() {
        assertError(RoomManager.ERR_ROOM_NOT_FOUND, new Executable() {
            @Override
            public void execute() {
                rm.deleteRoom("r1");
            }
        });
        assertError(RoomManager.ERR_ROOM_NOT_FOUND, new Executable() {
            @Override
            public void execute() {
                rm.deleteRoom(null);
            }
        });
        assertFalse(rm.hasRoom(null));
    }


}
