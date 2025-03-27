package ca.ucalgary.groupprojectgui.p3.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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
    private TextArea chatArea;

    @FXML
    private TextField chatInput;

    @FXML
    public void initialize() {
        // Create and add the player discs to the left sidebar
        Circle redDisc = new Circle(25); // adjust the radius as needed
        redDisc.getStyleClass().add("disc-red");
        player1DiscContainer.getChildren().add(redDisc);

        Circle cyanDisc = new Circle(25);
        cyanDisc.getStyleClass().add("disc-cyan");
        player2DiscContainer.getChildren().add(cyanDisc);

        // Create a grid layout for the Connect 4 board
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        // Create the board slots with the default "empty-slot" style defined in CSS
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                Circle slot = new Circle(30);
                slot.getStyleClass().add("empty-slot");
                grid.add(slot, col, row);
            }
        }

        // Optional: Create a glow background behind the grid (using a CSS style)
        Rectangle glow = new Rectangle(700, 600);
        glow.setArcWidth(30);
        glow.setArcHeight(30);
        glow.getStyleClass().add("glow-rect");

        boardContainer.getChildren().addAll(glow, grid);
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
     * This method removes the "empty-slot" style and applies either "disc-red" or "disc-cyan".
     *
     * @param row       The row index where the disc is dropped.
     * @param col       The column index where the disc is dropped.
     * @param discColor Either "red" or "cyan".
     */
    public void dropDiscAt(int row, int col, String discColor) {
        // Find the grid (assumes the grid is added as one of the children in boardContainer)
        GridPane grid = null;
        for (var node : boardContainer.getChildren()) {
            if (node instanceof GridPane) {
                grid = (GridPane) node;
                break;
            }
        }
        if (grid != null) {
            // Locate the Circle node at the given grid coordinates
            for (var node : grid.getChildren()) {
                Integer colIndex = GridPane.getColumnIndex(node);
                Integer rowIndex = GridPane.getRowIndex(node);
                // Handle possible nulls in index
                if (colIndex == null) { colIndex = 0; }
                if (rowIndex == null) { rowIndex = 0; }
                if (colIndex == col && rowIndex == row && node instanceof Circle) {
                    Circle disc = (Circle) node;
                    disc.getStyleClass().clear();  // Remove any previous style
                    if ("red".equalsIgnoreCase(discColor)) {
                        disc.getStyleClass().add("disc-red");
                    } else if ("cyan".equalsIgnoreCase(discColor)) {
                        disc.getStyleClass().add("disc-cyan");
                    }
                    break;
                }
            }
        }
    }
}
