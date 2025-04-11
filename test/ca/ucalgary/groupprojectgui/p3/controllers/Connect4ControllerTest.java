package ca.ucalgary.groupprojectgui.p3.controllers;


import static org.junit.jupiter.api.Assertions.*;


import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.effect.DropShadow;
import javafx.scene.control.ScrollPane;


import networking.chat.ChatManager;
import networking.chat.ChatMessage;
import networking.chat.InGameChat;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.List;


public class Connect4ControllerTest {


    private Connect4Controller controller;


    @BeforeAll
    static void initJavaFX() throws Exception {
        new JFXPanel();  // Initialize JavaFX toolkit
    }


    @BeforeEach
    void setUp() {
        controller = new Connect4Controller();


        controller.name1 = new Label("PLAYER1");
        controller.name2 = new Label("PLAYER2");
        controller.movesCount = new Label();
        controller.timeElapsed = new Label();
        controller.cellsGrid = new GridPane();
        controller.chatMessages = new VBox();
        controller.chatScrollPane = new ScrollPane(controller.chatMessages);


        // Mock chatSession
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
                // No-op
            }


            @Override
            public void sendMessage(String sender, String message) {
                chatManager.getChatHistory().add(new ChatMessage(sender, message));
            }
        };
    }


    @Test
    void testUpdateCellSetsCorrectColorForPlayer1() {
        Circle circle = new Circle();
        circle.setEffect(new DropShadow()); // Mock glow effect


        controller.cellsGrid.add(circle, 0, 0);
        controller.updateCell(0, 0, 1);


        Color fill = (Color) circle.getFill();
        assertEquals(Color.rgb(255, 0, 255), fill);
    }


    @Test
    void testUpdateCellSetsCorrectColorForPlayer2() {
        Circle circle = new Circle();
        circle.setEffect(new DropShadow()); // Mock glow effect


        controller.cellsGrid.add(circle, 0, 0);
        controller.updateCell(0, 0, 2);


        Color fill = (Color) circle.getFill();
        assertEquals(Color.rgb(0, 255, 255), fill);
    }




    @Test
    void testStartTimerInitialLabelCorrect() {
        controller.startTimer();
        String labelText = controller.timeElapsed.getText();
        assertTrue(labelText.contains("⏳ TURN TIME: 00:00"));
    }


    @Test
    void testAddSystemMessageAppendsToChat() {
        int initialSize = controller.chatMessages.getChildren().size();


        controller.addMessage("SYSTEM", "Test Message", true);


        assertEquals(initialSize + 1, controller.chatMessages.getChildren().size());
    }
}