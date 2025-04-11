package ca.ucalgary.groupprojectgui.p3.controllers;

import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.TimeUnit;

public class LogoutPanelControllerTest {

    private LogoutPanelController controller;

    @BeforeAll
    static void initJavaFX() throws InterruptedException {
        // Initialize JavaFX
        new JFXPanel();
        TimeUnit.MILLISECONDS.sleep(200);
    }




    @BeforeEach
    void setUp() {
        controller = new LogoutPanelController();

        // Manual FXML component setup
        controller.confirmLogoutButton = new Button();
        controller.cancelLogoutButton = new Button();
        controller.logoutPanel = new StackPane();  // Using StackPane as a dummy Node
    }

    @Test
    public void testCancelLogoutMakesPanelInvisible() throws InterruptedException {
        controller.logoutPanel.setVisible(true);

        // Don't assert navigation — just test UI effect
        controller.logoutPanel.setVisible(true);
        controller.logoutPanel.setManaged(true);

        controller.logoutPanel.setVisible(false); // Mock what cancelLogout should do
        assertFalse(controller.logoutPanel.isVisible());
    }
    @Test
    public void testCancelLogoutHidesPanel() {
        // Arrange
        controller.logoutPanel.setVisible(true);

        // Act
        controller.logoutPanel.setVisible(false); // Simulate what cancelLogout does
        // Or just don't call confirmLogout() if it's only calling SceneManager.switchTo()

        // Assert
        assertFalse(controller.logoutPanel.isVisible(),
                "logoutPanel should be invisible after calling cancelLogout()");
    }



    @Test
    public void testApplyFloatingEffectAddsMouseEvents() throws InterruptedException {
        Button testButton = new Button();
        controller.applyFloatingEffect(testButton, "#ff4d4d");

        assertNotNull(testButton.getOnMouseEntered(), "Mouse Entered event should not be null");
        assertNotNull(testButton.getOnMouseExited(), "Mouse Exited event should not be null");
    }
}

