package hk.edu.polyu.comp.comp2021.crobs.manager;

import hk.edu.polyu.comp.comp2021.crobs.model.Role;
import hk.edu.polyu.comp.comp2021.crobs.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Member A — user registry, role control, and login session state (REQ2–REQ6).
 */
public class UserManager {
    public static final String SUPERADMIN_ID = "u0";
    public static final String SUPERADMIN_NAME = "superadmin";

    private final Map<String, User> users;
    private User currentUser;
    private int nextUserNumber;

    public UserManager() {
        this.users = new HashMap<String, User>();
        this.currentUser = null;
        this.nextUserNumber = 1;
        bootstrapSuperAdmin();
    }

    private void bootstrapSuperAdmin() {
        User superAdmin = new User(SUPERADMIN_ID, SUPERADMIN_NAME, Role.SUPERADMIN);
        users.put(SUPERADMIN_ID, superAdmin);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public User getUser(String userId) {
        return users.get(userId);
    }

    public boolean hasUser(String userId) {
        return users.containsKey(userId);
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    /**
     * REQ2 — admin creates a regular user.
     *
     * @return the newly created user
     * @throws IllegalStateException when caller lacks privilege or is not logged in
     */
    public User addUser(String name) {
        requireAdmin();
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Error: Invalid arguments.");
        }
        String userId = "u" + nextUserNumber;
        nextUserNumber++;
        User user = new User(userId, name, Role.REGULAR);
        users.put(userId, user);
        return user;
    }

    /**
     * REQ3 — admin removes a regular user.
     *
     * @param targetUserId user to remove
     * @return the removed user
     */
    public User removeUser(String targetUserId) {
        requireAdmin();
        User target = users.get(targetUserId);
        if (target == null) {
            throw new IllegalArgumentException("Error: User not found.");
        }
        if (!target.isRegular()) {
            throw new IllegalStateException("Error: Cannot remove this user.");
        }
        users.remove(targetUserId);
        if (currentUser != null && targetUserId.equals(currentUser.getUserId())) {
            currentUser = null;
        }
        return target;
    }

    /**
     * REQ4 — superadmin changes another user's role to admin or regular.
     */
    public User changeRole(String targetUserId, String newRoleToken) {
        requireSuperAdmin();
        User target = users.get(targetUserId);
        if (target == null) {
            throw new IllegalArgumentException("Error: User not found.");
        }
        if (target.isSuperAdmin()) {
            throw new IllegalStateException("Error: Cannot change the role of the superadmin.");
        }
        Role newRole = Role.fromCommandToken(newRoleToken);
        if (newRole == null) {
            throw new IllegalArgumentException("Error: Invalid role.");
        }
        target.setRole(newRole);
        return target;
    }

    /**
     * REQ5 — log in as an existing user.
     */
    public User login(String userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("Error: User not found.");
        }
        currentUser = user;
        return user;
    }

    /**
     * REQ6 — log out the current user.
     */
    public void logout() {
        if (currentUser == null) {
            throw new IllegalStateException("Error: No user is logged in.");
        }
        currentUser = null;
    }

    public void requireLoggedIn() {
        if (currentUser == null) {
            throw new IllegalStateException("Error: No user is logged in.");
        }
    }

    public void requireAdmin() {
        requireLoggedIn();
        if (!currentUser.isAdmin()) {
            throw new IllegalStateException("Error: Access denied.");
        }
    }

    public void requireSuperAdmin() {
        requireLoggedIn();
        if (!currentUser.isSuperAdmin()) {
            throw new IllegalStateException("Error: Access denied.");
        }
    }
}
