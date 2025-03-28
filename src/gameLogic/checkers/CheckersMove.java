package gameLogic.checkers;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for handling checkers moves.
 */
public class CheckersMove {

    /**
     * checks available moves for the piece and returns moves array
     * @param board
     * @param piece
     * @param row
     * @param col
     * @return
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
            if (isValidMove(board, row + direction, col - 1))
                movesList.add(new int[]{row + direction, col - 1});
            if (isValidMove(board, row + direction, col + 1))
                movesList.add(new int[]{row + direction, col + 1});
        } else {
            int[] rowChanges = {1, 1, -1, -1};
            int[] colChanges = {-1, 1, -1, 1};
            for (int i = 0; i < 4; i++) {
                int newRow = row + rowChanges[i];
                int newCol = col + colChanges[i];
                if (isValidMove(board, newRow, newCol))
                    movesList.add(new int[]{newRow, newCol});
            }
        }

        int[][] movesArray = new int[movesList.size()][2];
        for (int i = 0; i < movesList.size(); i++) {
            movesArray[i] = movesList.get(i);
        }
        return movesArray;
    }

    /**
     * check if the move is valid
     * @param board
     * @param toRow
     * @param toCol
     * @return
     */
    private static boolean isValidMove(CheckersBoard board, int toRow, int toCol) {
        boolean inBounds = (toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8);
        boolean isEmpty = inBounds && board.board[toRow][toCol] == null;
        return isEmpty && inBounds;
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