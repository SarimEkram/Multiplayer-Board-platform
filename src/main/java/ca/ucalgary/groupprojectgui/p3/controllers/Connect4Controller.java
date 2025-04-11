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
import MatchmakingLeaderboard.Connect4.Connect4Matchmaking;
import MatchmakingLeaderboard.*;
import networking.chat.InGameChat;
import networking.chat.ChatMessage;
import networking.game.TurnTimer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static javafx.scene.paint.Color.rgb;

/**
 * Controller for the Connect 4 game scene. Handles UI updates, game logic,
 * matchmaking, real-time chat, and turn-timing features.
 */
public class Connect4Controller {

    /*
     * FXML-INJECTED UI ELEMENTS
     */

    /** Container for the chat messages (vertical list). */
    @FXML private VBox chatMessages;

    /** ScrollPane that contains the chat messages (to enable scrolling). */
    @FXML private ScrollPane chatScrollPane;

    /** Custom header label to display a stylized "OMG Network" header. */
    @FXML private Label chatHeader;

    /** The main grid container for the Connect 4 board. */
    @FXML private GridPane connect4Grid;

    /** The horizontal container above the board for column selection indicators. */
    @FXML private HBox columnSelectors;

    /** Label that displays the local player's name. */
    @FXML private Label name1;

    /** Label that displays the opponent's name. */
    @FXML private Label name2;

    /** A button that allows players to leave/quit the game. */
    @FXML private Button leaveGameBtn;

    /** Title label for the game (e.g., "OMG CONNECT 4"). */
    @FXML public Label gameTitle;

    /** Label to show the time elapsed for the current turn. */
    @FXML private Label timeElapsed;

    /** Label to show the number of moves made in the current game. */
    @FXML private Label movesCount;

    /** TextField for user input of chat messages. */
    @FXML private TextField chatInput;

    /*
     * INTERNAL FIELDS AND CONSTANTS
     */

    /** The inner grid that holds the 6x7 board circles. */
    private GridPane cellsGrid;

    // Game board constants
    private static final int ROWS = 6;
    private static final int COLUMNS = 7;
    private static final int CELL_SIZE = 65;

    // Colors for player pieces and empty cells
    private static final Color PLAYER1_COLOR = rgb(255, 0, 255); // Neon pink
    private static final Color PLAYER2_COLOR = rgb(0, 255, 255); // Cyan
    private static final Color EMPTY_COLOR = rgb(20, 20, 40);

    // Player ID constants for game logic
    private static final int PLAYER1_ID = 1;
    private static final int PLAYER2_ID = 2;

    // CSV file for archiving chat
    private static final String CONNECT4_CHAT_CSV = "connect4ChatHistory.csv";

    // Game logic
    private ConnectBoard connectBoard;
    private GameProcessor gameProcessor;

    // Game type enumeration
    private final GameType gameType = GameType.CONNECT_FOUR;

    // Matchmaking and player references
    private Connect4Matchmaking matchmaking;
    private int player1Id;
    private Player localPlayer;
    private Player opponentPlayer;

    // Game state tracking
    private boolean gameActive = true;

    // Timers and counters
    private Timeline timeline;
    private int secondsElapsed = 0;
    private int moveCounter = 0;

    // In-game chat
    private InGameChat chatSession;

    // Turn timers
    private TurnTimer timerP1;
    private TurnTimer timerP2;
    private Timeline turnCheckTimeline;
    private boolean warningSentP1 = false;
    private boolean warningSentP2 = false;

