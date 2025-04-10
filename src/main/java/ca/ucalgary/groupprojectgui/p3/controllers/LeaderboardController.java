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

        // Add header row
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER);
        header.getStyleClass().add("leaderboard-data-header");

        Label rankHeader = new Label("Rank");
        rankHeader.setAlignment(Pos.CENTER);
        rankHeader.setMinWidth(40);

        Label idHeader = new Label("User ID");
        idHeader.setAlignment(Pos.CENTER);
        idHeader.setMinWidth(120);

        Label usernameHeader = new Label("Username");
        usernameHeader.setAlignment(Pos.CENTER);
        usernameHeader.setMinWidth(180);

        Label levelHeader = new Label("Level");
        levelHeader.setAlignment(Pos.CENTER);
        levelHeader.setMinWidth(100);

        Label mmrHeader = new Label("MMR");
        mmrHeader.setAlignment(Pos.CENTER);
        mmrHeader.setMinWidth(120);

        Label rankTierHeader = new Label("Rank Tier");
        rankTierHeader.setAlignment(Pos.CENTER);
        rankTierHeader.setMinWidth(150);

        header.getChildren().addAll(rankHeader, idHeader, usernameHeader, levelHeader, mmrHeader, rankTierHeader);
        leaderboardBox.getChildren().add(header);
        leaderboardBox.setPrefWidth(800);
        leaderboardBox.setAlignment(Pos.TOP_CENTER);

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
            leaderboardBox.setPrefWidth(800);
            leaderboardBox.setAlignment(Pos.TOP_CENTER);
        } else {
            int rank = 1;
            for (Player player : players) {
                String rankTier = player.rankForPlayer(gameType);

                HBox row = new HBox(20);
                row.setAlignment(Pos.CENTER);
                row.getStyleClass().add("leaderboard-entry");

                Label rankLabel = new Label(String.valueOf(rank));
                rankLabel.setMinWidth(40);

                Label idLabel = new Label(String.valueOf(player.getUserID()));
                idLabel.setAlignment(Pos.CENTER);
                idLabel.setMinWidth(120);

                Label usernameLabel = new Label(player.getUsername());
                usernameLabel.setAlignment(Pos.CENTER);
                usernameLabel.setMinWidth(180);

                Label levelLabel = new Label(String.valueOf(player.getLevel()));
                levelLabel.setAlignment(Pos.CENTER);
                levelLabel.setMinWidth(100);

                Label mmrLabel = new Label(String.valueOf(player.getMMR(gameType)));
                mmrLabel.setAlignment(Pos.CENTER);
                mmrLabel.setMinWidth(120);

                Label rankTierLabel = new Label(rankTier);
                rankTierLabel.setMinWidth(150);
                rankTierLabel.setAlignment(Pos.CENTER);

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