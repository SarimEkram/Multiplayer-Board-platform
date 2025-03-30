package ca.ucalgary.groupprojectgui.p3.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class CheckersController {

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

    // Checkers board is 8x8
    private static final int BOARD_ROWS = 8;
    private static final int BOARD_COLUMNS = 8;

    // Margin around the board (for spacing)
    private static final double BOARD_MARGIN = 20.0;

    private GridPane boardGrid;
    private Rectangle boardBackground;

    @FXML
    public void initialize() {
        // Initialize player discs (example pieces for display purposes)
        Circle player1Piece = new Circle(25);
        player1Piece.getStyleClass().add("disc-red");
        player1DiscContainer.getChildren().add(player1Piece);

        Circle player2Piece = new Circle(25);
        player2Piece.getStyleClass().add("disc-black");
        player2DiscContainer.getChildren().add(player2Piece);

        // Initialize the checkers board grid
        boardGrid = new GridPane();
        boardGrid.setAlignment(Pos.CENTER);

        // Create a board background rectangle (for styling or shadow effects)
        boardBackground = new Rectangle();
        boardBackground.getStyleClass().add("board-background");

        // Build an 8x8 grid of alternating colored squares
        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int col = 0; col < BOARD_COLUMNS; col++) {
                Rectangle square = new Rectangle();
                square.getStyleClass().add("board-square");
                // Use alternating colors: light for even-sum cells, dark for odd-sum cells
                if ((row + col) % 2 == 0) {
                    square.setFill(Color.BEIGE);
                } else {
                    square.setFill(Color.BROWN);
                }
                boardGrid.add(square, col, row);
            }
        }

        // Add the background and grid to the board container (StackPane allows overlays)
        boardContainer.getChildren().addAll(boardBackground, boardGrid);

        // Listen for boardContainer resizing to update the board layout dynamically
        boardContainer.widthProperty().addListener((obs, oldVal, newVal) -> updateBoardLayout());
        boardContainer.heightProperty().addListener((obs, oldVal, newVal) -> updateBoardLayout());

        // Make the chat area read-only
        chatArea.setEditable(false);
    }

    /**
     * Dynamically update the board background and square sizes
     * so that the board fits nicely within the container.
     */
    private void updateBoardLayout() {
        double containerWidth = boardContainer.getWidth();
        double containerHeight = boardContainer.getHeight();

        if (containerWidth <= 0 || containerHeight <= 0) return;

        // Use 90% of the smallest container dimension for a square board
        double boardSize = Math.min(containerWidth, containerHeight) * 0.9;

        // Set the board background size
        boardBackground.setWidth(boardSize);
        boardBackground.setHeight(boardSize);

        // Calculate the cell size (subtracting margins from both sides)
        double cellSize = (boardSize - 2 * BOARD_MARGIN) / BOARD_COLUMNS;

        // Update every square in the grid with the calculated cell size
        for (Node node : boardGrid.getChildren()) {
            if (node instanceof Rectangle) {
                ((Rectangle) node).setWidth(cellSize);
                ((Rectangle) node).setHeight(cellSize);
            }
        }
    }

    /**
     * Opens a modal confirmation dialog when the user clicks the Leave Game button.
     */
    @FXML
    private void onLeaveGame() {
        // Blur the main game pane
        BoxBlur blur = new BoxBlur(10, 10, 3);
        mainGamePane.setEffect(blur);

        // Get the root StackPane (to overlay the modal)
        StackPane rootPane = (StackPane) mainGamePane.getScene().getRoot();

        // Create an overlay with a semi-transparent background
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        overlay.prefWidthProperty().bind(rootPane.widthProperty());
        overlay.prefHeightProperty().bind(rootPane.heightProperty());

        // Build the modal dialog
        VBox modal = new VBox(20);
        modal.setAlignment(Pos.CENTER);
        modal.setPadding(new Insets(20));
        modal.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-background-radius: 10;");
        modal.setMinWidth(300);
        modal.setMinHeight(150);
        Label prompt = new Label("Are you sure you want to leave the game?");
        prompt.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        Button yesButton = new Button("Yes, Leave");
        Button cancelButton = new Button("Cancel");
        yesButton.setStyle("-fx-background-color: #5f27cd; -fx-text-fill: white; -fx-background-radius: 10;");
        cancelButton.setStyle("-fx-background-color: #341f97; -fx-text-fill: white; -fx-background-radius: 10;");
        HBox buttonBox = new HBox(10, yesButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);
        modal.getChildren().addAll(prompt, buttonBox);
        overlay.getChildren().add(modal);

        // Add the overlay on top of the UI
        rootPane.getChildren().add(overlay);
        overlay.toFront();

        yesButton.setOnAction(e -> {
            rootPane.getChildren().remove(overlay);
            mainGamePane.setEffect(null);
            // TODO: Implement actual logic to leave the game
        });

        cancelButton.setOnAction(e -> {
            rootPane.getChildren().remove(overlay);
            mainGamePane.setEffect(null);
        });
    }

    /**
     * Handles sending chat messages.
     */
    @FXML
    private void onSendMessage() {
        String message = chatInput.getText();
        if (!message.trim().isEmpty()) {
            chatArea.appendText("You: " + message + "\n");
            chatInput.clear();
        }
    }
}