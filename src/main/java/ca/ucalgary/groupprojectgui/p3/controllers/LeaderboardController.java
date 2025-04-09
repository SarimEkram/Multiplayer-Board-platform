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
    private ImageView gameLogo;

    @FXML
    private VBox ticTacToeLeaderboardPane;

    @FXML
    private VBox checkersLeaderboardPane;

    @FXML
    private VBox connect4LeaderboardPane;

    @FXML
    public void openTicTacToeLeaderboard() {
        hideAllLeaderboards();
        populateLeaderboards(TIC_TAC_TOE);
        ticTacToeLeaderboardPane.setVisible(true);
        ticTacToeLeaderboardPane.setManaged(true);
    }

    @FXML
    public void openCheckersLeaderboard() {
        hideAllLeaderboards();
        populateLeaderboards(CHECKERS);
        checkersLeaderboardPane.setVisible(true);
        checkersLeaderboardPane.setManaged(true);
    }

    @FXML
    public void openConnect4Leaderboard() {
        hideAllLeaderboards();
        populateLeaderboards(CONNECT_FOUR);
        connect4LeaderboardPane.setVisible(true);
        connect4LeaderboardPane.setManaged(true);
    }

    private void hideAllLeaderboards() {
        ticTacToeLeaderboardPane.setVisible(false);
        ticTacToeLeaderboardPane.setManaged(false);

        checkersLeaderboardPane.setVisible(false);
        checkersLeaderboardPane.setManaged(false);

        connect4LeaderboardPane.setVisible(false);
        connect4LeaderboardPane.setManaged(false);
    }

    private void populateLeaderboards(GameType gameType) {
        VBox targetPane = switch (gameType) {
            case TIC_TAC_TOE -> ticTacToeLeaderboardPane;
            case CHECKERS -> checkersLeaderboardPane;
            case CONNECT_FOUR -> connect4LeaderboardPane;
        };

        targetPane.getChildren().clear();

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
            targetPane.getChildren().add(noData);
        } else {
            int rank = 1;
            for (Player player : players) {
                String rankTier = player.getRank(gameType).getCurrentTier().getRankName();
                String entryText = String.format( "%d. Player ID: %d | Username: %s | Level: %d | MMR: %d | Rank: %s",
                        rank, player.getUserID(), player.getUsername(), player.getLevel(), player.getMMR(gameType), rankTier);
                Label entry = new Label(entryText);
                entry.setStyle("-fx-text-fill: white; -fx-font-size: 16;");
                targetPane.getChildren().add(entry);
                rank++;
            }
        }
    }

    @FXML
    private void handleBack() {
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/HomePage.fxml", "Home Page", "home.css");
    }
}
