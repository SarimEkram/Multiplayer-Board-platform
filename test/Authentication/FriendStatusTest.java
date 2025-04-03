package Authentication;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the FriendStatus utility
 */
public class FriendStatusTest {

    private static final String TEST_USERNAME = "friendUser01";
    private static final String TEST_EMAIL = "friendUser01@example.com";
    private static final String TEST_PASSWORD = "password";
    private static int testUserId;

    @BeforeAll
    static void setUpTestUser() {
        User user = new User(0, TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, 0.5, 3, true);
        UserDatabase.saveUser(user);
        User saved = UserDatabase.getUserByEmail(TEST_EMAIL);
        assertNotNull(saved);
        testUserId = saved.getUserID();
    }

    @AfterAll
    static void cleanup() {
        UserDatabase.deleteUser(testUserId);
    }

    @Test
    void testIsFriendOnlineWhenOnline() {
        FriendStatus status = new FriendStatus();
        boolean result = status.isFriendOnline(testUserId);
        assertTrue(result, "Friend should be online");
    }

    @Test
    void testIsFriendOnlineWhenOffline() {
        User friend = UserDatabase.getUserById(testUserId);
        friend.setOnlineStatus(false);

        FriendStatus status = new FriendStatus();
        boolean result = status.isFriendOnline(testUserId);
        assertFalse(result, "Friend should be offline");
    }

    @Test
    void testIsFriendOnlineWhenUserNotFound() {
        FriendStatus status = new FriendStatus();
        boolean result = status.isFriendOnline(999999); // assuming this ID doesn’t exist
        assertFalse(result, "Should return false if user not found");
    }

    @Test
    void testGetFriendStatusOnline() {
        UserDatabase.getUserById(testUserId).setOnlineStatus(true);
        FriendStatus status = new FriendStatus();
        String msg = status.getFriendStatus(testUserId);
        assertEquals("Friend is online", msg);
    }

    @Test
    void testGetFriendStatusOffline() {
        UserDatabase.getUserById(testUserId).setOnlineStatus(false);
        FriendStatus status = new FriendStatus();
        String msg = status.getFriendStatus(testUserId);
        assertEquals("Friend is not online", msg);
    }

    @Test
    void testGetFriendStatusNotFound() {
        FriendStatus status = new FriendStatus();
        String msg = status.getFriendStatus(123456);
        assertEquals("Friend not found", msg);
    }
}
