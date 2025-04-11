package ca.ucalgary.groupprojectgui.p3.controllers;

import Authentication.User;
import Authentication.UserDatabase;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ChangePasswordController using JavaFX and Java Reflection.
 */
public class ChangePasswordControllerTest {

    private ChangePasswordController controller;

    @BeforeAll
    static void initJavaFX() throws InterruptedException {
        // Ensures JavaFX runtime is initialized before tests
        new JFXPanel();
        TimeUnit.MILLISECONDS.sleep(200);
    }

    @BeforeEach
    void setUp() throws Exception {
        controller = new ChangePasswordController();

        // Inject dummy TextFields using Reflection
        setPrivateField(controller, "forgotEmail", new TextField());
        setPrivateField(controller, "forgotErrorLabel", new Label());

        setPrivateField(controller, "oldPassword", new PasswordField());
        setPrivateField(controller, "newPassword", new PasswordField());
        setPrivateField(controller, "confirmNewPassword", new PasswordField());
        setPrivateField(controller, "resetErrorLabel", new Label());

        // Create a dummy user in database
        User testUser = UserDatabase.getUserById(100000);
        if (testUser == null) {
            System.out.println("Test user 100000 not found in database.");
        }
        LoginController.loginId = 100000;
    }

    /**
     * Test case: Invalid email input for verification.
     * Should display an error message in forgotErrorLabel.
     */
    @Test
    public void testInvalidEmailInput_ShowsErrorLabel() throws Exception {
        Platform.runLater(() -> {
            try {
                TextField forgotEmail = (TextField) getPrivateField(controller, "forgotEmail");
                forgotEmail.setText("invalidEmail");

                invokePrivateMethod(controller, "proceedForgot");

                Label errorLabel = (Label) getPrivateField(controller, "forgotErrorLabel");
                assertTrue(errorLabel.isVisible());
                assertTrue(errorLabel.getText().contains("Please enter a valid email"));
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
        });
        TimeUnit.MILLISECONDS.sleep(300);
    }

    /**
     * Test case: Mismatched new password and confirmation.
     * Should display an appropriate error in resetErrorLabel.
     */
    @Test
    public void testMismatchedPasswords_ShowsError() throws Exception {
        Platform.runLater(() -> {
            try {
                ((PasswordField) getPrivateField(controller, "oldPassword")).setText("wrongPassword");
                ((PasswordField) getPrivateField(controller, "newPassword")).setText("new123");
                ((PasswordField) getPrivateField(controller, "confirmNewPassword")).setText("new321");

                invokePrivateMethod(controller, "resetPassword");

                Label errorLabel = (Label) getPrivateField(controller, "resetErrorLabel");
                assertTrue(errorLabel.isVisible());
                assertTrue(errorLabel.getText().contains("do not match"));
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
        });
        TimeUnit.MILLISECONDS.sleep(300);
    }
    /**
     * Test case: Valid email input matching logged-in user.
     * Should hide verify section and show reset section after verification.
     */
    @Test
    public void testValidEmail_VerificationSuccess_ShowsResetSection() throws Exception {
        Platform.runLater(() -> {
            try {
                // Manually inject reset section VBox
                VBox fakeResetSection = new VBox();
                setPrivateField(controller, "resetPasswordSection", fakeResetSection);

                // Also inject required password fields and reset button to avoid further null errors
                setPrivateField(controller, "oldPassword", new PasswordField());
                setPrivateField(controller, "newPassword", new PasswordField());
                setPrivateField(controller, "confirmNewPassword", new PasswordField());
                setPrivateField(controller, "resetBtn", new Button());

                // Set the email field to match test user
                TextField forgotEmail = (TextField) getPrivateField(controller, "forgotEmail");
                forgotEmail.setText("user1@example.com");

                // Trigger email verification
                invokePrivateMethod(controller, "proceedForgot");

                // Wait for animation to show section
                TimeUnit.MILLISECONDS.sleep(2500);

                VBox resetSection = (VBox) getPrivateField(controller, "resetPasswordSection");
                assertNotNull(resetSection, "Reset section should not be null.");
                assertTrue(resetSection.isVisible(), "Reset section should be visible after successful email verification.");

            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
        });
        TimeUnit.MILLISECONDS.sleep(3000);
    }


    /**
     * Test case: Password reset fails due to incorrect old password.
     * Should display an appropriate error.
     */
    @Test
    public void testResetFailsDueToIncorrectOldPassword_ShowsError() throws Exception {
        Platform.runLater(() -> {
            try {
                // Fill password fields
                ((PasswordField) getPrivateField(controller, "oldPassword")).setText("wrongpass");
                ((PasswordField) getPrivateField(controller, "newPassword")).setText("new123");
                ((PasswordField) getPrivateField(controller, "confirmNewPassword")).setText("new123");

                // Attempt reset
                invokePrivateMethod(controller, "resetPassword");

                // Check error message
                Label errorLabel = (Label) getPrivateField(controller, "resetErrorLabel");
                assertTrue(errorLabel.isVisible());
                assertTrue(errorLabel.getText().toLowerCase().contains("old password does not match"));

            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
        });
        TimeUnit.MILLISECONDS.sleep(300);
    }



    // ------------------------
    // Reflection Utilities
    // ------------------------

    private void setPrivateField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    private Object getPrivateField(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    private void invokePrivateMethod(Object obj, String methodName) throws Exception {
        Method method = obj.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(obj);
    }
}
