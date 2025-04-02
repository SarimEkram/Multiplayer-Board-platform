package ca.ucalgary.groupprojectgui.p3.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class UserRegistrationController {

    @FXML
    private TextField fullName;
    @FXML
    private TextField email;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Label registerErrorMessageLabel;

    // Dummy method to simulate the registration process
    @FXML
    private void handleRegister(MouseEvent event) {
        // Dummy action, simulate success message
        registerErrorMessageLabel.setText("Registration attempt made!");
        System.out.println("Register button clicked.");
    }

    // Dummy method to simulate switching to the login view
    @FXML
    private void switchToLogin(MouseEvent event) {
        // Dummy action, simulate view switch
        System.out.println("Switch to login view initiated.");
    }
}
