package Authentication;

public class UserRegistration {

    /**
     * Registers a new user in the system.
     *
     * @param username user chooses a username
     * @param email The user's email address
     * @param password The plain text password that would be hashed
     * @return true if registration is successful, false otherwise
     */
    public boolean registerUser(String username, String email, String password) {
        // Check if username, email, and password are not empty
        // Make sure email looks correct
        // Check if username or email is already used in database
        // Change password to hashed password before saving
        // Save user info in database
        // Return true if successful, false otherwise
        return false;
    }

    /**
     * Checks if user input is valid.
     *
     * @param username The chosen username
     * @param email The user's email address
     * @param password The password chosen by the user
     * @return true if input is valid, false if something wrong
     */
    private boolean validateInput(String username, String email, String password) {
        // Check if username, email, password are not empty
        // Check if email is written properly
        // Check if password is strong enough
        return false;
    }

    /**
     * Checks if username or email is already taken.
     *
     * @param username The chosen username
     * @param email The user's email address
     * @return true if username or email is already used, false if not
     */
    private boolean isUserExists(String username, String email) {
        // Look in database for username or email
        // If found return true, else return false
        return false;
    }

    /**
     * Hashes the user's password before saving.
     *
     * @param password The plain text password
     * @return The hashed password
     */
    private String hashPassword(String password) {
        // Change the password to hashed format
        return null;
    }

    /**
     * Saves user details in the database.
     *
     * @param username The chosen username
     * @param email The user's email address
     * @param hashedPassword The hashed version of the password
     * @return true if user is saved successfully, false if error
     */
    private boolean saveUserToDatabase(String username, String email, String hashedPassword) {
        // Save user info (username, email, hashed password) in database
        return false;
    }
}