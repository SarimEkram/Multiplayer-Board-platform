package ca.ucalgary.groupprojectgui.p3.controllers;
import Authentication.UserDatabase;
import Authentication.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ca.ucalgary.groupprojectgui.p3.SceneManager;
import Authentication.UserRegistration;

public class UserRegistrationController {
    @FXML
    private TextField email;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Label registerErrorMessageLabel;

    // Instance of UserRegistration to handle the logic
    private UserRegistration userRegistration = new UserRegistration();

    @FXML
    private void handleRegister() {
        // Get user input from fields
        String userEmail = email.getText();
        String userUsername = username.getText();
        String userPassword = password.getText();

        // Attempt to register the user
        if (userEmail.isEmpty() || userUsername.isEmpty() || userPassword.isEmpty()) {
            registerErrorMessageLabel.setText("Please fill all fields.");
            return;
        }

        boolean registrationSuccess = userRegistration.registerUser(userUsername, userEmail, userPassword);

        if (registrationSuccess) {
            User newUser = new User(UserDatabase.generateUniqueUserID(), userUsername,userEmail,userPassword,0.0,0,false);

            registerErrorMessageLabel.setText("Registration successful!");
            switchToLogin();
        } else {
            registerErrorMessageLabel.setText("Registration failed. Please check your details and try again.");
        }
    }

    @FXML
    private void switchToLogin() {
        // Assuming SceneManager is correctly set up to switch views
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/login.fxml", "Login", "login.css");
    }
}
