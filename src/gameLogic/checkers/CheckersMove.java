package gameLogic.checkers;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for handling checkers moves.
 */
public class CheckersMove {

    /**
     * Determines and processes the available moves for a given checkers piece on the board.
     *
     * @param board the current state of the checkers board
     * @param piece the checkers piece for which available moves are to be determined
     * @param row   the row of the piece
     * @param col   the column of the piece
     * this function check the availaible moves based on the piece type (normal moves forward only, king moves forward or backward)
     */
    public static int[][] availableMoves(CheckersBoard board, CheckersPiece piece, int row, int col) {
        List<int[]> movesList = new ArrayList<>();

        int direction;
        if (piece.getColour() == CheckersPiece.Colour.RED) {
            direction = -1;
        } else {
            direction = 1;
        }

        if (!piece.isKing()) {
            addMoveIfValidToList(board, movesList, row + direction, col - 1);
            addMoveIfValidToList(board, movesList, row + direction, col + 1);
        } else {
            int[] rowChanges = {1, 1, -1, -1};
            int[] colChanges = {-1, 1, -1, 1};
            for (int i = 0; i < 4; i++) {
                int newRow = row + rowChanges[i];
                int newCol = col + colChanges[i];
                addMoveIfValidToList(board, movesList, newRow, newCol);
            }
        }

        int[][] movesArray = new int[movesList.size()][2];
        for (int i = 0; i < movesList.size(); i++) {
            movesArray[i] = movesList.get(i);
        }
        return movesArray;
    }

    /**
     * Helper function to check if a move is valid
     * @param board
     * @param movesList
     * @param toRow
     * @param toCol
     */
    private static void addMoveIfValidToList(CheckersBoard board, List<int[]> movesList, int toRow, int toCol) {
        boolean inBounds = toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8;
        boolean isEmpty = inBounds && board.board[toRow][toCol] == null;

        if (isEmpty) {
            movesList.add(new int[]{toRow, toCol});
        }
    }

    /**
     * Executes a  move for the given checkers piece on the specified board.
     *
     * @param board the current state of the checkers board
     * @param piece the checkers piece to move
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
        } else if (piece.getColour() == CheckersPiece.Colour.BLACK && destRow == 7) {
            piece.promoteToKing();
        }

    }

}