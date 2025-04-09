package networking.game;

import java.util.HashMap;
import java.util.Map;

/**
 * A class that simulates a game server and database for saving/retrieval of games being played.
 *
 * @param <T>
 */
public class GameServer<T> {

    private final Map<String, T> gameDatabase = new HashMap<>();

    /**
     * Save a game state on the server.
     *
     * @param gameId Unique identifier for the game.
     * @param board The game board object to store.
     * @return Boolean value; true if successful, false otherwise.
     */
    public boolean saveGameState(String gameId, T board) {
        if (gameId != null && board != null) {
            gameDatabase.put(gameId, board);
            return true;
        }
        return false;
    }

    /**
     * Retrieve a game state from the server.
     *
     * @param gameId Unique identifier for the game.
     * @return The stored game board object, or null if not found.
     */
    public T getGameState(String gameId) {
        return gameDatabase.get(gameId);
    }

    /**
     * Remove a game from gameDatabase.
     *
     * @param gameId Unique identifier for the game.
     * @return Boolean value; True if the game is removed, false otherwise.
     */
    public boolean removeGame(String gameId) {
        if (gameDatabase.containsKey(gameId)) {
            gameDatabase.remove(gameId);
            return true;
        }
        return false;
    }
}
