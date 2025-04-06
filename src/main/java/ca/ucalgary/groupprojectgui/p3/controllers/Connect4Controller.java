package ca.ucalgary.groupprojectgui.p3.controllers;

import ca.ucalgary.groupprojectgui.p3.Fonts;
import ca.ucalgary.groupprojectgui.p3.SceneManager;
import gameLogic.connect4.Connect4;
import gameLogic.connect4.ConnectBoard;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import MatchmakingLeaderboard.Connect4.Matchmaking.Connect4Matchmaking;
import MatchmakingLeaderboard.*;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import static javafx.scene.paint.Color.rgb;

public class Connect4Controller {

    // FXML elements
    @FXML private Region glowLayer;
    @FXML private Region scanlinesLayer;
    @FXML private VBox chatMessages;
    @FXML private ScrollPane chatScrollPane;
    @FXML private Label chatHeader;
    @FXML private GridPane connect4Grid;
    @FXML private HBox columnSelectors;
    @FXML private Label name1;
    @FXML private Label name2;
    @FXML private Label score1;
    @FXML private Label score2;
    @FXML private Button newGameBtn;
    @FXML private Button resetBtn;
    @FXML private Button soundBtn;
    @FXML private Button leaveGameBtn;
    @FXML public Label gameTitle;

    // Time and Moves labels (controlled UI elements)
    @FXML private Label timeElapsed;
    @FXML private Label movesCount;

    // Fields for the inner cells grid
    private GridPane cellsGrid;
    @FXML private TextField chatInput;
    @FXML private TextArea chatArea;

    // Game constants
    private static final int ROWS = 6;
    private static final int COLUMNS = 7;
    private static final int CELL_SIZE = 65;
    private static final Color PLAYER1_COLOR = rgb(255, 0, 255); // Neon pink
    private static final Color PLAYER2_COLOR = rgb(0, 255, 255); // Cyan
    private static final Color EMPTY_COLOR = rgb(20, 20, 40);

    // Define player piece IDs
    private static final int PLAYER1_ID = 1;
    private static final int PLAYER2_ID = 2;

    // Game logic instance
    private ConnectBoard connectBoard;
    private GameProcessor gameProcessor;

    // Game type
    private final int gameType = 2;

    // Matchmaking and player info
    private Connect4Matchmaking matchmaking;
    private int player1Id;
    private int opponentId;
    private Player localPlayer;
    private Player opponentPlayer;

    // Game state and scores
    private boolean gameActive = true;
    private int scorePlayer1 = 0;
    private int scorePlayer2 = 0;

    // Used to track message count for alternating chat message styling
    private int messageCount = 0;

    // Timer and move counter
    private Timeline timeline;
    private int secondsElapsed = 0;
    private int moveCounter = 0;

