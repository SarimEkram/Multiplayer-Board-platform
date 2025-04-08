package networking.game;

/**
 * A class that interacts with the server to send and receive game board (state) updates.
 *
 * @param <T> generic type parameter, allows to create objects with type safety
 */
public class UpdateGameBoard<T> {

    private GameServer gameServer = new GameServer(); // An object to simulate the role of a server and database
    private String gameId; // Unique identifier for the game session
    private T board;

    /**
     * A constructor to initialize gameId and board for Checkers.
     *
     * @param gameId A string to uniquely identify the current game from the server.
     * @param board The local game board object for CheckersBoard, ConnectBoard, or TicTacToeBoard; which we want to update.
     */
    public UpdateGameBoard(GameServer gameServer, String gameId, T board) {
        this.gameServer = gameServer;
        this.gameId = gameId;
        this.board = board;
    }

    /**
     * Fetch the game board state from the server and update the local board after each turn.
     *
     * @return A boolean value; true if successful, false otherwise.
     */
    public boolean fetchGameBoard() {
        T fetchedBoard = (T) gameServer.getGameState(gameId); //fetchedBoard is of generic type to use boards from all games.
        if (fetchedBoard != null) {     //If the fetched game board is not null, fetch is successful and return true
            board = (T) fetchedBoard;
            return true;
        }
        else
            return false;
    }

    /**
     * Upload the local game board state to the server after each turn.
     *
     * @return A boolean value; true if successful, false otherwise.
     */
    public boolean uploadGameBoard() {
        boolean success = gameServer.saveGameState(gameId, board);
        return success;
    }
}

