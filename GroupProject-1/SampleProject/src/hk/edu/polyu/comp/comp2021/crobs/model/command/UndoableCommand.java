package hk.edu.polyu.comp.comp2021.crobs.model.command;

/**
 * A command that can be undone and redone (BON2).
 */
public interface UndoableCommand {

    /**
     * @return the success message
     */
    String execute();

    /**
     * Reverses the effect of {@link #execute()}.
     */
    void undo();

    /**
     * Re-applies the command after {@link #undo()}, keeping the same IDs.
     */
    void redo();
}
