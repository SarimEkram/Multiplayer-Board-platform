package ca.ucalgary.groupprojectgui.p3.controllers;

import gameLogic.checkers.Checkers;
import gameLogic.checkers.CheckersBoard;
import gameLogic.checkers.CheckersPiece;
import gameLogic.checkers.CheckersMove;
import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
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
import javafx.util.Duration;

import java.util.ArrayList;

public class CheckersController {

    @FXML
    public Label player1CapturedLabel;

    @FXML
    public Label player2CapturedLabel;

    @FXML
    public Circle turnPiece;

    @FXML
    private Label turnLabel; // Turn indicator label

    @FXML
    private StackPane boardContainer;

    @FXML
    private StackPane checkerPiece1;

    @FXML
    private Circle checkerCircle1;      // Player 1's circle

    @FXML
    private StackPane checkerPiece2;

    @FXML
    private Circle checkerCircle2;      // Player 2's circle

    @FXML
    private BorderPane mainGamePane;

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField chatInput;

    @FXML
    private Label winnerLabel;

    // Board constants
    private static final int BOARD_ROWS = 8;
    private static final int BOARD_COLUMNS = 8;
    private static final double BOARD_MARGIN = 20.0;

    private GridPane boardGrid;
    private Rectangle boardBackground;

    private CheckersBoard checkersBoard;
    private Checkers gameLogic;

    // UI cell mapping for board cells
    private StackPane[][] cellPanes = new StackPane[BOARD_ROWS][BOARD_COLUMNS];

    // For tracking selection and highlighting moves
    private Position selectedPiecePosition = null;
    private boolean isPieceSelected = false;
    private ArrayList<Position> validMovePositions = new ArrayList<>();

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

        Image crown = new Image(getClass().getResourceAsStream("/ca/ucalgary/groupprojectgui/p3/images/crown.png"));
        ImageView crownImage = new ImageView(crown);
        crownImage.setFitWidth(45);
        crownImage.setFitHeight(30);
        crownImage.setVisible(false);

        boardGrid = new GridPane();
        boardGrid.setAlignment(Pos.CENTER);

        boardBackground = new Rectangle();
        boardBackground.getStyleClass().add("board-background");

        // Build board grid and store cells in cellPanes
        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int col = 0; col < BOARD_COLUMNS; col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(80, 80);
                Rectangle square = new Rectangle(80, 80);
                square.getStyleClass().add("board-square");
                if ((row + col) % 2 == 0) {
                    square.setFill(Color.BEIGE);
                } else {
                    square.setFill(Color.BROWN);
                }
                cell.getChildren().add(square);

                // Place initial pieces (using backend positions)
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

        boardContainer.getChildren().addAll(boardBackground, boardGrid);
        boardContainer.widthProperty().addListener((obs, oldVal, newVal) -> updateBoardLayout());
        boardContainer.heightProperty().addListener((obs, oldVal, newVal) -> updateBoardLayout());

