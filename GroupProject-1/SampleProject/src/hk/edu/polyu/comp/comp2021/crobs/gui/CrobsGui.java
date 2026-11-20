package hk.edu.polyu.comp.comp2021.crobs.gui;

import hk.edu.polyu.comp.comp2021.crobs.model.Booking;
import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;
import hk.edu.polyu.comp.comp2021.crobs.model.Room;
import hk.edu.polyu.comp.comp2021.crobs.model.User;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Graphical user interface for Crobs (BON1). Every action is sent to
 * {@link CROBS#processCommand(String)}, so it is logged and undoable like a typed command.
 * Launch example:
 * {@code java hk.edu.polyu.comp.comp2021.crobs.gui.CrobsGui -html d:\log.html -txt d:\log.txt}
 */
public class CrobsGui {

    private static final int WIDTH = 900;
    private static final int HEIGHT = 600;
    private static final int LOG_ROWS = 8;
    private static final String DATE = "Date (yyyy-MM-dd)";
    private static final String START = "Start (HH:mm)";
    private static final String END = "End (HH:mm)";

    private final CROBS crobs;
    private final JFrame frame = new JFrame("Crobs");
    private final JLabel status = new JLabel();
    private final JTextArea log = new JTextArea(LOG_ROWS, 0);
    private final List<CommandTab> tabs = new ArrayList<>();

    /**
     * @param crobs system the window controls
     */
    public CrobsGui(CROBS crobs) {
        this.crobs = crobs;
        JTabbedPane tabPane = new JTabbedPane();
        tabPane.addTab("Users", tab(this::userRows, "User ID", "Name", "Role")
                .action("Add", "addUser", "Name")
                .onSelected("Remove selected", "removeUser")
                .onSelected("Change role of selected", "changeRole", "Role (admin or regular)"));
        tabPane.addTab("Rooms", tab(this::roomRows, "Room ID", "Type", "Capacity", "Features")
                .action("Add", "addRoom",
                        "Type (lectureTheatre, computerLab, seminarRoom or meetingRoom)", "Capacity",
                        "Features (comma-separated)")
                .onSelected("Modify selected", "modifyRoom", "Field (roomType, capacity or features)",
                        "New value")
                .onSelected("Delete selected", "deleteRoom")
                .action("Search free rooms", "search", DATE, START, END, "Minimum capacity",
                        "Features (comma-separated)")
                .onSelected("Schedule of selected", "schedule", "Week start " + DATE));
        tabPane.addTab("My bookings", tab(this::bookingRows, "Booking ID", "Room", "Date", "Start", "End", "Purpose")
                .action("Book", "book", "Room ID", DATE, START, END, "Purpose")
                .onSelected("Modify selected", "modifyBooking", "Room ID", DATE, START, END, "Purpose")
                .onSelected("Delete selected", "deleteBooking"));

        JTextField command = new JTextField();
        command.addActionListener(e -> {
            execute(command.getText().trim());
            command.setText("");
        });
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(new JLabel("Command: "), BorderLayout.WEST);
        bottom.add(command, BorderLayout.CENTER);
        bottom.add(new JScrollPane(log), BorderLayout.SOUTH);
        log.setEditable(false);

        JPanel top = new JPanel();
        top.add(button("Login", "login", () -> "", "User ID"));
        top.add(button("Logout", "logout", () -> ""));
        top.add(button("Undo", "undo", () -> ""));
        top.add(button("Redo", "redo", () -> ""));
        top.add(status);

        frame.add(top, BorderLayout.NORTH);
        frame.add(tabPane, BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);
        frame.setSize(WIDTH, HEIGHT);
        refresh();
    }

    /**
     * @param args -html and -txt log paths
     */
    public static void main(String[] args) {
        List<String> options = Arrays.asList(args);
        CROBS crobs = new CROBS(option(options, "-html"), option(options, "-txt"));
        SwingUtilities.invokeLater(() -> new CrobsGui(crobs).open());
    }

    private static String option(List<String> options, String name) {
        int index = options.indexOf(name);
        return index >= 0 && index + 1 < options.size() ? options.get(index + 1) : null;
    }

    /**
     * Opens the window.
     */
    public void open() {
        frame.setVisible(true);
    }

    /**
     * Runs a command, shows its message in the log and refreshes every tab.
     *
     * @param command command line
     * @return the command's message
     */
    String execute(String command) {
        String message = crobs.processCommand(command);
        log.append("> " + command + "\n" + message + "\n");
        log.setCaretPosition(log.getDocument().getLength());
        refresh();
        if ("quit".equals(command)) {
            frame.dispose();
        }
        return message;
    }

    /**
     * Makes a button that asks for each argument in a dialog, then runs the command.
     *
     * @param label    button text
     * @param command  command name
     * @param selected supplies an ID to put first, or an empty string for none
     * @param prompts  questions for the remaining arguments
     * @return the button
     */
    JButton button(String label, String command, Supplier<String> selected, String... prompts) {
        JButton button = new JButton(label);
        button.addActionListener(e -> {
            StringBuilder line = new StringBuilder(command);
            String id = selected.get();
            if (!id.isEmpty()) {
                line.append(' ').append(id);
            }
            for (String prompt : prompts) {
                String answer = JOptionPane.showInputDialog(frame, prompt);
                if (answer == null) {
                    return;
                }
                line.append(' ').append(answer.trim());
            }
            execute(line.toString());
        });
        return button;
    }

    private CommandTab tab(Supplier<List<Object[]>> rows, String... columns) {
        CommandTab tab = new CommandTab(this, rows, columns);
        tabs.add(tab);
        return tab;
    }

    private void refresh() {
        User user = crobs.getCurrentUser();
        status.setText(user == null ? "Not logged in" : "Logged in as " + user.getUserId()
                + " (" + user.getRole().toCommandToken() + ")");
        for (CommandTab tab : tabs) {
            tab.refresh();
        }
    }

    private List<Object[]> userRows() {
        List<Object[]> rows = new ArrayList<>();
        for (User u : crobs.getUserManager().getAllUsers()) {
            rows.add(new Object[]{u.getUserId(), u.getName(), u.getRole().toCommandToken()});
        }
        return rows;
    }

    private List<Object[]> roomRows() {
        List<Object[]> rows = new ArrayList<>();
        for (Room r : crobs.getRoomManager().getAllRooms()) {
            rows.add(new Object[]{r.getRoomId(), r.getRoomType(), r.getCapacity(), r.getFeatures()});
        }
        return rows;
    }

    private List<Object[]> bookingRows() {
        List<Object[]> rows = new ArrayList<>();
        User user = crobs.getCurrentUser();
        if (user != null) {
            for (Booking b : crobs.getBookingManager().listBookings(user.getUserId())) {
                rows.add(new Object[]{b.getBookingId(), b.getRoomId(), b.getDate(), b.getStartTime(),
                    b.getEndTime(), b.getPurpose()});
            }
        }
        return rows;
    }
}
