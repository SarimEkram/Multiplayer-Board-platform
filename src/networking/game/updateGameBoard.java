package networking.game;

import gameLogic.checkers.CheckersBoard;
import gameLogic.connect4.ConnectBoard;
import gameLogic.tictactoe.TicTacToeBoard;

public class updateGameBoard<T> {

    private final GameServer gameServer = new GameServer(); // An object to simulate the role of a server and database
    private final String gameId; // Unique identifier for the game session
    private T board;

    /**
     * A constructor to initialize gameId and board for Checkers.
     *
     * @param gameId A string to uniquely identify the current game from the server.
     * @param board The local game board object for CheckersBoard, ConnectBoard, or TicTacToeBoard; which we want to update.
     */
    public updateGameBoard(String gameId, T board) {
        this.gameId = gameId;
        this.board = board;
    }

    /**
     * Fetch the game board state from the server and update the local board.
     *
     * @return A boolean value; true if successful, false otherwise.
     */
    public boolean fetchGameBoard() {
        T fetchedBoard = (T) gameServer.getGameState(gameId);
        if (fetchedBoard != null) {     //If the fetched game board is not null, fetch is successful and return true
            board = (T) fetchedBoard;
            return true;
        }
        else
            return false;
    }

    /**
     * Upload the local game board state to the server.
     *
     * @return A boolean value; true if successful, false otherwise.
     */
    public boolean uploadGameBoard() {
        boolean success = gameServer.saveGameState(gameId, board);
        if (success)
            return true;
        else
            return false;
    }
}

