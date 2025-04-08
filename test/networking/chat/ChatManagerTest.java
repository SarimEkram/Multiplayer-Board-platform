package networking.chat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChatManagerTest {

    private ChatManager chatManager;

    @BeforeEach
    void setUp() {
        chatManager = new ChatManager();
    }

    // Test 1: Add a valid message – should be stored correctly
    @Test
    void testAddValidMessage() {
        chatManager.addMessage("player1", "Hello world!");
        List<ChatMessage> history = chatManager.getChatHistory();
        assertEquals(1, history.size());
        assertEquals("player1", history.get(0).getPlayerId());
        assertEquals("Hello world!", history.get(0).getMessage());
    }

    // Test 2: Add empty message – should not be added
    @Test
    void testAddEmptyMessageShouldNotAdd() {
        chatManager.addMessage("player2", "   ");
        assertTrue(chatManager.getChatHistory().isEmpty());
    }

    // Test 3: Add null message – should not be added
    @Test
    void testAddNullMessageShouldNotAdd() {
        chatManager.addMessage("player3", null);
        assertTrue(chatManager.getChatHistory().isEmpty());
    }

    // Test 4: Add message with a known bad word – should be blocked
    @Test
    void testBadWordFiltering() {
        chatManager.addMessage("player4", "This is a fuck message");
        assertTrue(chatManager.getChatHistory().isEmpty());
    }

    // Test 5: Bad word detection should be case-insensitive
    @Test
    void testBadWordCaseInsensitive() {
        chatManager.addMessage("player5", "ShIt happens");
        assertTrue(chatManager.getChatHistory().isEmpty());
    }

    // Test 6: Exceed max message limit – oldest messages should be removed
    @Test
    void testMaxMessageLimitIsEnforced() {
        for (int i = 0; i < 105; i++) {
            chatManager.addMessage("p" + i, "msg " + i);
        }
        assertEquals(100, chatManager.getChatHistory().size());
        assertEquals("p5", chatManager.getChatHistory().get(0).getPlayerId());
    }

    // Test 7: Clear chat history – should be empty afterwards
    @Test
    void testClearChatHistory() {
        chatManager.addMessage("player6", "Hello!");
        chatManager.clearChatHistory();
        assertTrue(chatManager.getChatHistory().isEmpty());
    }

    // Test 8: Null player ID – should throw or handle safely
    @Test
    void testNullPlayerIdHandled() {
        assertDoesNotThrow(() -> chatManager.addMessage(null, "Hey there!"));
        List<ChatMessage> history = chatManager.getChatHistory();
        assertEquals(1, history.size());
        assertNull(history.get(0).getPlayerId());
    }

    // Test 9: ChatMessage should have a timestamp
    @Test
    void testChatMessageTimestampIsSet() {
        chatManager.addMessage("player7", "Timestamp test");
        ChatMessage msg = chatManager.getChatHistory().get(0);
        assertNotNull(msg.getTimestamp());
    }

    // Test 10: Mark message as read and verify it
    @Test
    void testChatMessageReadTracking() {
        chatManager.addMessage("player8", "Read test");
        ChatMessage msg = chatManager.getChatHistory().get(0);
        assertFalse(msg.isReadBy("playerX"));
        msg.markAsRead("playerX");
        assertTrue(msg.isReadBy("playerX"));
    }

    // Test 11: Fetch chat history and verify order and content
    @Test
    void testGetChatHistoryReturnsCorrectMessages() {
        chatManager.addMessage("player1", "First message");
        chatManager.addMessage("player2", "Second message");
        chatManager.addMessage("player3", "Third message");

        List<ChatMessage> history = chatManager.getChatHistory();

        assertEquals(3, history.size());
        assertEquals("player1", history.get(0).getPlayerId());
        assertEquals("First message", history.get(0).getMessage());

        assertEquals("player2", history.get(1).getPlayerId());
        assertEquals("Second message", history.get(1).getMessage());

        assertEquals("player3", history.get(2).getPlayerId());
        assertEquals("Third message", history.get(2).getMessage());
    }

}