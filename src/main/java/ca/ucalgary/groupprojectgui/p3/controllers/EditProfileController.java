package ca.ucalgary.groupprojectgui.p3.controllers;

import Authentication.User;
import Authentication.UserDatabase;
import Authentication.UpdateUserProfile;
import MatchmakingLeaderboard.Player;
import MatchmakingLeaderboard.PlayerDatabase;
import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

/**
 * Controller class for the Edit Profile screen.
 * Allows users to update their username and email.
 */
public class EditProfileController {

    @FXML
    private TextField usernameField;     // Field for editing username

    @FXML
    private TextField emailField;        // Field for editing email

    @FXML
    private Label statusLabel;           // Label to display status or error messages

    private User currentUser;            // Holds the currently logged-in user's data

    /**
     * Initializes the controller after the FXML elements are loaded.
     * Loads the current user's profile into the form fields.
     */
    @FXML
    public void initialize() {
        currentUser = UserDatabase.getUserById(LoginController.loginId);
        // If user not found, display error
        if (currentUser == null) {
            statusLabel.setText("Error: No user loaded.");
            return;
        }
        // Pre-fill the form with user's current info
        loadUserProfile();
    }

    /**
     * Loads the current user's profile data into the input fields.
     */
    private void loadUserProfile() {
        usernameField.setText(currentUser.getUsername());
        emailField.setText(currentUser.getEmail());
    }

    /**
     * Handles the Save button action.
     * Validates input and attempts to update the user's profile.
     */
    @FXML
    private void handleSaveChanges() {
        String newUsername = usernameField.getText().trim();
        String newEmail = emailField.getText().trim();

        // Basic input validation
        if (newUsername.isEmpty() || newEmail.isEmpty() || !newEmail.contains("@")) {
            statusLabel.setText("Please enter a valid username and email.");
            return;
        }
        // Check if changes were actually made
        if (newUsername.equals(currentUser.getUsername()) && newEmail.equals(currentUser.getEmail())) {
            statusLabel.setText("No changes were made.");
            return;
        }
        // Attempt to update the user's profile
        UpdateUserProfile updater = new UpdateUserProfile();
        boolean updateResult = updater.updateUser(currentUser.getUserID(), newUsername, newEmail);

        if (updateResult) {
            statusLabel.setText("Profile updated successfully: Username and Email have been changed.");
            Player player = PlayerDatabase.getPlayerByUserID(currentUser.getUserID());
            player.setUsername(newUsername);
            PlayerDatabase.savePlayer(player);
        } else {
            statusLabel.setText("Failed to update profile. Please try again.");
        }
    }

    /**
     * Navigates back to the Manage Profile screen.
     */
    @FXML
    private void handleBack() {
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/ManageProfile.fxml", "Manage Profile", "manage_profile.css");
    }
}
