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
        if (!validateInput(email, password)){
            return false;
        }
        // Check in database, if user exist or not
        if (!userExist(email)){
            return false;
        }
        // Get saved password from database
        String savedPass = storedPassword(email);
        // Compare given and saved password
        if (!verifyPassword(password, savedPass)){
            return false;
        }
        return true;
    }

    /**
     * Validates Input parameters
     * @param email User email that has been entered
     * @param password User password that has been entered
     * @return Validation status of input
     */
    private boolean validateInput(String email, String password){
        // Check if email is in correct format
        if (email == null || !email.contains("@")) {
            return false;
        }
        // Check if password is in correct format
        if (password == null || password.length() < 6) {
            return false;
        }
        return true;
    }

    /**
     * Check if user exist in database
     * @param email User email that has been entered
     * @return User existence status
     */
    private boolean userExist(String email){
        // Search email in database
        if (UserDatabase.getUserByEmail(email) == null) {
            return false;
        }
        // if found return true else return false
        return true;
    }

    /**
     * Get the stored password in hash format from database
     * @param email User email that has been entered
     * @return Password from database
     */
    private String storedPassword(String email){
        // Get password from database where entered email match
        User theUser = UserDatabase.getUserByEmail(email);

        String password = theUser.getPassword();

        return password;
    }

    /**
     * Verifies if entered password matches stored password
     * @param password User password that has been entered
     * @param storedHash The stored hash password
     * @return Password verification status
     */
    private boolean verifyPassword(String password, String storedHash){
        // Convert user password to hash
        String hashedPass = UserRegistration.hashPassword(password);
        // Compare entered hash to saved hash
        if (storedHash.equals(hashedPass)) {
            return true;
        }
        // if same return true, else false
        return false;
    }

    /**
     * Creates a new seesion for user after login
     * @param userID The user's ID
     * @param Session ID
     */
    private String createSession(int userID) {
        String sessionID = UUID.randomUUID().toString(); // create ID for the session



    }
}
