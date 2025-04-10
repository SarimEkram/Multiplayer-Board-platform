package Authentication;

import org.junit.jupiter.api.*;

import java.io.File;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FriendDatabaseTest {

    private static final int USER_1 = 1001;
    private static final int USER_2 = 1002;
    private static final int USER_3 = 1003;

    @BeforeEach
    void resetFile() {
        // Clear and re-initialize the file before each test
        FriendDatabase.deleteCSVFile();
        FriendDatabase.addFriend(USER_1, USER_2); // mutual friendship
        FriendDatabase.addFriend(USER_1, USER_3); // mutual friendship
    }

    @Test
    void testAddFriend() {
        int newUser = 2000;
        assertTrue(FriendDatabase.addFriend(USER_1, newUser));
        assertTrue(FriendDatabase.areFriends(USER_1, newUser));
        assertTrue(FriendDatabase.areFriends(newUser, USER_1));
    }

    @Test
    void testRemoveFriend() {
        assertTrue(FriendDatabase.removeFriend(USER_1, USER_2));
        assertFalse(FriendDatabase.areFriends(USER_1, USER_2));
        assertFalse(FriendDatabase.areFriends(USER_2, USER_1));
    }

    @Test
    void testGetFriends() {
        Set<Integer> friends = FriendDatabase.getFriends(USER_1);
        assertEquals(2, friends.size());
        assertTrue(friends.contains(USER_2));
        assertTrue(friends.contains(USER_3));
    }

    @Test
    void testAreFriends() {
        assertTrue(FriendDatabase.areFriends(USER_1, USER_2));
        assertTrue(FriendDatabase.areFriends(USER_2, USER_1));
        assertFalse(FriendDatabase.areFriends(USER_2, USER_3));
    }

    @Test
    void testAddSelfAsFriendFails() {
        assertFalse(FriendDatabase.addFriend(USER_1, USER_1));
    }

    @Test
    void testDeleteCSVFile() {
        assertTrue(FriendDatabase.deleteCSVFile());
        File file = new File("friends.csv");
        assertFalse(file.exists() || file.isFile());
    }
}
