package ca.ucalgary.groupprojectgui.p3.controllers;

import Authentication.DeleteUserAccount;
import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class ManageProfileController {
    @FXML
    private void handleBackButtonClick() {
        SceneManager.switchTo(
                "/ca/ucalgary/groupprojectgui/p3/HomePage.fxml",
                "Home Page",
                "home.css"
        );
    }
    @FXML
    private void handleEditProfileButtonClick() {
        SceneManager.switchTo(
                "/ca/ucalgary/groupprojectgui/p3/EditProfile.fxml",
                "Edit Profile",
                "edit_profile.css"
        );
    }
    @FXML
    private void handleChangePasswordButtonClick() {
        SceneManager.switchTo(
                "/ca/ucalgary/groupprojectgui/p3/ChangePassword.fxml",
                "Change Password",
                "change_password.css"
        );
    }

    @FXML
    private void handleDeleteProfile() {
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

        ButtonType yesBtn = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);        // styled as default
        ButtonType noBtn = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);    // styled as cancel
        dialogPane.getButtonTypes().setAll(yesBtn, noBtn);

        dialog.showAndWait().ifPresent(response -> {
            if (response == yesBtn) {
                int userId = LoginController.loginId;
                if (DeleteUserAccount.deleteAccount(userId)) {
                    System.out.println("Account deletion confirmed and completed.");
                    SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/Login.fxml", "Login", "login.css"); // Redirect to login or another appropriate screen
                } else {
                    System.out.println("Failed to delete account. Please try again.");
                }
            } else {
                System.out.println("Account deletion cancelled.");
            }
        });
    }
}
