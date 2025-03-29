package networking;

/**
 * Abstract class defining common networking functionalities.
 */
public abstract class NetworkHandler {
    protected String gameId; // Unique identifier for the game session

    /**
     * Constructor to initialize the network handler.
     *
     * @param gameId The unique ID of the game session.
     */
    public NetworkHandler(String gameId) {
        this.gameId = gameId;
    }

    /**
     * Establishes a network connection.
     * This method should handle setting up the connection using WebSockets, HTTP, or another networking protocol.
     */
    public abstract void establishConnection();

    /**
     * Closes the network connection.
     * This method should handle safely closing the connection and cleaning up resources.
     */
    public abstract void closeConnection();

    /**
     * Handles network-related errors, such as disconnections or timeouts.
     *
     * @param errorMessage The error message or exception details.
     */
    public void handleNetworkError(String errorMessage) {
        System.err.println("[Network Error][" + gameId + "]: " + errorMessage);

        System.out.println("Attempting to reconnect session: " + gameId);

        try {
            Thread.sleep(2000); // Simulate retry delay
            establishConnection();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Reconnection attempt interrupted for game: " + gameId);
        } catch (Exception e) {
            System.err.println("Failed to reconnect for game " + gameId + ": " + e.getMessage());
        }
    }
}
