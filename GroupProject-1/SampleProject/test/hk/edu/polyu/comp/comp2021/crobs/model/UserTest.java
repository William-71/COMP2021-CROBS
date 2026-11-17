package hk.edu.polyu.comp.comp2021.crobs.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    @Test
    void gettersAndRoleMutation() {
        User user = new User("u1", "Alice", Role.REGULAR);
        assertEquals("u1", user.getUserId());
        assertEquals("Alice", user.getName());
        assertEquals(Role.REGULAR, user.getRole());
        assertTrue(user.isRegular());
        assertFalse(user.isAdmin());
        assertFalse(user.isSuperAdmin());

        user.setRole(Role.ADMIN);
        assertTrue(user.isAdmin());
        assertFalse(user.isRegular());
    }
}
