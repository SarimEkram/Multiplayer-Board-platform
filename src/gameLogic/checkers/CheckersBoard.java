package gameLogic.checkers;

/**
 * Represents the checkers board used in the game.
 */
public class CheckersBoard {
    
    public final CheckersPiece[][] board;

    /**
     * Constructs a new CheckersBoard with an 8x8 grid.
     */
    public CheckersBoard() {
        this.board = new CheckersPiece[8][8];
    }

    /**
     * Places all the checkers pieces on the board in their initial positions.
     */
    public void placeAllPieces() {
        this.clearBoard();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 1) {
                    this.board[i][j] = new CheckersPiece(CheckersPiece.Colour.BLACK);
                }
            }
        }
        // Place red pieces (bottom 3 rows)
        for (int i = 5; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 1) {
                    this.board[i][j] = new CheckersPiece(CheckersPiece.Colour.RED);
                }
            }
        }
    }

    /**
     * Clears the board by removing all checkers pieces.
     */
    public void clearBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.board[i][j] = null;
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
        board[row][col] = null;
    }
}
