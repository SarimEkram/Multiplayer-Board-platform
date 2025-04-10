package Authentication;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * This class handles password reset logic including token generation,
 * password validation, and secure update of user credentials.
 */
public class ResetUserPassword {
    private static final Map<String, ResetTokenData> tokenStore = new HashMap<>();

    /**
     * Initiates a password reset request by generating a token.
     *
     * @param email The email of the user requesting password reset.
     * @return The reset token (to be emailed in real systems), or null if user not found.
     */
    public String resetRequest(String email) {
        User user = UserDatabase.getUserByEmail(email);
        if (user == null) return null;

        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(30);

        tokenStore.put(token, new ResetTokenData(user.getUserID(), token, expiry));
        // In a real system, you'd email the token to the user
        return token;
    }

    /**
     * Resets the user's password using the provided valid token.
     *
     * @param token The reset token issued to the user.
     * @param newPassword The new password to be set.
     * @return true if password reset is successful, false otherwise.
     */
    public boolean resetPassword(String token, String newPassword) {
        ResetTokenData data = tokenStore.get(token);
        if (data == null || data.isExpired()) {
            return false;
        }

        User user = UserDatabase.getUserById(data.getUserId());
        if (user == null || newPassword == null || newPassword.length() < 6) {
            return false;
        }

        user.setPassword(hashPassword(newPassword));
        tokenStore.remove(token);
        return true;
    }


    /**
     *Verifies if the provided old password matches the stored password.
     *
     * @param email       The email of the user.
     * @param oldPassword The old password entered by the user.
     * @return true if the old password matches the stored password, false otherwise.
     */
    public boolean verifyOldPassword(String email, String oldPassword) {
        if (email == null || oldPassword == null) return false;

        User user = UserDatabase.getUserByEmail(email);
        if (user == null) return false;

        String storedHash = user.getPassword();
        String inputHash = hashPassword(oldPassword);

        return storedHash != null && storedHash.equals(inputHash);
    }

    /**
     * Hashes a password using SHA-256 and Base64 encoding.
     *
     * @param password The plain text password.
     * @return The hashed password in Base64 format.
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            return null;
        }
    }
}
