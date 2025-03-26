package Authentication;

public class UserStatus {

    /**
     * Check the current status of user
     * @param userId The ID of user who want to check status
     * @return Status of user
     */
    public boolean isUserOnline(int userId){
        User user = UserDatabase.getUserById(userId);
        return (user!=null && user.isOnline());// Search in database or session store to check if user is online
    }

    /**
     * Updates the user's status
     * @param userId The ID of user who want to check status
     * @param status Current status of user
     * @return true if status update was successful
     */
    public boolean updateUserStatus(int userId, boolean status){
        User user = UserDatabase.getUserById(userId);
        if(user == null){   // Check if userId is valid
            return false;
        }

        user.setOnlineStatus(status);   // Update status in database or session store
        return true;
    }
}

