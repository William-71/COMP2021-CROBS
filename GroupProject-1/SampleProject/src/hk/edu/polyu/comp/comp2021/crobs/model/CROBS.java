package hk.edu.polyu.comp.comp2021.crobs.model;

import hk.edu.polyu.comp.comp2021.crobs.manager.BookingManager;
import hk.edu.polyu.comp.comp2021.crobs.manager.RoomManager;
import hk.edu.polyu.comp.comp2021.crobs.manager.UserManager;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Core Crobs model / command dispatcher.
 * Member A implements REQ1–REQ6 and the shared command framework.
 */
public class CROBS {

    private final UserManager userManager;
    private final RoomManager roomManager;
    private final BookingManager bookingManager;
    private final CommandLogger logger;
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
        this.logger = new CommandLogger(htmlPath, txtPath);
        this.running = false;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }

    public BookingManager getBookingManager() {
        return bookingManager;
    }

    public CommandLogger getLogger() {
        return logger;
    }

    public User getCurrentUser() {
        return userManager.getCurrentUser();
    }

    public boolean isRunning() {
        return running;
    }

    /**
     * Interactive CLI loop. Terminates when {@code quit} is processed.
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
            } else if ("addRoom".equals(cmd)
                    || "modifyRoom".equals(cmd)
                    || "deleteRoom".equals(cmd)
                    || "book".equals(cmd)
                    || "modifyBooking".equals(cmd)
                    || "deleteBooking".equals(cmd)
                    || "search".equals(cmd)
                    || "schedule".equals(cmd)
                    || "listRoom".equals(cmd)
                    || "listAllRooms".equals(cmd)
                    || "listBookings".equals(cmd)) {
                return "Error: Command not implemented.";
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
        User user = userManager.addUser(parts[1]);
        return "User " + user.getUserId() + " created.";
    }

    private String handleRemoveUser(String[] parts) {
        if (parts.length != 2) {
            return "Error: Invalid arguments.";
        }
        String targetId = parts[1];
        User removed = userManager.removeUser(targetId);
        bookingManager.cancelBookingsByUser(removed.getUserId());
        return "User " + removed.getUserId() + " removed.";
    }

    private String handleChangeRole(String[] parts) {
        if (parts.length != 3) {
            return "Error: Invalid arguments.";
        }
        User user = userManager.changeRole(parts[1], parts[2]);
        return "User " + user.getUserId() + " role changed to " + user.getRole().toCommandToken() + ".";
    }

    private String handleLogin(String[] parts) {
        if (parts.length != 2) {
            return "Error: Invalid arguments.";
        }
        User user = userManager.login(parts[1]);
        return "Logged in as " + user.getUserId() + ".";
    }

    private String handleLogout(String[] parts) {
        if (parts.length != 1) {
            return "Error: Invalid arguments.";
        }
        userManager.logout();
        return "Logged out.";
    }

    private String handleQuit(String[] parts) {
        if (parts.length != 1) {
            return "Error: Invalid arguments.";
        }
        running = false;
        return "";
    }
}
