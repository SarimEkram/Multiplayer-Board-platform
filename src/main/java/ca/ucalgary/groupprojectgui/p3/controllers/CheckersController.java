package ca.ucalgary.groupprojectgui.p3.controllers;

import MatchmakingLeaderboard.Checkers.CheckersMatchmaking;
import MatchmakingLeaderboard.GameProcessor;
import MatchmakingLeaderboard.GameType;
import MatchmakingLeaderboard.Player;
import MatchmakingLeaderboard.PlayerDatabase;
import ca.ucalgary.groupprojectgui.p3.Fonts;
import gameLogic.checkers.Checkers;
import gameLogic.checkers.CheckersBoard;
import gameLogic.checkers.CheckersPiece;
import gameLogic.checkers.CheckersMove;
import gameLogic.checkers.CheckersMove.Move;
import networking.chat.InGameChat;
import networking.chat.ChatMessage;
import networking.game.TurnTimer;
import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static javafx.scene.paint.Color.rgb;

/**
 * Controller for the Checkers game scene. Manages the board UI, user input,
 * match-making, timers, chat, and result processing.
 */
public class CheckersController {

    // -----------------------------
    // FXML-INJECTED UI COMPONENTS
    // -----------------------------

    /** A visual indicator for which player's turn it is (black or white piece). */
    @FXML public Circle turnPiece;

    /** Container for chat messages. */
    @FXML public VBox chatMessages;

    /** Scroll pane for the chat messages. */
    @FXML public ScrollPane chatScrollPane;

    /** A stylized header label for the chat. */
    @FXML public Label chatHeader;

    /** Button that allows the current user to leave the game. */
    @FXML public Button leaveGame;

    /** Title label for the game scene ("OMG CHECKERS"). */
    @FXML public Label gameTitle;

    /** Label that shows the elapsed time in the current turn. */
    @FXML public Label timeElapsed;

    /** TextField for entering chat messages. */
    @FXML public TextField chatInput;

    /** Label that shows which player's turn it is (e.g., "Black's TURN"). */
    @FXML public Label turnLabel;

    /** Main container (StackPane) that holds the checkers board. */
    @FXML public StackPane boardContainer;

    /** Sample circle used for styling Player 1's checker pieces. */
    @FXML public Circle checkerCircle1;

    /** Sample circle used for styling Player 2's checker pieces. */
    @FXML public Circle checkerCircle2;

    /** Label that displays the local player's (black) name. */
    @FXML public Label player1Name;

    /** Label that displays the opponent's (white) name. */
    @FXML public Label player2Name;

    // ------------------------------------
    // PRIVATE FIELDS AND GAME INFRASTRUCTURE
    // ------------------------------------

    /** A matchmaking object for handling checkers game queueing. */
    @FXML private CheckersMatchmaking matchmaking;

    /** The local player's data. */
    @FXML private Player localPlayer;

    /** The opponent's player data. */
    @FXML private Player opponentPlayer;

    // Board constants
    private static final int BOARD_ROWS = 8;
    private static final int BOARD_COLUMNS = 8;
    private static final double BOARD_MARGIN = 15.0;

    /** Underlying grid to display checker squares and pieces. */
    private GridPane boardGrid;

    /** A rectangular background for the board. */
    private Rectangle boardBackground;

    /** Core model of the checkers board. */
    private CheckersBoard checkersBoard;

    /** Checkers logic object that processes moves and turns. */
    private Checkers gameLogic;

    /** 2D array storing references to each cell's StackPane on the board UI. */
    public StackPane[][] cellPanes = new StackPane[BOARD_ROWS][BOARD_COLUMNS];

    /** Represents a selected piece on the board (row and col). */
    private Position selectedPiecePosition = null;

    /** Indicates whether the user has selected a piece to move. */
    private boolean isPieceSelected = false;

    /** A list of valid moves for the currently selected piece. */
    private ArrayList<Move> validMoves = new ArrayList<>();

    /** Processor for updating game results in the database. */
    private GameProcessor gameProcessor;

    /** The game type enumeration (CHECKERS). */
    private final GameType gameType = GameType.CHECKERS;

    /** Timeline object used to track per-turn time in the GUI. */
    private Timeline timeline;

    /** Count of seconds elapsed in the current turn (for UI display). */
    private int secondsElapsed = 0;

    /** The networking-based in-game chat session. */
    public InGameChat chatSession;

    /** Turn timer for the white player. */
    private TurnTimer timerWhite;

    /** Turn timer for the black player. */
    private TurnTimer timerBlack;

    /** Timeline for periodically checking turn timers. */
    private Timeline turnCheckTimeline;

