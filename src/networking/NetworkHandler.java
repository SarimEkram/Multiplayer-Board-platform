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
     * Logs the error and attempts to reconnect up to 3 times.
     *
     * @param errorMessage The error message or exception details.
     */
    public void handleNetworkError(String errorMessage) {
        System.err.println("[Network Error][" + gameId + "]: " + errorMessage);
        System.out.println("Attempting to reconnect session: " + gameId);

        int retries = 0;
        final int maxRetries = 3;
        final int retryDelayMillis = 2000;

        while (retries < maxRetries) {
            try {
                Thread.sleep(retryDelayMillis); // Simulate delay before retry
                System.out.println("Reconnection attempt " + (retries + 1) + "...");
                establishConnection(); // Try to re-establish the connection
                System.out.println("Reconnection successful for game: " + gameId);
                return;
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                System.err.println("Reconnection interrupted for game: " + gameId);
                return;
            } catch (Exception e) {
                retries++;
                System.err.println("Reconnection attempt " + retries + " failed: " + e.getMessage());
            }
        }

        System.err.println("All reconnection attempts failed for game: " + gameId);
    }

}
