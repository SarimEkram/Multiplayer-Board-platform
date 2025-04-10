package ca.ucalgary.groupprojectgui.p3.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
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

    public ImageView img;
    public HBox leaderboardHeader;
    public VBox leaderboardContainer;

    @FXML private ToggleGroup gameToggleGroup;
    @FXML private ToggleButton ticTacToeToggle;
    @FXML private ToggleButton checkersToggle;
    @FXML private ToggleButton connect4Toggle;

    @FXML private ScrollPane ticTacToeScroll;
    @FXML private ScrollPane checkersScroll;
    @FXML private ScrollPane connect4Scroll;

    @FXML private VBox ticTacToeLeaderboard;
    @FXML private VBox checkersLeaderboard;
    @FXML private VBox connect4Leaderboard;
    @FXML private TabPane gameTabs;
    @FXML private Button backButton;

    @FXML
    public void initialize() {
        ticTacToeToggle.setSelected(true); // default selected
        openTicTacToeLeaderboard();

        gameToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == ticTacToeToggle) {
                setVisibleLeaderboard(ticTacToeScroll);
                openTicTacToeLeaderboard();
            } else if (newToggle == checkersToggle) {
                setVisibleLeaderboard(checkersScroll);
                openCheckersLeaderboard();
            } else if (newToggle == connect4Toggle) {
                setVisibleLeaderboard(connect4Scroll);
                openConnect4Leaderboard();
            }
        });
    }
    private void setVisibleLeaderboard(ScrollPane visiblePane) {
        ticTacToeScroll.setVisible(false);
        checkersScroll.setVisible(false);
        connect4Scroll.setVisible(false);
        visiblePane.setVisible(true);
    }

    public void openTicTacToeLeaderboard() {
        ticTacToeScroll.setVisible(true);
        checkersScroll.setVisible(false);
        connect4Scroll.setVisible(false);
        populateLeaderboard(TIC_TAC_TOE, ticTacToeLeaderboard);
    }

    public void openCheckersLeaderboard() {
        ticTacToeScroll.setVisible(false);
        checkersScroll.setVisible(true);
        connect4Scroll.setVisible(false);
        populateLeaderboard(CHECKERS, checkersLeaderboard);
    }

    public void openConnect4Leaderboard() {
        ticTacToeScroll.setVisible(false);
        checkersScroll.setVisible(false);
        connect4Scroll.setVisible(true);
        populateLeaderboard(CONNECT_FOUR, connect4Leaderboard);
    }

    private void populateLeaderboard(GameType gameType, VBox leaderboardBox) {
        leaderboardBox.getChildren().clear();
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
                rankLabel.setAlignment(Pos.CENTER);
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