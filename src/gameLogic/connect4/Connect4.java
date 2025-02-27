package gameLogic.connect4;

public class Connect4 {

    /**
     * This class represents the game logic for a Connect 4 game. It manages game rules,
     * checks for wins in various directions, and controls the gameplay flow.
     * We have to create an instance of this class after creating a ConnectBoard instance.
     * Then we have to put that instance of Board class when we are creating instance of Connect4
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
     * This function checks if the row and column passed are valid or not.
     * This will be used in play method
     * @param row the row we are checking if it's valid or not.
     * @param column the column we are checking if it's valid or not.
     * @return true if the row and column is valid else return false.
     */
    private static boolean valid( int row, int column){
        return false;

    }
    /**
     * This function checks if we can play at a certain column or not.
     * @param column the column we want to play the piece.
     * @return true if the column is 0/EMP.
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
     * Is there a win in given board in any row of board
     * @param piece The piece to look for length in a row for any row
     * @return True if there is length in any row, False otherwise
     */
    public static boolean winInAnyRow(int piece) {
        return false;
    }


    /**
     * Is there a win in given board in any column of board
     * @param piece The piece to look for length in a row for any column
     * @return True if there is length in any column, False otherwise
     */
    public static boolean winInAnyColumn(int piece) {
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

}
