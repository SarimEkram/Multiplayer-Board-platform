package ca.ucalgary.groupprojectgui.p3.controllers;

import Authentication.User;
import Authentication.UserDatabase;
import Authentication.UpdateUserProfile;
import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

public class EditProfileController {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private Label statusLabel;

    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = UserDatabase.getUserById(LoginController.loginId);
        if (currentUser == null) {
            statusLabel.setText("Error: No user loaded.");
            return;
        }
        loadUserProfile();
    }

    private void loadUserProfile() {
        usernameField.setText(currentUser.getUsername());
        emailField.setText(currentUser.getEmail());
    }

    @FXML
    private void handleSaveChanges() {
        String newUsername = usernameField.getText().trim();
        String newEmail = emailField.getText().trim();

        if (newUsername.isEmpty() || newEmail.isEmpty() || !newEmail.contains("@")) {
            statusLabel.setText("Please enter a valid username and email.");
            return;
        }

        if (newUsername.equals(currentUser.getUsername()) && newEmail.equals(currentUser.getEmail())) {
            statusLabel.setText("No changes were made.");
            return;
        }

        UpdateUserProfile updater = new UpdateUserProfile();
        boolean updateResult = updater.updateUser(currentUser.getUserID(), newUsername, newEmail);

        if (updateResult) {
            statusLabel.setText("Profile updated successfully: Username and Email have been changed.");
        } else {
            statusLabel.setText("Failed to update profile. Please try again.");
        }
    }

    @FXML
    private void handleBack() {
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/HomePage.fxml", "Home Page", "Home.css");
    }
}
