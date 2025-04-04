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
     * Clears the chat history.
     */
    public void clearChat() {
        chatManager.clearChatHistory();
    }

    /**
     * Establishes the connection for the chat system.
     * This simulates setting up a WebSocket or another networking protocol.
     */
    @Override
    public void establishConnection() {
        isConnected = true;
        System.out.println("Chat connection established.");
    }

    /**
     * Closes the connection and stops message transmission.
     */
    @Override
    public void closeConnection() {
        isConnected = false;
        System.out.println("Chat connection closed.");
    }

    /**
     * Simulates receiving a message from a player.
     *
     * @param playerId The ID of the player who sent the message.
     * @param message  The message received.
     */
    public void receiveMessage(String playerId, String message) {
        if (!isConnected) {
            System.out.println("Error: Cannot receive message. Chat is not connected.");
            return;
        }

        chatManager.addMessage(playerId, message);
        System.out.println("New message received: " + message);
    }
}