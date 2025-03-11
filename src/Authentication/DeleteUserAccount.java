package Authentication;

public class DeleteUserAccount {

    /**
     * Deletes user's account from database
     * @param userId User's registered ID
     * @return Status for deletion of account
     */
    public boolean deleteAccount(int userId){
        // Check if user ID is valid
        // Verify existence of user in database
        // Remove user's data from database
        // Return true if account is successfully deleted
        return false;
    }

    /**
     * Check if user exist in system
     * @param userId User's registered ID
     * @return Status of user existence
     */
    private boolean userExist( int userId){
        // Search for user in database
        return false;
    }

    /**
     * Remove user data from system
     * @param userId User's registered ID
     * @return Status for successful deletion
     */
    private boolean removeUserdata(int userId){
        // Delete user details from database
        return false;
    }

    /**
     * Logs the account deletion for security purpose
     * @param userId User's registered ID
     * @return Status for logging data
     */
    private boolean deletionLog(int userId){
        // Record deletion event for security reason
        return false;
    }
}
