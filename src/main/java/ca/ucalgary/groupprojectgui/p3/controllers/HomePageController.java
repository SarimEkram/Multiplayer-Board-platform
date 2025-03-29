package ca.ucalgary.groupprojectgui.p3.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ca.ucalgary.groupprojectgui.p3.SceneManager;

public class HomePageController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private ListView<String> friendsList;

    @FXML
    private ListView<String> recentScores;

    @FXML
    private Button quickMatchButton;

    @FXML
    private Button logoutButton;

    @FXML
    private void onConnect4Click() {
        launchGame("Connect 4");
    }
    @FXML
    private void handleManageProfile() {
        SceneManager.switchTo(
                "/ca/ucalgary/groupprojectgui/p3/Manage Profile.fxml",
                "Manage Profile",
                "manage_profile.css"
        );
    }
    @FXML
    private void handleBackButtonClick() {
        SceneManager.switchTo(
                "/ca/ucalgary/groupprojectgui/p3/HomePage.fxml",
                "Manage Profile",
                "manage_profile.css"
        );
    }


    @FXML
    public void initialize() {
        // Simulate logged-in user
        String playerName = "Generic Player"; // Normally pulled from a user session
        welcomeLabel.setText("Welcome, " + playerName + "!");

        // Simulate online friends
        ObservableList<String> friends = FXCollections.observableArrayList(
                "Player1 (Online)", "Player2 (Online)", "Player3 (Online)"
        );
        friendsList.setItems(friends);

        // Simulate recent scores
        ObservableList<String> scores = FXCollections.observableArrayList(
                "Tic Tac Toe - Win vs Player1",
                "Connect 4 - Loss vs Player2"
        );
        recentScores.setItems(scores);
    }

    @FXML
    private void handleQuickMatch() {
        System.out.println("Searching for quick match...");
        // Matchmaking logic or scene transition can go here
    }

    @FXML
    private void handleLogout() {
        System.out.println("Logging out...");
        // Navigate back to login screen
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/login.fxml", "Login Page", "login.css");
    }

    public void launchGame(String gameName) {
        System.out.println("Launching game: " + gameName);

        String fxmlFile;
        String title;
        String cssFile;

        switch (gameName) {
            case "Connect 4":
                fxmlFile = "/ca/ucalgary/groupprojectgui/p3/connect4UI.fxml";
                title = "Connect 4 Game";
                cssFile = "connect4.css"; // specific CSS file for Connect 4
                break;

            // Add more game cases here if needed

            default:
                System.out.println("Game not recognized: " + gameName);
                return;
        }

        SceneManager.switchTo(fxmlFile, title, cssFile);
    }
}
