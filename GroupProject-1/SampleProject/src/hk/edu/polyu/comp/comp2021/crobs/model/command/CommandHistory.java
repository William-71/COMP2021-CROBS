package hk.edu.polyu.comp.comp2021.crobs.model.command;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Undo and redo stacks of executed commands (BON2).
 */
public class CommandHistory {

    private static final String ERR_NOTHING_TO_UNDO = "Error: Nothing to undo.";
    private static final String ERR_NOTHING_TO_REDO = "Error: Nothing to redo.";

    private final Deque<UndoableCommand> undoStack = new ArrayDeque<UndoableCommand>();
    private final Deque<UndoableCommand> redoStack = new ArrayDeque<UndoableCommand>();

    /**
     * Executes a command and records it.
     *
     * @param command command to run
     * @return the command's success message
     */
    public String run(UndoableCommand command) {
        String message = command.execute();
        undoStack.push(command);
        redoStack.clear();
        return message;
    }

    /**
     * Undoes the most recent command.
     */
    public void undo() {
        if (undoStack.isEmpty()) {
            throw new IllegalStateException(ERR_NOTHING_TO_UNDO);
        }
        UndoableCommand command = undoStack.pop();
        command.undo();
        redoStack.push(command);
    }

    /**
     * Redoes the most recently undone command.
     */
    public void redo() {
        if (redoStack.isEmpty()) {
            throw new IllegalStateException(ERR_NOTHING_TO_REDO);
        }
        UndoableCommand command = redoStack.pop();
        command.redo();
        undoStack.push(command);
    }

    /**
     * Forgets all recorded commands.
     */
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
}
