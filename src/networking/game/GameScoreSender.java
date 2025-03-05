package networking.game;

import java.util.List;

/**
 * Handles sending player scores to the server when a game ends.
 */
public class GameScoreSender extends GameNetworking{

    private String gameId;// Unique identifier for the game session
    private List<String> gameScore;

    /**
     * Constructor to initialize the score manager.
     *
     * @param gameId The unique ID of the game session.
     */
    public GameScoreSender(String gameId) {
        super(gameId);
        // Initialize necessary configurations
    }

    /**
     * Sends the final player scores to the server when the game ends.
     *
     * @param playerId The ID of the player sending the message.
     * @param score The score of a player
     */
    public boolean sendScores(String playerId, int score) {
        // Format and add score to gameScore
        // send the scores  to the server via HTTP or WebSocket
        // checks if the game has ended
        // if Game has ended each player score will be sent to the server
        return true;
    }

    /**
     * Receives confirmation from the server after scores are successfully sent.
     *
     * @param response The server response indicating success or failure.
     */

    /**
     * Checks if the game has ended.
     * @param gameId The unique ID of the game session.
     * @return true if the game is over, false otherwise.
     */
    public boolean isGameOver(String gameId ) {
        return true; // Replace with actual game-over check
    }

    /**
     * Establishes a connection to the server for sending scores.
     */
    @Override
    public void establishConnection() {
        // Initialize WebSocket or networking connection for real-time chat
        // Handle connection setup logic
    }

    /**
     * Closes the connection to server after sending scores.
     */
    @Override
    public void closeConnection() {
        // Gracefully close the WebSocket or networking connection
        // Ensure proper cleanup of resources
}

    /**
     * Handles network-related errors, such as disconnections or timeouts.
     *
     * @param errorMessage The error message or exception details.
     */
@Override
public void handleNetworkError(String errorMessage) {
    // Log the error message
    // Attempt to reconnect if necessary
}

}
