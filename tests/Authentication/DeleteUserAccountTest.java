package Authentication;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DeleteUserAccount class
 * Verifies account deletion logic for various edge and normal cases
 */
public class DeleteUserAccountTest {

    private static final String TEST_EMAIL = "deleteuser@example.com";
    private static final String TEST_USERNAME = "deleteuser";
    private static final String TEST_PASSWORD = "testpass";

    private int createdUserId;

    @BeforeEach
    void setUp() {
        // Create a user to delete
        User user = new User(0, TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, 0.3, 2, false);
        UserDatabase.saveUser(user);
        User retrieved = UserDatabase.getUserByEmail(TEST_EMAIL);
        assertNotNull(retrieved);
        createdUserId = retrieved.getUserID();
    }

    @AfterEach
    void tearDown() {
        // Ensure cleanup in case test failed
        UserDatabase.deleteUser(createdUserId);
    }

    @Test
    void testDeleteValidUser() {
        boolean result = DeleteUserAccount.deleteAccount(createdUserId);
        assertTrue(result, "Valid user should be deleted successfully");
        assertNull(UserDatabase.getUserById(createdUserId), "User should no longer exist in the database");
        assertTrue(DeleteUserAccount.deletedUsers.stream().anyMatch(u -> u.getUserID() == createdUserId), "Deleted user should be logged");
    }

    @Test
    void testDeleteNonExistentUser() {
        int fakeId = 999999;
        assertNull(UserDatabase.getUserById(fakeId), "No user should exist with this ID");
        boolean result = DeleteUserAccount.deleteAccount(fakeId);
        assertFalse(result, "Deletion should fail for non-existent user");
    }


    @Test
    void testLogDeletedUserManually() {
        User user = UserDatabase.getUserById(createdUserId);
        assertNotNull(user);
        DeleteUserAccount.deletedUsers.add(user);
        assertTrue(DeleteUserAccount.deletedUsers.contains(user), "User should be present in deleted log after manual insert");
    }
}
