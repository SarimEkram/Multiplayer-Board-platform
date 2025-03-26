package Authentication;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

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
        if (!validateInput(username, email, password)){
            return false;
        }

        // Check if username or email is already used in database
        if(UserDatabase.getUserByEmail(email) != null){
            return false;
        }
        if(UserDatabase.getUserByUsername(username) != null){
            return false;
        }

        // Change password to hashed password before saving
        String hashed = hashPassword(password);

        // Create a new user object
        User newUser = new User(0, username, email, hashed, 0.0, 1, false);

        //Save user info in database
        return UserDatabase.saveUser(newUser);
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
        // Check if password is of minimum length
        if(username == null || username.isEmpty()){
            return false;
        }
        if(email == null || !email.contains("@")){
            return false;
        }
        if(password == null || password.length() < 6){
            return false;
        }
        return true;
    }


    /**
     * Hashes the user's password before saving.
     *
     * @param password The plain text password
     * @return The hashed password
     */
    private String hashPassword(String password) {
        // Change the password to hashed format
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashed);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}