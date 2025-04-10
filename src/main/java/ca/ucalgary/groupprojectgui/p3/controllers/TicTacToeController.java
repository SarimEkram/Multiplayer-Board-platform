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

public class TicTacToeController {

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
    private TextArea chatArea;

    @FXML
    private TextField chatInput;

    @FXML
    public Label localPlayerLabel;
    @FXML
    public Label opponentLabel;
    public GameProcessor gameProcessor;

    public TicTacToeBoard logicBoard;
    public TicTacToe gameLogic;

    private GridPane grid;
    public char currentPlayer;

    // Track the current player. True = Player X, False = Player O.
    public boolean playerXTurn = true;

    //Prevents further interaction after game ends
    private boolean gameOver = false;
    private TicTacToeMatchmaking matchmaking;
    public Player localPlayer;
    private int player1Id;       // Local player's ID (from matchmaking)
    private int opponentId;      // Opponent's player ID
    public Player opponentPlayer;
    public final GameType gameType = GameType.TIC_TAC_TOE;
    private Timeline timeline;
    private int secondsElapsed = 0;
    public GridPane tttgrid;
    private int messageCount = 0;

    //Chat session
    private InGameChat chatSession;

    //Turn timer
    private TurnTimer timerX;
    public TurnTimer timerO;
    private Timeline turnCheckTimeline;
    private boolean warningSentX = false;
    private boolean warningSentO = false;

    //Chat history
    private static final String CHAT_HISTORY_FILE = "tictactoeChatHistory.csv";
    private static final int MAX_HISTORY_MESSAGES = 100;


