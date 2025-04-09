package Authentication;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserLoginTest {

    private static final String VALID_USERNAME = "loginTester";
    private static final String VALID_EMAIL = "loginTester@example.com";
    private static final String VALID_PASSWORD = "mySecurePass";

    private static int userId;

    @BeforeEach
    void setup() {
        // Register a user in the database
        User user = new User(0, VALID_USERNAME, VALID_EMAIL, UserLogin.hashPassword(VALID_PASSWORD), 0.0, 1, false);
        UserDatabase.saveUser(user);
        userId = user.getUserID();
    }

    @Test
    void testLoginSuccess() {
        int result = UserLogin.loginUser(VALID_USERNAME, VALID_PASSWORD);
        assertEquals(userId, result, "Login should return userID for valid credentials");

        User user = UserDatabase.getUserById(result);
        assertTrue(user.isOnline(), "User should be marked online after login");
        assertTrue(UserLogin.sessionData.containsKey(userId), "Session data should be created");
        assertTrue(UserLogin.authTokens.containsKey(userId), "Auth token should be created");
    }

    @Test
    void testInvalidUsernameFormat() {
        int result = UserLogin.loginUser("!invalid_email_format", VALID_PASSWORD);
        assertEquals(-2, result, "Should return -2 for user not found with invalid username");
    }

    @Test
    void testUserNotFound() {
        int result = UserLogin.loginUser("nonexistentuser", VALID_PASSWORD);
        assertEquals(-2, result, "Should return -2 for non-existent user");
    }

    @Test
    void testWrongPassword() {
        int result = UserLogin.loginUser(VALID_USERNAME, "wrongPassword123");
        assertEquals(-3, result, "Should return -3 for wrong password");
    }

    @Test
    void testEmptyPassword() {
        int result = UserLogin.loginUser(VALID_USERNAME, "");
        assertEquals(-1, result, "Should return -1 for invalid password input");
    }

    @Test
    void testNullPassword() {
        int result = UserLogin.loginUser(VALID_USERNAME, null);
        assertEquals(-1, result, "Should return -1 for null password input");
    }

    @AfterEach
    void cleanup() {
        UserDatabase.deleteUser(userId);
        UserLogin.sessionData.clear();
        UserLogin.authTokens.clear();
    }
}
