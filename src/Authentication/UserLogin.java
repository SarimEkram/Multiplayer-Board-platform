package Authentication;

public class UserLogin {
    /**
     * Verifies the user and return the login status
     * @param email User email that has been entered
     * @param password User password that has been entered
     * @return Status of login
     */
    public boolean loginUser(String email, String password){
        // Check if email and password are not null
        // Check in database, if user exist or not
        // Get saved password from database
        // Compare given and saved password
        // if same return true, else false
        return false;
    }

    /**
     * Validates Input parameters
     * @param email User email that has been entered
     * @param password User password that has been entered
     * @return Validation status of input
     */
    private boolean validateInput(String email, String password){
        // Check if email is in correct format
        // Check if password is in correct format
        return false;
    }

    /**
     * Check if user exist in database
     * @param email User email that has been entered
     * @return User existence status
     */
    private boolean userExist(String email){
        // Search email in database
        // if found return true else return false
        return false;
    }

    /**
     * Get the stored password in hash format from database
     * @param email User email that has been entered
     * @return Password from database
     */
    private String storedPassword(String email){
        // Get password from database where entered email match
        return null;
    }

    /**
     * Verifies if entered password matches stored password
     * @param password User password that has been entered
     * @param storedHash The stored hash password
     * @return Password verification status
     */
    private boolean verifyPassword(String password, String storedHash){
        // Convert user password to hash
        // Compare entered hash to saved hash
        // if same return true, else false
        return false;
    }
}
