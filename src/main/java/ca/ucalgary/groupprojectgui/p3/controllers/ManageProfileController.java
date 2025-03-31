package ca.ucalgary.groupprojectgui.p3.controllers;

import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.fxml.FXML;

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
                "Edit Profile",
                "edit_profile.css"
        );
    }

    @FXML
    private void handleDeleteProfile() {
//        placeholder
        System.out.println("Profile deleted");
    }
}
