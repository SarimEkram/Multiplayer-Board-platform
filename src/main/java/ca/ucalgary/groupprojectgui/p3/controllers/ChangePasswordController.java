package ca.ucalgary.groupprojectgui.p3.controllers;

import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ChangePasswordController {

    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button saveChangesButton;


    @FXML
    private TextField emailOrIDField;


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
    private void handleResetPassword() {
        // Placeholder: Validate password and show confirmation
        System.out.println("Password reset logic here.");
    }

    @FXML
    private void handleSaveChanges() {
        // Placeholder: Do nothing for now
        System.out.println("Save Changes clicked.");
    }

    @FXML
    private void handleBack() {
        SceneManager.switchTo(
                "/ca/ucalgary/groupprojectgui/p3/Manage Profile.fxml",
                "Manage Profile",
                "manage_profile.css"
        );
    }
}
