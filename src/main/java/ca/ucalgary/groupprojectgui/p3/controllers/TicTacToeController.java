package ca.ucalgary.groupprojectgui.p3.controllers;

import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;

public class TicTacToeController {

    @FXML
    public StackPane turnIndicator;

    @FXML
    public Label gameName;

    @FXML
    private StackPane boardContainer;

    @FXML
    private BorderPane mainGamePane;

    @FXML
    private Label turnLabel;

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField chatInput;

    // Track the current player. True = Player X, False = Player O.
    private boolean playerXTurn = true;

    @FXML
    public void initialize() {
        // Setup the game board on initialization
        createBoard();
        gameName.setText("X-Tic-Tac-Toe-O");
        turnLabel.setText("Player X's Turn");
    }

    /**
     * Create a 3x3 Tic Tac Toe grid and add it to the boardContainer.
     */
    private void createBoard() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        // Create a 3x3 grid
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                StackPane cell = new StackPane();
                cell.getStyleClass().add("ttt-square");
                cell.setPrefSize(200, 200);
                int r = row, c = col;
                cell.setOnMouseClicked((MouseEvent event) -> handleCellClick(r, c, cell));
                grid.add(cell, col, row);
            }
        }
        boardContainer.getChildren().add(grid);
    }

    /**
     * Handle a click on a board cell.
     * @param row the row index of the clicked cell.
     * @param col the column index of the clicked cell.
     * @param cell the StackPane representing the cell.
     */
    private void handleCellClick(int row, int col, StackPane cell) {
        // If the cell is empty, add the player's marker.
        if (cell.getChildren().isEmpty()) {
            if (playerXTurn) {
                // Draw an "X" marker using two lines
                Line line1 = new Line(10, 10, 110, 110);
                line1.getStyleClass().add("ttt-x");
                Line line2 = new Line(110, 10, 10, 110);
                line2.getStyleClass().add("ttt-x");
                cell.getChildren().addAll(line1, line2);
                turnLabel.setText("Player O's Turn");

            } else {
                // Draw an "O" marker as a circle outline using a Circle node
                javafx.scene.shape.Circle circle = new javafx.scene.shape.Circle(50, 50, 55);
                circle.getStyleClass().add("ttt-o");
                cell.getChildren().add(circle);
                turnLabel.setText("Player X's Turn");

            }
            playerXTurn = !playerXTurn;
            // In a complete game, you would check for a win or draw here.
        }
    }

    /**
     * Handle sending a chat message.
     */
    @FXML
    private void onSendMessage(ActionEvent event) {
        String message = chatInput.getText().trim();
        if (!message.isEmpty()) {
            chatArea.appendText("You: " + message + "\n");
            chatInput.clear();
        }
    }

    /**
     * Handle leaving the game.
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
}
