package Authentication;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateUserProfileTest {

    @AfterAll
    static void deleteDatabase() {
        boolean deleted = UserDatabase.deleteCSVFile();
    }

    @Test
    void testUpdateValidUsernameAndEmail() {
        // Create and save test user
        User user = new User(0, "oldName", "old@example.com", "pass123", 0.7, 2, false);
        UserDatabase.saveUser(user);
        int id= user.getUserID();


        UpdateUserProfile updater = new UpdateUserProfile();
        boolean result = updater.updateUser(id, "newName", "new@example.com");


        assertTrue(result, "User should be updated successfully");

        User updated = UserDatabase.getUserById(id);

        assertEquals("newName", updated.getUsername());
        assertEquals("new@example.com", updated.getEmail());
    }

    @Test
    void testUpdateOnlyUsername() {
        User user = new User(0, "startName", "fixed@example.com", "abc123", 0.8, 1, true);
        UserDatabase.saveUser(user);
        int userId = user.getUserID();

        UpdateUserProfile updater = new UpdateUserProfile();
        boolean result = updater.updateUser(userId, "onlyName", null);

        assertTrue(result);
        User updated = UserDatabase.getUserById(userId);
        assertEquals("onlyName", updated.getUsername());
        assertEquals("fixed@example.com", updated.getEmail());
    }

    @Test
    void testUpdateOnlyValidEmail() {
        User user = new User(0, "unchangedName", "start@example.com", "abc123", 0.9, 3, false);
        UserDatabase.saveUser(user);
        int userId = user.getUserID();

        UpdateUserProfile updater = new UpdateUserProfile();
        boolean result = updater.updateUser(userId, null, "updated@example.com");

        assertTrue(result);
        User updated = UserDatabase.getUserById(userId);
        assertEquals("unchangedName", updated.getUsername());
        assertEquals("updated@example.com", updated.getEmail());
    }

    @Test
    void testUpdateFailsWithInvalidUserID() {
        UpdateUserProfile updater = new UpdateUserProfile();
        boolean result = updater.updateUser(999999, "newUser", "new@email.com");

        assertFalse(result, "Update should fail for non-existent user");
    }

    @Test
    void testUpdateSkipsInvalidEmail() {
        User user = new User(0, "validName", "valid@email.com", "secure", 1.0, 5, false);
        UserDatabase.saveUser(user);
        int userId = user.getUserID();

        UpdateUserProfile updater = new UpdateUserProfile();
        boolean result = updater.updateUser(userId, "stillValid", "not-an-email");

        assertTrue(result);
        User updated = UserDatabase.getUserById(userId);
        assertEquals("stillValid", updated.getUsername());
        assertEquals("valid@email.com", updated.getEmail(), "Email should remain unchanged due to invalid format");
    }
}
