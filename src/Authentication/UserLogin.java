package Authentication;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;

public class UserLogin {

    /**
     * Verifies the user and return the login status
     * @param userid Userid that has been entered
     * @param password User password that has been entered
     * @return Status of login
     */
    public static int loginUser(String userid, String password){
        User user1 = UserDatabase.getUserByUsername(userid);
        String email = user1.getEmail();
        // Check if email and password are not null
        if (!validateInput(email, password)){
            return -1;
        }
        // Check in database, if user exist or not
        if (!userExist(email)){
            return -1;
        }
        // Get saved password from database
        String savedPass = storedPassword(email);
        if (!verifyPassword(password, savedPass)){
            return -1;
        }

        // login is successful
        User user = UserDatabase.getUserByEmail(email); // get user to update status and what not

        int userID = user.getUserID();

        user.setOnlineStatus(true); // set the user as online

        createSession(userID);

        createAuthToken(userID);

        return userID;
    }

    /**
     * Validates Input parameters
     * @param email User email that has been entered
     * @param password User password that has been entered
     * @return Validation status of input
     */
    private static boolean validateInput(String email, String password){
        // Check if email is in correct format
        if (email == null || !email.contains("@")) {
            return false;
        }
        // Check if password is in correct format
        return password != null && password.length() >= 6;
    }

    /**
     * Check if user exist in database
     * @param email User email that has been entered
     * @return User existence status
     */
    private static boolean userExist(String email){
        // Search email in database
        return UserDatabase.getUserByEmail(email) != null;
        // if found return true else return false
    }

    /**
     * Get the stored password in hash format from database
     * @param email User email that has been entered
     * @return Password from database
     */
    private static String storedPassword(String email){
        // Get password from database where entered email match
        User theUser = UserDatabase.getUserByEmail(email);

        return theUser.getPassword();
    }

    /**
     * Verifies if entered password matches stored password
     * @param password User password that has been entered
     * @param storedHash The stored hash password
     * @return Password verification status
     */
    private static boolean verifyPassword(String password, String storedHash){
        // Convert user password to hash
        String hashedPass = hashPassword(password);
        // Compare entered hash to saved hash
        return storedHash.equals(hashedPass);
        // if same return true, else false
    }

    public static HashMap<Integer, ResetTokenData> sessionData = new HashMap<>();

    /**
     * Creates a new seesion for user after login
     * @param userID The user's ID
     */
    private static void createSession(int userID) {
        String sessionID = UUID.randomUUID().toString(); // create ID for the session
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(60); // set expiry time

        sessionData.put(userID, new ResetTokenData(userID, sessionID, expiry));
    }

    public static HashMap<Integer, ResetTokenData> authTokens = new HashMap<>();

    /**
     * Creates an authentication token for the user for the session
     * @param userID The user's ID
     */
    private static void createAuthToken(int userID) {
        String authToken = UUID.randomUUID().toString(); // create authToken
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(90); // set expiry time

        authTokens.put(userID, new ResetTokenData(userID, authToken, expiry));
    }

    /**
     * Hashes the user's password before saving.
     *
     * @param password The plain text password
     * @return The hashed password
     */
    public static String hashPassword(String password) {
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
