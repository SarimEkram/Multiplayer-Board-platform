package Authentication;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ViewFriendProfileTest {

    @Test
    void testGetFriendProfileSuccess() {

        // Generating a unique email to ensure user uniqueness
        String email = "friendtest_" + System.currentTimeMillis() + "@example.com";

        // Creating a new user object with test data
        User user = new User(0, "TestFriend", email, "testpass", 0.75, 3, true);

        // Saving the user to the database of the system
        UserDatabase.saveUser(user);

        // Retrieving the user ID after saving using the email
        int userId = UserDatabase.getUserByEmail(email).getUserID();

        // Creating an instance of the ViewFriendProfile class
        ViewFriendProfile viewer = new ViewFriendProfile();

        // Getting the friend's profile as a string using the user ID
        String profile = viewer.getFriendProfile(userId);

        // Checking  the profile contains all expected details
        assertTrue(profile.contains("Username: TestFriend"));   // Checking username
        assertTrue(profile.contains("Email: " + email));        // Checking mail
        assertTrue(profile.contains("Level: 3"));               // Checking the  level
        assertTrue(profile.contains("Win Ratio: 0.75"));        // Checking the win ratio
        assertTrue(profile.contains("Online: Yes"));            // Checking the  online status
    }

    @Test
    void testGetFriendProfileNotFound() {

        // Creating an instance of ViewFriendProfile
        ViewFriendProfile viewer = new ViewFriendProfile();

        // Trying to get a profile for a non-existent user ID
        String result = viewer.getFriendProfile(999999); // Use an unlikely user ID

        // Checking that the returned string matches the expected message, and Assert expected not-found message
        assertEquals("Friend not found.", result);
    }
}