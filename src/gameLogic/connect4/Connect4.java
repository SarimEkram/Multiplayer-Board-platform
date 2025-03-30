package gameLogic.connect4;

public class Connect4 {

    /**
     * This class represents the game logic for a Connect 4 game. It manages game rules,
     * checks for wins in various directions, and controls the gameplay flow.
     * Connect 4 instance is created in ConnectBoard
     */
    ConnectBoard board;

    private boolean gameOver = false;

    /**
     * Constructs a new Connect4 game logic controller.
     *
     * @param board The ConnectBoard instance that represents the physical layout
     *              and state of the Connect 4 board. This board is used to check
     *              and update the game status as moves are made.
     */
    public Connect4(ConnectBoard board) {
        this.board = board;
    }

    /**
     * Switches the current player.
     */

    public void switchPlayer() { //D // If current player is player1, switch to player2, else switch to player1
        if (board.getCurrentPlayer() == board.piece1) {
            board.setCurrentPlayer(board.piece2);
        } else {
            board.setCurrentPlayer(board.piece1);
        }
    }

    /**
     * This function checks if we can play at a certain column or not.
     *
     * @param column the column we want to play the piece.
     * @return true if the column is empty
     */

    private static boolean canPlay(int[][] board, int column) {
        return board[0][column] == 0;

    }

    /**
     * This function plays the piece at the column we want to play.
     *
     * @param column the column we want to play the piece.
     * @param piece  the piece we want to play.
     * @return the row of the piece we played else -1.
     */
    public static int play(int[][] board, int column, int piece) {
        if (canPlay(board, column)) {
            // As the piece is deployed from the top and ends up on the bottom, I started the loop from
            // the row count, meaning the last row.
            for (int row = board.length - 1; row >= 0; row--) {
                // if the board at that row and column is EMP/0 we put that piece at that row and column.
                if (board[row][column] == 0) {
                    board[row][column] = piece;
                    // returning the row.
                    return row;
                }
            }
        }
        // else returning -2, meaning they cant play at that spot
        return -2;
    }

    /**
     * This checks if the board is full or not and will be called in isGameOver ConnectBoard
     *
     * @return true if board is full
     */
    public boolean isFull(int[][] board) { //D
        // Checks if the board is full by looping through the top row to check for empty slots, if the top row is full = board is full
        for (int i = 0; i < board[0].length; i++) {
            if (board[0][i] == 0) return false;
        }
        return true;
    }

    /**
     * This function checks if the user has won in row or not.
     * This will be called in winAnyRow
     *
     * @param row   the row we want to check for the win condition.
     * @param piece the piece we want to check for.
     * @return true if the conditions for win in row fulfills.
     */
    private static boolean winInRow(int[][] board, int row, int piece) {
        int count = 0;

        for (int col = 0; col < board[0].length; col++) {
            if (board[row][col] == piece) {
                count++;
                if (count == 4) {
                    return true;
                }
            } else {
                count = 0; // reset count if the sequence breaks
            }
        }
        return false;
    }

    /**
     * This function checks if the user has won in column or not.
     * This will be called in winAnyColumn
     *
     * @param column the column we want to check for the win condition.
     * @param piece  the piece we want to check for.
     * @return true if the conditions for win in column fulfills.
     */
    private static boolean winInColumn(int[][] board, int column, int piece) { //D
        int count = 0; // Keeps track of how many matching pieces have been seen in a row
        for (int row = 0; row < board.length; row++) {
            if (board[row][column] == piece) { //If the current cell matches the player's piece, it increases the count for the streak
                count++;
            } else {
                count = 0; // If it doesn't match, it resets the count to zero since it broke the streak
            }
            if (count >= 4) return true; // If we've found 4 in a row, the player wins
        }
        return false; // If no four in a row exists in this column, it just returns false
    }

    /**
     * This function checks if the user has won in diagonal Forward Slash or not.
     * This will be called in winAnyDiagonal
     *
     * @param piece the piece we want to check for.
     * @return true if the conditions for win in Diagonal forward slash fulfills.
     */
    private static boolean winInDiagonalForwardSlash(int[][] board, int piece) { //D
        // Starts from row 3 to make sure there are enough rows above to check diagonals going upward (avoid out of bounds)
        for (int row = 3; row < board.length; row++) {
            // Only check up to column (totalColumns - 4) to make sure there aren't any out of bounds errors
            for (int col = 0; col <= board[0].length - 4; col++) {
                // Checks for 4 matching pieces going up and to the right (/ direction)
                if (board[row][col] == piece && board[row - 1][col + 1] == piece && board[row - 2][col + 2] == piece && board[row - 3][col + 3] == piece){
                    return true; // A forward-slash diagonal win is found
            }
        }
    }
    return false; // No forward-slash diagonal win was found
}

    /**
     * This function checks if the user has won in diagonal backslash or not.
     * This will be called in winAnyDiagonal
     *
     * @param piece the piece we want to check for.
     * @return true if the conditions for win in Diagonal backslash fulfills.
     */
    private static boolean winInDiagonalBackslash(int[][] board,int piece) { //D // same concept as winInDiagonalForwardSlash, but checks in the opposite (\) direction
        // Starts from the top-left of the board (row 0) and checks diagonals going down and to the right (\ direction)
        for(int row = 0; row < board.length; row++) {
            // Only check up to column (totalColumns - 4) to avoid going out of bounds
            for (int col = 0; col <= board[0].length - 4; col++) {
                // Checks for 4 matching pieces going down and to the right (\ direction)
                if (board[row][col] == piece && board[row + 1][col + 1] == piece && board[row + 2][col + 2] == piece && board[row + 3][col + 3] == piece){
                    return true; // A forward-slash diagonal win is found
                }

            }
        }
        return false; // No back-slash diagonal win was found
    }


    /**
     * Is there a win in given board in any diagonal of board
     *
     * @param piece The piece to look for length in a row for any diagonal
     * @return True if there is
     */
    public static boolean winInAnyDiagonal(int[][] board, int piece) {//D
        return winInDiagonalForwardSlash(board, piece) || winInDiagonalBackslash(board, piece);
    } //checks for all diagonals back/front slash, returns true if either are true

    /**
     * When this function is called, winInRow, winInColumn & winInAnyDiagonal will be called to check.
     *
     * @param playerNumber The playerNumber to check for a win
     * @return True if playerNumber has won
     */

    public boolean won(int[][] board, int playerNumber) { //D // Checks if the player has won in any direction
        for (int row = 0; row < board.length; row++) {
            if (winInRow(board, row, playerNumber)) // Checks for a horizontal win in each row
                return true;
        }
        for (int column = 0; column < board[0].length; column++) {
            if (winInColumn(board, column, playerNumber)) { // Checks for a vertical win in each column
                return true;
            }
        }
        return winInAnyDiagonal(board, playerNumber); // Checks for a diagonal win in both directions
    }

    /**
     * Allows a player to forfeit the game.
     * This will be called in the gui controller class.
     * @param playerNumber The number identifying the player who wants to forfeit.
     * @return true if the forfeit is successful, false otherwise.
     */

    public int forfeit(int playerNumber) { //D
        gameOver = true; // Mark the game as over
        if (playerNumber == board.piece1) { // When forfeit is called, whatever the current player is, return the opposing player as the winner
            return board.piece2; // Player 1 forfeits, P2 wins
        } else {
            return board.piece1; // Player 2 forfeits, P1 wins
        }
    }
    public void resetGame() {
        gameOver = false;
    }

    public boolean isGameOverByForfeit() { //D
        return gameOver; // Returns the current gameOver state, which will be true only after a forfeit
    }

}