package ca.ucalgary.groupprojectgui.p3.controllers;

import Authentication.ResetUserPassword;
import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class ResetPasswordController {

    @FXML
    private TextField emailField;

    // Removed the old tokenField since the FXML now uses oldPasswordField.
    @FXML
    private TextField TokenField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private Label statusLabel;

    // Container for the hidden password reset controls.
    @FXML
    private VBox passwordResetPane;

    private final ResetUserPassword resetService = new ResetUserPassword();

    // Field to store the token generated during email verification
    private String currentToken = null;
    private String token;

    /**
     * Verifies the email by calling resetRequest.
     * If a token is returned (non-null), it means the account exists.
     * The token is stored for later use in the reset process,
     * and the hidden fields are made visible.
     */
    @FXML
    private void handleSendToken() {
        String email = emailField.getText();
        if (email == null || email.isEmpty()) {
            statusLabel.setText("Please enter your email.");
            return;
        }

        token = resetService.resetRequest(email);
        if (token == null) {
            statusLabel.setText("No account found for this email.");
        } else {
            // For testing purposes; in production you would email the token.
            statusLabel.setText("Token generated: " + token);
        }
    }

    /**
     * Handles the password reset process.
     * It first verifies that:
     * 1. The email was verified (i.e. currentToken is set).
     * 2. The entered old password matches the stored password.
     * 3. The new password is different from the old password.
     * Then, it uses the verified token to reset the password.
     */
    @FXML
    private void handleResetPassword() {
        String email = emailField.getText();
        String oldPassword = TokenField.getText(); // Now used as the old password
        String newPassword = newPasswordField.getText();


        if (email == null || email.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty()) {
            statusLabel.setText("Please fill in your email, old password, and new password.");
            return;
        }

        // Check that new password is different from the old password.
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

    @FXML
    private void handleBack() {
        // Navigate back to login or previous screen.
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/Login.fxml", "Login", "login.css");
    }
}