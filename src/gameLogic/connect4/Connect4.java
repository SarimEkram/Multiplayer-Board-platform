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
     * @param column the column we want to check for the win condition.
     * @param piece the piece we want to check for.
     * @return true if the conditions for win in column fulfills.
     */
    public static boolean winInColumn(int column, int piece) {
        return false;

    }

    /**
     * This function checks if the user has won in diagonal Forward Slash or not.
     * @param piece the piece we want to check for.
     * @return true if the conditions for win in Diagonal forward slash fulfills.
     */
    public static boolean winInDiagonalForwardSlash(int piece) {
        return false;
    }

    /**
     * This function checks if the user has won in diagonal backslash or not.
     * @param board a 2D array board of size rows (dimension 1) and columns (dimension 2)
     * @param piece the piece we want to check for.
     * @param length is the length required to win.
     * @return true if the conditions for win in Diagonal backslash fulfills.
     */
    public static boolean winInDiagonalBackslash (int[][] board, int piece, int length) {
        return false;
    }

}