    /** Flags to track if we have given the "10 seconds left" warning. */
    private boolean warningSentWhite = false;
    private boolean warningSentBlack = false;

    /** CSV file name to store chat logs. */
    private static final String CHECKERS_CHAT_CSV = "checkersChatHistory.csv";

    /**
     * A simple utility class for storing row/column positions on the board.
     */
    private static class Position {
        int row, col;

        Position(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    /**
     * Initializes the CheckersController after loading the FXML.
     * Sets up matchmaking, board UI, chat system, and timers.
     */
    @FXML
    public void initialize() {
        // Apply stylized header text
        setupHeaderWithSpacing();
        gameTitle.setText("OMG CHECKERS");

        // Matchmaking setup
        matchmaking = new CheckersMatchmaking();

        // If no friend-based match is provided, do normal matchmaking
        if (HomePageController.friendOpponentID == -1) {
            try {
                matchmaking.matchmakingConnect();
                localPlayer = PlayerDatabase.getPlayerByUserID(LoginController.loginId);
                matchmaking.joinQueue(localPlayer);

                // Attempt to match with all other relevant players
                for (Player player : PlayerDatabase.getAllPlayers()) {
                    if (player.getGameSignal(gameType) == gameType.getGameCode()) {
                        matchmaking.joinQueue(player);
                    }
                }
                opponentPlayer = matchmaking.findOpponent(localPlayer.getUserID());
            } catch (IOException e) {
                addMessage("SYSTEM", "Matchmaking error: " + e.getMessage(), true);
            }
        } else {
            // If a friend-based match is specified, retrieve relevant players
            localPlayer = PlayerDatabase.getPlayerByUserID(LoginController.loginId);
            opponentPlayer = PlayerDatabase.getPlayerByUserID(HomePageController.friendOpponentID);
        }

        // Initialize backend logic
        checkersBoard = new CheckersBoard();
        gameLogic = new Checkers(checkersBoard);
        gameLogic.start();

        // Create the board UI
        createBoard();

        // Update labels with player names
        player1Name.setText(localPlayer != null ? localPlayer.getUsername().toUpperCase() : "PLAYER 1");
        player1Name.setPadding(new Insets(5, 10, 5, 10));
        player1Name.setFont(Fonts.rajdhaniBold(16));

        player2Name.setText(opponentPlayer != null ? opponentPlayer.getUsername().toUpperCase() : "PLAYER 2");
        player2Name.setPadding(new Insets(5, 10, 5, 10));
        player2Name.setFont(Fonts.rajdhaniBold(16));

        // Game processor for updating results
        gameProcessor = new GameProcessor(localPlayer, opponentPlayer, gameType);

        // Start the per-turn timer for the GUI
        startTimer();

        // Set up the chat session
        String sessionId = localPlayer.getUserID() + "_vs_" + opponentPlayer.getUserID();
        chatSession = new InGameChat(sessionId);
        chatSession.establishConnection();
        initializeChat();
        clearChatHistoryCSV();

        // Send a message on ENTER keypress
        chatInput.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                onSendMessage();
            }
        });

        // Initialize both turn timers (30s each)
        timerBlack = new TurnTimer(localPlayer.getUsername(), 30);  // Local player is black
        timerWhite = new TurnTimer(opponentPlayer.getUsername(), 30);  // Opponent is white

        // Start periodically checking the timers
        startTurnTimer();

