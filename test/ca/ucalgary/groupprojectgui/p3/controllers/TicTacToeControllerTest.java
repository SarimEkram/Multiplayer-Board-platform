package ca.ucalgary.groupprojectgui.p3.controllers;

import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import networking.game.TurnTimer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import gameLogic.tictactoe.TicTacToeBoard;
import gameLogic.tictactoe.TicTacToe;
import MatchmakingLeaderboard.GameProcessor;
import MatchmakingLeaderboard.Player;

public class TicTacToeControllerTest {

    // Dummy subclass to bypass matchmaking integration.
    public class DummyTicTacToeController extends TicTacToeController {
        public char currentPlayer;

        @Override
        public void initialize() {
            // Create dummy players using the correct constructor parameters.
            this.localPlayer = new Player("Alice", 1, 1);
            this.opponentPlayer = new Player("Bob", 1, 2);

            controller.timerO = new TurnTimer("2",30);

            // Initialize game logic components.
            this.logicBoard = new TicTacToeBoard();
            this.gameLogic = new TicTacToe(this.logicBoard);
            this.gameLogic.start();

            // Create GameProcessor with valid player information.
            this.gameProcessor = new GameProcessor(localPlayer, opponentPlayer, gameType);

            // Set initial turn to 'X' and mark that playerXTurn is true.
            this.currentPlayer = 'X';
            this.playerXTurn = true;
            if (this.turnLabel != null) {
                this.turnLabel.setText("X: " + localPlayer.getUsername() + "'s Turn");
            }
            if (this.localPlayerLabel != null) {
                this.localPlayerLabel.setText(localPlayer.getUsername().toUpperCase());
            }
            if (this.opponentLabel != null) {
                this.opponentLabel.setText(opponentPlayer.getUsername().toUpperCase());
            }
            // Ensure boardContainer is initialized.
            if (this.boardContainer == null) {
                this.boardContainer = new StackPane();
            }
            // Create the game board.
            createBoard();
        }
    }

    private DummyTicTacToeController controller;

    // Use @BeforeAll to reliably initialize the JavaFX toolkit.
    @BeforeAll
    static void initJavaFX() throws InterruptedException {
        // Using a JFXPanel is a safe way to initialize the JavaFX toolkit.
        // If JavaFX is already started by your test runner, this will not re-initialize it.
        new JFXPanel();
        // A short sleep may help ensure the toolkit is fully ready.
        TimeUnit.MILLISECONDS.sleep(200);
    }

    @BeforeEach
    public void setUp() throws Exception {
        // Use the dummy subclass instead of the original controller.
        controller = new DummyTicTacToeController();

        // Set up the necessary UI components.
        // These fields are normally injected via FXML.
        controller.boardContainer = new StackPane();
        controller.tttgrid = new GridPane();
        controller.turnLabel = new Label();
        controller.timeElapsed = new Label();
        controller.chatHeader = new Label();
        controller.chatMessages = new javafx.scene.layout.VBox();
        controller.chatScrollPane = new javafx.scene.control.ScrollPane();
        controller.gameTitle = new Label();
        controller.leaveGame = new javafx.scene.control.Button();
        controller.localPlayerLabel = new Label();
        controller.opponentLabel = new Label();

        // Call the overridden initialize() from DummyTicTacToeController.
        controller.initialize();
    }

    @Test
    public void testInitializeSetsXTurn() {
        // The initialize() method should set playerXTurn to true and currentPlayer to 'X'.
        assertTrue(controller.playerXTurn, "playerXTurn should be true at initialization");
        assertEquals('X', controller.currentPlayer, "Initial current player should be 'X'");
    }

    @Test
    public void testClickOnEmptyCellPlacesX() {
        // Create an empty cell.
        StackPane cell = new StackPane();
        assertTrue(cell.getChildren().isEmpty(), "Cell should be empty initially");

        // Invoke the click handler on the empty cell at (row 0, col 2).
        Platform.runLater(() -> controller.handleCellClick(0, 2, cell));
        try {
            Thread.sleep(100); // Allow the JavaFX thread to process the event.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // The controller should draw an "X" using two Line nodes.
        long lineCount = cell.getChildren().stream()
                .filter(node -> node instanceof Line)
                .count();
        assertEquals(2, lineCount, "After clicking, the cell should have two Line nodes representing 'X'");

        // Verify that after the move, the turn switches (playerXTurn becomes false).
        assertFalse(controller.playerXTurn, "After an X move, playerXTurn should be false (i.e., O's turn)");
    }

    @Test
    public void testClickOnOccupiedCellDoesNothing() {
        // Create a cell that is already occupied.
        StackPane cell = new StackPane();
        Label dummyLabel = new Label("X");
        cell.getChildren().add(dummyLabel);
        int initialChildCount = cell.getChildren().size();

        Platform.runLater(() -> controller.handleCellClick(1, 1, cell));
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify that the cell's content remains unchanged.
        assertEquals(initialChildCount, cell.getChildren().size(),
                "Clicking on an occupied cell should not change its content");
    }

    @Test
    public void testResetGameClearsBoard() {
        // Simulate a board with a marked cell.
        StackPane cell = new StackPane();
        Label dummyLabel = new Label("X");
        cell.getChildren().add(dummyLabel);
        controller.tttgrid.add(cell, 0, 0);

        // Invoke createBoard() to simulate resetting the board.
        Platform.runLater(() -> controller.createBoard());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify that every cell in tttgrid is empty after resetting.
        for (Node node : controller.tttgrid.getChildren()) {
            StackPane cellNode = (StackPane) node;
            assertTrue(cellNode.getChildren().isEmpty(), "Cell should be empty after board reset");
        }

        // Verify that after reset, the game is set to start with X.
        assertTrue(controller.playerXTurn, "After reset, playerXTurn should be true");
        assertEquals('X', controller.currentPlayer, "After reset, the current player should be 'X'");
    }
}
