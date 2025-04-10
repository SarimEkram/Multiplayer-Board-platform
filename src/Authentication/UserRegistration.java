package Authentication;

import MatchmakingLeaderboard.Player;
import MatchmakingLeaderboard.PlayerDatabase;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class UserRegistration {

    /**
     * Registers a new user in the system.
     *
     * @param username The chosen username.
     * @param email    The user's email address.
     * @param password The plain text password that will be hashed.
     * @return An empty string if registration is successful;
     *         otherwise, a string of error messages (each error separated by a newline).
     */
    public List<String> registerUser(String username, String email, String password) {
        List<String> errors = validateInput(username, email, password);

        // Check if username or email is already used
        if (UserDatabase.getUserByEmail(email) != null) {
            errors.add("A user with this email already exists.");
        }
        if (UserDatabase.getUserByUsername(username) != null) {
            errors.add("A user with this username already exists.");
        }

        // Generate a unique userID.
        int userID = UserDatabase.generateUniqueUserID();
        if (userID <= 0 || UserDatabase.getUserById(userID) != null) {
            errors.add("Failed to generate a unique user ID.");
        }

        if (!errors.isEmpty()) {
            return errors;
        }

        // Hash the password.
        String hashed = hashPassword(password);
        if (hashed == null) {
            errors.add("Password hashing failed.");
            return errors;
        }

        // Create a new user object.
        User newUser = new User(userID, username, email, hashed, 0.0, 0, false);
        boolean success = UserDatabase.saveUser(newUser);
        if (!success) {
            errors.add("Failed to save the user to the database.");
            return errors;
        }

        // Create the player's record.
        Player player = new Player(newUser.getUsername(), 0, newUser.getUserID());
        PlayerDatabase.savePlayer(player);

        return errors;  // if registration was successful, errors will be empty.
    }

    private List<String> validateInput(String username, String email, String password) {
        List<String> errors = new ArrayList<>();

        // Check username is non-null and not just blank.
        if (username == null || username.trim().isEmpty()) {
            errors.add("Username is required.");
        }

        // Check that email is non-null, not blank, and contains "@".
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            errors.add("A valid email address is required.");
        }

        // Check that password is non-null and at least 6 characters long.
        if (password == null || password.trim().isEmpty()) {
            errors.add("Password is required.");
        } else if (password.length() < 6) {
            errors.add("Password must be at least 6 characters.");
        }

        return errors;
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
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}