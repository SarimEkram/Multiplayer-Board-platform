package ca.ucalgary.groupprojectgui.p3.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.scene.control.Label;

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
    public void initialize() {
        // Simulate logged-in user
        String playerName = "Alex"; // Normally pulled from a user session
        welcomeLabel.setText("Welcome, " + playerName + "!");

        // Simulate online friends
        ObservableList<String> friends = FXCollections.observableArrayList(
                "Jamie (Online)", "Riley (Online)", "Casey (Online)"
        );
        friendsList.setItems(friends);

        // Simulate recent scores
        ObservableList<String> scores = FXCollections.observableArrayList(
                "Tic Tac Toe - Win vs Jamie",
                "Connect 4 - Loss vs Riley",
                "Snake Duel - Win vs AI"
        );
        recentScores.setItems(scores);
    }

    @FXML
    private void handleQuickMatch() {
        // Logic to connect with random opponent or AI
        System.out.println("Searching for quick match...");
        // You could switch scenes or trigger matchmaking logic here
    }

    @FXML
    private void handleLogout() {
        // Logic to log out the player
        System.out.println("Logging out...");
        // Possibly switch to login screen
    }

    // Optional: Hook up buttons via FXML or manually
    public void launchGame(String gameName) {
        System.out.println("Launching game: " + gameName);
        String fxmlFile;
        String title;

        switch (gameName) {
            case "Connect 4":
                fxmlFile = "/ca/ucalgary/groupprojectgui/p3/connect4UI.fxml";
                title = "Connect 4 Game";
                break;
            // Add more cases as needed for other games
            default:
                System.out.println("Game not recognized: " + gameName);
                return;
        }

        // Assuming you have a SceneManager class to handle scene switching
        SceneManager.switchTo(fxmlFile, title);
    }

}

