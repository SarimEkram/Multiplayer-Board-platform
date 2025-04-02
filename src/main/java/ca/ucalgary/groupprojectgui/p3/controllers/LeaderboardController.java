package ca.ucalgary.groupprojectgui.p3.controllers;

import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LeaderboardController {

    @FXML
    private TabPane gameTabs;

    @FXML
    private ImageView gameLogo;

    @FXML
    private void initialize() {

        populateLeaderboards();
    }



    private void populateLeaderboards() {
        for (Tab tab : gameTabs.getTabs()) {
            VBox content = new VBox(10);
            content.setStyle("-fx-alignment: center; -fx-padding: 20;");

            for (int i = 1; i <= 5; i++) {
                Label entry = new Label("Player " + i + " - Score: " + (100 - i * 10));
                entry.setStyle("-fx-text-fill: white; -fx-font-size: 16;");
                content.getChildren().add(entry);
            }

            tab.setContent(content);
        }
    }

    @FXML
    private void handleBack() {
        SceneManager.switchTo(

                "/ca/ucalgary/groupprojectgui/p3/HomePage.fxml",
                "Home Page",
                "home.css"

        );    }
}
