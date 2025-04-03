package ca.ucalgary.groupprojectgui.p3.controllers;

import ca.ucalgary.groupprojectgui.p3.Fonts;
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

    // Game logic instance
    private ConnectBoard connectBoard;

    // Matchmaking instance and player information
    private Connect4Matchmaking matchmaking;
    private int player1Id;       // Local player's ID (from matchmaking)
    private int opponentId;      // Opponent's player ID
    private Player localPlayer;
    private Player opponentPlayer;

    // Used to track message count for alternating chat message styling
    private int messageCount = 0;

    @FXML
    public void initialize() {
        initializeChat();
        setupHeaderWithSpacing();

        // --- Matchmaking integration ---
        // Instead of hard coding the player id, we retrieve the local player via the matchmaking system.
        matchmaking = new Connect4Matchmaking();
        try {
            matchmaking.matchmakingConnect();
            // Simulate retrieving the local player from matchmaking.
            // In a real application, matchmaking might provide a method such as getLocalPlayer().
            localPlayer = new Player("LocalUser", 1, 123456);
            // Get the local player ID from the localPlayer instance.
            player1Id = localPlayer.getUserID();
            // Add the local player to the matchmaking queue.
            matchmaking.joinQueue(localPlayer);

            // For demonstration, create an opponent player and add it to the queue.
            Player opponentTemp = new Player("OpponentUser", 1, 123457);
            matchmaking.joinQueue(opponentTemp);

            // Find an opponent for the local player.
            opponentId = matchmaking.findMatch(player1Id);
            // Retrieve the opponent from the player database.
            opponentPlayer = PlayerDatabase.getPlayerByUserID(opponentId);
        } catch (IOException e) {
            addMessage("SYSTEM", "Matchmaking error: " + e.getMessage(), true);
            // You might want to disable online features or retry here.
        }
        // --- End of matchmaking integration ---

        // Set names in the UI using data from matchmaking.
        name1.setText(localPlayer != null ? localPlayer.getUsername() : "Player 1");
        name2.setText(opponentPlayer != null ? opponentPlayer.getUsername() : "Player 2");

        // Instantiate game logic with player identifiers.
        // Here, we assume that the local player is PLAYER1 and the opponent is PLAYER2.
        connectBoard = new ConnectBoard(PLAYER1_ID, PLAYER2_ID);

        // Set up the GUI board (UI grid) and column selectors.
        setupBoard();
        setupColumnSelectors();
        updatePlayerTurn();
    }

    /**
     * Sets up the board UI grid.
     */
    private void setupBoard() {
        connect4Grid.getChildren().clear();

        double hGap = 10, vGap = 10;
        double gridWidth = COLUMNS * CELL_SIZE + (COLUMNS - 1) * hGap;
        double gridHeight = ROWS * CELL_SIZE + (ROWS - 1) * vGap;

        // Main container for board layers
        StackPane boardContainer = new StackPane();
        boardContainer.setPrefSize(gridWidth, gridHeight);

        // 1. Base layer: beautiful gradient background
        Rectangle gradientBackground = new Rectangle(gridWidth, gridHeight);
        gradientBackground.setArcHeight(20);
        gradientBackground.setArcWidth(20);
        gradientBackground.setFill(new LinearGradient(
                1, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(255, 0, 255, 0.1)),
                new Stop(0.5, Color.rgb(0, 255, 255, 0.1)),
                new Stop(1, Color.rgb(255, 0, 255, 0.1))
        ));

        // 2. Semi-transparent black overlay
        Rectangle blackOverlay = new Rectangle(gridWidth, gridHeight);
        blackOverlay.setArcHeight(20);
        blackOverlay.setArcWidth(20);
        blackOverlay.setFill(Color.rgb(0, 0, 0, 0.03));

        // Border effects for the gradient layer
        gradientBackground.setStroke(new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.3, Color.rgb(255, 0, 255, 0.3)),
                new Stop(0.7, Color.rgb(0, 255, 255, 0.3))
        ));
        gradientBackground.setStrokeWidth(2);

        // Shadow effects
        InnerShadow innerShadow = new InnerShadow(BlurType.GAUSSIAN,
                Color.rgb(0, 0, 0, 0.8), 25, 0, 0, 0);
        DropShadow outerGlow = new DropShadow(BlurType.GAUSSIAN,
                Color.rgb(0, 255, 255, 0.2), 40, 0, 0, 0);
        innerShadow.setInput(outerGlow);
        gradientBackground.setEffect(innerShadow);

        // Cells grid for tokens
        cellsGrid = new GridPane();
        cellsGrid.setHgap(hGap);
        cellsGrid.setVgap(vGap);
        cellsGrid.setAlignment(Pos.CENTER);

        // Create UI cells (as Circle objects) and add them to the grid
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                Circle slot = new Circle(30);
                slot.getStyleClass().add("empty-slot");
                slot.setPickOnBounds(true);

                // Default shadow effect
                DropShadow defaultShadow = new DropShadow();
                defaultShadow.setRadius(5);
                defaultShadow.setColor(Color.rgb(255, 255, 255, 0.1));
                defaultShadow.setOffsetX(0);
                defaultShadow.setOffsetY(0);
                slot.setEffect(defaultShadow);

                // Hover effect shadow
                DropShadow hoverShadow = new DropShadow();
                hoverShadow.setRadius(25);
                hoverShadow.setColor(Color.rgb(0, 255, 255, 0.8));
                hoverShadow.setOffsetX(0);
                hoverShadow.setOffsetY(0);

                // Mouse event handlers for hover effects
                slot.setOnMouseEntered(e -> slot.setEffect(hoverShadow));
                slot.setOnMouseExited(e -> slot.setEffect(defaultShadow));

                // Click handler: delegate move processing to handleMove
                int finalCol = col;
                slot.setOnMouseClicked(e -> handleMove(finalCol));

                cellsGrid.add(slot, col, row);
            }
        }

        // Layer order: gradient → overlay → cells grid
        boardContainer.getChildren().addAll(gradientBackground, blackOverlay, cellsGrid);
        connect4Grid.getChildren().add(boardContainer);
    }

    /**
     * Set up the column selectors (clickable areas above the board).
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

            // Add glow effect to the indicator
            DropShadow glow = new DropShadow(15, Color.TRANSPARENT);
            indicator.setEffect(glow);

            selector.getChildren().add(indicator);
            final int column = col;

            // Hover effects for column selectors
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

            selector.setOnMouseClicked(e -> handleMove(column));

            columnSelectors.getChildren().add(selector);
        }
    }

    /**
     * Handles a move when a column is selected.
     */
    private void handleMove(int column) {
        // Delegate move to game logic
        int row = connectBoard.playPiece(column);
        if (row < 0) {
            addMessage("SYSTEM", "Column " + (column + 1) + " is full!", true);
            return;
        }

        // Determine the player that just played.
        // Since playPiece switches the current player upon a valid move,
        // the move was made by the opposite player.
        int lastPlayer = (connectBoard.getCurrentPlayer() == PLAYER1_ID) ? PLAYER2_ID : PLAYER1_ID;
        updateCell(row, column, lastPlayer);
        String playerName = (lastPlayer == PLAYER1_ID ? name1.getText() : name2.getText());
        addMessage(playerName, "Placed token in column " + (column + 1), false);

        // Check if the game is over via game logic
        Connect4 logic = new Connect4(connectBoard);
        if (connectBoard.isGameOver() || logic.won(connectBoard.getBoard(), lastPlayer)) {
            if (logic.won(connectBoard.getBoard(), lastPlayer)) {
                addMessage("SYSTEM", playerName + " wins!", true);
            } else {
                addMessage("SYSTEM", "It's a draw!", true);
            }
            // Optionally, highlight winning cells here

            // Start a new game after a delay
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(e -> resetGame(false));
            delay.play();
            return;
        }
        updatePlayerTurn();
    }

    /**
     * Updates a single cell's UI after a move.
     */
    private void updateCell(int row, int col, int player) {
        // The index is determined by row * COLUMNS + col.
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

        // Clear UI board: set each cell back to empty
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                Circle cell = (Circle) cellsGrid.getChildren().get(row * COLUMNS + col);
                cell.setFill(EMPTY_COLOR);
                // Reset cell effect to default shadow
                DropShadow defaultShadow = new DropShadow();
                defaultShadow.setRadius(5);
                defaultShadow.setColor(Color.rgb(255, 255, 255, 0.1));
                defaultShadow.setOffsetX(0);
                defaultShadow.setOffsetY(0);
                cell.setEffect(defaultShadow);
            }
        }
        updatePlayerTurn();
        // Optionally, update score labels here if needed.
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

        // Underline styling
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

        // Auto-scroll to the bottom after layout pass
        if (chatScrollPane != null) {
            Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
        }
    }

    // Optionally, additional methods for handling game events can be added:
    public void onPlayerMove(String playerName, int column) {
        addMessage(playerName, "Played in column " + column, false);
    }

    public void onGameEvent(String message) {
        addMessage("SYSTEM", message, true);
    }
}
