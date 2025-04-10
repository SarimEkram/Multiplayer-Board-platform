package gameLogic.checkers;

/**
 * Represents the checkers board used in the game.
 */
public class CheckersBoard {
    
    public final CheckersPiece[][] board;       // 8x8 board of Checkers pieces

    /**
     * Constructs a new CheckersBoard with an 8x8 grid.
     */
    public CheckersBoard() {
        this.board = new CheckersPiece[8][8];
    }       // Initialize an empty 8x8 board

    /**
     * Places all the checkers pieces on the board in their initial positions.
     */
    public void placeAllPieces() {
        this.clearBoard();                  // Clear any existing pieces before setting up
        // Place black pieces (top 3 rows)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 1) {
                    this.board[i][j] = new CheckersPiece(CheckersPiece.Colour.BLACK);
                }
            }
        }
        // Place white pieces (bottom 3 rows)
        for (int i = 5; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 1) {
                    this.board[i][j] = new CheckersPiece(CheckersPiece.Colour.WHITE);
                }
            }
        }
    }

    /**
     * Clears the board by removing all checkers pieces.
     */
    public void clearBoard() {
        // Iterate through each board cell
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.board[i][j] = null;        // Set each cell to null
            }
        }
    }

    /**
     * Removes a checkers piece from the board at the specified row and column.
     *
     * @param row the row index from which to remove the piece
     * @param col the column index from which to remove the piece
     */
    public void removePiece(int row, int col) {
        this.board[row][col] = null;
    }       // Remove piece at specified location
}
