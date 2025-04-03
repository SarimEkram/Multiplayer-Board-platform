package Authentication;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserStatusTest {

    @Test
    void testIsUserOnlineWhenUserExistsAndOnline() {
        // Setup: create and save a user who is online
        String email = "onlinetest_" + System.currentTimeMillis() + "@example.com";
        User user = new User(0, "OnlineTest", email, "testpass", 0.0, 1, true);
        UserDatabase.saveUser(user);

        int userId = UserDatabase.getUserByEmail(email).getUserID();

        UserStatus statusChecker = new UserStatus();
        assertTrue(statusChecker.isUserOnline(userId), "User should be reported as online");
    }

    @Test
    void testIsUserOnlineWhenUserDoesNotExist() {
        UserStatus statusChecker = new UserStatus();
        assertFalse(statusChecker.isUserOnline(999999), "Non-existent user should return false");
    }

    @Test
    void testIsUserOnlineWhenUserExistsButOffline() {
        // Setup: create a user and mark offline
        String email = "offlinetest_" + System.currentTimeMillis() + "@example.com";
        User user = new User(0, "OfflineTest", email, "testpass", 0.0, 1, false);
        UserDatabase.saveUser(user);

        int userId = UserDatabase.getUserByEmail(email).getUserID();

        UserStatus statusChecker = new UserStatus();
        assertFalse(statusChecker.isUserOnline(userId), "User should be offline");
    }

    @Test
    void testUpdateUserStatusSuccess() {
        // Setup: Create user with initial status false
        String email = "statuschange_" + System.currentTimeMillis() + "@example.com";
        User user = new User(0, "StatusChanger", email, "pass123", 0.0, 1, false);
        UserDatabase.saveUser(user);

        int userId = UserDatabase.getUserByEmail(email).getUserID();
        UserStatus statusChanger = new UserStatus();

        // Action: Set to online
        boolean updated = statusChanger.updateUserStatus(userId, true);

        assertTrue(updated, "Update should return true");
        assertTrue(UserDatabase.getUserById(userId).isOnline(), "User should now be online");
    }

    @Test
    void testUpdateUserStatusFailsForNonExistentUser() {
        UserStatus statusChanger = new UserStatus();
        boolean updated = statusChanger.updateUserStatus(123456789, true);
        assertFalse(updated, "Update should fail for non-existent user");
    }
}
