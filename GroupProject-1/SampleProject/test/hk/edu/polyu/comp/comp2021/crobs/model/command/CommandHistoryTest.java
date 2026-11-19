package hk.edu.polyu.comp.comp2021.crobs.model.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommandHistoryTest {

    private final List<String> calls = new ArrayList<String>();

    private UndoableCommand recording(final String name) {
        return new UndoableCommand() {
            @Override
            public String execute() {
                calls.add("execute " + name);
                return name + " done";
            }

            @Override
            public void undo() {
                calls.add("undo " + name);
            }

            @Override
            public void redo() {
                calls.add("redo " + name);
            }
        };
    }

    private void assertError(String message, Executable action) {
        assertEquals(message, assertThrows(IllegalStateException.class, action).getMessage());
    }

    @Test
    void emptyHistoryHasNothingToUndoOrRedo() {
        final CommandHistory history = new CommandHistory();
        assertError("Error: Nothing to undo.", new Executable() {
            @Override
            public void execute() {
                history.undo();
            }
        });
        assertError("Error: Nothing to redo.", new Executable() {
            @Override
            public void execute() {
                history.redo();
            }
        });
    }

    @Test
    void undoAndRedoFollowStackOrder() {
        CommandHistory history = new CommandHistory();
        assertEquals("a done", history.run(recording("a")));
        assertEquals("b done", history.run(recording("b")));
        history.undo();
        history.undo();
        history.redo();
        history.redo();
        assertEquals("[execute a, execute b, undo b, undo a, redo a, redo b]", calls.toString());
    }

    @Test
    void newCommandClearsRedo() {
        final CommandHistory history = new CommandHistory();
        history.run(recording("a"));
        history.undo();
        history.run(recording("b"));
        assertError("Error: Nothing to redo.", new Executable() {
            @Override
            public void execute() {
                history.redo();
            }
        });
    }

    @Test
    void failedCommandIsNotRecorded() {
        final CommandHistory history = new CommandHistory();
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                history.run(new UndoableCommand() {
                    @Override
                    public String execute() {
                        throw new IllegalArgumentException("Error: Invalid arguments.");
                    }

                    @Override
                    public void undo() {
                        calls.add("undo");
                    }

                    @Override
                    public void redo() {
                        calls.add("redo");
                    }
                });
            }
        });
        assertError("Error: Nothing to undo.", new Executable() {
            @Override
            public void execute() {
                history.undo();
            }
        });
    }

    @Test
    void clearForgetsBothStacks() {
        final CommandHistory history = new CommandHistory();
        history.run(recording("a"));
        history.run(recording("b"));
        history.undo();
        history.clear();
        assertError("Error: Nothing to undo.", new Executable() {
            @Override
            public void execute() {
                history.undo();
            }
        });
        assertError("Error: Nothing to redo.", new Executable() {
            @Override
            public void execute() {
                history.redo();
            }
        });
    }
}
