package ca.ucalgary.groupprojectgui.p3.controllers;

import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.fxml.FXML;

public class EditProfileController {
    @FXML
    private void handleBack() {
        SceneManager.switchTo(
                "/ca/ucalgary/groupprojectgui/p3/Manage Profile.fxml",
                "Manage Profile",
                "manage_profile.css"
        );
    }

}
