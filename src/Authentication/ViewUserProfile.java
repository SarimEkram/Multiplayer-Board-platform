package Authentication;

public class ViewUserProfile {
    /**
     * Fetch the user's profile details
     * @param userId The ID of user who want to see profile
     * @return User object with profile data
     */
    public User getUserProfile(int userId){
        // Check if userId is valid
        // Fetch details of the user from database
        // Return User object with profile data
        return null;
    }

    /**
     * Check if user ID exists in data
     * @param userId The ID of user who want to see profile
     * @return Status for existence of ID
     */
    private boolean userExists(int userId){
        // Search in database if ID exists or not
        return false;
    }

    /**
     * Fetch the profile data from database
     * @param userId The ID of user who want to see profile
     * @return User object with profile details
     */
    private User fetchProfile(int userId){
        // Query the database to get user profile details
        return null;
    }
}
