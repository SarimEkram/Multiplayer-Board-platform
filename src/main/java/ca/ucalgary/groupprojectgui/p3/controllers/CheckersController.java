package ca.ucalgary.groupprojectgui.p3.controllers;

import gameLogic.checkers.Checkers;
import gameLogic.checkers.CheckersBoard;
import gameLogic.checkers.CheckersPiece;
import gameLogic.checkers.CheckersMove;

import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;


import java.util.ArrayList;

public class CheckersController {

    @FXML
    public Label player1CapturedLabel;

    @FXML
    public Label player2CapturedLabel;

    @FXML
    public Circle turnPiece;

    @FXML
    private StackPane boardContainer;

    @FXML
    private StackPane checkerPiece1;

    @FXML
    private Circle checkerCircle1;      // Player 1's circle

    @FXML
    private StackPane checkerPiece2;

    @FXML
    private Circle checkerCircle2;     // Player 2's circle

    @FXML
    private BorderPane mainGamePane;

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField chatInput;

    @FXML
    private Label winnerLabel;

    private StackPane[][] cellPanes = new StackPane[BOARD_ROWS][BOARD_COLUMNS];
    private Position selectedPiecePosition = null;
    private boolean isPieceSelected = false;
    private ArrayList<Position> validMovePositions = new ArrayList<>();


    // Checkers board is 8x8
    private static final int BOARD_ROWS = 8;
    private static final int BOARD_COLUMNS = 8;

    // Margin around the board (for spacing)
    private static final double BOARD_MARGIN = 20.0;

    private GridPane boardGrid;
    private Rectangle boardBackground;

    private CheckersBoard checkersBoard;
    private Checkers gameLogic;

    private Position selectedSquare = null;

    private static class Position {
        int row, col;

        Position(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

        @FXML
    public void initialize() {

        checkersBoard = new CheckersBoard();
        gameLogic = new Checkers(checkersBoard);
        gameLogic.start();

        Image crown;
        crown = new Image(getClass().getResourceAsStream("/ca/ucalgary/groupprojectgui/p3/images/crown.png"));

        // Initially the crowns remain hidden.
        ImageView crownImage = new ImageView(crown);
        crownImage.setFitWidth(45);  // Adjust width as needed
        crownImage.setFitHeight(30);
        crownImage.setVisible(false);

        // Initialize the checkers board grid
        boardGrid = new GridPane();
        boardGrid.setAlignment(Pos.CENTER);

        // Create a board background rectangle (for styling or shadow effects)
        boardBackground = new Rectangle();
        boardBackground.getStyleClass().add("board-background");

        // Build an 8x8 grid of alternating colored squares
        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int col = 0; col < BOARD_COLUMNS; col++) {

                StackPane cell = new StackPane(); // Use StackPane to layer pieces
                cell.setPrefSize(80, 80); // Adjust size as needed

                Rectangle square = new Rectangle(80, 80); // Set initial size
                square.getStyleClass().add("board-square");

                // Use alternating colors: light for even-sum cells, dark for odd-sum cells
                if ((row + col) % 2 == 0) {
                    square.setFill(Color.BEIGE);
                } else {
                    square.setFill(Color.BROWN);
                }

                cell.getChildren().add(square); // Add the square to the StackPane


                if (row < 3 && (row + col) % 2 == 1) {
                    Circle whitePiece = new Circle(35);
                    whitePiece.getStyleClass().addAll(checkerCircle1.getStyleClass());
                    cell.getChildren().add(whitePiece);
                } else if (row > 4 && (row + col) % 2 == 1) {
                    Circle blackPiece = new Circle(35);
                    blackPiece.getStyleClass().addAll(checkerCircle2.getStyleClass());
                    cell.getChildren().add(blackPiece);
                }
                cellPanes[row][col] = cell;
                final int currentRow = row;
                final int currentCol = col;
                cell.setOnMouseClicked(e -> handleCellClick(currentRow, currentCol));
                cell.setOnMouseEntered(e -> handleCellHoverEnter(currentRow, currentCol));
                cell.setOnMouseExited(e -> handleCellHoverExit(currentRow, currentCol));
                boardGrid.add(cell, col, row);
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

            SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/HomePage.fxml", "Home Page", "home.css");
            rootPane.getChildren().remove(overlay);
            mainGamePane.setEffect(null);

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
    private void handleCellClick(int row, int col) {
        // If a piece is already selected, check if clicked cell is a valid move destination.
        if (isPieceSelected) {
            boolean isValidDestination = false;
            for (Position pos : validMovePositions) {
                if (pos.row == row && pos.col == col) {
                    isValidDestination = true;
                    break;
                }
            }

            if (isValidDestination) {
                // Get the selected piece.
                CheckersPiece selectedPiece = checkersBoard.board[selectedPiecePosition.row][selectedPiecePosition.col];
                // Call processMove so that the move is executed and turn is switched.
                gameLogic.processMove(selectedPiece, selectedPiecePosition.row, selectedPiecePosition.col, row, col);

                // Clear the selection and highlights.
                clearHighlights();
                isPieceSelected = false;
                selectedPiecePosition = null;
                validMovePositions.clear();

                // Update the UI after the move.
                updateBoardUI();
                checkForWinnerAndShowLabel();
                // Optionally, update any turn indicators in the UI here.
                return;
            } else {
                // If clicked cell is not valid, cancel the selection.
                clearHighlights();
                isPieceSelected = false;
                selectedPiecePosition = null;
                validMovePositions.clear();
            }
        }

        // If no piece is selected, check if the clicked cell contains a piece.
        CheckersPiece piece = checkersBoard.board[row][col];
        if (piece != null) {
            // Only allow selection if the piece belongs to the current turn.
            if (!isPieceOfCurrentTurn(piece)) {
                return; // Ignore if it isn't the correct player's turn.
            }
            selectedPiecePosition = new Position(row, col);
            isPieceSelected = true;

            // Retrieve valid moves using backend logic.
            int[][] moves = CheckersMove.availableMoves(checkersBoard, piece, row, col);
            for (int[] move : moves) {
                Position pos = new Position(move[0], move[1]);
                validMovePositions.add(pos);
                highlightValidMoveCell(pos.row, pos.col);
            }
            // Optionally, highlight the selected piece.
            highlightSelectedPieceCell(row, col);
        }
    }

    private void handleCellHoverEnter(int row, int col) {
        if (!isPieceSelected) {
            CheckersPiece piece = checkersBoard.board[row][col];
            if (piece != null && isPieceOfCurrentTurn(piece)) {
                int[][] moves = CheckersMove.availableMoves(checkersBoard, piece, row, col);
                for (int[] move : moves) {
                    Position pos = new Position(move[0], move[1]);
                    highlightValidMoveCell(pos.row, pos.col);
                }
                highlightSelectedPieceCell(row, col);
            }
        }
    }

    private void handleCellHoverExit(int row, int col) {
        if (!isPieceSelected) {
            clearHighlights();
        }
    }



    private boolean isPieceOfCurrentTurn(CheckersPiece piece) {
        Checkers.Turn currentTurn = gameLogic.getTurn();
        if (currentTurn == Checkers.Turn.WHITE && piece.getColour() == CheckersPiece.Colour.WHITE) {
            return true;
        } else if (currentTurn == Checkers.Turn.BLACK && piece.getColour() == CheckersPiece.Colour.BLACK) {
            return true;
        }
        return false;
    }


    /**
     * Highlights a cell by modifying its style.
     */
    private void highlightValidMoveCell(int row, int col) {
        Rectangle tint = new Rectangle();
        tint.setFill(Color.rgb(255, 255, 0, 0.3)); // Yellow tint for valid moves
        tint.setMouseTransparent(true);
        tint.widthProperty().bind(cellPanes[row][col].widthProperty());
        tint.heightProperty().bind(cellPanes[row][col].heightProperty());
        // Insert tint at index 1 so that board square remains at index 0 and piece is added later
        if (cellPanes[row][col].getChildren().size() > 0) {
            cellPanes[row][col].getChildren().add(1, tint);
        } else {
            cellPanes[row][col].getChildren().add(tint);
        }
    }

    private void highlightSelectedPieceCell(int row, int col) {
        Rectangle tint = new Rectangle();
        tint.setFill(Color.rgb(0, 0, 255, 0.3)); // Blue tint for the selected piece
        tint.setMouseTransparent(true);
        tint.widthProperty().bind(cellPanes[row][col].widthProperty());
        tint.heightProperty().bind(cellPanes[row][col].heightProperty());
        // Insert at index 1 so that the piece (if added later) appears on top
        if (cellPanes[row][col].getChildren().size() > 0) {
            cellPanes[row][col].getChildren().add(1, tint);
        } else {
            cellPanes[row][col].getChildren().add(tint);
        }
    }

    private void clearHighlights() {
        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int col = 0; col < BOARD_COLUMNS; col++) {
                cellPanes[row][col].getChildren().removeIf(node -> {
                    if (node instanceof Rectangle) {
                        Rectangle rect = (Rectangle) node;
                        Color fill = (Color) rect.getFill();
                        return fill.equals(Color.rgb(255, 255, 0, 0.3)) || fill.equals(Color.rgb(0, 0, 255, 0.3));
                    }
                    return false;
                });
            }
        }
    }


