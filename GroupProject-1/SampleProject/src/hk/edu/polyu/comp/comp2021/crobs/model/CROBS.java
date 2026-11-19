package hk.edu.polyu.comp.comp2021.crobs.model;

import hk.edu.polyu.comp.comp2021.crobs.manager.BookingManager;
import hk.edu.polyu.comp.comp2021.crobs.manager.RoomManager;
import hk.edu.polyu.comp.comp2021.crobs.manager.SearchScheduleManager;
import hk.edu.polyu.comp.comp2021.crobs.manager.UserManager;
import hk.edu.polyu.comp.comp2021.crobs.model.command.AddRoomCommand;
import hk.edu.polyu.comp.comp2021.crobs.model.command.AddUserCommand;
import hk.edu.polyu.comp.comp2021.crobs.model.command.BookCommand;
import hk.edu.polyu.comp.comp2021.crobs.model.command.ChangeRoleCommand;
import hk.edu.polyu.comp.comp2021.crobs.model.command.CommandHistory;
import hk.edu.polyu.comp.comp2021.crobs.model.command.DeleteBookingCommand;
import hk.edu.polyu.comp.comp2021.crobs.model.command.DeleteRoomCommand;
import hk.edu.polyu.comp.comp2021.crobs.model.command.ModifyBookingCommand;
import hk.edu.polyu.comp.comp2021.crobs.model.command.ModifyRoomCommand;
import hk.edu.polyu.comp.comp2021.crobs.model.command.RemoveUserCommand;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

/**
 * Core Crobs model / command dispatcher.
 * Wires UserManager, RoomManager, BookingManager, and SearchScheduleManager to the CLI.
 */
public class CROBS {

    private final UserManager userManager;
    private final RoomManager roomManager;
    private final BookingManager bookingManager;
    private final SearchScheduleManager searchScheduleManager;
    private final CommandLogger logger;
    private final CommandHistory history;
    private boolean running;

    /**
     * Default constructor (no file logging). Kept for sample / auto-grading compatibility.
     */
    public CROBS() {
        this(null, null);
    }

    /**
     * @param htmlPath path for HTML command log (REQ1), may be null
     * @param txtPath  path for TXT command log (REQ1), may be null
     */
    public CROBS(String htmlPath, String txtPath) {
        this.userManager = new UserManager();
        this.roomManager = new RoomManager();
        this.bookingManager = new BookingManager(this.roomManager);
        this.searchScheduleManager = new SearchScheduleManager(this.roomManager, this.bookingManager);
        this.logger = new CommandLogger(htmlPath, txtPath);
        this.history = new CommandHistory();
        this.running = false;
    }

    /**
     * @return the user manager
     */
    public UserManager getUserManager() {
        return userManager;
    }

    /**
     * @return the room manager
     */
    public RoomManager getRoomManager() {
        return roomManager;
    }

    /**
     * @return the booking manager
     */
    public BookingManager getBookingManager() {
        return bookingManager;
    }

    /**
     * @return the search and schedule manager
     */
    public SearchScheduleManager getSearchScheduleManager() {
        return searchScheduleManager;
    }

    /**
     * @return the command logger
     */
    public CommandLogger getLogger() {
        return logger;
    }

    /**
     * @return the logged-in user, or null
     */
    public User getCurrentUser() {
        return userManager.getCurrentUser();
    }

