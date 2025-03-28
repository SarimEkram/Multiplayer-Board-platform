package ca.ucalgary.groupprojectgui.p3.controllers;

import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to OMG");

        // Switch to the HomePage.fxml and load its specific stylesheet
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/HomePage.fxml", "Home Page", "home.css");
    }
}
