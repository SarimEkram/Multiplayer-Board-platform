package ca.ucalgary.groupprojectgui.p3.controllers;

import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.util.Duration;

/**
 * Controller for the logout panel view.
 * <p>
 * This class applies floating and glow effects to logout buttons and handles logout actions.
 * </p>
 */
public class LogoutPanelController {

    @FXML private Button confirmLogoutButton; // Button to confirm logout action.
    @FXML private Button cancelLogoutButton;  // Button to cancel logout action.
    @FXML private Node logoutPanel;           // The entire logout panel container.

    /**
     * Initializes the logout panel controller.
     * <p>
     * Applies a floating effect with a glow color to both logout buttons.
     * </p>
     */
    @FXML
    public void initialize() {
        // Apply floating and glow effects to the logout buttons.
        applyFloatingEffect(confirmLogoutButton, "#ff4d4d"); // Red glow for confirm.
        applyFloatingEffect(cancelLogoutButton, "#00ffff");  // Cyan glow for cancel.
    }

    /**
     * Applies a floating effect (translate and scale) to a given button, along with a glow effect.
     *
     * @param button   the button to which the effect is applied.
     * @param glowColor the glow color specified in hexadecimal format.
     */
    private void applyFloatingEffect(Button button, String glowColor) {
        // Create a translate transition for moving the button slightly upward.
        TranslateTransition floatUp = new TranslateTransition(Duration.millis(150), button);
        floatUp.setByY(-3);

        // Create a scale transition to slightly enlarge the button.
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), button);
        scaleUp.setToX(1.05);
        scaleUp.setToY(1.05);

        // Create a translate transition for moving the button back to its original position.
        TranslateTransition floatDown = new TranslateTransition(Duration.millis(150), button);
        floatDown.setToY(0);

        // Create a scale transition to shrink the button back to its original size.
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), button);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        // On mouse enter, play the upward movement and scale-up transitions and apply a glow effect.
        button.setOnMouseEntered(e -> {
            floatUp.playFromStart();
            scaleUp.playFromStart();
            button.setStyle("-fx-effect: dropshadow(gaussian, " + glowColor + ", 10, 0.5, 0, 2);");
        });

        // On mouse exit, play the downward movement and scale-down transitions and reset styles.
        button.setOnMouseExited(e -> {
            floatDown.playFromStart();
            scaleDown.playFromStart();
            button.setStyle(""); // Clear inline style to revert to default CSS.
        });
    }

    /**
     * Cancels the logout action.
     * <p>
     * Hides the logout panel and switches the scene back to the home page.
     * </p>
     */
    @FXML
    private void cancelLogout() {
        // Instead of removing the node, simply hide the logout panel.
        logoutPanel.setVisible(false);
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/views/homePage.fxml");
    }

    /**
     * Confirms the logout action.
     * <p>
     * Switches the scene to the login view.
     * </p>
     */
    @FXML
    private void confirmLogout() {
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/views/login.fxml");
    }
}
