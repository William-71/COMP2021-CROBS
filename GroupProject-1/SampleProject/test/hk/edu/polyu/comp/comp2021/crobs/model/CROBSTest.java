package hk.edu.polyu.comp.comp2021.crobs.model;

import hk.edu.polyu.comp.comp2021.crobs.manager.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CROBSTest {

    @TempDir
    Path tempDir;

    private CROBS crobs;
    private Path html;
    private Path txt;

    @BeforeEach
    void setUp() {
        html = tempDir.resolve("log.html");
        txt = tempDir.resolve("log.txt");
        crobs = new CROBS(html.toString(), txt.toString());
    }

    @Test
    void testCROBSConstructor() {
        CROBS defaultCrobs = new CROBS();
        assertNotNull(defaultCrobs);
        assertNotNull(defaultCrobs.getUserManager());
        assertNotNull(defaultCrobs.getRoomManager());
        assertNotNull(defaultCrobs.getBookingManager());
        assertNull(defaultCrobs.getCurrentUser());
        assertTrue(defaultCrobs.getUserManager().hasUser(UserManager.SUPERADMIN_ID));
    }

    // ---- REQ1 ----

    @Test
    void req1LogsEveryCommandIncludingErrors() throws IOException {
        crobs.processCommand("login u0");
        crobs.processCommand("addUser");
        crobs.processCommand("logout");

        String txtContent = new String(Files.readAllBytes(txt), StandardCharsets.UTF_8);
        assertTrue(txtContent.contains("login u0"));
        assertTrue(txtContent.contains("addUser"));
        assertTrue(txtContent.contains("logout"));

        String htmlContent = new String(Files.readAllBytes(html), StandardCharsets.UTF_8);
        assertTrue(htmlContent.contains("<tr><td>1</td><td>login u0</td></tr>"));
        assertTrue(htmlContent.contains("<tr><td>2</td><td>addUser</td></tr>"));
        assertTrue(htmlContent.contains("<tr><td>3</td><td>logout</td></tr>"));
    }

    // ---- REQ5 / REQ6 ----

    @Test
    void req5LoginSuccessAndFailures() {
        assertEquals("Error: Invalid arguments.", crobs.processCommand("login"));
        assertEquals("Error: User not found.", crobs.processCommand("login u99"));
        assertEquals("Logged in as u0.", crobs.processCommand("login u0"));
        assertEquals("u0", crobs.getCurrentUser().getUserId());
    }

    @Test
    void req6LogoutSuccessAndFailures() {
        assertEquals("Error: No user is logged in.", crobs.processCommand("logout"));
        assertEquals("Error: Invalid arguments.", crobs.processCommand("logout extra"));
        crobs.processCommand("login u0");
        assertEquals("Logged out.", crobs.processCommand("logout"));
        assertNull(crobs.getCurrentUser());
    }

    // ---- REQ2 ----

    @Test
    void req2AddUserHappyPathAndGuards() {
        assertEquals("Error: No user is logged in.", crobs.processCommand("addUser Alice"));
        crobs.processCommand("login u0");
        assertEquals("Error: Invalid arguments.", crobs.processCommand("addUser"));
        assertEquals("Error: Invalid arguments.", crobs.processCommand("addUser Alice Bob"));
        assertEquals("User u1 created.", crobs.processCommand("addUser Alice"));
        assertTrue(crobs.getUserManager().hasUser("u1"));
        assertEquals(Role.REGULAR, crobs.getUserManager().getUser("u1").getRole());

        crobs.processCommand("logout");
        crobs.processCommand("login u1");
        assertEquals("Error: Access denied.", crobs.processCommand("addUser Bob"));
    }

    // ---- REQ3 ----

    @Test
    void req3RemoveUserAndCancelBookings() {
        crobs.processCommand("login u0");
        crobs.processCommand("addUser Alice");
        crobs.processCommand("addUser Bob");

        Booking booking = new Booking("b1", "u1", "r1", "2026-01-01", "09:00", "10:00", "meeting");
        crobs.getBookingManager().putBooking(booking);
        assertNotNull(crobs.getBookingManager().getBooking("b1"));

        assertEquals("Error: Invalid arguments.", crobs.processCommand("removeUser"));
        assertEquals("Error: User not found.", crobs.processCommand("removeUser u99"));
        assertEquals("Error: Cannot remove this user.", crobs.processCommand("removeUser u0"));

        assertEquals("User u1 removed.", crobs.processCommand("removeUser u1"));
        assertFalse(crobs.getUserManager().hasUser("u1"));
        assertNull(crobs.getBookingManager().getBooking("b1"));

        crobs.processCommand("changeRole u2 admin");
        assertEquals("Error: Cannot remove this user.", crobs.processCommand("removeUser u2"));

        crobs.processCommand("logout");
        crobs.processCommand("login u2");
        assertEquals("User u3 created.", crobs.processCommand("addUser Carol"));
        // u2 is admin: can remove regular
        assertEquals("User u3 removed.", crobs.processCommand("removeUser u3"));
    }

    // ---- REQ4 ----

    @Test
    void req4ChangeRoleHappyPathAndGuards() {
        crobs.processCommand("login u0");
        crobs.processCommand("addUser Alice");

        assertEquals("Error: Invalid arguments.", crobs.processCommand("changeRole u1"));
        assertEquals("Error: Invalid role.", crobs.processCommand("changeRole u1 superadmin"));
        assertEquals("Error: User not found.", crobs.processCommand("changeRole u99 admin"));
        assertEquals("Error: Cannot change the role of the superadmin.",
                crobs.processCommand("changeRole u0 admin"));

        assertEquals("User u1 role changed to admin.", crobs.processCommand("changeRole u1 admin"));
        assertEquals(Role.ADMIN, crobs.getUserManager().getUser("u1").getRole());
        assertEquals("User u1 role changed to regular.", crobs.processCommand("changeRole u1 regular"));

        crobs.processCommand("logout");
        crobs.processCommand("login u1");
        assertEquals("Error: Access denied.", crobs.processCommand("changeRole u1 admin"));

        crobs.processCommand("logout");
        assertEquals("Error: No user is logged in.", crobs.processCommand("changeRole u1 admin"));
    }

    // ---- Dispatch / REQ18 skeleton ----

    @Test
    void unknownAndUnimplementedCommands() {
        assertEquals("Error: Unknown command.", crobs.processCommand("foo"));
        assertEquals("Error: Command not implemented.", crobs.processCommand("listAllRooms"));
        assertEquals("Error: Command not implemented.", crobs.processCommand("addRoom lectureTheatre 100 projector"));
    }

    @Test
    void quitStopsRunLoop() {
        String input = "login u0\nquit\naddUser ShouldNotRun\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuffer);

        crobs.run(in, out);

        String output = outBuffer.toString();
        assertTrue(output.contains("Logged in as u0."));
        assertFalse(output.contains("User u1 created."));
        assertFalse(crobs.isRunning());
    }

    @Test
    void emptyLinesIgnoredInProcessCommand() {
        assertEquals("", crobs.processCommand("   "));
        assertEquals(0, crobs.getLogger().getLoggedCount());
    }
}
