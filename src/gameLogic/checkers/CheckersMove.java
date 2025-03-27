package gameLogic.checkers;

/**
 * Utility class for handling checkers moves.
 */
public class CheckersMove {

    /**
     * Determines and processes the available moves for a given checkers piece on the board.
     *
     * @param board the current state of the checkers board
     * @param piece the checkers piece for which available moves are to be determined\
     * @param row the row of the piece
     * @param col the column of the piece
     * 
     * this function check the availaible moves based on the piece type (normal moves forward only, king moves forward or backward)
     */
    public static void availableMoves(CheckersBoard board, CheckersPiece piece, int row, int col) {

    }


    /**
     * Executes a  move for the given checkers piece on the specified board.
     * 
     * 
     * @param board the current state of the checkers board
     * @param piece the checkers piece to move
     * 
     * moves the piece from start to dest location 
     */
    public static void move(CheckersBoard board, CheckersPiece piece, int startRow, int startCol, int destRow, int destCol) {
        board.board[destRow][destCol] = piece;
        board.board[startRow][startCol] = null;

        // Check if this move was a jump over another piece
        int rowDifference = destRow - startRow;
        int colDifference = destCol - startCol;

        if (Math.abs(rowDifference) == 2 && Math.abs(colDifference) == 2) {
            // Find the piece in the middle and remove it (captured)
            int capturedRow = startRow + rowDifference / 2;
            int capturedCol = startCol + colDifference / 2;
            board.removePiece(capturedRow, capturedCol);
        }

        // Promote to king if the piece reaches the opposite end
        if (piece.getColour() == CheckersPiece.Colour.RED && destRow == 0) {
            piece.promoteToKing();
            System.out.println("RED piece became a KING!");
        } else if (piece.getColour() == CheckersPiece.Colour.BLACK && destRow == 7) {
            piece.promoteToKing();
            System.out.println("BLACK piece became a KING!");
        }

    }


    

}