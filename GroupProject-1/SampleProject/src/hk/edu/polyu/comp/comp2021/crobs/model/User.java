package hk.edu.polyu.comp.comp2021.crobs.model;

/**
 * A Crobs user with a unique ID, display name, and role.
 */
public class User {
    private final String userId;
    private final String name;
    private Role role;

    public User(String userId, String name, Role role) {
        this.userId = userId;
        this.name = name;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return role.isAdmin();
    }

    public boolean isSuperAdmin() {
        return role.isSuperAdmin();
    }

    public boolean isRegular() {
        return role == Role.REGULAR;
    }
}
