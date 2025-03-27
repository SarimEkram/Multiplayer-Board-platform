package networking.game;
import java.util.ArrayList;
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

        // Initialize necessary configurations
    }

    /**
     * Sends the final player scores to the server when the game ends.
     *
     * @param playerId The ID of the player sending the message.
     * @param score The score of a player
     */
    public void sendScores(String playerId, int score) {
        if (!isconnected) {
            System.out.println("Error: Cannot send scores. Server is not connected.");
        } else if (isconnected) {
            if(isGameOver) {
                //Format and send score to server if game is over
            }else if (!isGameOver) {
                System.out.println("Error: Cannot send scores. Game is not over.");
            }



        }
        // Format and add score to gameScore
        // send the scores  to the server via HTTP or WebSocket
        // checks if the game has ended
        // if Game has ended each player score will be sent to the server

    }




    /**
     * Establishes a connection to the server for sending scores.
     */
    @Override
    public void establishConnection() {
            isconnected = true;
            System.out.println("Connected to Server");
    }

    /**
     * Closes the connection to server after sending scores.
     */
    @Override
    public void closeConnection() {
        isconnected = false;
        System.out.println("Disconnected from Server");

}


}
