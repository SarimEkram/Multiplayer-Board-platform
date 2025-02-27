package gameLogic.connect4;

public class Connect4 {
    ConnectBoard board;

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
    public static boolean valid( int row, int column){
        return false;

    }
    /**
     * This function checks if we can play at a certain column or not.
     * @param column the column we want to play the piece.
     * @return true if the column is 0/EMP.
     */
    public static boolean canPlay(int column) {
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
    public static boolean winInRow(int row, int piece) {
        return false;

    }

    /**
     * This function checks if the user has won in column or not.
     * This will be called in winAnyColumn
     * @param column the column we want to check for the win condition.
     * @param piece the piece we want to check for.
     * @return true if the conditions for win in column fulfills.
     */
    public static boolean winInColumn(int column, int piece) {
        return false;

    }

    /**
     * This function checks if the user has won in diagonal Forward Slash or not.
     * This will be called in winAnyDiagonal
     * @param piece the piece we want to check for.
     * @return true if the conditions for win in Diagonal forward slash fulfills.
     */
    public static boolean winInDiagonalForwardSlash(int piece) {
        return false;
    }

    /**
     * This function checks if the user has won in diagonal backslash or not.
     * This will be called in winAnyDiagonal
     * @param piece the piece we want to check for.
     * @return true if the conditions for win in Diagonal backslash fulfills.
     */
    public static boolean winInDiagonalBackslash (int piece) {
        return false;
    }

    /**
     * Is there a win in given board in any row of board
     * @param piece The piece to look for length in a row for any row
     * @return True if there is length in any row, False otherwise
     */
    private static boolean winInAnyRow(int piece) {
        return false;
    }


    /**
     * Is there a win in given board in any column of board
     * @param piece The piece to look for length in a row for any column
     * @return True if there is length in any column, False otherwise
     */
    private static boolean winInAnyColumn(int piece) {
        return false;
    }

    /**
     * Is there a win in given board in any diagonal of board
     * @param piece The piece to look for length in a row for any diagonal
     * @return True if there is
     */
    private static boolean winInAnyDiagonal(int piece) {
        return false;
    }
}
