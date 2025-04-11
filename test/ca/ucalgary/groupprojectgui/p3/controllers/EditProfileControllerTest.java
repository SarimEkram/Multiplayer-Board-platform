package ca.ucalgary.groupprojectgui.p3.controllers;

import Authentication.User;
import Authentication.UserDatabase;
import ca.ucalgary.groupprojectgui.p3.controllers.EditProfileController;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for verifying the behavior of EditProfileController.
 * Focuses on field validation, update logic, and input handling.
 */
public class EditProfileControllerTest {

    private EditProfileController controller;

    /**
     * Ensures JavaFX is initialized before any tests run.
     */
    @BeforeAll
    static void initJavaFX() throws InterruptedException {
        new JFXPanel(); // Initializes JavaFX Toolkit
        TimeUnit.MILLISECONDS.sleep(200);
    }

    /**
     * Instantiates a fresh controller before each test.
     */
    @BeforeEach
    void setUp() {
        controller = new EditProfileController();
    }

    /**
     * Injects a private field using reflection.
     * Used to simulate FXML-injected values (e.g., TextField, Label).
     *
     * @param target     the object to inject into
     * @param fieldName  the name of the field
     * @param value      the value to inject
     */
    private void injectPrivateField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            System.out.println("Failed to inject field: " + fieldName + " — " + e.getMessage());
        }
    }

    /**
     * Test Case 1: Update the user's email while leaving the username blank.
     * Validates successful email change and reverts the change after the test.
     */
    @Test
    public void testHandleSaveChanges_WithRealUser_UpdateEmailOnly() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                User user = UserDatabase.getUserById(100001);
                assertNotNull(user);
                String originalEmail = user.getEmail();
                String newEmail = "updated_" + originalEmail;

                injectPrivateField(controller, "currentUser", user);
                injectPrivateField(controller, "usernameField", new TextField(""));
                injectPrivateField(controller, "emailField", new TextField(newEmail));
                injectPrivateField(controller, "infoLabel", new Label());
                injectPrivateField(controller, "userIdLabel", new Label());
                injectPrivateField(controller, "winRatioLabel", new Label());
                injectPrivateField(controller, "levelLabel", new Label());
                injectPrivateField(controller, "onlineStatusLabel", new Label());

                Method saveMethod = EditProfileController.class.getDeclaredMethod("handleSaveChanges");
                saveMethod.setAccessible(true);
                saveMethod.invoke(controller);

                User updated = UserDatabase.getUserById(100001);
                assertEquals(newEmail, updated.getEmail(), "Email should be updated.");

                // Restore original state
                updated.setEmail(originalEmail);
                latch.countDown();
            } catch (Exception e) {
                System.out.println("Exception occurred: " + e.getMessage());
            }
        });

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    /**
     * Test Case 2: Submits an update with no changes to the username or email.
     * Expects a message indicating that no changes were made.
     */
    @Test
    public void testHandleSaveChanges_WithRealUser_NoChanges() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                User user = UserDatabase.getUserById(100002);
                assertNotNull(user);

                injectPrivateField(controller, "currentUser", user);
                injectPrivateField(controller, "usernameField", new TextField(""));
                injectPrivateField(controller, "emailField", new TextField(""));
                Label info = new Label();
                injectPrivateField(controller, "infoLabel", info);
                injectPrivateField(controller, "userIdLabel", new Label());
                injectPrivateField(controller, "winRatioLabel", new Label());
                injectPrivateField(controller, "levelLabel", new Label());
                injectPrivateField(controller, "onlineStatusLabel", new Label());

                Method saveMethod = EditProfileController.class.getDeclaredMethod("handleSaveChanges");
                saveMethod.setAccessible(true);
                saveMethod.invoke(controller);

                assertTrue(info.getText().toLowerCase().contains("no changes"), "Expected 'no changes' message.");
                latch.countDown();
            } catch (Exception e) {
                System.out.println("Exception occurred: " + e.getMessage());
            }
        });

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    /**
     * Test Case 3: Attempts to update the email field with an invalid email format.
     * Expects the controller to reject the update and show an appropriate message.
     */
    @Test
    public void testHandleSaveChanges_WithRealUser_InvalidEmail() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                User user = UserDatabase.getUserById(100003);
                assertNotNull(user);

                injectPrivateField(controller, "currentUser", user);
                injectPrivateField(controller, "usernameField", new TextField(""));
                injectPrivateField(controller, "emailField", new TextField("invalid-email")); // Missing '@'
                Label info = new Label();
                injectPrivateField(controller, "infoLabel", info);
                injectPrivateField(controller, "userIdLabel", new Label());
                injectPrivateField(controller, "winRatioLabel", new Label());
                injectPrivateField(controller, "levelLabel", new Label());
                injectPrivateField(controller, "onlineStatusLabel", new Label());

                Method saveMethod = EditProfileController.class.getDeclaredMethod("handleSaveChanges");
                saveMethod.setAccessible(true);
                saveMethod.invoke(controller);

                assertTrue(info.getText().toLowerCase().contains("valid email"), "Expected email validation error.");
                latch.countDown();
            } catch (Exception e) {
                System.out.println("Exception occurred: " + e.getMessage());
            }
        });

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }
}
