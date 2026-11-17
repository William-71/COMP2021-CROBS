package hk.edu.polyu.comp.comp2021.crobs.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoleTest {

    @Test
    void fromCommandTokenAcceptsAdminAndRegular() {
        assertEquals(Role.ADMIN, Role.fromCommandToken("admin"));
        assertEquals(Role.REGULAR, Role.fromCommandToken("regular"));
    }

    @Test
    void fromCommandTokenRejectsInvalid() {
        assertNull(Role.fromCommandToken("superadmin"));
        assertNull(Role.fromCommandToken("ADMIN"));
        assertNull(Role.fromCommandToken(null));
        assertNull(Role.fromCommandToken(""));
    }

    @Test
    void toCommandTokenAndFlags() {
        assertEquals("superadmin", Role.SUPERADMIN.toCommandToken());
        assertEquals("admin", Role.ADMIN.toCommandToken());
        assertEquals("regular", Role.REGULAR.toCommandToken());
        assertTrue(Role.SUPERADMIN.isAdmin());
        assertTrue(Role.ADMIN.isAdmin());
        assertFalse(Role.REGULAR.isAdmin());
        assertTrue(Role.SUPERADMIN.isSuperAdmin());
        assertFalse(Role.ADMIN.isSuperAdmin());
    }
}