    @FXML
    public void initialize() {

        setupHeaderWithSpacing();

        // Draw board
        createBoard();
        gameTitle.setText("X-Tic-Tac-Toe-O");
        // --- Matchmaking integration ---
        matchmaking = new TicTacToeMatchmaking();

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
                // uncommented addMessage as its implemented now
                 addMessage("SYSTEM", "Matchmaking error: " + e.getMessage(), true);
            }
        } else {
            localPlayer = PlayerDatabase.getPlayerByUserID(LoginController.loginId);
            opponentPlayer = PlayerDatabase.getPlayerByUserID(HomePageController.friendOpponentID);
        }

        // --- End of matchmaking integration ---

        logicBoard = new TicTacToeBoard();
        gameLogic = new TicTacToe(logicBoard);
        gameLogic.start();
        // create a new game processor class to update result
        gameProcessor = new GameProcessor(localPlayer, opponentPlayer, gameType);
        currentPlayer = 'X';
        turnLabel.setText("X: " + localPlayer.getUsername() + "'s Turn");
        localPlayerLabel.setText(localPlayer.getUsername().toUpperCase());
        opponentLabel.setText(opponentPlayer.getUsername().toUpperCase());

        chatSession = new InGameChat("TicTacToe-" + localPlayer.getUserID()); // or a real session ID if you have one
        chatSession.establishConnection();
        initializeChat();

        timerX = new TurnTimer(localPlayer.getUsername(), 30);
        timerO = new TurnTimer(opponentPlayer.getUsername(), 30);
        startTurnTimer();  // Start monitoring loop
        timerX.startTimer();  // X goes first

        // Clear previous chat CSV
        File history = new File(CHAT_HISTORY_FILE);
        if (history.exists()) {
            history.delete();
        }


    }

    /**
     * Create a 3x3 Tic Tac Toe grid and add it to the boardContainer.
     */
    public void createBoard() {
        tttgrid = new GridPane();
        tttgrid.setHgap(10);
        tttgrid.setVgap(10);
        tttgrid.setAlignment(Pos.CENTER);
        // Create a 3x3 grid
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                StackPane cell = new StackPane();
                cell.getStyleClass().add("ttt-square");
                cell.setPrefSize(200, 200);
                int r = row, c = col;
                cell.setOnMouseClicked((MouseEvent event) -> handleCellClick(r, c, cell));
                tttgrid.add(cell, col, row);
            }
        }
        boardContainer.getChildren().add(tttgrid);

        if (leaveGame != null) {
            leaveGame.setOnAction(e -> onLeaveGame());
        }
        startTimer();


    }

    /**
     * Handle a click on a board cell.
     *
     * @param row  the row index of the clicked cell.
     * @param col  the column index of the clicked cell.
     * @param cell the StackPane representing the cell.
     */


    public void handleCellClick(int row, int col, StackPane cell) {
        //  Ignore if the game is over or cell is already filled
        if (gameOver || !cell.getChildren().isEmpty() || !logicBoard.isCellEmpty(row, col)) return;

        char symbol = playerXTurn ? 'X' : 'O';
        logicBoard.placePiece(row, col, symbol);

        // Draw marker
        if (playerXTurn) {
            Line line1 = new Line(10, 10, 110, 110);
            line1.getStyleClass().add("ttt-x");
            Line line2 = new Line(110, 10, 10, 110);
            line2.getStyleClass().add("ttt-x");
            cell.getChildren().addAll(line1, line2);
        } else {
            javafx.scene.shape.Circle circle = new javafx.scene.shape.Circle(50, 50, 55);
            circle.getStyleClass().add("ttt-o");
            cell.getChildren().add(circle);
        }

        // Check for win
        if (logicBoard.checkForWin(symbol)) {
            if (symbol == 'X') {
                showGameOverPopup(localPlayer.getUsername(), true);
                gameOver = true;
                stopTimer();
                // updates the result, local player wins
                gameProcessor.UpdateResults(localPlayer, opponentPlayer, gameType);
                return;
            } else {
                showGameOverPopup(opponentPlayer.getUsername(), true);
                gameOver = true;
                stopTimer();
                // updates the result, opponent player wins
                gameProcessor.UpdateResults(opponentPlayer, localPlayer, gameType);
                return;
            }

        }

        // Check for draw
        if (logicBoard.boardFull()) {
            addMessage("SYSTEM", "It's a draw!", true);
            showGameOverPopup("No one", false);
            stopTimer();
            gameOver = true;
            gameProcessor.ProcessDraw(localPlayer, opponentPlayer, gameType);
            return;
        }

        // Next turn
        playerXTurn = !playerXTurn;
        gameLogic.changeActivePlayer();
        turnLabel.setText(playerXTurn ? "X: " + localPlayer.getUsername() + "'s Turn"
                : "O: " + opponentPlayer.getUsername() + "'s Turn");

        // Reset chat warning flags
        warningSentX = false;
        warningSentO = false;

        // Reset GUI timer clock
        secondsElapsed = 0;
        startTimer();  // This stops and restarts the timer

        // Reset and start the appropriate TurnTimer
        if (playerXTurn) {
            timerX.resetTimer();
            timerX.startTimer();
        } else {
            timerO.resetTimer();
            timerO.startTimer();
        }

    }

    private void showGameOverPopup(String winner, boolean isWin) {
        leaveGame.setVisible(false);
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
        if (isWin) {
            message.setText("Winner: " + winner);
        } else {
            message.setText("It's a draw!");
        }

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

    private void onLeaveGame() {
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
        Button yesButton = new Button("Yes");
        yesButton.getStyleClass().add("popup-button");
        yesButton.setOnAction(e -> {
            boardContainer.getChildren().remove(overlay);
            SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/views/homePage.fxml");
        });
        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("popup-button");
        cancelButton.setOnAction(e -> {
            boardContainer.getChildren().remove(overlay);
        });
        HBox buttonBox = new HBox(15, yesButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);
        popup.getChildren().addAll(title, message, buttonBox);
        overlay.getChildren().add(popup);
        boardContainer.getChildren().add(overlay);
    }

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
        chatInput.setOnKeyPressed(event -> {
            if (Objects.requireNonNull(event.getCode()) == KeyCode.ENTER) {
                onSendMessage();
            }
        });

    }

    private void stopTimer() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    @FXML
    private void onSendMessage() {
        String message = chatInput.getText();
        if (message == null || message.trim().isEmpty()) {
            return;
        }

        String sender = playerXTurn ? localPlayer.getUsername()
                : (opponentPlayer != null ? opponentPlayer.getUsername() : "Player O");

        chatSession.sendMessage(sender, message);

        // Check the most recent message in history to see if it passed the filter
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
     * Adds a message to the chat view.
     * Uses inline styles to force the text color and a 3px left border so that the
     * message appearance remains consistent based on the sender.
     *
     * @param sender   the sender of the message
     * @param text     the message text
     * @param isSystem if true, applies system message styling
     */
    public void addMessage(String sender, String text, boolean isSystem) {
        if (chatMessages == null) return;

        ChatMessage lastMessage = null;
        for (ChatMessage msg : chatSession.chatManager.getChatHistory()) {
            if (msg.getPlayerId().equals(sender) && msg.getMessage().equals(text)) {
                lastMessage = msg;
                break;
            }
        }

        // Mark the message as read if found
        if (lastMessage != null) {
            lastMessage.markAsRead(String.valueOf(LoginController.loginId));
            writeChatHistoryToCSV(); // Save to CSV
        }

        // Create message container
        HBox messageContainer = new HBox(5);
        messageContainer.getStyleClass().add("chat-message");

        // Determine the neon border color
        String colorCode = isSystem ? "#ffff00" :
                (sender.equalsIgnoreCase(localPlayer.getUsername()) ? "#ff00ff" :
                        sender.equalsIgnoreCase(opponentPlayer.getUsername()) ? "#00ffff" : "#ffffff");

        messageContainer.setStyle("-fx-border-width: 0 0 0 3px; -fx-border-color: " + colorCode + ";");

        // Build sender label
        Label senderLabel = new Label(sender + ":");

        senderLabel.setFont(Fonts.rajdhani(FontWeight.BOLD, 14));
        senderLabel.setStyle("-fx-text-fill: " + colorCode + ";");

        // Build message label
        Label messageLabel = new Label(text);
        messageLabel.setFont(Fonts.rajdhaniRegular(14));
        messageLabel.setStyle("-fx-text-fill: " + colorCode + ";");

        // Add to chat box
        messageContainer.getChildren().addAll(senderLabel, messageLabel);
        chatMessages.getChildren().add(messageContainer);
        messageCount++;

        // Auto-scroll to bottom
        if (chatScrollPane != null) {
            Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
        }
    }



    private void startTurnTimer() {
        turnCheckTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (gameOver) return;

            TurnTimer currentTimer = playerXTurn ? timerX : timerO;
            long elapsedTime = System.currentTimeMillis() - currentTimer.getStartTime();
            long remainingMillis = currentTimer.getRemainingTime() - elapsedTime;
            int remainingSec = (int) (remainingMillis / 1000);

            // Show chat warning once if time drops to 10 or less
            if (remainingSec <= 10) {
                if (playerXTurn && !warningSentX) {
                    addMessage("SYSTEM", "⚠ " + localPlayer.getUsername() + " has 10 seconds left!", true);
                    warningSentX = true;
                } else if (!playerXTurn && !warningSentO) {
                    addMessage("SYSTEM", "⚠ " + opponentPlayer.getUsername() + " has 10 seconds left!", true);
                    warningSentO = true;
                }
            }

            // Time's up — end game
            if (currentTimer.isTimeExpired()) {
                String loser = playerXTurn ? localPlayer.getUsername() : opponentPlayer.getUsername();
                String winner = playerXTurn ? opponentPlayer.getUsername() : localPlayer.getUsername();
                if (playerXTurn) {
                    // Player X's time expired, Player O wins
//                    scorePlayerO++; // Increment score for Player O
//                    score2.setText("Score: " + scorePlayerO); // Update UI for Player O's score
                    // updates the result, opponent player wins
                    gameProcessor.UpdateResults(opponentPlayer, localPlayer, gameType);
                } else {
                    //
                    // Player O's time expired, Player X wins
//                    scorePlayerX++; // Increment score for Player X
//                    score1.setText("Score: " + scorePlayerX); // Update UI for Player X's score
                    // updates the result, local player wins
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


    private void stopTurnTimer() {
        if (turnCheckTimeline != null) {
            turnCheckTimeline.stop();
        }
    }

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