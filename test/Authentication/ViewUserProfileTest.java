package Authentication;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ViewUserProfileTest {

    @Test
    void testGetUserProfileSuccess() {

        String email = "myprofile_" + System.currentTimeMillis() + "@example.com";


        User user = new User(0, "MyProfileUser", email, "securepass", 0.85, 10, true);
        UserDatabase.saveUser(user);
        int userId = UserDatabase.getUserByEmail(email).getUserID();


        ViewUserProfile viewer = new ViewUserProfile();
        String profile = viewer.getUserProfile(userId);


        assertTrue(profile.contains("Your Profile:"));
        assertTrue(profile.contains("Username: MyProfileUser"));
        assertTrue(profile.contains("Email: " + email));
        assertTrue(profile.contains("Level: 10"));
        assertTrue(profile.contains("Win Ratio: 0.85"));
        assertTrue(profile.contains("Online: Yes"));
    }

    @Test
    void testGetUserProfileNotFound() {
        ViewUserProfile viewer = new ViewUserProfile();
        String result = viewer.getUserProfile(999999); // ID not likely to exist
        assertEquals("User not found.", result);
    }
}

