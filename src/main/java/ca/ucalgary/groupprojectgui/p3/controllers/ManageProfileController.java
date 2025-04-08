package ca.ucalgary.groupprojectgui.p3.controllers;

import Authentication.DeleteUserAccount;
import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Objects;

/**
 * Controller class for managing user profile-related actions.
 * Supports editing profile, changing password, and deleting the user account.
 */
public class ManageProfileController {

    /**
     * Navigates the user back to the home page.
     */
    @FXML
    private void handleBackButtonClick() {
        SceneManager.switchTo(
                "/ca/ucalgary/groupprojectgui/p3/HomePage.fxml",
                "Home Page",
                "home.css"
        );
    }
    /**
     * Navigates to the Edit Profile screen.
     */
    @FXML
    private void handleEditProfileButtonClick() {
        SceneManager.switchTo(
                "/ca/ucalgary/groupprojectgui/p3/EditProfile.fxml",
                "Edit Profile",
                "edit_profile.css"
        );
    }
    /**
     * Navigates to the Change Password screen.
     */
    @FXML
    private void handleChangePasswordButtonClick() {
        SceneManager.switchTo(
                "/ca/ucalgary/groupprojectgui/p3/ChangePassword.fxml",
                "Change Password",
                "change_password.css"
        );
    }
    /**
     * Handles account deletion with confirmation dialog.
     * If confirmed, deletes the user's account and navigates to login screen.
     */
    @FXML
    private void handleDeleteProfile() {
        // Create a confirmation dialog for deleting the account
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Delete Account");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ca/ucalgary/groupprojectgui/p3/styles/manage_profile.css")).toExternalForm());
        dialogPane.getStyleClass().add("custom-dialog");

        Label confirmText = new Label("Are you sure you want to delete your account?");
        confirmText.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        VBox contentBox = new VBox(confirmText);
        contentBox.setSpacing(15);
        contentBox.setStyle("-fx-alignment: center; -fx-padding: 20;");
        dialogPane.setContent(contentBox);

        ButtonType yesBtn = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType noBtn = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogPane.getButtonTypes().setAll(yesBtn, noBtn);

        dialog.showAndWait().ifPresent(response -> {
            if (response == yesBtn) {
                int userId = LoginController.loginId;
                if (DeleteUserAccount.deleteAccount(userId)) {
                    showDialog("Success", "Your account has been successfully deleted.", Alert.AlertType.INFORMATION);
                    SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/Login.fxml", "Login", "login.css");
                } else {
                    showDialog("Error", "Failed to delete account. Please try again.", Alert.AlertType.ERROR);
                }
            } else {
                showDialog("Cancelled", "Account deletion cancelled.", Alert.AlertType.INFORMATION);
            }
        });
    }

    /**
     * Utility method to show alert dialogs.
     *
     * @param title   The title of the alert window.
     * @param content The message content.
     * @param type    The type of alert
     */
    private void showDialog(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
