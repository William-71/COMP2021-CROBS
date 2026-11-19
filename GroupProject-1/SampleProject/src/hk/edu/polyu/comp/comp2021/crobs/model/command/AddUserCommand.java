package hk.edu.polyu.comp.comp2021.crobs.model.command;

import hk.edu.polyu.comp.comp2021.crobs.manager.UserManager;
import hk.edu.polyu.comp.comp2021.crobs.model.User;

/**
 * addUser; undo removes the user.
 */
public class AddUserCommand implements UndoableCommand {

    private final UserManager userManager;
    private final String name;
    private User user;

    /**
     * @param userManager user manager
     * @param name        user's name
     */
    public AddUserCommand(UserManager userManager, String name) {
        this.userManager = userManager;
        this.name = name;
    }

    @Override
    public String execute() {
        user = userManager.addUser(name);
        return "User " + user.getUserId() + " created.";
    }

    @Override
    public void undo() {
        userManager.discardUser(user.getUserId());
    }

    @Override
    public void redo() {
        userManager.restoreUser(user);
    }
}
