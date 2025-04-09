package ca.ucalgary.groupprojectgui.p3.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
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

    @FXML private VBox ticTacToeLeaderboard;
    @FXML private VBox checkersLeaderboard;
    @FXML private VBox connect4Leaderboard;
    @FXML private TabPane gameTabs;

    @FXML
    public void openTicTacToeLeaderboard() {
        populateLeaderboard(TIC_TAC_TOE, ticTacToeLeaderboard);
    }

    @FXML
    public void openCheckersLeaderboard() {
        populateLeaderboard(CHECKERS, checkersLeaderboard);
    }

    @FXML
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
                String tier = player.getRank(gameType).getCurrentTier().getRankName();

                Label rankLabel = new Label(rank + ".");
                Label idLabel = new Label("ID: " + player.getUserID());
                Label usernameLabel = new Label("Username: " + player.getUsername());
                Label levelLabel = new Label("Level: " + player.getLevel());
                Label mmrLabel = new Label("MMR: " + player.getMMR(gameType));
                Label tierLabel = new Label("Rank: " + tier);

                // Set fixed widths for alignment
                rankLabel.setPrefWidth(40);
                idLabel.setPrefWidth(120);
                usernameLabel.setPrefWidth(200);
                levelLabel.setPrefWidth(100);
                mmrLabel.setPrefWidth(100);
                tierLabel.setPrefWidth(100);

                for (Label label : new Label[]{rankLabel, idLabel, usernameLabel, levelLabel, mmrLabel, tierLabel}) {
                    label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                }

                HBox row = new HBox(20, rankLabel, idLabel, usernameLabel, levelLabel, mmrLabel, tierLabel);
                row.setStyle("-fx-alignment: center-left;");
                row.getStyleClass().add("leaderboard-entry");
                leaderboardBox.getChildren().add(row);
                rank++;
            }
        }
    }

    @FXML
    public void initialize() {
        gameTabs.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            switch (newTab.getText()) {
                case "Tic Tac Toe" -> openTicTacToeLeaderboard();
                case "Checkers" -> openCheckersLeaderboard();
                case "Connect 4" -> openConnect4Leaderboard();
            }
        });

        // Trigger default tab content population
        openTicTacToeLeaderboard();
    }

    @FXML
    private void handleBack() {
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/HomePage.fxml", "Home Page", "home.css");
    }
}
