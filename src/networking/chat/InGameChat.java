// TODO: Player Typing Indicator

package networking.chat;

import networking.NetworkHandler;

import java.util.List;

/**
 * Handles real-time in-game chat between players during an active game session.
 * Manages sending, receiving, and storing chat messages.
 */
public class InGameChat extends NetworkHandler {
    private String gameId;       // Unique game session identifier
    private ChatManager chatManager;
    private boolean isConnected;

    /**
     * Initializes the in-game chat system for a given game session.
     *
     * @param gameId The unique ID of the game session.
     */
    public InGameChat(String gameId) {
        super(gameId);
        this.gameId = gameId;
        this.chatManager = new ChatManager();
        this.isConnected = false;
    }

    /**
     * Sends a chat message from a player.
     *
     * @param playerId The player who is sending the message.
     * @param message  The content of the message.
     */
    public void sendMessage(String playerId, String message) {
        if (!isConnected) {
            System.out.println("Error: Cannot send message. Chat is not connected.");
            return;
        }

        chatManager.addMessage(playerId, message);
        System.out.println("Message sent: " + message);
    }

    /**
     * Retrieves and displays the full chat history for the session.
     */
    public void displayChatHistory() {
        List<ChatMessage> history = chatManager.getChatHistory();
        if (history.isEmpty()) {
            System.out.println("No chat history available.");
            return;
        }

        System.out.println("Chat History:");
        for (ChatMessage message : history) {
            System.out.println(message);
        }
    }

    /**
     * Retrieves the chat history for the current game session.
     *
     * @return A list of chat messages exchanged during the session.
     */
    public List<String> getChatHistory() {
        // Return the stored chat messages for the game session
        return null;
    }

    /**
     * Establishes a real-time connection for the chat system.
     */
    @Override
    public void establishConnection() {
        // Initialize WebSocket or networking connection for real-time chat
        // Handle connection setup logic
    }

    /**
     * Closes the real-time chat connection.
     */
    @Override
    public void closeConnection() {
        // Gracefully close the WebSocket or networking connection
        // Ensure proper cleanup of resources
    }
}
