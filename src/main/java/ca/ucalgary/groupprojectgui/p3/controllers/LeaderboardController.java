package ca.ucalgary.groupprojectgui.p3.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import MatchmakingLeaderboard.Player;
import MatchmakingLeaderboard.Connect4.Leaderboard.Connect4Leaderboard;
import MatchmakingLeaderboard.TicTacToe.Leaderboard.TicTacToeLeaderboard;
import MatchmakingLeaderboard.Checkers.Leaderboard.CheckersLeaderboard;
import MatchmakingLeaderboard.GameType;
import static MatchmakingLeaderboard.GameType.*;

import ca.ucalgary.groupprojectgui.p3.SceneManager;
import java.util.List;

/**
 * Controller for managing the Leaderboard view.
 * <p>
 * This controller displays and switches between leaderboards for Tic Tac Toe,
 * Checkers, and Connect Four. It populates the corresponding leaderboard
 * UI components based on the game type selected.
 * </p>
 */
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

    /**
     * Initializes the Leaderboard view.
     * <p>
     * Sets the default leaderboard to Tic Tac Toe and adds a listener to the game toggle group
     * to switch between leaderboards.
     * </p>
     */
    @FXML
    public void initialize() {
        // Set default leaderboard as Tic Tac Toe.
        ticTacToeToggle.setSelected(true);
        openTicTacToeLeaderboard();

        // Add listener to toggle group to update leaderboard when selection changes.
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

    /**
     * Sets the visible leaderboard ScrollPane and hides the others.
     *
     * @param visiblePane the ScrollPane to be made visible.
     */
    private void setVisibleLeaderboard(ScrollPane visiblePane) {
        ticTacToeScroll.setVisible(false);
        checkersScroll.setVisible(false);
        connect4Scroll.setVisible(false);
        visiblePane.setVisible(true);
    }

    /**
     * Opens the Tic Tac Toe leaderboard.
     */
    public void openTicTacToeLeaderboard() {
        ticTacToeScroll.setVisible(true);
        checkersScroll.setVisible(false);
        connect4Scroll.setVisible(false);
        populateLeaderboard(TIC_TAC_TOE, ticTacToeLeaderboard);
    }

    /**
     * Opens the Checkers leaderboard.
     */
    public void openCheckersLeaderboard() {
        ticTacToeScroll.setVisible(false);
        checkersScroll.setVisible(true);
        connect4Scroll.setVisible(false);
        populateLeaderboard(CHECKERS, checkersLeaderboard);
    }

    /**
     * Opens the Connect Four leaderboard.
     */
    public void openConnect4Leaderboard() {
        ticTacToeScroll.setVisible(false);
        checkersScroll.setVisible(false);
        connect4Scroll.setVisible(true);
        populateLeaderboard(CONNECT_FOUR, connect4Leaderboard);
    }

    /**
     * Populates the given leaderboard VBox with the list of players and their scores based on the game type.
     *
     * @param gameType       the type of game for which the leaderboard is to be populated.
     * @param leaderboardBox the VBox container for the leaderboard entries.
     */
    private void populateLeaderboard(GameType gameType, VBox leaderboardBox) {
        // Clear any existing leaderboard entries.
        leaderboardBox.getChildren().clear();
        leaderboardBox.setPrefWidth(800);
        leaderboardBox.setAlignment(Pos.TOP_CENTER);

        // Retrieve players with scores based on the game type using a switch expression.
        List<Player> players = switch (gameType) {
            case TIC_TAC_TOE -> {
                // Display and fetch the Tic Tac Toe leaderboard.
                TicTacToeLeaderboard.getInstance().displayLeaderboard();
                yield TicTacToeLeaderboard.getInstance().getScores();
            }
            case CHECKERS -> {
                // Display and fetch the Checkers leaderboard.
                CheckersLeaderboard.getInstance().displayLeaderboard();
                yield CheckersLeaderboard.getInstance().getScores();
            }
            case CONNECT_FOUR -> {
                // Display and fetch the Connect Four leaderboard.
                Connect4Leaderboard.getInstance().displayLeaderboard();
                yield Connect4Leaderboard.getInstance().getScores();
            }
        };

        // If no players are found, display a message.
        if (players == null || players.isEmpty()) {
            Label noData = new Label("No players found.");
            noData.setStyle("-fx-text-fill: white; -fx-font-size: 16;");
            leaderboardBox.getChildren().add(noData);
            leaderboardBox.setPrefWidth(800);
            leaderboardBox.setAlignment(Pos.TOP_CENTER);
        } else {
            // Start ranking from 1.
            int rank = 1;
            // Iterate through the list of players and create a row for each.
            for (Player player : players) {
                // Get the player's ranking tier (e.g., Bronze, Silver, Gold).
                String rankTier = player.rankForPlayer(gameType);

                HBox row = new HBox(20);
                row.setAlignment(Pos.CENTER);
                row.getStyleClass().add("leaderboard-entry");

                // Create labels for rank, player ID, username, level, MMR, and rank tier.
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

                // Add all labels to the row.
                row.getChildren().addAll(rankLabel, idLabel, usernameLabel, levelLabel, mmrLabel, rankTierLabel);
                leaderboardBox.getChildren().add(row);
                rank++; // Increment rank for the next player.
            }
        }
    }

    /**
     * Handles the action for the Back button.
     * <p>
     * Switches the scene back to the Home page.
     * </p>
     */
    @FXML
    public void handleBack() {
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/views/homePage.fxml");
    }
}
