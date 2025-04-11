package ca.ucalgary.groupprojectgui.p3.controllers;

import MatchmakingLeaderboard.GameProcessor;
import MatchmakingLeaderboard.GameType;
import MatchmakingLeaderboard.Player;
import MatchmakingLeaderboard.PlayerDatabase;
import MatchmakingLeaderboard.TicTacToe.TicTacToeMatchmaking;
import javafx.scene.input.KeyCode;
import networking.chat.InGameChat;
import networking.chat.ChatMessage;
import networking.game.TurnTimer;
import gameLogic.tictactoe.TicTacToe;
import gameLogic.tictactoe.TicTacToeBoard;
import ca.ucalgary.groupprojectgui.p3.Fonts;
import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import static javafx.scene.paint.Color.rgb;

/**
 * Controller for the Tic Tac Toe game view.
 * This controller handles the Tic Tac Toe gameplay including the board display, turn logic,
 * matchmaking integration, in-game chat, turn timing, and game over handling.
 */
public class TicTacToeController {

    // UI Components injected via FXML
    @FXML
    public StackPane turnIndicator;
    @FXML
    public Label gameName;
    public Label timeElapsed;
    public Label chatHeader;
    public VBox chatMessages;
    public ScrollPane chatScrollPane;
    public Label gameTitle;
    public Button leaveGame;
    @FXML
    public StackPane boardContainer;
    @FXML
    public Label turnLabel;
    @FXML
    public TextField chatInput;
    @FXML
    public Label localPlayerLabel;
    @FXML
    public Label opponentLabel;

    // Game logic and matchmaking components
    public GameProcessor gameProcessor;
    public TicTacToeBoard logicBoard;
    public TicTacToe gameLogic;

    // Flag to track which player's turn it is. True = Player X's turn.
    public boolean playerXTurn = true;
    // Flag to prevent further interaction after the game ends
    private boolean gameOver = false;
    // Matchmaking integration for Tic Tac Toe
    private TicTacToeMatchmaking matchmaking;
    public Player localPlayer;
    private int player1Id; // Local player's ID
    public Player opponentPlayer;
    public final GameType gameType = GameType.TIC_TAC_TOE;

    // Timer for counting elapsed seconds of the current turn
    private Timeline timeline;
    private int secondsElapsed = 0;

    // Grid for Tic Tac Toe board UI
    public GridPane tttgrid;

    // Chat and turn timer session objects
    private InGameChat chatSession;
    private TurnTimer timerX;
    public TurnTimer timerO;
    private Timeline turnCheckTimeline;
    private boolean warningSentX = false;
    private boolean warningSentO = false;

    // Chat history constants
    private static final String CHAT_HISTORY_FILE = "tictactoeChatHistory.csv";
    private static final int MAX_HISTORY_MESSAGES = 100;

