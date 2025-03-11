package Authentication;

public class ResetUserPassword {

    /**
     * Send password reset link or code to user's email
     * @param email User's registered email
     * @return Status of reset request
     */
    public boolean resetRequest(String email){
        // Check if email is valid
        // Look for email in database
        // Generate reset token or code
        // Send reset link to user's email
        return false;
    }

    /**
     * Validates reset token
     * @param token Password reset token
     * @return Status of token
     */
    public boolean validateResetToken(String token){
        // Check if token is in correct format
        // Verify existence of token and check it's not expired
        return false;
    }

    /**
     * Resets the password
     * @param token Password reset token
     * @param newPassword New password entered by user
     * @return Status if password reset is done or not
     */
    public boolean resetPassword(String token, String newPassword){
        // Validate the token
        // Validate new password(length, strength etc)
        // Hash new password
        // Store new hashed password to database
        return false;
    }
}
