package gameLogic.connect4;

public class Connect4 {

    /**
     * This class represents the game logic for a Connect 4 game. It manages game rules,
     * checks for wins in various directions, and controls the gameplay flow.
     * Connect 4 instance is created in ConnectBoard
     */
    ConnectBoard board;

    /**
     * Constructs a new Connect4 game logic controller.
     *
     * @param board The ConnectBoard instance that represents the physical layout
     *              and state of the Connect 4 board. This board is used to check
     *              and update the game status as moves are made.
     */
    public Connect4 (ConnectBoard board){
        this.board = board;
    }

    /**
     * Switches the current player.
     */
    public void switchPlayer() {
    }

    /**
     * This function checks if we can play at a certain column or not.
     * @param column the column we want to play the piece.
     * @return true if the column is empty
     */

    private static boolean canPlay(int column) {
        return false;

    }

    /**
     * This function plays the piece at the column we want to play.
     * @param column the column we want to play the piece.
     * @param piece the piece we want to play.
     * @return the row of the piece we played else -1.
     */
    public static int play( int column, int piece) {
        return 0;

    }
    /**
     * This checks if the board is full or not and will be called in isGameOver ConnectBoard
     * @return true if board is full
     */
    public boolean isFull() {
        return false;
    }

    /**
     * This function checks if the user has won in row or not.
     * This will be called in winAnyRow
     * @param row the row we want to check for the win condition.
     * @param piece the piece we want to check for.
     * @return true if the conditions for win in row fulfills.
     */
    private static boolean winInRow(int row, int piece) {
        return false;

    }

    /**
     * This function checks if the user has won in column or not.
     * This will be called in winAnyColumn
     * @param column the column we want to check for the win condition.
     * @param piece the piece we want to check for.
     * @return true if the conditions for win in column fulfills.
     */
    private static boolean winInColumn(int column, int piece) {
        return false;

    }

    /**
     * This function checks if the user has won in diagonal Forward Slash or not.
     * This will be called in winAnyDiagonal
     * @param piece the piece we want to check for.
     * @return true if the conditions for win in Diagonal forward slash fulfills.
     */
    private static boolean winInDiagonalForwardSlash(int piece) {
        return false;
    }

    /**
     * This function checks if the user has won in diagonal backslash or not.
     * This will be called in winAnyDiagonal
     * @param piece the piece we want to check for.
     * @return true if the conditions for win in Diagonal backslash fulfills.
     */
    private static boolean winInDiagonalBackslash (int piece) {
        return false;
    }


    /**
     * Is there a win in given board in any diagonal of board
     * @param piece The piece to look for length in a row for any diagonal
     * @return True if there is
     */
    public static boolean winInAnyDiagonal(int piece) {
        return false;
    }

    /**
     * When this function is called winInRow, winInColumn winInAnyDiagonal will be called to check.
     * @param playerNumber The playerNumber to check for a win
     * @return True if playerNumber has won
     */
    public boolean won (int playerNumber) {
        return false;
    }

    /**
     * Allows a player to forfeit the game.
     * This will be called in the gui controller class.
     * @param playerNumber The number identifying the player who wants to forfeit.
     * @return true if the forfeit is successful, false otherwise.
     */
    public boolean forfeit(int playerNumber){
        return false;
    }

}