    /**
     * Initializes the Connect4Controller once the FXML is loaded. Sets up
     * matchmaking, players, UI elements, chat system, and game timers.
     */
    @FXML
    public void initialize() {
        // Initialize chat header with stylized text
        setupHeaderWithSpacing();
        gameTitle.setText("OMG CONNECT 4");

        // Initialize matchmaking
        matchmaking = new Connect4Matchmaking();

        // If friendOpponentID is -1, attempt standard matchmaking
        if (HomePageController.friendOpponentID == -1) {
            try {
                matchmaking.matchmakingConnect();
                localPlayer = PlayerDatabase.getPlayerByUserID(LoginController.loginId);
                player1Id = localPlayer.getUserID();
                matchmaking.joinQueue(localPlayer);

                // Join queue for all relevant players in the database
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
            // Otherwise, use the friend-based setup
            localPlayer = PlayerDatabase.getPlayerByUserID(LoginController.loginId);
            opponentPlayer = PlayerDatabase.getPlayerByUserID(HomePageController.friendOpponentID);
        }

        // Create a new game processor to update the result in the database
        gameProcessor = new GameProcessor(localPlayer, opponentPlayer, gameType);

        // Update player name labels
        name1.setText(localPlayer != null ? localPlayer.getUsername().toUpperCase() : "PLAYER 1");
        name1.setPadding(new Insets(5, 10, 5, 10));
        name1.setFont(Fonts.rajdhaniBold(16));

        name2.setText(opponentPlayer != null ? opponentPlayer.getUsername().toUpperCase() : "PLAYER 2");
        name2.setPadding(new Insets(5, 10, 5, 10));
        name2.setFont(Fonts.rajdhaniBold(16));

        // Instantiate ConnectBoard for the game
        connectBoard = new ConnectBoard(PLAYER1_ID, PLAYER2_ID);

        // Set up the board UI and column selectors
        setupBoard();
        setupColumnSelectors();
        updatePlayerTurn();

        // Prepare the "Leave Game" button
        if (leaveGameBtn != null) {
            leaveGameBtn.setOnAction(e -> showLeaveGameConfirmationPopup());
        }

        // Start the overall turn-based timer (visual display in the GUI)
        startTimer();

        // Set up in-game chat
        chatSession = new InGameChat("connect4-" + localPlayer.getUserID() + "-" + opponentPlayer.getUserID());
        chatSession.establishConnection();
        initializeChat();
        clearChatHistoryCSV(); // Clear old chat logs from CSV

        // Turn timers (each player has 30 seconds per turn)
        timerP1 = new TurnTimer(localPlayer.getUsername(), 30);
        timerP2 = new TurnTimer(opponentPlayer.getUsername(), 30);
        startTurnTimer(); // Begin monitoring turn timers

        // Player 1 starts, so start their clock
        timerP1.startTimer();
    }

    /**
     * Sets up a stylized header ("OMG Network") at the top of the chat panel,
     * including letter spacing and special effects.
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
        // Build stylized text one character at a time
        for (char c : headerText.toCharArray()) {
            Text letter = new Text(String.valueOf(c));
            letter.setFont(Fonts.orbitron(FontWeight.NORMAL, 24));
            letter.setFill(Color.WHITE);
            letter.setEffect(new DropShadow(5, rgb(0, 255, 255)));
            textContainer.getChildren().add(letter);
        }

        // Add a neon-like gradient underline
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
     * Sets up the Connect 4 board with a grid of circular cells (6 rows x 7 columns).
     * Applies visual effects such as neon glows and hover interactions.
     */
    private void setupBoard() {
        // Clear any existing children in the FXML grid
        connect4Grid.getChildren().clear();

        // Layout spacing
        double hGap = 10, vGap = 10;
        double gridWidth = COLUMNS * CELL_SIZE + (COLUMNS - 1) * hGap;
        double gridHeight = ROWS * CELL_SIZE + (ROWS - 1) * vGap;

        // Container to hold the background and the cellsGrid
        StackPane boardContainer = new StackPane();
        boardContainer.setPrefSize(gridWidth, gridHeight);

        // Neon-style background gradient
        Rectangle gradientBackground = new Rectangle(gridWidth, gridHeight);
        gradientBackground.setArcHeight(20);
        gradientBackground.setArcWidth(20);
        gradientBackground.setFill(new LinearGradient(
                1, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(255, 0, 255, 0.1)),
                new Stop(0.5, Color.rgb(0, 255, 255, 0.1)),
                new Stop(1, Color.rgb(255, 0, 255, 0.1))
        ));

        // Slight black overlay to dim the underlying gradient
        Rectangle blackOverlay = new Rectangle(gridWidth, gridHeight);
        blackOverlay.setArcHeight(20);
        blackOverlay.setArcWidth(20);
        blackOverlay.setFill(Color.rgb(0, 0, 0, 0.03));

        // Stroke around the gradient
        gradientBackground.setStroke(new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.3, Color.rgb(255, 0, 255, 0.3)),
                new Stop(0.7, Color.rgb(0, 255, 255, 0.3))
        ));
        gradientBackground.setStrokeWidth(2);

        // Inner and drop shadows for neon effect
        InnerShadow innerShadow = new InnerShadow(BlurType.GAUSSIAN,
                Color.rgb(0, 0, 0, 0.8), 25, 0, 0, 0);
        DropShadow outerGlow = new DropShadow(BlurType.GAUSSIAN,
                Color.rgb(0, 255, 255, 0.2), 40, 0, 0, 0);
        innerShadow.setInput(outerGlow);
        gradientBackground.setEffect(innerShadow);

        // The actual grid that will hold the circular "slots"
        cellsGrid = new GridPane();
        cellsGrid.setHgap(hGap);
        cellsGrid.setVgap(vGap);
        cellsGrid.setAlignment(Pos.CENTER);

        /*
         * Create the 6 x 7 slots (circles). Each circle responds to mouse clicks
         * and highlights on hover. If clicked, it attempts to place a piece in
         * the corresponding column.
         */
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                Circle slot = new Circle(30);
                slot.getStyleClass().add("empty-slot");
                slot.setPickOnBounds(true);

                // Default subtle shadow
                DropShadow defaultShadow = new DropShadow();
                defaultShadow.setRadius(5);
                defaultShadow.setColor(Color.rgb(255, 255, 255, 0.1));
                defaultShadow.setOffsetX(0);
                defaultShadow.setOffsetY(0);
                slot.setEffect(defaultShadow);

                // Hover shadow
                DropShadow hoverShadow = new DropShadow();
                hoverShadow.setRadius(25);
                hoverShadow.setColor(Color.rgb(0, 255, 255, 0.8));
                hoverShadow.setOffsetX(0);
                hoverShadow.setOffsetY(0);

                // Mouse hover effects
                slot.setOnMouseEntered(e -> slot.setEffect(hoverShadow));
                slot.setOnMouseExited(e -> slot.setEffect(defaultShadow));

                /*
                 * When a slot is clicked, we handle the move if the game is active.
                 * The move logic is delegated to handleMove(column).
                 */
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
     * Sets up the clickable column selectors (above the board),
     * allowing players to drop a piece in a column.
     */
    private void setupColumnSelectors() {
        columnSelectors.getChildren().clear();

        for (int col = 0; col < COLUMNS; col++) {
            StackPane selector = new StackPane();
            selector.setPrefSize(CELL_SIZE, CELL_SIZE);

            // Transparent circle that, on hover, shows a neon color
            Circle indicator = new Circle((double) CELL_SIZE / 2 - 5);
            indicator.setFill(Color.TRANSPARENT);
            indicator.setStroke(Color.TRANSPARENT);
            indicator.setStrokeWidth(2);

            DropShadow glow = new DropShadow(15, Color.TRANSPARENT);
            indicator.setEffect(glow);

            selector.getChildren().add(indicator);
            final int column = col;

            // Visual feedback on hover
            selector.setOnMouseEntered(e -> {
                indicator.setFill(connectBoard.getCurrentPlayer() == PLAYER1_ID
                        ? Color.color(PLAYER1_COLOR.getRed(),
                        PLAYER1_COLOR.getGreen(),
                        PLAYER1_COLOR.getBlue(), 0.3)
                        : Color.color(PLAYER2_COLOR.getRed(),
                        PLAYER2_COLOR.getGreen(),
                        PLAYER2_COLOR.getBlue(), 0.3));
                glow.setColor(connectBoard.getCurrentPlayer() == PLAYER1_ID
                        ? PLAYER1_COLOR
                        : PLAYER2_COLOR);
            });

            selector.setOnMouseExited(e -> {
                indicator.setFill(Color.TRANSPARENT);
                glow.setColor(Color.TRANSPARENT);
            });

            // Click to drop a piece in this column
            selector.setOnMouseClicked(e -> {
                if (gameActive) {
                    handleMove(column);
                }
            });

            columnSelectors.getChildren().add(selector);
        }
    }

    /**
     * Handles a move request: attempts to drop a piece into the specified column.
     * Updates the board if valid, checks for a win or draw, and switches turns.
     *
     * @param column the column index (0-based) where the player is dropping a piece
     */
    private void handleMove(int column) {
        /*
         * connectBoard.playPiece(column) returns the row index where the piece
         * lands, or -1 if the column is already full.
         */
        int row = connectBoard.playPiece(column);
        if (row < 0) {
            addMessage("SYSTEM", "Column " + (column + 1) + " is full!", true);
            return;
        }

        // The player who just played is the "last player"
        int lastPlayer = (connectBoard.getCurrentPlayer() == PLAYER1_ID) ? PLAYER2_ID : PLAYER1_ID;
        updateCell(row, column, lastPlayer);

        // Identify player name for the chat message
        String playerName = (lastPlayer == PLAYER1_ID ? name1.getText() : name2.getText());
        addMessage(playerName, "Placed token in column " + (column + 1), false);

        // Increment move counter and update the Moves label
        moveCounter++;
        movesCount.setText("MOVES: " + moveCounter);

        // Check if there's a winner or if it's a draw
        Connect4 logic = new Connect4(connectBoard);
        if (connectBoard.isGameOver() || logic.won(connectBoard.getBoard(), lastPlayer)) {
            gameActive = false;

            // If a win condition is detected, update the database
            if (logic.won(connectBoard.getBoard(), lastPlayer)) {
                if (lastPlayer == PLAYER1_ID) {
                    gameProcessor.UpdateResults(localPlayer, opponentPlayer, gameType);
                } else {
                    gameProcessor.UpdateResults(opponentPlayer, localPlayer, gameType);
                }
                addMessage("SYSTEM", playerName + " wins!", true);
                showGameOverPopup(playerName, true);
            } else {
                // Otherwise it's a draw
                gameProcessor.ProcessDraw(localPlayer, opponentPlayer, gameType);
                addMessage("SYSTEM", "It's a draw!", true);
                showGameOverPopup("No one", false);
            }

            // Stop the GUI turn timer
            stopTimer();
            return;
        }

        // If no one has won yet, switch to next player
        updatePlayerTurn();

        // Reset any "10 seconds left" warnings for new turn
        warningSentP1 = false;
        warningSentP2 = false;

        // Reset and restart the GUI clock
        secondsElapsed = 0;
        startTimer();

        /*
         * Reset turn timer logic for whichever player is about to move.
         * Player 1 or 2 each have a TurnTimer object.
         */
        if (connectBoard.getCurrentPlayer() == PLAYER1_ID) {
            timerP1.resetTimer();
            timerP1.startTimer();
        } else {
            timerP2.resetTimer();
            timerP2.startTimer();
        }
    }

    /**
     * Updates a single board cell (circle) to reflect the correct color
     * after a move is made.
     *
     * @param row    the row index of the placed piece
     * @param col    the column index of the placed piece
     * @param player the player ID who placed the piece
     */
    private void updateCell(int row, int col, int player) {
        // Each slot is stored in cellsGrid's children in row-major order
        Circle cell = (Circle) cellsGrid.getChildren().get(row * COLUMNS + col);
        cell.setFill(player == PLAYER1_ID ? PLAYER1_COLOR : PLAYER2_COLOR);
        DropShadow glow = (DropShadow) cell.getEffect();
        glow.setColor(player == PLAYER1_ID ? PLAYER1_COLOR : PLAYER2_COLOR);
    }

    /**
     * Highlights the name label of the current player to show whose turn it is.
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
     * Shows a pop-up confirming if the user wants to leave the game mid-match.
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
     * Displays a pop-up indicating the game has ended, the winner (or draw),
     * and provides a button to return to the main menu.
     *
     * @param winner the username of the winning player (or "No one" if draw)
     * @param isWin  true if there's a winner, false if it's a draw
     */
    private void showGameOverPopup(String winner, boolean isWin) {
        leaveGameBtn.setVisible(false);
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
            message.setText("Winner: " + winner);
        } else {
            message.setText("It's a draw!");
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

    /**
     * Switches the scene to the main menu (homePage.fxml).
     */
    private void goToMainMenu() {
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/views/homePage.fxml");
    }

    /**
     * Called when the user clicks the "Send" button or presses Enter.
     * Sends the message in the chatInput field to the server (via chatSession).
     */
    @FXML
    public void onSendMessage() {
        String message = chatInput.getText();
        if (message == null || message.trim().isEmpty()) {
            return;
        }

        /*
         * We arbitrarily treat localPlayer as "PLAYER1" and the opponent as "PLAYER2"
         * for identifying which name to use as sender. This may vary depending on
         * your networking logic.
         */
        String sender = (connectBoard.getCurrentPlayer() == PLAYER1_ID)
                ? localPlayer.getUsername()
                : opponentPlayer.getUsername();

        // Send message to chat system
        chatSession.sendMessage(sender, message);

        /*
         * If the message was not filtered out, it should appear in the chat history.
         * We confirm the last message in the chat history is indeed the same to ensure
         * it passed the filter.
         */
        var history = chatSession.chatManager.getChatHistory();
        if (!history.isEmpty()) {
            ChatMessage lastMessage = history.get(history.size() - 1);
            if (lastMessage.getPlayerId().equals(sender) && lastMessage.getMessage().equals(message)) {
                addMessage(sender, message, false);
            } else {
                // If something changed or was blocked, warn user
                addMessage("SYSTEM", "Warning: Inappropriate content!", true);
            }
        } else {
            // If the chat is still empty, the message might have been blocked
            addMessage("SYSTEM", "Warning: Inappropriate content!", true);
        }

        chatInput.clear();
        Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
    }

    /**
     * Initializes the chat area CSS and adds some welcome system messages.
     */
    private void initializeChat() {
        try {
            URL cssUrl = getClass().getResource("/ca/ucalgary/groupprojectgui/p3/styles/connect4.css");
            if (cssUrl != null) {
                chatMessages.getStylesheets().add(cssUrl.toExternalForm());
            }
            addMessage("SYSTEM", "Welcome to Neon Connect 4", true);
            addMessage("SYSTEM", "Game initialized", true);
            addMessage("SYSTEM", "Make your move in 30 seconds", true);
        } catch (Exception e) {
            System.err.println("Error initializing chat: " + e.getMessage());
        }
    }

    /**
     * Adds a new chat message to the chatMessages VBox (UI).
     * Also writes the message to the CSV if it passes the chat filter.
     *
     * @param sender   the username of the message sender
     * @param text     the message content
     * @param isSystem whether the message originates from the system (affects styling)
     */
    public void addMessage(String sender, String text, boolean isSystem) {
        if (chatMessages == null) {
            System.err.println("Cannot add message - chatMessages is null");
            return;
        }

        /*
         * Check if this message exists in the chat history, so we can mark it read
         * and then write it to CSV if appropriate.
         */
        ChatMessage lastMessage = null;
        for (ChatMessage msg : chatSession.chatManager.getChatHistory()) {
            if (msg.getPlayerId().equals(sender) && msg.getMessage().equals(text)) {
                lastMessage = msg;
                break;
            }
        }

        if (lastMessage != null) {
            lastMessage.markAsRead(String.valueOf(LoginController.loginId));
            writeChatHistoryToCSV();  // Persist new message to CSV
        }

        String timestamp = (lastMessage != null)
                ? lastMessage.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                : "";

        // Build UI container for the message
        HBox messageContainer = new HBox(5);
        messageContainer.getStyleClass().add("chat-message");

        // Color code is based on system messages, local user, or opponent
        String colorCode = isSystem ? "#ffff00"
                : (sender.equalsIgnoreCase(localPlayer.getUsername()) ? "#ff00ff"
                : sender.equalsIgnoreCase(opponentPlayer.getUsername()) ? "#00ffff" : "#ffffff");

        messageContainer.setStyle("-fx-border-width: 0 0 0 3px; -fx-border-color: " + colorCode + ";");

        Label senderLabel = new Label(sender + ":");
        senderLabel.setFont(Fonts.rajdhani(FontWeight.BOLD, 14));

        Label messageLabel = new Label(text);
        messageLabel.setFont(Fonts.rajdhaniRegular(14));

        senderLabel.setStyle("-fx-text-fill: " + colorCode + ";");
        messageLabel.setStyle("-fx-text-fill: " + colorCode + ";");

        messageContainer.getChildren().addAll(senderLabel, messageLabel);
        chatMessages.getChildren().add(messageContainer);

        // Scroll to bottom to show new message
        if (chatScrollPane != null) {
            Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
        }
    }

    /**
     * Starts or restarts a Timeline that updates the 'timeElapsed' label every second.
     * Displays how long the current turn has taken so far.
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
     * Stops the current Timeline that tracks turn time.
     */
    private void stopTimer() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    /**
     * Starts a recurring check (every second) on each player's TurnTimer.
     * If a turn timer expires, the current player forfeits.
     */
    private void startTurnTimer() {
        turnCheckTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            // If the game is over, no need to check
            if (!gameActive) {
                return;
            }

            // Determine whose timer to check
            TurnTimer currentTimer = (connectBoard.getCurrentPlayer() == PLAYER1_ID) ? timerP1 : timerP2;
            boolean isP1 = (connectBoard.getCurrentPlayer() == PLAYER1_ID);

            // Each second, the timer "ticks"
            currentTimer.notifyPlayer();
            long elapsed = System.currentTimeMillis() - currentTimer.getStartTime();

            /*
             * If the player has 10 seconds left and hasn't been warned yet,
             * broadcast a system chat warning.
             */
            if (isP1 && !warningSentP1 && currentTimer.getRemainingTime() - elapsed <= 10000) {
                addMessage("SYSTEM", "⚠ " + localPlayer.getUsername() + " has 10 seconds left!", true);
                warningSentP1 = true;
            } else if (!isP1 && !warningSentP2 && currentTimer.getRemainingTime() - elapsed <= 10000) {
                addMessage("SYSTEM", "⚠ " + opponentPlayer.getUsername() + " has 10 seconds left!", true);
                warningSentP2 = true;
            }

            // If time is completely up, that player forfeits
            if (currentTimer.isTimeExpired()) {
                String loser = isP1 ? localPlayer.getUsername() : opponentPlayer.getUsername();
                String winner = !isP1 ? localPlayer.getUsername() : opponentPlayer.getUsername();

                // Update database: the other player wins
                if (isP1) {
                    gameProcessor.UpdateResults(opponentPlayer, localPlayer, gameType);
                } else {
                    gameProcessor.UpdateResults(localPlayer, opponentPlayer, gameType);
                }

                addMessage("SYSTEM", loser + " ⏰ Time's up! " + winner + " wins!", true);
                showGameOverPopup(winner, true);
                gameActive = false;
                stopTimer();
                stopTurnTimer();
            }
        }));
        turnCheckTimeline.setCycleCount(Timeline.INDEFINITE);
        turnCheckTimeline.play();
    }

    /**
     * Stops the recurring timeline that checks each player's TurnTimer.
     */
    private void stopTurnTimer() {
        if (turnCheckTimeline != null) {
            turnCheckTimeline.stop();
        }
    }

    /**
     * Writes the current chat history to a CSV file for record-keeping.
     * Each row contains Timestamp,Sender,Message,ReadBy.
     */
    private void writeChatHistoryToCSV() {
        List<ChatMessage> history = chatSession.chatManager.getChatHistory();
        try (PrintWriter writer = new PrintWriter(new FileWriter(CONNECT4_CHAT_CSV))) {
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
     * Clears the chat history CSV file (used when starting a new session).
     */
    private void clearChatHistoryCSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CONNECT4_CHAT_CSV))) {
            // Write only the header row
            writer.println("Timestamp,Sender,Message,ReadBy");
        } catch (IOException e) {
            System.err.println("Failed to clear chat history: " + e.getMessage());
        }
    }
}
