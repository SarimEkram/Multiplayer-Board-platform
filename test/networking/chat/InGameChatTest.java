package networking.chat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.regex.Pattern;

class InGameChatTest {
    private InGameChat chat;
    private ChatManager chatManager;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        chat = new InGameChat("game123");
    }

    // Test 1: Connection Handling
    @Test
    void testConnectionEstablishmentAndClosure() {
        assertFalse(chat.isConnected, "chat should start disconnected.");

        chat.establishConnection();
        assertTrue(chat.isConnected, "chat should be connected after calling establishConnection.");

        chat.closeConnection();
        assertFalse(chat.isConnected, "chat should be disconnected after calling closeConnection.");
    }

    // Test 2: Sending Messages
    @Test
    void testSendMessageWhenConnected() {
        chat.establishConnection();
        chat.sendMessage("Player1", "Hello!");

        List<ChatMessage> history = chat.chatManager.getChatHistory();
        assertEquals(1, history.size(), "chat history should have one message.");
        assertEquals("Hello!", history.get(0).getMessage(), "Message content should match.");
    }

    @Test
    void testSendMessageWhenDisconnected() {
        chat.sendMessage("Player1", "Hello!");
        List<ChatMessage> history = chat.chatManager.getChatHistory();

        assertEquals(0, history.size(), "Message should not be sent when chat is disconnected.");
    }

    // Test 3: Receiving Messages
    @Test
    void testReceiveMessage() {
        chat.establishConnection();
        chat.receiveMessage("Player2", "Hey there!");

        List<ChatMessage> history = chat.chatManager.getChatHistory();
        assertEquals(1, history.size(), "chat history should contain received message.");
        assertEquals("Hey there!", history.get(0).getMessage(), "Received message content should match.");
    }

    @Test
    void testReceiveMessageWhenDisconnected() {
        chat.receiveMessage("Player2", "Hello!");
        List<ChatMessage> history = chat.chatManager.getChatHistory();

        assertEquals(0, history.size(), "Message should not be received when chat is disconnected.");
    }

    // Test 4: Typing Indicator
    @Test
    void testTypingIndicator() {
        chat.startTyping("Player1");
        assertTrue(chat.currentlyTyping.contains("Player1"), "Typing set should contain Player1.");

        chat.stopTyping("Player1");
        assertFalse(chat.currentlyTyping.contains("Player1"), "Typing set should no longer contain Player1.");
    }

    @Test
    void testStopTypingWhenNotTyping() {
        chat.stopTyping("Player1"); // Should not throw errors
        assertFalse(chat.currentlyTyping.contains("Player1"), "Should remain unchanged if player wasn't typing.");
    }

    // Test 5: chat History Management
    @Test
    void testClearChatHistory() {
        chat.establishConnection();
        chat.sendMessage("Player1", "First Message");
        chat.sendMessage("Player2", "Second Message");

        assertEquals(2, chat.chatManager.getChatHistory().size(), "chat should have two messages.");

        chat.clearChat();
        assertEquals(0, chat.chatManager.getChatHistory().size(), "chat should be empty after clearing.");
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut); // Restore original System.out
    }

    // Test 6: No messages added
    @Test
    void testDisplayChatHistory_Empty() {
        chat.displayChatHistory();

        String output = outContent.toString().trim();
        assertEquals("", output);
    }

    @Test
    void testDisplayEmptyChatHistory() {
        chat.displayChatHistory();
        String output = outContent.toString().trim();
        assertEquals("No chat history available.", output,
                "Should display message for empty chat history");
    }

    @Test
    void testDisplaySingleMessageHistory() {
        chat.establishConnection();
        chat.sendMessage("Player1", "Hello world!");

        outContent.reset(); // Clear previous output
        chat.displayChatHistory();

        String output = outContent.toString().trim();
        assertTrue(output.startsWith("chat History:"), "Should display chat history header");
        assertTrue(output.contains("Player1: Hello world!"),
                "Should display the sent message in history");
        assertTrue(Pattern.compile("\\[\\d{2}:\\d{2}:\\d{2}\\]").matcher(output).find(),
                "Should contain timestamp");
    }

    @Test
    void testDisplayMultipleMessagesHistory() {
        chat.establishConnection();
        chat.sendMessage("Player1", "First message");
        chat.sendMessage("Player2", "Second message");
        chat.receiveMessage("Player3", "Third message");

        outContent.reset();
        chat.displayChatHistory();

        String output = outContent.toString().trim();
        assertTrue(output.startsWith("chat History:"), "Should display chat history header");

        // Verify all messages are present with correct format
        assertTrue(Pattern.compile("\\[\\d{2}:\\d{2}:\\d{2}\\] Player1: First message \\(Read by: \\[\\]\\)").matcher(output).find(),
                "Should contain first message with correct format");
        assertTrue(Pattern.compile("\\[\\d{2}:\\d{2}:\\d{2}\\] Player2: Second message \\(Read by: \\[\\]\\)").matcher(output).find(),
                "Should contain second message with correct format");
        assertTrue(Pattern.compile("\\[\\d{2}:\\d{2}:\\d{2}\\] Player3: Third message \\(Read by: \\[\\]\\)").matcher(output).find(),
                "Should contain third message with correct format");

        // Verify order of messages (newest last)
        int firstIndex = output.indexOf("First message");
        int secondIndex = output.indexOf("Second message");
        int thirdIndex = output.indexOf("Third message");
        assertTrue(firstIndex < secondIndex && secondIndex < thirdIndex,
                "Messages should be displayed in chronological order");
    }

    @Test
    void testDisplayAfterClearingHistory() {
        chat.establishConnection();
        chat.sendMessage("Player1", "Test message");
        chat.clearChat();

        outContent.reset();
        chat.displayChatHistory();

        String output = outContent.toString().trim();
        assertEquals("No chat history available.", output,
                "Should display empty message after clearing history");
    }
}
