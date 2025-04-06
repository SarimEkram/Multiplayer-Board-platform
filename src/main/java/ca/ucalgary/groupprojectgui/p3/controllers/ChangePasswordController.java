package ca.ucalgary.groupprojectgui.p3.controllers;

import Authentication.ResetUserPassword;
import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class ChangePasswordController {

    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button saveChangesButton;
    @FXML
    private TextField emailField;
    @FXML
    private Label statusLabel;
    @FXML
    private PasswordField oldPasswordField;
    @FXML
    private VBox passwordResetPane;
    private String currentToken = null;


    @FXML
    private TextField emailOrIDField;
    private final ResetUserPassword resetService = new ResetUserPassword();

    @FXML
    private void handleVerify() {
        // Show step 2 elements
        newPasswordField.setVisible(true);
        newPasswordField.setManaged(true);

        confirmPasswordField.setVisible(true);
        confirmPasswordField.setManaged(true);

        saveChangesButton.setVisible(true);
        saveChangesButton.setManaged(true);
    }
    @FXML
    private void handleVerifyEmail() {
        String email = emailField.getText();
        if (email == null || email.isEmpty()) {
            statusLabel.setText("Please enter your email.");
            return;
        }

        // Call resetRequest to check if the email exists.
        String token = resetService.resetRequest(email);
        if (token == null) {
            statusLabel.setText("No account found for this email.");
        } else {
            currentToken = token;
            // Reveal the hidden password reset controls.
            passwordResetPane.setVisible(true);
            passwordResetPane.setManaged(true);
            statusLabel.setText("Email verified. Please enter your old and new password.");
        }
    }

    @FXML
    private void handleResetPassword() {
        String email = emailField.getText();
        String oldPassword = oldPasswordField.getText(); // Now used as the old password
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
        if (currentToken == null) {
            statusLabel.setText("Please verify your email first.");
            return;
        }

        // Verify that the old password matches the stored password.
        boolean oldPassMatches = resetService.verifyOldPassword(email, oldPassword);
        if (!oldPassMatches) {
            statusLabel.setText("Old password does not match.");
            return;
        }

        // Use the previously generated token to reset the password.
        boolean success = resetService.resetPassword(currentToken, newPassword);
        if (success) {
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
    private void handleSaveChanges() {
        // Placeholder: Do nothing for now
        System.out.println("Save Changes clicked.");
    }

    @FXML
    private void handleBack() {
        SceneManager.switchTo(
                "/ca/ucalgary/groupprojectgui/p3/ManageProfile.fxml",
                "Manage Profile",
                "manage_profile.css"
        );
    }

}