    /**
     * Initializes the Tic Tac Toe controller.
     * Sets up the header, draws the game board, initializes matchmaking and game logic,
     * starts the turn timer and chat session, and clears any previous chat history.
     */
    @FXML
    public void initialize() {
        // Setup the header (for chat) with visual spacing and styling.
        setupHeaderWithSpacing();

        // Draw the Tic Tac Toe board.
        createBoard();
        gameTitle.setText("X-Tic-Tac-Toe-O");

        // --- Matchmaking Integration ---
        matchmaking = new TicTacToeMatchmaking();

        // If no friend opponent is selected from HomePage, use matchmaking logic.
        if (HomePageController.friendOpponentID == -1) {
            try {
                // Connect and add local player to matchmaking queue.
                matchmaking.matchmakingConnect();

                localPlayer = PlayerDatabase.getPlayerByUserID(LoginController.loginId);
                player1Id = localPlayer.getUserID();
                matchmaking.joinQueue(localPlayer);

                // Optionally join all players with the correct game signal to the queue.
                for (Player player : PlayerDatabase.getAllPlayers()) {
                    if (player.getGameSignal(gameType) == gameType.getGameCode())
                        matchmaking.joinQueue(player);
                }
                opponentPlayer = matchmaking.findOpponent(localPlayer.getUserID());
            } catch (IOException e) {
                // If matchmaking fails, display a system message.
                addMessage("SYSTEM", "Matchmaking error: " + e.getMessage(), true);
            }
        } else { // Friend opponent selected from HomePage.
            localPlayer = PlayerDatabase.getPlayerByUserID(LoginController.loginId);
            opponentPlayer = PlayerDatabase.getPlayerByUserID(HomePageController.friendOpponentID);
        }
        // --- End of matchmaking integration ---

        // Initialize game logic components.
        logicBoard = new TicTacToeBoard();
        gameLogic = new TicTacToe(logicBoard);
        gameLogic.start();
        // Create a game processor to update match results.
        gameProcessor = new GameProcessor(localPlayer, opponentPlayer, gameType);

        // Set initial turn for player X.
        playerXTurn = true;
        turnLabel.setText("X: " + localPlayer.getUsername() + "'s Turn");
        localPlayerLabel.setText(localPlayer.getUsername().toUpperCase());
        opponentLabel.setText(opponentPlayer.getUsername().toUpperCase());

        // Initialize chat session for the game.
        chatSession = new InGameChat("TicTacToe-" + localPlayer.getUserID());
        chatSession.establishConnection();
        initializeChat();

        // Initialize turn timers for both players.
        timerX = new TurnTimer(localPlayer.getUsername(), 30);
        timerO = new TurnTimer(opponentPlayer.getUsername(), 30);
        startTurnTimer();    // Start monitoring the turn timer
        timerX.startTimer(); // Player X starts

        // Clear previous chat history CSV file if it exists.
        File history = new File(CHAT_HISTORY_FILE);
        if (history.exists()) {
            history.delete();
        }
    }

