package ca.ucalgary.groupprojectgui.p3.controllers;

import Authentication.ResetUserPassword;
import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ResetPasswordController {

    @FXML
    private TextField emailField;

    @FXML
    private TextField tokenField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private Label statusLabel;

    private final ResetUserPassword resetService = new ResetUserPassword();

    @FXML
    private void handleSendToken() {
        String email = emailField.getText();
        if (email == null || email.isEmpty()) {
            statusLabel.setText("Please enter your email.");
            return;
        }

        String token = resetService.resetRequest(email);
        if (token == null) {
            statusLabel.setText("No account found for this email.");
        } else {
            // should be removed later ( for testing purpose)
            statusLabel.setText("Token generated: " + token);
        }
    }

    @FXML
    private void handleResetPassword() {
        String token = tokenField.getText();
        String newPassword = newPasswordField.getText();

        if (token.isEmpty() || newPassword.isEmpty()) {
            statusLabel.setText("Please fill in both token and new password.");
            return;
        }

        boolean success = resetService.resetPassword(token, newPassword);
        if (success) {
            statusLabel.setStyle("-fx-text-fill: #44ff44;");
            statusLabel.setText("✅ Password reset successfully.");
        } else {
            statusLabel.setStyle("-fx-text-fill: #ff4444;");
            statusLabel.setText("❌ Invalid or expired token, or password too weak.");
        }
    }

    @FXML
    private void handleBack() {
        // Navigate back to login or previous screen
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/Login.fxml", "Login","login.css");
    }

}
