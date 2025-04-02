
package ca.ucalgary.groupprojectgui.p3.controllers;
import Authentication.User;
import Authentication.UserDatabase;
import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
public class LoginController {

    public static int playerid;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Label loginErrorMessageLabel;
    @FXML
    private void handleLogin() {
        String user = username.getText();
        String pass = password.getText();
        // Placeholder: You would add actual validation logic here
        if (user.isEmpty() || pass.isEmpty()) {
            loginErrorMessageLabel.setText("Username or Password cannot be empty.");
            loginErrorMessageLabel.setVisible(true);
            loginErrorMessageLabel.setManaged(true);
        } else {
            loginErrorMessageLabel.setVisible(false);
            loginErrorMessageLabel.setManaged(false);
            System.out.println("Login attempted with: " + user + " / " + pass);
        }

        User loginuser  = UserDatabase.getUserByUsername(username.getText());
        int loginid = loginuser.getUserID();
        playerid=loginid;

    }
    @FXML
    private void handleRegister() {
        System.out.println("Register button clicked.");
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/UserRegistration.fxml", "User Registation", "UserRegistration.css");
        // Navigate to Register screen
    }
    @FXML
    private void handleForgotPassword() {
        System.out.println("Forgot Password clicked.");
        // Navigate to Password Recovery screen
    }
}
