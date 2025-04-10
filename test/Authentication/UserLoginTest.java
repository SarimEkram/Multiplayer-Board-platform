package Authentication;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

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
    
    @Test
    void testShortPassword() {
        int result = UserLogin.loginUser(VALID_USERNAME, "short");
        assertEquals(-1, result, "Should return -1 for password shorter than 6 characters");
    }
    
    @Test
    void testHashPassword() {
        String password = "testPassword";
        String hashedPassword = UserLogin.hashPassword(password);
        
        assertNotNull(hashedPassword, "Hashed password should not be null");
        assertNotEquals(password, hashedPassword, "Hashed password should be different from original");
        assertEquals(hashedPassword, UserLogin.hashPassword(password), "Same password should produce same hash");
    }
    
    @Test
    void testConsecutiveLogins() {
        // First login
        int firstResult = UserLogin.loginUser(VALID_USERNAME, VALID_PASSWORD);
        assertEquals(userId, firstResult, "First login should succeed");
        
        String firstSessionId = UserLogin.sessionData.get(userId).getToken();
        String firstAuthToken = UserLogin.authTokens.get(userId).getToken();
        
        // Second login
        int secondResult = UserLogin.loginUser(VALID_USERNAME, VALID_PASSWORD);
        assertEquals(userId, secondResult, "Second login should succeed");
        
        String secondSessionId = UserLogin.sessionData.get(userId).getToken();
        String secondAuthToken = UserLogin.authTokens.get(userId).getToken();
        
        assertNotEquals(firstSessionId, secondSessionId, "Session ID should be regenerated on new login");
        assertNotEquals(firstAuthToken, secondAuthToken, "Auth token should be regenerated on new login");
    }
    
    @Test
    void testSessionDataCreation() {
        UserLogin.loginUser(VALID_USERNAME, VALID_PASSWORD);
        
        ResetTokenData sessionData = UserLogin.sessionData.get(userId);
        assertNotNull(sessionData, "Session data should be created");
        assertEquals(userId, sessionData.getUserId(), "Session should be associated with correct user");
        assertNotNull(sessionData.getToken(), "Session should have a token");
        assertNotNull(sessionData.getExpiryTime(), "Session should have expiry time");
    }
    
    @Test
    void testAuthTokenCreation() {
        UserLogin.loginUser(VALID_USERNAME, VALID_PASSWORD);
        
        ResetTokenData authToken = UserLogin.authTokens.get(userId);
        assertNotNull(authToken, "Auth token should be created");
        assertEquals(userId, authToken.getUserId(), "Auth token should be associated with correct user");
        assertNotNull(authToken.getToken(), "Auth token should have a token value");
        assertNotNull(authToken.getExpiryTime(), "Auth token should have expiry time");
    }
    
    @Test
    void testSessionExpiryTime() {
        UserLogin.loginUser(VALID_USERNAME, VALID_PASSWORD);
        
        LocalDateTime expiry = UserLogin.sessionData.get(userId).getExpiryTime();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expectedExpiry = now.plusMinutes(60);
        
        // Allow 5 seconds tolerance for test execution time
        assertTrue(expiry.isAfter(expectedExpiry.minusSeconds(5)) && 
                   expiry.isBefore(expectedExpiry.plusSeconds(5)),
                  "Session should expire in approximately 60 minutes");
    }
    
    @Test
    void testAuthTokenExpiryTime() {
        UserLogin.loginUser(VALID_USERNAME, VALID_PASSWORD);
        
        LocalDateTime expiry = UserLogin.authTokens.get(userId).getExpiryTime();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expectedExpiry = now.plusMinutes(90);
        
        // Allow 5 seconds tolerance for test execution time
        assertTrue(expiry.isAfter(expectedExpiry.minusSeconds(5)) && 
                   expiry.isBefore(expectedExpiry.plusSeconds(5)),
                  "Auth token should expire in approximately 90 minutes");
    }
    
    @Test
    void testUserWithBadEmailFormat() {
        // Create a user with an email that doesn't contain @
        String badEmail = "bademailformat";
        User badUser = new User(0, "badEmailUser", badEmail, UserLogin.hashPassword(VALID_PASSWORD), 0.0, 1, false);
        UserDatabase.saveUser(badUser);
        int badUserId = badUser.getUserID();
        
        try {
            int result = UserLogin.loginUser("badEmailUser", VALID_PASSWORD);
            assertEquals(-1, result, "Should return -1 for invalid email format");
        } finally {
            // Clean up the test user
            UserDatabase.deleteUser(badUserId);
        }
    }

    @AfterEach
    void cleanup() {
        UserDatabase.deleteUser(userId);
        UserLogin.sessionData.clear();
        UserLogin.authTokens.clear();
    }
}
