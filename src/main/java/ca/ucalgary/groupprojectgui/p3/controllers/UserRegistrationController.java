package ca.ucalgary.groupprojectgui.p3.controllers;
import Authentication.UserDatabase;
import Authentication.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ca.ucalgary.groupprojectgui.p3.SceneManager;
import Authentication.UserRegistration;

/**
 * Controller class responsible for handling user registration.
 * Collects input data, validates it, and attempts to register a new user.
 */
public class UserRegistrationController {
    @FXML
    private TextField email;                                      // Input field for user's email
    @FXML
    private TextField username;                                   // Input field for user's chosen username

    @FXML
    private PasswordField password;                               // Input field for user's chosen password

    @FXML
    private Label registerErrorMessageLabel;                       // Label to show registration error and success messages

    private UserRegistration userRegistration = new UserRegistration();   // Instance of UserRegistration to handle the logic

    /**
     * Called when the user clicks the Register button.
     * Validates the input fields and attempts to register a new user.
     */
    @FXML
    private void handleRegister() {
        // Get user input from fields
        String userEmail = email.getText();
        String userUsername = username.getText();
        String userPassword = password.getText();

        // Ensures all fields are filled out
        if (userEmail.isEmpty() || userUsername.isEmpty() || userPassword.isEmpty()) {
            registerErrorMessageLabel.setText("Please fill all fields.");
            return;
        }

        // Attempt to register the user
        boolean registrationSuccess = userRegistration.registerUser(userUsername, userEmail, userPassword);

        if (registrationSuccess) {
            // Create and store new user (this may be handled in UserRegistration internally depending on logic)
            User newUser = new User(UserDatabase.generateUniqueUserID(), userUsername,userEmail,userPassword,0.0,0,false);

            registerErrorMessageLabel.setText("Registration successful!");
            // Navigate back to the login screen
            switchToLogin();
        } else {
            // Registration failed ( user already exists or invalid input)
            registerErrorMessageLabel.setText("Registration failed. Please check your details and try again.");
        }
    }

    /**
     * Switches the view to the login screen.
     * Triggered after a successful registration or when user opts to go back.
     */
    @FXML
    private void switchToLogin() {
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/login.fxml", "Login", "login.css");
    }
}
