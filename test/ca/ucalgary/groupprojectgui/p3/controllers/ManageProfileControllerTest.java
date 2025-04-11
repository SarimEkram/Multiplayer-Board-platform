package ca.ucalgary.groupprojectgui.p3.controllers;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ManageProfileController.
 * These tests avoid real scene switching or dialog display to prevent NPEs in headless mode.
 */
public class ManageProfileControllerTest {

    private ManageProfileController controller;

    @BeforeAll
    static void setupJavaFX() throws InterruptedException {
        new JFXPanel(); // Bootstraps JavaFX runtime
        TimeUnit.MILLISECONDS.sleep(200); // Give FX thread time
    }

    @BeforeEach
    public void setUp() {
        controller = new ManageProfileController();
        controller.gridBackground = new Pane();
        controller.cyberGlow = new Pane();
        controller.profileBox = new VBox();
    }

    /**
     * Tests whether the setupBackground() function applies black background and glowing effect properly.
     */
    @Test
    public void testSetupBackground() throws InterruptedException {
        Platform.runLater(() -> {
            controller.setupBackground();

            // Check if black background was set
            assertEquals("-fx-background-color: black;", controller.gridBackground.getStyle());

            // Verify that glowing layer was added
            assertEquals(1, controller.cyberGlow.getChildren().size());
            assertNotNull(controller.cyberGlow.getChildren().get(0));
        });

        TimeUnit.MILLISECONDS.sleep(250); // Wait for UI thread
    }

    @Test
    public void testHandleDeleteProfileSafety_WithSceneAttached() throws InterruptedException {
        Platform.runLater(() -> {
            try {
                // Create dummy scene & attach to profileBox to prevent NPE
                Stage dummyStage = new Stage();
                Scene dummyScene = new Scene(controller.profileBox, 400, 400);
                dummyStage.setScene(dummyScene); // now profileBox.getScene() won't be null

                // Show the stage off-screen (avoids UI flashing)
                dummyStage.setOpacity(0);
                dummyStage.show();

                // Call the method to test that it does not crash
                assertDoesNotThrow(() -> controller.handleDeleteProfile());

                dummyStage.close(); // cleanup
            } catch (Exception e) {
                fail("Exception during test: " + e.getMessage());
            }
        });

        TimeUnit.MILLISECONDS.sleep(300); // allow UI thread to settle
    }

}
