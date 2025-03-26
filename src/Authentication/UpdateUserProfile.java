package Authentication;

public class UpdateUserProfile {

    /**
     * Updates user profile details.
     *
     * @param userId The ID of the user whose profile is being updated
     * @param username The new username (optional)
     * @param email The new email address (optional)
     * @return true if profile update is successful, false otherwise
     */
    public boolean updateUser(int userId, String username, String email) {
        // Check if userId is valid
        // Validate new username and email
        // Save changes in database
        // Return true if update successful, else false
        return false;
    }

    /**
     * Checks if the new profile details are valid.
     *
     * @param username The new username (optional)
     * @param email The new email address (optional)
     * @return true if details are valid, false otherwise
     */
    private boolean checkDetails(String username, String email) {
        // Check if username follows correct format
        // Check if email is in valid format
        return false;
    }

    /**
     * Saves the updated user details in the database.
     *
     * @param userId The ID of the user
     * @param username The new username (optional)
     * @param email The new email address (optional)
     * @return true if database update is successful, false otherwise
     */
    private boolean saveUser(int userId, String username, String email) {
        // Save username and email in the database
        return false;
    }
}