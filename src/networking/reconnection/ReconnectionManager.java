package networking.reconnection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Handles reconnection attempts for players who lose connection during an ongoing game.
 */
public class ReconnectionManager extends AbstractReconnectionManager{

    // Flag indicating whether the game session is active.
    private boolean sessionActive;
    // Set of players currently connected.
    private Set<String> connectedPlayers;
    // Set of players that got disconnected.
    private Set<String> disconnectedPlayers;
    // Map to store saved game states for players.
    private Map<String, GameState> savedGameStates;

    /**
     * Constructor to initialize the reconnection manager.
     *
     * @param gameId The unique ID of the game session.
     */
    public ReconnectionManager(String gameId) {
        super(gameId);
        this.connectedPlayers = new HashSet<>();
        this.disconnectedPlayers = new HashSet<>();
        this.savedGameStates = new HashMap<>();
        this.sessionActive = true;
    }

    /**
     * Starts the game session by adding the provided players as connected.
     *
     * @param players A set of player IDs.
     */
    public void startGame(Set<String> players) {
        connectedPlayers.addAll(players);
        System.out.println("Game started with players: " + connectedPlayers);
    }

    /**
     * Simulates saving the current game state for a player.
     *
     * @param playerId The ID of the player.
     * @param state The game state to be saved.
     */
    public void saveGameState(String playerId, GameState state) {
        savedGameStates.put(playerId, state);
        System.out.println("Saved game state for player " + playerId + ": " + state);
    }

    /**
     * Marks a player as disconnected by moving them from the connected set
     * to the disconnected set.
     *
     * @param playerId The ID of the player that disconnected.
     */
    public void playerDisconnected(String playerId) {
        if (connectedPlayers.remove(playerId)) {
            disconnectedPlayers.add(playerId);
            System.out.println("Player " + playerId + " disconnected.");
        } else {
            System.out.println("Player " + playerId + " was not connected.");
        }
    }

    /**
     * Attempts to reconnect a player to the game session.
     *
     * @param playerId The ID of the player attempting to reconnect.
     * @return true if reconnection is successful, false otherwise.
     */
    @Override
    public boolean attemptReconnection(String playerId) {
        // Check if the game session is still active.
        if (!sessionActive) {
            System.out.println("Game session is not active. Cannot reconnect player " + playerId + ".");
            return false;
        }
        if (disconnectedPlayers.contains(playerId)) {
            System.out.println("Reconnecting player " + playerId + "...");
            disconnectedPlayers.remove(playerId);
            connectedPlayers.add(playerId);
            restoreGameState(playerId);
            // Notify other players about the reconnection.
            notifyReconnection(playerId);
            return true;
        } else {
            System.out.println("Player " + playerId + " is not marked as disconnected.");
            return false;
        }
    }

    /**
     * Checks if the game session allows reconnection.
     *
     * @return true if the game allows reconnections, false otherwise.
     */
    public boolean isReconnectionAllowed() {
        return sessionActive;
    }

    /**
     * Restores the game state for the reconnected player.
     *
     * @param playerId The ID of the player who reconnected.
     */
    public void restoreGameState(String playerId) {
        if (savedGameStates.containsKey(playerId)) {
            GameState state = savedGameStates.get(playerId);
            // Logic to apply the saved state to the player's session.
            // For demonstration, we simply print out the restored state.
            System.out.println("Restoring game state for player " + playerId + ": " + state);
            // Optionally, remove the saved state after restoring.
            savedGameStates.remove(playerId);
        } else {
            System.out.println("No saved game state found for player " + playerId + ".");
        }
    }

    /**
     * Notifies other connected players that a player has successfully reconnected.
     *
     * @param playerId The ID of the player who reconnected.
     */
    @Override
    public void notifyReconnection(String playerId) {
        for (String player : connectedPlayers) {
            if (!player.equals(playerId)) {
                System.out.println("Notifying player " + player + " that " + playerId + " has reconnected.");
            }
        }
    }

    /**
     * Handles the case where a player fails to reconnect within the allowed time.
     *
     * @param playerId The ID of the player who failed to reconnect.
     */
    public void handleFailedReconnection(String playerId) {
        disconnectedPlayers.remove(playerId);
        System.out.println("Failed to reconnect player " + playerId + ". Player removed from the session.");
    }

    /**
     * Ends the game session.
     */
    public void endGame() {
        sessionActive = false;
        System.out.println("Game session " + gameId + " ended.");
    }
}
