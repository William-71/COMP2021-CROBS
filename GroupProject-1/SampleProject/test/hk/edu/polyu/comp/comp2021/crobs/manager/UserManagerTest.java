package hk.edu.polyu.comp.comp2021.crobs.manager;

import hk.edu.polyu.comp.comp2021.crobs.model.Role;
import hk.edu.polyu.comp.comp2021.crobs.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserManagerTest {

    private UserManager um;

    @BeforeEach
    void setUp() {
        um = new UserManager();
    }

    @Test
    void bootstrapsSuperAdminAndNoLogin() {
        assertNull(um.getCurrentUser());
        assertFalse(um.isLoggedIn());
        User sa = um.getUser(UserManager.SUPERADMIN_ID);
        assertNotNull(sa);
        assertTrue(sa.isSuperAdmin());
        assertEquals(UserManager.SUPERADMIN_NAME, sa.getName());
    }

    @Test
    void addUserRequiresAdmin() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                um.addUser("Alice");
            }
        });
        assertEquals("Error: No user is logged in.", ex.getMessage());

        um.login(UserManager.SUPERADMIN_ID);
        User u1 = um.addUser("Alice");
        assertEquals("u1", u1.getUserId());
        assertEquals(Role.REGULAR, u1.getRole());
    }

    @Test
    void regularUserCannotAddUser() {
        um.login(UserManager.SUPERADMIN_ID);
        um.addUser("Alice");
        um.logout();
        um.login("u1");
        IllegalStateException ex = assertThrows(IllegalStateException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                um.addUser("Bob");
            }
        });
        assertEquals("Error: Access denied.", ex.getMessage());
    }

    @Test
    void removeUserOnlyRegularAndCancelsSessionIfSelf() {
        um.login(UserManager.SUPERADMIN_ID);
        um.addUser("Alice");
        um.addUser("Bob");

        IllegalStateException cannotRemoveSa = assertThrows(IllegalStateException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                um.removeUser(UserManager.SUPERADMIN_ID);
            }
        });
        assertEquals("Error: Cannot remove this user.", cannotRemoveSa.getMessage());

        um.changeRole("u2", "admin");
        IllegalStateException cannotRemoveAdmin = assertThrows(IllegalStateException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                um.removeUser("u2");
            }
        });
        assertEquals("Error: Cannot remove this user.", cannotRemoveAdmin.getMessage());

        um.removeUser("u1");
        assertFalse(um.hasUser("u1"));
    }

    @Test
    void removeUserNotFound() {
        um.login(UserManager.SUPERADMIN_ID);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                um.removeUser("u99");
            }
        });
        assertEquals("Error: User not found.", ex.getMessage());
    }

    @Test
    void changeRoleSuperAdminOnly() {
        um.login(UserManager.SUPERADMIN_ID);
        um.addUser("Alice");
        um.changeRole("u1", "admin");
        assertEquals(Role.ADMIN, um.getUser("u1").getRole());
        um.changeRole("u1", "regular");
        assertEquals(Role.REGULAR, um.getUser("u1").getRole());

        um.logout();
        um.login("u1");
        IllegalStateException denied = assertThrows(IllegalStateException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                um.changeRole("u1", "admin");
            }
        });
        assertEquals("Error: Access denied.", denied.getMessage());
    }

    @Test
    void changeRoleRejectsInvalidAndSuperAdminTarget() {
        um.login(UserManager.SUPERADMIN_ID);
        um.addUser("Alice");

        IllegalStateException sa = assertThrows(IllegalStateException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                um.changeRole(UserManager.SUPERADMIN_ID, "admin");
            }
        });
        assertEquals("Error: Cannot change the role of the superadmin.", sa.getMessage());

        IllegalArgumentException badRole = assertThrows(IllegalArgumentException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                um.changeRole("u1", "superadmin");
            }
        });
        assertEquals("Error: Invalid role.", badRole.getMessage());

        IllegalArgumentException missing = assertThrows(IllegalArgumentException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                um.changeRole("u99", "admin");
            }
        });
        assertEquals("Error: User not found.", missing.getMessage());
    }

    @Test
    void loginAndLogout() {
        IllegalArgumentException missing = assertThrows(IllegalArgumentException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                um.login("u99");
            }
        });
        assertEquals("Error: User not found.", missing.getMessage());

        um.login(UserManager.SUPERADMIN_ID);
        assertTrue(um.isLoggedIn());
        assertEquals(UserManager.SUPERADMIN_ID, um.getCurrentUser().getUserId());

        um.logout();
        assertNull(um.getCurrentUser());

        IllegalStateException noUser = assertThrows(IllegalStateException.class, new org.junit.jupiter.api.function.Executable() {
            @Override
            public void execute() {
                um.logout();
            }
        });
        assertEquals("Error: No user is logged in.", noUser.getMessage());
    }
}
