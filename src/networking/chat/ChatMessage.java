package networking.chat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single chat message within the in-game chat system.
 * Each message contains the player ID, message content, and a timestamp.
 */
public class ChatMessage {
    private String playerId;
    private String message;
    private LocalDateTime timestamp;

    // TODO: Message Timestamps & Read Receipts
    /**
     * Constructs a new ChatMessage object.
     *
     * @param playerId The ID of the player who sent the message.
     * @param message  The content of the message.
     */
    public ChatMessage(String playerId, String message) {
        this.playerId = playerId;
        this.message = message;
        this.timestamp = LocalDateTime.now(); // Capture the exact time of message sending
    }

    /**
     * Retrieves the ID of the player who sent the message.
     *
     * @return The player's ID.
     */
    public String getPlayerId() {
        return playerId;
    }

    /**
     * Retrieves the content of the message.
     *
     * @return The message content.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Retrieves the timestamp when the message was sent.
     *
     * @return The timestamp of the message.
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Formats the chat message for display.
     *
     * @return A formatted string representation of the chat message.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return "[" + timestamp.format(formatter) + "] " + playerId + ": " + message;
    }
}