    @FXML
    public void initialize() {
        // Initialize chat view and header
        initializeChat();
        setupHeaderWithSpacing();

        gameTitle.setText("OMG CONNECT 4");

        // --- Matchmaking integration ---
        matchmaking = new Connect4Matchmaking();

        if (HomePageController.friendOpponentID == -1) {
            try {
                matchmaking.matchmakingConnect();
                localPlayer = PlayerDatabase.getPlayerByUserID(LoginController.loginId);
                player1Id = localPlayer.getUserID();
                matchmaking.joinQueue(localPlayer);
                for (Player player : PlayerDatabase.getAllPlayers()) {
                    if (player.getGameSignal(gameType) == gameType)
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
        // --- End matchmaking integration ---
        gameProcessor = new GameProcessor(localPlayer, opponentPlayer, gameType);


        // Update player names to uppercase and set font
        name1.setText(localPlayer != null ? localPlayer.getUsername().toUpperCase() : "PLAYER 1");
        name1.setPadding(new Insets(5, 10, 5, 10));
        name1.setFont(Fonts.rajdhaniBold(16));

        name2.setText(opponentPlayer != null ? opponentPlayer.getUsername().toUpperCase() : "PLAYER 2");
        name2.setPadding(new Insets(5, 10, 5, 10));
        name2.setFont(Fonts.rajdhaniBold(16));

        // Initialize scores
        score1.setText("Score: " + scorePlayer1);
        score2.setText("Score: " + scorePlayer2);

        // Instantiate game logic (PLAYER1 is local, PLAYER2 is opponent)
        connectBoard = new ConnectBoard(PLAYER1_ID, PLAYER2_ID);

        // Setup board and selectors
        setupBoard();
        setupColumnSelectors();
        updatePlayerTurn();

        // Setup leave game button action
        if (leaveGameBtn != null) {
            leaveGameBtn.setOnAction(e -> showLeaveGameConfirmationPopup());
        }

        // Start timer for the game
        startTimer();
    }

    /**
     * Sets up the game board UI grid.
     */
    private void setupBoard() {
        connect4Grid.getChildren().clear();

        double hGap = 10, vGap = 10;
        double gridWidth = COLUMNS * CELL_SIZE + (COLUMNS - 1) * hGap;
        double gridHeight = ROWS * CELL_SIZE + (ROWS - 1) * vGap;

        StackPane boardContainer = new StackPane();
        boardContainer.setPrefSize(gridWidth, gridHeight);

        Rectangle gradientBackground = new Rectangle(gridWidth, gridHeight);
        gradientBackground.setArcHeight(20);
        gradientBackground.setArcWidth(20);
        gradientBackground.setFill(new LinearGradient(
                1, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(255, 0, 255, 0.1)),
                new Stop(0.5, Color.rgb(0, 255, 255, 0.1)),
                new Stop(1, Color.rgb(255, 0, 255, 0.1))
        ));

        Rectangle blackOverlay = new Rectangle(gridWidth, gridHeight);
        blackOverlay.setArcHeight(20);
        blackOverlay.setArcWidth(20);
        blackOverlay.setFill(Color.rgb(0, 0, 0, 0.03));

        gradientBackground.setStroke(new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.3, Color.rgb(255, 0, 255, 0.3)),
                new Stop(0.7, Color.rgb(0, 255, 255, 0.3))
        ));
        gradientBackground.setStrokeWidth(2);

        InnerShadow innerShadow = new InnerShadow(BlurType.GAUSSIAN,
                Color.rgb(0, 0, 0, 0.8), 25, 0, 0, 0);
        DropShadow outerGlow = new DropShadow(BlurType.GAUSSIAN,
                Color.rgb(0, 255, 255, 0.2), 40, 0, 0, 0);
        innerShadow.setInput(outerGlow);
        gradientBackground.setEffect(innerShadow);

        cellsGrid = new GridPane();
        cellsGrid.setHgap(hGap);
        cellsGrid.setVgap(vGap);
        cellsGrid.setAlignment(Pos.CENTER);

        // Create slots for Connect 4 board
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                Circle slot = new Circle(30);
                slot.getStyleClass().add("empty-slot");
                slot.setPickOnBounds(true);

                DropShadow defaultShadow = new DropShadow();
                defaultShadow.setRadius(5);
                defaultShadow.setColor(Color.rgb(255, 255, 255, 0.1));
                defaultShadow.setOffsetX(0);
                defaultShadow.setOffsetY(0);
                slot.setEffect(defaultShadow);

                DropShadow hoverShadow = new DropShadow();
                hoverShadow.setRadius(25);
                hoverShadow.setColor(Color.rgb(0, 255, 255, 0.8));
                hoverShadow.setOffsetX(0);
                hoverShadow.setOffsetY(0);

                slot.setOnMouseEntered(e -> slot.setEffect(hoverShadow));
                slot.setOnMouseExited(e -> slot.setEffect(defaultShadow));

                int finalCol = col;
                slot.setOnMouseClicked(e -> {
                    if (gameActive) {
                        handleMove(finalCol);
                    }
                });

                cellsGrid.add(slot, col, row);
            }
        }

        boardContainer.getChildren().addAll(gradientBackground, blackOverlay, cellsGrid);
        connect4Grid.getChildren().add(boardContainer);
    }

    /**
     * Sets up the column selectors (clickable areas above the board).
     */
    private void setupColumnSelectors() {
        columnSelectors.getChildren().clear();

        for (int col = 0; col < COLUMNS; col++) {
            StackPane selector = new StackPane();
            selector.setPrefSize(CELL_SIZE, CELL_SIZE);

            Circle indicator = new Circle((double) CELL_SIZE / 2 - 5);
            indicator.setFill(Color.TRANSPARENT);
            indicator.setStroke(Color.TRANSPARENT);
            indicator.setStrokeWidth(2);

            DropShadow glow = new DropShadow(15, Color.TRANSPARENT);
            indicator.setEffect(glow);

            selector.getChildren().add(indicator);
            final int column = col;

            selector.setOnMouseEntered(e -> {
                indicator.setFill(connectBoard.getCurrentPlayer() == PLAYER1_ID ?
                        Color.color(PLAYER1_COLOR.getRed(), PLAYER1_COLOR.getGreen(), PLAYER1_COLOR.getBlue(), 0.3) :
                        Color.color(PLAYER2_COLOR.getRed(), PLAYER2_COLOR.getGreen(), PLAYER2_COLOR.getBlue(), 0.3));
                glow.setColor(connectBoard.getCurrentPlayer() == PLAYER1_ID ? PLAYER1_COLOR : PLAYER2_COLOR);
            });

            selector.setOnMouseExited(e -> {
                indicator.setFill(Color.TRANSPARENT);
                glow.setColor(Color.TRANSPARENT);
            });

            selector.setOnMouseClicked(e -> {
                if (gameActive) {
                    handleMove(column);
                }
            });

            columnSelectors.getChildren().add(selector);
        }
    }

    /**
     * Handles a move when a column is selected.
     */
    private void handleMove(int column) {
        int row = connectBoard.playPiece(column);
        if (row < 0) {
            addMessage("SYSTEM", "Column " + (column + 1) + " is full!", true);
            return;
        }

        int lastPlayer = (connectBoard.getCurrentPlayer() == PLAYER1_ID) ? PLAYER2_ID : PLAYER1_ID;
        updateCell(row, column, lastPlayer);
        String playerName = (lastPlayer == PLAYER1_ID ? name1.getText() : name2.getText());
        addMessage(playerName, "Placed token in column " + (column + 1), false);

        // Increment move counter and update moves label
        moveCounter++;
        movesCount.setText("MOVES: " + moveCounter);

        Connect4 logic = new Connect4(connectBoard);
        if (connectBoard.isGameOver() || logic.won(connectBoard.getBoard(), lastPlayer)) {
            gameActive = false;
            if (logic.won(connectBoard.getBoard(), lastPlayer)) {
                if (lastPlayer == PLAYER1_ID) {
                    scorePlayer1++;
                    score1.setText("Score: " + scorePlayer1);
                    gameProcessor.UpdateResults(localPlayer, opponentPlayer, gameType);
                } else {
                    scorePlayer2++;
                    score2.setText("Score: " + scorePlayer2);
                    gameProcessor.UpdateResults(opponentPlayer, localPlayer, gameType);
                }
                addMessage("SYSTEM", playerName + " wins!", true);
                showGameOverPopup(playerName, true);
            } else {
                gameProcessor.ProcessDraw(localPlayer, opponentPlayer, gameType);
                addMessage("SYSTEM", "It's a draw!", true);
                showGameOverPopup("No one", false);
            }
            stopTimer(); // Stop timer when game ends
            return;
        }
        updatePlayerTurn();
    }

    /**
     * Updates a single cell's UI after a move.
     */
    private void updateCell(int row, int col, int player) {
        Circle cell = (Circle) cellsGrid.getChildren().get(row * COLUMNS + col);
        cell.setFill(player == PLAYER1_ID ? PLAYER1_COLOR : PLAYER2_COLOR);
        DropShadow glow = (DropShadow) cell.getEffect();
        glow.setColor(player == PLAYER1_ID ? PLAYER1_COLOR : PLAYER2_COLOR);
    }

    /**
     * Updates the player turn indicator.
     */
    private void updatePlayerTurn() {
        if (connectBoard.getCurrentPlayer() == PLAYER1_ID) {
            name1.setEffect(new DropShadow(10, PLAYER1_COLOR));
            name2.setEffect(null);
        } else {
            name2.setEffect(new DropShadow(10, PLAYER2_COLOR));
            name1.setEffect(null);
        }
    }

    /**
     * Resets the game board and UI.
     */
    private void resetGame(boolean fullReset) {
        connectBoard.clearBoard();
        gameActive = true;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                Circle cell = (Circle) cellsGrid.getChildren().get(row * COLUMNS + col);
                cell.setFill(EMPTY_COLOR);
                DropShadow defaultShadow = new DropShadow();
                defaultShadow.setRadius(5);
                defaultShadow.setColor(Color.rgb(255, 255, 255, 0.1));
                defaultShadow.setOffsetX(0);
                defaultShadow.setOffsetY(0);
                cell.setEffect(defaultShadow);
            }
        }
        // Reset moves and timer
        moveCounter = 0;
        movesCount.setText("MOVES: " + moveCounter);
        secondsElapsed = 0;
        timeElapsed.setText("TIME: 00:00");
        startTimer();
        updatePlayerTurn();
    }

    /**
     * Displays an in-scene pop-up overlay showing the game result and scores.
     */
    private void showGameOverPopup(String winner, boolean isWin) {
        StackPane overlay = new StackPane();
        overlay.getStyleClass().add("popup-overlay");
        overlay.setPrefSize(connect4Grid.getWidth(), connect4Grid.getHeight());

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
            message.setText("Winner: " + winner + "\nScore:\n"
                    + name1.getText() + ": " + scorePlayer1 + "\n"
                    + name2.getText() + ": " + scorePlayer2);
        } else {
            message.setText("It's a draw!\nScore:\n"
                    + name1.getText() + ": " + scorePlayer1 + "\n"
                    + name2.getText() + ": " + scorePlayer2);
        }

        Button mainMenuButton = new Button("Main Menu");
        mainMenuButton.getStyleClass().add("popup-button");
        mainMenuButton.setOnAction(e -> {
            ((Pane) connect4Grid.getParent()).getChildren().remove(overlay);
            goToMainMenu();
        });

        popup.getChildren().addAll(title, message, mainMenuButton);
        overlay.getChildren().add(popup);
        ((Pane) connect4Grid.getParent()).getChildren().add(overlay);
    }

    private void goToMainMenu() {
        SceneManager.switchTo(
                "/ca/ucalgary/groupprojectgui/p3/HomePage.fxml",
                "Home Page",
                "home.css"
        );
    }

    @FXML
    private void onSendMessage() {
        String message = chatInput.getText();
        if (message == null || message.trim().isEmpty()) {
            return;
        }
        addMessage("You", message, false);
        chatInput.clear();
        Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
    }

    /**
     * Displays a confirmation overlay asking if the user wants to quit.
     */
    private void showLeaveGameConfirmationPopup() {
        StackPane overlay = new StackPane();
        overlay.getStyleClass().add("popup-overlay");
        overlay.setPrefSize(connect4Grid.getWidth(), connect4Grid.getHeight());

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
            ((Pane) connect4Grid.getParent()).getChildren().remove(overlay);
            goToMainMenu();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("popup-button");
        cancelButton.setOnAction(e -> ((Pane) connect4Grid.getParent()).getChildren().remove(overlay));

        HBox buttonBox = new HBox(15, yesButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);
        popup.getChildren().addAll(title, message, buttonBox);
        overlay.getChildren().add(popup);
        ((Pane) connect4Grid.getParent()).getChildren().add(overlay);
    }

    /**
     * Sets up the header with individual letter effects.
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
            letter.setEffect(new DropShadow(5, rgb(0, 255, 255)));
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
            addMessage("SYSTEM", "Welcome to Neon Connect 4", true);
            addMessage("SYSTEM", "Game initialized", true);
        } catch (Exception e) {
            System.err.println("Error initializing chat: " + e.getMessage());
        }
    }

    /**
     * Adds a message to the chat view.
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

    public void onPlayerMove(String playerName, int column) {
        addMessage(playerName, "Played in column " + column, false);
    }

    public void onGameEvent(String message) {
        addMessage("SYSTEM", message, true);
    }

    /**
     * Starts a timer that updates the timeElapsed label every second.
     */
    private void startTimer() {
        if (timeline != null) {
            timeline.stop();
        }
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsElapsed++;
            int minutes = secondsElapsed / 60;
            int seconds = secondsElapsed % 60;
            timeElapsed.setText(String.format("TIME: %02d:%02d", minutes, seconds));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Stops the timer.
     */
    private void stopTimer() {
        if (timeline != null) {
            timeline.stop();
        }
    }
}
