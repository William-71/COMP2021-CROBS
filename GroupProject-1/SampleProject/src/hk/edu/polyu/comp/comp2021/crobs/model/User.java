package hk.edu.polyu.comp.comp2021.crobs.model;

/**
 * A Crobs user with a unique ID, display name, and role.
 */
public class User {
    private final String userId;
    private final String name;
    private Role role;

    /**
     * @param userId user ID
     * @param name   name
     * @param role   role
     */
    public User(String userId, String name, Role role) {
        this.userId = userId;
        this.name = name;
        this.role = role;
    }

    /**
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the role
     */
    public Role getRole() {
        return role;
    }

    /**
     * @param role new role
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * @return true if admin or superadmin
     */
    public boolean isAdmin() {
        return role.isAdmin();
    }

    /**
     * @return true if superadmin
     */
    public boolean isSuperAdmin() {
        return role.isSuperAdmin();
    }

    /**
     * @return true if regular
     */
    public boolean isRegular() {
        return role == Role.REGULAR;
    }
}
