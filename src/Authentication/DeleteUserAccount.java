package Authentication;

import java.util.HashSet;

public class DeleteUserAccount {

    /**
     * Deletes user's account from database
     * @param userId User's registered ID
     * @return Status for deletion of account
     */
    public static boolean deleteAccount(int userId){
        // Check if user ID is valid
        if (userId <= 0) {
            return false;
        }
        // Verify existence of user in database
        if (!userExist(userId)) {
            return false;
        }

        //log user deletion
        if (!deletionLog(userId)) {
            return false;
        }

        // Remove user's data from database
        if (!UserDatabase.deleteUser(userId)) {
            deletedUsers.remove(UserDatabase.getUserById(userId));
            return false;
        }
        // Return true if account is successfully deleted
        return (!(userExist(userId) && deletedUsers.contains(UserDatabase.getUserById(userId))));
    }

    /**
     * Check if user exist in system
     * @param userId User's registered ID
     * @return Status of user existence
     */
    private static boolean userExist( int userId){
        // Search for user in database
        User user = UserDatabase.getUserById(userId);
        return user != null;
    }

    //for logging deletion
    public static HashSet<User> deletedUsers = new HashSet<>();

    /**
     * Logs the account deletion for security purpose
     * @param userId User's registered ID
     * @return Status for logging data
     */
    private static boolean deletionLog(int userId){
        // Record deletion event for security reason
        deletedUsers.add(UserDatabase.getUserById(userId));
        return deletedUsers.contains(UserDatabase.getUserById(userId));
    }
}
