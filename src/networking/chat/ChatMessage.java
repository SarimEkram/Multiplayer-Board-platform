package networking.chat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a single chat message within the in-game chat system.
 * Each message contains the player ID, message content, a timestamp, and read receipts.
 */
public class ChatMessage {
    private String playerId;
    private String message;
    private LocalDateTime timestamp;
    private Set<String> readByPlayers; // ✅ Track which players have read the message

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
        this.readByPlayers = new HashSet<>();
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
     * Marks this message as read by the specified player.
     *
     * @param readerId The ID of the player who has read the message.
     */
    public void markAsRead(String readerId) {
        readByPlayers.add(readerId);
    }

    /**
     * Checks whether a specific player has read this message.
     *
     * @param playerId The ID of the player to check.
     * @return True if the player has read the message, false otherwise.
     */
    public boolean isReadBy(String playerId) {
        return readByPlayers.contains(playerId);
    }

    /**
     * Retrieves the list of players who have read this message.
     *
     * @return A set of player IDs who have read the message.
     */
    public Set<String> getReaders() {
        return new HashSet<>(readByPlayers);
    }

    /**
     * Formats the chat message for display.
     *
     * @return A formatted string representation of the chat message.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return "[" + timestamp.format(formatter) + "] " + playerId + ": " + message + " (Read by: " + readByPlayers + ")";
    }
}
