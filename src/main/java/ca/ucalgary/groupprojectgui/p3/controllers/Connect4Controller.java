package ca.ucalgary.groupprojectgui.p3.controllers;
import MatchmakingLeaderboard.Connect4.Matchmaking.Connect4Matchmaking;
import MatchmakingLeaderboard.Player;
import gameLogic.connect4.ConnectBoard;
import gameLogic.connect4.Connect4;


import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Connect4Controller {

    @FXML
    private StackPane boardContainer;

    @FXML
    private HBox player1DiscContainer;

    @FXML
    private HBox player2DiscContainer;

    @FXML
    private BorderPane mainGamePane;

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField chatInput;

    @FXML
    private Circle turnDisc;

    @FXML
    private Label turnLabel;

    private int player1Id;
    private int player2Id;

    private GridPane boardGrid;
    private Rectangle glowRect;

    private ConnectBoard connectBoard;
    private Connect4 gameLogic;

    // Connect 4 board is 7 columns by 6 rows
    private static final double ASPECT_RATIO = 7.0 / 6.0;

    // We'll scale the board to 90% of the container’s size
    private static final double BOARD_CONTAINER_SCALE = 0.9;

    // Increased margin inside the rectangle so circles aren’t flush with the edges
    private static final double BOARD_MARGIN = 20.0;

    @FXML
    public void initialize() throws Exception {
        // Create and add the player discs to the left sidebar
        Circle redDisc = new Circle(25);
        redDisc.getStyleClass().add("disc-red");
        player1DiscContainer.getChildren().add(redDisc);

        Circle cyanDisc = new Circle(25);
        cyanDisc.getStyleClass().add("disc-cyan");
        player2DiscContainer.getChildren().add(cyanDisc);

        // Create the grid for the Connect 4 board\
        connectBoard = new ConnectBoard(1,2);
        gameLogic = new Connect4(connectBoard);
        
        boardGrid = new GridPane();
        boardGrid.setHgap(10);  // horizontal spacing between circles
        boardGrid.setVgap(10);  // vertical spacing
        boardGrid.setAlignment(Pos.CENTER);

        // Populate the grid with circles
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                Circle slot = new Circle(30); // initial radius (will be updated)
                slot.getStyleClass().add("empty-slot");
                int finalCol = col;
                slot.setOnMouseClicked(e -> handleMove(finalCol));
                boardGrid.add(slot, col, row);
            }
        }

        // Create the glow rectangle behind the grid
        glowRect = new Rectangle();
        glowRect.setArcWidth(30);
        glowRect.setArcHeight(30);
        glowRect.getStyleClass().add("glow-rect");

        // Add the rectangle and the grid to the same StackPane
        boardContainer.getChildren().addAll(glowRect, boardGrid);

        // Listen for boardContainer resizing
        boardContainer.widthProperty().addListener((obs, oldVal, newVal) -> updateBoardLayout());
        boardContainer.heightProperty().addListener((obs, oldVal, newVal) -> updateBoardLayout());

        int p1 = LoginController.loginId;

        Connect4Matchmaking c4m = new Connect4Matchmaking();
        Player p = new Player("hihi", 1, 123457);
        Player p3 = new Player("dhfi",1, 123456 );

        c4m.joinQueue(p);
        c4m.joinQueue(p3);
        int p2 = c4m.findMatch(p1);
        this.player1Id = p1;
        this.player2Id = p2;

    }


    private void handleMove(int column) {
        if (gameLogic.isGameOver() || !gameLogic.canPlay(connectBoard.getBoard(), column)) return;

        int piece = connectBoard.getCurrentPlayer();
        int row = Connect4.play(connectBoard.getBoard(), column, piece);

        if (row >= 0) {
            dropDiscAt(row, column, piece == connectBoard.piece1 ? "red" : "cyan");

            if (gameLogic.won(connectBoard.getBoard(), piece)) {
                gameLogic.setGameOver(true);
                int winnerId = (piece == connectBoard.piece1) ? player1Id : player2Id;
                turnLabel.setText("Player ID " + winnerId + " WINS!");

                // OPTIONAL: Update stats
            /*
            Player winner = PlayerDatabase.getPlayerByUserID(winnerId);
            int loserId = (piece == connectBoard.piece1) ? player2Id : player1Id;
            Player loser = PlayerDatabase.getPlayerByUserID(loserId);

            if (winner != null) {
                winner.addWin();
                PlayerDatabase.savePlayer(winner);
            }
            if (loser != null) {
                loser.addLoss();
                PlayerDatabase.savePlayer(loser);
            }
            */

            } else if (gameLogic.isFull(connectBoard.getBoard())) {
                turnLabel.setText("TIE GAME");

                // OPTIONAL: Update draw stats
            /*
            Player player1 = PlayerDatabase.getPlayerByUserID(player1Id);
            Player player2 = PlayerDatabase.getPlayerByUserID(player2Id);
            if (player1 != null && player2 != null) {
                player1.addDraw();
                player2.addDraw();
                PlayerDatabase.savePlayer(player1);
                PlayerDatabase.savePlayer(player2);
            }
            */
            } else {
                gameLogic.switchPlayer();
                turnLabel.setText("TURN");
                updateTurnDisc();
            }
        }
    }

    private void updateTurnDisc() {
        int currentPlayer = connectBoard.getCurrentPlayer();
        turnDisc.getStyleClass().clear();
        if (currentPlayer == connectBoard.piece1) {
            turnDisc.getStyleClass().add("disc-red");
        } else {
            turnDisc.getStyleClass().add("disc-cyan");
        }
    }

    @FXML
    private void onLeaveGame() {
        // Blur only the main game pane (not the entire StackPane)
        BoxBlur blur = new BoxBlur(10, 10, 3);
        mainGamePane.setEffect(blur);

        // Grab the root (StackPane) so we can place our overlay on top
        StackPane rootPane = (StackPane) mainGamePane.getScene().getRoot();

        // Create a semi-transparent overlay
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        overlay.prefWidthProperty().bind(rootPane.widthProperty());
        overlay.prefHeightProperty().bind(rootPane.heightProperty());

        // Create your modal content
        VBox modal = new VBox(15);
        modal.setAlignment(Pos.CENTER);
        modal.setPadding(new Insets(20));
        modal.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-background-radius: 10;");
        modal.setMinWidth(300);

        Label prompt = new Label("Pause Menu");
        prompt.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        // Example Buttons
        Button resumeButton = new Button("Resume");
        Button saveAndQuitButton = new Button("Save & Quit");
        Button quitWithoutSavingButton = new Button("Quit Without Saving");
        Button cancelButton = new Button("Cancel");

        // A simple shared style, or you can style each button differently
        String buttonStyle = "-fx-background-color: #5f27cd; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 10; " +
                "-fx-font-weight: bold;";
        resumeButton.setStyle(buttonStyle);
        saveAndQuitButton.setStyle(buttonStyle);
        quitWithoutSavingButton.setStyle(buttonStyle);
        cancelButton.setStyle(buttonStyle);

        // Add them all to the modal
        modal.getChildren().addAll(prompt, resumeButton, saveAndQuitButton, quitWithoutSavingButton, cancelButton);
        overlay.getChildren().add(modal);

        // Add overlay above the current UI
        rootPane.getChildren().add(overlay);
        overlay.toFront();

        // Button actions
        resumeButton.setOnAction(e -> {
            // Simply remove the overlay and clear the blur
            rootPane.getChildren().remove(overlay);
            mainGamePane.setEffect(null);
            // Logic for unpausing goes here, if any
        });

        saveAndQuitButton.setOnAction(e -> {
            // Add your "save game" logic here
            SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/HomePage.fxml", "Home Page", "home.css");

            // Then remove overlay, clear blur
            rootPane.getChildren().remove(overlay);
            mainGamePane.setEffect(null);
            // Maybe load main menu or exit
        });

        quitWithoutSavingButton.setOnAction(e -> {
            // Remove overlay, clear blur
            SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/HomePage.fxml", "Home Page", "home.css");

            rootPane.getChildren().remove(overlay);
            mainGamePane.setEffect(null);
            // Go back to main menu or exit directly
        });

        cancelButton.setOnAction(e -> {
            // “Cancel” here just closes the modal
            rootPane.getChildren().remove(overlay);
            mainGamePane.setEffect(null);
        });
    }


    /**
     * Dynamically update the glow rectangle and circle sizes,
     * preserving a 7:6 aspect ratio and adding margin around the edges.
     * The circles are scaled down by 20% to avoid looking too big.
     */
    private void updateBoardLayout() {
        double containerWidth = boardContainer.getWidth();
        double containerHeight = boardContainer.getHeight();

        if (containerWidth <= 0 || containerHeight <= 0) {
            return;
        }

        // Scale to 90% of the container so there's some outer padding
        double maxUsableWidth = containerWidth * BOARD_CONTAINER_SCALE;
        double maxUsableHeight = containerHeight * BOARD_CONTAINER_SCALE;

        // Decide the boardWidth and boardHeight based on 7:6 ratio
        double containerRatio = maxUsableWidth / maxUsableHeight;
        double boardWidth, boardHeight;

        if (containerRatio > ASPECT_RATIO) {
            // Container is relatively wider, so limit by height
            boardHeight = maxUsableHeight;
            boardWidth = boardHeight * ASPECT_RATIO;
        } else {
            // Container is relatively taller (or equal ratio), so limit by width
            boardWidth = maxUsableWidth;
            boardHeight = boardWidth / ASPECT_RATIO;
        }

        // Set the glow rectangle size
        glowRect.setWidth(boardWidth);
        glowRect.setHeight(boardHeight);

        // Calculate how much horizontal/vertical spacing the grid consumes
        int columns = 7;
        int rows = 6;
        double totalHSpacing = boardGrid.getHgap() * (columns - 1);
        double totalVSpacing = boardGrid.getVgap() * (rows - 1);

        // Subtract the spacing + the BOARD_MARGIN from the rectangle to find the actual circle area
        double circleAreaWidth = boardWidth - totalHSpacing - 2 * BOARD_MARGIN;
        double circleAreaHeight = boardHeight - totalVSpacing - 2 * BOARD_MARGIN;

        // Each cell dimension
        double cellWidth = circleAreaWidth / columns;
        double cellHeight = circleAreaHeight / rows;

        // The circle's radius is half the smaller dimension of the cell,
        // further reduced by 20% (multiply by 0.8)
        double newRadius = (Math.min(cellWidth, cellHeight) / 2.0) * 0.7;

        // Update every circle
        boardGrid.getChildren().forEach(node -> {
            if (node instanceof Circle) {
                ((Circle) node).setRadius(Math.max(0, newRadius));
            }
        });
    }

    @FXML
    private void onSendMessage() {
        String message = chatInput.getText();
        if (!message.trim().isEmpty()) {
            chatArea.appendText("You: " + message + "\n");
            chatInput.clear();
        }
    }

    /**
     * Example method to update a slot with a disc.
     * This removes the "empty-slot" style and applies either "disc-red" or "disc-cyan".
     *
     * @param row       The row index (0-based)
     * @param col       The column index (0-based)
     * @param discColor "red" or "cyan"
     */
    public void dropDiscAt(int row, int col, String discColor) {
        if (boardGrid == null) return;

        boardGrid.getChildren().forEach(node -> {
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);
            if (colIndex == null) colIndex = 0;
            if (rowIndex == null) rowIndex = 0;

            if (colIndex == col && rowIndex == row && node instanceof Circle) {
                Circle disc = (Circle) node;
                disc.getStyleClass().clear();
                if ("red".equalsIgnoreCase(discColor)) {
                    disc.getStyleClass().add("disc-red");
                } else if ("cyan".equalsIgnoreCase(discColor)) {
                    disc.getStyleClass().add("disc-cyan");
                }
            }
        });
    }
}