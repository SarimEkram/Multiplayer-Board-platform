package networking.game;
import java.util.HashMap;

/**
 * Handles sending player scores to the server when a game ends.
 */
public class GameScoreSender extends GameNetworking{

    private String gameId;// Unique identifier for the game session
    private HashMap<String, Integer> gameScore; // Hashmap for storing each player score
    private boolean isconnected;// boolean to check connection status to server
    private boolean isGameOver; // boolean to check if game is over

    /**
     * Constructor to initialize the score manager.
     *
     * @param gameId The unique ID of the game session.
     */
    public GameScoreSender(String gameId) {
        super(gameId);
        this.gameId = gameId;
        this.gameScore = new  HashMap<>();
        this.isconnected = false;
        this.isGameOver = false;

    }

    /**
     * Sends the final player scores to the server when the game ends.
     *
     * @param playerId The ID of the player sending the message.
     * @param score The score of a player
     */
    public void sendScores(String playerId, int score) {
        if (!isconnected) { // checking server connection status
            System.out.println("Error: Cannot send scores. Server is not connected."); // Error Message
        } else if (isconnected) { // checking server connection status
            if(isGameOver) { // checking if game is over
                gameScore.put(playerId, score); // storing player id and in hash map
            }else if (!isGameOver) { // checking if game is over
                System.out.println("Error: Cannot send scores. Game is not over."); // Error Message
            }

        }

    }

    /**
     * Establishes a connection to the server for sending scores.
     */
    @Override
    public void establishConnection() {
            isconnected = true; // checking server connection status
            System.out.println("Connected to Server for sending scores."); // connection message
    }
    /**
     * Closes the connection to server after sending scores.
     */
    @Override
    public void closeConnection() {
        isconnected = false; // checking server connection status
        System.out.println("Disconnected from Server for sending scores."); // connection message

}


}
