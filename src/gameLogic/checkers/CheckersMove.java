package gameLogic.checkers;

import java.util.ArrayList;
import java.util.List;

public class CheckersMove {

    /**
     * Returns a list of all valid moves (simple and capture) for the given piece.
     * @param board the game board
     * @param piece the piece to move
     * @param row the piece's current row
     * @param col the piece's current column
     * @return a list of valid move destinations as [row, col] pairs
     */
    public static int[][] availableMoves(CheckersBoard board, CheckersPiece piece, int row, int col) {
        List<int[]> movesList = new ArrayList<>();

        int[][] directions;

        if (!piece.isKing()) {
            if (piece.getColour() == CheckersPiece.Colour.WHITE) {
                directions = new int[][]{{-1, -1}, {-1, 1}};  // white moves up the board
            } else {
                directions = new int[][]{{1, -1}, {1, 1}};    // black moves down the board
            }
        } else {
            // Kings can move in all 4 diagonal directions
            directions = new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        }

        // to find valid moves
        for (int[] dir : directions) {
            int nextRow = row + dir[0];
            int nextCol = col + dir[1];

            if (onBoard(nextRow, nextCol) && board.board[nextRow][nextCol] == null) {
                movesList.add(new int[]{nextRow, nextCol});
            }

            // Jump move (2 squares over opponent's piece)
            int jumpRow = row + 2 * dir[0];
            int jumpCol = col + 2 * dir[1];

            if (onBoard(jumpRow, jumpCol) && board.board[jumpRow][jumpCol] == null) {
                // to Check if ther is piece in between to capture
                int midRow = row + dir[0];
                int midCol = col + dir[1];
                CheckersPiece middlePiece = board.board[midRow][midCol];

                if (middlePiece != null && middlePiece.getColour() != piece.getColour()) {
                    movesList.add(new int[]{jumpRow, jumpCol});
                }
            }
        }
        return movesList.toArray(new int[0][]);
    }

    /**
     * Checks if a row and column are within the board boundaries.
     */
    private static boolean onBoard(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    /**
     * Moves a piece from one position to another.
     * Handles captures (jumps) and king promotion.
     * @param board the game board
     * @param piece the piece being moved
     * @param startRow current row of the piece
     * @param startCol current column of the piece
     * @param destRow destination row
     * @param destCol destination column
     */
    public static void move(CheckersBoard board, CheckersPiece piece, int startRow, int startCol, int destRow, int destCol) {
        board.board[destRow][destCol] = piece;          // Place the piece from old spot and put it in new one
        board.board[startRow][startCol] = null;

        int rowDiff = destRow - startRow;               // Check if it's a jump
        int colDiff = destCol - startCol;

        if (Math.abs(rowDiff) == 2 && Math.abs(colDiff) == 2) {
            int capturedRow = startRow + rowDiff / 2;
            int capturedCol = startCol + colDiff / 2;
            board.removePiece(capturedRow, capturedCol);
        }

        // Check promotion to king
        if (piece.getColour() == CheckersPiece.Colour.WHITE && destRow == 0) {
            piece.promoteToKing();
        } else if (piece.getColour() == CheckersPiece.Colour.BLACK && destRow == 7) {
            piece.promoteToKing();
        }
    }
}
