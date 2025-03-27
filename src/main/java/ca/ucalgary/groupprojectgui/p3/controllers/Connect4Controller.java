package ca.ucalgary.groupprojectgui.p3.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Pos;

import java.net.URL;

public class Connect4Controller {

    @FXML
    private StackPane boardContainer;

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField chatInput;

    @FXML
    public void initialize() {
        // Attempt to load resource
        URL resourceUrl = getClass().getResource("/images/futuristic_background.jpg");
        if (resourceUrl == null) {
            System.err.println("Could not find /images/futuristic_background.jpg on the classpath!");
            return;
        }


        // Board background rectangle
        Rectangle boardBackground = new Rectangle(600, 500, Color.BURLYWOOD);
        boardBackground.setArcWidth(20);
        boardBackground.setArcHeight(20);

        // Create board grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                Circle slot = new Circle(30, Color.LIGHTGRAY);
                slot.setStroke(Color.BLACK);
                slot.setStrokeWidth(1.5);
                grid.add(slot, col, row);
            }
        }

        boardContainer.getChildren().addAll(boardBackground, grid);
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
