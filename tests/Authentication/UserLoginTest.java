package Authentication;

import org.junit.jupiter.api.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UserLoginTest {

    private static final String TEST_EMAIL = "testlogin@example.com";
    private static final String TEST_PASSWORD = "secure123";
    private static final String HASHED_PASSWORD = UserLogin.hashPassword(TEST_PASSWORD);
    private static final String TEST_USERNAME = "loginUser";

    private static User testUser;

    @BeforeAll
    static void setup() {
        testUser = new User(0, TEST_USERNAME, TEST_EMAIL, HASHED_PASSWORD, 0.0, 1, false);
        UserDatabase.saveUser(testUser);
    }

    @Test
    void testLoginSuccess() {
        int login = UserLogin.loginUser(TEST_EMAIL, TEST_PASSWORD);
        assertTrue(login>0);

        User user = UserDatabase.getUserByEmail(TEST_EMAIL);
        assertNotNull(user);
        assertTrue(user.isOnline(), "User should be marked as online");

        assertTrue(UserLogin.sessionData.containsKey(user.getUserID()), "Session data should be created");
        assertTrue(UserLogin.authTokens.containsKey(user.getUserID()), "Auth token should be created");
    }

    @Test
    void testLoginFailsWrongPassword() {
        int login = UserLogin.loginUser(TEST_EMAIL, "wrongpass");
        assertEquals(login,-1);
    }

    @Test
    void testLoginFailsInvalidEmailFormat() {
        int login = UserLogin.loginUser("invalid-email", TEST_PASSWORD);
        assertEquals(login,-1);
    }

    @Test
    void testLoginFailsForNonExistingUser() {
        int login = UserLogin.loginUser("doesnotexist@example.com", "anything123");
        assertEquals(login,-1);
    }

    @Test
    void testPasswordHashingConsistency() {
        String hash1 = UserLogin.hashPassword("mypassword");
        String hash2 = UserLogin.hashPassword("mypassword");
        assertEquals(hash1, hash2, "Same input should always produce the same hash");
    }

    @Test
    void testSessionAndTokenExpiryAreSet() {
        int userID = UserDatabase.getUserByEmail(TEST_EMAIL).getUserID();

        UserLogin.loginUser(TEST_EMAIL, TEST_PASSWORD);

        ResetTokenData session = UserLogin.sessionData.get(userID);
        ResetTokenData token = UserLogin.authTokens.get(userID);

        assertNotNull(session, "Session should be set");
        assertNotNull(token, "Token should be set");
        assertTrue(session.getExpiry().isAfter(token.getExpiry().minusMinutes(31)), "Session should expire before token");
    }
}

