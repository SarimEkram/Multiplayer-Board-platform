package Authentication;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserLogoutTest {

    private static final String TEST_EMAIL = "logouttest@example.com";
    private static final String TEST_PASSWORD = "logoutpass123";
    private static final String HASHED_PASSWORD = UserLogin.hashPassword(TEST_PASSWORD);
    private static final String TEST_USERNAME = "logoutUser";

    private static User testUser;

    @BeforeEach
    void setup() {
        testUser = new User(0, TEST_USERNAME, TEST_EMAIL, HASHED_PASSWORD, 0.0, 1, true);
        UserDatabase.saveUser(testUser);
        UserLogin.loginUser(TEST_EMAIL, TEST_PASSWORD); // Log in before testing logout
    }

    @Test
    void testSuccessfulLogout() {
        int userId = UserDatabase.getUserByEmail(TEST_EMAIL).getUserID();

        boolean logout = UserLogout.logoutUser(userId);
        assertTrue(logout, "Logout should succeed");

        // User should be marked offline
        assertFalse(UserDatabase.getUserById(userId).isOnline(), "User should be marked offline");

        // Session and token should be removed
        assertFalse(UserLogin.sessionData.containsKey(userId), "Session should be cleared");
        assertFalse(UserLogin.authTokens.containsKey(userId), "Auth token should be cleared");
    }

    @Test
    void testLogoutWithInvalidUserId() {
        assertFalse(UserLogout.logoutUser(-1), "Logout should fail for invalid user ID");
        assertFalse(UserLogout.logoutUser(0), "Logout should fail for zero user ID");
    }

    @Test
    void testLogoutWithoutSession() {
        int userId = UserDatabase.getUserByEmail(TEST_EMAIL).getUserID();

        // Manually clear session/token
        UserLogin.sessionData.remove(userId);
        UserLogin.authTokens.remove(userId);

        boolean logout = UserLogout.logoutUser(userId);
        assertTrue(logout, "Logout should still succeed even if session/token already cleared");

        assertFalse(UserDatabase.getUserById(userId).isOnline(), "User should be offline");
    }
}
