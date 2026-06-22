package com.myy803.requirements.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.myy803.requirements.dao.UserMapper;
import com.myy803.requirements.model.Role;
import com.myy803.requirements.model.User;

/**
 * Unit tests for UserServiceImpl.
 *
 * === WHAT IS A UNIT TEST? ===
 * A unit test tests ONE class in isolation.
 * All dependencies (UserMapper, BCryptPasswordEncoder) are replaced
 * with MOCKS — fake objects that we control in the test.
 * This way we test only the logic inside UserServiceImpl, not the DB.
 *
 * === MOCKITO ANNOTATIONS ===
 * @ExtendWith(MockitoExtension.class)
 *   Activates Mockito for this test class.
 *
 * @Mock
 *   Creates a fake (mock) object. We define what it returns with when().
 *
 * @InjectMocks
 *   Creates the real UserServiceImpl and injects the @Mock fields into it.
 *   This replaces Spring's @Autowired during tests.
 *
 * === TEST ANATOMY ===
 * Each test method follows the AAA pattern:
 *   Arrange — set up data and mock behaviour
 *   Act     — call the method under test
 *   Assert  — verify the result
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    // A reusable User object built fresh before each test
    private User testUser;

    /**
     * @BeforeEach runs before EVERY test method.
     * We reset testUser here so tests don't affect each other.
     */
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("alexdev");
        testUser.setPassword("plaintext123");
        testUser.setFirstName("Alex");
        testUser.setLastName("Dev");
        testUser.setEmail("alex@test.com");
        testUser.setRole(Role.USER);
    }

    // -------------------------------------------------------------------------
    // saveUser tests
    // -------------------------------------------------------------------------

    /**
     * US1 — saveUser should BCrypt-encode the password and set role to USER.
     *
     * when(bCryptPasswordEncoder.encode("plaintext123")).thenReturn("encoded$hash")
     * means: whenever encode() is called with that argument, return "encoded$hash".
     *
     * verify(userMapper).save(testUser)
     * means: assert that save() was called on the mock exactly once with testUser.
     */
    @Test
    void saveUser_shouldEncodePasswordAndSetUserRole() {
        // Arrange
        when(bCryptPasswordEncoder.encode("plaintext123")).thenReturn("encoded$hash");

        // Act
        userService.saveUser(testUser);

        // Assert — password was encoded
        assertEquals("encoded$hash", testUser.getPassword());
        // Assert — role was set to USER
        assertEquals(Role.USER, testUser.getRole());
        // Assert — the mapper was called to persist the user
        verify(userMapper).save(testUser);
    }

    // -------------------------------------------------------------------------
    // isUserPresent tests
    // -------------------------------------------------------------------------

    /**
     * US1 — isUserPresent should return true when the username exists in the DB.
     */
    @Test
    void isUserPresent_shouldReturnTrue_whenUsernameExists() {
        // Arrange: the mapper finds the user
        when(userMapper.findByUsername("alexdev")).thenReturn(Optional.of(testUser));

        // Act
        boolean result = userService.isUserPresent(testUser);

        // Assert
        assertTrue(result);
    }

    /**
     * US1 — isUserPresent should return false when the username does not exist.
     */
    @Test
    void isUserPresent_shouldReturnFalse_whenUsernameDoesNotExist() {
        // Arrange: the mapper finds nothing
        when(userMapper.findByUsername("alexdev")).thenReturn(Optional.empty());

        // Act
        boolean result = userService.isUserPresent(testUser);

        // Assert
        assertFalse(result);
    }

    // -------------------------------------------------------------------------
    // findByUsername tests
    // -------------------------------------------------------------------------

    /**
     * US2 — findByUsername should return the User when found.
     */
    @Test
    void findByUsername_shouldReturnUser_whenFound() {
        // Arrange
        when(userMapper.findByUsername("alexdev")).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.findByUsername("alexdev");

        // Assert
        assertEquals("alexdev", result.getUsername());
        assertEquals("Alex", result.getFirstName());
    }

    /**
     * US2 — findByUsername should throw UsernameNotFoundException when user does not exist.
     *
     * assertThrows(ExceptionClass, lambda) passes if the lambda throws that exception.
     * If the exception is NOT thrown, the test fails.
     */
    @Test
    void findByUsername_shouldThrowException_whenNotFound() {
        // Arrange: the mapper returns empty
        when(userMapper.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act + Assert — the call must throw UsernameNotFoundException
        assertThrows(UsernameNotFoundException.class,
                () -> userService.findByUsername("unknown"));
    }

    // -------------------------------------------------------------------------
    // updateUser tests
    // -------------------------------------------------------------------------

    /**
     * US2 — updateUser should re-encode the password and call save().
     */
    @Test
    void updateUser_shouldEncodePasswordAndSave() {
        // Arrange
        when(bCryptPasswordEncoder.encode("plaintext123")).thenReturn("newEncoded$hash");

        // Act
        userService.updateUser(testUser);

        // Assert
        assertEquals("newEncoded$hash", testUser.getPassword());
        verify(userMapper).save(testUser);
    }

    // -------------------------------------------------------------------------
    // loadUserByUsername tests (Spring Security)
    // -------------------------------------------------------------------------

    /**
     * Spring Security calls loadUserByUsername on every login.
     * It should return the User (which implements UserDetails) when found.
     */
    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenFound() {
        // Arrange
        when(userMapper.findByUsername("alexdev")).thenReturn(Optional.of(testUser));

        // Act
        var result = userService.loadUserByUsername("alexdev");

        // Assert — the returned object is our user
        assertEquals("alexdev", result.getUsername());
    }

    /**
     * loadUserByUsername should throw UsernameNotFoundException for unknown users.
     * Spring Security catches this and converts it to an authentication failure.
     */
    @Test
    void loadUserByUsername_shouldThrowException_whenNotFound() {
        // Arrange
        when(userMapper.findByUsername("ghost")).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("ghost"));
    }
}
