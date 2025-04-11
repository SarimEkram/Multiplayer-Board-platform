package ca.ucalgary.groupprojectgui.p3.controllers;

import static org.junit.jupiter.api.Assertions.*;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import networking.chat.ChatManager;
import networking.chat.ChatMessage;
import networking.chat.InGameChat;
import gameLogic.connect4.ConnectBoard;

public class Connect4ControllerTest {

    private Connect4Controller controller;

    @BeforeAll
    static void initJavaFX() throws Exception {
        // Initialize the JavaFX toolkit.
        new JFXPanel();
    }

    @BeforeEach
    void setUp() {
        controller = new Connect4Controller();

        // Set up FXML-injected fields.
        controller.name1 = new Label("PLAYER1");
        controller.name2 = new Label("PLAYER2");
        controller.movesCount = new Label();
        controller.timeElapsed = new Label();
        controller.cellsGrid = new GridPane();
        controller.chatMessages = new VBox();
        controller.chatScrollPane = new ScrollPane(controller.chatMessages);
        controller.connect4Grid = new GridPane();  // Used for displaying pop-ups, etc.

        // Initialize the ConnectBoard (using dummy player IDs 1 and 2).
        controller.connectBoard = new ConnectBoard(1, 2);

        // Populate the cellsGrid with dummy Circle nodes (for a 6x7 board).
        int rows = 6;
        int columns = 7;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                Circle dummyCell = new Circle(30);
                dummyCell.setEffect(new DropShadow());
                controller.cellsGrid.add(dummyCell, col, row);
            }
        }

        // Initialize the chatSession with a dummy implementation.
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
                // No operation during tests.
            }

            @Override
            public void sendMessage(String sender, String message) {
                chatManager.getChatHistory().add(new ChatMessage(sender, message));
            }
        };
    }

    @Test
    void testUpdateCellSetsCorrectColorForPlayer1() throws Exception {
        // Prepare a fresh dummy Circle at position (0,0).
        Circle circle = new Circle(30);
        circle.setEffect(new DropShadow());
        // Clear the grid and add only this circle.
        controller.cellsGrid.getChildren().clear();
        controller.cellsGrid.add(circle, 0, 0);

        // Update the cell on the FX thread.
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.updateCell(0, 0, 1);
            latch.countDown();
        });
        assertTrue(latch.await(3, TimeUnit.SECONDS), "Timeout waiting for updateCell");

        // Expected color for player 1 (neon pink: 255, 0, 255).
        Color expected = Color.rgb(255, 0, 255);
        Color actual = (Color) circle.getFill();
        assertEquals(expected, actual, "Player 1 color should be neon pink (255, 0, 255)");
    }

    @Test
    void testUpdateCellSetsCorrectColorForPlayer2() throws Exception {
        // Prepare a fresh dummy Circle at position (0,0).
        Circle circle = new Circle(30);
        circle.setEffect(new DropShadow());
        controller.cellsGrid.getChildren().clear();
        controller.cellsGrid.add(circle, 0, 0);

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.updateCell(0, 0, 2);
            latch.countDown();
        });
        assertTrue(latch.await(3, TimeUnit.SECONDS), "Timeout waiting for updateCell");

        // Expected color for player 2 (cyan: 0, 255, 255).
        Color expected = Color.rgb(0, 255, 255);
        Color actual = (Color) circle.getFill();
        assertEquals(expected, actual, "Player 2 color should be cyan (0, 255, 255)");
    }

    @Test
    void testStartTimerInitialLabelCorrect() throws Exception {
        // Start the timer on the FX thread.
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.startTimer();
            latch.countDown();
        });
        assertTrue(latch.await(3, TimeUnit.SECONDS), "Timeout waiting for startTimer");

        // Wait a short time to allow the label to update.
        Thread.sleep(100);
        String labelText = controller.timeElapsed.getText();
        assertTrue(labelText.contains("⏳ TURN TIME: 00:00"),
                "Timer initial label should indicate '00:00'");
        Platform.runLater(() -> controller.stopTimer());
    }

    @Test
    void testAddSystemMessageAppendsToChat() throws Exception {
        int initialSize = controller.chatMessages.getChildren().size();
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.addMessage("SYSTEM", "Test Message", true);
            latch.countDown();
        });
        assertTrue(latch.await(3, TimeUnit.SECONDS), "Timeout waiting for addMessage");

        int newSize = controller.chatMessages.getChildren().size();
        assertEquals(initialSize + 1, newSize, "A new chat message should be appended to chatMessages");
    }





    @Test
    void testUpdatePlayerTurnHighlightsCorrectPlayer() throws Exception {
        // Use reflection to access the private updatePlayerTurn() method.
        java.lang.reflect.Method method = Connect4Controller.class.getDeclaredMethod("updatePlayerTurn");
        method.setAccessible(true);

        // Reset the labels.
        controller.name1 = new Label("PLAYER1");
        controller.name2 = new Label("PLAYER2");

        // Set the current player via reflection on the connectBoard.
        java.lang.reflect.Field field = controller.connectBoard.getClass().getDeclaredField("currentPlayer");
        field.setAccessible(true);
        field.set(controller.connectBoard, 1); // Set current player to PLAYER1.

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                method.invoke(controller);
            } catch (Exception e) {
                e.printStackTrace();
            }
            latch.countDown();
        });
        latch.await(3, TimeUnit.SECONDS);

        assertNotNull(controller.name1.getEffect(), "Name1 should have a drop shadow effect for PLAYER1");
        assertNull(controller.name2.getEffect(), "Name2 should have no effect for PLAYER1");
    }


}
