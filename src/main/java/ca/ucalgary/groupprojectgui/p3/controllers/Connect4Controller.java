package ca.ucalgary.groupprojectgui.p3.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Connect4Controller {

    @FXML
    private StackPane boardContainer;

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField chatInput;

    @FXML
    public void initialize() {
        // Create a grid layout for Connect 4
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                Circle slot = new Circle(30);
                slot.setFill(Color.rgb(30, 30, 30, 0.7)); // semi-dark base
                slot.setStroke(Color.web("#FF00FF"));
                slot.setStrokeWidth(2);
                slot.setEffect(null); // could use DropShadow later
                grid.add(slot, col, row);
            }
        }

        // Optional: Glow background behind grid
        Rectangle glow = new Rectangle(700, 600);
        glow.setArcWidth(30);
        glow.setArcHeight(30);
        glow.setFill(Color.rgb(0, 0, 0, 0.35));
        glow.setStroke(Color.web("#9900ff"));
        glow.setStrokeWidth(3);

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
}
