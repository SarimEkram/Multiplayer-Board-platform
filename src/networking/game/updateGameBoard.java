package networking.game;

import gameLogic.checkers.CheckersBoard;
import gameLogic.connect4.ConnectBoard;
import gameLogic.tictactoe.TicTacToeBoard;

public class updateGameBoard<T> extends GameNetworking {

    //TODO: Remove enum GameType if not used. Also create multiple versions of constructor and methods for each game if needed.
    private GameType gameType;

    private String gameId; // Unique identifier for the game session
    private T board;

    /**
     * A constructor to initialize gameId and board for Checkers.
     *
     * @param gameId A string to uniquely identify the current game from the server.
     * @param board The local game board object for CheckersBoard, ConnectBoard, or TicTacToeBoard; which we want to update.
     */
    public updateGameBoard(String gameId, T board) {
        super(gameId);
        this.gameId = gameId;
        this.board = board;
    }

    /**
     * Fetch the game board state from the server and update the local board.
     *
     * @return A boolean value; true if successful, false otherwise.
     */
    public boolean fetchGameBoard() {
        // Search the server for the specific game using the gameId. Alternatively, the game board could also store gameId.
        // Fetch the current state of the game board from the server and update the local version of the game board.
        // Return true if successful, false otherwise. Alternatively, could throw an exception if unsuccessful.
        return true;
    }

    /**
     * Upload the local game board state to the server.
     *
     * @return A boolean value; true if successful, false otherwise.
     */
    public boolean uploadGameBoard() {
        // Search the server for the specific game using the gameId. Alternatively, the game board could also store gameId.
        // Upload the local version of game board to the server.
        // Return true if successful, false otherwise. Alternatively, could throw an exception if unsuccessful.
        return true;
    }

    /**
     * Establishes a network connection.
     * This method should handle setting up the connection using WebSockets, HTTP, or another networking protocol.
     */
    @Override
    public void establishConnection() {

    }

    /**
     * Closes the network connection.
     * This method should handle safely closing the connection and cleaning up resources.
     */
    @Override
    public void closeConnection() {

    }
    @Override
    public void sendGameUpdate(String update) {
        System.out.println("Uploading board update to server: " + update);
        // simulate WebSocket or HTTP call to push update to the server
    }

    @Override
    public void receiveGameUpdate() {
        System.out.println("Fetching board update from server...");
        // Simulate retrieving update from server
    }

    private enum GameType {
        CHECKERS, CONNECT4, TICTACTOE;
    }
}

