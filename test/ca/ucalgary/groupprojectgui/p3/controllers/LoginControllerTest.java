package ca.ucalgary.groupprojectgui.p3.controllers;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import Authentication.User;
import Authentication.UserDatabase;
import Authentication.UserLogin;
import Authentication.UserRegistration;
import Authentication.ResetTokenData;
import Authentication.ResetUserPassword;

/**
 * Test cases for the LoginController in a JavaFX application.
 */
public class LoginControllerTest {

    private LoginController controller;

    @BeforeAll
    static void setupJavaFX() throws InterruptedException {
        // Initialize JavaFX environment
        new JFXPanel();
        TimeUnit.MILLISECONDS.sleep(200); // Ensure JavaFX platform is initialized
    }

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        // Setup the controller and mock its dependencies
        controller = new LoginController();
        mockControllerFields();
    }

    private void mockControllerFields() {
        controller.loginUsername = new TextField();
        controller.loginPassword = new PasswordField();
        controller.loginButton = new Button();
        controller.loginErrorLabel = new Label();
        controller.forgotEmail = new TextField();
        controller.verifyEmailBtn = new Button();
        controller.forgotErrorLabel = new Label();
        controller.resetToken = new TextField();
        controller.newPassword = new PasswordField();
        controller.confirmNewPassword = new PasswordField();
        controller.resetBtn = new Button();
        controller.resetErrorLabel = new Label();
        controller.registerFullName = new TextField();
        controller.registerUsername = new TextField();
        controller.registerEmail = new TextField();
        controller.registerPassword = new PasswordField();
        controller.registerConfirmPassword = new PasswordField();
        controller.registerBtn = new Button();
        controller.registerErrorLabel = new Label();
    }

    @Test
    public void testLoginEmptyFields() throws InterruptedException {
        Platform.runLater(() -> {
            controller.loginUsername.setText("");
            controller.loginPassword.setText("");
            controller.handleLogin();
            assertEquals("Username or Password cannot be empty.", controller.loginErrorLabel.getText());
        });
        TimeUnit.MILLISECONDS.sleep(250); // Wait for UI thread
    }

    @Test
    public void testLoginInvalidUser() throws InterruptedException {
        Platform.runLater(() -> {
            controller.loginUsername.setText("nonexistent");
            controller.loginPassword.setText("password");
            controller.handleLogin();
            assertEquals("User does not exist.", controller.loginErrorLabel.getText());
        });
        TimeUnit.MILLISECONDS.sleep(250); // Wait for UI thread
    }

    @Test
    public void testRegisterValidData() throws InterruptedException {
        Platform.runLater(() -> {
            controller.registerUsername.setText("newuser");
            controller.registerEmail.setText("newuser@example.com");
            controller.registerPassword.setText("password");
            controller.registerConfirmPassword.setText("password");
            controller.handleRegister();
            // Assuming registration goes through successfully
            assertEquals("Registration successful! Redirecting to login...", controller.registerErrorLabel.getText());
        });
        TimeUnit.MILLISECONDS.sleep(250); // Wait for UI thread
    }

}