    /**
     * Creates a 3x3 Tic Tac Toe board and adds it to the board container.
     * Each cell is a clickable StackPane that triggers a move when clicked.
     */
    public void createBoard() {
        tttgrid = new GridPane();
        tttgrid.setHgap(10);
        tttgrid.setVgap(10);
        tttgrid.setAlignment(Pos.CENTER);

        // Create a 3x3 grid of cells.
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                StackPane cell = new StackPane();
                cell.getStyleClass().add("ttt-square");
                cell.setPrefSize(200, 200);
                int r = row, c = col;
                // Set mouse click handler for each cell.
                cell.setOnMouseClicked((MouseEvent event) -> handleCellClick(r, c, cell));
                tttgrid.add(cell, col, row);
            }
        }
        boardContainer.getChildren().add(tttgrid);

        // Configure the leave game button if present.
        if (leaveGame != null) {
            leaveGame.setOnAction(e -> onLeaveGame());
        }
        startTimer();
    }

    /**
     * Handles a click event on a Tic Tac Toe cell.
     * Places the player's symbol if the cell is empty, updates the board,
     * checks for win or draw conditions, and alternates the turn.
     *
     * @param row  the row index of the clicked cell.
     * @param col  the column index of the clicked cell.
     * @param cell the StackPane representing the cell.
     */
    public void handleCellClick(int row, int col, StackPane cell) {
        // Ignore the click if game is over or cell is already occupied.
        if (gameOver || !cell.getChildren().isEmpty() || !logicBoard.isCellEmpty(row, col)) return;

        char symbol = playerXTurn ? 'X' : 'O';
        logicBoard.placePiece(row, col, symbol);

        // Draw the player's symbol on the cell.
        if (playerXTurn) {
            // Draw an X using two crossing lines.
            Line line1 = new Line(10, 10, 110, 110);
            line1.getStyleClass().add("ttt-x");
            Line line2 = new Line(110, 10, 10, 110);
            line2.getStyleClass().add("ttt-x");
            cell.getChildren().addAll(line1, line2);
        } else {
            // Draw an O using a circle.
            javafx.scene.shape.Circle circle = new javafx.scene.shape.Circle(50, 50, 55);
            circle.getStyleClass().add("ttt-o");
            cell.getChildren().add(circle);
        }

        // Check for a win condition.
        if (logicBoard.checkForWin(symbol)) {
            if (symbol == 'X') {
                showGameOverPopup(localPlayer.getUsername(), true);
                gameOver = true;
                stopTimer();
                gameProcessor.UpdateResults(localPlayer, opponentPlayer, gameType);
                return;
            } else {
                showGameOverPopup(opponentPlayer.getUsername(), true);
                gameOver = true;
                stopTimer();
                gameProcessor.UpdateResults(opponentPlayer, localPlayer, gameType);
                return;
            }
        }

        // Check for a draw.
        if (logicBoard.boardFull()) {
            addMessage("SYSTEM", "It's a draw!", true);
            showGameOverPopup("No one", false);
            stopTimer();
            gameOver = true;
            gameProcessor.ProcessDraw(localPlayer, opponentPlayer, gameType);
            return;
        }

        // Alternate the turn.
        playerXTurn = !playerXTurn;
        gameLogic.changeActivePlayer();
        turnLabel.setText(playerXTurn ? "X: " + localPlayer.getUsername() + "'s Turn"
                : "O: " + opponentPlayer.getUsername() + "'s Turn");

        // Reset chat warning flags.
        warningSentX = false;
        warningSentO = false;

        // Reset the turn timer.
        secondsElapsed = 0;
        startTimer();

        // Restart the appropriate TurnTimer.
        if (playerXTurn) {
            timerX.resetTimer();
            timerX.startTimer();
        } else {
            timerO.resetTimer();
            timerO.startTimer();
        }
    }

    /**
     * Displays a popup overlay indicating that the game is over.
     * Shows the winner or if the match is a draw, with an option to return to the main menu.
     *
     * @param winner the winner's username or "No one" in case of a draw.
     * @param isWin  whether the game ended with a win.
     */
    private void showGameOverPopup(String winner, boolean isWin) {
        leaveGame.setVisible(false);
        // Create overlay and popup container.
        StackPane overlay = new StackPane();
        overlay.getStyleClass().add("popup-overlay");
        overlay.setPrefSize(tttgrid.getWidth(), tttgrid.getHeight());

        VBox popup = new VBox();
        popup.getStyleClass().add("popup-dialog");
        popup.setAlignment(Pos.CENTER);
        popup.setSpacing(10);
        popup.setPadding(new Insets(20));

        Text title = new Text("Game Over");
        title.getStyleClass().add("popup-title");

        Text message = new Text();
        message.getStyleClass().add("popup-message");
        message.setText(isWin ? "Winner: " + winner : "It's a draw!");

        // Button to return to the main menu.
        Button mainMenuButton = new Button("Main Menu");
        mainMenuButton.getStyleClass().add("popup-button");
        mainMenuButton.setOnAction(e -> {
            ((Pane) boardContainer.getParent()).getChildren().remove(overlay);
            SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/views/homePage.fxml");
        });

        popup.getChildren().addAll(title, message, mainMenuButton);
        overlay.getChildren().add(popup);
        ((Pane) tttgrid.getParent()).getChildren().add(overlay);
    }

    /**
     * Handles the leave game action by showing a confirmation popup.
     * If confirmed, the game is exited and the scene switches to the Home page.
     */
    private void onLeaveGame() {
        // Create overlay for leave-game confirmation.
        StackPane overlay = new StackPane();
        overlay.getStyleClass().add("popup-overlay");
        overlay.setPrefSize(boardContainer.getWidth(), boardContainer.getHeight());
        VBox popup = new VBox();
        popup.getStyleClass().add("popup-dialog");
        popup.setAlignment(Pos.CENTER);
        popup.setSpacing(15);
        popup.setPadding(new Insets(20));

        Text title = new Text("Confirm Quit");
        title.getStyleClass().add("popup-title");
        Text message = new Text("Are you sure you want to quit the game?");
        message.getStyleClass().add("popup-message");

        // Yes button confirms exit.
        Button yesButton = new Button("Yes");
        yesButton.getStyleClass().add("popup-button");
        yesButton.setOnAction(e -> {
            boardContainer.getChildren().remove(overlay);
            SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/views/homePage.fxml");
        });
        // Cancel button dismisses the popup.
        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("popup-button");
        cancelButton.setOnAction(e -> boardContainer.getChildren().remove(overlay));
        HBox buttonBox = new HBox(15, yesButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);
        popup.getChildren().addAll(title, message, buttonBox);
        overlay.getChildren().add(popup);
        boardContainer.getChildren().add(overlay);
    }

    /**
     * Starts or restarts the turn timer which updates the elapsed time every second.
     */
    private void startTimer() {
        if (timeline != null) {
            timeline.stop();
        }
        // Display the initial time.
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
     * Sets up the header for the chat section with custom spacing and styling.
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

        // Each letter is styled using a custom font and drop shadow.
        String headerText = "OMG NETWORK";
        for (char c : headerText.toCharArray()) {
            Text letter = new Text(String.valueOf(c));
            letter.setFont(Fonts.orbitron(FontWeight.NORMAL, 24));
            letter.setFill(Color.WHITE);
            letter.setEffect(new DropShadow(5, rgb(0, 255, 255)));
            letter.setEffect(new DropShadow(10, rgb(0, 255, 255)));
            textContainer.getChildren().add(letter);
        }

        // Create an underline with a gradient effect.
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
     * Initializes the in-game chat view.
     * Loads the chat stylesheet, displays initial system messages, and sets up the Enter key handler.
     */
    private void initializeChat() {
        try {
            URL cssUrl = getClass().getResource("/ca/ucalgary/groupprojectgui/p3/styles/TicTacToe.css");
            if (cssUrl != null) {
                chatMessages.getStylesheets().add(cssUrl.toExternalForm());
            }
            addMessage("SYSTEM", "Welcome to Neon Tic Tac Toe", true);
            addMessage("SYSTEM", "Game initialized", true);
            addMessage("SYSTEM", "Make your move in 30 seconds", true);
        } catch (Exception e) {
            System.err.println("Error initializing chat: " + e.getMessage());
        }
        // Send message when Enter key is pressed.
        chatInput.setOnKeyPressed(event -> {
            if (Objects.requireNonNull(event.getCode()) == KeyCode.ENTER) {
                onSendMessage();
            }
        });
    }

    /**
     * Stops the turn timer.
     */
    private void stopTimer() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    /**
     * Handles sending chat messages.
     * Retrieves message text from the input, sends it via the chat session, and adds it to the chat view.
     */
    @FXML
    private void onSendMessage() {
        String message = chatInput.getText();
        if (message == null || message.trim().isEmpty()) {
            return;
        }
        // Determine sender based on whose turn it is.
        String sender = playerXTurn ? localPlayer.getUsername()
                : (opponentPlayer != null ? opponentPlayer.getUsername() : "Player O");

        chatSession.sendMessage(sender, message);

        // Check the chat history to verify the message (a simple filter example)
        var history = chatSession.chatManager.getChatHistory();
        if (!history.isEmpty()) {
            ChatMessage last = history.get(history.size() - 1);
            if (last.getPlayerId().equals(sender) && last.getMessage().equals(message)) {
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
     * Adds a message to the in-game chat view.
     * Applies inline styles to force consistent appearance based on the sender.
     *
     * @param sender   the sender of the message.
     * @param text     the message text.
     * @param isSystem if true, styles the message as a system message.
     */
    public void addMessage(String sender, String text, boolean isSystem) {
        if (chatMessages == null) return;

        // Mark the message as read if found in history and write to CSV.
        ChatMessage lastMessage = null;
        for (ChatMessage msg : chatSession.chatManager.getChatHistory()) {
            if (msg.getPlayerId().equals(sender) && msg.getMessage().equals(text)) {
                lastMessage = msg;
                break;
            }
        }
        if (lastMessage != null) {
            lastMessage.markAsRead(String.valueOf(LoginController.loginId));
            writeChatHistoryToCSV();
        }

        // Create container for the chat message.
        HBox messageContainer = new HBox(5);
        messageContainer.getStyleClass().add("chat-message");

        // Determine border color based on sender.
        String colorCode = isSystem ? "#ffff00" :
                (sender.equalsIgnoreCase(localPlayer.getUsername()) ? "#ff00ff" :
                        sender.equalsIgnoreCase(opponentPlayer.getUsername()) ? "#00ffff" : "#ffffff");
        messageContainer.setStyle("-fx-border-width: 0 0 0 3px; -fx-border-color: " + colorCode + ";");

        // Build sender label.
        Label senderLabel = new Label(sender + ":");
        senderLabel.setFont(Fonts.rajdhani(FontWeight.BOLD, 14));
        senderLabel.setStyle("-fx-text-fill: " + colorCode + ";");

        // Build message label.
        Label messageLabel = new Label(text);
        messageLabel.setFont(Fonts.rajdhaniRegular(14));
        messageLabel.setStyle("-fx-text-fill: " + colorCode + ";");

        messageContainer.getChildren().addAll(senderLabel, messageLabel);
        chatMessages.getChildren().add(messageContainer);

        // Auto-scroll chat to the bottom.
        if (chatScrollPane != null) {
            Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
        }
    }

    /**
     * Starts the turn timer monitoring loop.
     * Checks every second if the player's turn time is nearly up or expired.
     */
    private void startTurnTimer() {
        turnCheckTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (gameOver) return;

            // Get the current player's timer.
            TurnTimer currentTimer = playerXTurn ? timerX : timerO;
            long elapsedTime = System.currentTimeMillis() - currentTimer.getStartTime();
            long remainingMillis = currentTimer.getRemainingTime() - elapsedTime;
            int remainingSec = (int) (remainingMillis / 1000);

            // Issue a warning when 10 seconds or less remain.
            if (remainingSec <= 10) {
                if (playerXTurn && !warningSentX) {
                    addMessage("SYSTEM", "⚠ " + localPlayer.getUsername() + " has 10 seconds left!", true);
                    warningSentX = true;
                } else if (!playerXTurn && !warningSentO) {
                    addMessage("SYSTEM", "⚠ " + opponentPlayer.getUsername() + " has 10 seconds left!", true);
                    warningSentO = true;
                }
            }

            // If time has expired, end the game.
            if (currentTimer.isTimeExpired()) {
                String loser = playerXTurn ? localPlayer.getUsername() : opponentPlayer.getUsername();
                String winner = playerXTurn ? opponentPlayer.getUsername() : localPlayer.getUsername();
                // Update results accordingly.
                if (playerXTurn) {
                    gameProcessor.UpdateResults(opponentPlayer, localPlayer, gameType);
                } else {
                    gameProcessor.UpdateResults(localPlayer, opponentPlayer, gameType);
                }
                addMessage("SYSTEM", loser + " ⏰ Time's up! " + winner + " wins!", true);
                showGameOverPopup(winner, true);
                gameOver = true;
                stopTimer();
                stopTurnTimer();
            }
        }));
        turnCheckTimeline.setCycleCount(Timeline.INDEFINITE);
        turnCheckTimeline.play();
    }

    /**
     * Stops the turn timer monitoring loop.
     */
    private void stopTurnTimer() {
        if (turnCheckTimeline != null) {
            turnCheckTimeline.stop();
        }
    }

    /**
     * Writes the chat history to a CSV file.
     * Only the most recent MAX_HISTORY_MESSAGES are written.
     */
    private void writeChatHistoryToCSV() {
        File file = new File(CHAT_HISTORY_FILE);

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("Timestamp,Sender,Message,ReadBy");
            var history = chatSession.chatManager.getChatHistory();
            int startIdx = Math.max(0, history.size() - MAX_HISTORY_MESSAGES);
            for (int i = startIdx; i < history.size(); i++) {
                ChatMessage msg = history.get(i);
                String time = msg.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                String readers = String.join(" | ", msg.getReaders());
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\"%n", time, msg.getPlayerId(), msg.getMessage().replace("\"", "\"\""), readers);
            }
        } catch (IOException e) {
            System.err.println("Error writing chat history: " + e.getMessage());
        }
    }
}
