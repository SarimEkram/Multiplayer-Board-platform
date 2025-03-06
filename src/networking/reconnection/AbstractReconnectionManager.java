package networking.reconnection;

/**
 * Abstract class for handling reconnection attempts in a game.
 * Defines the basic structure for reconnection management.
 */
public abstract class AbstractReconnectionManager {

    protected String gameId;  // Unique identifier for the game session

    /**
     * Constructor to initialize the reconnection manager.
     *
     * @param gameId The unique ID of the game session.
     */
    public AbstractReconnectionManager(String gameId) {
        this.gameId = gameId;
    }

    /**
     * Attempts to reconnect a player to the game session.
     *
     * @param playerId The ID of the player attempting to reconnect.
     * @return true if reconnection is successful, false otherwise.
     */
    public abstract boolean attemptReconnection(String playerId);

    /**
     * Getter for game ID.
     *
     * @return The unique game session ID.
     */
    public String getGameId() {
        return gameId;
    }
}
