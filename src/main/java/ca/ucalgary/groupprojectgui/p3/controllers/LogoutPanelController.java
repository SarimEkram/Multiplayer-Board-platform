package ca.ucalgary.groupprojectgui.p3.controllers;

import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.scene.Node;

public class LogoutPanelController {

    @FXML private Button confirmLogoutButton;
    @FXML private Button cancelLogoutButton;
    @FXML private Node logoutPanel;

    @FXML
    public void initialize() {
        applyFloatingEffect(confirmLogoutButton, "#ff4d4d");
        applyFloatingEffect(cancelLogoutButton, "#00ffff");
    }

    private void applyFloatingEffect(Button button, String glowColor) {
        TranslateTransition floatUp = new TranslateTransition(Duration.millis(150), button);
        floatUp.setByY(-3);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), button);
        scaleUp.setToX(1.05);
        scaleUp.setToY(1.05);

        TranslateTransition floatDown = new TranslateTransition(Duration.millis(150), button);
        floatDown.setToY(0);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), button);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        button.setOnMouseEntered(e -> {
            floatUp.playFromStart();
            scaleUp.playFromStart();
            button.setStyle("-fx-effect: dropshadow(gaussian, " + glowColor + ", 10, 0.5, 0, 2);");
        });

        button.setOnMouseExited(e -> {
            floatDown.playFromStart();
            scaleDown.playFromStart();
            button.setStyle(""); // Reset to CSS class
        });
    }

    @FXML
    private void cancelLogout() {
        // Instead of removing the node from its parent, just set it invisible.
        logoutPanel.setVisible(false);
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/HomePage.fxml", "Home Page", "Home.css");

    }

    @FXML
    private void confirmLogout() {
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/login.fxml", "Login Page", "login.css");
    }
}
