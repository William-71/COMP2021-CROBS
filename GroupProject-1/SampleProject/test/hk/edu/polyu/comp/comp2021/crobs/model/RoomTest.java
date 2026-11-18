package hk.edu.polyu.comp.comp2021.crobs.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoomTest {

    private static void assertError(String expectedMessage, Executable action) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, action);
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void constructorStoresFields() {
        Room room = new Room("r1", "lectureTheatre", 120, "projector,whiteboard");
        assertEquals("r1", room.getRoomId());
        assertEquals("lectureTheatre", room.getRoomType());
        assertEquals(120, room.getCapacity());
        assertEquals("projector,whiteboard", room.getFeatures());
        assertEquals(1, room.getIdNumber());
    }

    @Test
    void allRoomTypesAreAccepted() {
        for (String type : new String[]{"lectureTheatre", "computerLab", "seminarRoom", "meetingRoom"}) {
            assertEquals(type, new Room("r1", type, 10, "x").getRoomType());
        }
        for (final String bad : new String[]{"kitchen", "LectureTheatre", null}) {
            assertError("Error: Invalid room type.", new Executable() {
                @Override
                public void execute() {
                    new Room("r1", bad, 10, "x");
                }
            });
        }
    }

    @Test
    void constructorRejectsInvalidValues() {
        assertError("Error: Invalid room type.", new Executable() {
            @Override
            public void execute() {
                new Room("r1", "kitchen", 10, "x");
            }
        });
        assertError("Error: Invalid capacity.", new Executable() {
            @Override
            public void execute() {
                new Room("r1", "computerLab", 0, "x");
            }
        });
        assertError("Error: Invalid features.", new Executable() {
            @Override
            public void execute() {
                new Room("r1", "computerLab", 10, "a, b");
            }
        });
    }

    @Test
    void parseCapacity() {
        assertEquals(30, Room.parseCapacity("30"));
        for (final String bad : new String[]{null, "", "0", "-5", "abc", "3.5", "+3", "99999999999"}) {
            assertError("Error: Invalid capacity.", new Executable() {
                @Override
                public void execute() {
                    Room.parseCapacity(bad);
                }
            });
        }
    }

    @Test
    void parseFeatures() {
        assertEquals(Collections.singletonList("projector"), Room.parseFeatures("projector"));
        assertEquals(Arrays.asList("a", "b"), Room.parseFeatures("a,b,a"));
        for (final String bad : new String[]{null, "", ",", "a,", ",a", "a,,b", "a b", "a,\tb"}) {
            assertError("Error: Invalid features.", new Executable() {
                @Override
                public void execute() {
                    Room.parseFeatures(bad);
                }
            });
        }
    }

    @Test
    void settersValidateAndUpdate() {
        final Room room = new Room("r2", "seminarRoom", 20, "whiteboard");
        room.setRoomType("meetingRoom");
        room.setCapacity(8);
        room.setFeatures("tv,phone");
        assertEquals("meetingRoom", room.getRoomType());
        assertEquals(8, room.getCapacity());
        assertEquals("tv,phone", room.getFeatures());

        assertError("Error: Invalid capacity.", new Executable() {
            @Override
            public void execute() {
                room.setCapacity(-1);
            }
        });
        assertEquals(8, room.getCapacity());
    }

    @Test
    void featureQueries() {
        Room room = new Room("r1", "computerLab", 40, "pc,projector,whiteboard");
        List<String> required = Arrays.asList("pc", "whiteboard");
        assertTrue(room.hasAllFeatures(required));
        assertFalse(room.hasAllFeatures(Arrays.asList("pc", "tv")));
        assertTrue(room.hasAllFeatures(Collections.<String>emptyList()));
    }

    @Test
    void idNumberFallsBackForMalformedId() {
        assertEquals(12, new Room("r12", "computerLab", 1, "pc").getIdNumber());
        assertEquals(Integer.MAX_VALUE, new Room("rx", "computerLab", 1, "pc").getIdNumber());
    }

    @Test
    void toStringDescribesRoom() {
        Room room = new Room("r3", "meetingRoom", 6, "tv");
        assertEquals("r3 | type: meetingRoom | capacity: 6 | features: tv", room.toString());
    }
}
