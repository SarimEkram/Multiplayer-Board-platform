package Authentication;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ViewFriendProfileTest {

    @Test
    void testGetFriendProfileSuccess() {

        String email = "friendtest_" + System.currentTimeMillis() + "@example.com";
        User user = new User(0, "TestFriend", email, "testpass", 0.75, 3, true);
        UserDatabase.saveUser(user);

        int userId = UserDatabase.getUserByEmail(email).getUserID();

        ViewFriendProfile viewer = new ViewFriendProfile();
        String profile = viewer.getFriendProfile(userId);


        assertTrue(profile.contains("Username: TestFriend"));
        assertTrue(profile.contains("Email: " + email));
        assertTrue(profile.contains("Level: 3"));
        assertTrue(profile.contains("Win Ratio: 0.75"));
        assertTrue(profile.contains("Online: Yes"));
    }

    @Test
    void testGetFriendProfileNotFound() {
        ViewFriendProfile viewer = new ViewFriendProfile();
        String result = viewer.getFriendProfile(999999); // unlikely to exist
        assertEquals("Friend not found.", result);
    }
}