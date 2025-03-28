package gameLogic.checkers;

/**
 * Utility class for handling checkers moves.
 */
public class CheckersMove {

    /**
     * Determines and processes the available moves for a given checkers piece on the board.
     *
     * @param board the current state of the checkers board
     * @param piece the checkers piece for which available moves are to be determined
     * @param row the row of the piece
     * @param col the column of the piece
     * 
     * this function check the availaible moves based on the piece type (normal moves forward only, king moves forward or backward)
     */
    public static void availableMoves(CheckersBoard board, CheckersPiece piece, int row, int col) {

        System.out.println("Available moves for " + piece.getColour() + " at (" + row + "," + col + "):");

        int direction;

        if (piece.getColour() == CheckersPiece.Colour.RED) {
            direction = -1; // RED moves upward
        }
        else {
            direction = 1;  // BLACK moves downward
        }

        if (!piece.isKing()) {
            printMoveIfValid(board, row, col, row + direction, col - 1);
            printMoveIfValid(board, row, col, row + direction, col + 1);
        }
        else {
            int[] rowChanges = {1, 1, -1, -1};
            int[] colChanges = {-1, 1, -1, 1};
                for (int i = 0; i < 4; i++) {
                    int newRow = row + rowChanges[i];
                    int newCol = col + colChanges[i];
                    printMoveIfValid(board, row, col, newRow, newCol);
                }
        }

    }

    //  check if a move is valid and print it if not
    private static void printMoveIfValid(CheckersBoard board, int fromRow, int fromCol, int toRow, int toCol) {
        boolean inBounds = toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8;
        boolean isEmpty = inBounds && board.board[toRow][toCol] == null;

        if (isEmpty) {
            System.out.println("  -> Move to (" + toRow + "," + toCol + ")");
        }

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

        int rowDifference = destRow - startRow;
        int colDifference = destCol - startCol;

        if (Math.abs(rowDifference) == 2 && Math.abs(colDifference) == 2) {
            int capturedRow = startRow + rowDifference / 2;
            int capturedCol = startCol + colDifference / 2;
            board.removePiece(capturedRow, capturedCol);
        }
        if (piece.getColour() == CheckersPiece.Colour.RED && destRow == 0) {
            piece.promoteToKing();
            System.out.println("RED piece became a KING!");
        } else if (piece.getColour() == CheckersPiece.Colour.BLACK && destRow == 7) {
            piece.promoteToKing();
            System.out.println("BLACK piece became a KING!");
        }

    }

}