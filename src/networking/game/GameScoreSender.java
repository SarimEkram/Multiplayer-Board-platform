package networking.game;
import java.util.HashMap;


/**
 * Handles sending player scores to the server when a game ends.
 */
public class GameScoreSender extends GameNetworking{

    private String gameId;// Unique identifier for the game session
    public HashMap<String, HashMap<String, Integer>> gameScore ;// Hashmap for storing each player score
    private HashMap<String, Integer> playerScore;
    private boolean isconnected;// boolean to check connection status to server
    private boolean isGameOver; // boolean to check if game is over

    /**
     * Constructor to initialize the score manager.
     *
     * @param gameId The unique ID of the game session.
     */
    public GameScoreSender(String gameId) {
        super(gameId);
        this.gameId = gameId;               // unique game id
        this.playerScore = new HashMap<>();  // initializing hashmap
        this.gameScore = new HashMap<>();
        this.isconnected = false;           // initial connection set to false
        this.isGameOver = false;            // initial game status set to false

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
                   if(ScoreValidator.isScoreValid(gameId,playerId,score, playerScore,gameScore)){
                       playerScore.put(playerId,score);  // storing player id and in hash map
                       gameScore.put(gameId,playerScore);
                       System.out.println("Scores sent to player " + playerId  + "for game " + gameId);

                   }else {
                       System.out.println("Enter valid score");
                   }
                System.out.println("Game score sent for player " + playerId + ": " + score); // success message
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
            System.out.println("Connected to Server for sending scores."); // success message
    }
    /**
     * Closes the connection to server after sending scores.
     */
    @Override
    public void closeConnection() {
        isconnected = false; // checking server connection status
        System.out.println("Disconnected from Server for sending scores."); // success message

}   /**
     * Sends game updates to the server.
     *
     * @param update The update message to be sent.
     */
    @Override
    public void sendGameUpdate(String update) {
        if (!isconnected) {
            System.out.println("Error: Cannot send game update. Server not connected.");
            return;
        }
        System.out.println("Sending game update: " + update);
        // Simulate server transmission logic here
    }


    /**
     * Receives game updates from the server.
     */
    @Override
    public void receiveGameUpdate() {
        if (!isconnected) {
            System.out.println("Error: Cannot receive game update. Server not connected.");
            return;
        }
        System.out.println("Receiving game update...");

    }
}
