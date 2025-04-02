package Authentication;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserRegistrationTest {

    private static final String VALID_USERNAME = "newuser01";
    private static final String VALID_EMAIL = "newuser01@example.com";
    private static final String VALID_PASSWORD = "securePassword123";

    private static final String DUPLICATE_USERNAME = "duplicateUser";
    private static final String DUPLICATE_EMAIL = "duplicate@example.com";

    @BeforeEach
    void setup() {
        // Ensure duplicate test user exists
        User existing = new User(
                UserDatabase.generateUniqueUserID(),
                DUPLICATE_USERNAME,
                DUPLICATE_EMAIL,
                "dummyHash", 0.0, 1, false);
        UserDatabase.saveUser(existing);
    }

    @Test
    void testRegisterUserSuccessfully() {
        UserRegistration reg = new UserRegistration();

        String uniqueUsername = "testuser_" + System.currentTimeMillis();
        String uniqueEmail = "user" + System.currentTimeMillis() + "@example.com";
        String password = "securepass";

        boolean result = reg.registerUser(uniqueUsername, uniqueEmail, password);
        assertTrue(result, "Registration should succeed with valid input");

        User saved = UserDatabase.getUserByEmail(uniqueEmail);
        assertNotNull(saved, "User should be saved in the database");
        assertEquals(uniqueUsername, saved.getUsername());
    }

    @Test
    void testDuplicateEmailRegistration() {
        UserRegistration reg = new UserRegistration();

        boolean result = reg.registerUser("anotherUser", DUPLICATE_EMAIL, "password123");
        assertFalse(result, "Should fail due to duplicate email");
    }

    @Test
    void testDuplicateUsernameRegistration() {
        UserRegistration reg = new UserRegistration();

        boolean result = reg.registerUser(DUPLICATE_USERNAME, "newemail@abc.com", "password123");
        assertFalse(result, "Should fail due to duplicate username");
    }

    @Test
    void testInvalidEmailRegistration() {
        UserRegistration reg = new UserRegistration();

        boolean result = reg.registerUser("user", "invalidemail.com", "password123");
        assertFalse(result, "Should fail due to invalid email");
    }

    @Test
    void testWeakPasswordRegistration() {
        UserRegistration reg = new UserRegistration();

        boolean result = reg.registerUser("user", "user@example.com", "123");
        assertFalse(result, "Should fail due to short password");
    }

    @Test
    void testEmptyUsernameRegistration() {
        UserRegistration reg = new UserRegistration();

        boolean result = reg.registerUser("", "user@example.com", "password123");
        assertFalse(result, "Should fail due to empty username");
    }

    @Test
    void testNullInputRegistration() {
        UserRegistration reg = new UserRegistration();

        assertFalse(reg.registerUser(null, VALID_EMAIL, VALID_PASSWORD));
        assertFalse(reg.registerUser(VALID_USERNAME, null, VALID_PASSWORD));
        assertFalse(reg.registerUser(VALID_USERNAME, VALID_EMAIL, null));
    }
}
