package ca.ucalgary.groupprojectgui.p3.controllers;

import MatchmakingLeaderboard.Checkers.Matchmaking.CheckersMatchmaking;
import MatchmakingLeaderboard.GameProcessor;
import MatchmakingLeaderboard.Player;
import MatchmakingLeaderboard.PlayerDatabase;
import ca.ucalgary.groupprojectgui.p3.Fonts;
import gameLogic.checkers.Checkers;
import gameLogic.checkers.CheckersBoard;
import gameLogic.checkers.CheckersPiece;
import gameLogic.checkers.CheckersMove;
import gameLogic.checkers.CheckersMove.Move;
import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.BoxBlur;
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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static javafx.scene.paint.Color.rgb;

public class CheckersController {

    @FXML
    public Label whiteCaptured;

    @FXML
    public Label blackCaptured;

    @FXML
    public Circle turnPiece;

    @FXML
    public VBox chatMessages;

    @FXML
    public ScrollPane chatScrollPane;

    @FXML
    public Label chatHeader;
    public Button leaveGame;

    @FXML
    private Label turnLabel; // Turn indicator label

    @FXML
    private StackPane boardContainer;

    @FXML
    private StackPane checkerPiece1;

    @FXML
    private Circle checkerCircle1;      // Player 1's circle

    @FXML
    private StackPane checkerPiece2;

    @FXML
    private Circle checkerCircle2;      // Player 2's circle

    @FXML
    private BorderPane mainGamePane;

    @FXML
    private TextField chatInput;
    @FXML
    private Label player1Name;
    @FXML
    private Label player2Name;

    @FXML
    private Label winnerLabel;
    private CheckersMatchmaking matchmaking;
    private Player localPlayer;
    private int player1Id;       // Local player's ID (from matchmaking)
    private int opponentId;      // Opponent's player ID
    private Player opponentPlayer;

    // Board constants
    private static final int BOARD_ROWS = 8;
    private static final int BOARD_COLUMNS = 8;
    private static final double BOARD_MARGIN = 20.0;

    private GridPane boardGrid;
    private Rectangle boardBackground;

    private CheckersBoard checkersBoard;
    private Checkers gameLogic;

    // UI cell mapping for board cells
    private StackPane[][] cellPanes = new StackPane[BOARD_ROWS][BOARD_COLUMNS];

    // For tracking selection and valid moves (using Move objects)
    private Position selectedPiecePosition = null;
    private boolean isPieceSelected = false;
    private ArrayList<Move> validMoves = new ArrayList<>();

    private int messageCount = 0;
    private GameProcessor gameProcessor;

    private static class Position {
        int row, col;
        Position(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    @FXML
    public void initialize() {
        initializeChat();
        setupHeaderWithSpacing();

        matchmaking = new CheckersMatchmaking();

        if (HomePageController.friendOpponentID == -1) {
            try {
                matchmaking.matchmakingConnect();
                localPlayer = PlayerDatabase.getPlayerByUserID(LoginController.loginId);
                player1Id = localPlayer.getUserID();
                matchmaking.joinQueue(localPlayer);
                for (Player player : PlayerDatabase.getAllPlayers()) {
                    if (player.getGameSignal(3) == 3)
                        matchmaking.joinQueue(player);
                }
                opponentPlayer = matchmaking.findOpponent(localPlayer.getUserID());
            } catch (IOException e) {
                addMessage("SYSTEM", "Matchmaking error: " + e.getMessage(), true);
            }
        } else {
            localPlayer = PlayerDatabase.getPlayerByUserID(LoginController.loginId);
            opponentPlayer = PlayerDatabase.getPlayerByUserID(HomePageController.friendOpponentID);
        }

        // --- End of matchmaking integration ---

        checkersBoard = new CheckersBoard();
        gameLogic = new Checkers(checkersBoard);
        gameLogic.start();
        createBoard();
        player1Name.setText(localPlayer.getUsername());
        player2Name.setText(opponentPlayer.getUsername());

        gameProcessor = new GameProcessor(localPlayer, opponentPlayer, 3);
    }

    private void createBoard() {
        Image crown = new Image(getClass().getResourceAsStream("/ca/ucalgary/groupprojectgui/p3/images/crown.png"));
        ImageView crownImage = new ImageView(crown);
        crownImage.setFitWidth(45);
        crownImage.setFitHeight(30);
        crownImage.setVisible(false);

        boardGrid = new GridPane();
        boardGrid.setAlignment(Pos.CENTER);

        boardBackground = new Rectangle();
        boardBackground.getStyleClass().add("board-background");

        // Build board grid and store cells in cellPanes
        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int col = 0; col < BOARD_COLUMNS; col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(72, 72);
                Rectangle square = new Rectangle(72, 72);
                square.getStyleClass().add("board-square");
                if ((row + col) % 2 == 0) {
                    square.setFill(Color.BEIGE);
                } else {
                    square.setFill(Color.BROWN);
                }
                cell.getChildren().add(square);

                // Place initial pieces based on backend setup
                if (row < 3 && (row + col) % 2 == 1) {
                    Circle whitePiece = new Circle(32);
                    whitePiece.getStyleClass().addAll(checkerCircle1.getStyleClass());
                    cell.getChildren().add(whitePiece);
                } else if (row > 4 && (row + col) % 2 == 1) {
                    Circle blackPiece = new Circle(32);
                    blackPiece.getStyleClass().addAll(checkerCircle2.getStyleClass());
                    cell.getChildren().add(blackPiece);
                }

                cellPanes[row][col] = cell;
                final int currentRow = row;
                final int currentCol = col;
                cell.setOnMouseClicked(e -> handleCellClick(currentRow, currentCol));
                cell.setOnMouseEntered(e -> handleCellHoverEnter(currentRow, currentCol));
                cell.setOnMouseExited(e -> handleCellHoverExit(currentRow, currentCol));
                boardGrid.add(cell, col, row);
            }
        }

