package ca.ucalgary.groupprojectgui.p3.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;

import MatchmakingLeaderboard.Player;
import MatchmakingLeaderboard.Connect4.Leaderboard.Connect4Leaderboard;
import MatchmakingLeaderboard.TicTacToe.Leaderboard.TicTacToeLeaderboard;
import MatchmakingLeaderboard.Checkers.Leaderboard.CheckersLeaderboard;
import MatchmakingLeaderboard.GameType;
import static MatchmakingLeaderboard.GameType.*;

import ca.ucalgary.groupprojectgui.p3.SceneManager;

import java.util.List;

public class LeaderboardController {

    @FXML
    private TabPane gameTabs;

    @FXML
    private ImageView gameLogo;



    @FXML
    private void initialize() {
        setupTabChangeListeners();
        populateLeaderboards(TIC_TAC_TOE); // Load Tic Tac Toe by default
    }

    private void setupTabChangeListeners() {
        gameTabs.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue.getText()) {
                case "Tic Tac Toe":
                    populateLeaderboards(TIC_TAC_TOE);
                    break;
                case "Checkers":
                    populateLeaderboards(CHECKERS);
                    break;
                case "Connect 4":
                    populateLeaderboards(CONNECT_FOUR);
                    break;
            }
        });
    }

    private void populateLeaderboards(GameType gameType) {
        System.out.println("Populating leaderboard for game type: " + gameType);

        VBox content = new VBox(10);
        content.setStyle("-fx-alignment: center; -fx-padding: 20;");

        List<Player> players = null;

        // Manually populate each leaderboard before fetching scores
        switch (gameType) {
            case TIC_TAC_TOE:
                TicTacToeLeaderboard.getInstance().displayLeaderboard();
                players = TicTacToeLeaderboard.getInstance().getScores();
                break;
            case CHECKERS:
                CheckersLeaderboard.getInstance().displayLeaderboard();
                players = CheckersLeaderboard.getInstance().getScores();
                break;
            case CONNECT_FOUR:
                Connect4Leaderboard.getInstance().displayLeaderboard();
                players = Connect4Leaderboard.getInstance().getScores();
                break;
        }

        if (players == null || players.isEmpty()) {
            System.out.println("No players found for game type: " + gameType);
            Label noData = new Label("No players found.");
            noData.setStyle("-fx-text-fill: white; -fx-font-size: 16;");
            content.getChildren().add(noData);
        } else {
            int rank = 1;
            for (Player player : players) {
                String playerDetails = String.format(
                        "%d. Player ID: %d - Username: %s - Level: %d - MMR: %d",
                        rank, player.getUserID(), player.getUsername(), player.getLevel(), player.getMMR(gameType));
                Label entry = new Label(playerDetails);
                entry.setStyle("-fx-text-fill: white; -fx-font-size: 16;");
                content.getChildren().add(entry);
                rank++;
            }
        }

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(content);
        scrollPane.setFitToWidth(true);

        gameTabs.getSelectionModel().getSelectedItem().setContent(scrollPane);
    }

    @FXML
    private void handleBack() {
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/HomePage.fxml", "Home Page", "home.css");
    }
}
