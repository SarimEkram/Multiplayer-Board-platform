package ca.ucalgary.groupprojectgui.p3.controllers;

import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ChangePasswordController {
    @FXML
    private Button submitAnswerButton;

    @FXML private Label securityQuestionLabel;
    @FXML private TextField securityAnswerField;

    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button saveChangesButton;


    @FXML
    private TextField emailOrIDField;


    @FXML
    private void handleVerify() {
        // Simulate a security question being loaded from the system
        securityQuestionLabel.setText("What was your first pet's name?");

        // Reveal step 2 elements
        securityQuestionLabel.setVisible(true);
        securityQuestionLabel.setManaged(true);

        securityAnswerField.setVisible(true);
        securityAnswerField.setManaged(true);

        submitAnswerButton.setVisible(true);
        submitAnswerButton.setManaged(true);
    }


    @FXML
    private void handleSubmitAnswer() {
        // Show step 3 elements
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
