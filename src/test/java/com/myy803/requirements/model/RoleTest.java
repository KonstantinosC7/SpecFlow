package com.myy803.requirements.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Role enum.
 *
 * Although Role is a small enum, testing it is worthwhile because:
 *   1. Spring Security reads Role.name() to build GrantedAuthority objects.
 *      If the string value changes (e.g. "USER" becomes "ROLE_USER"),
 *      security rules in WebSecurityConfig would silently break.
 *   2. The @Enumerated(EnumType.STRING) annotation in User persists the
 *      enum by its name — so the name strings must stay stable.
 *
 * These tests act as a "guard" that fails immediately if someone
 * accidentally renames an enum constant.
 */
public class RoleTest {

    /**
     * Role.USER must exist and its name() must be "USER".
     * WebSecurityConfig uses .hasAnyAuthority("USER") — these must match.
     */
    @Test
    void roleUser_shouldExistWithCorrectName() {
        Role role = Role.USER;
        assertNotNull(role);
        assertEquals("USER", role.name());
    }

    /**
     * Role.ADMIN must exist and its name() must be "ADMIN".
     */
    @Test
    void roleAdmin_shouldExistWithCorrectName() {
        Role role = Role.ADMIN;
        assertNotNull(role);
        assertEquals("ADMIN", role.name());
    }

    /**
     * The enum must contain exactly two values.
     * If a new role is added without updating security rules,
     * this test fails and forces the developer to review the
     * security configuration.
     */
    @Test
    void roleShouldHaveExactlyTwoValues() {
        assertEquals(2, Role.values().length,
                "Role enum must have exactly USER and ADMIN. " +
                "If you add a new role, update WebSecurityConfig too.");
    }

    /**
     * Role.valueOf("USER") must return Role.USER.
     * This is the operation JPA performs when loading a User from the DB:
     * it calls Role.valueOf(columnValue) to reconstruct the enum.
     */
    @Test
    void valueOf_shouldReturnCorrectConstant() {
        assertEquals(Role.USER,  Role.valueOf("USER"));
        assertEquals(Role.ADMIN, Role.valueOf("ADMIN"));
    }

    /**
     * The ordinal of USER must be 0 and ADMIN must be 1.
     * We store by STRING (not ordinal) but it is still good to
     * document the declaration order in case anyone switches to
     * EnumType.ORDINAL accidentally.
     */
    @Test
    void ordinalShouldReflectDeclarationOrder() {
        assertEquals(0, Role.USER.ordinal());
        assertEquals(1, Role.ADMIN.ordinal());
    }
}
