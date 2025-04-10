package Authentication;
/**
 * Provides utility methods to check and update the online status of users.
 */
public class UserStatus {

    /**
     * Checks whether a specific user is currently online.
     *
     * @param userId The ID of the user to check.
     * @return true if the user exists and is online, false otherwise.
     */
    public boolean isUserOnline(int userId){
        User user = UserDatabase.getUserById(userId);
        return (user!=null && user.isOnline());  // Check user exists and online status
    }

    /**
     * Updates the online status of a specific user.
     *
     * @param userId The ID of the user to update.
     * @param status The new status to set (true for online, false for offline).
     * @return true if the user exists and status is updated; false otherwise.
     */
    public boolean updateUserStatus(int userId, boolean status){
        User user = UserDatabase.getUserById(userId);
        if(user == null){   // Return false if user doesn't exist
            return false;
        }

        user.setOnlineStatus(status);   // Update the user's online status
        return true;
    }
}