        chatArea.setEditable(false);
        updateTurnIndicator();
    }

    private void updateBoardLayout() {
        double containerWidth = boardContainer.getWidth();
        double containerHeight = boardContainer.getHeight();
        if (containerWidth <= 0 || containerHeight <= 0) return;
        double boardSize = Math.min(containerWidth, containerHeight) * 0.9;
        boardBackground.setWidth(boardSize);
        boardBackground.setHeight(boardSize);
        double cellSize = (boardSize - 2 * BOARD_MARGIN) / BOARD_COLUMNS;
        for (Node node : boardGrid.getChildren()) {
            if (node instanceof Rectangle) {
                ((Rectangle) node).setWidth(cellSize);
                ((Rectangle) node).setHeight(cellSize);
            }
        }
    }

    @FXML
    private void onLeaveGame() {
        BoxBlur blur = new BoxBlur(10, 10, 3);
        mainGamePane.setEffect(blur);
        StackPane rootPane = (StackPane) mainGamePane.getScene().getRoot();
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        overlay.prefWidthProperty().bind(rootPane.widthProperty());
        overlay.prefHeightProperty().bind(rootPane.heightProperty());
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

    @FXML
    private void onSendMessage() {
        String message = chatInput.getText();
        if (!message.trim().isEmpty()) {
            chatArea.appendText("You: " + message + "\n");
            chatInput.clear();
        }
    }

    private void handleCellClick(int row, int col) {
        if (isPieceSelected) {
            boolean isValidDestination = false;
            for (Position pos : validMovePositions) {
                if (pos.row == row && pos.col == col) {
                    isValidDestination = true;
                    break;
                }
            }
            if (isValidDestination) {
                CheckersPiece selectedPiece = checkersBoard.board[selectedPiecePosition.row][selectedPiecePosition.col];
                gameLogic.processMove(selectedPiece, selectedPiecePosition.row, selectedPiecePosition.col, row, col);
                clearHighlights();
                isPieceSelected = false;
                selectedPiecePosition = null;
                validMovePositions.clear();
                updateBoardUI();
                Checkers.WINNER winner = gameLogic.checkWin();
                if (winner != Checkers.WINNER.NONE) {
                    checkForWinnerAndShowLabel();
                } else {
                    updateTurnIndicator();
                }
                return;
            } else {
                clearHighlights();
                isPieceSelected = false;
                selectedPiecePosition = null;
                validMovePositions.clear();
            }
        }
        CheckersPiece piece = checkersBoard.board[row][col];
        if (piece != null) {
            if (!isPieceOfCurrentTurn(piece)) {
                return;
            }
            selectedPiecePosition = new Position(row, col);
            isPieceSelected = true;
            int[][] moves = CheckersMove.availableMoves(checkersBoard, piece, row, col);
            for (int[] move : moves) {
                Position pos = new Position(move[0], move[1]);
                validMovePositions.add(pos);
                highlightValidMoveCell(pos.row, pos.col);
            }
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

    private void highlightValidMoveCell(int row, int col) {
        Rectangle tint = new Rectangle();
        tint.setFill(Color.rgb(255, 255, 0, 0.3));
        tint.setMouseTransparent(true);
        tint.widthProperty().bind(cellPanes[row][col].widthProperty());
        tint.heightProperty().bind(cellPanes[row][col].heightProperty());
        if (cellPanes[row][col].getChildren().size() > 0) {
            cellPanes[row][col].getChildren().add(1, tint);
        } else {
            cellPanes[row][col].getChildren().add(tint);
        }
    }

    private void highlightSelectedPieceCell(int row, int col) {
        Rectangle tint = new Rectangle();
        tint.setFill(Color.rgb(0, 0, 255, 0.3));
        tint.setMouseTransparent(true);
        tint.widthProperty().bind(cellPanes[row][col].widthProperty());
        tint.heightProperty().bind(cellPanes[row][col].heightProperty());
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
                        return fill.equals(Color.rgb(255, 255, 0, 0.3)) ||
                                fill.equals(Color.rgb(0, 0, 255, 0.3));
                    }
                    return false;
                });
            }
        }
    }

    private void updateBoardUI() {
        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int col = 0; col < BOARD_COLUMNS; col++) {
                StackPane cell = cellPanes[row][col];
                cell.getChildren().removeIf(node -> node instanceof Circle || node instanceof ImageView);
                CheckersPiece piece = checkersBoard.board[row][col];
                if (piece != null) {
                    Circle pieceCircle = new Circle(35);
                    if (piece.getColour() == CheckersPiece.Colour.WHITE) {
                        pieceCircle.getStyleClass().addAll(checkerCircle2.getStyleClass());
                    } else {
                        pieceCircle.getStyleClass().addAll(checkerCircle1.getStyleClass());
                    }
                    cell.getChildren().add(pieceCircle);
                    if (piece.isKing()) {
                        Image crownImage = new Image(getClass().getResourceAsStream("/ca/ucalgary/groupprojectgui/p3/images/crown.png"));
                        ImageView crownView = new ImageView(crownImage);
                        crownView.setFitWidth(45);
                        crownView.setFitHeight(30);
                        cell.getChildren().add(crownView);

                        // Only play the animation if it is the first time this piece is a king
                        if (!piece.hasAnimatedKing) {
                            ScaleTransition scale = new ScaleTransition(Duration.millis(500), crownView);
                            scale.setFromX(0);
                            scale.setFromY(0);
                            scale.setToX(1);
                            scale.setToY(1);

// Rotate effect (makes it spin when appearing)
                            RotateTransition rotate = new RotateTransition(Duration.millis(500), crownView);
                            rotate.setByAngle(360);

// Play both animations
                            scale.play();
                            rotate.play();
                            piece.setHasAnimatedKing(true);


                        }
                    }
                }
            }
        }
    }

    private void updateTurnIndicator() {
        Checkers.Turn currentTurn = gameLogic.getTurn();
        if (currentTurn == Checkers.Turn.BLACK) {
            turnPiece.getStyleClass().clear();
            turnPiece.getStyleClass().add("checker-black");
            turnLabel.setText("Black's TURN");
        } else if (currentTurn == Checkers.Turn.WHITE) {
            turnPiece.getStyleClass().clear();
            turnPiece.getStyleClass().add("checker-white");
            turnLabel.setText("White's TURN");
        }
    }

    private void checkForWinnerAndShowLabel() {
        Checkers.WINNER winner = gameLogic.checkWin();
        if (winner != Checkers.WINNER.NONE) {
            String message = (winner == Checkers.WINNER.WHITE) ? "White wins!" : "Black wins!";
            turnLabel.setText(message);
            boardGrid.setDisable(true);

            // Override the turn indicator with the winner's piece/color.
            turnPiece.getStyleClass().clear();
            if (winner == Checkers.WINNER.WHITE) {
                turnPiece.getStyleClass().add("checker-white");
                turnLabel.setText("White wins!");
            } else {
                turnPiece.getStyleClass().add("checker-black");
                turnLabel.setText("Black wins!");
            }
        }
    }


}
