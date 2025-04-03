package ca.ucalgary.groupprojectgui.p3.controllers;

import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import MatchmakingLeaderboard.Player;
import MatchmakingLeaderboard.PlayerDatabase;

import java.util.List;

public class LeaderboardController {

    @FXML
    private TabPane gameTabs;

    @FXML
    private ImageView gameLogo;

    private static final int GAME_TIC_TAC_TOE = 1;
    private static final int GAME_CHECKERS = 2;
    private static final int GAME_CONNECT_4 = 3;

    @FXML
    private void initialize() {
        setupTabChangeListeners();
        populateLeaderboards(GAME_TIC_TAC_TOE); // Default to Tic Tac Toe on open
    }

    private void setupTabChangeListeners() {
        gameTabs.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.getText().equals("Tic Tac Toe")) {
                populateLeaderboards(GAME_TIC_TAC_TOE);
            } else if (newValue.getText().equals("Checkers")) {
                populateLeaderboards(GAME_CHECKERS);
            } else if (newValue.getText().equals("Connect 4")) {
                populateLeaderboards(GAME_CONNECT_4);
            }
        });
    }

    private void populateLeaderboards(int gameType) {
        VBox content = new VBox(10);
        content.setStyle("-fx-alignment: center; -fx-padding: 20;");

        List<Player> players = PlayerDatabase.getPlayersForGame(gameType);
        int rank = 1;  // Initialize a rank counter to label each player with a rank number
        for (Player player : players) {
            Label entry = new Label(rank + ". Player ID: " + player.getUserID() + " - Score: " + player.getMMR(gameType));
            entry.setStyle("-fx-text-fill: white; -fx-font-size: 16;");
            content.getChildren().add(entry);
            rank++;  // Increment the rank for the next player
        }

        // Create a ScrollPane and set its content to the VBox
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(content);
        scrollPane.setFitToWidth(true); // This ensures the ScrollPane will adjust to the width of its content

        // Set the ScrollPane as the content of the selected tab
        gameTabs.getSelectionModel().getSelectedItem().setContent(scrollPane);
    }




    @FXML
    private void handleBack() {
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/HomePage.fxml", "Home Page", "home.css");
    }
}
