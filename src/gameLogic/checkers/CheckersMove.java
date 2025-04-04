package gameLogic.checkers;

import java.util.ArrayList;
import java.util.List;

public class CheckersMove {

    public static class Move {
        public int destRow, destCol;
        public List<int[]> capturedPositions;

        public Move(int destRow, int destCol, List<int[]> capturedPositions) {
            this.destRow = destRow;
            this.destCol = destCol;
            this.capturedPositions = capturedPositions;
        }
    }

    public static Move[] availableMoves(CheckersBoard board, CheckersPiece piece, int row, int col) {
        List<Move> jumpMoves = new ArrayList<>();
        findJumps(board, piece, row, col, new ArrayList<>(), jumpMoves);

        // Compute simple moves
        List<Move> simpleMoves = new ArrayList<>();
        int[][] directions;
        if (!piece.isKing()) {
            if (piece.getColour() == CheckersPiece.Colour.WHITE) {
                directions = new int[][]{{-1, -1}, {-1, 1}};
            } else {
                directions = new int[][]{{1, -1}, {1, 1}};
            }
        } else {
            directions = new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        }
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (onBoard(newRow, newCol) && board.board[newRow][newCol] == null) {
                simpleMoves.add(new Move(newRow, newCol, new ArrayList<>()));
            }
        }

        // Combine both simple moves and jump moves
        List<Move> allMoves = new ArrayList<>();
        // If you want standard checkers (force jump), you can skip adding simple moves if jumpMoves is not empty.
        allMoves.addAll(simpleMoves);
        allMoves.addAll(jumpMoves);

        return allMoves.toArray(new Move[0]);
    }

    private static void findJumps(CheckersBoard board, CheckersPiece piece, int currentRow, int currentCol,
                                  List<int[]> currentCaptures, List<Move> result) {
        int[][] directions;
        if (!piece.isKing()) {
            if (piece.getColour() == CheckersPiece.Colour.WHITE) {
                directions = new int[][]{{-1, -1}, {-1, 1}};
            } else {
                directions = new int[][]{{1, -1}, {1, 1}};
            }
        } else {
            directions = new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        }

        boolean jumpFound = false;
        for (int[] dir : directions) {
            int jumpRow = currentRow + 2 * dir[0];
            int jumpCol = currentCol + 2 * dir[1];
            int midRow = currentRow + dir[0];
            int midCol = currentCol + dir[1];

            if (onBoard(jumpRow, jumpCol) && board.board[jumpRow][jumpCol] == null) {
                CheckersPiece middlePiece = board.board[midRow][midCol];
                if (middlePiece != null && middlePiece.getColour() != piece.getColour()) {
                    jumpFound = true;
                    List<int[]> newCaptures = new ArrayList<>(currentCaptures);
                    newCaptures.add(new int[]{midRow, midCol});

                    // Simulate the jump
                    CheckersPiece captured = board.board[midRow][midCol];
                    board.board[currentRow][currentCol] = null;
                    board.board[midRow][midCol] = null;
                    board.board[jumpRow][jumpCol] = piece;

                    // ------------------------------------------------------------
                    // 1) ADD the "partial" jump move here (so user sees single-jump)
                    // ------------------------------------------------------------
                    Move partialMove = new Move(jumpRow, jumpCol, newCaptures);
                    if (!containsMove(result, partialMove)) {
                        result.add(partialMove);
                    }

                    // 2) Then recurse to find further jumps
                    findJumps(board, piece, jumpRow, jumpCol, newCaptures, result);

                    // Backtrack
                    board.board[currentRow][currentCol] = piece;
                    board.board[midRow][midCol] = captured;
                    board.board[jumpRow][jumpCol] = null;
                }
            }
        }
        // If no further jumps, but we have at least one capture, this is a final chain
        if (!currentCaptures.isEmpty() && !jumpFound) {
            Move finalMove = new Move(currentRow, currentCol, currentCaptures);
            if (!containsMove(result, finalMove)) {
                result.add(finalMove);
            }
        }
    }

    // Helper to avoid duplicates in the result list
    private static boolean containsMove(List<Move> moves, Move newMove) {
        for (Move m : moves) {
            if (m.destRow == newMove.destRow && m.destCol == newMove.destCol
                    && sameCaptures(m.capturedPositions, newMove.capturedPositions)) {
                return true;
            }
        }
        return false;
    }

    private static boolean sameCaptures(List<int[]> a, List<int[]> b) {
        if (a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i)[0] != b.get(i)[0] || a.get(i)[1] != b.get(i)[1]) {
                return false;
            }
        }
        return true;
    }

    private static boolean onBoard(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public static void move(CheckersBoard board, CheckersPiece piece, int startRow, int startCol, Move move) {
        board.board[move.destRow][move.destCol] = piece;
        board.board[startRow][startCol] = null;
        // Remove each captured piece
        for (int[] cap : move.capturedPositions) {
            board.removePiece(cap[0], cap[1]);
        }
        // King promotion
        if (piece.getColour() == CheckersPiece.Colour.WHITE && move.destRow == 0) {
            piece.promoteToKing();
        } else if (piece.getColour() == CheckersPiece.Colour.BLACK && move.destRow == 7) {
            piece.promoteToKing();
        }
    }
}
