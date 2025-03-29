package Authentication;

public class UserStatus {

    // Checks if the specified user is currently online
    // userId - The ID of the user to check
    // Returns true if user exists and is online, false otherwise
    public boolean isUserOnline(int userId){
        User user = UserDatabase.getUserById(userId);
        return (user!=null && user.isOnline());  // Check user exists and online status
    }

    // Updates the online status of the specified user
    // userId - The ID of the user to update
    // status - The new online status (true=online, false=offline)
    // Returns true if update was successful, false if user not found
    public boolean updateUserStatus(int userId, boolean status){
        User user = UserDatabase.getUserById(userId);
        if(user == null){   // Return false if user doesn't exist
            return false;
        }

        user.setOnlineStatus(status);   // Update the user's online status
        return true;
    }
}

