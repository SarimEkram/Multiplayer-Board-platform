package gameLogic.checkers;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for handling checkers moves.
 */
public class CheckersMove {

    /**
     * return alll valid moves for a given checkers piece.
     *
     * @param board current state of checkers board
     * @param piece the piece to check
     * @param row current row of the piece
     * @param col current col of the piece
     * @return an array with valid move positions
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
     * check if the position is on the board nd empty
     *
     * @param toRow the target row to move
     * @param toCol the target col to move
     * @return true if the move is valid, false otherwise
     */
    private static boolean isValidMove(CheckersBoard board, int toRow, int toCol) {
        boolean inBounds = (toRow >= 0 && toRow < 8 && toCol >= 0 && toCol < 8);
        boolean isEmpty = inBounds && board.board[toRow][toCol] == null;
        return isEmpty && inBounds;
    }

    /**
     * Moves a piece on the board and handles captures and promotions
     *
     * @param startRow the row the piece is moving from
     * @param startCol the column the piece is moving from
     * @param destRow the row the piece is moving to
     * @param destCol the column the piece is moving to
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