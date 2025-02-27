package gameLogic.tictactoe;

/**
 * Represents a Tic Tac Toe game board that provides functionality to play the game.
 */
public class TicTacToeBoard {

    private static final int BOARD_SIZE = 3;
    private static char[][] gameBoard = new char[BOARD_SIZE][BOARD_SIZE];

    /**
     * Creates and initializes the game board.
     */
    public void createBoard() {
        return null;
    }

    /**
     * Displays the current state of the game board.
     */
    public void displayBoard() {}

    /**
     * Checks if a specific cell is empty.
     *
     * @param row    The row index (0-based).
     * @param column The column index (0-based).
     * @return True if the cell is empty, false otherwise.
     */
    public boolean isCellEmpty(int row, int column) {
        return false;
    }

    /**
     * Places a player's piece on the board.
     *
     * @param row    The row index (0-based).
     * @param column The column index (0-based).
     * @param player The player's symbol ('X' or 'O').
     */
    public void placePiece(int row, int column, char player) {}

    /**
     * Checks if the given player has won the game.
     *
     * @param player The player's symbol ('X' or 'O').
     * @return True if the player has won, false otherwise.
     */
    public boolean checkForWin(char player) {
        return false;
    }

    /**
     * Checks if the board is full.
     *
     * @return True if the board is full, false otherwise.
     */
    public static boolean boardFull() {
        return false;
    }

    /**
     * Determines if the game is over.
     *
     * @return True if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        return false;
    }


}
