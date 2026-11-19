package hk.edu.polyu.comp.comp2021.crobs.model;

/**
 * User access roles in Crobs.
 */
public enum Role {
    /**
     * Superadmin.
     */
    SUPERADMIN,
    /**
     * Admin.
     */
    ADMIN,
    /**
     * Regular user.
     */
    REGULAR;

    /**
     * Parses a role token used by the {@code changeRole} command.
     * Only {@code admin} and {@code regular} are assignable via CLI.
     *
     * @param token role string from the command line
     * @return the corresponding role, or {@code null} if invalid
     */
    public static Role fromCommandToken(String token) {
        if (token == null) {
            return null;
        }
        if ("admin".equals(token)) {
            return ADMIN;
        }
        if ("regular".equals(token)) {
            return REGULAR;
        }
        return null;
    }

    /**
     * @return CLI-facing role name
     */
    public String toCommandToken() {
        switch (this) {
            case SUPERADMIN:
                return "superadmin";
            case ADMIN:
                return "admin";
            case REGULAR:
                return "regular";
            default:
                return name().toLowerCase();
        }
    }

    /**
     * @return true if admin or superadmin
     */
    public boolean isAdmin() {
        return this == ADMIN || this == SUPERADMIN;
    }

    /**
     * @return true if superadmin
     */
    public boolean isSuperAdmin() {
        return this == SUPERADMIN;
    }
}