        // Black always goes first in Checkers
        timerBlack.startTimer();
    }

    /**
     * Dynamically creates the checkers board UI based on the internal CheckersBoard data.
     * Initializes the grid, background, square colors, and sets up event handlers.
     */
    public void createBoard() {
        // Example image (for kings)
        Image crown = new Image(getClass().getResourceAsStream(
                "/ca/ucalgary/groupprojectgui/p3/images/crown.png"));
        ImageView crownImage = new ImageView(crown);
        crownImage.setFitWidth(45);
        crownImage.setFitHeight(30);
        crownImage.setVisible(false);

        boardGrid = new GridPane();
        boardGrid.setAlignment(Pos.CENTER);

        boardBackground = new Rectangle();
        boardBackground.getStyleClass().add("board-background");

        // Build board squares and store them in cellPanes
        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int col = 0; col < BOARD_COLUMNS; col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(66, 66);

                // Each square gets a distinct background color
                Rectangle square = new Rectangle(66, 66);
                square.getStyleClass().add("board-square");

                // Alternate color pattern
                if ((row + col) % 2 == 0) {
                    square.setFill(Color.valueOf("#16858c"));
                    square.setStroke(Color.rgb(0, 255, 255, 0.3));
                } else {
                    square.setFill(Color.valueOf("#a7759c"));
                    square.setStroke(Color.rgb(255, 0, 255, 0.3));
                }
                square.setStrokeWidth(2);

                // Check if this square should contain an initial checker (based on row)
                if (row < 3 && (row + col) % 2 == 1) {
                    Circle whitePiece = new Circle(31);
                    whitePiece.getStyleClass().addAll(checkerCircle1.getStyleClass());
                    cell.getChildren().add(whitePiece);
                } else if (row > 4 && (row + col) % 2 == 1) {
                    Circle blackPiece = new Circle(31);
                    blackPiece.getStyleClass().addAll(checkerCircle2.getStyleClass());
                    cell.getChildren().add(blackPiece);
                }

                // Subtle shadow effect
                DropShadow shadow = new DropShadow();
                shadow.setRadius(3);
                shadow.setOffsetX(1);
                shadow.setOffsetY(1);
                shadow.setColor(Color.rgb(0, 0, 0, 0.25));
                square.setEffect(shadow);

                // Add a small margin around each tile
                GridPane.setMargin(square, new Insets(1));
                cell.getChildren().add(square);

                // Hover animation
                square.setOnMouseEntered(e -> animateHover(square, 1.05));
                square.setOnMouseExited(e -> animateHover(square, 1.0));

                // Store this cell in the 2D array
                cellPanes[row][col] = cell;
                final int currentRow = row;
                final int currentCol = col;

                // On-click, try to move or select a piece
                cell.setOnMouseClicked(e -> handleCellClick(currentRow, currentCol));

                // Hover highlights valid moves
                cell.setOnMouseEntered(e -> handleCellHoverEnter(currentRow, currentCol));
                cell.setOnMouseExited(e -> handleCellHoverExit(currentRow, currentCol));

                boardGrid.add(cell, col, row);
            }
        }

        boardContainer.getChildren().addAll(boardBackground, boardGrid);

        // Dynamically resize the board layout when the container size changes
        boardContainer.widthProperty().addListener((obs, oldVal, newVal) -> updateBoardLayout());
        boardContainer.heightProperty().addListener((obs, oldVal, newVal) -> updateBoardLayout());

        // Update turn indicator
        updateTurnIndicator();

        // Hook up a "leave game" confirmation
        if (leaveGame != null) {
            leaveGame.setOnAction(e -> onLeaveGame());
        }
    }

    /**
     * Starts or restarts a Timeline that updates the 'timeElapsed' label every second.
     */
    private void startTimer() {
        if (timeline != null) {
            timeline.stop();
        }
        timeElapsed.setText(String.format("⏳ TURN TIME: %02d:%02d", secondsElapsed / 60, secondsElapsed % 60));

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsElapsed++;
            int minutes = secondsElapsed / 60;
            int seconds = secondsElapsed % 60;
            timeElapsed.setText(String.format("⏳ TURN TIME: %02d:%02d", minutes, seconds));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Stops the GUI turn timer.
     */
    private void stopTimer() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    /**
     * Dynamically updates the board layout (square sizes) when the container resizes.
     */
    private void updateBoardLayout() {
        double containerWidth = boardContainer.getWidth();
        double containerHeight = boardContainer.getHeight();
        if (containerWidth <= 0 || containerHeight <= 0) return;

        double boardSize = Math.min(containerWidth, containerHeight) * 0.9;
        boardBackground.setWidth(boardSize);
        boardBackground.setHeight(boardSize);

        double cellSize = (boardSize - 2 * BOARD_MARGIN) / BOARD_COLUMNS;

        // Resize squares to match container changes
        for (Node node : boardGrid.getChildren()) {
            if (node instanceof Rectangle) {
                ((Rectangle) node).setWidth(cellSize);
                ((Rectangle) node).setHeight(cellSize);
            }
        }
    }

    /**
     * Scales the specified square to achieve a simple "zoom on hover" effect.
     *
     * @param square the square (Rectangle) to animate
     * @param scale  the target scale factor
     */
    private void animateHover(Rectangle square, double scale) {
        ScaleTransition st = new ScaleTransition(Duration.millis(150), square);
        st.setToX(scale);
        st.setToY(scale);
        st.play();
    }

    /**
     * Shows a confirmation pop-up asking if the user really wants to leave the game.
     */
    private void onLeaveGame() {
        Pane parent = (Pane) boardGrid.getParent();
        StackPane overlay = new StackPane();
        overlay.getStyleClass().add("popup-overlay");
        overlay.setPrefSize(parent.getWidth(), parent.getHeight());

        VBox popup = new VBox();
        popup.getStyleClass().add("popup-dialog");
        popup.setAlignment(Pos.CENTER);
        popup.setSpacing(15);
        popup.setPadding(new Insets(20));

        Text title = new Text("Confirm Quit");
        title.getStyleClass().add("popup-title");

        Text message = new Text("Are you sure you want to quit the game?");
        message.getStyleClass().add("popup-message");

        Button yesButton = new Button("Yes");
        yesButton.getStyleClass().add("popup-button");
        yesButton.setOnAction(e -> {
            parent.getChildren().remove(overlay);
            SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/views/homePage.fxml");
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("popup-button");
        cancelButton.setOnAction(e -> parent.getChildren().remove(overlay));

        HBox buttonBox = new HBox(15, yesButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);
        popup.getChildren().addAll(title, message, buttonBox);
        overlay.getChildren().add(popup);
        parent.getChildren().add(overlay);
    }

    /**
     * Handles a cell-click event on the board. Either selects a piece or attempts
     * to move it if a piece is already selected.
     *
     * @param row the row of the clicked cell
     * @param col the column of the clicked cell
     */
    private void handleCellClick(int row, int col) {
        // If a piece is already selected, check if the clicked cell is a valid destination
        if (isPieceSelected) {
            Move selectedMove = null;
            for (Move move : validMoves) {
                if (move.destRow == row && move.destCol == col) {
                    selectedMove = move;
                    break;
                }
            }

            // If valid, execute the move
            if (selectedMove != null) {
                CheckersPiece selectedPiece =
                        checkersBoard.board[selectedPiecePosition.row][selectedPiecePosition.col];
                gameLogic.processMove(selectedPiece,
                        selectedPiecePosition.row,
                        selectedPiecePosition.col,
                        selectedMove);

                clearHighlights();
                isPieceSelected = false;
                selectedPiecePosition = null;
                validMoves.clear();
                updateBoardUI();

                // Check for a winner
                Checkers.WINNER winner = gameLogic.checkWin();
                if (winner == Checkers.WINNER.NONE) {
                    // If no one has won yet, check if the current player can move
                    if (!hasAnyValidMovesForTurn(gameLogic.getTurn())) {
                        /*
                         * If they have no valid moves, they lose automatically,
                         * so the other player wins.
                         */
                        if (gameLogic.getTurn() == Checkers.Turn.WHITE) {
                            turnPiece.getStyleClass().clear();
                            turnPiece.getStyleClass().add("checker-black");
                            turnLabel.setText(opponentPlayer.getUsername() + " wins!");
                        } else {
                            turnPiece.getStyleClass().clear();
                            turnPiece.getStyleClass().add("checker-white");
                            turnLabel.setText(opponentPlayer.getUsername() + " wins!");
                        }
                        boardGrid.setDisable(true);
                        return;
                    }
                }

                // If there's a definite winner, show pop-up and update results
                if (winner != Checkers.WINNER.NONE) {
                    checkForWinnerAndShowLabel();
                    stopTimer();
                    return;
                } else {
                    // Otherwise, update turn indicator
                    updateTurnIndicator();

                    // Reset GUI clock
                    secondsElapsed = 0;
                    startTimer();

                    // Reset warning flags
                    warningSentBlack = false;
                    warningSentWhite = false;

                    // Start the new turn's timer
                    if (gameLogic.getTurn() == Checkers.Turn.BLACK) {
                        timerBlack.resetTimer();
                        timerBlack.startTimer();
                    } else {
                        timerWhite.resetTimer();
                        timerWhite.startTimer();
                    }
                }
                return;
            } else {
                // If the click wasn't a valid move, just clear and reset
                clearHighlights();
                isPieceSelected = false;
                selectedPiecePosition = null;
                validMoves.clear();
            }
        }

        // If no piece is selected yet, check if the clicked cell has a piece belonging to the current turn
        CheckersPiece piece = checkersBoard.board[row][col];
        if (piece != null) {
            if (!isPieceOfCurrentTurn(piece)) {
                return;
            }
            selectedPiecePosition = new Position(row, col);
            isPieceSelected = true;
            validMoves.clear();

            // Collect all available moves for that piece
            Move[] moves = CheckersMove.availableMoves(checkersBoard, piece, row, col);
            for (Move move : moves) {
                validMoves.add(move);
                highlightValidMoveCell(move.destRow, move.destCol);
            }
            highlightSelectedPieceCell(row, col);
        }
    }

    /**
     * Handles cell hover entry, highlighting valid moves if the hovered piece
     * belongs to the current turn.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     */
    private void handleCellHoverEnter(int row, int col) {
        if (!isPieceSelected) {
            CheckersPiece piece = checkersBoard.board[row][col];
            if (piece != null && isPieceOfCurrentTurn(piece)) {
                Move[] moves = CheckersMove.availableMoves(checkersBoard, piece, row, col);
                for (Move move : moves) {
                    highlightValidMoveCell(move.destRow, move.destCol);
                }
                highlightSelectedPieceCell(row, col);
            }
        }
    }

    /**
     * Handles cell hover exit, clearing any temporary highlights
     * if no piece is currently selected.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     */
    private void handleCellHoverExit(int row, int col) {
        if (!isPieceSelected) {
            clearHighlights();
        }
    }

    /**
     * Checks if the specified piece belongs to the current player's turn.
     *
     * @param piece the piece to check
     * @return true if the piece matches the current turn color; false otherwise
     */
    private boolean isPieceOfCurrentTurn(CheckersPiece piece) {
        Checkers.Turn currentTurn = gameLogic.getTurn();
        return (currentTurn == Checkers.Turn.WHITE && piece.getColour() == CheckersPiece.Colour.WHITE)
                || (currentTurn == Checkers.Turn.BLACK && piece.getColour() == CheckersPiece.Colour.BLACK);
    }

    /**
     * Determines if at least one piece belonging to the specified turn
     * has a valid move available.
     *
     * @param turn the turn to check (BLACK or WHITE)
     * @return true if there's a piece that can move; false otherwise
     */
    private boolean hasAnyValidMovesForTurn(Checkers.Turn turn) {
        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int col = 0; col < BOARD_COLUMNS; col++) {
                CheckersPiece piece = checkersBoard.board[row][col];
                if (piece != null) {
                    if ((turn == Checkers.Turn.WHITE && piece.getColour() == CheckersPiece.Colour.WHITE)
                            || (turn == Checkers.Turn.BLACK && piece.getColour() == CheckersPiece.Colour.BLACK)) {
                        CheckersMove.Move[] moves = CheckersMove.availableMoves(checkersBoard, piece, row, col);
                        if (moves.length > 0) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Highlights a cell to indicate it is a valid destination move.
     *
     * @param row the row of the cell to highlight
     * @param col the column of the cell to highlight
     */
    private void highlightValidMoveCell(int row, int col) {
        Rectangle tint = new Rectangle();
        tint.setFill(Color.rgb(255, 255, 0, 0.3));
        tint.setMouseTransparent(true);
        tint.widthProperty().bind(cellPanes[row][col].widthProperty());
        tint.heightProperty().bind(cellPanes[row][col].heightProperty());

        if (cellPanes[row][col].getChildren().size() > 0) {
            cellPanes[row][col].getChildren().add(1, tint);
        } else {
            cellPanes[row][col].getChildren().add(tint);
        }
    }

    /**
     * Highlights the cell containing the currently selected piece.
     *
     * @param row the row of the piece
     * @param col the column of the piece
     */
    private void highlightSelectedPieceCell(int row, int col) {
        Rectangle tint = new Rectangle();
        tint.setFill(Color.rgb(0, 0, 255, 0.3));
        tint.setMouseTransparent(true);
        tint.widthProperty().bind(cellPanes[row][col].widthProperty());
        tint.heightProperty().bind(cellPanes[row][col].heightProperty());

        if (cellPanes[row][col].getChildren().size() > 0) {
            cellPanes[row][col].getChildren().add(1, tint);
        } else {
            cellPanes[row][col].getChildren().add(tint);
        }
    }

    /**
     * Clears all highlighted cells (both valid-move highlights and selected-piece highlights).
     */
    public void clearHighlights() {
        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int col = 0; col < BOARD_COLUMNS; col++) {
                cellPanes[row][col].getChildren().removeIf(node -> {
                    if (node instanceof Rectangle) {
                        Rectangle rect = (Rectangle) node;
                        Color fill = (Color) rect.getFill();
                        return fill.equals(Color.rgb(255, 255, 0, 0.3))
                                || fill.equals(Color.rgb(0, 0, 255, 0.3));
                    }
                    return false;
                });
            }
        }
    }

    /**
     * Rebuilds the UI representation of the board based on the CheckersBoard model.
     * Removes stale pieces and images, then adds new circles for each piece.
     */
    private void updateBoardUI() {
        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int col = 0; col < BOARD_COLUMNS; col++) {
                StackPane cell = cellPanes[row][col];
                // Remove any existing piece/crown
                cell.getChildren().removeIf(node -> node instanceof Circle || node instanceof ImageView);

                CheckersPiece piece = checkersBoard.board[row][col];
                if (piece != null) {
                    // Create a circle for the piece
                    Circle pieceCircle = new Circle(31);
                    if (piece.getColour() == CheckersPiece.Colour.WHITE) {
                        pieceCircle.getStyleClass().addAll(checkerCircle2.getStyleClass());
                    } else {
                        pieceCircle.getStyleClass().addAll(checkerCircle1.getStyleClass());
                    }
                    cell.getChildren().add(pieceCircle);

                    // If it's a king, show the crown image
                    if (piece.isKing()) {
                        Image crownImage = new Image(getClass().getResourceAsStream(
                                "/ca/ucalgary/groupprojectgui/p3/images/crown.png"));
                        ImageView crownView = new ImageView(crownImage);
                        crownView.setFitWidth(45);
                        crownView.setFitHeight(30);
                        cell.getChildren().add(crownView);

                        // Animate the crowning only once
                        if (!piece.hasAnimatedKing) {
                            ScaleTransition scale = new ScaleTransition(Duration.millis(500), crownView);
                            scale.setFromX(0);
                            scale.setFromY(0);
                            scale.setToX(1);
                            scale.setToY(1);

                            RotateTransition rotate = new RotateTransition(Duration.millis(500), crownView);
                            rotate.setByAngle(360);

                            scale.play();
                            rotate.play();
                            piece.setHasAnimatedKing(true);
                        }
                    }
                }
            }
        }
    }

    /**
     * Updates the turn indicator (circle color and label) to reflect the current turn (black/white).
     */
    public void updateTurnIndicator() {
        Checkers.Turn currentTurn = gameLogic.getTurn();
        if (currentTurn == Checkers.Turn.BLACK) {
            turnPiece.getStyleClass().clear();
            turnPiece.getStyleClass().add("checker-black");
            turnLabel.setText("Black's TURN");
        } else if (currentTurn == Checkers.Turn.WHITE) {
            turnPiece.getStyleClass().clear();
            turnPiece.getStyleClass().add("checker-white");
            turnLabel.setText("White's TURN");
        }
    }

    /**
     * Checks for a winner using Checkers logic. If there is one, shows the
     * game over pop-up and updates the results in the database.
     */
    private void checkForWinnerAndShowLabel() {
        Checkers.WINNER winner = gameLogic.checkWin();
        if (winner != Checkers.WINNER.NONE) {
            if (winner == Checkers.WINNER.WHITE) {
                showGameOverPopup(opponentPlayer.getUsername(), true);
                // Opponent (white) wins
                gameProcessor.UpdateResults(opponentPlayer, localPlayer, gameType);
            } else {
                showGameOverPopup(localPlayer.getUsername(), true);
                // Local player (black) wins
                gameProcessor.UpdateResults(localPlayer, opponentPlayer, gameType);
            }
            boardGrid.setDisable(true);
        }
    }

    /**
     * Displays a pop-up indicating the game is over, with the specified winner
     * or a draw result. Offers a button to go back to the main menu.
     *
     * @param winner the winner's username
     * @param isWin  whether there's a decisive winner (true) or a draw (false)
     */
    private void showGameOverPopup(String winner, boolean isWin) {
        leaveGame.setVisible(false);
        StackPane overlay = new StackPane();
        overlay.getStyleClass().add("popup-overlay");
        overlay.setPrefSize(boardGrid.getWidth(), boardGrid.getHeight());

        VBox popup = new VBox();
        popup.getStyleClass().add("popup-dialog");
        popup.setAlignment(Pos.CENTER);
        popup.setSpacing(10);
        popup.setPadding(new Insets(20));

        Text title = new Text("Game Over");
        title.getStyleClass().add("popup-title");

        Text message = new Text();
        message.getStyleClass().add("popup-message");
        if (isWin) {
            message.setText("Winner: " + winner);
        } else {
            message.setText("It's a draw!");
        }

        Button mainMenuButton = new Button("Main Menu");
        mainMenuButton.getStyleClass().add("popup-button");
        mainMenuButton.setOnAction(e -> {
            boardGrid.getChildren().remove(overlay);
            SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/views/homePage.fxml");
        });

        popup.getChildren().addAll(title, message, mainMenuButton);
        overlay.getChildren().add(popup);
        ((Pane) boardGrid.getParent()).getChildren().add(overlay);
    }

    /**
     * Creates a stylized header for the chat panel by spacing out letters,
     * applying neon-like effects, and adding an underline.
     */
    private void setupHeaderWithSpacing() {
        chatHeader.setText("");
        chatHeader.setAlignment(Pos.CENTER);
        chatHeader.setMaxWidth(Double.MAX_VALUE);

        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(0, 0, 10, 0));

        HBox textContainer = new HBox(2);
        textContainer.setAlignment(Pos.CENTER);

        String headerText = "OMG NETWORK";
        for (char c : headerText.toCharArray()) {
            Text letter = new Text(String.valueOf(c));
            letter.setFont(Fonts.orbitron(FontWeight.NORMAL, 24));
            letter.setFill(Color.WHITE);

            // Neon-like effect
            letter.setEffect(new DropShadow(5, rgb(0, 255, 255)));
            letter.setEffect(new DropShadow(10, rgb(0, 255, 255)));

            textContainer.getChildren().add(letter);
        }

        // A gradient underline
        Rectangle underline = new Rectangle(150, 2);
        underline.setFill(new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),
                new Stop(0.3, rgb(255, 0, 255)),
                new Stop(0.7, rgb(0, 255, 255)),
                new Stop(1, Color.TRANSPARENT)
        ));

        VBox.setMargin(underline, new Insets(5, 0, 0, 0));
        container.getChildren().addAll(textContainer, underline);

        chatHeader.setGraphic(container);
        chatHeader.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    /**
     * Initializes the chat area, applying any needed CSS and adding
     * initial system messages.
     */
    private void initializeChat() {
        try {
            URL cssUrl = getClass().getResource("/ca/ucalgary/groupprojectgui/p3/styles/checkers.css");
            if (cssUrl != null) {
                chatMessages.getStylesheets().add(cssUrl.toExternalForm());
            }
            addMessage("SYSTEM", "Welcome to Neon Checkers", true);
            addMessage("SYSTEM", "Game initialized", true);
            addMessage("SYSTEM", "Make your move in 30 seconds", true);
        } catch (Exception e) {
            System.err.println("Error initializing chat: " + e.getMessage());
        }
    }

    /**
     * Sends the chat message in the chatInput field, if non-empty.
     * The local player is always black, so if it's black's turn, the sender
     * is the local player. Otherwise, it's the opponent.
     */
    @FXML
    public void onSendMessage() {
        String message = chatInput.getText();
        if (message == null || message.trim().isEmpty()) {
            return;
        }

        // Local player is BLACK
        String sender = (gameLogic.getTurn() == Checkers.Turn.BLACK)
                ? localPlayer.getUsername()
                : opponentPlayer.getUsername();

        // Send via chat session
        chatSession.sendMessage(sender, message);

        // Verify it wasn't filtered out
        var history = chatSession.chatManager.getChatHistory();
        if (!history.isEmpty()) {
            ChatMessage lastMessage = history.get(history.size() - 1);
            if (lastMessage.getPlayerId().equals(sender) && lastMessage.getMessage().equals(message)) {
                addMessage(sender, message, false);
            } else {
                addMessage("SYSTEM", "Warning: Inappropriate content!", true);
            }
        } else {
            addMessage("SYSTEM", "Warning: Inappropriate content!", true);
        }

        chatInput.clear();
        Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
    }

    /**
     * Adds a new message to the chatMessages VBox, with specific styling
     * (border color and text color) depending on whether it's a system
     * message or from which player.
     *
     * @param sender   the username of the message sender
     * @param text     the message content
     * @param isSystem true if the message originates from the system
     */
    public void addMessage(String sender, String text, boolean isSystem) {
        if (chatMessages == null) {
            System.err.println("Cannot add message - chatMessages is null");
            return;
        }

        // Find matching ChatMessage in chat history
        ChatMessage lastMessage = null;
        for (ChatMessage msg : chatSession.chatManager.getChatHistory()) {
            if (msg.getPlayerId().equals(sender) && msg.getMessage().equals(text)) {
                lastMessage = msg;
                break;
            }
        }

        // Mark read and log to CSV if appropriate
        if (lastMessage != null) {
            lastMessage.markAsRead(String.valueOf(LoginController.loginId));
            // Only log non-system messages or system messages that aren't warnings
            if (!isSystem || !(text.contains("⚠") || text.contains("Time's up"))) {
                writeChatHistoryToCSV();
            }
        }

        String timestamp = (lastMessage != null)
                ? lastMessage.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                : "";

        // Container for the message
        HBox messageContainer = new HBox(5);
        messageContainer.getStyleClass().add("chat-message");

        // Determine color code
        String colorCode;
        if (isSystem) {
            colorCode = "#ffff00";  // Yellow
        } else if (localPlayer != null && sender.equalsIgnoreCase(localPlayer.getUsername())) {
            colorCode = "#ff00ff";  // Neon pink
        } else if (opponentPlayer != null && sender.equalsIgnoreCase(opponentPlayer.getUsername())) {
            colorCode = "#00ffff";  // Neon cyan
        } else {
            colorCode = "#ffffff";  // Default white
        }

        // Use left border color for visual distinction
        messageContainer.setStyle("-fx-border-width: 0 0 0 3px; -fx-border-color: " + colorCode + ";");

        // Sender label
        Label senderLabel = new Label(sender + ":");
        senderLabel.setFont(Fonts.rajdhani(FontWeight.BOLD, 14));
        senderLabel.setStyle("-fx-text-fill: " + colorCode + ";");

        // Message label
        Label messageLabel = new Label(text);
        messageLabel.setFont(Fonts.rajdhaniRegular(14));
        messageLabel.setStyle("-fx-text-fill: " + colorCode + ";");

        // Add a slight glow to system messages
        if (isSystem) {
            messageLabel.setEffect(new DropShadow(5, Color.YELLOW));
        }

        messageContainer.getChildren().addAll(senderLabel, messageLabel);
        chatMessages.getChildren().add(messageContainer);

        // Auto-scroll to bottom
        if (chatScrollPane != null) {
            Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
        }
    }

    /**
     * Starts a recurring check (every second) of the current turn's TurnTimer.
     * Issues a 10-second warning and ends the game if time runs out.
     */
    private void startTurnTimer() {
        if (turnCheckTimeline != null) {
            turnCheckTimeline.stop();
        }

        turnCheckTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            // If the game has ended, don't continue
            if (gameLogic == null || checkersBoard == null
                    || gameLogic.checkWin() != Checkers.WINNER.NONE) {
                return;
            }

            Checkers.Turn currentTurn = gameLogic.getTurn();
            TurnTimer currentTimer = (currentTurn == Checkers.Turn.WHITE) ? timerWhite : timerBlack;
            boolean isWhiteTurn = (currentTurn == Checkers.Turn.WHITE);

            long elapsedTime = System.currentTimeMillis() - currentTimer.getStartTime();
            long remainingMillis = currentTimer.getRemainingTime() - elapsedTime;
            int remainingSec = (int) (remainingMillis / 1000);

            // 10-second warning
            if (remainingSec <= 10) {
                if (isWhiteTurn && !warningSentWhite) {
                    addMessage("SYSTEM", "⚠ " + opponentPlayer.getUsername() + " has 10 seconds left!", true);
                    warningSentWhite = true;
                } else if (!isWhiteTurn && !warningSentBlack) {
                    addMessage("SYSTEM", "⚠ " + localPlayer.getUsername() + " has 10 seconds left!", true);
                    warningSentBlack = true;
                }
            }

            // If time is up, the current player loses
            if (currentTimer.isTimeExpired()) {
                Platform.runLater(() -> {
                    String loser = isWhiteTurn ? opponentPlayer.getUsername() : localPlayer.getUsername();
                    String winner = isWhiteTurn ? localPlayer.getUsername() : opponentPlayer.getUsername();

                    addMessage("SYSTEM", loser + " ⏰ Time's up! " + winner + " wins!", true);
                    showGameOverPopup(winner, true);
                    turnLabel.setText(winner + " wins!");
                    turnPiece.getStyleClass().clear();
                    turnPiece.getStyleClass().add(isWhiteTurn ? "checker-black" : "checker-white");

                    boardGrid.setDisable(true);
                    stopTimer();
                    stopTurnTimer();

                    // Update game results: the *other* player wins
                    if (isWhiteTurn) {
                        // White timed out, black wins
                        gameProcessor.UpdateResults(localPlayer, opponentPlayer, gameType);
                    } else {
                        // Black timed out, white wins
                        gameProcessor.UpdateResults(opponentPlayer, localPlayer, gameType);
                    }
                });
            }
        }));

        turnCheckTimeline.setCycleCount(Timeline.INDEFINITE);
        turnCheckTimeline.play();
    }

    /**
     * Stops the recurring timeline that checks the turn timers.
     */
    private void stopTurnTimer() {
        if (turnCheckTimeline != null) {
            turnCheckTimeline.stop();
        }
    }

    /**
     * Writes the current chat history to a CSV file for persistence.
     * Each line has the columns: Timestamp,Sender,Message,ReadBy
     */
    private void writeChatHistoryToCSV() {
        List<ChatMessage> history = chatSession.chatManager.getChatHistory();
        try (PrintWriter writer = new PrintWriter(new FileWriter(CHECKERS_CHAT_CSV))) {
            writer.println("Timestamp,Sender,Message,ReadBy");
            for (ChatMessage msg : history) {
                String timestamp = msg.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                String readers = String.join(";", msg.getReaders());
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        timestamp, msg.getPlayerId(), msg.getMessage(), readers);
            }
        } catch (IOException e) {
            System.err.println("Failed to write chat history: " + e.getMessage());
        }
    }

    /**
     * Clears the chat history CSV file (writes only the header).
     */
    private void clearChatHistoryCSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CHECKERS_CHAT_CSV))) {
            writer.println("Timestamp,Sender,Message,ReadBy"); // CSV header
        } catch (IOException e) {
            System.err.println("Failed to clear chat history: " + e.getMessage());
        }
    }
}
