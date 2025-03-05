package networking.game;

public class updateGameBoard {

    private String gameId; // Unique identifier for the game session

    //TODO: Create a field/parameter 'board' of the game board from the game logic team when available.
    //TODO: If a default constructor is used, keep the parameters in the class methods for fetch and upload.
    //TODO: If a constructor with fields is used, remove the parameters from the class methods.

    /**
     * A constructor to initialize gameId and board.
     *
     * @param gameId A string to uniquely identify the current game from the server.
     * @param //board The local game board object which we want to update.
     */
    public updateGameBoard(String gameId /* , board */) {
        this.gameId = gameId;
        // Initialize the board field.
    }

    /**
     * Fetch the game board state from the server and update the local board.
     *
     * @param gameId A string to uniquely identify the current game from the server.
     * @param //board The local game board object which we want to update.
     * @return A boolean value, true if successful, false otherwise.
     */
    public boolean fetchGameBoard(String gameId /* , board */) {
        // Search the server for the specific game using the gameId. Alternatively, the game board could also store gameId.
        // Fetch the current state of the game board from the server and update the local version of the game board.
        // Return true if successful, false otherwise. Alternatively, could throw an exception if unsuccessful.
        return true;
    }

    /**
     * Upload the local game board state to the server.
     *
     * @param gameId A string to uniquely identify the current game from the server.
     * @param //board The local game board object which we want to update.
     * @return A boolean value, true if successful, false otherwise.
     */
    public boolean uploadGameBoard(String gameId /* , board */) {
        // Search the server for the specific game using the gameId. Alternatively, the game board could also store gameId.
        // Upload the local version of game board to the server.
        // Return true if successful, false otherwise. Alternatively, could throw an exception if unsuccessful.
        return true;
    }
}