package Authentication;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class ResetUserPasswordTest {

    private final String email = "resetuser@example.com";
    private final String initialPassword = "initialPassword";
    private final String newPassword = "newStrongPassword";

    @Test
    void testResetRequestAndPasswordChange() {
        // Create and save a test user
        User testUser = new User(0, "resetUser", email, initialPassword, 0.5, 3, false);
        UserDatabase.saveUser(testUser);

        // Trigger password reset
        ResetUserPassword resetService = new ResetUserPassword();
        String token = resetService.resetRequest(email);

        assertNotNull(token, "Reset token should be generated");
        assertFalse(token.isEmpty(), "Token should not be empty");

        // Reset the password using the token
        boolean resetSuccessful = resetService.resetPassword(token, newPassword);
        assertTrue(resetSuccessful, "Password should be reset successfully");

        // Confirm the new password is hashed and updated in DB
        User updatedUser = UserDatabase.getUserByEmail(email);
        assertNotNull(updatedUser);
        String newhashpassword = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(newPassword.getBytes(StandardCharsets.UTF_8));
            newhashpassword = Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
        }
        assertEquals(newhashpassword, updatedUser.getPassword(), "Password should be hashed");
    }

    @Test
    void testInvalidEmailReset() {
        ResetUserPassword resetService = new ResetUserPassword();
        String token = resetService.resetRequest("nonexistent@example.com");

        assertNull(token, "Token should not be generated for invalid email");
    }

    @Test
    void testInvalidTokenReset() {
        ResetUserPassword resetService = new ResetUserPassword();
        boolean result = resetService.resetPassword("invalid-token", newPassword);

        assertFalse(result, "Reset should fail for an invalid token");
    }

    @Test
    void testWeakPasswordReset() {
        // Create user
        User user = new User(0, "weakPassUser", "weakpass@example.com", "pass123", 0.2, 1, false);
        UserDatabase.saveUser(user);

        // Generate reset token
        ResetUserPassword resetService = new ResetUserPassword();
        String token = resetService.resetRequest("weakpass@example.com");

        // Attempt with weak password
        boolean result = resetService.resetPassword(token, "123");
        assertFalse(result, "Should not allow weak password (less than 6 chars)");
    }
}
