package ca.ucalgary.groupprojectgui.p3.controllers;

import ca.ucalgary.groupprojectgui.p3.Fonts;
import ca.ucalgary.groupprojectgui.p3.SceneManager;
import gameLogic.connect4.Connect4;
import gameLogic.connect4.ConnectBoard;
import javafx.animation.PauseTransition;
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

import static javafx.scene.paint.Color.rgb;

public class Connect4Controller {

    // FXML elements
    @FXML private Region glowLayer;
    @FXML private Region scanlinesLayer;
    @FXML private VBox chatMessages;
    @FXML private ScrollPane chatScrollPane;  // Scroll pane for chat messages
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

    // Field for the inner cells grid
    private GridPane cellsGrid;

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

    @FXML private Button leaveGameBtn; // Add this if you wire it via FXML

    // Game logic instance
    private ConnectBoard connectBoard;

    // Matchmaking instance and player information
    private Connect4Matchmaking matchmaking;
    private int player1Id;       // Local player's ID (from matchmaking)
    private int opponentId;      // Opponent's player ID
    private Player localPlayer;
    private Player opponentPlayer;

    // Game state and scores
    private boolean gameActive = true;
    private int scorePlayer1 = 0;
    private int scorePlayer2 = 0;

    // Used to track message count for alternating chat message styling
    private int messageCount = 0;

    @FXML
    public void initialize() {
        initializeChat();
        setupHeaderWithSpacing();

        // --- Matchmaking integration ---
        matchmaking = new Connect4Matchmaking();

        if (HomePageController.friendOpponentID==-1) {
            try {

                matchmaking.matchmakingConnect();

                localPlayer = PlayerDatabase.getPlayerByUserID(LoginController.loginId);


                player1Id = localPlayer.getUserID();

                matchmaking.joinQueue(localPlayer);

                for (Player player : PlayerDatabase.getAllPlayers()) {
                    if (player.getGameSignal(2) == 2)
                        matchmaking.joinQueue(player);
                }


                opponentPlayer = matchmaking.findOpponent(localPlayer.getUserID());


            } catch (IOException e) {
                addMessage("SYSTEM", "Matchmaking error: " + e.getMessage(), true);
            }
        }else {
            localPlayer = PlayerDatabase.getPlayerByUserID(LoginController.loginId);
            opponentPlayer = PlayerDatabase.getPlayerByUserID(HomePageController.friendOpponentID);
        }
        // --- End of matchmaking integration ---

        name1.setText(localPlayer != null ? localPlayer.getUsername() : "Player 1");
        name2.setText(opponentPlayer != null ? opponentPlayer.getUsername() : "Player 2");

        // Instantiate game logic. Local player is PLAYER1; opponent is PLAYER2.
        connectBoard = new ConnectBoard(PLAYER1_ID, PLAYER2_ID);

        // Initialize scores on UI.
        score1.setText("Score: " + scorePlayer1);
        score2.setText("Score: " + scorePlayer2);

        setupBoard();
        setupColumnSelectors();
        updatePlayerTurn();

        if (leaveGameBtn != null) {
            leaveGameBtn.setOnAction(e -> showLeaveGameConfirmationPopup());
        }
    }

    /**
     * Sets up the board UI grid.
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

        Connect4 logic = new Connect4(connectBoard);
        if (connectBoard.isGameOver() || logic.won(connectBoard.getBoard(), lastPlayer)) {
            gameActive = false;
            if (logic.won(connectBoard.getBoard(), lastPlayer)) {
                if (lastPlayer == PLAYER1_ID) {
                    scorePlayer1++;
                    score1.setText("Score: " + scorePlayer1);
                } else {
                    scorePlayer2++;
                    score2.setText("Score: " + scorePlayer2);
                }
                addMessage("SYSTEM", playerName + " wins!", true);
                showGameOverPopup(playerName, true);
            } else {
                addMessage("SYSTEM", "It's a draw!", true);
                showGameOverPopup("No one", false);
            }
            return;
        }
        updatePlayerTurn();
    }

    /**
     * Displays an in-scene pop-up overlay (using external CSS classes) showing the game result and current scores.
     *
     * @param winner the winning player's name (or "No one" for a draw)
     * @param isWin  true if there's a win; false for a draw.
     */
    private void showGameOverPopup(String winner, boolean isWin) {
        // Create an overlay pane that covers the current scene (assumes parent is a Pane)
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

        // Instead of an OK button, we now create a Main Menu button.
        Button mainMenuButton = new Button("Main Menu");
        mainMenuButton.getStyleClass().add("popup-button");
        mainMenuButton.setOnAction(e -> {
            // Remove the overlay from the scene.
            ((Pane) connect4Grid.getParent()).getChildren().remove(overlay);
            // Here, instead of resetting the game, navigate to the Main Menu.
            // For example, you might call a method in your application to load the main menu scene.
            goToMainMenu();
        });

        popup.getChildren().addAll(title, message, mainMenuButton);
        overlay.getChildren().add(popup);

        // Add the overlay to the parent container.
        ((Pane) connect4Grid.getParent()).getChildren().add(overlay);
    }

    private void goToMainMenu() {
        // Implement your logic to navigate back to the main menu.
        // For example, switching scenes or showing a different pane.
        SceneManager.switchTo(
                "/ca/ucalgary/groupprojectgui/p3/HomePage.fxml",
                "Home Page",
                "home.css"

        );

    }



    /**
     * Displays an in-scene confirmation overlay asking if the user wants to quit.
     */
    private void showLeaveGameConfirmationPopup() {
        // Create an overlay pane that covers the current scene (assumes the board's parent is a Pane)
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

        // "Yes" button – confirms leaving the game (navigates to Main Menu, for example)
        Button yesButton = new Button("Yes");
        yesButton.getStyleClass().add("popup-button");
        yesButton.setOnAction(e -> {
            // Remove overlay
            ((Pane) connect4Grid.getParent()).getChildren().remove(overlay);
            // Call your leave game logic; for example, navigate to the main menu.
            goToMainMenu();
        });

        // "Cancel" button – cancels and removes the overlay.
        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("popup-button");
        cancelButton.setOnAction(e -> {
            ((Pane) connect4Grid.getParent()).getChildren().remove(overlay);
        });

        // Container for buttons (optional: horizontally arrange them)
        HBox buttonBox = new HBox(15, yesButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        popup.getChildren().addAll(title, message, buttonBox);
        overlay.getChildren().add(popup);

        // Add the overlay to the parent container (assumes the parent's type is Pane)
        ((Pane) connect4Grid.getParent()).getChildren().add(overlay);
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
     * Resets the game board both in game logic and UI.
     *
     * @param fullReset if additional full reset logic is needed
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
        updatePlayerTurn();
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
            addMessage("SYSTEM", "Welcome to Neon Connect 4", true);
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

    public void onPlayerMove(String playerName, int column) {
        addMessage(playerName, "Played in column " + column, false);
    }

    public void onGameEvent(String message) {
        addMessage("SYSTEM", message, true);
    }
}
