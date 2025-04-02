package Authentication;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for UserDatabase:
 * Includes saving, retrieving, deleting, and generating user IDs.
 */
public class UserDatabaseTest {

    private static final String TEST_USERNAME = "testuser01";
    private static final String TEST_EMAIL = "testuser01@example.com";
    private static final String TEST_PASSWORD = "hashedpassword";

    @Test
    void testSaveAndRetrieveUser() {
        User user = new User(0, TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, 0.6, 5, true);
        boolean saved = UserDatabase.saveUser(user);
        assertTrue(saved, "User should be saved");

        User retrieved = UserDatabase.getUserByEmail(TEST_EMAIL);
        assertNotNull(retrieved);
        assertEquals(TEST_USERNAME, retrieved.getUsername());
        assertEquals(TEST_PASSWORD, retrieved.getPassword());
        assertEquals(0.6, retrieved.getWinRatio(), 0.001);
        assertEquals(5, retrieved.getLevel());
        assertTrue(retrieved.isOnline());
    }

    @Test
    void testUpdateUser() {
        User user = new User(1, "updateuser", "update@example.com", "pass123", 0.4, 1, false);
        UserDatabase.saveUser(user);

        user.setLevel(10);
        user.setOnlineStatus(true);

        User updated = UserDatabase.getUserByEmail("update@example.com");
        assertNotNull(updated);
        assertEquals(10, updated.getLevel());
        assertTrue(updated.isOnline());
    }

    @Test
    void testDeleteUser() {
        User user = new User(0, "deleteuser", "delete@example.com", "delete123", 0.9, 2, true);
        UserDatabase.saveUser(user);

        boolean deleted = UserDatabase.deleteUser(user.getUserID());
        assertTrue(deleted, "User should be deleted");

        User shouldBeNull = UserDatabase.getUserById(user.getUserID());
        assertNull(shouldBeNull, "Deleted user should not be retrievable");
    }

    @Test
    void testGetUserById() {
        User user = new User(0, "iduser", "id@example.com", "idpass", 0.5, 4, false);
        UserDatabase.saveUser(user);

        User found = UserDatabase.getUserById(user.getUserID());
        assertNotNull(found);
        assertEquals("iduser", found.getUsername());
    }

    @Test
    void testGetUserByUsername() {
        User user = new User(0, "lookupuser", "lookup@example.com", "look123", 0.3, 6, true);
        UserDatabase.saveUser(user);

        User found = UserDatabase.getUserByUsername("lookupuser");
        assertNotNull(found);
        assertEquals("lookup@example.com", found.getEmail());
    }

    @Test
    void testGenerateUniqueUserID() {
        int id1 = UserDatabase.generateUniqueUserID();
        int id2 = UserDatabase.generateUniqueUserID();

        assertNotEquals(id1, id2, "User IDs should be unique");
        assertTrue(id1 >= 100000 && id1 <= 999999);
        assertTrue(id2 >= 100000 && id2 <= 999999);
    }

    @Test
    void testAutoAssignIDIfZero() {
        User user = new User(0, "autoiduser", "autoid@example.com", "auto123", 0.45, 7, false);
        UserDatabase.saveUser(user);

        assertTrue(user.getUserID() >= 100000 && user.getUserID() <= 999999, "Auto-assigned ID should be valid");

        User found = UserDatabase.getUserById(user.getUserID());
        assertNotNull(found);
        assertEquals("autoiduser", found.getUsername());
    }
}
