package ca.ucalgary.groupprojectgui.p3.controllers;

import Authentication.User;
import Authentication.UserDatabase;
import Authentication.UserLogin;
import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller class for handling user login functionality.
 * It manages user authentication and navigation to other screens
 * like registration, password recovery, and homepage.
 */
public class LoginController {

    @FXML
    private TextField username;                  // Input for the username
    @FXML
    private PasswordField password;              // Input for the password
    @FXML
    private Label loginErrorMessageLabel;        // Label to show error messages

    public static int loginId;                   // Stores the ID of the successfully logged-in use

    /**
     * Called when the user clicks the Login button.
     * Validates credentials and switches to the home page on successful login.
     */
    @FXML
    private void handleLogin() {
        String user = username.getText();         // Get username from input
        String pass = password.getText();         // Get password from input

        // Validate that neither the username nor the password is empty
        if (user.isEmpty() || pass.isEmpty()) {
            loginErrorMessageLabel.setText("Username or Password cannot be empty.");
            loginErrorMessageLabel.setVisible(true);
            loginErrorMessageLabel.setManaged(true);
            return;
        }

        // Check if the user exists in the database
        User loginUser = UserDatabase.getUserByUsername(user);
        if (loginUser == null) {
            // If user does not exist, display an error message
            loginErrorMessageLabel.setText("User does not exist.");
            loginErrorMessageLabel.setVisible(true);
            loginErrorMessageLabel.setManaged(true);
            return;
        }

        // Validate user credentials with the UserLogin class
        int loginSuccessful = UserLogin.loginUser(user, pass);

        if (loginSuccessful != -1) {
            // If login is successful, hide the error message
            loginErrorMessageLabel.setVisible(false);
            loginErrorMessageLabel.setManaged(false);

            loginId = loginUser.getUserID();
            System.out.println("Login successful for: " + user);

            // After successful login, switch to the home screen or another screen
            SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/HomePage.fxml", "Home Page", "Home.css");
        } else {
            // If login fails, display an error message
            loginErrorMessageLabel.setText("Invalid username or password.");
            loginErrorMessageLabel.setVisible(true);
            loginErrorMessageLabel.setManaged(true);
        }
    }

    /**
     * Called when the user clicks the Register button.
     * Navigates to the user registration screen.
     */
    @FXML
    private void handleRegister() {
        System.out.println("Register button clicked.");
        // Navigate to the registration screen
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/UserRegistration.fxml", "User Registration", "UserRegistration.css");
    }

    /**
     * Called when the user clicks the Forgot Password link/button.
     * Navigates to the password reset screen.
     */
    @FXML
    private void handleForgotPassword() {
        System.out.println("Forgot Password clicked.");
        // Navigate to the password recovery screen
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/resetPassword.fxml", "Forgot Password", "resetPassword.css");
    }
}