    /**
     * Updates the board UI to reflect the current backend state.
     * This method redraws the pieces on the board.
     */
    private void updateBoardUI() {
        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int col = 0; col < BOARD_COLUMNS; col++) {
                StackPane cell = cellPanes[row][col];
                // Remove any existing piece graphics (Circles, ImageViews, etc.)
                cell.getChildren().removeIf(node -> node instanceof Circle || node instanceof ImageView);

                CheckersPiece piece = checkersBoard.board[row][col];
                if (piece != null) {
                    Circle pieceCircle = new Circle(35);
                    // Instead of using direct colors, we apply CSS style classes.
                    if (piece.getColour() == CheckersPiece.Colour.WHITE) {
                        // Use the CSS styles from the FXML for the "white" pieces (if that's how it's set up)
                        pieceCircle.getStyleClass().addAll(checkerCircle2.getStyleClass());
                    } else {
                        // Use the CSS styles for the "black" pieces
                        pieceCircle.getStyleClass().addAll(checkerCircle1.getStyleClass());
                    }
                    cell.getChildren().add(pieceCircle);

                    // If the piece is a king, overlay the crown image.
                    if (piece.isKing()) {
                        Image crownImage = new Image(getClass().getResourceAsStream("/ca/ucalgary/groupprojectgui/p3/images/crown.png"));
                        ImageView crownView = new ImageView(crownImage);
                        crownView.setFitWidth(45);
                        crownView.setFitHeight(30);
                        cell.getChildren().add(crownView);
                    }
                }
            }
        }
    }

    /**
     * Checks if the game has a winner.
     * If someone has won, shows a message and stops the game.
     */
    private void checkForWinnerAndShowLabel() {
        Checkers.WINNER winner = gameLogic.checkWin();
        if (winner != Checkers.WINNER.NONE) {
            String message = (winner == Checkers.WINNER.WHITE)
                    ? "White wins!"
                    : "Black wins!";
            winnerLabel.setText(message);

            // Optionally disable the board
            boardGrid.setDisable(true);
        }
    }


}
