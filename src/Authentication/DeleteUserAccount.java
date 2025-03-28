package Authentication;

public class DeleteUserAccount {

    /**
     * Deletes user's account from database
     * @param userId User's registered ID
     * @return Status for deletion of account
     */
    public boolean deleteAccount(int userId){
        // Check if user ID is valid
        if (userId <= 0) {
            return false;
        }
        // Verify existence of user in database
        if (!userExist(userId)) {
            return false;
        }
        // Remove user's data from database
        if (!UserDatabase.deleteUser(userId)) {
            return false;
        }
        // Return true if account is successfully deleted
        return deletionLog(userId);
    }

    /**
     * Check if user exist in system
     * @param userId User's registered ID
     * @return Status of user existence
     */
    private boolean userExist( int userId){
        // Search for user in database
        User user = UserDatabase.getUserById(userId);
        return user != null;
    }

    /**
     * Logs the account deletion for security purpose
     * @param userId User's registered ID
     * @return Status for logging data
     */
    private boolean deletionLog(int userId){
        // Record deletion event for security reason
        System.out.println("UserID: " + userId + " has been removed from the database.");
        return true;
    }
}
