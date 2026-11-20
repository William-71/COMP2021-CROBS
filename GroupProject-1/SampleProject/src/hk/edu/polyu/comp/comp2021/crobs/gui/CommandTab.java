package hk.edu.polyu.comp.comp2021.crobs.gui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.List;
import java.util.function.Supplier;

/**
 * A tab with a table of records and a row of command buttons.
 */
class CommandTab extends JPanel {

    private final CrobsGui gui;
    private final Supplier<List<Object[]>> rows;
    private final DefaultTableModel model;
    private final JTable table;
    private final JPanel buttons = new JPanel();

    /**
     * @param gui     main window that runs commands
     * @param rows    supplies the table rows
     * @param columns table column headings
     */
    CommandTab(CrobsGui gui, Supplier<List<Object[]>> rows, String... columns) {
        super(new BorderLayout());
        this.gui = gui;
        this.rows = rows;
        this.model = new DefaultTableModel(columns, 0);
        this.table = new JTable(model);
        table.setDefaultEditor(Object.class, null);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    /**
     * Adds a button for a command whose first argument is the selected row's ID.
     *
     * @param label   button text
     * @param command command name
     * @param prompts questions for the remaining arguments
     * @return this tab
     */
    CommandTab onSelected(String label, String command, String... prompts) {
        buttons.add(gui.button(label, command, this::selectedId, prompts));
        return this;
    }

    /**
     * Adds a button for a command that does not use the selected row.
     *
     * @param label   button text
     * @param command command name
     * @param prompts questions for the arguments
     * @return this tab
     */
    CommandTab action(String label, String command, String... prompts) {
        buttons.add(gui.button(label, command, () -> "", prompts));
        return this;
    }

    private String selectedId() {
        int row = table.getSelectedRow();
        return row < 0 ? "" : String.valueOf(table.getValueAt(row, 0));
    }

    /**
     * Reloads the table.
     */
    void refresh() {
        model.setRowCount(0);
        for (Object[] row : rows.get()) {
            model.addRow(row);
        }
    }
}
