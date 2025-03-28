package gameLogic.tictactoe;

/**
 * Represents a Tic Tac Toe game board that provides functionality to play the game.
 */
public class TicTacToeBoard {

    private static final int BOARD_SIZE = 3;
    private char[][] gameBoard = new char[BOARD_SIZE][BOARD_SIZE];

    /**
     * Creates and initializes the game board.
     */
    public void createBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                gameBoard[i][j] = ' ';
            }
        }
    }

    /**
     * Displays the current state of the game board.
     */
    public void displayBoard() {
        System.out.println("----------------");
        // Display rows for the board
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.out.print("| ");
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(gameBoard[i][j] + " | ");
            }
            System.out.println();
            System.out.println("----------------");
        }
    }

    /**
     * Checks if a specific cell is empty.
     *
     * @param row    The row index (0-based).
     * @param column The column index (0-based).
     * @return True if the cell is empty, false otherwise.
     */
    public boolean isCellEmpty(int row, int column) {
        return gameBoard[row][column] == ' ';
    }

    /**
     * Places a player's piece on the board.
     *
     * @param row    The row index (0-based).
     * @param column The column index (0-based).
     * @param player The player's symbol ('X' or 'O').
     */
    public void placePiece(int row, int column, char player) {
        if (isCellEmpty(row, column)) {
            gameBoard[row][column] = player;
        }
    }

    /**
     * Checks if the given player has won the game.
     *
     * @param player The player's symbol ('X' or 'O').
     * @return True if the player has won, false otherwise.
     */
    public boolean checkForWin(char player) {
        // Check for rows and columns
        for (int i = 0; i < BOARD_SIZE; i++) {

            // check for row wins only
            if  (gameBoard[i][0] == player && gameBoard[i][1] == player && gameBoard[i][2] == player) {
                return true;
            }
            // check for column wins only
            if (gameBoard[0][i] == player && gameBoard[1][i] == player && gameBoard[2][i] == player) {
                return true;
            }
        }

        // Check for diagonal wins only
        if (gameBoard[0][0] == player && gameBoard[1][1] == player && gameBoard[2][2] == player) {
            return true;
        }
        if (gameBoard[0][2] == player && gameBoard[1][1] == player && gameBoard[2][0] == player) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the board is full.
     *
     * @return True if the board is full, false otherwise.
     */
    public boolean boardFull() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(gameBoard[i][j] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }
}
