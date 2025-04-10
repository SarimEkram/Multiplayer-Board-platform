package ca.ucalgary.groupprojectgui.p3.controllers;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import networking.chat.ChatManager;
import networking.chat.ChatMessage;
import networking.chat.InGameChat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Import your controller and related game logic classes.
import ca.ucalgary.groupprojectgui.p3.controllers.CheckersController;
import gameLogic.checkers.Checkers;
import gameLogic.checkers.Checkers.Turn;


public class CheckersControllerTest {

    private CheckersController controller;

    @BeforeAll
    static void initJavaFX() throws InterruptedException {
        // Using a JFXPanel is a safe way to initialize the JavaFX toolkit.
        // If JavaFX is already started by your test runner, this will not re-initialize it.
        new JFXPanel();
        // A short sleep may help ensure the toolkit is fully ready.
        TimeUnit.MILLISECONDS.sleep(200);
    }

    @BeforeEach
    void setUp() {
        // Instantiate the controller.
        controller = new CheckersController();

        // Set up the dummy UI components that would normally be injected via FXML.
        controller.chatMessages = new VBox();
        controller.chatInput = new TextField();
        controller.chatScrollPane = new ScrollPane(new VBox());
        controller.chatHeader = new Label();
        controller.leaveGame = new Button();
        controller.gameTitle = new Label();
        controller.timeElapsed = new Label();
        controller.turnLabel = new Label();
        controller.boardContainer = new StackPane();
        controller.checkerCircle1 = new Circle();
        controller.checkerCircle2 = new Circle();
        controller.player1Name = new Label();
        controller.player2Name = new Label();

        // Create the dummy board cells (8x8) for UI-related tests.
        controller.cellPanes = new StackPane[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                controller.cellPanes[row][col] = new StackPane();
            }
        }

        controller.chatSession = new InGameChat("dummy") {
            public final ChatManager chatManager = new ChatManager() {
                private final List<ChatMessage> history = new ArrayList<>();
                @Override
                public List<ChatMessage> getChatHistory() {
                    return history;
                }
            };

            @Override
            public void establishConnection() {
                // No-op for test
            }

            @Override
            public void sendMessage(String sender, String message) {
                chatManager.getChatHistory().add(new ChatMessage(sender, message));
            }
        };
    }

    @Test
    public void testAddMessageAddsChildToChatMessages() throws InterruptedException {
        int initialCount = controller.chatMessages.getChildren().size();

        Platform.runLater(() -> {
            controller.addMessage("SYSTEM", "Hello, test!", true);
            // The chatMessages container should now contain one additional HBox.
            assertEquals(initialCount + 1, controller.chatMessages.getChildren().size());
            Node msgNode = controller.chatMessages.getChildren().get(initialCount);
            assertTrue(msgNode instanceof HBox, "Message should be contained in an HBox");
        });
        // Wait a short while to allow the UI thread to process.
        TimeUnit.MILLISECONDS.sleep(200);
    }

    @Test
    public void testOnSendMessageWithEmptyInputDoesNothing() throws InterruptedException {
        Platform.runLater(() -> {
            controller.chatInput.setText("   ");
            int initialCount = controller.chatMessages.getChildren().size();
            controller.onSendMessage();
            // No new message should be added when input is empty.
            assertEquals(initialCount, controller.chatMessages.getChildren().size(),
                    "No message should be added when input is empty");
        });
        TimeUnit.MILLISECONDS.sleep(200);
    }

    @Test
    public void testClearHighlightsRemovesHighlightRectangles() throws InterruptedException {
        Platform.runLater(() -> {
            // Prepare one cell with two highlight rectangles and one normal rectangle.
            StackPane cell = controller.cellPanes[0][0];
            Rectangle yellowHighlight = new Rectangle(10, 10);
            yellowHighlight.setFill(Color.rgb(255, 255, 0, 0.3));
            Rectangle blueHighlight = new Rectangle(10, 10);
            blueHighlight.setFill(Color.rgb(0, 0, 255, 0.3));
            Rectangle normalRect = new Rectangle(10, 10);
            normalRect.setFill(Color.RED);
            cell.getChildren().addAll(yellowHighlight, blueHighlight, normalRect);

            controller.clearHighlights();
            // Only the non-highlight (red) rectangle should remain.
            assertEquals(1, cell.getChildren().size());
            assertEquals(normalRect, cell.getChildren().get(0));
        });
        TimeUnit.MILLISECONDS.sleep(200);
    }

    @Test
    public void testUpdateTurnIndicatorSetsCorrectStyles() throws InterruptedException {
        Platform.runLater(() -> {
            // Create a dummy Checkers instance that always returns Turn.BLACK.
            Checkers dummyGameLogic = new Checkers(null) {
                @Override
                public Turn getTurn() {
                    return Turn.BLACK;
                }
            };

            // Use reflection to inject the dummy game logic into the controller.
            try {
                Field gameLogicField = controller.getClass().getDeclaredField("gameLogic");
                gameLogicField.setAccessible(true);
                gameLogicField.set(controller, dummyGameLogic);
            } catch (Exception e) {
                fail("Failed to set gameLogic via reflection: " + e.getMessage());
            }

            // Reinitialize the UI components for the turn indicator.
            controller.turnPiece = new Circle();
            controller.turnLabel = new Label();

            controller.updateTurnIndicator();

            // For Turn.BLACK, the turnPiece should have the style class "checker-black"
            // and the turnLabel should be updated accordingly.
            assertTrue(controller.turnPiece.getStyleClass().contains("checker-black"),
                    "turnPiece should have style 'checker-black' when turn is BLACK");
            assertEquals("Black's TURN", controller.turnLabel.getText(),
                    "turnLabel text should be set correctly for Turn.BLACK");
        });
        TimeUnit.MILLISECONDS.sleep(200);
    }

}
