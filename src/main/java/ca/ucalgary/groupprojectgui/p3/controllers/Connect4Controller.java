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
    private final GameType gameType = GameType.CONNECT_FOUR;

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

    // Chat filter
    private InGameChat chatSystem;
    private InGameChat chatSession;

    //Implemented turn timer
    private TurnTimer timerP1;
    private TurnTimer timerP2;
    private Timeline turnCheckTimeline;
    private boolean warningSentP1 = false;
    private boolean warningSentP2 = false;

    private static final String CONNECT4_CHAT_CSV = "connect4ChatHistory.csv";


    @FXML
    public void initialize() {
        // Initialize chat view and header
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
                    if (player.getGameSignal(gameType) == gameType.getGameCode())
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

        // create a new game processor class to update result
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

        chatSession = new InGameChat("connect4-" + localPlayer.getUserID() + "-" + opponentPlayer.getUserID());
        chatSession.establishConnection();
        initializeChat();
        clearChatHistoryCSV();

        timerP1 = new TurnTimer(localPlayer.getUsername(), 30);
        timerP2 = new TurnTimer(opponentPlayer.getUsername(), 30);
        startTurnTimer();  // begin periodic checks
        timerP1.startTimer();  // Player 1 always starts

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

        // Reset warning flags
        warningSentP1 = false;
        warningSentP2 = false;

        // Reset GUI clock
        secondsElapsed = 0;
        startTimer(); // Restart GUI clock

        // Reset TurnTimer
        if (connectBoard.getCurrentPlayer() == PLAYER1_ID) {
            timerP1.resetTimer();
            timerP1.startTimer();
        } else {
            timerP2.resetTimer();
            timerP2.startTimer();
        }

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
    public void onSendMessage() {
        String message = chatInput.getText();
        if (message == null || message.trim().isEmpty()) {
            return;
        }

        // Determine sender based on turn (you are always PLAYER1)
        String sender = (connectBoard.getCurrentPlayer() == PLAYER1_ID)
                ? localPlayer.getUsername()
                : opponentPlayer.getUsername();

        // Send the message via chat system
        chatSession.sendMessage(sender, message);

        // Check chat history to verify if it passed the filter
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
            addMessage("SYSTEM", "Make your move in 30 seconds", true);
        } catch (Exception e) {
            System.err.println("Error initializing chat: " + e.getMessage());
        }
    }

    public void addMessage(String sender, String text, boolean isSystem) {
        if (chatMessages == null) {
            System.err.println("Cannot add message - chatMessages is null");
            return;
        }

        ChatMessage lastMessage = null;
        for (ChatMessage msg : chatSession.chatManager.getChatHistory()) {
            if (msg.getPlayerId().equals(sender) && msg.getMessage().equals(text)) {
                lastMessage = msg;
                break;
            }
        }

        if (lastMessage != null) {
            lastMessage.markAsRead(String.valueOf(LoginController.loginId));
            writeChatHistoryToCSV();  // Save new message
        }

        String timestamp = (lastMessage != null)
                ? lastMessage.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                : "";

        HBox messageContainer = new HBox(5);
        messageContainer.getStyleClass().add("chat-message");

        String colorCode = isSystem ? "#ffff00" :
                (sender.equalsIgnoreCase(localPlayer.getUsername()) ? "#ff00ff" :
                        sender.equalsIgnoreCase(opponentPlayer.getUsername()) ? "#00ffff" : "#ffffff");

        messageContainer.setStyle("-fx-border-width: 0 0 0 3px; -fx-border-color: " + colorCode + ";");

        Label senderLabel = new Label(sender + ":");

        senderLabel.setFont(Fonts.rajdhani(FontWeight.BOLD, 14));

        Label messageLabel = new Label(text);
        messageLabel.setFont(Fonts.rajdhaniRegular(14));

        senderLabel.setStyle("-fx-text-fill: " + colorCode + ";");
        messageLabel.setStyle("-fx-text-fill: " + colorCode + ";");

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
     * Stops the timer.
     */
    private void stopTimer() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    private void startTurnTimer() {
        turnCheckTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (!gameActive) return;

            TurnTimer currentTimer = (connectBoard.getCurrentPlayer() == PLAYER1_ID) ? timerP1 : timerP2;
            boolean isP1 = connectBoard.getCurrentPlayer() == PLAYER1_ID;

            currentTimer.notifyPlayer();
            long elapsed = System.currentTimeMillis() - currentTimer.getStartTime();

            // Send system chat warning at 10 seconds left
            if ((isP1 && !warningSentP1 && currentTimer.getRemainingTime() - elapsed <= 10000)) {
                addMessage("SYSTEM", "⚠ " + localPlayer.getUsername() + " has 10 seconds left!", true);
                warningSentP1 = true;
            } else if (!isP1 && !warningSentP2 && currentTimer.getRemainingTime() - elapsed <= 10000) {
                addMessage("SYSTEM", "⚠ " + opponentPlayer.getUsername() + " has 10 seconds left!", true);
                warningSentP2 = true;
            }

            if (currentTimer.isTimeExpired()) {
                String loser = isP1 ? localPlayer.getUsername() : opponentPlayer.getUsername();
                String winner = !isP1 ? localPlayer.getUsername() : opponentPlayer.getUsername();
                if (isP1) {
                    // Player 1 time expired, Player 2 wins
                    scorePlayer2++; // Increment score for Player 2
                    // update the result, opponent player wins
                    gameProcessor.UpdateResults(opponentPlayer,localPlayer, gameType);
                    score2.setText("Score: " + scorePlayer2); // Update UI for Player 2's score
                } else {
                    // Player 2 time expired, Player 1 wins
                    scorePlayer1++; // Increment score for Player 1
                    // update the result, local player wins
                    gameProcessor.UpdateResults(localPlayer, opponentPlayer, gameType);
                    score1.setText("Score: " + scorePlayer1); // Update UI for Player 1's score
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

    private void stopTurnTimer() {
        if (turnCheckTimeline != null) {
            turnCheckTimeline.stop();
        }
    }

    private void writeChatHistoryToCSV() {
        List<ChatMessage> history = chatSession.chatManager.getChatHistory();
        try (PrintWriter writer = new PrintWriter(new FileWriter(CONNECT4_CHAT_CSV))) {
            writer.println("Timestamp,Sender,Message,ReadBy");

            for (ChatMessage msg : history) {
                String timestamp = msg.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                String readers = String.join(";", msg.getReaders());
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\"%n", timestamp, msg.getPlayerId(), msg.getMessage(), readers);
            }
        } catch (IOException e) {
            System.err.println("Failed to write chat history: " + e.getMessage());
        }
    }

    private void clearChatHistoryCSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CONNECT4_CHAT_CSV))) {
            writer.println("Timestamp,Sender,Message,ReadBy"); // Header
        } catch (IOException e) {
            System.err.println("Failed to clear chat history: " + e.getMessage());
        }
    }
}
