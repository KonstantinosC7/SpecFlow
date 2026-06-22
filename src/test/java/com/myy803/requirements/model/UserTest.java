package com.myy803.requirements.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

/**
 * Unit tests for the User domain class.
 *
 * === WHY TEST THE MODEL? ===
 * Although Lombok generates getters/setters automatically, we still test:
 *   1. That Lombok annotations are applied correctly (no typos in field names).
 *   2. That UserDetails methods (getAuthorities, isEnabled, etc.) return
 *      the correct values — these are used by Spring Security on every request.
 *   3. That the @Builder pattern works as expected.
 *
 * No Spring context, no Mockito — just plain Java object instantiation.
 */
public class UserTest {

    private User user;

    /**
     * Build a fresh User before each test using the Lombok @Builder.
     * This also verifies the builder itself works correctly.
     */
    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1)
                .username("alexdev")
                .password("encoded$pass")
                .firstName("Alex")
                .lastName("Dev")
                .email("alex@test.com")
                .role(Role.USER)
                .build();
    }

    // -------------------------------------------------------------------------
    // Lombok @Getter tests
    // -------------------------------------------------------------------------

    @Test
    void getId_shouldReturnCorrectId() {
        assertEquals(1, user.getId());
    }

    @Test
    void getUsername_shouldReturnCorrectUsername() {
        assertEquals("alexdev", user.getUsername());
    }

    @Test
    void getPassword_shouldReturnCorrectPassword() {
        assertEquals("encoded$pass", user.getPassword());
    }

    @Test
    void getFirstName_shouldReturnCorrectFirstName() {
        assertEquals("Alex", user.getFirstName());
    }

    @Test
    void getLastName_shouldReturnCorrectLastName() {
        assertEquals("Dev", user.getLastName());
    }

    @Test
    void getEmail_shouldReturnCorrectEmail() {
        assertEquals("alex@test.com", user.getEmail());
    }

    @Test
    void getRole_shouldReturnCorrectRole() {
        assertEquals(Role.USER, user.getRole());
    }

    // -------------------------------------------------------------------------
    // Lombok @Setter tests
    // -------------------------------------------------------------------------

    @Test
    void setUsername_shouldUpdateUsername() {
        user.setUsername("newdev");
        assertEquals("newdev", user.getUsername());
    }

    @Test
    void setPassword_shouldUpdatePassword() {
        user.setPassword("newpass");
        assertEquals("newpass", user.getPassword());
    }

    @Test
    void setRole_shouldUpdateRole() {
        user.setRole(Role.ADMIN);
        assertEquals(Role.ADMIN, user.getRole());
    }

    // -------------------------------------------------------------------------
    // UserDetails interface method tests
    // These are called by Spring Security — correctness is critical.
    // -------------------------------------------------------------------------

    /**
     * getAuthorities() must return a single authority whose name equals
     * the role name ("USER" or "ADMIN").
     * Spring Security reads this to enforce URL-level access rules.
     */
    @Test
    void getAuthorities_shouldReturnRoleAsAuthority() {
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertEquals("USER", authorities.iterator().next().getAuthority());
    }

    /**
     * getAuthorities() with ADMIN role should return "ADMIN" authority.
     */
    @Test
    void getAuthorities_shouldReturnAdminAuthority_forAdminRole() {
        user.setRole(Role.ADMIN);
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertEquals("ADMIN", authorities.iterator().next().getAuthority());
    }

    /**
     * isAccountNonExpired, isAccountNonLocked, isCredentialsNonExpired,
     * and isEnabled must all return true — we have no lock/expiry logic.
     */
    @Test
    void userDetailsStatusMethods_shouldAllReturnTrue() {
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }

    // -------------------------------------------------------------------------
    // @NoArgsConstructor test
    // -------------------------------------------------------------------------

    /**
     * JPA requires a no-argument constructor.
     * If @NoArgsConstructor is missing, Hibernate will throw at startup.
     */
    @Test
    void noArgsConstructor_shouldCreateEmptyUser() {
        User emptyUser = new User();
        assertNotNull(emptyUser);
        // id defaults to 0 for int (Java primitive default)
        assertEquals(0, emptyUser.getId());
    }
}
