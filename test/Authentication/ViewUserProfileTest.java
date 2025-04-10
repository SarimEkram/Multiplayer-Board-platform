package Authentication;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ViewUserProfileTest {

    @Test
    void testGetUserProfileSuccess() {

        // Creating a unique email using current timestamp to avoid collisions
        String email = "myprofile_" + System.currentTimeMillis() + "@example.com";

        // Creating a new user with specific details
        User user = new User(0, "MyProfileUser", email, "securepass", 0.85, 10, true);

        // Saving  the user into the UserDatabase of the system
        UserDatabase.saveUser(user);

        // Retrieving the user by email to get the assigned userID
        int userId = UserDatabase.getUserByEmail(email).getUserID();

        // Creating the ViewUserProfile instance to call the method under test
        ViewUserProfile viewer = new ViewUserProfile();

        // Get the profile string for the user
        String profile = viewer.getUserProfile(userId);

        // Asserting  the profile contains expected pieces of information
        assertTrue(profile.contains("Your Profile:"));             // Checking profile
        assertTrue(profile.contains("Username: MyProfileUser"));   // Checking username
        assertTrue(profile.contains("Email: " + email));           // Checking email
        assertTrue(profile.contains("Level: 10"));                 // Checking the  level
        assertTrue(profile.contains("Win Ratio: 0.85"));           // Checking the  win ratio
        assertTrue(profile.contains("Online: Yes"));               // Checking the online status
    }

    @Test
    void testGetUserProfileNotFound() {

        // Creating an instance of the class under test
        ViewUserProfile viewer = new ViewUserProfile();

        // Attempting to retrieve the profile for a user ID that likely doesn't exist
        String result = viewer.getUserProfile(999999);       // Non-existent user ID

        // Assert that the method returns "User not found."
        assertEquals("User not found.", result);
    }
}


