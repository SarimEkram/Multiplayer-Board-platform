package networking.game;

import networking.NetworkHandler;

/**
 * Handles game-related networking operations (e.g., sending scores, updating board).
 */
public abstract class GameNetworking extends NetworkHandler {

    /**
     * Constructor to initialize the game networking handler.
     *
     * @param gameId The unique ID of the game session.
     */
    public GameNetworking(String gameId) {
        super(gameId);
    }

    // TODO: can implement the below methods in the updateGameBoard and GameScoreSender as both of them send and recieve something
    /**
     * Sends game state updates to the server.
     * This method should format the update, serialize the data if necessary,
     * and send it over the network using WebSocket, HTTP, or another protocol.
     *
     * @param update The game state update to be sent.
     */
    public abstract void sendGameUpdate(String update);

    /**
     * Handles incoming game state updates.
     * This method should process the received data, deserialize it if needed,
     * and update the game state accordingly.
     */
    public abstract void receiveGameUpdate();
}
