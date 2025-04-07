package networking.chat;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ChatMessageTest {

    // Test 1: Basic creation and getters
    @Test
    void testChatMessageCreation() {
        ChatMessage message = new ChatMessage("Player1", "Hello");
        assertEquals("Player1", message.getPlayerId());
        assertEquals("Hello", message.getMessage());
        assertNotNull(message.getTimestamp());
    }

    // Test 2: Mark message as read and check reader
    @Test
    void testMarkAsReadAndCheck() {
        ChatMessage message = new ChatMessage("Player1", "Hi");
        message.markAsRead("Player2");
        assertTrue(message.isReadBy("Player2"));
    }

    // Test 3: Unread message check
    @Test
    void testUnreadMessage() {
        ChatMessage message = new ChatMessage("Player1", "Test");
        assertFalse(message.isReadBy("Player3"));
    }

    // Test 4: Read by multiple players
    @Test
    void testMultipleReads() {
        ChatMessage message = new ChatMessage("Player1", "Move");
        message.markAsRead("P1");
        message.markAsRead("P2");
        message.markAsRead("P3");

        Set<String> readers = message.getReaders();
        assertEquals(3, readers.size());
        assertTrue(readers.contains("P1"));
        assertTrue(readers.contains("P2"));
        assertTrue(readers.contains("P3"));
    }

    // Test 5: Repeated markAsRead should not duplicate entries
    @Test
    void testMarkAsReadIsIdempotent() {
        ChatMessage message = new ChatMessage("Player1", "Unit test message");
        message.markAsRead("Player2");
        message.markAsRead("Player2"); // Repeat

        Set<String> readers = message.getReaders();
        assertEquals(1, readers.size());
        assertTrue(readers.contains("Player2"));
    }

    // Test 6: Marking many players as read (simulate full room read)
    @Test
    void testMarkAsReadMultiplePlayers() {
        ChatMessage message = new ChatMessage("Host", "Game start");
        for (int i = 1; i <= 100; i++) {
            message.markAsRead("Player" + i);
        }
        assertEquals(100, message.getReaders().size());
        assertTrue(message.isReadBy("Player42"));
    }

    // Test 7: Empty player ID handling (if allowed)
    @Test
    void testMarkAsReadWithEmptyString() {
        ChatMessage message = new ChatMessage("P1", "Testing edge case");
        message.markAsRead("");
        assertTrue(message.isReadBy(""));
    }

    // Test 8: Null player ID – should throw or handle safely
    @Test
    void testMarkAsReadWithNull() {
        ChatMessage message = new ChatMessage("P1", "Null ID test");

        assertThrows(NullPointerException.class, () -> {
            message.markAsRead(null);
        });
    }

    // Test 9: Long player ID and message
    @Test
    void testVeryLongPlayerIdAndMessage() {
        String longPlayerId = "Player_" + "X".repeat(1000);
        String longMessage = "M".repeat(5000);

        ChatMessage message = new ChatMessage(longPlayerId, longMessage);
        assertEquals(longPlayerId, message.getPlayerId());
        assertEquals(longMessage, message.getMessage());
        assertTrue(message.toString().contains("Read by"));
    }

    // Test 10: Reader ID case sensitivity
    @Test
    void testReaderIdCaseSensitivity() {
        ChatMessage message = new ChatMessage("Player1", "Check case");
        message.markAsRead("player2");
        message.markAsRead("Player2");

        assertTrue(message.isReadBy("player2"));
        assertTrue(message.isReadBy("Player2"));
        assertEquals(2, message.getReaders().size());
    }
}
