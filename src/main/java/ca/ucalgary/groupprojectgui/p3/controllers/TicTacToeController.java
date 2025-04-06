package ca.ucalgary.groupprojectgui.p3.controllers;

import MatchmakingLeaderboard.GameProcessor;
import MatchmakingLeaderboard.Player;
import MatchmakingLeaderboard.PlayerDatabase;
import MatchmakingLeaderboard.TicTacToe.Matchmaking.TicTacToeMatchmaking;
import ca.ucalgary.groupprojectgui.p3.Fonts;
import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import gameLogic.tictactoe.TicTacToe;
import gameLogic.tictactoe.TicTacToeBoard;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;

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
    private StackPane boardContainer;

    @FXML
    private Label turnLabel;

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField chatInput;

    @FXML
    private Label localPlayerLabel;
    @FXML
    private Label opponentLabel;
    private GameProcessor gameProcessor;

    private TicTacToeBoard logicBoard;
    private TicTacToe gameLogic;

    private GridPane grid;
    private char currentPlayer;

    // Track the current player. True = Player X, False = Player O.
    private boolean playerXTurn = true;

    //Prevents further interaction after game ends
    private boolean gameOver = false;
    private TicTacToeMatchmaking matchmaking;
    private Player localPlayer;
    private int player1Id;       // Local player's ID (from matchmaking)
    private int opponentId;      // Opponent's player ID
    private Player opponentPlayer;
    private final int gameType = 1;
    private Timeline timeline;
    private int secondsElapsed = 0;
    private GridPane tttgrid;
    private int messageCount = 0;

    @FXML
    public void initialize() {

        setupHeaderWithSpacing();
        initializeChat();

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
                    if (player.getGameSignal(gameType) == gameType)
                        matchmaking.joinQueue(player);
                }

                opponentPlayer = matchmaking.findOpponent(localPlayer.getUserID());

            } catch (IOException e) {
                // This needs to be implemented
                // addMessage("SYSTEM", "Matchmaking error: " + e.getMessage(), true);
            }
        } else {
            localPlayer = PlayerDatabase.getPlayerByUserID(LoginController.loginId);
            opponentPlayer = PlayerDatabase.getPlayerByUserID(HomePageController.friendOpponentID);
        }

        // --- End of matchmaking integration ---

        logicBoard = new TicTacToeBoard();
        gameLogic = new TicTacToe(logicBoard);
        gameLogic.start();
        gameProcessor = new GameProcessor(localPlayer, opponentPlayer, gameType);
        currentPlayer = 'X';
        turnLabel.setText("X: " + localPlayer.getUsername() + "'s Turn");
        localPlayerLabel.setText("X: " + localPlayer.getUsername());
        opponentLabel.setText("O: " + opponentPlayer.getUsername());
    }

    /**
     * Create a 3x3 Tic Tac Toe grid and add it to the boardContainer.
     */
    private void createBoard() {
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


    private void handleCellClick(int row, int col, StackPane cell) {
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
                boardContainer.setDisable(true);
                gameProcessor.UpdateResults(localPlayer, opponentPlayer, gameType);
                return;
            } else {
                showGameOverPopup(opponentPlayer.getUsername(), true);
                gameOver = true;
                boardContainer.setDisable(true);
                gameProcessor.UpdateResults(opponentPlayer, localPlayer, gameType);
                return;
            }

        }

        // Check for draw
        if (logicBoard.boardFull()) {
            addMessage("SYSTEM", "It's a draw!", true);
            showGameOverPopup("No one", false);
            gameOver = true;
            boardContainer.setDisable(true);
            gameProcessor.ProcessDraw(localPlayer, opponentPlayer, gameType);
            return;
        }

        // Next turn
        playerXTurn = !playerXTurn;
        gameLogic.changeActivePlayer();
        turnLabel.setText(playerXTurn ? "X: " + localPlayer.getUsername() + "'s Turn" : "O: " + opponentPlayer.getUsername() + "'s Turn");
    }

    private void showGameOverPopup(String winner, boolean isWin) {
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
            tttgrid.getChildren().remove(overlay);
            SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/HomePage.fxml", "Home Page", "home.css");
        });

        popup.getChildren().addAll(title, message, mainMenuButton);
        overlay.getChildren().add(popup);
        ((Pane) tttgrid.getParent()).getChildren().add(overlay);
    }

    private void onLeaveGame() {
        StackPane overlay = new StackPane();
        overlay.getStyleClass().add("popup-overlay");
        overlay.setPrefSize(tttgrid.getWidth(), tttgrid.getHeight());
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
            tttgrid.getChildren().remove(overlay);
            SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/HomePage.fxml", "Home Page", "home.css");
        });
        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("popup-button");
        cancelButton.setOnAction(e -> {
            tttgrid.getChildren().remove(overlay);
        });
        HBox buttonBox = new HBox(15, yesButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);
        popup.getChildren().addAll(title, message, buttonBox);
        overlay.getChildren().add(popup);
        tttgrid.getChildren().add(overlay);
    }

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
            addMessage("SYSTEM", "Welcome to Neon Checkers", true);
            addMessage("SYSTEM", "Game initialized", true);
        } catch (Exception e) {
            System.err.println("Error initializing chat: " + e.getMessage());
        }
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
        addMessage("You", message, false);
        chatInput.clear();
        Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
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