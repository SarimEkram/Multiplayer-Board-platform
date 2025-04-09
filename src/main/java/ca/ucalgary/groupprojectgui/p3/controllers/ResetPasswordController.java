package ca.ucalgary.groupprojectgui.p3.controllers;

import Authentication.ResetUserPassword;
import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

/**
 * Controller for handling the password reset process.
 * It verifies a user's email, generates a token, and allows them
 * to reset their password using the token and a new password.
 */
public class ResetPasswordController {

    @FXML
    private TextField emailField;  // Input user's registered email

    @FXML
    private TextField TokenField;  // Field to enter the token received

    @FXML
    private PasswordField newPasswordField;   // Input user's new password

    @FXML
    private Label statusLabel;  // Label to show status messages

    @FXML
    private VBox passwordResetPane;   // Container for fields shown after token is sent

    // Service for handling reset logic
    private final ResetUserPassword resetService = new ResetUserPassword();

    // Field to store the token generated during email verification
    private String currentToken = null;
    private String token;

    /**
     * Handles sending a reset token after email is submitted.
     * If the email is valid and exists in the database, a token is generated.
     */
    @FXML
    private void handleSendToken() {
        String email = emailField.getText();
        // Validate email field
        if (email == null || email.isEmpty()) {
            statusLabel.setText("Please enter your email.");
            return;
        }
        // Request token from reset service
        token = resetService.resetRequest(email);
        if (token == null) {
            statusLabel.setText("No account found for this email.");
        } else {
            statusLabel.setText("Token generated: " + token);
        }
    }

    /**
     * Handles the actual password reset.
     * Validates the entered token and checks password criteria before updating.
     */
    @FXML
    private void handleResetPassword() {
        String email = emailField.getText();
        String oldPassword = TokenField.getText();
        String newPassword = newPasswordField.getText();

        // Ensure all fields are filled
        if (email == null || email.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty()) {
            statusLabel.setText("Please fill in your email, old password, and new password.");
            return;
        }
        // Prevents using the same password again
        if (oldPassword.equals(newPassword)) {
            statusLabel.setText("New password must be different from the old password.");
            return;
        }
        // Ensure the email was verified first.
        if (token == null) {
            System.out.println(token);
            statusLabel.setText("Please verify your email first.");
            return;
        }
        if (!token.equals(oldPassword)){
            statusLabel.setText("Token does not match");
            return;
        }
        // Use the previously generated token to reset the password.
        boolean success = resetService.resetPassword(token, newPassword);
        if (success && token.equals(oldPassword)) {
            statusLabel.setStyle("-fx-text-fill: #44ff44;");
            statusLabel.setText("✅ Password reset successfully.");
            // Clear the token after a successful reset.
            currentToken = null;
        } else {
            statusLabel.setStyle("-fx-text-fill: #ff4444;");
            statusLabel.setText("❌ Password reset failed. New password might be too weak or token expired.");
        }
    }
    /**
     * Navigates the user back to the login screen.
     */
    @FXML
    private void handleBack() {
        // Navigate back to login or previous screen.
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/Login.fxml", "Login", "login.css");
    }
}