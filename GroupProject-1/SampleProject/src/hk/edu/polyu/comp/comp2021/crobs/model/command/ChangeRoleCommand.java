package hk.edu.polyu.comp.comp2021.crobs.model.command;

import hk.edu.polyu.comp.comp2021.crobs.manager.UserManager;
import hk.edu.polyu.comp.comp2021.crobs.model.Role;
import hk.edu.polyu.comp.comp2021.crobs.model.User;

/**
 * changeRole; undo restores the user's previous role.
 */
public class ChangeRoleCommand implements UndoableCommand {

    private final UserManager userManager;
    private final String targetUserId;
    private final String roleToken;
    private User user;
    private Role oldRole;
    private Role newRole;

    /**
     * @param userManager  user manager
     * @param targetUserId user to change
     * @param roleToken    admin or regular
     */
    public ChangeRoleCommand(UserManager userManager, String targetUserId, String roleToken) {
        this.userManager = userManager;
        this.targetUserId = targetUserId;
        this.roleToken = roleToken;
    }

    @Override
    public String execute() {
        if (userManager.hasUser(targetUserId)) {
            oldRole = userManager.getUser(targetUserId).getRole();
        }
        user = userManager.changeRole(targetUserId, roleToken);
        newRole = user.getRole();
        return "User " + user.getUserId() + " role changed to " + newRole.toCommandToken() + ".";
    }

    @Override
    public void undo() {
        user.setRole(oldRole);
    }

    @Override
    public void redo() {
        user.setRole(newRole);
    }
}