        boardContainer.getChildren().addAll(boardBackground, boardGrid);
        boardContainer.widthProperty().addListener((obs, oldVal, newVal) -> updateBoardLayout());
        boardContainer.heightProperty().addListener((obs, oldVal, newVal) -> updateBoardLayout());
        updateTurnIndicator();

        if (leaveGame != null) {
            leaveGame.setOnAction(e -> onLeaveGame());
        }
    }

    private void updateBoardLayout() {
        double containerWidth = boardContainer.getWidth();
        double containerHeight = boardContainer.getHeight();
        if (containerWidth <= 0 || containerHeight <= 0) return;
        double boardSize = Math.min(containerWidth, containerHeight) * 0.9;
        boardBackground.setWidth(boardSize);
        boardBackground.setHeight(boardSize);
        double cellSize = (boardSize - 2 * BOARD_MARGIN) / BOARD_COLUMNS;
        for (Node node : boardGrid.getChildren()) {
            if (node instanceof Rectangle) {
                ((Rectangle) node).setWidth(cellSize);
                ((Rectangle) node).setHeight(cellSize);
            }
        }
    }

    private void onLeaveGame() {
        Pane parent = (Pane) boardContainer.getParent();
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
            SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/HomePage.fxml", "Home Page", "home.css");
        });
        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("popup-button");
        cancelButton.setOnAction(e -> {
            parent.getChildren().remove(overlay);
        });
        HBox buttonBox = new HBox(15, yesButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);
        popup.getChildren().addAll(title, message, buttonBox);
        overlay.getChildren().add(popup);
        parent.getChildren().add(overlay);
    }

    private void handleCellClick(int row, int col) {
        if (isPieceSelected) {
            // Check if the clicked cell corresponds to one of the valid moves
            Move selectedMove = null;
            for (Move move : validMoves) {
                if (move.destRow == row && move.destCol == col) {
                    selectedMove = move;
                    break;
                }
            }
            if (selectedMove != null) {
                CheckersPiece selectedPiece = checkersBoard.board[selectedPiecePosition.row][selectedPiecePosition.col];
                gameLogic.processMove(selectedPiece, selectedPiecePosition.row, selectedPiecePosition.col, selectedMove);
                clearHighlights();
                isPieceSelected = false;
                selectedPiecePosition = null;
                validMoves.clear();
                updateBoardUI();
                // First, check standard win by piece count.
                Checkers.WINNER winner = gameLogic.checkWin();
                // Then check if the current turn has any valid moves.
                if (winner == Checkers.WINNER.NONE) {
                    if (!hasAnyValidMovesForTurn(gameLogic.getTurn())) {
                        // No moves available: current turn loses; opponent wins.
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
                if (winner != Checkers.WINNER.NONE) {
                    checkForWinnerAndShowLabel();
                } else {
                    updateTurnIndicator();
                }
                return;
            } else {
                clearHighlights();
                isPieceSelected = false;
                selectedPiecePosition = null;
                validMoves.clear();
            }
        }
        CheckersPiece piece = checkersBoard.board[row][col];
        if (piece != null) {
            if (!isPieceOfCurrentTurn(piece)) {
                return;
            }
            selectedPiecePosition = new Position(row, col);
            isPieceSelected = true;
            validMoves.clear();
            Move[] moves = CheckersMove.availableMoves(checkersBoard, piece, row, col);
            for (Move move : moves) {
                validMoves.add(move);
                highlightValidMoveCell(move.destRow, move.destCol);
            }
            highlightSelectedPieceCell(row, col);
        }
    }

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

    private void handleCellHoverExit(int row, int col) {
        if (!isPieceSelected) {
            clearHighlights();
        }
    }

    private boolean isPieceOfCurrentTurn(CheckersPiece piece) {
        Checkers.Turn currentTurn = gameLogic.getTurn();
        if (currentTurn == Checkers.Turn.WHITE && piece.getColour() == CheckersPiece.Colour.WHITE) {
            return true;
        } else if (currentTurn == Checkers.Turn.BLACK && piece.getColour() == CheckersPiece.Colour.BLACK) {
            return true;
        }
        return false;
    }

    // Helper method: returns true if at least one piece of the specified turn has valid moves.
    private boolean hasAnyValidMovesForTurn(Checkers.Turn turn) {
        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int col = 0; col < BOARD_COLUMNS; col++) {
                CheckersPiece piece = checkersBoard.board[row][col];
                if (piece != null) {
                    if ((turn == Checkers.Turn.WHITE && piece.getColour() == CheckersPiece.Colour.WHITE) ||
                            (turn == Checkers.Turn.BLACK && piece.getColour() == CheckersPiece.Colour.BLACK)) {
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

    private void clearHighlights() {
        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int col = 0; col < BOARD_COLUMNS; col++) {
                cellPanes[row][col].getChildren().removeIf(node -> {
                    if (node instanceof Rectangle) {
                        Rectangle rect = (Rectangle) node;
                        Color fill = (Color) rect.getFill();
                        return fill.equals(Color.rgb(255, 255, 0, 0.3)) ||
                                fill.equals(Color.rgb(0, 0, 255, 0.3));
                    }
                    return false;
                });
            }
        }
    }

    private void updateBoardUI() {
        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int col = 0; col < BOARD_COLUMNS; col++) {
                StackPane cell = cellPanes[row][col];
                cell.getChildren().removeIf(node -> node instanceof Circle || node instanceof ImageView);
                CheckersPiece piece = checkersBoard.board[row][col];
                if (piece != null) {
                    Circle pieceCircle = new Circle(32);
                    if (piece.getColour() == CheckersPiece.Colour.WHITE) {
                        pieceCircle.getStyleClass().addAll(checkerCircle2.getStyleClass());
                    } else {
                        pieceCircle.getStyleClass().addAll(checkerCircle1.getStyleClass());
                    }
                    cell.getChildren().add(pieceCircle);
                    if (piece.isKing()) {
                        Image crownImage = new Image(getClass().getResourceAsStream("/ca/ucalgary/groupprojectgui/p3/images/crown.png"));
                        ImageView crownView = new ImageView(crownImage);
                        crownView.setFitWidth(45);
                        crownView.setFitHeight(30);
                        cell.getChildren().add(crownView);
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

    private void updateTurnIndicator() {
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

    private void checkForWinnerAndShowLabel() {
        Checkers.WINNER winner = gameLogic.checkWin();
        if (winner != Checkers.WINNER.NONE) {
            if (winner == Checkers.WINNER.WHITE) {
                turnLabel.setText(opponentPlayer.getUsername() + " wins!");
                turnPiece.getStyleClass().clear();
                turnPiece.getStyleClass().add("checker-white");
                gameProcessor.UpdateResults(opponentPlayer, localPlayer, 3);
            } else {
                turnLabel.setText(localPlayer.getUsername() + " wins!");
                turnPiece.getStyleClass().clear();
                turnPiece.getStyleClass().add("checker-black");
                gameProcessor.UpdateResults(localPlayer, opponentPlayer, 3);
            }
            boardGrid.setDisable(true);
        }
    }

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
            letter.setEffect(new DropShadow(5, rgb(0, 255, 255)));
            letter.setEffect(new DropShadow(10, rgb(0, 255, 255)));
            textContainer.getChildren().add(letter);
        }

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
     * Initializes the chat view and adds initial messages.
     */
    private void initializeChat() {
        try {
            URL cssUrl = getClass().getResource("/ca/ucalgary/groupprojectgui/p3/styles/connect4.css");
            if (cssUrl != null) {
                chatMessages.getStylesheets().add(cssUrl.toExternalForm());
            }
            addMessage("SYSTEM", "Welcome to Neon Checkers", true);
            addMessage("SYSTEM", "Game initialized", true);
        } catch (Exception e) {
            System.err.println("Error initializing chat: " + e.getMessage());
        }
    }

    /**
     * Adds a message to the chat view.
     *
     * @param sender   the sender of the message
     * @param text     the message text
     * @param isSystem flag to indicate if this is a system message
     */
    public void addMessage(String sender, String text, boolean isSystem) {
        if (chatMessages == null) {
            System.err.println("Cannot add message - chatMessages is null");
            return;
        }

        HBox messageContainer = new HBox(5);
        messageContainer.getStyleClass().add("chat-message");
        messageContainer.getStyleClass().add(messageCount % 2 == 0 ? "chat-message-even" : "chat-message-odd");

        Label senderLabel = new Label(sender + ":");
        senderLabel.getStyleClass().add("sender-label");
        senderLabel.setFont(Fonts.rajdhani(FontWeight.BOLD, 14));

        Label messageLabel = new Label(text);
        messageLabel.getStyleClass().add(isSystem ? "system-message" : "player-message");
        messageLabel.setFont(Fonts.rajdhaniRegular(14));

        if (isSystem) {
            messageLabel.setTextFill(Color.YELLOW);
            messageLabel.setEffect(new DropShadow(5, Color.YELLOW));
        } else {
            messageLabel.setTextFill(Color.WHITE);
        }

        messageContainer.getChildren().addAll(senderLabel, messageLabel);
        chatMessages.getChildren().add(messageContainer);
        messageCount++;

        if (chatScrollPane != null) {
            Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
        }
    }
}
