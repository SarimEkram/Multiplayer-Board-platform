package ca.ucalgary.groupprojectgui.p3.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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

    @FXML private VBox ticTacToeLeaderboard;
    @FXML private VBox checkersLeaderboard;
    @FXML private VBox connect4Leaderboard;
    @FXML private TabPane gameTabs;
    @FXML private Button backButton;

    @FXML
    public void initialize() {
        gameTabs.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            switch (newTab.getText()) {
                case "Tic Tac Toe" -> openTicTacToeLeaderboard();
                case "Checkers" -> openCheckersLeaderboard();
                case "Connect 4" -> openConnect4Leaderboard();
            }
        });

        openTicTacToeLeaderboard();
    }

    public void openTicTacToeLeaderboard() {
        populateLeaderboard(TIC_TAC_TOE, ticTacToeLeaderboard);
    }

    public void openCheckersLeaderboard() {
        populateLeaderboard(CHECKERS, checkersLeaderboard);
    }

    public void openConnect4Leaderboard() {
        populateLeaderboard(CONNECT_FOUR, connect4Leaderboard);
    }

    private void populateLeaderboard(GameType gameType, VBox leaderboardBox) {
        leaderboardBox.getChildren().clear();

        List<Player> players = switch (gameType) {
            case TIC_TAC_TOE -> {
                TicTacToeLeaderboard.getInstance().displayLeaderboard();
                yield TicTacToeLeaderboard.getInstance().getScores();
            }
            case CHECKERS -> {
                CheckersLeaderboard.getInstance().displayLeaderboard();
                yield CheckersLeaderboard.getInstance().getScores();
            }
            case CONNECT_FOUR -> {
                Connect4Leaderboard.getInstance().displayLeaderboard();
                yield Connect4Leaderboard.getInstance().getScores();
            }
        };

        if (players == null || players.isEmpty()) {
            Label noData = new Label("No players found.");
            noData.setStyle("-fx-text-fill: white; -fx-font-size: 16;");
            leaderboardBox.getChildren().add(noData);
        } else {
            int rank = 1;
            for (Player player : players) {
                String rankTier = player.rankForPlayer(gameType);

                HBox row = new HBox(20);
                row.setAlignment(Pos.CENTER_LEFT);
                row.getStyleClass().add("leaderboard-entry");

                Label rankLabel = new Label(rank + ".");
                rankLabel.setMinWidth(40);
                rankLabel.setAlignment(Pos.CENTER_LEFT);

                Label idLabel = new Label("ID: " + player.getUserID());
                idLabel.setMinWidth(120);

                Label usernameLabel = new Label("Username: " + player.getUsername());
                usernameLabel.setMinWidth(180);

                Label levelLabel = new Label("Level: " + player.getLevel());
                levelLabel.setMinWidth(100);

                Label mmrLabel = new Label("MMR: " + player.getMMR(gameType));
                mmrLabel.setMinWidth(120);

                Label rankTierLabel = new Label("Rank: " + rankTier);
                rankTierLabel.setMinWidth(150);
                HBox.setHgrow(rankTierLabel, Priority.ALWAYS);

                row.getChildren().addAll(rankLabel, idLabel, usernameLabel, levelLabel, mmrLabel, rankTierLabel);
                leaderboardBox.getChildren().add(row);
                rank++;
            }
        }
    }

    @FXML
    public void handleBack() {
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/HomePage.fxml", "Home Page", "home.css");
    }
}