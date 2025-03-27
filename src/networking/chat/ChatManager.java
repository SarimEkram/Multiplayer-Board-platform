package networking.chat;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the in-game chat system, including message storage, filtering, and retrieval.
 */
public class ChatManager {
    private List<ChatMessage> chatHistory;
    private static final int MAX_MESSAGES = 100; // Limit to prevent memory overflow

    /**
     * Initializes a new ChatManager with an empty chat history.
     */
    public ChatManager() {
        this.chatHistory = new ArrayList<>();
    }

    // TODO: Offline Message Queue & Delayed Delivery, Chat Message Editing & Deleting

    /**
     * Adds a new message to the chat history with basic filtering.
     *
     * @param playerId The player who sent the message.
     * @param message  The content of the message.
     */
    public void addMessage(String playerId, String message) {
        if (message == null || message.trim().isEmpty()) {
            System.out.println("Message cannot be empty!");
            return;
        }

        // Basic filtering (can be extended for profanity filters)
        if (message.toLowerCase().contains("badword")) {
            System.out.println("Warning: Message contains inappropriate content!");
            return;
        }

        // If message history exceeds max limit, remove the oldest message
        if (chatHistory.size() >= MAX_MESSAGES) {
            chatHistory.remove(0);
        }

        ChatMessage chatMessage = new ChatMessage(playerId, message);
        chatHistory.add(chatMessage);
    }

    /**
     * Retrieves the entire chat history.
     *
     * @return A list of ChatMessage objects representing chat history.
     */
    public List<ChatMessage> getChatHistory() {
        return new ArrayList<>(chatHistory); // Return a copy to prevent external modification
    }

    /**
     * Clears all messages in the chat history.
     */
    public void clearChatHistory() {
        chatHistory.clear();
        System.out.println("Chat history has been cleared.");
    }
}