    /**
     * @return true while the CLI loop is running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Interactive CLI loop. Terminates when {@code quit} is processed.
     *
     * @param in  input stream
     * @param out output stream
     */
    public void run(InputStream in, PrintStream out) {
        running = true;
        Scanner scanner = new Scanner(in);
        while (running && scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line == null) {
                break;
            }
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            String result = processCommand(line);
            if (result != null && !result.isEmpty()) {
                out.println(result);
            }
        }
    }

    /**
     * Parses and executes one command line. Always logs the command (REQ1).
     *
     * @param line raw command line
     * @return message to print, or empty string if none; never {@code null}
     */
    public String processCommand(String line) {
        if (line == null) {
            return "Error: Invalid arguments.";
        }
        String trimmed = line.trim();
        if (trimmed.isEmpty()) {
            return "";
        }

        logger.log(trimmed);

        String[] parts = trimmed.split("\\s+");
        String cmd = parts[0];

        try {
            if ("addUser".equals(cmd)) {
                return handleAddUser(parts);
            } else if ("removeUser".equals(cmd)) {
                return handleRemoveUser(parts);
            } else if ("changeRole".equals(cmd)) {
                return handleChangeRole(parts);
            } else if ("login".equals(cmd)) {
                return handleLogin(parts);
            } else if ("logout".equals(cmd)) {
                return handleLogout(parts);
            } else if ("quit".equals(cmd)) {
                return handleQuit(parts);
            } else if ("addRoom".equals(cmd)) {
                return handleAddRoom(parts);
            } else if ("modifyRoom".equals(cmd)) {
                return handleModifyRoom(parts);
            } else if ("deleteRoom".equals(cmd)) {
                return handleDeleteRoom(parts);
            } else if ("book".equals(cmd)) {
                return handleBook(parts);
            } else if ("modifyBooking".equals(cmd)) {
                return handleModifyBooking(parts);
            } else if ("deleteBooking".equals(cmd)) {
                return handleDeleteBooking(parts);
            } else if ("search".equals(cmd)) {
                return handleSearch(parts);
            } else if ("schedule".equals(cmd)) {
                return handleSchedule(parts);
            } else if ("listRoom".equals(cmd)) {
                return handleListRoom(parts);
            } else if ("listAllRooms".equals(cmd)) {
                return handleListAllRooms(parts);
            } else if ("listBookings".equals(cmd)) {
                return handleListBookings(parts);
            } else if ("undo".equals(cmd)) {
                return handleUndo(parts);
            } else if ("redo".equals(cmd)) {
                return handleRedo(parts);
            } else {
                return "Error: Unknown command.";
            }
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (IllegalStateException e) {
            return e.getMessage();
        } catch (UnsupportedOperationException e) {
            return "Error: Command not implemented.";
        }
    }

    private String handleAddUser(String[] parts) {
        if (parts.length != 2) {
            return "Error: Invalid arguments.";
        }
        return history.run(new AddUserCommand(userManager, parts[1]));
    }

    private String handleRemoveUser(String[] parts) {
        if (parts.length != 2) {
            return "Error: Invalid arguments.";
        }
        return history.run(new RemoveUserCommand(userManager, bookingManager, parts[1]));
    }

    private String handleChangeRole(String[] parts) {
        if (parts.length != 3) {
            return "Error: Invalid arguments.";
        }
        return history.run(new ChangeRoleCommand(userManager, parts[1], parts[2]));
    }

    private String handleLogin(String[] parts) {
        if (parts.length != 2) {
            return "Error: Invalid arguments.";
        }
        User user = userManager.login(parts[1]);
        history.clear();
        return "Logged in as " + user.getUserId() + ".";
    }

    private String handleLogout(String[] parts) {
        if (parts.length != 1) {
            return "Error: Invalid arguments.";
        }
        userManager.logout();
        history.clear();
        return "Logged out.";
    }

    private String handleQuit(String[] parts) {
        if (parts.length != 1) {
            return "Error: Invalid arguments.";
        }
        running = false;
        return "";
    }

    // ---- REQ7–REQ9, REQ15–REQ16 (RoomManager) ----

    private String handleAddRoom(String[] parts) {
        if (parts.length != 4) {
            return "Error: Invalid arguments.";
        }
        userManager.requireAdmin();
        return history.run(new AddRoomCommand(roomManager, parts[1], parts[2], parts[3]));
    }

    private String handleModifyRoom(String[] parts) {
        if (parts.length != 4) {
            return "Error: Invalid arguments.";
        }
        userManager.requireAdmin();
        return history.run(new ModifyRoomCommand(roomManager, parts[1], parts[2], parts[3]));
    }

    private String handleDeleteRoom(String[] parts) {
        if (parts.length != 2) {
            return "Error: Invalid arguments.";
        }
        userManager.requireAdmin();
        return history.run(new DeleteRoomCommand(roomManager, bookingManager, parts[1]));
    }

    private String handleListRoom(String[] parts) {
        if (parts.length != 2) {
            return "Error: Invalid arguments.";
        }
        return roomManager.listRoom(parts[1]);
    }

    private String handleListAllRooms(String[] parts) {
        if (parts.length != 1) {
            return "Error: Invalid arguments.";
        }
        return roomManager.listAllRooms();
    }

    // ---- REQ10–REQ12, REQ17 (BookingManager) ----

    private String handleBook(String[] parts) {
        if (parts.length < 6) {
            return "Error: Invalid arguments.";
        }
        userManager.requireLoggedIn();
        String purpose = joinFrom(parts, 5);
        return history.run(new BookCommand(bookingManager, parts[1], parts[2], parts[3], parts[4], purpose,
                userManager.getCurrentUser().getUserId()));
    }

    private String handleModifyBooking(String[] parts) {
        if (parts.length < 7) {
            return "Error: Invalid arguments.";
        }
        userManager.requireLoggedIn();
        String purpose = joinFrom(parts, 6);
        return history.run(new ModifyBookingCommand(bookingManager, parts[1], parts[2], parts[3], parts[4],
                parts[5], purpose, userManager.getCurrentUser().getUserId()));
    }

    private String handleDeleteBooking(String[] parts) {
        if (parts.length != 2) {
            return "Error: Invalid arguments.";
        }
        userManager.requireLoggedIn();
        return history.run(new DeleteBookingCommand(bookingManager, parts[1],
                userManager.getCurrentUser().getUserId()));
    }

    private String handleListBookings(String[] parts) {
        if (parts.length != 1) {
            return "Error: Invalid arguments.";
        }
        userManager.requireLoggedIn();
        List<Booking> bookings = bookingManager.listBookings(
                userManager.getCurrentUser().getUserId());
        if (bookings.isEmpty()) {
            return "No bookings.";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bookings.size(); i++) {
            if (i > 0) {
                sb.append(System.lineSeparator());
            }
            Booking booking = bookings.get(i);
            sb.append(booking.getBookingId())
                    .append(" | room: ").append(booking.getRoomId())
                    .append(" | date: ").append(booking.getDate())
                    .append(" | time: ").append(booking.getStartTime())
                    .append('-').append(booking.getEndTime())
                    .append(" | purpose: ").append(booking.getPurpose());
        }
        return sb.toString();
    }

    // ---- REQ13–REQ14 (SearchScheduleManager) ----

    private String handleSearch(String[] parts) {
        if (parts.length != 6) {
            return "Error: Invalid arguments.";
        }
        List<Room> rooms = searchScheduleManager.search(
                parts[1], parts[2], parts[3], parts[4], parts[5]);
        return searchScheduleManager.formatSearchResults(rooms);
    }

    private String handleSchedule(String[] parts) {
        if (parts.length != 3) {
            return "Error: Invalid arguments.";
        }
        userManager.requireLoggedIn();
        List<Booking> bookings = searchScheduleManager.schedule(parts[1], parts[2]);
        return searchScheduleManager.formatSchedule(bookings, userManager.getCurrentUser());
    }

    private String handleUndo(String[] parts) {
        if (parts.length != 1) {
            return "Error: Invalid arguments.";
        }
        userManager.requireLoggedIn();
        history.undo();
        return "Undo successful.";
    }

    private String handleRedo(String[] parts) {
        if (parts.length != 1) {
            return "Error: Invalid arguments.";
        }
        userManager.requireLoggedIn();
        history.redo();
        return "Redo successful.";
    }

    private static String joinFrom(String[] parts, int startIndex) {
        StringBuilder sb = new StringBuilder();
        for (int i = startIndex; i < parts.length; i++) {
            if (i > startIndex) {
                sb.append(' ');
            }
            sb.append(parts[i]);
        }
        return sb.toString();
    }
}
