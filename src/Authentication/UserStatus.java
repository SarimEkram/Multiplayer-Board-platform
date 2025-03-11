package Authentication;

public class UserStatus {

    /**
     * Check the current status of user
     * @param userId The ID of user who want to check status
     * @return Status of user
     */
    public boolean isUserOnline(int userId){
        // Check if userId is valid
        // Search in database or session store to check if user is online
        return false;
    }

    /**
     * Updates the user's status
     * @param userId The ID of user who want to check status
     * @param status Current status of user
     * @return true if status update was successful
     */
    public boolean updateUserStatus(int userId, boolean status){
        // Check if userId is valid
        // Update status in database or session store
        return false;
    }

    /**
     * Fetch the user's last seen timestamp
     * @param userId The ID of user who want to check status
     * @return Last seen timestamp, if not found then null
     */
    public String getLastSeen(int userId){
        // Fetch last seen timestamp from database
        return null;
    }
}

